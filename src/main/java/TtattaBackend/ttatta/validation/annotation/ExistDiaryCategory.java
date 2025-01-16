package TtattaBackend.ttatta.validation.annotation;

import TtattaBackend.ttatta.validation.validator.ExistDiaryCategoryValidator;
import TtattaBackend.ttatta.validation.validator.ExistUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistDiaryCategoryValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistDiaryCategory {
    String message() default "해당하는 일기 카테고리가 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
