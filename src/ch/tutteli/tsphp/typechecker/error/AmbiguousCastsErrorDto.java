package ch.tutteli.tsphp.typechecker.error;

import java.util.List;

public class AmbiguousCastsErrorDto extends ReferenceErrorDto
{

    public List<String> leftToRightCasts;
    public List<String> rightToLeftCasts;
    public List<List<String>> leftAmbiguities;
    public List<List<String>> rightAmbiguities;

    public AmbiguousCastsErrorDto(String theIdentifier, int theLine, int thePosition,
            List<String> theLeftToRightCasts, List<String> theRightToLeftCasts,
            List<List<String>> theLeftAmbiguities, List<List<String>> theRightAmbiguities) {
        super(theIdentifier, theLine, thePosition);
        leftToRightCasts = theLeftToRightCasts;
        rightToLeftCasts = theRightToLeftCasts;
        leftAmbiguities = theLeftAmbiguities;
        rightAmbiguities = theRightAmbiguities;
    }
}
