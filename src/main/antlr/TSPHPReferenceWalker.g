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
tree grammar TSPHPReferenceWalker;
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
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

}

@members {
ITypeCheckerController controller;

public TSPHPReferenceWalker(TreeNodeStream input, ITypeCheckerController theController) {
    this(input);
    controller = theController;
}
}

topdown
	:  	useDeclarationList
    	|	interfaceDeclaration
   	|	classDeclaration
   	|	constructDeclaration
   	|	methodFunctionDeclaration
   	|	constantDeclarationList
   	|	parameterDeclarationList
    	|	variableDeclarationList
    	// function call has no callee and is therefor not resolved in this phase. Resolving occurs in the type checking phase where overloads are taken into account
// 	|	functionCall
 	|	methodCallStatic
 	|	methodCall
 	|	classConstantStaticMember
 	|	casting
 	|	instanceofStatement
 	|	newOperator
    	|	atom
    	|	breakContinue
    	;

useDeclarationList
	:	^('use'	useDeclaration+)
	;
	
useDeclaration
	:	^(USE_DECLARATION typeName=TYPE_NAME alias=.)
		{
			ITypeSymbol typeSymbol = controller.resolveUseType($typeName, $alias);
			$typeName.setSymbol(typeSymbol);
			$alias.getSymbol().setType(typeSymbol);
			
			INamespaceScope namespaceScope = (INamespaceScope) $alias.getScope();
			namespaceScope.useDefinitionCheck((IAliasSymbol) $alias.getSymbol());
		}
	;

interfaceDeclaration
	:	^('interface' iMod=. identifier=Identifier extIds=interfaceExtendsDeclaration[$identifier] .)
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
				    ((IPolymorphicTypeSymbol)identifier.getSymbol()).addParentTypeSymbol((IPolymorphicTypeSymbol)typeSymbol);
				}
			})+
		) 
	|	'extends'
	;

	
classDeclaration
	:	^('class' cMod=. identifier=Identifier extId=classExtendsDeclaration[$identifier] implIds=implementsDeclaration[identifier] .) 
		{
			INamespaceScope namespaceScope = (INamespaceScope) $identifier.getScope();
			namespaceScope.doubleDefinitionCheckCaseInsensitive($identifier.getSymbol());
		}
	;

classExtendsDeclaration[ITSPHPAst identifier]
	:	^('extends' 
			allTypes[true]{
				ITypeSymbol typeSymbol = $allTypes.type;
				if(controller.checkIsClass($allTypes.start, typeSymbol)){
				    IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) identifier.getSymbol();
				    classTypeSymbol.setParent((IClassTypeSymbol)typeSymbol);
				    classTypeSymbol.addParentTypeSymbol((IClassTypeSymbol)typeSymbol);
				}
			} 
		)
	|	'extends'
	;

implementsDeclaration[ITSPHPAst identifier]
	:	^('implements' 	
			(allTypes[true]{
				ITypeSymbol typeSymbol = $allTypes.type;
				if(controller.checkIsInterface($allTypes.start, typeSymbol)){
				    ((IClassTypeSymbol)identifier.getSymbol()).addParentTypeSymbol((IInterfaceTypeSymbol)typeSymbol);
				}
			})+
		)
	|	'implements'
	;

constructDeclaration
	:	^(identifier='__construct' .  ^(TYPE rtMod=. voidType) . .)
		{
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($voidType.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
		}
	;
		
methodFunctionDeclaration
	:	^( 	(	METHOD_DECLARATION
			|	Function
			) 
			. ^(TYPE typeModifier returnTypes[$typeModifier.isNullable]) 
			identifier=. . .
		)
		{
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($returnTypes.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
		}
	;
typeModifier returns[boolean isNullable]
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
	

constantDeclarationList
	:	^(CONSTANT_DECLARATION_LIST ^(TYPE tMod=. scalarTypes[false]) constantDeclaration[$scalarTypes.type]+)
	;

constantDeclaration[ITypeSymbol type]
	:	^(identifier=Identifier .)
		{ 
			IVariableSymbol variableSymbol = (IVariableSymbol) $identifier.getSymbol();
			variableSymbol.setType(type); 
			$identifier.getScope().doubleDefinitionCheck(variableSymbol); 
		}
	;

parameterDeclarationList
	:	^(PARAMETER_LIST parameterDeclaration+)
	;

parameterDeclaration
	:	^(PARAMETER_DECLARATION 
			^(TYPE typeModifier allTypes[$typeModifier.isNullable]) 
			variableDeclaration[$allTypes.type]
		)
		{
		    IVariableSymbol parameter = $variableDeclaration.variableSymbol;
		    IMethodSymbol methodSymbol = (IMethodSymbol) parameter.getDefinitionScope();
		    methodSymbol.addParameter(parameter);
		}
	;

variableDeclarationList 
	:	^(VARIABLE_DECLARATION_LIST 
			^(TYPE typeModifier allTypes[$typeModifier.isNullable]) 
			variableDeclaration[$allTypes.type]+ 
		)
        ;
        
variableDeclaration[ITypeSymbol type] returns [IVariableSymbol variableSymbol]
	:
		(	^(variableId=VariableId .)
		|	variableId=VariableId	
		)
		{ 
			$variableSymbol = (IVariableSymbol) $variableId.getSymbol();
			$variableSymbol.setType(type); 
			$variableId.getScope().doubleDefinitionCheck($variableId.getSymbol());
		}
	;

methodCallStatic
	:	^(METHOD_CALL_STATIC callee=TYPE_NAME . .)	
		{$callee.setSymbol(controller.resolveType($callee));}
	;
	
methodCall
	:	^(METHOD_CALL callee=methodCallee . .)	
		{//callee's symbol is set in methodCallee
		}
	;
methodCallee
	:	t='$this'
		{$t.setSymbol(controller.resolveThisSelf($t));}
	
	|	varId=VariableId
		{
      			$varId.setSymbol(controller.resolveVariable($varId));
			controller.checkForwardReference($varId);
		}
	
	|	slf='self'
		{$slf.setSymbol(controller.resolveThisSelf($slf));}
		
	|	par='parent'
		{$par.setSymbol(controller.resolveParent($par));}
	;
	
classConstantStaticMember
	:	^(CLASS_STATIC_ACCESS 
			accessor=staticAccessor 
			(	identifier=CLASS_STATIC_ACCESS_VARIABLE_ID
				{$identifier.setSymbol(controller.resolveStaticMember($accessor.start, $identifier));}
			|	identifier=CONSTANT
				{$identifier.setSymbol(controller.resolveClassConstant($accessor.start, $identifier));}
			)
		)
		
	;

staticAccessor
@after{$start.setEvalType((ITypeSymbol) $start.getSymbol());}
	:	typeName=TYPE_NAME
		{$typeName.setSymbol(controller.resolveType($typeName));}

	|	slf='self'
		{$slf.setSymbol(controller.resolveThisSelf($slf).getType());}
		
	|	par='parent'
		{$par.setSymbol(controller.resolveParent($par).getType());}							
	;


returnTypes[boolean isNullable] returns [ITypeSymbol type]
	:	allTypes[isNullable] {$type = $allTypes.type;}
	|	voidType {$type = $voidType.type;}
	;

casting	:	^(CASTING ^(TYPE typeModifier allTypes[$typeModifier.isNullable]) .) 
	;
	
instanceofStatement
	:	^('instanceof' . identifier=TYPE_NAME)
		{$identifier.setSymbol(controller.resolveType($identifier));}
	//no need to do the same for VariableId since this symbol is resolved with rule variable
	//|	^('instanceof' . identifier=VariableId)
	;
	
newOperator
	:	^('new' identifier=TYPE_NAME .)
		{$identifier.setSymbol(controller.resolveType($identifier));}
	;
	
atom	:	
	|	thisVariable
	|	variable
 	|	constant
	;

variable	
@init { int tokenType = $start.getParent().getType();}
	: 	
		{
			tokenType!=VARIABLE_DECLARATION_LIST 
			&& tokenType!=PARAMETER_DECLARATION 
			&& tokenType!=METHOD_CALL
		}? varId=VariableId
		{
      			$varId.setSymbol(controller.resolveVariable($varId));
			controller.checkForwardReference($varId);
			controller.checkOutOfConditionalScope($varId);
      		}
	;
	
thisVariable
@init { int tokenType = $start.getParent().getType();}
	:	{ tokenType!=METHOD_CALL
		}? t='$this'	
		{$t.setSymbol(controller.resolveThisSelf($t));}
	;


constant
@init { int tokenType = $start.getParent().getType();}
	:	{ tokenType!=CLASS_STATIC_ACCESS
		}? cnst=CONSTANT
		{
			IVariableSymbol variableSymbol = controller.resolveConstant($cnst);
			$cnst.setSymbol(variableSymbol);
			controller.checkForwardReference($cnst);
		}
	;
	
voidType returns [ITypeSymbol type]
 	:	'void'
		{
			$type = controller.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
 	;
 	
allTypes[boolean isNullable] returns [ITypeSymbol type]
	:	scalarTypes[isNullable]
		{$type = $scalarTypes.type;}
	|	(	'array'
		|	'object'
		|	'resource'
		)
		{
			$type = controller.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
		
	|	TYPE_NAME
		{
			$type = controller.resolveType($start);
			$start.setSymbol($type);
		}
	;
	
scalarTypes[boolean isNullable] returns [ITypeSymbol type]
	:	(	'bool'
		|	'int'
		|	'float'
		|	'string'
		)
		{
			//const are never null -> one can use the const null to represent null
			$type = controller.resolveScalarType($start, isNullable);
			$start.setSymbol($type);
		}
	;

breakContinue
	:	(	nil=Break
		|	^(nil=Break level=Int)
		|	nil=Continue
		|	^(nil=Continue level=Int)
		)
		{controller.checkBreakContinueLevel($nil, $level);}
	;
