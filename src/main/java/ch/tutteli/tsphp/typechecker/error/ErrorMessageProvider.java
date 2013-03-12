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
        referenceErrors.put("variableExpected", "Line %line%|%pos% - assignments can only be made to variables,"
                + " \"%id%\" is not a variable.");
        referenceErrors.put("notInClass", "Line %line%|%pos% - %id% is used outside a class.");

        referenceErrors.put("noParentClass", "Line %line%|%pos% - class %id% has no parent class.");
        referenceErrors.put("notDefined", "Line %line%|%pos% - %id% was never defined.");
        referenceErrors.put("notStatic", "Line %line%|%pos% - %id% is not static.");
    }

    @Override
    protected void loadWrongArgumentTyperrorMessages() {
        wrongArgumentTypeErrors = new HashMap<>();
        wrongArgumentTypeErrors.put("wrongOperatorUsage", "Line %line%|%pos% - usage of operator %id% is wrong.\n"
                + "It cannot be applied to the given types for LHS/RHS: %aParams%\n"
                + "existing overloads: %overloads%");
        wrongArgumentTypeErrors.put("ambiguousOperatorUsage", "Line %line%|%pos% - usage of operator %id% is ambiguous."
                + "\ntypes LHS/RHS: %aParams%\n"
                + "ambiguous overloads: %overloads%");
    }

    @Override
    protected void loadTypeCheckErrorMessages() {
        typeCheckErrors = new HashMap<>();
        typeCheckErrors.put("equalityOperator", "Line %line%|%pos% - usage of operator %id% is wrong.\n"
                + "LHS/RHS cannot be compared because they are not from the same type, non of them is a sub-type of "
                + "the other and there does not exists an explicit cast from one type to the other.\n"
                + "The following types where found: [%tExp%, %tFound%]");
        typeCheckErrors.put("wrongCast", "Line %line%|%pos% - cannot cast %tFound% to %tExp%");
        typeCheckErrors.put("identityOperator", "Line %line%|%pos% - usage of operator %id% is wrong.\n"
                + "LHS/RHS have to be from the same type or one has to be a sub-type of the other.\n"
                + "The following types where found: [%tExp%, %tFound%]");
        typeCheckErrors.put("wrongAssignment", "Line %line%|%pos% - cannot assign RHS to LHS using the operator %id% "
                + "types are not compatible.\n"
                + "types LHS/RHS: [%tExp%, %tFound%]");

    }

    @Override
    protected void loadAmbiguousCastsErrorMessages() {
        ambiguousCastsErrors = new HashMap<>();
        ambiguousCastsErrors.put("operatorBothSideCast", "Line %line%|%pos% - ambiguous cast detected in conjunction "
                + "with the operator %id%. LHS can be casted to RHS and RHS can be casted to LHS.\n"
                + "cast LHS to RHS: %LHS%\n"
                + "cast RHS to LHS: %RHS%");
        ambiguousCastsErrors.put("operatorAmbiguousCasts", "Line %line%|%pos% - ambiguous cast detected in conjunction "
                + "with the operator %id%. LHS can be casted to RHS and RHS can be casted to LHS.\n"
                + "cast LHS to RHS: %LHS%\n"
                + "cast RHS to LHS: %RHS%\n"
                + "Further ambiguouities:\n"
                + "ambiguous casts LHS to RHS: %ambLHS%\n"
                + "ambiguous casts RHS to LHS: %ambRHS%");

        ambiguousCastsErrors.put("ambiguousCasts", "Line %line%|%pos% - cast from %RHS% to %LHS% is "
                + "ambiguous.\n"
                + "The following ambiguous casts were found: %ambRHS%");
    }

    @Override
    protected String getStandardDefinitionErrorMessage(String key, DefinitionErrorDto dto) {
        return "DefinitionException occured, corresponding error message for \"" + key + "\" not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " was already defined on line "
                + dto.lineNewDefinition + "|" + dto.positionNewDefinition + ".";
    }

    @Override
    protected String getStandardReferenceErrorMessage(String key, ReferenceErrorDto dto) {
        return "ReferenceException occured, corresponding error message for \"" + key + "\" is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - " + dto.identifier + " could not been resolved to its"
                + "corresponding reference.";
    }

    @Override
    protected String getStandardWrongArgumentTypeErrorMessage(String key, WrongArgumentTypeErrorDto dto) {
        return "WrongArgumentTypeException occured, corresponding error message for \"" + key + "\" is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - usage of " + dto.identifier + " was wrong.\n"
                + "types actual parameters: " + dto.actualParameterTypes.toString() + "\n"
                + "existing overloads: " + getOverloadSignatures(dto.possibleOverloads);
    }

    @Override
    protected String getStandardTypeCheckErrorMessage(String key, TypeCheckErrorDto dto) {
        return "TypeCheckException occured, corresponding error message for \"" + key + "\" is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - usage of " + dto.identifier + " was wrong.\n"
                + "type expected: " + dto.typeExpected + "\n"
                + "type found:: " + dto.typeFound;
    }

    @Override
    protected String getStandardAmbiguousCastsErrorMessage(String key, AmbiguousCastsErrorDto dto) {
        return "AmbiguousCastsException occured, corresponding error message for \"" + key + "\" is not defined. "
                + "Please report bug to http://tsphp.tutteli.ch\n"
                + "However, the following information was gathered.\n"
                + "Line " + dto.line + "|" + dto.position + " - usage of " + dto.identifier + " was wrong.\n"
                + "casts LHS to RHS or LHS type: " + getCastsSequence(dto.leftToRightCasts) + "\n"
                + "casts RHS to LHS or RHS type: " + getCastsSequence(dto.rightToLeftCasts) + "\n"
                + "ambiguous casts LHS to RHS:" + getAmbiguousCastsSequences(dto.leftAmbiguouities) + "\n"
                + "ambiguous casts RHS to LHS:" + getAmbiguousCastsSequences(dto.rightAmbiguouities);
    }
}
