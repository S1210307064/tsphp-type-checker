/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.error;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class AErrorMessageProvider implements IErrorMessageProvider
{

    protected Map<String, String> definitionErrors;
    protected Map<String, String> referenceErrors;
    protected Map<String, String> wrongArgumentTypeErrors;

    protected abstract void loadDefinitionErrorMessages();

    protected abstract void loadReferenceErrorMessages();

    protected abstract void loadWrongArgumentTyperrorMessages();

    protected abstract String getStandardDefinitionErrorMessage(String key, DefinitionErrorDto dto);

    protected abstract String getStandardReferenceErrorMessage(String key, ReferenceErrorDto dto);

    protected abstract String getStandardWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto dto);

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

            message = message.replace("%line%", "" + dto.line);
            message = message.replace("%pos%", "" + dto.position);

            message = message.replace("%idN%", dto.identifierNewDefinition);
            message = message.replace("%lineN%", "" + dto.lineNewDefinition);
            message = message.replace("%posN%", "" + dto.positionNewDefinition);

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
            message = message.replace("%id%", "" + dto.identifier);
            message = message.replace("%line%", "" + dto.line);
            message = message.replace("%pos%", "" + dto.position);
        } else {
            message = getStandardReferenceErrorMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto dto) {
        String message;
        if (wrongArgumentTypeErrors == null) {
            loadWrongArgumentTyperrorMessages();
        }
        if (wrongArgumentTypeErrors.containsKey(key)) {
            message = wrongArgumentTypeErrors.get(key);
            message = message.replace("%id%", "" + dto.identifier);
            message = message.replace("%line%", "" + dto.line);
            message = message.replace("%pos%", "" + dto.position);
            message = message.replace("%aParams%", Arrays.toString(dto.actualParameterTypes));
            message = message.replace("%overloads%", getOverloadSignatures(dto.possibleOverloads));
        } else {
            message = getStandardWrongArgumentTypeErrorMessage(key, dto);
        }
        return message;
    }

    protected String getOverloadSignatures(List<String[]> ambiguousFormalParameterTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] parameterTypes : ambiguousFormalParameterTypes) {
            stringBuilder.append(Arrays.toString(parameterTypes));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
