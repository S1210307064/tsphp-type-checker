/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
