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

import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class AErrorMessageProvider implements IErrorMessageProvider
{

    protected Map<String, String> definitionErrors;
    protected Map<String, String> referenceErrors;

    protected abstract void loadDefinitionErrorMessages();

    protected abstract void loadReferenceErrorMessages();

    protected abstract String getStandardErrorDefinitionMessage(String key, DefinitionErrorDto dto);

    protected abstract String getStandardErrorReferenceMessage(String key, ReferenceErrorDto dto);

    @Override
    public String getErrorDefinitionMessage(String key, DefinitionErrorDto dto) {
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
            message = getStandardErrorDefinitionMessage(key, dto);
        }
        return message;
    }

    @Override
    public String getErrorReferenceMessage(String key, ReferenceErrorDto dto) {
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
            message = getStandardErrorReferenceMessage(key, dto);
        }
        return message;
    }
}
