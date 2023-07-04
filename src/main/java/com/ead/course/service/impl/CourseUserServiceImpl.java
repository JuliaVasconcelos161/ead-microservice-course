package com.ead.course.service.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.repository.CourseUserRepository;
import com.ead.course.service.CourseUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    private final CourseUserRepository repository;

    public CourseUserServiceImpl(CourseUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId) {
        return repository.existsByCourseAndUserId(courseModel, userId);
    }
}
