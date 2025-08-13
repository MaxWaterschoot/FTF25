package utils;

import domain.Festival;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FestivalCodesValidator implements ConstraintValidator<ValidFestivalCodes, Festival> {
    @Override
    public boolean isValid(Festival f, ConstraintValidatorContext ctx) {
        if (f == null || f.getFestivalCode1() == null || f.getFestivalCode2() == null) return false;
        int c1 = f.getFestivalCode1();
        int c2 = f.getFestivalCode2();
        boolean c1ok = c1 > 0 && (c1 % 2 == 0);
        boolean c2ok = c2 % 3 == 0;
        boolean diffOk = Math.abs(c1 - c2) < 300;
        return c1ok && c2ok && diffOk;
    }
}
