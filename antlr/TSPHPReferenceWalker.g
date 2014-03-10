/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

tree grammar TSPHPReferenceWalker;
options {
	tokenVocab = TSPHP;
	ASTLabelType = ITSPHPAst;
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
import ch.tsphp.common.ITSPHPErrorAst;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.scopes.ICaseInsensitiveScope;
import ch.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.IVoidTypeSymbol;

}

@members {
private IReferencePhaseController controller;
private IAccessResolver accessResolver;
private boolean hasAtLeastOneReturnOrThrow;

public TSPHPReferenceWalker(TreeNodeStream input, IReferencePhaseController theController, IAccessResolver theAccessResolver) {
    this(input);
    controller = theController;
    accessResolver = theAccessResolver;
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
	:	useDefinitionList
	|	definition
	|	instruction[false]
	;
	
useDefinitionList
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
	:	classDefinition
	|	interfaceDefinition
	|	functionDefinition
	|	constDefinitionList
	;
	
classDefinition
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
	:	constDefinitionList
	|	classMemberDefinition
	|	constructDefinition
	|	methodDefinition
	;

constDefinitionList
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
			controller.checkIsNotForwardReference($cnst);
		}
	|	^(CLASS_STATIC_ACCESS accessor=staticAccessor identifier=CONSTANT)
		{$identifier.setSymbol(accessResolver.resolveClassConstantAccess($accessor.start, $identifier));}
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
	
classMemberDefinition
	:	^(CLASS_MEMBER variableDeclarationList[true])
	;
	
variableDeclarationList[boolean isImplicitlyInitialised] 
	:	^(VARIABLE_DECLARATION_LIST 
			^(TYPE variableModifier allTypes[$variableModifier.isNullable]) 
			variableDeclaration[$allTypes.type, isImplicitlyInitialised]+ 
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
        
variableDeclaration[ITypeSymbol type, boolean isImplicitlyInitialised] returns [IVariableSymbol variableSymbol]
@init{boolean isInitialised = false;}
	:	(	^(variableId=VariableId expression) {isInitialised = true;}
		|	variableId=VariableId
		)
		{ 
			//Warning! start duplicated code as in parameterNormalOrOptional
			$variableSymbol = (IVariableSymbol) $variableId.getSymbol();
			$variableSymbol.setType(type); 
			$variableId.getScope().doubleDefinitionCheck($variableId.getSymbol());
			//Warning! end duplicated code as in parameterNormalOrOptional
			if(isInitialised || isImplicitlyInitialised){
			    $variableId.getScope().addToInitialisedSymbols($variableSymbol, true);
			}
		}
	;
	
accessModifier
	:	Private
	|	Protected
	|	Public
	;

constructDefinition
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
		
methodDefinition
//Warning! start duplicated code as in functionDeclaration
	@init{
	    hasAtLeastOneReturnOrThrow = false;
	    boolean shallCheckIfReturns = false;
	}
//Warning! end duplicated code as in functionDeclaration

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
			    controller.checkReturnsFromMethod($block.isReturning, hasAtLeastOneReturnOrThrow, $identifier);
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
	
functionDefinition
//Warning! start duplicated code as in functionDeclaration
	@init{
	    hasAtLeastOneReturnOrThrow = false;
	    boolean shallCheckIfReturns = false;
	}
//Warning! start duplicated code as in functionDeclaration
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
			    controller.checkReturnsFromFunction($block.isReturning, hasAtLeastOneReturnOrThrow, $identifier);
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
			$variableId.getScope().addToInitialisedSymbols($variableSymbol, true);
		} 
	;
	
block[boolean shallCheckIfReturns] returns[boolean isReturning]
	:	^(BLOCK instructions[$shallCheckIfReturns]) {$isReturning = $instructions.isReturning;}
	|	BLOCK {$isReturning = false;}
	;

interfaceDefinition
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
	:	constDefinitionList
	|	methodDefinition
	|	constructDefinition
	;	

instructions[boolean shallCheckIfReturns] returns[boolean isReturning]
@init{boolean isBreaking = false;}
	:	(	instruction[$shallCheckIfReturns]
			{
			    if(shallCheckIfReturns){
			        $isReturning = $isReturning || (!isBreaking && $instruction.isReturning);
			        isBreaking = $instruction.isBreaking;
			    }
			}
		)+
	;

instruction[boolean shallCheckIfReturns] returns[boolean isReturning, boolean isBreaking]
	// those statement which do not have an isReturning block can never return. 
	// Yet, it might be that they contain a return or throw statement and thus hasAtLeastOneReturnOrThrow has been set to true
	:	variableDeclarationList[false] 		
	|	ifCondition[$shallCheckIfReturns]	{$isReturning = $ifCondition.isReturning;}
	|	switchCondition[$shallCheckIfReturns]	{$isReturning = $switchCondition.isReturning;}
	|	forLoop					
	|	foreachLoop
	|	whileLoop
	|	doWhileLoop[$shallCheckIfReturns]	{$isReturning = $doWhileLoop.isReturning;}
	|	tryCatch[$shallCheckIfReturns]		{$isReturning = $tryCatch.isReturning;}
	|	^(EXPRESSION expression?)
	|	^('return' expression?) 		{$isReturning = true; hasAtLeastOneReturnOrThrow = true;}
	|	^('throw' expression)			{$isReturning = true; hasAtLeastOneReturnOrThrow = true;}
	|	^('echo' expression+)
	|	breakContinue				{$isBreaking = true;}
	;
	
ifCondition[boolean shallCheckIfReturns] returns[boolean isReturning]
	:	^('if' 
			expression 
			ifBlock=blockConditional[$shallCheckIfReturns]
			(elseBlock=blockConditional[$shallCheckIfReturns])?
		)
		{
		    $isReturning = shallCheckIfReturns && $ifBlock.isReturning && $elseBlock.isReturning;
		    controller.sendUpInitialisedSymbolsAfterIf($ifBlock.ast, $elseBlock.ast);
		}
	;

blockConditional[boolean shallCheckIfReturns] returns[boolean isReturning, ITSPHPAst ast]
	:	^(BLOCK_CONDITIONAL instructions[$shallCheckIfReturns]) 
		{
		    $isReturning = $instructions.isReturning; 
		    $ast = $BLOCK_CONDITIONAL;
		}
		
	|	BLOCK_CONDITIONAL 
		{
		    $isReturning = false; 
		    $ast = $BLOCK_CONDITIONAL;
		}
	;
	
switchCondition[boolean shallCheckIfReturns] returns[boolean isReturning]
	:	^('switch' expression switchContents[$shallCheckIfReturns]?) 
		{
		    $isReturning = $switchContents.hasDefault && $switchContents.isReturning;
		}
	;
	
switchContents[boolean shallCheckIfReturns] returns[boolean isReturning, boolean hasDefault]
//Warning! start duplicated code as in catchBlocks
@init{
    boolean isFirst = true;
    List<ITSPHPAst> asts = new ArrayList<>();
}
//Warning! start duplicated code as in catchBlocks
	:	(	^(SWITCH_CASES caseLabels) blockConditional[$shallCheckIfReturns]
			{
			    if(shallCheckIfReturns){
			    	$hasDefault = $hasDefault || $caseLabels.hasDefault;
			    	$isReturning = $blockConditional.isReturning && ($isReturning || isFirst);		
			    	isFirst = false;    	
			    }
			    asts.add($blockConditional.ast);
			}
		)+
		{controller.sendUpInitialisedSymbolsAfterSwitch(asts, $hasDefault);}
	;

caseLabels returns[boolean hasDefault]
	: 	(	expression 	
		|	Default	{$hasDefault=true;}
		)+
	;
	
forLoop
	:	^('for' 
			(init=variableDeclarationList[false]|init=expressionList)
			expressionList 
			expressionList
			blockConditional[false]
		)
		{controller.sendUpInitialisedSymbols($blockConditional.ast);}
	;

expressionList
	:	^(EXPRESSION_LIST expression*)
	|	EXPRESSION_LIST
	;

foreachLoop
	:
		^(foreach='foreach' 
			expression 
			variableDeclarationList[true]
			// corresponding to the parser the first variableDeclarationList (the key) should be optional
			// however, it does not matter here since both are just variable declarations
			variableDeclarationList[true]? 
			blockConditional[false]
		)     		
		{
		    controller.sendUpInitialisedSymbols($blockConditional.ast);
  		    controller.sendUpInitialisedSymbols($foreach);
		}
	;

whileLoop
	:	^('while' expression blockConditional[false])
		{controller.sendUpInitialisedSymbols($blockConditional.ast);}
	;


doWhileLoop[boolean shallCheckIfReturns] returns[boolean isReturning]
	:	^('do' block[$shallCheckIfReturns] expression)
		{$isReturning = $block.isReturning;}
	;

tryCatch[boolean shallCheckIfReturns] returns[boolean isReturning]
	:	^('try' blockConditional[$shallCheckIfReturns] catchBlocks[$shallCheckIfReturns]) 
		{
		    $isReturning = shallCheckIfReturns && $blockConditional.isReturning && $catchBlocks.isReturning;
		    $catchBlocks.asts.add($blockConditional.ast);
		    controller.sendUpInitialisedSymbolsAfterTryCatch($catchBlocks.asts);
		}
	;
catchBlocks[boolean shallCheckIfReturns] returns[boolean isReturning,List<ITSPHPAst> asts]
//Warning! start duplicated code as in catchBlocks
@init{
    boolean isFirst = true;
    $asts = new ArrayList<>();
}
//Warning! start duplicated code as in catchBlocks
	:	(	^('catch' variableDeclarationList[true] blockConditional[$shallCheckIfReturns])
			{
			    if(shallCheckIfReturns){
			        $isReturning = $blockConditional.isReturning && ($isReturning || isFirst);
			        isFirst = false;
			    }
			    $asts.add($blockConditional.ast);
			}
		)+ 
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
      			controller.checkVariableIsOkToUse($varId);
      		}
	;
	
thisVariable
	:	t='$this'	
		{$t.setSymbol(controller.resolveThisSelf($t));}
	;

operator
 	:	^(unaryOperator expression)
	|	^(binaryOperatorExcludingAssign expression expression)
	|	^(assignOperator varId=expression expression)
		{
		    ITSPHPAst variableId = $varId.start;
		    if(variableId.getType()==VariableId){
		        variableId.getScope().addToInitialisedSymbols(variableId.getSymbol(), true);
		    }
		}
	|	^('?' expression expression expression)
	|	^(CAST ^(TYPE variableModifier allTypes[$variableModifier.isNullable]) expression) 
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
	
binaryOperatorExcludingAssign
	:	'or' 
	|	'xor' 
	|	'and' 
	
	|	'||' 
	|	'&&' 
	|	'|' 
	|	'^' 
	|	'&' 
	
	|	'==' 			
	|	'!=' 
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
	
assignOperator
	:	(	'=' 
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
		|	CAST_ASSIGN	
		)
		{
		}
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
		{$identifier.setSymbol(accessResolver.resolveStaticMemberAccess($accessor.start, $identifier));}
	;		

postFixExpression
// postFixExpression are resolved in the type checking phase
// due to the fact that method/function calls are resolved during the type check phase
// This rules are needed to resolve variables/function calls etc. in expression and actualParameters
	:	^(CLASS_MEMBER_ACCESS expression Identifier) 			
	|	^(ARRAY_ACCESS expression expression)		
	|	^(METHOD_CALL_POSTFIX expression Identifier actualParameters)
	;

exit
	:	^('exit' expression)
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
