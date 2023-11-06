package com.ead.course.validation;

import com.ead.course.config.security.AuthenticationCurrentUserService;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.models.dtos.CourseDto;
import com.ead.course.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;


@Component
public class CourseValidator implements Validator {

    @Qualifier("defaultValidator")
    private final Validator validator;

    private final UserService userService;

    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public CourseValidator(Validator defaultValidator, UserService userService,
                           AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.validator = defaultValidator;
        this.userService = userService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
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
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        if(!currentUserId.equals(userInstructor))
            throw new AccessDeniedException("Forbidden");
        Optional<UserModel> userModelOptional = userService.findById(userInstructor);
        if(!userModelOptional.isPresent())
            errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found.");
        if(userModelOptional.get().getUserType().equals(UserType.STUDENT.toString()))
            errors.rejectValue("userInstructor","UserInstructorError", "User must be INSTRUCTOR or ADMIN.");
//
    }
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
