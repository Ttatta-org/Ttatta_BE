package TtattaBackend.ttatta.validation.annotation;

import TtattaBackend.ttatta.validation.validator.ExistDiaryCategoryColorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistDiaryCategoryColorValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistDiaryCategoryColor {
    String message() default "해당하는 카테고리 색상이 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
