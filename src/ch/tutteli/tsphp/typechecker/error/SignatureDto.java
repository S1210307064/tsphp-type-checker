package ch.tutteli.tsphp.typechecker.error;

import java.util.List;

public class SignatureDto
{
    public String identifier;
    public String returnType;
    public List<String> argumentTypes;

    public SignatureDto(String theReturnType, String theIdentifier, List<String> theArgumentTypes) {
        identifier = theIdentifier;
        returnType = theReturnType;
        argumentTypes = theArgumentTypes;
    }
}
