package com.ead.course.service;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;

import java.util.UUID;

public interface CourseUserService {
    boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId);

    CourseUserModel save(CourseUserModel courseUserModel);
}
