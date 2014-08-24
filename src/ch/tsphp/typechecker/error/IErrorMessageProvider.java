/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

/**
 * Represents a collection of error messages.
 * <p/>
 * This interface does not define how the error messages are stored (hard coded, file,
 * db etc.) nor which language they have
 */
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
