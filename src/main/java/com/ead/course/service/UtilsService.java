package com.ead.course.service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsService {

    String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable);

    String createUrlGetOneUserById(UUID userId);

    String createUrlPostSubscriptionUserInCourse(UUID userId);
}
