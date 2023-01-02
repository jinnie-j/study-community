package com.project.community.domain.study.dto.request;

import com.project.community.domain.location.entity.Location;
import com.project.community.domain.study.entity.StudyGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StudyGroupRequest implements Cloneable{

    @NotBlank
    private String title;

    private String content;

    private String createdBy;

    private String studyType;

    private String numberOfMembers;

    private Location location;

    private String duration;

    private String online;

    private Object skills;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime studyStartDate;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private boolean closed;


    public static StudyGroup toEntity(StudyGroupRequest studyGroupRequest){
        return StudyGroup.builder()
                .title(studyGroupRequest.getTitle())
                .content(studyGroupRequest.getContent())
                .createdBy(studyGroupRequest.getCreatedBy())
                .studyType(studyGroupRequest.getStudyType())
                .numberOfMembers(studyGroupRequest.getNumberOfMembers())
                .location(studyGroupRequest.getLocation())
                .duration(studyGroupRequest.getDuration())
                .online(studyGroupRequest.getOnline())
                .skills((Set) studyGroupRequest.getSkills())
                .studyStartDate(studyGroupRequest.getStudyStartDate())
                .closed(studyGroupRequest.isClosed())
                .build();
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }


}
