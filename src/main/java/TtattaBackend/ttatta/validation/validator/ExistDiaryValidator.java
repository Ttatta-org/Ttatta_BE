package TtattaBackend.ttatta.validation.validator;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.validation.annotation.ExistDiary;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistDiaryValidator implements ConstraintValidator<ExistDiary, Long> {

    private final DiaryRepository diaryRepository;

    @Override
    public void initialize(ExistDiary constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long diaryId, ConstraintValidatorContext context) {
        ErrorStatus errorStatus;

        boolean isValid;

        if(diaryId == null) {
            errorStatus = ErrorStatus.DIARY_IS_NULL;
            isValid = false;
        } else {
            errorStatus = ErrorStatus.DIARY_NOT_FOUND;
            isValid = diaryRepository.findById(diaryId).isPresent();
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
