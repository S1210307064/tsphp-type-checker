package ch.tutteli.tsphp.typechecker.test.integration.definition;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class GlobalNamespaceTest extends ADefinitionTest
{

    private String[] namespaces;

    public GlobalNamespaceTest(String testString, String[] theNamespaces) {
        super(testString);
        namespaces = theNamespaces;
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyDefinitions() {
        ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = definer.getGlobalNamespaceScopes();
        assertThat(testString + " failed. size wrong ", globalNamespaceScopes.size(), equalTo(namespaces.length));

        for (String namespace : namespaces) {
            assertTrue(testString + " failed. Global namespace " + namespace + " did not exists in "
                    + globalNamespaceScopes.keySet(), globalNamespaceScopes.containsKey(namespace));
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {"int $a=1;", new String[]{"\\"}},
                    {"namespace{}", new String[]{"\\"}},
                    {"namespace{} namespace{}", new String[]{"\\"}},
                    {"namespace{} namespace b{} namespace a\\b{}", new String[]{"\\", "\\b\\", "\\a\\b\\"}},
                    {"namespace{} namespace{}  namespace a\\b{}", new String[]{"\\", "\\a\\b\\"}},
                    {"namespace{} namespace b{} namespace{} ", new String[]{"\\", "\\b\\"}},
                    {"namespace{} namespace{} namespace{} ", new String[]{"\\"}},
                    //default space is always created since int, float etc. is defined in default namespace
                    {"namespace a;", new String[]{"\\","\\a\\"}}
                });
    }
}
