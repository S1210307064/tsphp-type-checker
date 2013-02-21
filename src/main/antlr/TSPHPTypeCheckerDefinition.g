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
tree grammar TSPHPTypeCheckerDefinition;
options {
	tokenVocab = TSPHP;
	ASTLabelType = ITSPHPAst;
	filter = true;        
}

@header{
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
package ch.tutteli.tsphp.typechecker.antlr;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;

}

@members {

protected ISymbolTable symbolTable;
protected IScope currentScope;
protected IScopeFactory scopeFactory;


public TSPHPTypeCheckerDefinition(TreeNodeStream input, ISymbolTable theSymbolTable) {
    this(input);
    symbolTable = theSymbolTable;    
}

}

topdown
		//scoped symbols
    	:	namespaceDeclaration
    	|	useDeclarationList
    	|	interfaceDeclaration
    	|	classDeclaration
    	|	constructDeclaration
    	|	methodFunctionDeclaration
    	|	conditionalBlock
    
    		//symbols
	|	constantDeclarationList
	|	parameterDeclarationList
	|	variableDeclarationList
	|	methodFunctionCall
	|	atom
	|	constant
    	;

bottomup
    	:   	exitNamespace
    	|	exitScope
   	;

exitNamespace
	:	Namespace
		{currentScope = currentScope.getEnclosingScope().getEnclosingScope();}
	;
	
exitScope
	:	(	'interface'
		|	'class'
		|	'__construct'
		|	METHOD_DECLARATION
		|	Function
		|	BLOCK_CONDITIONAL
		) 
		{currentScope = currentScope.getEnclosingScope();}
	;   
    
namespaceDeclaration
	:	^(Namespace t=(TYPE_NAME|DEFAULT_NAMESPACE) .) 
		{currentScope = symbolTable.defineNamespace($t.text); }
	;

useDeclarationList
	:	^('use'	useDeclaration+)
	;
	
useDeclaration
	:	^(USE_DECLARATION type=TYPE_NAME alias=Identifier)
		{symbolTable.defineUse((INamespaceScope) currentScope, $type, $alias);}
	;
	
interfaceDeclaration
	:	^('interface' iMod=. identifier=Identifier extIds=. .)
		{currentScope = symbolTable.defineInterface(currentScope, $iMod, $identifier, $extIds); }
	;
	
classDeclaration
	:	^('class' cMod=. identifier=Identifier extId=. implIds=. .) 
		{currentScope = symbolTable.defineClass(currentScope, $cMod, $identifier, $extId, $implIds); }	
	;
	
constructDeclaration
	:	^(identifier='__construct' mMod=.  ^(TYPE rtMod=. returnType=.) . .)
		{currentScope = symbolTable.defineConstruct(currentScope, $mMod, $rtMod, $returnType, $identifier);}
	;

methodFunctionDeclaration
	:	^( 	(	METHOD_DECLARATION
			|	Function
			) 
			mMod=. ^(TYPE rtMod=. returnType=.) identifier=. . .
		)
		{currentScope = symbolTable.defineMethod(currentScope,$mMod, $rtMod, $returnType, $identifier); }
	;
	
conditionalBlock
	:	^(BLOCK_CONDITIONAL .*) 
		{currentScope = symbolTable.defineConditionalScope(currentScope); }	
	;
	
constantDeclarationList
	:	^(CONSTANT_DECLARATION_LIST ^(TYPE tMod=. type=.) constantDeclaration[$tMod, $type]+)
	;

constantDeclaration[ITSPHPAst tMod, ITSPHPAst type]
	:	^(identifier=Identifier .)
		{ symbolTable.defineConstant(currentScope,$tMod, $type,$identifier); }
	;

parameterDeclarationList
	:	^(PARAMETER_LIST parameterDeclaration+)
	;

parameterDeclaration
	:	^(PARAMETER_DECLARATION 
			^(TYPE tMod=. type=.) variableDeclaration[$tMod,$type]
		)
	;

variableDeclarationList 
	:	^(VARIABLE_DECLARATION_LIST 
    			^(TYPE tMod=. type=.)
    			variableDeclaration[$tMod,$type]+
    		)
        ;
	
variableDeclaration[ITSPHPAst tMod, ITSPHPAst type]
	:
		(	^(variableId=VariableId .)
		|	variableId=VariableId	
		)
		{ symbolTable.defineVariable(currentScope, $tMod, $type, $variableId); }
	;
	
methodFunctionCall
	:	(	^(METHOD_CALL callee=. identifier=Identifier .)
			{$callee.setScope(currentScope);}
		|	^(METHOD_CALL_STATIC callee=. identifier=Identifier ACTUAL_PARAMETERS)
			{$callee.setScope(currentScope);}
		|	^(METHOD_CALL_POSTFIX identifier=Identifier ACTUAL_PARAMETERS)
		|	^(FUNCTION_CALL identifier=TYPE_NAME ACTUAL_PARAMETERS)
			{$identifier.setScope(currentScope);}
		)
		{
			$identifier.setText($identifier.text+"()");
		}
	;

atom	
	: 	(	identifier='$this'
		|	identifier=VariableId
    		|	identifier='parent'
    		|	identifier='self'
    			//self and parent are already covered above
    		|	^(CLASS_STATIC_ACCESS identifier=TYPE_NAME .)
    		|	^(CASTING ^(TYPE . type=allTypesWithoutObjectAndResource) .) {$identifier=$type.start;}
    		|	^('instanceof' . (identifier=VariableId | identifier=TYPE_NAME))
    		|	^('new' identifier=TYPE_NAME .)
	    	)
       		{$identifier.setScope(currentScope);}
	;

allTypesWithoutObjectAndResource
	:	'bool'
	|	'int'
	|	'float'
	|	'string'
	|	'array'
	|	TYPE_NAME	
	;


constant
	:	cst=CONSTANT
		{
			$cst.setText($cst.text+"#");
			$cst.setScope(currentScope);
		}
	;



