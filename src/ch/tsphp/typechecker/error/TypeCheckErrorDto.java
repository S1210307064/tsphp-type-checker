package ch.tsphp.typechecker.error;

public class TypeCheckErrorDto extends ReferenceErrorDto
{

    public String typeExpected;
    public String typeFound;

    public TypeCheckErrorDto(String theIdentifier, int theLine, int thePosition,
            String theTypeExpected, String theTypeFound) {
        super(theIdentifier, theLine, thePosition);
        typeExpected = theTypeExpected;
        typeFound = theTypeFound;
    }
}
