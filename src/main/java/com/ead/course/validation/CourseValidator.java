package com.ead.course.validation;

import com.ead.course.client.AuthuserClient;
import com.ead.course.enums.UserType;
import com.ead.course.models.dtos.CourseDto;
import com.ead.course.models.dtos.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;


@Component
public class CourseValidator implements Validator {

    @Qualifier("defaultValidator")
    private final Validator validator;

    private final AuthuserClient authuserClient;

    public CourseValidator(Validator defaultValidator, AuthuserClient authuserClient) {
        this.validator = defaultValidator;
        this.authuserClient = authuserClient;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
        if(!errors.hasErrors())
            validateUserInstructor(courseDto.getUserInstructor(), errors);

    }

    private void validateUserInstructor(UUID userInstructor, Errors errors) {
        ResponseEntity<UserDto> responseUserInstructor;
        try {
            responseUserInstructor = authuserClient.getOneUserById(userInstructor);
            if(responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT))
                errors.rejectValue("userInstructor","UserInstructorError", "User must be INSTRUCTOR or ADMIN.");
        }catch(HttpStatusCodeException e) {
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found");

        }
    }
}
