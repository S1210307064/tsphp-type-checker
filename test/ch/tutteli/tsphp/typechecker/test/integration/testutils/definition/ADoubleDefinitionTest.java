package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;

@Ignore
public abstract class ADoubleDefinitionTest extends ADefinitionTest
{

    protected ScopeTestStruct[] testStructs;
    protected String namespace;
    protected String identifier;
    protected int occurence;

    public ADoubleDefinitionTest(String testString, String theNamespace, String theIdentifier, int howMany) {
        super(testString);
        namespace = theNamespace;
        identifier = theIdentifier;
        occurence = howMany;
    }

    @Override
    protected void verifyDefinitions() {
        ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaces = definer.getGlobalNamespaceScopes();
        IScope globalNamespace = globalNamespaces.get(namespace);
        Assert.assertNotNull(errorMessagePrefix + " failed, global namespace " + namespace + " could not be found.",
                globalNamespace);

        Map<String, List<ISymbol>> symbols = globalNamespace.getSymbols();
        Assert.assertNotNull(errorMessagePrefix + " failed, symbols was null.", symbols);
        Assert.assertTrue(errorMessagePrefix + " failed. " + identifier + " not found.", symbols.containsKey(identifier));
        Assert.assertEquals(errorMessagePrefix + " failed. size was wrong", occurence, symbols.get(identifier).size());
    }

    protected static Collection<Object[]> getDifferentNamespaces(String statements, String identifiers, int occurence) {
        return Arrays.asList(new Object[][]{
                    {statements, "\\", identifiers, occurence},
                    {"namespace b;" + statements, "\\b\\", identifiers, occurence},
                    {"namespace b\\c;" + statements, "\\b\\c\\", identifiers, occurence},
                    {"namespace{" + statements + "}", "\\", identifiers, occurence},
                    {"namespace b{" + statements + "}", "\\b\\", identifiers, occurence},
                    {"namespace b\\c\\e\\R{" + statements + "}", "\\b\\c\\e\\R\\", identifiers, occurence},
                    {"namespace{" + statements + "} namespace{" + statements + "}", "\\", identifiers, occurence * 2},
                    {
                        "namespace b{" + statements + "} namespace b{" + statements + "}",
                        "\\b\\", identifiers, occurence * 2
                    },
                    {
                        "namespace c{" + statements + "} namespace a{" + statements + "} "
                        + "namespace b{" + statements + "}",
                        "\\c\\", identifiers, occurence
                    },
                    {
                        "namespace c{" + statements + "} namespace a{" + statements + "} "
                        + "namespace c{" + statements + "}",
                        "\\c\\", identifiers, 2 * occurence
                    },
                    {
                        "namespace{" + statements + "} namespace {" + statements + "} namespace c{" + statements + "}",
                        "\\", identifiers, 2 * occurence
                    }
                });
    }
}
