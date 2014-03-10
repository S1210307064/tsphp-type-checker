/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

import java.util.List;

public class MissingImplementationErrorDto extends ReferenceErrorDto
{
    public List<SignatureDto> signatureDtos;

    public MissingImplementationErrorDto(String theIdentifier, int theLine, int thePosition,
            List<SignatureDto> theSignatureDtos) {
        super(theIdentifier, theLine, thePosition);
        signatureDtos = theSignatureDtos;
    }
}
