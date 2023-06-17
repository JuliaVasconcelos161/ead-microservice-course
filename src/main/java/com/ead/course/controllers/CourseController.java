package com.ead.course.controllers;

import com.ead.course.models.CourseModel;
import com.ead.course.models.dtos.CourseDto;
import com.ead.course.service.impl.CourseServiceImpl;
import com.ead.course.specifications.Specification;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    private final CourseServiceImpl service;

    public CourseController(CourseServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseDto courseDto) {
        log.debug("POST saveCourse courseDto received {}", courseDto.toString());
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        service.save(courseModel);
        log.debug("POST saveCourse courseModel saved {}", courseModel.toString());
        log.info("Course saved successfully courseId {}", courseModel.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModel);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {
        log.debug("DELETE deleteCourse courseId received {}", courseId);
        Optional<CourseModel> courseModelOptional = service.findById(courseId);
        if(!courseModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");

        service.delete(courseModelOptional.get());
        log.debug("DELETE deleteCourse courseId deleted {}", courseId);
        log.info("Course deleted successfully courseId {}", courseId);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully.");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @RequestBody @Valid CourseDto courseDto) {
        log.debug("PUT updateCourse courseDto received {}", courseDto.toString());
        Optional<CourseModel> courseModelOptional = service.findById(courseId);
        if(!courseModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        var courseModel = courseModelOptional.get();
        courseModel.setName(courseDto.getName());
        courseModel.setDescription(courseDto.getDescription());
        courseModel.setImageUrl(courseDto.getImageUrl());
        courseModel.setCourseStatus(courseDto.getCourseStatus());
        courseModel.setCourseLevel(courseDto.getCourseLevel());
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        service.save(courseModel);
        log.debug("PUT updateCourse courseModel saved {}", courseModel.toString());
        log.info("User updated successfully courseId {}", courseModel.getCourseId());
        return ResponseEntity.status(HttpStatus.OK).body(courseModel);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(Specification.CourseSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "courseId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(spec, pageable));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value = "courseId") UUID courseId) {
        Optional<CourseModel> courseModelOptional = service.findById(courseId);
        if(!courseModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
