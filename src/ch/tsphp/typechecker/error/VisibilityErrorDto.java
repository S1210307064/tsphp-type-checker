package ch.tsphp.typechecker.error;

public class VisibilityErrorDto extends ReferenceErrorDto
{
    public String visibility;
    public String accessedFrom;

    public VisibilityErrorDto(String theIdentifier, int theLine, int thePosition,
            String theVisibility, String wasAccessedFrom) {
        super(theIdentifier, theLine, thePosition);
        visibility = theVisibility;
        accessedFrom = wasAccessedFrom;
    }
}
