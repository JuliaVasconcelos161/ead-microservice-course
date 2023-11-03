package com.ead.course.service.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import com.ead.course.models.dtos.NotificationCommandDto;
import com.ead.course.publishers.NotificationCommandPublisher;
import com.ead.course.repository.CourseRepository;
import com.ead.course.repository.LessonRepository;
import com.ead.course.repository.ModuleRepository;
import com.ead.course.service.CourseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class CourseServiceImpl implements CourseService {

    private final NotificationCommandPublisher notificationCommandPublisher;
    private final CourseRepository repository;

    private final ModuleRepository moduleRepository;

    private final LessonRepository lessonRepository;

    public CourseServiceImpl(NotificationCommandPublisher notificationCommandPublisher,
                             CourseRepository repository, ModuleRepository moduleRepository,
                             LessonRepository lessonRepository) {
        this.notificationCommandPublisher = notificationCommandPublisher;
        this.repository = repository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
        if(!moduleModelList.isEmpty()) {
            for(ModuleModel module : moduleModelList) {
                List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
                if(!lessonModelList.isEmpty())
                    lessonRepository.deleteAll(lessonModelList);
            }
            moduleRepository.deleteAll(moduleModelList);
        }
        repository.deleteCourseUserByCourse(courseModel.getCourseId());
        repository.delete(courseModel);
    }
    @Transactional
    @Override
    public CourseModel save(CourseModel courseModel) {
        return repository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return repository.findById(courseId);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public boolean existsByCourseAndUser(UUID courseId, UUID userId) {
        return repository.existsByCourseAndUser(courseId, userId);
    }

    @Transactional
    @Override
    public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        repository.saveCourseUser(courseId, userId);
    }

    @Transactional
    @Override
    public void saveSubscriptionUserInCourseAndSendNotification(CourseModel courseModel, UserModel userModel) {
        repository.saveCourseUser(courseModel.getCourseId(), userModel.getUserId());
        try{
            NotificationCommandDto notificationCommandDto = new NotificationCommandDto();
            notificationCommandDto.setTitle("Bem-vindo(a) ao Curso: " + courseModel.getName());
            notificationCommandDto.setMessage(userModel.getFullName() + " a sua inscrição foi realizada com sucesso!");
            notificationCommandDto.setUserId(userModel.getUserId());
            notificationCommandPublisher.publishNotificationCommand(notificationCommandDto);
        } catch (Exception e) {
            log.warn("Error sending notification!");
        }
    }
}
