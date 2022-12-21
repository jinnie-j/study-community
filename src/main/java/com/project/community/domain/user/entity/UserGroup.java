package com.project.community.domain.user.entity;

import com.project.community.domain.enrollment.entity.Enrollment;
import com.project.community.domain.study.entity.StudyGroup;
import com.project.community.domain.user.UserType;
import com.project.community.domain.user.dto.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class UserGroup {
    @Id
    @GeneratedValue
    @Column(name = "user_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount){
        return !isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
    }

    public boolean isDisenrollableFor(UserAccount userAccount){
        return !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }

    public boolean isAlreadyEnrolled(UserAccount userAccount){
        User user = userAccount.getUser();
        for(Enrollment e : this.enrollments){
            if(e.getUser().equals(user)){
                return true;
            }
        }
        return false;
    }

    public boolean isAttended(UserAccount userAccount){
        User user = userAccount.getUser();
        for(Enrollment e : this.enrollments){
            if(e.getUser().equals(user) && e.isAttended()){
                return true;
            }
        }
        return false;
    }

    public void addEnrollment(Enrollment enrollment){
        this.enrollments.add(enrollment);
        enrollment.setUserGroup(this);
    }

    public void removeEnrollment(Enrollment enrollment){
        this.enrollments.remove(enrollment);
        enrollment.setUserGroup(null);
    }

    @Builder
    public UserGroup(Long id , User user, StudyGroup studyGroup, UserType userType){
        this.id = id;
        this.user = user;
        this.studyGroup = studyGroup;
        this.userType = userType;
    }

    public void accept(Enrollment enrollment) {
        enrollment.setAccepted(true);
    }

    public void reject(Enrollment enrollment) {
        enrollment.setAccepted(false);
    }
}
