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
	ASTLabelType = TSPHPAst;
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
import ch.tutteli.tsphp.common.TSPHPAst;
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
	|	atom
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
	:	type=TYPE_NAME 
		{symbolTable.defineUse((INamespaceScope) currentScope,$type);}
		
	|	type=TYPE_NAME identifier=Identifier
		{symbolTable.defineUse((INamespaceScope) currentScope,$type, $identifier.text);}
	;
	
interfaceDeclaration
	:	^('interface' identifier=Identifier extIds=. .)
		{currentScope = symbolTable.defineInterface(currentScope,$identifier,$extIds); }
	;
	
classDeclaration
	:	^('class' cMod=. identifier=Identifier extIds=. implIds=. .) 
		{currentScope = symbolTable.defineClass(currentScope,$cMod,$identifier,$extIds,$implIds); }	
	;
	
constructDeclaration
	:	^(identifier='__construct' mMod=. . .)
		{currentScope = symbolTable.defineConstruct(currentScope, $mMod, $identifier);}
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
	:	^(CONSTANT_DECLARATION_LIST type=. constantDeclaration[$type]+)
	;

constantDeclaration[TSPHPAst type]
	:	^(identifier=Identifier .)
		{ symbolTable.defineConstant(currentScope, $type,$identifier); }
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
	
variableDeclaration[TSPHPAst tMod, TSPHPAst type]
	:
		(	^(variableId=VariableId .)
		|	variableId=VariableId	
		)
		{ symbolTable.defineVariable(currentScope, $tMod, $type, $variableId); }
	;

atom	
	: 	variableId=	(	'$this'
		    		|	VariableId
		    		|	CONSTANT
		    		|	CLASS_STATIC_ACCESS
		    		|	METHOD_CALL
		    		|	METHOD_CALL_STATIC
		    		|	FUNCTION_CALL
    				)
       		{variableId.scope = currentScope;}
	;
