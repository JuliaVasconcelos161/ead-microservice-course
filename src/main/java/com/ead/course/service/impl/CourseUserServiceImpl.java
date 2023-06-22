package com.ead.course.service.impl;

import com.ead.course.repository.CourseUserRepository;
import com.ead.course.service.CourseUserService;
import org.springframework.stereotype.Service;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    private final CourseUserRepository repository;

    public CourseUserServiceImpl(CourseUserRepository repository) {
        this.repository = repository;
    }
}
