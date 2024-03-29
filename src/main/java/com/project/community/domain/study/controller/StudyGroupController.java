package com.project.community.domain.study.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.community.common.CurrentUser;
import com.project.community.domain.comment.dto.CommentRequest;
import com.project.community.domain.comment.dto.CommentResponse;
import com.project.community.domain.location.entity.Location;
import com.project.community.domain.location.service.LocationService;
import com.project.community.domain.skill.entity.Skill;
import com.project.community.domain.skill.service.SkillService;
import com.project.community.domain.study.dto.StudyGroupRequest;
import com.project.community.domain.study.dto.StudyGroupResponse;
import com.project.community.domain.study.entity.StudyGroup;
import com.project.community.domain.study.repository.StudyGroupRepository;
import com.project.community.domain.study.service.StudyGroupService;
import com.project.community.domain.user.entity.User;
import com.project.community.domain.user.entity.UserGroup;
import com.project.community.domain.user.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final StudyGroupRepository studyGroupRepository;
    private final UserGroupRepository userGroupRepository;
    private final SkillService skillService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    //스터디 그룹 작성 화면 조회
    @GetMapping("/study-group/new")
    public String createStudyGroupForm(@CurrentUser User user, Model model) throws JsonProcessingException {
        model.addAttribute(user);
        model.addAttribute(new StudyGroupRequest());

        //기술 리스트
        List<String> allSkills = skillService.getAllSkills();
        model.addAttribute("skillList", objectMapper.writeValueAsString(allSkills));

        //지역 리스트
        List<Location> allLocations = locationService.findAll();
        model.addAttribute("locationList", allLocations);

        return "study/study-group-form";
    }

    //스터디 그룹 등록(리더)
    @PostMapping("/study-group/create")
    public String createStudyGroup(@Valid StudyGroupRequest studyGroupRequest, Errors errors, @CurrentUser User user, Model model) throws ParseException, CloneNotSupportedException {

        if(errors.hasErrors()){
            model.addAttribute(user);
            return "study/study-group-form";
        }

        studyGroupService.createStudyGroup(user, studyGroupRequest);
        return "redirect:/";
    }

    //스터디 그룹 전체 조회
    @GetMapping(value={"/study-group/sort/{sortValue}/{checked}", "/study-group"})
    public String getAllStudyGroup(Model model, @PathVariable(required = false) String sortValue, @PathVariable Boolean checked){
        if(checked) {
            //모집 중인 모임 리스트
            List<StudyGroupResponse> openStudyGroupList = studyGroupService.getOpenStudyGroup(sortValue);
            model.addAttribute("studyGroupList", openStudyGroupList);
            model.addAttribute("checked",true);
        }
        else{
            //마감한 모임 리스트
            List<StudyGroupResponse> closedStudyList = studyGroupService.getClosedStudyGroup(sortValue);
            model.addAttribute("studyGroupList", closedStudyList);
            model.addAttribute("checked",false);
        }
        return "index";
    }

    //모집중인 스터디 조회
    @GetMapping("/study-group/openedStudy")
    public String getOpenedStudyGroup(Model model){
        List<StudyGroupResponse> openStudyGroupList = studyGroupService.getOpenStudyGroup("id");
        model.addAttribute("studyGroupList", openStudyGroupList);
        model.addAttribute("checked", true);
        log.info("open스터디 "+ openStudyGroupList);
        return "index";
    }

    //마감한 스터디 조회
    @GetMapping("/study-group/closedStudy")
    public String getClosedStudyGroup(Model model){
        List<StudyGroupResponse> closedStudyGroupList = studyGroupService.getClosedStudyGroup("id");
        model.addAttribute("studyGroupList", closedStudyGroupList);
        model.addAttribute("checked",false);
        log.info("close 스터디" + closedStudyGroupList);
        return "index";
    }

    //스터디 그룹 상세 조회
    @GetMapping("/study-group/{studyGroupId}")
    public String getStudyGroup(Model model, @PathVariable("studyGroupId") Long studyGroupId, @CurrentUser User user){
        StudyGroupResponse studyGroup = studyGroupService.getStudyGroup(studyGroupId);
        UserGroup userGroup = userGroupRepository.findByStudyGroupId(studyGroupId);
        model.addAttribute("studyGroup", studyGroup);
        model.addAttribute("userGroup", userGroup);

        // 조회수 증가
        studyGroupService.updateView(studyGroupId);

        //댓글
        List<CommentResponse> comments = studyGroup.getComments();
        model.addAttribute("comments", comments);
        model.addAttribute("user", user);
        model.addAttribute(new CommentRequest());

        return "study/study-group-detail";
    }

    //스터디 그룹 상세 - 모집현황 조회
    @GetMapping("/study-group/{studyGroupId}/people")
    public String getStudyGroupPeoPle(Model model, @PathVariable("studyGroupId") Long studyGroupId){
        StudyGroupResponse studyGroup = studyGroupService.getStudyGroup(studyGroupId);
        UserGroup userGroup = userGroupRepository.findByStudyGroupId(studyGroupId);
        model.addAttribute("studyGroup", studyGroup);
        model.addAttribute("userGroup", userGroup);

        return "study/study-group-people";
    }

    //스터디 그룹 수정 폼
    @GetMapping("/study-group/update/{studyGroupId}")
    public String updateStudyGroup(@PathVariable("studyGroupId") Long studyGroupId, @CurrentUser User user, Model model) throws JsonProcessingException {
        StudyGroupResponse studyGroup = studyGroupService.getStudyGroup(studyGroupId);
        model.addAttribute(user);
        model.addAttribute("studyGroup", studyGroup);
        model.addAttribute(new StudyGroupRequest());

        Set<Skill> skills = studyGroupService.getSkills(studyGroupId);
        model.addAttribute("skills", skills.stream().map(Skill::getTitle).collect(Collectors.toList()));

        List<String> allSkills = skillService.getAllSkills();
        model.addAttribute("skillList", objectMapper.writeValueAsString(allSkills));

        List<Location> allLocations = locationService.findAll();
        model.addAttribute("locationList", allLocations);
        return "study/study-group-modify-form";
    }

    //스터디 그룹 수정
    @PostMapping("/study-group/update/{studyGroupId}")
    public String updateStudyGroup(@Valid StudyGroupRequest studyGroupRequest, Errors errors, @CurrentUser User user,@PathVariable("studyGroupId") Long studyGroupId, RedirectAttributes attributes) throws ParseException {

        if(errors.hasErrors()){
            attributes.addFlashAttribute("message", "제목을 입력하세요.");
            return "redirect:/study-group/update/{studyGroupId}";
        }

        studyGroupService.updateStudyGroup(user, studyGroupId, studyGroupRequest);
        attributes.addFlashAttribute("message", "모임 정보를 수정했습니다.");
        return "redirect:/study-group/{studyGroupId}";
    }

    //스터디 그룹 모집 마감
    @PostMapping("/study-group/close/{studyGroupId}")
    public String closeStudyGroup(@CurrentUser User user, @PathVariable("studyGroupId") Long studyGroupId, RedirectAttributes attributes){
        studyGroupService.closeStudyGroup(user, studyGroupId);
        attributes.addFlashAttribute("message", "모임 모집을 마감했습니다");

        return "redirect:/study-group/{studyGroupId}";
    }

    //스터디 그룹 검색
    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model,
                              @PageableDefault(size = 9, sort="createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<StudyGroup> studyGroupPage = studyGroupRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyGroupPage", studyGroupPage);
        model.addAttribute("keyword",keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("createdDate") ? "createdDate" : "remainingSeats");
        return "search";
    }

    //스터디 그룹 삭제
    @GetMapping("/study-group/delete/{studyGroupId}")
    public String deleteStudyGroup(@CurrentUser User user, @PathVariable("studyGroupId") Long studyGroupId, RedirectAttributes attributes){
        studyGroupService.deleteStudyGroup(user, studyGroupId);
        attributes.addFlashAttribute("message", "스터디를 삭제했습니다");

        return "redirect:/";
    }
}
