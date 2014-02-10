package ch.tsphp.typechecker.error;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AErrorMessageProvider implements IErrorMessageProvider
{

    protected Map<String, String> definitionErrors;
    protected Map<String, String> referenceErrors;
    protected Map<String, String> wrongArgumentTypeErrors;
    protected Map<String, String> typeCheckErrors;
    protected Map<String, String> ambiguousCastsErrors;
    protected Map<String, String> visibilityViolationErrors;
    protected Map<String, String> missingImplementationErrors;

    protected abstract void loadDefinitionErrorMessages();

    protected abstract void loadReferenceErrorMessages();

    protected abstract void loadWrongArgumentTypeErrorMessages();

    protected abstract void loadTypeCheckErrorMessages();

    protected abstract void loadAmbiguousCastsErrorMessages();

    protected abstract void loadVisibilityViolationErrorMessages();

    protected abstract void loadMissingImplementationErrorMessages();

    protected abstract String getStandardDefinitionErrorMessage(String key, DefinitionErrorDto dto);

    protected abstract String getStandardReferenceErrorMessage(String key, ReferenceErrorDto dto);

    protected abstract String getStandardWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto dto);

    protected abstract String getStandardTypeCheckErrorMessage(String key, TypeCheckErrorDto dto);

    protected abstract String getStandardAmbiguousCastsErrorMessage(String key, AmbiguousCastsErrorDto dto);

    protected abstract String getStandardVisibilityViolationErrorMessage(String key, VisibilityErrorDto dto);

    protected abstract String getStandardMissingImplementationErrorMessage(String key, MissingImplementationErrorDto
            dto);

    @Override
    public String getDefinitionErrorMessage(String key, DefinitionErrorDto dto) {
        String message;
        if (definitionErrors == null) {
            loadDefinitionErrorMessages();
        }
        if (definitionErrors.containsKey(key)) {
            message = definitionErrors.get(key);
            if (dto.identifier.equals(dto.identifier)) {
                message = message.replace("%id%", "");
            } else {
                message = message.replace("%id%", "(" + dto.identifier + ")");
            }

            message = message.replace("%line%", Integer.toString(dto.line));
            message = message.replace("%pos%", Integer.toString(dto.position));

            message = message.replace("%idN%", dto.identifierNewDefinition);
            message = message.replace("%lineN%", Integer.toString(dto.lineNewDefinition));
            message = message.replace("%posN%", Integer.toString(dto.positionNewDefinition));

        } else {
            message = getStandardDefinitionErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getReferenceErrorMessage(String key, ReferenceErrorDto dto) {
        String message;
        if (referenceErrors == null) {
            loadReferenceErrorMessages();
        }
        if (referenceErrors.containsKey(key)) {
            message = referenceErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);

        } else {
            message = getStandardReferenceErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto dto) {
        String message;
        if (wrongArgumentTypeErrors == null) {
            loadWrongArgumentTypeErrorMessages();
        }
        if (wrongArgumentTypeErrors.containsKey(key)) {
            message = wrongArgumentTypeErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);

            message = message.replace("%aParams%", Arrays.toString(dto.actualParameterTypes));
            message = message.replace("%overloads%", getOverloadSignatures(dto.possibleOverloads));
        } else {
            message = getStandardWrongArgumentTypeErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getTypeCheckErrorMessage(String key, TypeCheckErrorDto dto) {
        String message;
        if (typeCheckErrors == null) {
            loadTypeCheckErrorMessages();
        }
        if (typeCheckErrors.containsKey(key)) {
            message = typeCheckErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);

            message = message.replace("%tExp%", dto.typeExpected);
            message = message.replace("%tFound%", dto.typeFound);
        } else {
            message = getStandardTypeCheckErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getOperatorAmbiguousCastingErrorMessage(String key, AmbiguousCastsErrorDto dto) {
        String message;
        if (ambiguousCastsErrors == null) {
            loadAmbiguousCastsErrorMessages();
        }
        if (ambiguousCastsErrors.containsKey(key)) {
            message = ambiguousCastsErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);

            message = message.replace("%LHS%", getCastsSequence(dto.leftToRightCasts));
            message = message.replace("%RHS%", getCastsSequence(dto.rightToLeftCasts));
            message = message.replace("%ambLHS%", getAmbiguousCastsSequences(dto.leftAmbiguities));
            message = message.replace("%ambRHS%", getAmbiguousCastsSequences(dto.rightAmbiguities));
        } else {
            message = getStandardAmbiguousCastsErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getVisibilityErrorMessage(String key, VisibilityErrorDto dto) {
        String message;
        if (visibilityViolationErrors == null) {
            loadVisibilityViolationErrorMessages();
        }
        if (visibilityViolationErrors.containsKey(key)) {
            message = visibilityViolationErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);

            message = message.replace("%vis%", dto.visibility);
            message = message.replace("%access%", dto.accessedFrom);

        } else {
            message = getStandardVisibilityViolationErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getMissingImplementationErrorMessage(String key, MissingImplementationErrorDto dto) {
        String message;
        if (missingImplementationErrors == null) {
            loadMissingImplementationErrorMessages();
        }
        if (missingImplementationErrors.containsKey(key)) {
            message = missingImplementationErrors.get(key);
            message = replaceStandardPlaceholders(dto, message);
            message = message.replace("%missingImplementations%", getMissingSignatures(dto.signatureDtos));
        } else {
            message = getStandardMissingImplementationErrorMessage(key, dto);
        }
        return message;
    }

    private String replaceStandardPlaceholders(ReferenceErrorDto dto, String message) {
        String msg = message.replace("%id%", dto.identifier);
        msg = msg.replace("%line%", Integer.toString(dto.line));
        return msg.replace("%pos%", Integer.toString(dto.position));
    }

    protected String getOverloadSignatures(List<List<String>> ambiguousFormalParameterTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> parameterTypes : ambiguousFormalParameterTypes) {
            boolean isNotFirst = false;
            for (String type : parameterTypes) {
                if (isNotFirst) {
                    stringBuilder.append(", ");
                }
                isNotFirst = true;
                stringBuilder.append(type);
            }

            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    protected String getAmbiguousCastsSequences(List<List<String>> castingTypes) {

        StringBuilder stringBuilder = new StringBuilder();
        if (castingTypes != null && !castingTypes.isEmpty()) {
            for (List<String> types : castingTypes) {
                stringBuilder.append(getCastsSequence(types));
                stringBuilder.append("\n");
            }
        } else {
            stringBuilder.append(" - ");
        }
        return stringBuilder.toString();
    }

    protected String getCastsSequence(List<String> castTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isNotFirst = false;
        for (String type : castTypes) {
            if (isNotFirst) {
                stringBuilder.append(" -> ");
            }
            isNotFirst = true;
            stringBuilder.append(type);
        }
        return stringBuilder.toString();
    }


    protected String getMissingSignatures(List<SignatureDto> signatureDtos) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SignatureDto signatureDto : signatureDtos) {
            stringBuilder.append(signatureDto.returnType).append(" ").append(signatureDto.identifier).append("(");
            boolean notFirst = false;
            for (String argumentType : signatureDto.argumentTypes) {
                if (notFirst) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(argumentType);
                notFirst = true;
            }
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }
}
