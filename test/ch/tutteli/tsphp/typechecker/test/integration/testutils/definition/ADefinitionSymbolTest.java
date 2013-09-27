package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;

@Ignore
public abstract class ADefinitionSymbolTest extends ADefinitionTest
{

    protected String expectedResult;

    public ADefinitionSymbolTest(String testString, String theExpectedResult) {
        super(testString);
        expectedResult = theExpectedResult;
    }

    @Override
    protected void verifyDefinitions() {
        super.verifyDefinitions();
        Assert.assertEquals(testString + " failed.", expectedResult, getSymbolsAsString());
    }
    
    public String getSymbolsAsString() {
        List<Map.Entry<ISymbol, ITSPHPAst>> symbols = definer.getSymbols();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for (Map.Entry<ISymbol, ITSPHPAst> entry : symbols) {
            if (!isFirstSymbol) {
                stringBuilder.append(" ");
            }
            isFirstSymbol = false;
            stringBuilder.append(getTypesAsString(entry.getValue()))
                    .append(ScopeTestHelper.getEnclosingScopeNames(entry.getKey().getDefinitionScope()))
                    .append(entry.getKey().toString());
        }
        return stringBuilder.toString();
    }

    protected String getTypesAsString(ITSPHPAst types) {
        String typesAsString;

        if (types == null) {
            typesAsString = "";
        } else if (types.getChildCount() == 0) {
            typesAsString = getSingleTypeAsString(types);
        } else {
            typesAsString = getMultipleTypesAsString(types);
        }

        return typesAsString;
    }

    protected String getSingleTypeAsString(ITSPHPAst type) {
        return ScopeTestHelper.getEnclosingScopeNames(type.getScope()) + type.getText() + " ";
    }

    protected String getMultipleTypesAsString(ITSPHPAst types) {

        StringBuilder stringBuilder = new StringBuilder();
        int lenght = types.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            stringBuilder.append(getSingleTypeAsString((ITSPHPAst) types.getChild(i)));
        }
        return stringBuilder.toString();
    }
}
