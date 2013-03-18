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
	|	constantInit
   	|	parameterDefaultValue
	;
    
expressionLists
	:	^(	(	ACTUAL_PARAMETERS	
	   		|	TypeArray
		    	)
			expression+
		)
	;
expressionRoot 
	:	^(nil=EXPRESSION expr=expression)
	
 	|	^(nil='return' expr=expression?)
 		{
 		    ITSPHPAst exprAst = $expr.start;
 		    if (exprAst != null) {
		    	$nil.setEvalType(exprAst.getEvalType());
		    }
		    controller.checkReturn($nil, exprAst);
 		}
 	
 	|	^(nil='throw' expr=expression)
 		{controller.checkThrow($nil, $expr.start);}
 	
 	|	^(nil=ARRAY_ACCESS expr=expression index=expression)
 		{$nil.setEvalType(controller.getReturnTypeArrayAccess($nil, $expr.start, $index.start));}

 	|	^(nil=If expr=expression . .?)
 		{controller.checkIf($nil, $expr.start);}

 	|	^(nil=While expr=expression .)
 		{controller.checkWhile($nil, $expr.start);}

 	|	^(nil=Do instr=. expr=expression)
	 	{controller.checkDoWhile($nil, $expr.start);}

	|	^(nil=For . ^(EXPRESSION_LIST expr=expression+) . .)
		{controller.checkFor($nil,$expr.start);}

 	|	switchCondition
 	|	foreachLoop
 	|	tryCatch
 	|	echo
	;
	
switchCondition
	:	^(Switch condition=expression 
		{
		    $Switch.setEvalType($condition.type);
		    controller.checkSwitch($Switch,$condition.start);
		}
			(
				^(SWITCH_CASES (label=expression {controller.checkSwitchCase($Switch,$label.start);})* Default?) 
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
	
tryCatch
	:	^(Try . 
			(^(nil=Catch
				^(VARIABLE_DECLARATION_LIST type=. variableId=VariableId) .
				{
				    $variableId.setEvalType($variableId.getSymbol().getType());
				    controller.checkCatch($nil, $variableId);
				}
			))+
		)	
	;

echo 	:	^('echo' (expression {controller.checkEcho($expression.start);})+)
	;

variableInit
	:	{$start.getParent().getType()!=Catch && $start.getParent().getType()!=Foreach}?
		^(list=VARIABLE_DECLARATION_LIST 
			type=. 
			(	^(VariableId expression)
				{
		   		    $VariableId.setEvalType($VariableId.getSymbol().getType());
				    if($list.getParent().getType() != CLASS_MEMBER){
				         controller.checkInitialValue($VariableId, $expression.start);
				    } else {
				        controller.checkConstantInitialValue($VariableId, $expression.start);
				    }
				}
			)+
		)
		
	;
	
constantInit
	:	^(CONSTANT_DECLARATION_LIST type=.
			(	^(Identifier expression)
				{controller.checkConstantInitialValue($Identifier, $expression.start);}
			)+
		)	
	;
	
parameterDefaultValue
	:	
	|	^(PARAMETER_DECLARATION type=. ^(VariableId expression))
		{controller.checkConstantInitialValue($VariableId, $expression.start);}
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
	    	|	identifier=CONSTANT
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
	 	    controller.checkAssignment($start, $left.start, $right.start);
	 	    ITSPHPAst casting = (ITSPHPAst) $start.getChild(1);
    		    $type = casting.getType() == CASTING 
		         ? casting.getEvalType() 
		         : $right.start.getEvalType();
		}
	;
	
castOperator returns [ITypeSymbol type]
	:	^(CASTING_ASSIGN left=expression right=expression)
		{
		    controller.checkCastAssignment($start, $left.start, $right.start);
		    ITSPHPAst casting = (ITSPHPAst) $start.getChild(1);
    		    $type = casting.getType() == CASTING 
		         ? casting.getEvalType() 
		         : $right.start.getEvalType();
		}
		
	|	^(CASTING ^(TYPE . identifier=allTypes) right=expression)
		{
		    controller.checkCast($start, $identifier.start, $right.start);
		    ITSPHPAst casting = (ITSPHPAst) $start.getChild(1);
		    $type = casting.getType() == CASTING 
		         ? casting.getEvalType() 
		         : (ITypeSymbol) $identifier.start.getSymbol();
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
    		//{controller.checkClone($expression.start);}
	;