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
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
}

@members {
ITypeCheckerController controller;
ISymbolTable symbolTable;


public TSPHPTypeCheckWalker(TreeNodeStream input, ITypeCheckerController theController) {
    this(input);
    controller = theController;
    symbolTable = theController.getSymbolTable();
}
}


bottomup 
    	:	expressionLists
   	|	expressionRoot 
   	|	variableInit
	;
    
expressionLists
	:	^(	(	EXPRESSION_LIST
		    	|	ACTUAL_PARAMETERS	
	   		|	TypeArray
	    		|	'echo'
		    	)
			expression+
		)
	;
expressionRoot 
	:	^(nil=EXPRESSION expr=expression)
 	|	^(nil='return' expr=expression)
 	|	^(nil='throw' expr=expression)
 	
 	|	^(nil=ARRAY_ACCESS expr=expression)
 	|	booleanStatements
 	|	switchCondition
 	|	foreachLoop
	;

booleanStatements
	:	(	^(nil=If expr=expression . .?)
		|	^(nil=While expr=expression .)
		|	^(nil=Do instr=. expr=expression)
		)
 		{controller.checkIsType($nil, $expr.start, symbolTable.getBoolTypeSymbol());}	
	;
	
switchCondition
	:	^(Switch condition=expression {controller.checkIsType($Switch,$condition.start,symbolTable.getStringNullableTypeSymbol());}
			(
				^(SWITCH_CASES (label=expression {controller.checkIsType($Switch,$label.start,$condition.type);})* Default?) 
				.
			)*
		)
	;

foreachLoop
	:	^(nil=Foreach expr=expression
			
			//key 
			(	^(VARIABLE_DECLARATION_LIST
					^(TYPE TYPE_MODIFIER type=.) 
					keyVarId=VariableId
				)
			)?
				
			^(VARIABLE_DECLARATION_LIST type=. valueVarId=VariableId) 
			.
		)
		{
	    	    if ($keyVarId != null) {
    		        $keyVarId.setEvalType($keyVarId.getSymbol().getType());
    		    }
		    $valueVarId.setEvalType($valueVarId.getSymbol().getType());

		    controller.checkForeach($nil, $expr.start, $keyVarId, $valueVarId);
		}
	;

variableInit
	:	^(VariableId expression)
	;
	
expression returns [ITypeSymbol type]
@after { $start.setEvalType($type); } // do after any alternative
	:   	Bool			{$type = symbolTable.getBoolTypeSymbol();}
    	|   	Int     		{$type =  symbolTable.getIntTypeSymbol();}
    	|   	Float			{$type = symbolTable.getFloatTypeSymbol();}
    	|   	String			{$type =  symbolTable.getStringTypeSymbol();}
    	|	^(TypeArray .*)		{$type = symbolTable.getArrayTypeSymbol();}
    	|	Null			{$type = symbolTable.getNullTypeSymbol();}
    	|  	symbol			{$type = $symbol.type;}
	|	unaryOperator 		{$type = $unaryOperator.type;}
	|	binaryOperator 		{$type = $binaryOperator.type;}
 	|	^('@' expr=expression)	{$type = $expr.start.getEvalType();}
      	|	equalityOperator	{$type = $equalityOperator.type;}
      	|	assignOperator		{$type = $assignOperator.type;}
      	|	castOperator		{$type = $castOperator.type;}
    	;
    	
symbol returns [ITypeSymbol type]
	:	(	identifier=VariableId	
		|   	^(FUNCTION_CALL	identifier=TYPE_NAME .)
		|	^(METHOD_CALL_STATIC TYPE_NAME identifier=Identifier .)	
		|	^(METHOD_CALL . identifier=Identifier .)
		|	^(CLASS_STATIC_ACCESS . identifier=(CLASS_STATIC_ACCESS_VARIABLE_ID|CONSTANT))
		)
		{$type = $identifier.getSymbol().getType();}		
	;
   
unaryOperator returns [ITypeSymbol type]
	:	^(	(	PRE_INCREMENT
		    	|	PRE_DECREMENT 
		    	|	'~' 
		    	|	'!' 
		    	|	UNARY_MINUS     	
		    	|	UNARY_PLUS
		    	|	POST_INCREMENT 
		    	|	POST_DECREMENT 
			)
			expr=expression
		)
    		{$type = controller.getUnaryOperatorEvalType($start, $expr.start);}
   	;
   	   	

binaryOperator returns [ITypeSymbol type]
   	:	^(	(	'or' 
			|	'xor' 
			|	'and' 
			|	'||' 
			|	'&&' 
			|	'|' 
			|	'^' 
			|	'&' 
			|	'<' 
			|	'<=' 
			|	'>' 
			|	'>=' 
			|	'<<' 
			|	'>>' 
			|	'+' 
			|	'-' 
			|	'.' 
			|	'*' 
			|	'/' 
			|	'%' 
			)
			left=expression right=expression
		)
		{$type = controller.getBinaryOperatorEvalType($start, $left.start, $right.start);}
	;

equalityOperator returns [ITypeSymbol type]
	:	^(	(	'==' 			
			|	'!=' 
			|	'<>' 
			)
			left=expression right=expression
		)
		{
		    $type = symbolTable.getBoolTypeSymbol();
		    controller.checkEquality($start, $left.start, $right.start);
		}
		
	|	^( ('===' |'!==') left=expression right=expression)
		{
		    $type = symbolTable.getBoolTypeSymbol();
		    controller.checkIdentity($start, $left.start, $right.start);
		}
	;

assignOperator returns [ITypeSymbol type]
	:	^('=' left=expression right=expression )	
		{
		    $type = $left.start.getEvalType();
	 	    controller.checkAssignment($start, $left.start, $right.start);
		}
	;
	
castOperator returns [ITypeSymbol type]
	:	^(CASTING_ASSIGN left=expression right=expression)
		{
		    $type = $left.start.getEvalType();
		    controller.checkCastAssignment($start, $left.start, $right.start);
		}
		
	|	^(CASTING ^(TYPE . identifier=allTypes) right=expression)
		{
		    $type = $identifier.start.getSymbol().getType();
		    controller.checkCast($start, $identifier.start, $right.start);
		}
	;

allTypes
	:	'bool'
	|	'int'
	|	'float'
	|	'string'
	|	'array'
	|	'resource'
	|	'object'
	|	TYPE_NAME
	;


specialOperators
	:	^('?' condition=expression caseTrue=expression caseFalse=expression)
	|	^('instanceof' variable=expression type=(TYPE_NAME|VariableId))
    	|	^('new' type=TYPE_NAME args=.)
    	|	^('clone' expression)
	;
    	