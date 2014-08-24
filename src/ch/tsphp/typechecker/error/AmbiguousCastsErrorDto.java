/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

import java.util.List;

/**
 * Represents the meta-data of an ambiguous casts.
 * <p/>
 * For instance, if class A can be casted to B and to C and we have the two following methods (overload):
 * <pre>
 *     function void foo(cast B $b){}
 *     function void foo(cast C $b){}
 * </pre>
 * Then it is not possible to chose which overload should be taken if the method is called like this
 * <pre>
 *     $o->foo(new A());
 * </pre>
 * which results in an ambiguous cast error.
 */
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
