package TtattaBackend.ttatta.validation.validator;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistDiaryCategoryValidator implements ConstraintValidator<ExistDiaryCategory, Long> {

    private final DiaryCategoryRepository diaryCategoryRepository;

    @Override
    public void initialize(ExistDiaryCategory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long diaryCategoryId, ConstraintValidatorContext context) {
        ErrorStatus errorStatus;

        boolean isValid;

        if(diaryCategoryId == null) {
            errorStatus = ErrorStatus.DIARY_CATEGORY_IS_NULL;
            isValid = false;
        } else {
            errorStatus = ErrorStatus.DIARY_CATEGORY_NOT_FOUND;
            isValid = diaryCategoryRepository.findById(diaryCategoryId).isPresent();
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
