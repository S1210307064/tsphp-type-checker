package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import java.util.List;

/**
 * Represents an exception which occurs when a call is made and the actual parameters match to multiple signatures.
 */
public class AmbiguousCallException extends TypeCheckerException
{

    private List<OverloadDto> ambiguousOverloads;

    AmbiguousCallException(List<OverloadDto> theAmmbiguousMethodDtos) {
        ambiguousOverloads = theAmmbiguousMethodDtos;
    }

    public List<OverloadDto> getAmbiguousOverloads() {
        return ambiguousOverloads;
    }
}
