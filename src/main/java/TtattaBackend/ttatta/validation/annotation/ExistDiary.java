package TtattaBackend.ttatta.validation.annotation;

import TtattaBackend.ttatta.validation.validator.ExistDiaryValidator;
import TtattaBackend.ttatta.validation.validator.ExistUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistDiaryValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistDiary {
    String message() default "해당하는 일기가 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
