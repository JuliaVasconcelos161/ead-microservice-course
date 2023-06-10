package com.ead.course.service.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repository.CourseRepository;
import com.ead.course.repository.LessonRepository;
import com.ead.course.repository.ModuleRepository;
import com.ead.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository repository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

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
        repository.delete(courseModel);
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return repository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return repository.findById(id);
    }

    @Override
    public List<CourseModel> findAll() {
        return repository.findAll();
    }
}
