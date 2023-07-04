package com.ead.course.service.impl;

import com.ead.course.service.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

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
}
