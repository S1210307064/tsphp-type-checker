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

import java.util.HashMap;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErrorMessageProvider extends AErrorMessageProvider
{

    @Override
    protected void loadDefinitionErrorMessages() {
        definitionErrors = new HashMap<>();
        definitionErrors.put("alreadyDefined", "Line %line%|%pos% - %id% was already defined in line %lineE%|%posE%");
        definitionErrors.put("definedInOuterScope",
                "Line %line%|%pos% - %id% was already defined in outerscope in line %lineE%|%posE%");
        definitionErrors.put("aliasForwardReference",
                "Line %line%|%pos% - alias is used before its use declaration. Corresponding use declaration is "
                + "in line %lineE%|%posE%");
    }

    @Override
    protected void loadReferenceErrorMessages() {
        referenceErrors = new HashMap<>();
        referenceErrors.put("unkownType", "Line %line%|%pos% - The type \"%id%\" could not be resolved.");
    }

    @Override
    protected String getStandardErrorDefinitionMessage(DefinitionErrorDto dto) {
        return "DefinitionException occured, corresponding error message is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " was already defined in line "
                + dto.lineNewDefinition + "|" + dto.positionNewDefinition + ".";
    }

    @Override
    protected String getStandardErrorReferenceMessage(UnresolvedReferenceErrorDto dto) {
        return "UnresolvedReferenceException occured, corresponding error message is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " could not been resolved to its"
                + "corresponding reference.";
    }
}
