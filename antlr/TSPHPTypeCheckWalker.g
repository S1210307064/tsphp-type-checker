/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

tree grammar TSPHPTypeCheckWalker;
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

import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
}

@members {
private ITypeCheckPhaseController controller;
private IAccessResolver accessResolver;
private ITypeSystem typeSystem;

public TSPHPTypeCheckWalker(TreeNodeStream input, ITypeCheckPhaseController theController, IAccessResolver theAccessResolver, ITypeSystem theTypeSystem) {
    this(input);
    controller = theController;
    accessResolver = theAccessResolver;
    typeSystem = theTypeSystem;
}
}

bottomup 
	:	classInterfaceDefinition
	|	expressionLists
	|	expressionRoot 
	|	variableInit
	|	constantInit
	|	parameterDefaultValue
	;
	
classInterfaceDefinition
	:	( 	^('class' . identifier=. . . .)
	       	|	^('interface' . identifier=. . .)
	       	)
		{controller.checkPolymorphism(identifier);}
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
	:	{$start.getParent() != null && $start.getParent().getType()!=Catch && $start.getParent().getType()!=Foreach}?
		^(list=VARIABLE_DECLARATION_LIST 
			type=. 
			(	(	^(variableId=VariableId expression)
					{
			   		    $variableId.setEvalType($variableId.getSymbol().getType());
					    if($list.getParent().getType() != CLASS_MEMBER){
					         controller.checkInitialValue($variableId, $expression.start);
					    } else {
					        controller.checkClassMemberInitialValue($variableId, $expression.start);
					    }
					}
				|	variableId=VariableId
					{if($list.getParent().getType() == CLASS_MEMBER) controller.addDefaultValue($variableId);}
				)
			)+
		)
	;
	
constantInit
	:	^(CONSTANT_DECLARATION_LIST type=.
			(	^(Identifier expression)
				{
				    $Identifier.setEvalType($Identifier.getSymbol().getType());
				    controller.checkConstantInitialValue($Identifier, $expression.start);
				}
			)+
		)	
	;
	
parameterDefaultValue
	:	
	|	^(PARAMETER_DECLARATION type=. ^(variableId=VariableId expression))
		{
		    $variableId.setEvalType($variableId.getSymbol().getType());
		    controller.checkConstantInitialValue($variableId, $expression.start);
		}
	;
	
expression returns [ITypeSymbol type]
@after { $start.setEvalType($type); } // do after any alternative
	:   	Bool			{$type = typeSystem.getBoolTypeSymbol();}
    	|   	Int     		{$type = typeSystem.getIntTypeSymbol();}
    	|   	Float			{$type = typeSystem.getFloatTypeSymbol();}
    	|   	String			{$type = typeSystem.getStringTypeSymbol();}
    	|	^(TypeArray .*)		{$type = typeSystem.getArrayTypeSymbol();}
    	|	Null			{$type = typeSystem.getNullTypeSymbol();}
    	|  	symbol			{$type = $symbol.type;}
	|	unaryOperator 		{$type = $unaryOperator.type;}
	|	binaryOperator 		{$type = $binaryOperator.type;}
 	|	^('@' expr=expression)	{$type = $expr.start.getEvalType();}
      	|	equalityOperator	{$type = $equalityOperator.type;}
      	|	identityOperator	{$type = $identityOperator.type;}
      	|	assignOperator		{$type = $assignOperator.type;}
      	|	castOperator		{$type = $castOperator.type;}
   	|	specialOperators	{$type = $specialOperators.type;}
   	|	postFixOperators	{$type = $postFixOperators.type;}
    	;
    	
symbol returns [ITypeSymbol type]
	:	(	identifier=VariableId	
		|	identifier=This
	    	|	identifier=CONSTANT
		|   	^(FUNCTION_CALL	identifier=TYPE_NAME args=.)
			{
			    IMethodSymbol methodSymbol = controller.resolveFunctionCall($identifier, $args);
		     	    $identifier.setSymbol(methodSymbol);
			    $type = methodSymbol.getType();
			}
			
		|	^(METHOD_CALL callee=. identifier=Identifier args=.)
			{
			    $callee.setEvalType($callee.getSymbol().getType());
			    IMethodSymbol methodSymbol = controller.resolveMethodCall($callee, $identifier, $args);
			    $identifier.setSymbol(methodSymbol);
			    $type = methodSymbol.getType();
			}
			
		|	^(METHOD_CALL_STATIC calleeStatic=TYPE_NAME identifier=Identifier args=.)	
			{
			    $calleeStatic.setEvalType((ITypeSymbol) $calleeStatic.getSymbol());
			    IMethodSymbol methodSymbol = controller.resolveStaticMethodCall($calleeStatic, $identifier, $args);
			    $identifier.setSymbol(methodSymbol);
			    $type = methodSymbol.getType();
			}

		|	^(CLASS_STATIC_ACCESS accessor=. (identifier=CLASS_STATIC_ACCESS_VARIABLE_ID|identifier=CONSTANT))
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
    		{$type = controller.resolveUnaryOperatorEvalType($start, $expr.start);}
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
		{$type = controller.resolveBinaryOperatorEvalType($start, $left.start, $right.start);}
	;

equalityOperator returns [ITypeSymbol type]
	:	^(	(	'==' 			
			|	'!=' 
			)
			left=expression right=expression
		)
		{
		    $type = typeSystem.getBoolTypeSymbol();
		    controller.checkEquality($start, $left.start, $right.start);
		}
	;
	
identityOperator returns [ITypeSymbol type]
	:	^( ('===' |'!==') left=expression right=expression)
		{
		    $type = typeSystem.getBoolTypeSymbol();
		    controller.checkIdentity($start, $left.start, $right.start);
		}
	;

assignOperator returns [ITypeSymbol type]
	:	^('=' left=expression right=expression )	
		{
	 	    controller.checkAssignment($start, $left.start, $right.start);
		//Warning! start duplicated code as in castOperator
	 	    ITSPHPAst cast = (ITSPHPAst) $start.getChild(1);
    		    $type = cast.getType() == CAST 
		         ? cast.getEvalType() 
		         : $right.start.getEvalType();
		//Warning! end duplicated code as in castOperator
		}
	;
	
castOperator returns [ITypeSymbol type]
	:	^(CAST_ASSIGN left=expression right=expression)
		{
		    controller.checkCastAssignment($start, $left.start, $right.start);
		//Warning! start duplicated code as in castOperator
		    ITSPHPAst cast = (ITSPHPAst) $start.getChild(1);
    		    $type = cast.getType() == CAST
		         ? cast.getEvalType() 
		         : $right.start.getEvalType();
       		//Warning! end duplicated code as in castOperator
		}
		
	|	^(CAST ^(TYPE . identifier=allTypes) right=expression)
		{
		    controller.checkCast($start, $identifier.start, $right.start);
		    ITSPHPAst cast = (ITSPHPAst) $start.getChild(1);
		    $type = cast.getType() == CAST
		         ? cast.getEvalType() 
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


specialOperators returns [ITypeSymbol type]
	:	^(nil='?' condition=expression caseTrue=expression caseFalse=expression)
		{$type = controller.resolveTernaryOperatorEvalType($nil, $condition.start, $caseTrue.start, $caseFalse.start);}
		
	|	^('instanceof' 
			expr=expression 
			(	identifier=TYPE_NAME 
				{$identifier.setEvalType((ITypeSymbol)$identifier.getSymbol());}
			|	identifier=VariableId
				{$identifier.setEvalType($identifier.getSymbol().getType());}
			)
		)
		{
		    
		    $type = typeSystem.getBoolTypeSymbol();
		    controller.checkInstanceof($start, $expr.start, $identifier);
		}
    	|	^(nil='new' identifier=TYPE_NAME args=.)
    		{
    		    $type = (ITypeSymbol) $identifier.getSymbol();
    		}
    	|	^(nil='clone' expression)
    		{
    		    $type = $expression.type;
    		    controller.checkClone($nil, $expression.start);
    		}
	;

postFixOperators returns [ITypeSymbol type]
	:	^(CLASS_MEMBER_ACCESS accessor=expression Identifier)
		{
		    IVariableSymbol variableSymbol = accessResolver.resolveClassMemberAccess($accessor.start, $Identifier);
		    $Identifier.setSymbol(variableSymbol);
		    $type = variableSymbol.getType();
		}	
		
	|	^(nil=ARRAY_ACCESS expr=expression index=expression)
 		{$type = controller.resolveReturnTypeArrayAccess($nil, $expr.start, $index.start);}	
 		
 	|	^(METHOD_CALL_POSTFIX callee=expression identifier=Identifier args=.)
 		{
 		    IMethodSymbol methodSymbol = controller.resolveMethodCall($callee.start, $identifier, $args);
		    $identifier.setSymbol(methodSymbol);
		    $type = methodSymbol.getType();
 		}
	;
