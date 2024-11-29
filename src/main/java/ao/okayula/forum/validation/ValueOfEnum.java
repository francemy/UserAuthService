package ao.okayula.forum.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// Define que a anotação será utilizada em campos do tipo String.
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class) // Especifica o validador que será usado
public @interface ValueOfEnum {

    String message() default "Invalid value. Must be one of {USER, ADMIN}"; // Mensagem de erro padrão

    Class<?>[] groups() default {}; // Para agrupar restrições, se necessário

    Class<? extends Payload>[] payload() default {}; // Para carregar dados adicionais de payload
}
