package ao.okayula.forum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {

    private static final String[] ALLOWED_VALUES = {"USER", "ADMIN"};

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        // Você pode inicializar algo aqui, se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Retorna false se o valor for nulo
        }
        for (String allowedValue : ALLOWED_VALUES) {
            if (allowedValue.equals(value)) {
                return true; // Valor válido
            }
        }
        return false; // Se não encontrar o valor permitido
    }
}
