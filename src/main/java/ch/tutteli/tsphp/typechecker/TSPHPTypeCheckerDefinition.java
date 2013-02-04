// $ANTLR 3.5 D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g 2013-02-04 01:08:36

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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.NamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class TSPHPTypeCheckerDefinition extends TreeFilter {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTUAL_PARAMETERS", "ARRAY_ACCESS", 
		"Abstract", "Arrow", "As", "Assign", "At", "BINARY", "BLOCK", "BLOCK_CONDITIONAL", 
		"Backslash", "BitwiseAnd", "BitwiseAndAssign", "BitwiseNot", "BitwiseOr", 
		"BitwiseOrAssign", "BitwiseXor", "BitwiseXorAssign", "Bool", "Break", 
		"CASTING", "CASTING_ASSIGN", "CLASS_BODY", "CLASS_MEMBER", "CLASS_MEMBER_STATIC", 
		"CLASS_MODIFIER", "CONSTANT_DECLARATION", "CONSTANT_DECLARATION_LIST", 
		"Case", "Cast", "Catch", "Class", "Clone", "Colon", "Comma", "Comment", 
		"Const", "Construct", "Continue", "DECIMAL", "DEFAULT_NAMESPACE", "Deconstruct", 
		"Default", "Divide", "DivideAssign", "Do", "Dolar", "Dot", "DotAssign", 
		"DoubleColon", "EXCEPTION_LIST", "EXPONENT", "EXPRESSION", "EXPRESSION_LIST", 
		"Echo", "Else", "Equal", "Exit", "Extends", "FUNCTION_CALL", "Final", 
		"Float", "For", "Foreach", "Function", "GreaterEqualThan", "GreaterThan", 
		"HEXADECIMAL", "INTERFACE_BODY", "INTERFACE_CONSTRUCT", "Identical", "Identifier", 
		"If", "Implements", "Instanceof", "Int", "Interface", "LeftCurlyBrace", 
		"LeftParanthesis", "LeftSquareBrace", "LessEqualThan", "LessThan", "LogicAnd", 
		"LogicAndWeak", "LogicNot", "LogicOr", "LogicOrWeak", "LogicXorWeak", 
		"MEMBER_ACCESS", "MEMBER_ACCESS_STATIC", "MEMBER_MODIFIER", "METHOD_CALL", 
		"METHOD_DECLARATION", "METHOD_MODIFIER", "Minus", "MinusAssign", "MinusMinus", 
		"Modulo", "ModuloAssign", "Multiply", "MultiplyAssign", "NAMESPACE_BODY", 
		"Namespace", "New", "NotEqual", "NotEqualAlternative", "NotIdentical", 
		"Null", "OCTAL", "ObjectOperator", "PARAM_DECLARATION", "PARAM_LIST", 
		"PARAM_TYPE", "POST_DECREMENT", "POST_INCREMENT", "PRE_DECREMENT", "PRE_INCREMENT", 
		"Parent", "ParentColonColon", "Plus", "PlusAssign", "PlusPlus", "Private", 
		"ProtectThis", "Protected", "Public", "QuestionMark", "Return", "RightCurlyBrace", 
		"RightParanthesis", "RightSquareBrace", "STRING_DOUBLE_QUOTED", "STRING_SINGLE_QUOTED", 
		"SWITCH_CASES", "Self", "SelfColonColon", "Semicolon", "ShiftLeft", "ShiftLeftAssign", 
		"ShiftRight", "ShiftRightAssign", "Static", "String", "Switch", "TYPE", 
		"TYPE_MODIFIER", "TYPE_NAME", "This", "Throw", "Try", "TypeAliasBool", 
		"TypeAliasFloat", "TypeAliasInt", "TypeArray", "TypeBool", "TypeFloat", 
		"TypeInt", "TypeObject", "TypeResource", "TypeString", "UNARY_MINUS", 
		"USE_DECLRATARION", "Use", "VARIABLE_DECLARATION", "VARIABLE_DECLARATION_LIST", 
		"VariableId", "Void", "While", "Whitespace"
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
	public static final int CLASS_MEMBER_STATIC=28;
	public static final int CLASS_MODIFIER=29;
	public static final int CONSTANT_DECLARATION=30;
	public static final int CONSTANT_DECLARATION_LIST=31;
	public static final int Case=32;
	public static final int Cast=33;
	public static final int Catch=34;
	public static final int Class=35;
	public static final int Clone=36;
	public static final int Colon=37;
	public static final int Comma=38;
	public static final int Comment=39;
	public static final int Const=40;
	public static final int Construct=41;
	public static final int Continue=42;
	public static final int DECIMAL=43;
	public static final int DEFAULT_NAMESPACE=44;
	public static final int Deconstruct=45;
	public static final int Default=46;
	public static final int Divide=47;
	public static final int DivideAssign=48;
	public static final int Do=49;
	public static final int Dolar=50;
	public static final int Dot=51;
	public static final int DotAssign=52;
	public static final int DoubleColon=53;
	public static final int EXCEPTION_LIST=54;
	public static final int EXPONENT=55;
	public static final int EXPRESSION=56;
	public static final int EXPRESSION_LIST=57;
	public static final int Echo=58;
	public static final int Else=59;
	public static final int Equal=60;
	public static final int Exit=61;
	public static final int Extends=62;
	public static final int FUNCTION_CALL=63;
	public static final int Final=64;
	public static final int Float=65;
	public static final int For=66;
	public static final int Foreach=67;
	public static final int Function=68;
	public static final int GreaterEqualThan=69;
	public static final int GreaterThan=70;
	public static final int HEXADECIMAL=71;
	public static final int INTERFACE_BODY=72;
	public static final int INTERFACE_CONSTRUCT=73;
	public static final int Identical=74;
	public static final int Identifier=75;
	public static final int If=76;
	public static final int Implements=77;
	public static final int Instanceof=78;
	public static final int Int=79;
	public static final int Interface=80;
	public static final int LeftCurlyBrace=81;
	public static final int LeftParanthesis=82;
	public static final int LeftSquareBrace=83;
	public static final int LessEqualThan=84;
	public static final int LessThan=85;
	public static final int LogicAnd=86;
	public static final int LogicAndWeak=87;
	public static final int LogicNot=88;
	public static final int LogicOr=89;
	public static final int LogicOrWeak=90;
	public static final int LogicXorWeak=91;
	public static final int MEMBER_ACCESS=92;
	public static final int MEMBER_ACCESS_STATIC=93;
	public static final int MEMBER_MODIFIER=94;
	public static final int METHOD_CALL=95;
	public static final int METHOD_DECLARATION=96;
	public static final int METHOD_MODIFIER=97;
	public static final int Minus=98;
	public static final int MinusAssign=99;
	public static final int MinusMinus=100;
	public static final int Modulo=101;
	public static final int ModuloAssign=102;
	public static final int Multiply=103;
	public static final int MultiplyAssign=104;
	public static final int NAMESPACE_BODY=105;
	public static final int Namespace=106;
	public static final int New=107;
	public static final int NotEqual=108;
	public static final int NotEqualAlternative=109;
	public static final int NotIdentical=110;
	public static final int Null=111;
	public static final int OCTAL=112;
	public static final int ObjectOperator=113;
	public static final int PARAM_DECLARATION=114;
	public static final int PARAM_LIST=115;
	public static final int PARAM_TYPE=116;
	public static final int POST_DECREMENT=117;
	public static final int POST_INCREMENT=118;
	public static final int PRE_DECREMENT=119;
	public static final int PRE_INCREMENT=120;
	public static final int Parent=121;
	public static final int ParentColonColon=122;
	public static final int Plus=123;
	public static final int PlusAssign=124;
	public static final int PlusPlus=125;
	public static final int Private=126;
	public static final int ProtectThis=127;
	public static final int Protected=128;
	public static final int Public=129;
	public static final int QuestionMark=130;
	public static final int Return=131;
	public static final int RightCurlyBrace=132;
	public static final int RightParanthesis=133;
	public static final int RightSquareBrace=134;
	public static final int STRING_DOUBLE_QUOTED=135;
	public static final int STRING_SINGLE_QUOTED=136;
	public static final int SWITCH_CASES=137;
	public static final int Self=138;
	public static final int SelfColonColon=139;
	public static final int Semicolon=140;
	public static final int ShiftLeft=141;
	public static final int ShiftLeftAssign=142;
	public static final int ShiftRight=143;
	public static final int ShiftRightAssign=144;
	public static final int Static=145;
	public static final int String=146;
	public static final int Switch=147;
	public static final int TYPE=148;
	public static final int TYPE_MODIFIER=149;
	public static final int TYPE_NAME=150;
	public static final int This=151;
	public static final int Throw=152;
	public static final int Try=153;
	public static final int TypeAliasBool=154;
	public static final int TypeAliasFloat=155;
	public static final int TypeAliasInt=156;
	public static final int TypeArray=157;
	public static final int TypeBool=158;
	public static final int TypeFloat=159;
	public static final int TypeInt=160;
	public static final int TypeObject=161;
	public static final int TypeResource=162;
	public static final int TypeString=163;
	public static final int UNARY_MINUS=164;
	public static final int USE_DECLRATARION=165;
	public static final int Use=166;
	public static final int VARIABLE_DECLARATION=167;
	public static final int VARIABLE_DECLARATION_LIST=168;
	public static final int VariableId=169;
	public static final int Void=170;
	public static final int While=171;
	public static final int Whitespace=172;

	// delegates
	public TreeFilter[] getDelegates() {
		return new TreeFilter[] {};
	}

	// delegators


	public TSPHPTypeCheckerDefinition(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public TSPHPTypeCheckerDefinition(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return TSPHPTypeCheckerDefinition.tokenNames; }
	@Override public String getGrammarFileName() { return "D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g"; }


		protected SymbolTable symbolTable;
		protected ISymbolFactory symbolFactory;
		private IScope currentScope;
	    	public TSPHPTypeCheckerDefinition(TreeNodeStream input, SymbolTable theSymbolTable, ISymbolFactory theSymbolFactory) {
	        	this(input);
		        symbolTable = theSymbolTable;
	        	currentScope = theSymbolTable.globalScope;
	    		symbolFactory = theSymbolFactory;
		}



	// $ANTLR start "topdown"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:62:1: topdown : ( enterNamespace | varDeclarationList );
	public final void topdown() throws RecognitionException {
		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:63:5: ( enterNamespace | varDeclarationList )
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==Namespace) ) {
				alt1=1;
			}
			else if ( (LA1_0==VARIABLE_DECLARATION_LIST) ) {
				alt1=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:63:7: enterNamespace
					{
					pushFollow(FOLLOW_enterNamespace_in_topdown64);
					enterNamespace();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:64:7: varDeclarationList
					{
					pushFollow(FOLLOW_varDeclarationList_in_topdown72);
					varDeclarationList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "topdown"



	// $ANTLR start "bottomup"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:67:1: bottomup : exitNamespace ;
	public final void bottomup() throws RecognitionException {
		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:68:5: ( exitNamespace )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:68:9: exitNamespace
			{
			pushFollow(FOLLOW_exitNamespace_in_bottomup91);
			exitNamespace();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "bottomup"



	// $ANTLR start "enterNamespace"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:71:1: enterNamespace : ^( Namespace t= ( TYPE_NAME | DEFAULT_NAMESPACE ) . ) ;
	public final void enterNamespace() throws RecognitionException {
		TSPHPAst t=null;

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:72:2: ( ^( Namespace t= ( TYPE_NAME | DEFAULT_NAMESPACE ) . ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:72:4: ^( Namespace t= ( TYPE_NAME | DEFAULT_NAMESPACE ) . )
			{
			match(input,Namespace,FOLLOW_Namespace_in_enterNamespace110); if (state.failed) return;
			match(input, Token.DOWN, null); if (state.failed) return;
			t=(TSPHPAst)input.LT(1);
			if ( input.LA(1)==DEFAULT_NAMESPACE||input.LA(1)==TYPE_NAME ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			matchAny(input); if (state.failed) return;
			match(input, Token.UP, null); if (state.failed) return;

			if ( state.backtracking==1 ) {currentScope = new NamespaceScope((t!=null?t.getText():null),currentScope);}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "enterNamespace"



	// $ANTLR start "exitNamespace"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:74:1: exitNamespace : Namespace ;
	public final void exitNamespace() throws RecognitionException {
		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:75:2: ( Namespace )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:75:4: Namespace
			{
			match(input,Namespace,FOLLOW_Namespace_in_exitNamespace133); if (state.failed) return;
			if ( state.backtracking==1 ) {currentScope = currentScope.getEnclosingScope();}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "exitNamespace"



	// $ANTLR start "varDeclarationList"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:78:1: varDeclarationList : ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= typeModifier type= . ) ( varDeclaration[$tMod.start,$type] )* ) ;
	public final void varDeclarationList() throws RecognitionException {
		TSPHPAst type=null;
		TreeRuleReturnScope tMod =null;

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:79:5: ( ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= typeModifier type= . ) ( varDeclaration[$tMod.start,$type] )* ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:79:9: ^( VARIABLE_DECLARATION_LIST ^( TYPE tMod= typeModifier type= . ) ( varDeclaration[$tMod.start,$type] )* )
			{
			match(input,VARIABLE_DECLARATION_LIST,FOLLOW_VARIABLE_DECLARATION_LIST_in_varDeclarationList156); if (state.failed) return;
			match(input, Token.DOWN, null); if (state.failed) return;
			match(input,TYPE,FOLLOW_TYPE_in_varDeclarationList166); if (state.failed) return;
			match(input, Token.DOWN, null); if (state.failed) return;
			pushFollow(FOLLOW_typeModifier_in_varDeclarationList170);
			tMod=typeModifier();
			state._fsp--;
			if (state.failed) return;
			type=(TSPHPAst)input.LT(1);
			matchAny(input); if (state.failed) return;
			match(input, Token.UP, null); if (state.failed) return;

			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:81:7: ( varDeclaration[$tMod.start,$type] )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==VariableId) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:81:7: varDeclaration[$tMod.start,$type]
					{
					pushFollow(FOLLOW_varDeclaration_in_varDeclarationList183);
					varDeclaration((tMod!=null?((TSPHPAst)tMod.start):null), type);
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop2;
				}
			}

			match(input, Token.UP, null); if (state.failed) return;

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "varDeclarationList"


	public static class typeModifier_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "typeModifier"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:86:1: typeModifier : ( TYPE_MODIFIER | ^( TYPE_MODIFIER ( Cast )? ( QuestionMark )? ) );
	public final TSPHPTypeCheckerDefinition.typeModifier_return typeModifier() throws RecognitionException {
		TSPHPTypeCheckerDefinition.typeModifier_return retval = new TSPHPTypeCheckerDefinition.typeModifier_return();
		retval.start = input.LT(1);

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:87:2: ( TYPE_MODIFIER | ^( TYPE_MODIFIER ( Cast )? ( QuestionMark )? ) )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==TYPE_MODIFIER) ) {
				int LA5_1 = input.LA(2);
				if ( (LA5_1==DOWN) ) {
					alt5=2;
				}
				else if ( ((LA5_1 >= ACTUAL_PARAMETERS && LA5_1 <= Whitespace)) ) {
					alt5=1;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 5, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:87:4: TYPE_MODIFIER
					{
					match(input,TYPE_MODIFIER,FOLLOW_TYPE_MODIFIER_in_typeModifier219); if (state.failed) return retval;
					}
					break;
				case 2 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:88:8: ^( TYPE_MODIFIER ( Cast )? ( QuestionMark )? )
					{
					match(input,TYPE_MODIFIER,FOLLOW_TYPE_MODIFIER_in_typeModifier229); if (state.failed) return retval;
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); if (state.failed) return retval;
						// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:88:24: ( Cast )?
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( (LA3_0==Cast) ) {
							alt3=1;
						}
						switch (alt3) {
							case 1 :
								// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:88:24: Cast
								{
								match(input,Cast,FOLLOW_Cast_in_typeModifier231); if (state.failed) return retval;
								}
								break;

						}

						// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:88:30: ( QuestionMark )?
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( (LA4_0==QuestionMark) ) {
							alt4=1;
						}
						switch (alt4) {
							case 1 :
								// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:88:30: QuestionMark
								{
								match(input,QuestionMark,FOLLOW_QuestionMark_in_typeModifier234); if (state.failed) return retval;
								}
								break;

						}

						match(input, Token.UP, null); if (state.failed) return retval;
					}

					}
					break;

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
	// $ANTLR end "typeModifier"



	// $ANTLR start "varDeclaration"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:91:1: varDeclaration[TSPHPAst tMod, TSPHPAst type] : ( ^(variableId= VariableId (~ CASTING ) ) |variableId= VariableId | ^(variableId= VariableId cast ) ) ;
	public final void varDeclaration(TSPHPAst tMod, TSPHPAst type) throws RecognitionException {
		TSPHPAst variableId=null;

		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:92:2: ( ( ^(variableId= VariableId (~ CASTING ) ) |variableId= VariableId | ^(variableId= VariableId cast ) ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:93:2: ( ^(variableId= VariableId (~ CASTING ) ) |variableId= VariableId | ^(variableId= VariableId cast ) )
			{
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:93:2: ( ^(variableId= VariableId (~ CASTING ) ) |variableId= VariableId | ^(variableId= VariableId cast ) )
			int alt6=3;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==VariableId) ) {
				int LA6_1 = input.LA(2);
				if ( (LA6_1==DOWN) ) {
					int LA6_2 = input.LA(3);
					if ( ((LA6_2 >= ACTUAL_PARAMETERS && LA6_2 <= Break)||(LA6_2 >= CASTING_ASSIGN && LA6_2 <= Whitespace)) ) {
						alt6=1;
					}
					else if ( (LA6_2==CASTING) ) {
						alt6=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 6, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA6_1==UP||LA6_1==VariableId) ) {
					alt6=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:93:4: ^(variableId= VariableId (~ CASTING ) )
					{
					variableId=(TSPHPAst)match(input,VariableId,FOLLOW_VariableId_in_varDeclaration255); if (state.failed) return;
					match(input, Token.DOWN, null); if (state.failed) return;
					if ( (input.LA(1) >= ACTUAL_PARAMETERS && input.LA(1) <= Break)||(input.LA(1) >= CASTING_ASSIGN && input.LA(1) <= Whitespace) ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					match(input, Token.UP, null); if (state.failed) return;

					}
					break;
				case 2 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:94:4: variableId= VariableId
					{
					variableId=(TSPHPAst)match(input,VariableId,FOLLOW_VariableId_in_varDeclaration268); if (state.failed) return;
					}
					break;
				case 3 :
					// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:95:4: ^(variableId= VariableId cast )
					{
					variableId=(TSPHPAst)match(input,VariableId,FOLLOW_VariableId_in_varDeclaration277); if (state.failed) return;
					match(input, Token.DOWN, null); if (state.failed) return;
					pushFollow(FOLLOW_cast_in_varDeclaration279);
					cast();
					state._fsp--;
					if (state.failed) return;
					match(input, Token.UP, null); if (state.failed) return;

					}
					break;

			}

			if ( state.backtracking==1 ) {
				        type.scope = currentScope;
				        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(tMod,variableId);
				        currentScope.define(variableSymbol);
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
	}
	// $ANTLR end "varDeclaration"



	// $ANTLR start "cast"
	// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:104:1: cast : ^( CASTING ^( TYPE typeModifier . ) . ) ;
	public final void cast() throws RecognitionException {
		try {
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:104:6: ( ^( CASTING ^( TYPE typeModifier . ) . ) )
			// D:\\TSPHP-typechecker\\src\\main\\antlr\\TSPHPTypeCheckerDefinition.g:104:8: ^( CASTING ^( TYPE typeModifier . ) . )
			{
			match(input,CASTING,FOLLOW_CASTING_in_cast297); if (state.failed) return;
			match(input, Token.DOWN, null); if (state.failed) return;
			match(input,TYPE,FOLLOW_TYPE_in_cast300); if (state.failed) return;
			match(input, Token.DOWN, null); if (state.failed) return;
			pushFollow(FOLLOW_typeModifier_in_cast302);
			typeModifier();
			state._fsp--;
			if (state.failed) return;
			matchAny(input); if (state.failed) return;
			match(input, Token.UP, null); if (state.failed) return;

			matchAny(input); if (state.failed) return;
			match(input, Token.UP, null); if (state.failed) return;

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "cast"

	// Delegated rules



	public static final BitSet FOLLOW_enterNamespace_in_topdown64 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varDeclarationList_in_topdown72 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_exitNamespace_in_bottomup91 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Namespace_in_enterNamespace110 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_set_in_enterNamespace114 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x00001FFFFFFFFFFFL});
	public static final BitSet FOLLOW_Namespace_in_exitNamespace133 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VARIABLE_DECLARATION_LIST_in_varDeclarationList156 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TYPE_in_varDeclarationList166 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_typeModifier_in_varDeclarationList170 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x00001FFFFFFFFFFFL});
	public static final BitSet FOLLOW_varDeclaration_in_varDeclarationList183 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000020000000000L});
	public static final BitSet FOLLOW_TYPE_MODIFIER_in_typeModifier219 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TYPE_MODIFIER_in_typeModifier229 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Cast_in_typeModifier231 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_QuestionMark_in_typeModifier234 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VariableId_in_varDeclaration255 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_set_in_varDeclaration257 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VariableId_in_varDeclaration268 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VariableId_in_varDeclaration277 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_cast_in_varDeclaration279 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CASTING_in_cast297 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TYPE_in_cast300 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_typeModifier_in_cast302 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x00001FFFFFFFFFFFL});
}
