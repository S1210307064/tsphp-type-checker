/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

/**
 * Represents a scalar type such as bool, int, float and string.
 * <p/>
 * String is considered as scalar type since there is not a smaller type such as char as in other languages.
 */
public interface IScalarTypeSymbol extends ITypeSymbolWithPHPBuiltInCasting
{
    int getDefaultValueTokenType();

    String getDefaultValueAsString();
}
