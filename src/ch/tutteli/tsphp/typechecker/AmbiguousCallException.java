package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;

import java.util.List;

/**
 * Represents an exception which occurs when a call is made and the actual parameters match to multiple signatures.
 */
public class AmbiguousCallException extends TypeCheckerException
{

    private final List<OverloadDto> ambiguousOverloads;

    public AmbiguousCallException(List<OverloadDto> theAmbiguousMethodDtos) {
        ambiguousOverloads = theAmbiguousMethodDtos;
    }

    public List<OverloadDto> getAmbiguousOverloads() {
        return ambiguousOverloads;
    }
}
