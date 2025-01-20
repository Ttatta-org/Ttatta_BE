package TtattaBackend.ttatta.validation.validator;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategoryColor;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExistDiaryCategoryColorValidator implements ConstraintValidator<ExistDiaryCategoryColor, Optional<String>> {
    private final DiaryCategoryRepository diaryCategoryRepository;

    @Override
    public void initialize(ExistDiaryCategoryColor constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


    @Override
    public boolean isValid(Optional<String> categoryColor, ConstraintValidatorContext context) {
        ErrorStatus errorStatus;
        boolean isValid;

        String colorValue = categoryColor.orElse(null);
        if(colorValue == null) {
            errorStatus = ErrorStatus.DIARY_CATEGORY_COLOR_NULL;
            isValid = false;
        } else {
            errorStatus = ErrorStatus.DIARY_CATEGORY_COLOR_NOT_FOUND;
            isValid = Arrays.stream(CategoryColor.values())
                    .anyMatch(color -> color.name().equalsIgnoreCase(colorValue));
        }

        if(!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage()).addConstraintViolation();
        }
        return isValid;
    }
}
