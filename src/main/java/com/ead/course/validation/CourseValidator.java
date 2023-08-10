package com.ead.course.validation;

import com.ead.course.models.dtos.CourseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class CourseValidator implements Validator {

    @Qualifier("defaultValidator")
    private final Validator validator;


    public CourseValidator(Validator defaultValidator) {
        this.validator = defaultValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
//        if(!errors.hasErrors())
//            validateUserInstructor(courseDto.getUserInstructor(), errors);

    }

//    private void validateUserInstructor(UUID userInstructor, Errors errors) {
//        ResponseEntity<UserDto> responseUserInstructor;
//        try {
//            responseUserInstructor = authuserClient.getOneUserById(userInstructor);
//            if(responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT))
//                errors.rejectValue("userInstructor","UserInstructorError", "User must be INSTRUCTOR or ADMIN.");
//        }catch(HttpStatusCodeException e) {
//            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND))
//                errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found");
//
//        }
//    }
}
