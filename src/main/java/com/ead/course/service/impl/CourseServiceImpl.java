package com.ead.course.service.impl;

import com.ead.course.client.AuthuserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repository.CourseRepository;
import com.ead.course.repository.CourseUserRepository;
import com.ead.course.repository.LessonRepository;
import com.ead.course.repository.ModuleRepository;
import com.ead.course.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;

    private final ModuleRepository moduleRepository;

    private final LessonRepository lessonRepository;

    private final CourseUserRepository courseUserRepository;

    private final AuthuserClient authuserClient;

    public CourseServiceImpl(CourseRepository repository, ModuleRepository moduleRepository, LessonRepository lessonRepository, CourseUserRepository courseUserRepository, AuthuserClient authuserClient) {
        this.repository = repository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseUserRepository = courseUserRepository;
        this.authuserClient = authuserClient;
    }

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        boolean isCourseUserInAuthuser = false;
        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
        if(!moduleModelList.isEmpty()) {
            for(ModuleModel module : moduleModelList) {
                List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
                if(!lessonModelList.isEmpty())
                    lessonRepository.deleteAll(lessonModelList);
            }
            moduleRepository.deleteAll(moduleModelList);
        }
        List<CourseUserModel> courseUserModelList = courseUserRepository.findAllCourseUserIntoCourse(courseModel.getCourseId());
        if(!courseUserModelList.isEmpty()) {
            courseUserRepository.deleteAll(courseUserModelList);
            isCourseUserInAuthuser = true;
        }
        repository.delete(courseModel);
        if(isCourseUserInAuthuser)
            authuserClient.deleteCourseInAuthuser(courseModel.getCourseId());
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
}
