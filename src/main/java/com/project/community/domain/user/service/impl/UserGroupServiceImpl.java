package com.project.community.domain.user.service.impl;

import com.project.community.domain.enrollment.entity.Enrollment;
import com.project.community.domain.enrollment.repository.EnrollmentRepository;
import com.project.community.domain.user.entity.User;
import com.project.community.domain.user.entity.UserGroup;
import com.project.community.domain.user.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class UserGroupServiceImpl implements UserGroupService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void newEnrollment(UserGroup userGroup, User user) {
        if(!enrollmentRepository.existsByUserGroupAndUser(userGroup, user)){
            Enrollment enrollment = Enrollment.builder()
                    .userGroup(userGroup)
                    .enrolledAt(LocalDateTime.now())
                    .user(user)
                    .build();

            userGroup.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    @Override
    public void cancelEnrollment(UserGroup userGroup, User user) {
       Enrollment enrollment = enrollmentRepository.findByUserGroupAndUser(userGroup, user);
       userGroup.removeEnrollment(enrollment);
       enrollmentRepository.delete(enrollment);
    }

    @Override
    public void acceptEnrollment(UserGroup userGroup, Enrollment enrollment) {
        userGroup.accept(enrollment);
    }

    @Override
    public void rejectEnrollment(UserGroup userGroup, Enrollment enrollment) {
        userGroup.reject(enrollment);
    }
}