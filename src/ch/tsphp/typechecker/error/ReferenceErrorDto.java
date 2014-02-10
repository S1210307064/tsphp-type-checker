package ch.tsphp.typechecker.error;

public class ReferenceErrorDto
{

    public String identifier;
    public int line;
    public int position;

    public ReferenceErrorDto(String theIdentifier, int theLine, int thePosition) {
        identifier = theIdentifier;
        line = theLine;
        position = thePosition;

    }
}
