package com.ead.course.service.impl;

import com.ead.course.models.UserModel;
import com.ead.course.repository.UserRepository;
import com.ead.course.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public UserModel save(UserModel userModel) {
        return repository.save(userModel);
    }

    @Override
    public void delete(UUID userId) {
        repository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(UUID userInstructor) {
        return repository.findById(userInstructor);
    }
}
