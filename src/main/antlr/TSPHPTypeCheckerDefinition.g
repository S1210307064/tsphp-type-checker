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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;

}

@members {

protected ISymbolTable symbolTable;
protected IScope currentScope;
protected IScopeFactory scopeFactory;


public TSPHPTypeCheckerDefinition(TreeNodeStream input, IScopeFactory theScopeFactory, ISymbolTable theSymbolTable) {
    this(input);
    scopeFactory = theScopeFactory;
    currentScope = theScopeFactory.getGlobalScope();
    symbolTable = theSymbolTable;
}

}

topdown
	//scoped symbols
    	:	enterNamespace
    	|	enterInterface
    	|	enterClass
    	|	enterConstruct
    	|	enterMethodFunction
    	|	enterConditionalBlock
    
    		//symbols
	|	constantDeclarationList
	|	parameterDeclarationList
	|	variableDeclarationList
	|	atom
    	;

bottomup
    	:   exitScope
   	;
   
exitScope
	:	(	Namespace
		|	'interface'
		|	'class'
		|	'__construct'
		|	METHOD_DECLARATION
		|	Function
		|	BLOCK_CONDITIONAL
		) 
		{currentScope = currentScope.getEnclosingScope();}
	;   
    
enterNamespace
	:	^(Namespace t=(TYPE_NAME|DEFAULT_NAMESPACE) .) 
		{currentScope = scopeFactory.createNamespace($t.text, currentScope); }
	;

enterInterface
	:	^('interface' identifier=Identifier extIds=. .)
		{currentScope = symbolTable.defineInterface(currentScope,$identifier,$extIds); }
	;
	
enterClass
	:	^('class' cMod=. identifier=Identifier extIds=. implIds=. .) 
		{currentScope = symbolTable.defineClass(currentScope,$cMod,$identifier,$extIds,$implIds); }	
	;
	
enterConstruct
	:	^(identifier='__construct' mMod=. . .)
		{currentScope = symbolTable.defineConstruct(currentScope, $mMod, $identifier);}
	;

enterMethodFunction
	:	^( 	(	METHOD_DECLARATION
			|	Function
			) 
			mMod=. ^(TYPE rtMod=. returnType=.) identifier=. . .
		)
		{currentScope = symbolTable.defineMethod(currentScope,$mMod, $rtMod, $returnType, $identifier); }
	;
	
enterConditionalBlock
	:	^(BLOCK_CONDITIONAL .*) 
		{currentScope = scopeFactory.createConditionalScope(currentScope); }	
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
		    		|	FUNCTION_CALL
		    		|	METHOD_CALL_STATIC
    				)
       		{variableId.scope = currentScope;}
	;
