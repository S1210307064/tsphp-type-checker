/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

tree grammar TSPHPDefinitionWalker;
options {
    tokenVocab = TSPHP;
    ASTLabelType = ITSPHPAst;
    filter = true;        
}

@header{
/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.antlr;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IDefinitionPhaseController;
import ch.tsphp.typechecker.scopes.INamespaceScope;

}

@members {

private IDefinitionPhaseController definer;
private IScope currentScope;


public TSPHPDefinitionWalker(TreeNodeStream input, IDefinitionPhaseController theDefiner) {
    this(input);
    definer = theDefiner;    
}

}

topdown
        //scoped symbols
    :   namespaceDefinition
    |   useDefinitionList
    |   interfaceDefinition
    |   classDefinition
    |   constructDefinition
    |   methodFunctionDefinition
    |   blockConditional
    |   foreachLoop
    
        //symbols
    |   constantDefinitionList
    |   parameterDeclarationList
    |   variableDeclarationList
    |   methodFunctionCall
    |   atom
    |   constant
    |   returnBreakContinue
    ;

bottomup
    :   exitNamespace
    |   exitScope
    ;

exitNamespace
    :   Namespace
        {currentScope = currentScope.getEnclosingScope().getEnclosingScope();}
    ;
    
exitScope
    :   (   'interface'
        |   'class'
        |   '__construct'
        |   METHOD_DECLARATION
        |   Function
        |   BLOCK_CONDITIONAL
        |   Foreach
        ) 
        {
            //only get enclosing scope if a scope was defined - might not be the case due to syntax errors
            if(!(currentScope instanceof INamespaceScope)){
                currentScope = currentScope.getEnclosingScope();
            }
        }
    ;   
    
namespaceDefinition
    :   ^(Namespace t=(TYPE_NAME|DEFAULT_NAMESPACE) .)
        {currentScope = definer.defineNamespace($t.text); }
    ;

useDefinitionList
    :   ^('use'    useDeclaration+)
    ;
    
useDeclaration
    :   ^(USE_DECLARATION type=TYPE_NAME alias=Identifier)
        {definer.defineUse((INamespaceScope) currentScope, $type, $alias);}
    ;
    
interfaceDefinition
    :   ^('interface' iMod=. identifier=Identifier extIds=. .)
        {currentScope = definer.defineInterface(currentScope, $iMod, $identifier, $extIds); }
    ;
    
classDefinition
    :   ^('class' cMod=. identifier=Identifier extId=. implIds=. .)
        {currentScope = definer.defineClass(currentScope, $cMod, $identifier, $extId, $implIds); }    
    ;
    
constructDefinition
    :   ^(identifier='__construct' mMod=.  ^(TYPE rtMod=. returnType=.) . .)
        {currentScope = definer.defineConstruct(currentScope, $mMod, $rtMod, $returnType, $identifier);}
    ;

methodFunctionDefinition
    :   ^(  (   METHOD_DECLARATION
            |   Function
            )
            mMod=. ^(TYPE rtMod=. returnType=.) identifier=. . .
        )
        {currentScope = definer.defineMethod(currentScope,$mMod, $rtMod, $returnType, $identifier); }
    ;
    
blockConditional
    :   ^(block=BLOCK_CONDITIONAL .*)
        {
            currentScope = definer.defineConditionalScope(currentScope);
            $block.setScope(currentScope);
        }    
    ;
    
foreachLoop
    :   ^(Foreach .*)
        {
            currentScope = definer.defineConditionalScope(currentScope);
            $Foreach.setScope(currentScope);
        }    
    ;
    
constantDefinitionList
    :   ^(CONSTANT_DECLARATION_LIST ^(TYPE tMod=. type=.) constantDeclaration[$tMod, $type]+)
    ;

constantDeclaration[ITSPHPAst tMod, ITSPHPAst type]
    :   ^(identifier=Identifier .)
        { definer.defineConstant(currentScope,$tMod, $type,$identifier); }
    ;

parameterDeclarationList
    :   ^(PARAMETER_LIST parameterDeclaration+)
    ;

parameterDeclaration
    :   ^(PARAMETER_DECLARATION
            ^(TYPE tMod=. type=.) variableDeclaration[$tMod,$type]
        )
    ;

variableDeclarationList 
    :   ^(VARIABLE_DECLARATION_LIST
            ^(TYPE tMod=. type=.)
                variableDeclaration[$tMod,$type]+
            )
    ;
    
variableDeclaration[ITSPHPAst tMod, ITSPHPAst type]
    :
        (   ^(variableId=VariableId .)
        |   variableId=VariableId
        )
        {definer.defineVariable(currentScope, $tMod, $type, $variableId);}
    ;
    
methodFunctionCall
    :   (   ^(METHOD_CALL callee=. identifier=Identifier .)
            {$callee.setScope(currentScope);}
        |   ^(METHOD_CALL_STATIC callee=. identifier=Identifier .)
            {$callee.setScope(currentScope);}
        |   ^(METHOD_CALL_POSTFIX identifier=Identifier .)
        |   ^(FUNCTION_CALL identifier=TYPE_NAME .)
            {$identifier.setScope(currentScope);}
        )
    ;

atom    
    :   (   identifier='$this'
        |   identifier=VariableId
        |    identifier='parent'
        |    identifier='self'
            //self and parent are already covered above
        |    ^(CLASS_STATIC_ACCESS identifier=(TYPE_NAME|'self'|'parent') .)
        |    ^(CAST ^(TYPE . type=allTypesWithoutMixedAndResource) .) {$identifier=$type.start;}
        |    ^('instanceof' . (identifier=VariableId | identifier=TYPE_NAME))
        |    ^('new' identifier=TYPE_NAME .)
        )
        {$identifier.setScope(currentScope);}
    ;

allTypesWithoutMixedAndResource
    :   'bool'
    |   'int'
    |   'float'
    |   'string'
    |   'array'
    |   TYPE_NAME
    ;


constant
    :   cst=CONSTANT
        {$cst.setScope(currentScope);}
    ;

returnBreakContinue
    :   (   Return
        |   Break
        |   Continue
        )
        {$start.setScope(currentScope);}
    ;
