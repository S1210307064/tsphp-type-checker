// $ANTLR 3.x D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g 2013-02-12 11:55:09

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



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class TSPHPTypeCheckerReference extends TreeFilter {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTUAL_PARAMETERS", "ARRAY_ACCESS", "Abstract", "Arrow", "As", "Assign", "At", "BINARY", "BLOCK", "BLOCK_CONDITIONAL", "Backslash", "BitwiseAnd", "BitwiseAndAssign", "BitwiseNot", "BitwiseOr", "BitwiseOrAssign", "BitwiseXor", "BitwiseXorAssign", "Bool", "Break", "CASTING", "CASTING_ASSIGN", "CLASS_BODY", "CLASS_MEMBER", "CLASS_MEMBER_ACCESS", "CLASS_MEMBER_MODIFIER", "CLASS_MODIFIER", "CLASS_STATIC_ACCESS", "CONSTANT", "CONSTANT_DECLARATION", "CONSTANT_DECLARATION_LIST", "Case", "Cast", "Catch", "Class", "Clone", "Colon", "Comma", "Comment", "Const", "Construct", "Continue", "DECIMAL", "DEFAULT_NAMESPACE", "Default", "Destruct", "Divide", "DivideAssign", "Do", "Dolar", "Dot", "DotAssign", "DoubleColon", "EXCEPTION_LIST", "EXPONENT", "EXPRESSION", "EXPRESSION_LIST", "Echo", "Else", "Equal", "Exit", "Extends", "FUNCTION_CALL", "FUNCTION_MODIFIER", "Final", "Float", "For", "Foreach", "Function", "GreaterEqualThan", "GreaterThan", "HEXADECIMAL", "INTERFACE_BODY", "Identical", "Identifier", "If", "Implements", "Instanceof", "Int", "Interface", "LeftCurlyBrace", "LeftParanthesis", "LeftSquareBrace", "LessEqualThan", "LessThan", "LogicAnd", "LogicAndWeak", "LogicNot", "LogicOr", "LogicOrWeak", "LogicXorWeak", "METHOD_CALL", "METHOD_CALL_STATIC", "METHOD_DECLARATION", "METHOD_MODIFIER", "Minus", "MinusAssign", "MinusMinus", "Modulo", "ModuloAssign", "Multiply", "MultiplyAssign", "NAMESPACE_BODY", "Namespace", "New", "NotEqual", "NotEqualAlternative", "NotIdentical", "Null", "OCTAL", "ObjectOperator", "PARAMETER_DECLARATION", "PARAMETER_LIST", "PARAMETER_TYPE", "POST_DECREMENT", "POST_INCREMENT", "PRE_DECREMENT", "PRE_INCREMENT", "Parent", "ParentColonColon", "Plus", "PlusAssign", "PlusPlus", "Private", "ProtectThis", "Protected", "Public", "QuestionMark", "Return", "RightCurlyBrace", "RightParanthesis", "RightSquareBrace", "STRING_DOUBLE_QUOTED", "STRING_SINGLE_QUOTED", "SWITCH_CASES", "Self", "SelfColonColon", "Semicolon", "ShiftLeft", "ShiftLeftAssign", "ShiftRight", "ShiftRightAssign", "Static", "String", "Switch", "TYPE", "TYPE_MODIFIER", "TYPE_NAME", "This", "Throw", "Try", "TypeAliasBool", "TypeAliasFloat", "TypeAliasInt", "TypeArray", "TypeBool", "TypeFloat", "TypeInt", "TypeObject", "TypeResource", "TypeString", "UNARY_MINUS", "USE_DECLRATARION", "Use", "VARIABLE_DECLARATION", "VARIABLE_DECLARATION_LIST", "VariableId", "Void", "While", "Whitespace"
	};

	public static final int EOF=-1;
	public static final int ACTUAL_PARAMETERS=4;
	public static final int ARRAY_ACCESS=5;
	public static final int Abstract=6;
	public static final int Arrow=7;
	public static final int As=8;
	public static final int Assign=9;
	public static final int At=10;
	public static final int BINARY=11;
	public static final int BLOCK=12;
	public static final int BLOCK_CONDITIONAL=13;
	public static final int Backslash=14;
	public static final int BitwiseAnd=15;
	public static final int BitwiseAndAssign=16;
	public static final int BitwiseNot=17;
	public static final int BitwiseOr=18;
	public static final int BitwiseOrAssign=19;
	public static final int BitwiseXor=20;
	public static final int BitwiseXorAssign=21;
	public static final int Bool=22;
	public static final int Break=23;
	public static final int CASTING=24;
	public static final int CASTING_ASSIGN=25;
	public static final int CLASS_BODY=26;
	public static final int CLASS_MEMBER=27;
	public static final int CLASS_MEMBER_ACCESS=28;
	public static final int CLASS_MEMBER_MODIFIER=29;
	public static final int CLASS_MODIFIER=30;
	public static final int CLASS_STATIC_ACCESS=31;
	public static final int CONSTANT=32;
	public static final int CONSTANT_DECLARATION=33;
	public static final int CONSTANT_DECLARATION_LIST=34;
	public static final int Case=35;
	public static final int Cast=36;
	public static final int Catch=37;
	public static final int Class=38;
	public static final int Clone=39;
	public static final int Colon=40;
	public static final int Comma=41;
	public static final int Comment=42;
	public static final int Const=43;
	public static final int Construct=44;
	public static final int Continue=45;
	public static final int DECIMAL=46;
	public static final int DEFAULT_NAMESPACE=47;
	public static final int Default=48;
	public static final int Destruct=49;
	public static final int Divide=50;
	public static final int DivideAssign=51;
	public static final int Do=52;
	public static final int Dolar=53;
	public static final int Dot=54;
	public static final int DotAssign=55;
	public static final int DoubleColon=56;
	public static final int EXCEPTION_LIST=57;
	public static final int EXPONENT=58;
	public static final int EXPRESSION=59;
	public static final int EXPRESSION_LIST=60;
	public static final int Echo=61;
	public static final int Else=62;
	public static final int Equal=63;
	public static final int Exit=64;
	public static final int Extends=65;
	public static final int FUNCTION_CALL=66;
	public static final int FUNCTION_MODIFIER=67;
	public static final int Final=68;
	public static final int Float=69;
	public static final int For=70;
	public static final int Foreach=71;
	public static final int Function=72;
	public static final int GreaterEqualThan=73;
	public static final int GreaterThan=74;
	public static final int HEXADECIMAL=75;
	public static final int INTERFACE_BODY=76;
	public static final int Identical=77;
	public static final int Identifier=78;
	public static final int If=79;
	public static final int Implements=80;
	public static final int Instanceof=81;
	public static final int Int=82;
	public static final int Interface=83;
	public static final int LeftCurlyBrace=84;
	public static final int LeftParanthesis=85;
	public static final int LeftSquareBrace=86;
	public static final int LessEqualThan=87;
	public static final int LessThan=88;
	public static final int LogicAnd=89;
	public static final int LogicAndWeak=90;
	public static final int LogicNot=91;
	public static final int LogicOr=92;
	public static final int LogicOrWeak=93;
	public static final int LogicXorWeak=94;
	public static final int METHOD_CALL=95;
	public static final int METHOD_CALL_STATIC=96;
	public static final int METHOD_DECLARATION=97;
	public static final int METHOD_MODIFIER=98;
	public static final int Minus=99;
	public static final int MinusAssign=100;
	public static final int MinusMinus=101;
	public static final int Modulo=102;
	public static final int ModuloAssign=103;
	public static final int Multiply=104;
	public static final int MultiplyAssign=105;
	public static final int NAMESPACE_BODY=106;
	public static final int Namespace=107;
	public static final int New=108;
	public static final int NotEqual=109;
	public static final int NotEqualAlternative=110;
	public static final int NotIdentical=111;
	public static final int Null=112;
	public static final int OCTAL=113;
	public static final int ObjectOperator=114;
	public static final int PARAMETER_DECLARATION=115;
	public static final int PARAMETER_LIST=116;
	public static final int PARAMETER_TYPE=117;
	public static final int POST_DECREMENT=118;
	public static final int POST_INCREMENT=119;
	public static final int PRE_DECREMENT=120;
	public static final int PRE_INCREMENT=121;
	public static final int Parent=122;
	public static final int ParentColonColon=123;
	public static final int Plus=124;
	public static final int PlusAssign=125;
	public static final int PlusPlus=126;
	public static final int Private=127;
	public static final int ProtectThis=128;
	public static final int Protected=129;
	public static final int Public=130;
	public static final int QuestionMark=131;
	public static final int Return=132;
	public static final int RightCurlyBrace=133;
	public static final int RightParanthesis=134;
	public static final int RightSquareBrace=135;
	public static final int STRING_DOUBLE_QUOTED=136;
	public static final int STRING_SINGLE_QUOTED=137;
	public static final int SWITCH_CASES=138;
	public static final int Self=139;
	public static final int SelfColonColon=140;
	public static final int Semicolon=141;
	public static final int ShiftLeft=142;
	public static final int ShiftLeftAssign=143;
	public static final int ShiftRight=144;
	public static final int ShiftRightAssign=145;
	public static final int Static=146;
	public static final int String=147;
	public static final int Switch=148;
	public static final int TYPE=149;
	public static final int TYPE_MODIFIER=150;
	public static final int TYPE_NAME=151;
	public static final int This=152;
	public static final int Throw=153;
	public static final int Try=154;
	public static final int TypeAliasBool=155;
	public static final int TypeAliasFloat=156;
	public static final int TypeAliasInt=157;
	public static final int TypeArray=158;
	public static final int TypeBool=159;
	public static final int TypeFloat=160;
	public static final int TypeInt=161;
	public static final int TypeObject=162;
	public static final int TypeResource=163;
	public static final int TypeString=164;
	public static final int UNARY_MINUS=165;
	public static final int USE_DECLRATARION=166;
	public static final int Use=167;
	public static final int VARIABLE_DECLARATION=168;
	public static final int VARIABLE_DECLARATION_LIST=169;
	public static final int VariableId=170;
	public static final int Void=171;
	public static final int While=172;
	public static final int Whitespace=173;

	// delegates
	public TreeFilter[] getDelegates() {
		return new TreeFilter[] {};
	}

	// delegators


	public TSPHPTypeCheckerReference(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public TSPHPTypeCheckerReference(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return TSPHPTypeCheckerReference.tokenNames; }
	@Override public String getGrammarFileName() { return "D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g"; }


	    ISymbolTable symbolTable;
	    public TSPHPTypeCheckerReference(TreeNodeStream input, ISymbolTable theSymbolTable) {
	        this(input);
	        symbolTable = theSymbolTable;
	    }
	    



	// $ANTLR start "topdown"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:58:1: topdown : variableDeclarationList ;
	public final void topdown() throws RecognitionException {
		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:59:5: ( variableDeclarationList )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:59:9: variableDeclarationList
			{
			pushFollow(FOLLOW_variableDeclarationList_in_topdown62);
			variableDeclarationList();
			state._fsp--;
			if (state.failed) return ;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return ;
	}
	// $ANTLR end "topdown"



	// $ANTLR start "variableDeclarationList"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:63:1: variableDeclarationList : ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= . allTypes ) ( variableDeclaration[(ITypeSymbol) $allTypes.start] )+ ) ;
	public final void variableDeclarationList() throws RecognitionException {
		TSPHPAst tMod=null;
		TreeRuleReturnScope allTypes1 =null;

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:64:2: ( ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= . allTypes ) ( variableDeclaration[(ITypeSymbol) $allTypes.start] )+ ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:64:4: ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= . allTypes ) ( variableDeclaration[(ITypeSymbol) $allTypes.start] )+ )
			{
			match(input,VARIABLE_DECLARATION_LIST,FOLLOW_VARIABLE_DECLARATION_LIST_in_variableDeclarationList79); if (state.failed) return ;
			match(input, Token.DOWN, null); if (state.failed) return ;
			match(input,TYPE,FOLLOW_TYPE_in_variableDeclarationList82); if (state.failed) return ;
			match(input, Token.DOWN, null); if (state.failed) return ;
			tMod=(TSPHPAst)input.LT(1);
			matchAny(input); if (state.failed) return ;
			pushFollow(FOLLOW_allTypes_in_variableDeclarationList88);
			allTypes1=allTypes();
			state._fsp--;
			if (state.failed) return ;
			match(input, Token.UP, null); if (state.failed) return ;

			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:64:56: ( variableDeclaration[(ITypeSymbol) $allTypes.start] )+
			int cnt1=0;
			loop1:
			do {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==VariableId) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:64:56: variableDeclaration[(ITypeSymbol) $allTypes.start]
					{
					pushFollow(FOLLOW_variableDeclaration_in_variableDeclarationList91);
					variableDeclaration((ITypeSymbol) (allTypes1!=null?((TSPHPAst)allTypes1.start):null));
					state._fsp--;
					if (state.failed) return ;
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					if (state.backtracking>0) {state.failed=true; return ;}
						EarlyExitException eee =
							new EarlyExitException(1, input);
						throw eee;
				}
				cnt1++;
			} while (true);

			match(input, Token.UP, null); if (state.failed) return ;

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return ;
	}
	// $ANTLR end "variableDeclarationList"



	// $ANTLR start "variableDeclaration"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:67:1: variableDeclaration[ITypeSymbol type] : ( ^(variableId= VariableId . ) |variableId= VariableId ) ;
	public final void variableDeclaration(ITypeSymbol type) throws RecognitionException {
		TSPHPAst variableId=null;

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:68:2: ( ( ^(variableId= VariableId . ) |variableId= VariableId ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:69:3: ( ^(variableId= VariableId . ) |variableId= VariableId )
			{
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:69:3: ( ^(variableId= VariableId . ) |variableId= VariableId )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==VariableId) ) {
				int LA2_1 = input.LA(2);
				if ( (LA2_1==DOWN) ) {
					alt2=1;
				}
				else if ( (LA2_1==UP||LA2_1==VariableId) ) {
					alt2=2;
				}
				else {
					if (state.backtracking>0) {state.failed=true; return ;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}
			else {
				if (state.backtracking>0) {state.failed=true; return ;}
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:69:5: ^(variableId= VariableId . )
					{
					variableId=(TSPHPAst)match(input,VariableId,FOLLOW_VariableId_in_variableDeclaration129); if (state.failed) return ;
					match(input, Token.DOWN, null); if (state.failed) return ;
					matchAny(input); if (state.failed) return ;
					match(input, Token.UP, null); if (state.failed) return ;

					}
					break;
				case 2 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:70:5: variableId= VariableId
					{
					variableId=(TSPHPAst)match(input,VariableId,FOLLOW_VariableId_in_variableDeclaration140); if (state.failed) return ;
					}
					break;

			}

			if ( state.backtracking==1 ) { 
						variableId.symbol.setType(type); 
						variableId.scope.definitionCheck(variableId.symbol);
					}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return ;
	}
	// $ANTLR end "variableDeclaration"


	public static class allTypes_return extends TreeRuleReturnScope {
		public ITypeSymbol type;
	};


	// $ANTLR start "allTypes"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:78:1: allTypes returns [ITypeSymbol type] : ( 'bool' | 'int' | 'float' | 'string' | 'array' | 'object' | 'resource' | TYPE_NAME );
	public final TSPHPTypeCheckerReference.allTypes_return allTypes() throws RecognitionException {
		TSPHPTypeCheckerReference.allTypes_return retval = new TSPHPTypeCheckerReference.allTypes_return();
		retval.start = input.LT(1);


			retval.type = symbolTable.resolveType(((TSPHPAst)retval.start));

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:82:2: ( 'bool' | 'int' | 'float' | 'string' | 'array' | 'object' | 'resource' | TYPE_NAME )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerReference.g:
			{
			if ( input.LA(1)==TYPE_NAME||(input.LA(1) >= TypeArray && input.LA(1) <= TypeString) ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "allTypes"

	// Delegated rules



	public static final BitSet FOLLOW_variableDeclarationList_in_topdown62 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VARIABLE_DECLARATION_LIST_in_variableDeclarationList79 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TYPE_in_variableDeclarationList82 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_allTypes_in_variableDeclarationList88 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_variableDeclaration_in_variableDeclarationList91 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000040000000000L});
	public static final BitSet FOLLOW_VariableId_in_variableDeclaration129 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_VariableId_in_variableDeclaration140 = new BitSet(new long[]{0x0000000000000002L});
}
