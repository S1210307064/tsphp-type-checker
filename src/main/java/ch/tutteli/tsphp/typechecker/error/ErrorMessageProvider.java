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
        definitionErrors.put("alreadyDefined", "Line %lineN%|%posN% - %idN% was already defined on "
                + "line %line%|%pos% %id%");
        definitionErrors.put("definedInOuterScope",
                "Line %lineN%|%posN% - %idN% was either already defined in outer scope or in another conditional scope."
                + " First definition was on line %line%|%pos% %id%\n"
                + "Please be aware, that conditional scopes are not real scopes, they do not actually create a new "
                + "scope");
        definitionErrors.put("aliasForwardReference",
                "Line %lineN%|%posN% - alias %idN% is used before its use declaration. Corresponding use declaration is"
                + " on line %line%|%pos%");
        definitionErrors.put("forwardReference",
                "Line %lineN%|%posN% - %idN% is used before its declaration. Corresponding declaration is "
                + "on line %line%|%pos%");
        definitionErrors.put("methodNotDefined", "Line %lineN%|%posN% - method %idN% is not defined in %id%");
        definitionErrors.put("memberNotDefined", "Line %lineN%|%posN% - class member %idN% is not defined in %id%");
        definitionErrors.put("objectExpected", "Line %lineN%|%posN% - object expected but %idN% is of type %id%");
    }

    @Override
    protected void loadReferenceErrorMessages() {
        referenceErrors = new HashMap<>();
        referenceErrors.put("unkownType", "Line %line%|%pos% - The type \"%id%\" could not be resolved.");
        referenceErrors.put("interfaceExpected", "Line %line%|%pos% - Interface expected, \"%id%\" is not an interface.");
        referenceErrors.put("classExpected", "Line %line%|%pos% - class expected, \"%id%\" is not a class.");
        referenceErrors.put("notInClass", "Line %line%|%pos% - %id% is used outside a class.");
        
        referenceErrors.put("noParentClass", "Line %line%|%pos% - class %id% has no parent class.");
        referenceErrors.put("notDefined", "Line %line%|%pos% - %id% was never defined.");
        referenceErrors.put("notStatic", "Line %line%|%pos% - %id% is not static.");
    }

    @Override
    protected String getStandardErrorDefinitionMessage(String key, DefinitionErrorDto dto) {
        return "DefinitionException occured, corresponding error message for \"" + key + "\" not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " was already defined on line "
                + dto.lineNewDefinition + "|" + dto.positionNewDefinition + ".";
    }

    @Override
    protected String getStandardErrorReferenceMessage(String key, ReferenceErrorDto dto) {
        return "ReferenceException occured, corresponding error message for \"" + key + "\" is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " could not been resolved to its"
                + "corresponding reference.";
    }
}
