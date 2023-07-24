package com.ead.course.client;

import com.ead.course.models.dtos.CourseUserDto;
import com.ead.course.models.dtos.ResponsePageDto;
import com.ead.course.models.dtos.UserDto;
import com.ead.course.service.UtilsService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
@Log4j2
@Component
public class AuthuserClient {

    private final RestTemplate restTemplate;

    private final UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URL_AUTHUSER;

    public AuthuserClient(RestTemplate restTemplate, UtilsService utilsService) {
        this.restTemplate = restTemplate;
        this.utilsService = utilsService;
    }

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        List<UserDto> searchResult = null;
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        String urlString = REQUEST_URL_AUTHUSER + utilsService.createUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Request Url: {} ", urlString);
        log.info("Request Url: {} ", urlString);
        try{
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};
            result = restTemplate.exchange(urlString, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request /users  {} ", e);
        }
        log.info("Ending request /users courseId {} ", courseId);
        return result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.createUrlGetOneUserById(userId);
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.createUrlPostSubscriptionUserInCourse(userId);
        var courseUserDto = new CourseUserDto();
        courseUserDto.setUserId(userId);
        courseUserDto.setCourseId(courseId);
        restTemplate.postForObject(url, courseUserDto, String.class);
    }

    public void deleteCourseInAuthuser(UUID courseId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.createUrlDeleteCourseInAuthuser(courseId);
        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
