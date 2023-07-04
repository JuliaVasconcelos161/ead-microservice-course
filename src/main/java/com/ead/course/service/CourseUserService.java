package com.ead.course.service;

import com.ead.course.models.CourseModel;

import java.util.UUID;

public interface CourseUserService {
    boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId);
}
