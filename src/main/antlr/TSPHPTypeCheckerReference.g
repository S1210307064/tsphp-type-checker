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
tree grammar TSPHPTypeCheckerReference;
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
package ch.tutteli.tsphp.typechecker.antlr;

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;

}

@members {
    ISymbolTable symbolTable;
    public TSPHPTypeCheckerReference(TreeNodeStream input, ISymbolTable theSymbolTable) {
        this(input);
        symbolTable = theSymbolTable;
    }
    
}

topdown
    :   useDeclarationList
    |	variableDeclarationList
    ;

useDeclarationList
	:	^('use'	useDeclaration+)
	;
	
useDeclaration
	:	(	type=TYPE_NAME 
		|	type=TYPE_NAME identifier=Identifier
		)
		{
			$type.symbol.setType(symbolTable.resolveType($start));
			INamespaceScope namespaceScope = (INamespaceScope) $type.scope;
			namespaceScope.useDefinitionCheck((IAliasSymbol) $type.symbol);
		}
	;

variableDeclarationList 
	:	^(VARIABLE_DECLARATION_LIST ^(TYPE . allTypes) variableDeclaration[$allTypes.type]+ )
        ;
        
variableDeclaration[ITypeSymbol type]
	:
		(	^(variableId=VariableId .)
		|	variableId=VariableId	
		)
		{ 
			$variableId.symbol.setType(type); 
			$variableId.scope.definitionCheck($variableId.symbol);
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
			$start.symbol = $type;
		}
	|	TYPE_NAME
		{
			$type = symbolTable.resolveType($start);
			$start.symbol = $type;
		}
	;

