/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.AmbiguousCallException;
import ch.tsphp.typechecker.CastingDto;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.OverloadDto;
import ch.tsphp.typechecker.OverloadResolver;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.ModifierSet;
import ch.tsphp.typechecker.symbols.TypeWithModifiersDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadResolverTest
{
    @Test(expected = AmbiguousCallException.class)
    public void getMostSpecificApplicableOverload_NoPromotionNoCasting_ThrowAmbiguousCallException()
            throws AmbiguousCallException {
        OverloadDto dto = new OverloadDto(mock(IMethodSymbol.class), 0, 0, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(dto);
        methods.add(dto);

        IOverloadResolver resolver = createResolver();
        resolver.getMostSpecificApplicableOverload(methods);

        //assert in @Test
    }

    @Test
    public void getMostSpecificApplicableOverload_1stPromotionBothNoCasting_Return2ndWithoutPromotion()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 0, 0, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(second));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stPromotion2ndCasting_Return1stWithoutCasting()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, new ArrayList<CastingDto>());
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stPromotion2ndLowerCasting_Return2ndWithLowerCasting()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        castings.add(mock(CastingDto.class));
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, new ArrayList<CastingDto>());
        castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stPromotionSameCasting_Return2ndWithoutPromotion()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(second);
        methods.add(first);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(second));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stPromotion2ndHigherCasting_Return1stWithLowerCasting()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test(expected = AmbiguousCallException.class)
    public void getMostSpecificApplicableOverload_SamePromotionBothNoCasting_ThrowAmbiguousCallException()
            throws AmbiguousCallException {
        OverloadDto dto = new OverloadDto(mock(IMethodSymbol.class), 1, 1, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(dto);
        methods.add(dto);

        IOverloadResolver resolver = createResolver();
        resolver.getMostSpecificApplicableOverload(methods);

        //assert in @Test
    }

    @Test
    public void getMostSpecificApplicableOverload_SamePromotion2ndCasting_Return1stWithoutCasting()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, new ArrayList<CastingDto>());
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(second);
        methods.add(first);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_SamePromotion2ndLowerCasting_Return2ndWithLowerCasting()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        castings.add(mock(CastingDto.class));
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(second));
    }

    @Test(expected = AmbiguousCallException.class)
    public void getMostSpecificApplicableOverload_SamePromotionSameCasting_ThrowAmbiguousCallException()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto dto = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(dto);
        methods.add(dto);

        IOverloadResolver resolver = createResolver();
        resolver.getMostSpecificApplicableOverload(methods);

        //assert in @Test
    }

    @Test
    public void getMostSpecificApplicableOverload_SamePromotion2ndHigherCasting_Return1stWithLowerCasting()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        castings.add(mock(CastingDto.class));
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 1, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(second);
        methods.add(first);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stLessParamsLowerTotalBothNoCasting_Return1stWithLessParameters()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 2, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 2, 3, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }


    @Test
    public void getMostSpecificApplicableOverload_1stLessParamsSameTotalBothNoCasting_Return1stWithLessParameters()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 3, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 2, 3, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_1stLessParamsHigherTotalBothNoCasting_Return1stWithLessParameters()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 5, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 2, 2, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_SameNoOfParams2ndLowerTotalNoCasting_Return2ndWithLowerTotal()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 5, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 3, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(second));
    }

    @Test
    public void getMostSpecificApplicableOverload_SameNoOfParams2ndHigherTotalNoCasting_Return1stWithLowerTotal()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 1, 2, new ArrayList<CastingDto>());
        OverloadDto second = new OverloadDto(mock(IMethodSymbol.class), 1, 4, new ArrayList<CastingDto>());
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(second);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }


    @Test
    public void getMostSpecificApplicableOverload_BothNoPromotion2ndCasting_Return1stWithoutCasting()
            throws AmbiguousCallException {
        OverloadDto first = new OverloadDto(mock(IMethodSymbol.class), 0, 0, new ArrayList<CastingDto>());
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto casting = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(first);
        methods.add(casting);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(first));
    }

    @Test
    public void getMostSpecificApplicableOverload_BothNoPromotion2ndHigherCasting_Return1stWithLowerCasting()
            throws AmbiguousCallException {
        List<CastingDto> castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        OverloadDto better = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        castings = new ArrayList<>();
        castings.add(mock(CastingDto.class));
        castings.add(mock(CastingDto.class));
        OverloadDto worse = new OverloadDto(mock(IMethodSymbol.class), 0, 0, castings);
        List<OverloadDto> methods = new ArrayList<>();
        methods.add(better);
        methods.add(worse);

        IOverloadResolver resolver = createResolver();
        OverloadDto result = resolver.getMostSpecificApplicableOverload(methods);

        assertThat(result, is(better));
    }

    @Test
    public void getPromotionLevelFromTo_SameTypeSymbol_Return0() {
        ITypeSymbol intType = mock(ITypeSymbol.class);

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromTo(intType, intType);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromTo_ActualIsSubTypeOfFormal_Return1() {
        ITypeSymbol actualType = mock(ITypeSymbol.class);
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(formalType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);
        IVariableSymbol actualSymbol = mock(IVariableSymbol.class);
        when(actualSymbol.getType()).thenReturn(actualType);

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromTo(actualType, formalType);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_ActualIsSubSubTypeOfFormal_Return2() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);

        ITypeSymbol middleType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsMiddle = new HashSet<>();
        parentsMiddle.add(formalType);
        when(middleType.getParentTypeSymbols()).thenReturn(parentsMiddle);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(middleType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromTo(actualType, formalType);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_SameTypeSymbolAndFormalIsNotFalseableNorNullable_Return0() {
        ITypeSymbol actualType = mock(ITypeSymbol.class);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(actualType, new ModifierSet());

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_SameTypeSymbolAndFormalIsFalseableButNotNullable_Return0() {
        ITypeSymbol actualType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(actualType, modifiers);

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_SameTypeSymbolAndFormalIsNotFalseableButNullable_Return0() {
        ITypeSymbol actualType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(actualType, modifiers);

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_SameTypeSymbolAndFormalIsFalseableAndNullable_Return0() {
        ITypeSymbol actualType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(actualType, modifiers);

        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubTypeAndFormalNeitherFalseableNorNullable_Return1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(formalType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubTypeAndFormalIsFalseableButNotNullable_Return1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(formalType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubTypeAndFormalIsNotFalseableButNullable_Return1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(formalType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubTypeAndFormalIsFalseableAndNullable_Return1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(formalType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubSubTypeAndFormalNotFalseableAndNotNullable_Return2() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol middleType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsMiddle = new HashSet<>();
        parentsMiddle.add(formalType);
        when(middleType.getParentTypeSymbols()).thenReturn(parentsMiddle);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(middleType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubSubTypeAndFormalFalseableButNotNullable_Return2() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol middleType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsMiddle = new HashSet<>();
        parentsMiddle.add(formalType);
        when(middleType.getParentTypeSymbols()).thenReturn(parentsMiddle);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(middleType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubSubTypeAndFormalNotFalseableButNullable_Return2() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol middleType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsMiddle = new HashSet<>();
        parentsMiddle.add(formalType);
        when(middleType.getParentTypeSymbols()).thenReturn(parentsMiddle);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(middleType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsSubSubTypeAndFormalFalseableAndNullable_Return2() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        ITypeSymbol middleType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsMiddle = new HashSet<>();
        parentsMiddle.add(formalType);
        when(middleType.getParentTypeSymbols()).thenReturn(parentsMiddle);

        ITypeSymbol actualType = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentsActual = new HashSet<>();
        parentsActual.add(middleType);
        when(actualType.getParentTypeSymbols()).thenReturn(parentsActual);

        //act
        IOverloadResolver resolver = createResolver();
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(actualType, "$a", dto);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsFalseAndFormalIsNotFalseableAndNotNullable_ReturnNeg1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        IScalarTypeSymbol bool = mock(IScalarTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getBoolTypeSymbol()).thenReturn(bool);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(bool, "false", dto);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsFalseAndFormalIsFalseableButNotNullable_Return0() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        IScalarTypeSymbol bool = mock(IScalarTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getBoolTypeSymbol()).thenReturn(bool);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(bool, "false", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsFalseAndFormalIsNotFalseableButNullable_ReturnNeg1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        IScalarTypeSymbol bool = mock(IScalarTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getBoolTypeSymbol()).thenReturn(bool);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(bool, "false", dto);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsFalseAndFormalIsFalseableAndNullable_Return0() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        IScalarTypeSymbol bool = mock(IScalarTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getBoolTypeSymbol()).thenReturn(bool);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(bool, "false", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsTrueAndFormalIsFalseableButNotNullable_ReturnNeg1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        IScalarTypeSymbol bool = mock(IScalarTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getBoolTypeSymbol()).thenReturn(bool);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(bool, "true", dto);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsNullAndFormalIsNotFalseableAndNotNullable_ReturnNeg1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        INullTypeSymbol nullType = mock(INullTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getNullTypeSymbol()).thenReturn(nullType);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(nullType, "null", dto);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsNullAndFormalIsFalseableButNotNullable_ReturnNeg1() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        INullTypeSymbol nullType = mock(INullTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getNullTypeSymbol()).thenReturn(nullType);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(nullType, "null", dto);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsNullAndFormalIsNotFalseableButNullable_Return0() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        INullTypeSymbol nullType = mock(INullTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getNullTypeSymbol()).thenReturn(nullType);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(nullType, "null", dto);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromToConsiderNullAndFalse_IsNullAndFormalIsFalseableAndNullable_Return0() {
        ITypeSymbol formalType = mock(ITypeSymbol.class);
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(TSPHPDefinitionWalker.LogicNot);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        TypeWithModifiersDto dto = new TypeWithModifiersDto(formalType, modifiers);

        INullTypeSymbol nullType = mock(INullTypeSymbol.class);
        ITypeSystem typeSystem = mock(ITypeSystem.class);
        when(typeSystem.getNullTypeSymbol()).thenReturn(nullType);

        //act
        IOverloadResolver resolver = createResolver(typeSystem);
        int result = resolver.getPromotionLevelFromToConsiderFalseAndNull(nullType, "null", dto);

        assertThat(result, is(0));
    }

    protected IOverloadResolver createResolver() {
        return new OverloadResolver(mock(ITypeSystem.class));
    }

    protected IOverloadResolver createResolver(ITypeSystem typeSystem) {
        return new OverloadResolver(typeSystem);
    }
}
