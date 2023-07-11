package com.ead.course.controllers;

import com.ead.course.client.AuthuserClient;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.dtos.SubscriptionDto;
import com.ead.course.models.dtos.UserDto;
import com.ead.course.service.CourseService;
import com.ead.course.service.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    private final AuthuserClient authuserClient;

    private final CourseService courseService;

    private final CourseUserService courseUserService;

    public CourseUserController(AuthuserClient authuserClient, CourseService courseService, CourseUserService courseUserService) {
        this.authuserClient = authuserClient;
        this.courseService = courseService;
        this.courseUserService = courseUserService;
    }

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserDto>> getAllUsersByCourse(
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(authuserClient.getAllUsersByCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto) {
        ResponseEntity<UserDto> responseUser;
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");

        if(courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.getUserId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");

        try {
            responseUser = authuserClient.getOneUserById(subscriptionDto.getUserId());
            if(responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked.");

        } catch (HttpStatusCodeException e) {
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
    }
}
