package ch.tutteli.tsphp.typechecker.error;

public interface IErrorMessageProvider
{

    String getDefinitionErrorMessage(String key, DefinitionErrorDto dto);

    String getReferenceErrorMessage(String key, ReferenceErrorDto referenceErrorDto);

    String getWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto wrongArgumentTypeErrorDto);

    String getTypeCheckErrorMessage(String identityOperator, TypeCheckErrorDto typeCheckErrorDto);

    String getOperatorAmbiguousCastingErrorMessage(String key, AmbiguousCastsErrorDto ambiguousCastingErrorDto);

    String getVisibilityErrorMessage(String key, VisibilityErrorDto visibilityErrorDto);

    String getMissingImplementationErrorMessage(String key, MissingImplementationErrorDto
            missingImplementationErrorDto);
}
