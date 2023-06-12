package com.ead.course.service.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repository.LessonRepository;
import com.ead.course.service.LessonService;
import org.springframework.stereotype.Service;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository repository;

    public LessonServiceImpl(LessonRepository repository) {
        this.repository = repository;
    }

    @Override
    public LessonModel save(LessonModel lessonModel) {
        return repository.save(lessonModel);
    }
}
