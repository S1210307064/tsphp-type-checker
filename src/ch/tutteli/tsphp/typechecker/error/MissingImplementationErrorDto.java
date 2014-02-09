package ch.tutteli.tsphp.typechecker.error;

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
