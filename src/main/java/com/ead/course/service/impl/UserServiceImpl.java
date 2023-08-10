package com.ead.course.service.impl;

import com.ead.course.repository.UserRepository;
import com.ead.course.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }
}
