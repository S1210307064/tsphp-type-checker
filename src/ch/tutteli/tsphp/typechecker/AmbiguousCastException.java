package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import java.util.List;

public class AmbiguousCastException extends TypeCheckerException
{

    private final List<CastingDto> ambiguousCasts;

    public AmbiguousCastException(List<CastingDto> theAmbiguousCasts) {

        ambiguousCasts = theAmbiguousCasts;
    }

    public List<CastingDto> getAmbiguousCasts() {
        return ambiguousCasts;
    }
}
