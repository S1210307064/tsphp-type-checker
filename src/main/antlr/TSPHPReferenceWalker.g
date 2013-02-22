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
import ch.tutteli.tsphp.typechecker.ISymbolTable;
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
ISymbolTable symbolTable;

public TSPHPReferenceWalker(TreeNodeStream input, ISymbolTable theSymbolTable) {
    this(input);
    symbolTable = theSymbolTable;
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
 	|	functionCall
 	|	methodCallStatic
 	|	methodCall
 	|	classConstantStaticMember
 	|	casting
 	|	instanceofStatement
 	|	newOperator
    	|	atom
    	;

useDeclarationList
	:	^('use'	useDeclaration+)
	;
	
useDeclaration
	:	^(USE_DECLARATION typeName=TYPE_NAME alias=.)
		{
			ITypeSymbol typeSymbol = symbolTable.resolveUseType($typeName, $alias);
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
			(allTypes {	
				ITypeSymbol typeSymbol = $allTypes.type;
				if(symbolTable.checkIfInterface($allTypes.start, typeSymbol)){
				    ((IPolymorphicTypeSymbol)identifier.getSymbol()).setParent((IPolymorphicTypeSymbol)typeSymbol);
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
			allTypes {
				ITypeSymbol typeSymbol = $allTypes.type;
				if(symbolTable.checkIfClass($allTypes.start, typeSymbol)){
				    ((IClassTypeSymbol)identifier.getSymbol()).setParent((IPolymorphicTypeSymbol)typeSymbol);
				}
			} 
		)
	|	'extends'
	;

implementsDeclaration[ITSPHPAst identifier]
	:	^('implements' 	
			(allTypes{
				ITypeSymbol typeSymbol = $allTypes.type;
				if(symbolTable.checkIfInterface($allTypes.start, typeSymbol)){
				    ((IClassTypeSymbol)identifier.getSymbol()).addInterface((IInterfaceTypeSymbol)typeSymbol);
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
			. ^(TYPE rtMod=. returnTypes) identifier=. . .
		)
		{
			IMethodSymbol methodSymbol = (IMethodSymbol) $identifier.getSymbol();
			methodSymbol.setType($returnTypes.type); 
			ICaseInsensitiveScope scope = (ICaseInsensitiveScope) $identifier.getScope();
			scope.doubleDefinitionCheckCaseInsensitive(methodSymbol);
		}
	;
	
constantDeclarationList
	:	^(CONSTANT_DECLARATION_LIST ^(TYPE tMod=. scalarTypes) constantDeclaration[$scalarTypes.type]+)
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
			^(TYPE tMod=. allTypes) variableDeclaration[$allTypes.type]
		)
		{
		    IVariableSymbol parameter = $variableDeclaration.variableSymbol;
		    IMethodSymbol methodSymbol = (IMethodSymbol) parameter.getDefinitionScope();
		    methodSymbol.addParameter(parameter);
		}
	;

variableDeclarationList 
	:	^(VARIABLE_DECLARATION_LIST ^(TYPE tMod=. allTypes) variableDeclaration[$allTypes.type]+ )
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


functionCall
	:	^(FUNCTION_CALL	identifier=TYPE_NAME .)
		{
			IMethodSymbol methodSymbol = symbolTable.resolveFunction($identifier);
			$identifier.setSymbol(methodSymbol);
		}
	;

methodCallStatic
	:	^(METHOD_CALL_STATIC callee=TYPE_NAME identifier=Identifier .)	
		{
			ITypeSymbol typeSymbol = symbolTable.resolveType($callee);
			$callee.setSymbol(typeSymbol);
			$identifier.setSymbol(symbolTable.resolveStaticMethod($callee, $identifier));
		}
	;
	
methodCall
	:	^(METHOD_CALL callee=methodCallee identifier=Identifier .)	
		{
			//callee's symbol is set in methodCallee
			$identifier.setSymbol( symbolTable.resolveMethod($callee.start, $identifier));
		}
	;
methodCallee
	:	t='$this'
		{$t.setSymbol(symbolTable.resolveThisSelf($t));}
	
	|	varId=VariableId
		{
      			$varId.setSymbol(symbolTable.resolveVariable($varId));
			symbolTable.checkForwardReference($varId);
		}
	
	|	slf='self'
		{$slf.setSymbol(symbolTable.resolveThisSelf($slf));}
		
	|	par='parent'
		{$par.setSymbol(symbolTable.resolveParent($par));}
	;
	
classConstantStaticMember
	:	^(CLASS_STATIC_ACCESS accessor=staticAccessor identifier=(CLASS_STATIC_ACCESS_VARIABLE_ID|CONSTANT))
		{$identifier.setSymbol(symbolTable.resolveStaticMemberOrClassConstant($accessor.start, $identifier));}
	;

staticAccessor
	:	typeName=TYPE_NAME
		{$typeName.setSymbol(symbolTable.resolveType($typeName));}

	|	slf='self'
		{$slf.setSymbol(symbolTable.resolveThisSelf($slf).getType());}
		
	|	par='parent'
		{$par.setSymbol(symbolTable.resolveParent($par).getType());}							
	;


returnTypes returns [ITypeSymbol type]
	:	allTypes {$type = $allTypes.type;}
	|	voidType {$type = $voidType.type;}
	;

casting	:	^(CASTING ^(TYPE . type=allTypes) .) 
	;
	
instanceofStatement
	:	(	^('instanceof' . identifier=VariableId)
		|	^('instanceof' . identifier=TYPE_NAME)
		)
		{$identifier.setSymbol(symbolTable.resolveType($identifier));}
	;
	
newOperator
	:	^('new' identifier=TYPE_NAME .)
		{$identifier.setSymbol(symbolTable.resolveType($identifier));}
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
      			$varId.setSymbol(symbolTable.resolveVariable($varId));
			symbolTable.checkForwardReference($varId);
      		}
	;
	
thisVariable
@init { int tokenType = $start.getParent().getType();}
	:	{ tokenType!=METHOD_CALL
		}? t='$this'	
		{$t.setSymbol(symbolTable.resolveThisSelf($t));}
	;


constant
@init { int tokenType = $start.getParent().getType();}
	:	{ tokenType!=CLASS_STATIC_ACCESS
		}? cnst=CONSTANT
		{
			IVariableSymbol variableSymbol = symbolTable.resolveConstant($cnst);
			$cnst.setSymbol(variableSymbol);
			symbolTable.checkForwardReference($cnst);
		}
	;
	
voidType returns [ITypeSymbol type]
 	:	'void'
		{
			$type = symbolTable.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
 	;
 	
allTypes returns [ITypeSymbol type]
	:	(	'bool'
		|	'int'
		|	'float'
		|	'string'
		|	'array'
		|	'object'
		|	'resource'
		)
		{
			$type = symbolTable.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
		
	|	TYPE_NAME
		{
			$type = symbolTable.resolveType($start);
			$start.setSymbol($type);
		}
	;
	
scalarTypes returns [ITypeSymbol type]
	:	(	'bool'
		|	'int'
		|	'float'
		|	'string'
		)
		{
			$type = symbolTable.resolvePrimitiveType($start);
			$start.setSymbol($type);
		}
	;
