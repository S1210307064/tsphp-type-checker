/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

import java.util.List;

public class SignatureDto
{
    public String identifier;
    public String returnType;
    public List<String> argumentTypes;

    public SignatureDto(String theReturnType, String theIdentifier, List<String> theArgumentTypes) {
        identifier = theIdentifier;
        returnType = theReturnType;
        argumentTypes = theArgumentTypes;
    }
}
