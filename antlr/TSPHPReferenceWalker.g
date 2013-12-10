tree grammar TSPHPReferenceWalker;
options {
	tokenVocab = TSPHP;
	ASTLabelType = ITSPHPAst;
}

@header{
package ch.tutteli.tsphp.typechecker.antlr;

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPErrorAst;
import ch.tutteli.tsphp.typechecker.EReturnState;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVoidTypeSymbol;

}

@members {
private ITypeCheckerController controller;

public TSPHPReferenceWalker(TreeNodeStream input, ITypeCheckerController theController) {
    this(input);
    controller = theController;
}
}

compilationUnit
	:	namespace+	
	;
	
namespace
	:	^(Namespace . n=namespaceBody)
	;	

namespaceBody
	:	^(NAMESPACE_BODY statement*)
	|	NAMESPACE_BODY	
	;

statement
	:	useDeclarationList
	|	definition
	|	instruction[false]
	;
	
useDeclarationList
	:	^(Use	useDeclaration+)
	;
	
useDeclaration
	:	^(USE_DECLARATION typeName=TYPE_NAME alias=Identifier)
		{
			ITypeSymbol typeSymbol = controller.resolveUseType($typeName, $alias);
			$typeName.setSymbol(typeSymbol);
			$alias.getSymbol().setType(typeSymbol);
			
			INamespaceScope namespaceScope = (INamespaceScope) $alias.getScope();
			namespaceScope.useDefinitionCheck((IAliasSymbol) $alias.getSymbol());
		}
	;

definition
	:	classDeclaration
	|	interfaceDeclaration
	|	functionDeclaration
	|	constDeclarationList
	;
	
classDeclaration
	:	^(Class 
			cMod=. 
			identifier=Identifier 
			classExtendsDeclaration[$identifier] 
			implementsDeclaration[identifier] 
			classBody) 
		{
			INamespaceScope namespaceScope = (INamespaceScope) $identifier.getScope();
			namespaceScope.doubleDefinitionCheckCaseInsensitive($identifier.getSymbol());
		}
	;

classExtendsDeclaration[ITSPHPAst identifier]
	:	^(Extends classInterfaceType)
		{
			ITypeSymbol typeSymbol = $classInterfaceType.type;
			if(controller.checkIsClass($classInterfaceType.start, typeSymbol)){
			    IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) identifier.getSymbol();
			    classTypeSymbol.setParent((IClassTypeSymbol)typeSymbol);
			    classTypeSymbol.addParentTypeSymbol((IClassTypeSymbol)typeSymbol);
			}
		} 
	|	'extends'
	;

implementsDeclaration[ITSPHPAst identifier]
	:	^('implements' 	
			(classInterfaceType{
				ITypeSymbol typeSymbol = $classInterfaceType.type;
				if(controller.checkIsInterface($classInterfaceType.start, typeSymbol)){
				    ((IClassTypeSymbol)identifier.getSymbol()).addParentTypeSymbol((IInterfaceTypeSymbol)typeSymbol);
				}
			})+
		)
	|	'implements'
	;
	
classBody
	:	^(CLASS_BODY classBodyDefinition*)
	|	CLASS_BODY
	;
	
classBodyDefinition
	:	constDeclarationList
	|	classMemberDeclaration
	|	constructDeclaration
	|	methodDeclaration
	;

constDeclarationList
	:	^(CONSTANT_DECLARATION_LIST ^(TYPE tMod=. scalarTypes[false]) constDeclaration[$scalarTypes.type]+)
	;

constDeclaration[ITypeSymbol type]
	:	^(identifier=Identifier unaryPrimitiveAtom)
		{
			IVariableSymbol variableSymbol = (IVariableSymbol) $identifier.getSymbol();
			variableSymbol.setType(type); 
			$identifier.getScope().doubleDefinitionCheck(variableSymbol); 
		}
	;

unaryPrimitiveAtom
	:	primitiveAtomWithConstant
	|	^(	(	unary=UNARY_MINUS
			|	unary=UNARY_PLUS
			) primitiveAtomWithConstant
		)
	; 

primitiveAtomWithConstant
	:	Bool
	|	Int
	|	Float
	|	String
	|	Null
	|	array
	|	cnst=CONSTANT
		{
			IVariableSymbol variableSymbol = controller.resolveConstant($cnst);
			$cnst.setSymbol(variableSymbol);
			controller.checkIsForwardReference($cnst);
		}
	|	^(CLASS_STATIC_ACCESS accessor=staticAccessor identifier=CONSTANT)
		{$identifier.setSymbol(controller.resolveClassConstant($accessor.start, $identifier));}
	;
	
array	:	^(TypeArray arrayKeyValue*)
	;

arrayKeyValue
	:	^('=>' expression expression)
	|	value=expression
	;

staticAccessor
@after{$start.setEvalType((ITypeSymbol) $start.getSymbol());}
	:	classInterfaceType
	|	slf='self'
		{
		    IVariableSymbol variableSymbol = controller.resolveThisSelf($slf);
		    $slf.setSymbol(variableSymbol.getType());
		}
	|	par='parent'
		{
		    IVariableSymbol variableSymbol = controller.resolveParent($par);
		    $par.setSymbol(variableSymbol.getType());
		}	
	;
	
classMemberDeclaration
	:	^(CLASS_MEMBER variableDeclarationList)
	;
	
variableDeclarationList 
	:	^(VARIABLE_DECLARATION_LIST 
			^(TYPE variableModifier allTypes[$variableModifier.isNullable]) 
			variableDeclaration[$allTypes.type]+ 
		)
        ;

variableModifier returns[boolean isNullable]
	:	^(TYPE_MODIFIER 
			Cast? 
			nullable=QuestionMark? 
			(	Static (Private|Protected|Public) 
			| 	(Private|Protected|Public) Static?
			)?
		)
		{$isNullable = $nullable!=null;}
		
	|	TYPE_MODIFIER	
	;	
        
variableDeclaration[ITypeSymbol type] returns [IVariableSymbol variableSymbol]
	:	(	^(variableId=VariableId expression)
		|	variableId=VariableId	
		)
		{ 
			//Warning! start duplicated code as in parameterNormalOrOptional
			$variableSymbol = (IVariableSymbol) $variableId.getSymbol();
			$variableSymbol.setType(type); 
			$variableId.getScope().doubleDefinitionCheck($variableId.getSymbol());
			//Warning! end duplicated code as in parameterNormalOrOptional
		}
	;
	
accessModifier
	:	Private
	|	Protected
	|	Public
	;

constructDeclaration
	:	^(identifier='__construct' 
			.
			^(TYPE rtMod=. voidType) 
			parameterDeclarationList block[false]
		)
		{
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($voidType.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
		}
	;
		
methodDeclaration
@init{boolean shallCheckIfReturns = false;}
	:	^(METHOD_DECLARATION 
			^(METHOD_MODIFIER methodModifier)
			^(TYPE returnTypeModifier returnTypes[$returnTypeModifier.isNullable]) 
			{shallCheckIfReturns = !($returnTypes.type instanceof IVoidTypeSymbol) && !$methodModifier.isAbstract;}
			(identifier=Identifier|identifier=Destruct) parameterDeclarationList block[shallCheckIfReturns]
		)
		{
		//Warning! start duplicated code as in functionDeclaration
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($returnTypes.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
			if(shallCheckIfReturns){
		//Warning! end duplicated code as in functionDeclaration
			    controller.checkReturnsFromMethod($block.returnState, $identifier);
			}
		}

	;

methodModifier returns[boolean isAbstract]
	:	(	Static	Final		accessModifier
		|	Static	accessModifier	Final
		|	Static	accessModifier
		
		|	Final	Static	 	accessModifier
		|	Final	accessModifier 	Static	 
		|	Final	accessModifier
		
		
		|	accessModifier Final 	Static	
		|	accessModifier Static	Final
		|	accessModifier Static	
		|	accessModifier Final
		|	accessModifier
		
		|	abstr=Abstract accessModifier
		|	accessModifier abstr=Abstract
		)
		{$isAbstract= $abstr != null;}
	;

returnTypeModifier returns[boolean isNullable]
	:	^(TYPE_MODIFIER 
			Cast? 
			nullable=QuestionMark? 
		)
		{$isNullable = $nullable!=null;}
	;
	
functionDeclaration
@init{boolean shallCheckIfReturns = false;}
	:	^('function'
			.
			^(TYPE returnTypeModifier returnTypes[$returnTypeModifier.isNullable]) {shallCheckIfReturns = !($returnTypes.type instanceof IVoidTypeSymbol);}
			identifier=Identifier parameterDeclarationList block[shallCheckIfReturns]
		)
		{
		//Warning! start duplicated code as in functionDeclaration
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($returnTypes.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
			if(shallCheckIfReturns){
		//Warning! end duplicated code as in functionDeclaration
			    controller.checkReturnsFromFunction($block.returnState, $identifier);
			}
		}
	;

parameterDeclarationList
	:	^(PARAMETER_LIST parameterDeclaration+)
	|	PARAMETER_LIST
	;

parameterDeclaration
	:	^(PARAMETER_DECLARATION 
			^(TYPE variableModifier allTypes[$variableModifier.isNullable]) 
			parameterNormalOrOptional[$allTypes.type]
		)
		{
		    IVariableSymbol parameter = $parameterNormalOrOptional.variableSymbol;
		    IMethodSymbol methodSymbol = (IMethodSymbol) parameter.getDefinitionScope();
		    methodSymbol.addParameter(parameter);
		}
	;

parameterNormalOrOptional[ITypeSymbol type] returns [IVariableSymbol variableSymbol]
	:	(	variableId=VariableId
		|	^(variableId=VariableId unaryPrimitiveAtom)
		)
		{ 
			//Warning! start duplicated code as in variableDeclaration
			$variableSymbol = (IVariableSymbol) $variableId.getSymbol();
			$variableSymbol.setType(type); 
			$variableId.getScope().doubleDefinitionCheck($variableId.getSymbol());
			//Warning! end duplicated code as in variableDeclaration
		} 
	;
	
block[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^(BLOCK instructions[$shallCheckIfReturns]) {$returnState = $instructions.returnState;}
	|	BLOCK {$returnState = EReturnState.IsNotReturning;}
	;

interfaceDeclaration
	:	^('interface' iMod=. identifier=Identifier extIds=interfaceExtendsDeclaration[$identifier] interfaceBody)
		{
			INamespaceScope namespaceScope = (INamespaceScope) $identifier.getScope();
			namespaceScope.doubleDefinitionCheckCaseInsensitive($identifier.getSymbol());
		}
	;
interfaceExtendsDeclaration[ITSPHPAst identifier]
	:	^('extends' 	
			(allTypes[true]{	
				ITypeSymbol typeSymbol = $allTypes.type;
				if(controller.checkIsInterface($allTypes.start, typeSymbol)){
				    ((IInterfaceTypeSymbol)identifier.getSymbol()).addParentTypeSymbol((IInterfaceTypeSymbol)typeSymbol);
				}
			})+
		) 
	|	'extends'
	;
	
interfaceBody
	:	^(INTERFACE_BODY interfaceBodyDefinition*)
	|	INTERFACE_BODY
	;
	
interfaceBodyDefinition
	:	constDeclarationList
	|	methodDeclaration
	|	constructDeclaration
	;	

instructions[boolean shallCheckIfReturns] returns[EReturnState returnState]
@init{boolean isBreaking = false;}
	:	(	instruction[$shallCheckIfReturns]
			{
			    if(shallCheckIfReturns && $returnState != EReturnState.IsReturning){
			        if(!isBreaking){
			            isBreaking = $instruction.isBreaking;
			            $returnState = controller.evaluateReturnStateOr($returnState, $instruction.returnState);
			        }
			    }
			}
		)+
		{
		    if(isBreaking){
		        $returnState = EReturnState.IsNotReturning;
		    }
		}
	;

instruction[boolean shallCheckIfReturns] returns[EReturnState returnState, boolean isBreaking]
	:	variableDeclarationList 		{$returnState = EReturnState.IsNotReturning;}
	|	ifCondition[$shallCheckIfReturns]	{$returnState = $ifCondition.returnState;}
	|	switchCondition[$shallCheckIfReturns]	{$returnState = $switchCondition.returnState;}
	|	forLoop					{$returnState = EReturnState.IsNotReturning;}
	|	foreachLoop 				{$returnState = EReturnState.IsNotReturning;}
	|	whileLoop 				{$returnState = EReturnState.IsNotReturning;}
	|	doWhileLoop[$shallCheckIfReturns]	{$returnState = $doWhileLoop.returnState;}
	|	tryCatch[$shallCheckIfReturns]		{$returnState = $tryCatch.returnState;}
	|	^(EXPRESSION expression?)		{$returnState = EReturnState.IsNotReturning;}
	|	^('return' expression?) 		{$returnState = EReturnState.IsReturning;}
	|	^('throw' expression)			{$returnState = EReturnState.IsReturning;}
	|	^('echo' expression+)			{$returnState = EReturnState.IsNotReturning;}
	|	breakContinue				{$isBreaking = true;}
	;
	
ifCondition[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^('if' 
			expression 
			ifBlock=blockConditional[$shallCheckIfReturns]
			(elseBlock=blockConditional[$shallCheckIfReturns])?
		)
		{
		    if(shallCheckIfReturns){
		        if($elseBlock.returnState == null || $elseBlock.returnState == EReturnState.IsNotReturning){
		            $returnState = $ifBlock.returnState != EReturnState.IsNotReturning ? EReturnState.IsPartiallyReturning : EReturnState.IsNotReturning;
		        } else if($elseBlock.returnState == EReturnState.IsPartiallyReturning){
		            $returnState = EReturnState.IsPartiallyReturning;
		        } else {
		            $returnState = $ifBlock.returnState == EReturnState.IsReturning ? EReturnState.IsReturning : EReturnState.IsPartiallyReturning;
		        }
		    }
		}
	;

blockConditional[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^(BLOCK_CONDITIONAL instructions[$shallCheckIfReturns]) {$returnState = $instructions.returnState;}
	|	BLOCK_CONDITIONAL {$returnState = EReturnState.IsNotReturning;}
	;
	
switchCondition[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^('switch' expression switchContents[$shallCheckIfReturns]?) 
		{$returnState = $switchContents.hasDefault ? $switchContents.returnState : EReturnState.IsNotReturning;}
	;
	
switchContents[boolean shallCheckIfReturns] returns[EReturnState returnState, boolean hasDefault]
	:	(	^(SWITCH_CASES caseLabels) blockConditional[$shallCheckIfReturns]
			{
			    if(shallCheckIfReturns){
			    	$hasDefault = $hasDefault || $caseLabels.hasDefault;
			    	$returnState = controller.evaluateReturnStateAnd($returnState,$blockConditional.returnState);		    	
			    }
			}
		)+
	;

caseLabels returns[boolean hasDefault]
	: 	(	expression 	
		|	Default	{$hasDefault=true;}
		)+
	;
	
forLoop
	:	^('for' 
			(init=variableDeclarationList|init=expressionList)
			expressionList 
			expressionList
			blockConditional[false]
		)
	;

expressionList
	:	^(EXPRESSION_LIST expression*)
	|	EXPRESSION_LIST
	;

foreachLoop
	:
		^('foreach' 
			expression 
			variableDeclarationList
			// corresponding to the parser the first variableDeclarationList (the key) should be option
			// however, it does not matter here since both are just variable declarations
			variableDeclarationList? 
			blockConditional[false]
		)		
	;

whileLoop
	:	^('while' expression blockConditional[false])
	;


doWhileLoop[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^('do' block[$shallCheckIfReturns] expression)
		{$returnState = $block.returnState;}
	;

tryCatch[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^('try' blockConditional[$shallCheckIfReturns] catchBlocks[$shallCheckIfReturns]) 
		{
		    if(shallCheckIfReturns){
		        $returnState = controller.evaluateReturnStateAnd($blockConditional.returnState, $catchBlocks.returnState);		    	
		    }
		}
	;
catchBlocks[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	(	catchBlock[$shallCheckIfReturns]
			{
			    if(shallCheckIfReturns){
			        $returnState = controller.evaluateReturnStateAnd($returnState, $catchBlock.returnState);		    	
			    }
			}
		)+ 
	;

catchBlock[boolean shallCheckIfReturns] returns[EReturnState returnState]
	:	^('catch' variableDeclarationList blockConditional[$shallCheckIfReturns])
		{$returnState = $blockConditional.returnState;}
	;	
	
breakContinue
	:	(	nil=Break
		|	^(nil=Break level=Int)
		|	nil=Continue
		|	^(nil=Continue level=Int)
		)
		{controller.checkBreakContinueLevel($nil, $level);}
	;
	
expression
	:   	atom	
	|	operator
 	|	functionCall
	|	methodCall 		
	|	methodCallStatic 	
	|	classStaticAccess
	|	postFixExpression
	|	exit
    	;
    	
atom	:	primitiveAtomWithConstant
	|	variable
	|	thisVariable
	;

variable	
	: 	varId=VariableId
		{
      			$varId.setSymbol(controller.resolveVariable($varId));
			controller.checkIsForwardReference($varId);
			controller.checkIsOutOfConditionalScope($varId);
      		}
	;
	
thisVariable
	:	t='$this'	
		{$t.setSymbol(controller.resolveThisSelf($t));}
	;

operator
		//includes clone
 	:	^(unaryOperator expression)
	|	^(binaryOperator expression expression)
	|	^('?' expression expression expression)
	|	^(CASTING ^(TYPE variableModifier allTypes[$variableModifier.isNullable]) expression) 
	|	^(Instanceof expr=expression (variable|classInterfaceType))  
	|	^('new' classInterfaceType actualParameters)
	|	^('clone' expression)	
    	;
    	
unaryOperator
	:	PRE_INCREMENT
    	|	PRE_DECREMENT
    	|	'@'
    	|	'~'
    	|	'!'
    	|	UNARY_MINUS
    	|	UNARY_PLUS
    	|	POST_INCREMENT
    	|	POST_DECREMENT
	;
	
binaryOperator
	:	'or' 
	|	'xor' 
	|	'and' 
	
	|	'=' 
	|	'+='
	|	'-='
	|	'*='
	|	'/='
	|	'&='
	|	'|='
	|	'^='
	|	'%='
	|	'.='
	|	'<<=' 
	|	'>>=' 
	|	CASTING_ASSIGN
	
	|	'||' 
	|	'&&' 
	|	'|' 
	|	'^' 
	|	'&' 
	
	|	'==' 			
	|	'!=' 
	|	'<>' 
	|	'==='
	|	'!=='
	
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
	;

actualParameters
	:	^(ACTUAL_PARAMETERS expression+)
	|	ACTUAL_PARAMETERS
	;
	
functionCall
	    	// function call has no callee and is therefor not resolved in this phase. 
	    	// resolving occurs in the type checking phase where overloads are taken into account
	:	^(FUNCTION_CALL	identifier=TYPE_NAME actualParameters)
	;
	
methodCall
	:	^(METHOD_CALL methodCallee Identifier actualParameters
		)	
	;
	
methodCallee
	:	thisVariable
	|	variable
	|	slf='self'
		{$slf.setSymbol(controller.resolveThisSelf($slf));}
	|	par='parent'
		{$par.setSymbol(controller.resolveParent($par));}	
	;
	
methodCallStatic
	:	^(METHOD_CALL_STATIC classInterfaceType Identifier actualParameters)	
	;
	
classStaticAccess
	:	^(CLASS_STATIC_ACCESS accessor=staticAccessor identifier=CLASS_STATIC_ACCESS_VARIABLE_ID)
		{$identifier.setSymbol(controller.resolveStaticMember($accessor.start, $identifier));}
	;		

postFixExpression
// postFixExpression are resolved in the type checking phase
// due to the fact that method/function calls are resolved during the type check phase
// This rules are needed to get variables/function calls etc. in expression and actualParameters
	:	^(CLASS_MEMBER_ACCESS expression Identifier) 			
	|	^(ARRAY_ACCESS expression expression)		
	|	^(METHOD_CALL_POSTFIX expression Identifier actualParameters)
	;

exit
	:	^('exit' expression?)
	|	'exit'
	;
 	
returnTypes[boolean isNullable] returns [ITypeSymbol type]
	:	allTypes[isNullable] {$type = $allTypes.type;}
	|	voidType {$type = $voidType.type;}
	;
 	
voidType returns [ITypeSymbol type]
@init{
    if(state.backtracking == 1 && $start instanceof ITSPHPErrorAst){
        $type = controller.createErroneousTypeSymbol((ITSPHPErrorAst)$start);
        input.consume();
        return retval;
    }
}
 	:	'void'
		{
			$type = controller.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
 	; 	
 	
allTypes[boolean isNullable] returns [ITypeSymbol type]

	:	scalarTypes[isNullable] {$type = $scalarTypes.type;}
	|	classInterfaceType {$type = $classInterfaceType.type;}
	|	arrayOrResourceOrObject {$type = $arrayOrResourceOrObject.type;}
	;
	
scalarTypes[boolean isNullable] returns [ITypeSymbol type]
@init{
    if(state.backtracking == 1 && $start instanceof ITSPHPErrorAst){
        $type = controller.createErroneousTypeSymbol((ITSPHPErrorAst)$start);
        input.consume();
        return retval;
    }
}
	:	(	'bool'
		|	'int'
		|	'float'
		|	'string'
		)
		{
			//const are never nullable -> one can use the const null to represent null
			$type = controller.resolveScalarType($start, isNullable);
			$start.setSymbol($type);
		}
	;
	
classInterfaceType returns [ITypeSymbol type]
@init{
    if(state.backtracking == 1 && $start instanceof ITSPHPErrorAst){
        $type = controller.createErroneousTypeSymbol((ITSPHPErrorAst)$start);
        input.consume();
        return retval;
    }
}
	:	TYPE_NAME
		{
			$type = controller.resolveType($start);
			$start.setSymbol($type);
		}
	;
	
arrayOrResourceOrObject returns [ITypeSymbol type]
@init{
    if(state.backtracking == 1 && $start instanceof ITSPHPErrorAst){
        $type = controller.createErroneousTypeSymbol((ITSPHPErrorAst)$start);
        input.consume();
        return retval;
    }
}
	:	(	'array'
		|	'object'
		|	'resource'
		)
		{
			$type = controller.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
	;
