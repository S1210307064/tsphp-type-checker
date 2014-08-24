/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

import java.util.List;

/**
 * Represents the meta-data of errors which concerns wrong argument types, e.g. wrong method call.
 */
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
