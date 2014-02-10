package ch.tsphp.typechecker.error;

import java.util.List;

public class WrongArgumentTypeErrorDto extends ReferenceErrorDto
{

    public String[] actualParameterTypes;
    public List<List<String>> possibleOverloads;

    public WrongArgumentTypeErrorDto(String theIdentifier, int theLine, int thePosition,
            String[] theActualParameterTypes, List<List<String>> thePossibleOverloads) {
        super(theIdentifier, theLine, thePosition);
        actualParameterTypes = theActualParameterTypes;
        possibleOverloads = thePossibleOverloads;
    }
}
