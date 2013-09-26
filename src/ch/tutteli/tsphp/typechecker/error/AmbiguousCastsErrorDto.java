package ch.tutteli.tsphp.typechecker.error;

import java.util.List;

public class AmbiguousCastsErrorDto extends ReferenceErrorDto
{

    public List<String> leftToRightCasts;
    public List<String> rightToLeftCasts;
    public List<List<String>> leftAmbiguouities;
    public List<List<String>> rightAmbiguouities;

    public AmbiguousCastsErrorDto(String theIdentifier, int theLine, int thePosition,
            List<String> theLeftToRightCasts, List<String> theRightToLeftCasts,
            List<List<String>> theLeftAmbiguouities, List<List<String>> theRightAmbiguouities) {
        super(theIdentifier, theLine, thePosition);
        leftToRightCasts = theLeftToRightCasts;
        rightToLeftCasts = theRightToLeftCasts;
        leftAmbiguouities = theLeftAmbiguouities;
        rightAmbiguouities = theRightAmbiguouities;
    }
}
