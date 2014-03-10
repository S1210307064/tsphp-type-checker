/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

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
