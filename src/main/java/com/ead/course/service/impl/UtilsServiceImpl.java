package com.ead.course.service.impl;

import com.ead.course.service.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Override
    public String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable) {
        StringBuilder url =  new StringBuilder();
        url.append("/users?courseId=");
        url.append(courseId);
        url.append("&page=");
        url.append(pageable.getPageNumber());
        url.append("&size=");
        url.append(pageable.getPageSize());
        url.append("&sort=");
        url.append(pageable.getSort().toString().replaceAll(": ", ","));
        return url.toString();
    }

    @Override
    public String createUrlGetOneUserById(UUID userId) {
        StringBuilder url =  new StringBuilder();
        url.append("/users/");
        url.append(userId);
        return url.toString();
    }

    @Override
    public String createUrlPostSubscriptionUserInCourse(UUID userId) {
        StringBuilder url =  new StringBuilder();
        url.append("/users/");
        url.append(userId);
        url.append("/courses/subscription");
        return url.toString();
    }

    @Override
    public String createUrlDeleteCourseInAuthuser(UUID courseId) {
        StringBuilder url =  new StringBuilder();
        url.append("/users/courses/");
        url.append(courseId);
        return url.toString();
    }
}
