/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
