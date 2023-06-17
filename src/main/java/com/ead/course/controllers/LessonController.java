package com.ead.course.controllers;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.dtos.LessonDto;
import com.ead.course.service.LessonService;
import com.ead.course.service.ModuleService;
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
@CrossOrigin(value = "*", maxAge = 3600)
public class LessonController {
    private final LessonService service;

    private final ModuleService moduleService;

    public LessonController(LessonService service, ModuleService moduleService) {
        this.service = service;
        this.moduleService = moduleService;
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                             @RequestBody @Valid LessonDto lessonDto) {
        log.debug("POST saveLesson lessonDto received {}", lessonDto.toString());
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if(!moduleModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found.");

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        service.save(lessonModel);
        log.debug("POST saveLesson lessonId saved {}", lessonModel.getLessonId());
        log.info("Lesson saved successfully lessonId {}", lessonModel.getLessonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModel);
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId) {
        log.debug("DELETE deleteLesson lessonId received {}", lessonId);
        Optional<LessonModel> lessonModelOptional = service.findLessonIntoModule(moduleId, lessonId);
        if(!lessonModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");

        service.delete(lessonModelOptional.get());
        log.debug("DELETE deleteLesson lessonId saved {}", lessonId);
        log.info("Lesson delete successfully lessonId {}", lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully.");

    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto) {
        log.debug("PUT updateLesson lessonDto received {}", lessonDto.toString());
        Optional<LessonModel> lessonModelOptional = service.findLessonIntoModule(moduleId, lessonId);
        if(!lessonModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");

        var lessonModel = lessonModelOptional.get();
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());
        service.save(lessonModel);
        log.debug("PUT updateLesson lessonId saved {}", lessonModel.getLessonId());
        log.info("Lesson updated successfully lessonId {}", lessonModel.getLessonId());
        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value = "moduleId") UUID moduleId,
                                                           Specification.LessonSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "lessonId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findAllByModule(Specification.lessonModuleId(moduleId).and(spec), pageable));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = service.findLessonIntoModule(moduleId, lessonId);
        if(!lessonModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");

        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
