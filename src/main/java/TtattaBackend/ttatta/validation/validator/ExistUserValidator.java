package TtattaBackend.ttatta.validation.validator;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistUserValidator implements ConstraintValidator<ExistUser, Long> {

    private final UserRepository userRepository;

    @Override
    public void initialize(ExistUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        ErrorStatus errorStatus;

        boolean isValid;

        // null is invalid
        if (userId == null) {
            errorStatus = ErrorStatus.USER_ID_NULL;
            isValid = false;
        } else {
            errorStatus = ErrorStatus.USER_NOT_FOUND;
            isValid = userRepository.findById(userId).isPresent();
        }

        if (!isValid) { // ExistUser의 message대신 ErrorStatus의 message출력
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
