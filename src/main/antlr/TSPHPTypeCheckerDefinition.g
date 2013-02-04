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
@members {
	protected SymbolTable symbolTable;
	protected ISymbolFactory symbolFactory;
	private IScope currentScope;
    	public TSPHPTypeCheckerDefinition(TreeNodeStream input, SymbolTable theSymbolTable, ISymbolFactory theSymbolFactory) {
        	this(input);
	        symbolTable = theSymbolTable;
        	currentScope = theSymbolTable.globalScope;
    		symbolFactory = theSymbolFactory;
	}
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
import ch.tutteli.tsphp.typechecker.scopes.NamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

}


topdown
    :	enterNamespace
    |	varDeclarationList
    ;

bottomup
    :   exitNamespace
    ;
    
enterNamespace
	:	^(Namespace t=(TYPE_NAME|DEFAULT_NAMESPACE) .) {currentScope = new NamespaceScope($t.text,currentScope);}
	;
exitNamespace
	:	Namespace {currentScope = currentScope.getEnclosingScope();}
	;   

varDeclarationList 
    :   ^(VARIABLE_DECLARATION_LIST 
    		^(TYPE tMod=typeModifier type=.)
    		varDeclaration[$tMod.start,$type]*
    	)
        
    ;
    
typeModifier
	:	TYPE_MODIFIER
    	|	^(TYPE_MODIFIER Cast? QuestionMark?)
	;
	
varDeclaration[TSPHPAst tMod, TSPHPAst type]
	:
	(	^(variableId=VariableId (~CASTING))
	|	variableId=VariableId	
	|	^(variableId=VariableId cast)
	)
	{
	        $type.scope = currentScope;
	        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(tMod,$variableId);
	        currentScope.define(variableSymbol);
        }
	;

cast	:	^(CASTING ^(TYPE typeModifier .) .)
	;
