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
tree grammar TSPHPTypeCheckWalker;
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

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.IDefiner;
}

@members {
ISymbolTable symbolTable;
IDefiner definer;

public TSPHPTypeCheckWalker(TreeNodeStream input, ISymbolTable theSymbolTable) {
    this(input);
    symbolTable = theSymbolTable;
    definer = symbolTable.getDefiner();
}
}


bottomup 
    	:	expressionLists
   	|	expressionRoot 
   	|	variableInit
	;
    
expressionLists
	:	(	EXPRESSION_LIST
	    	|	ACTUAL_PARAMETERS	
   		|	TypeArray
	    	)
	    	expression+
	;
expressionRoot 
	:	^(	nil=(	EXPRESSION
	    		|	'return'
	    		|	'throw'
	    		|	'echo'
	    		|	ARRAY_ACCESS
	    		|	If
	    		|	Foreach
	    		|	While
	    		)
		    	expression
    		)
		{$nil.setEvalType($expression.type);}
	;
variableInit
	:	^(VariableId expression)
	;
	
expression returns [ITypeSymbol type]
@after { $start.setEvalType($type); } // do after any alternative
	:   	Bool		{$type = definer.getBoolTypeSymbol();}
    	|   	Int     	{$type =  definer.getIntTypeSymbol();}
    	|   	Float		{$type = definer.getFloatTypeSymbol();}
    	|   	String		{$type =  definer.getStringTypeSymbol();}
    	|	TypeArray	{$type = definer.getArrayTypeSymbol();}
    	|  	VariableId	{$type = $VariableId.getSymbol().getType();}
    	;
    	
    	
    	