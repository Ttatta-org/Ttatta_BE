package TtattaBackend.ttatta.validation.annotation;

import TtattaBackend.ttatta.validation.validator.ExistUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistUserValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistUser {
    String message() default "해당하는 유저가 존재하지 않습니다."; // message대신 ErrorStatus에 정의한 message사용할 것임.
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
