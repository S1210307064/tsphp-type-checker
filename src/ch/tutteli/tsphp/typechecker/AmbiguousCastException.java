package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import java.util.List;

public class AmbiguousCastException extends TypeCheckerException
{

    private List<CastingDto> ambiguousCasts;

    public AmbiguousCastException(List<CastingDto> theAmmbiguousCasts) {

        ambiguousCasts = theAmmbiguousCasts;
    }

    public List<CastingDto> getAmbiguousCasts() {
        return ambiguousCasts;
    }
}
