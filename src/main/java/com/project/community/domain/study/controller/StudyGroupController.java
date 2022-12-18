package com.project.community.domain.study.controller;

import com.project.community.common.CurrentUser;
import com.project.community.domain.study.dto.request.StudyGroupRequest;
import com.project.community.domain.study.dto.response.StudyGroupResponse;
import com.project.community.domain.study.service.StudyGroupService;
import com.project.community.domain.user.entity.User;
import com.project.community.domain.user.entity.UserGroup;
import com.project.community.domain.user.repository.UserGroupRepository;
import com.project.community.domain.user.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupService userGroupService;
    //스터디 그룹 작성 화면 조회
    @GetMapping("/study-group/new")
    public String createStudyGroupForm(@CurrentUser User user, Model model){
        model.addAttribute(user);
        model.addAttribute(new StudyGroupRequest());
        return "study/study-group-form";
    }

    //스터디 그룹 등록(리더)
    @PostMapping("/study-group/create")
    public String createStudyGroup(StudyGroupRequest studyGroupRequest, @CurrentUser User user){
        studyGroupService.createStudyGroup(user, studyGroupRequest);

        return "redirect:/study-group";
    }

    //스터디 그룹 전체 조회
    @GetMapping("/study-group")
    public String getAllStudyGroup(Model model){
        List<StudyGroupResponse> studyGroupList = studyGroupService.getAllStudyGroup();
        model.addAttribute("studyGroupList", studyGroupList);
        return "study/study-group";
    }

    //스터디 그룹 상세 조회
    @GetMapping("/study-group/{studyGroupId}")
    public String getStudyGroup(Model model, @PathVariable("studyGroupId") Long studyGroupId){
        StudyGroupResponse studyGroup = studyGroupService.getStudyGroup(studyGroupId);
        model.addAttribute("studyGroup", studyGroup);

        return "study/study-group-detail";
    }

    //스터디 그룹 수정 폼
    @GetMapping("/study-group/update/{studyGroupId}")
    public String updateStudyGroup(@PathVariable("studyGroupId") Long studyGroupId, @CurrentUser User user, Model model) {
        StudyGroupResponse studyGroup = studyGroupService.getStudyGroup(studyGroupId);
        model.addAttribute(user);
        model.addAttribute("studyGroup", studyGroup);
        model.addAttribute(new StudyGroupRequest());
        return "study/study-group-modify-form";
    }

    //스터디 그룹 수정
    @PostMapping("/study-group/update/{studyGroupId}")
    public String updateStudyGroup(StudyGroupRequest studyGroupRequest, @CurrentUser User user, @PathVariable("studyGroupId") Long studyGroupId, RedirectAttributes attributes){
        studyGroupService.updateStudyGroup(user, studyGroupId, studyGroupRequest);
        attributes.addFlashAttribute("message", "스터디 정보를 수정했습니다.");
        return "redirect:/study-group/{studyGroupId}";
    }

    //스터디 그룹 모집 마감
    @PostMapping("/study-group/close/{studyGroupId}")
    public String closeStudyGroup(@CurrentUser User user, @PathVariable("studyGroupId") Long studyGroupId, RedirectAttributes attributes){
        studyGroupService.closeStudyGroup(user, studyGroupId);
        attributes.addFlashAttribute("message", "스터디를 마감했습니다");

        return "redirect:/study-group/{studyGroupId}";
    }

    //스터디 그룹 참가 신청
    @PostMapping("/study-group/{studyGroupId}/enroll")
    public String newEnrollment(@CurrentUser User user, @PathVariable("studyGroupId")Long studyGroupId){

        UserGroup userGroup = userGroupRepository.findByStudyGroupId(studyGroupId);
        userGroupService.newEnrollment(userGroup, user);
        return "redirect:/study-group/{studyGroupId}";
    }

    //스터디 그룹 참가 신청 취소
    @PostMapping("/study-group/{studyGroupId}/disenroll")
    public String cancelEnrollment(@CurrentUser User user, @PathVariable("studyGroupId") Long studyGroupId){
        UserGroup userGroup = userGroupRepository.findByStudyGroupId(studyGroupId);
        userGroupService.cancelEnrollment(userGroup, user);
        return "redirect:/study-group/{studyGroupId}";
    }

}
