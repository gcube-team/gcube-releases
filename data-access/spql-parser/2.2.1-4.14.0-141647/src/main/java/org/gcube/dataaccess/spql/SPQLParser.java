// Generated from /home/lucio/workspace/imarine/SPQLParser.BRANCH/src/main/resources/SPQL.g by ANTLR 4.6

package org.gcube.dataaccess.spql;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.gcube.dataaccess.spql.model.*;
import org.gcube.dataaccess.spql.model.where.*;
import org.gcube.dataaccess.spql.model.having.*;
import org.gcube.dataaccess.spql.model.ret.*;
import org.gcube.dataaccess.spql.model.error.SyntaxError;
import static org.gcube.dataaccess.spql.model.where.ConditionParameter.*;
import static org.gcube.dataaccess.spql.model.RelationalOperator.*;
import static org.gcube.dataaccess.spql.model.TermType.*;


import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SPQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
	T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
	T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
	T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
	T__24=25, T__25=26, T__26=27, LUCIO=28, EXL=29, XPATH=30, SEARCHBY=31, 
	EXPAND=32, RESOLVE=33, UNFOLD=34, WITH=35, IN=36, WHERE=37, RETURN=38, 
	AND=39, IS=40, AS=41, HAVING=42, PRODUCT=43, OCCURRENCE=44, TAXON=45, 
	TRUE=46, FALSE=47, NULL=48, ID=49, INT=50, FLOAT=51, COMMENT=52, WS=53, 
	STRING=54, STRING_DOUBLE_QUOTE=55;
	public static final int
	RULE_query = 0, RULE_termsRule = 1, RULE_termRule = 2, RULE_scientificTerms = 3, 
	RULE_commonNameTerms = 4, RULE_wordsRule = 5, RULE_wordRule = 6, RULE_unfoldClause = 7, 
	RULE_expandClause = 8, RULE_resolveClause = 9, RULE_datasourcesRule = 10, 
	RULE_datasourceRule = 11, RULE_rankRule = 12, RULE_whereExpression = 13, 
	RULE_wexpression = 14, RULE_coordinateCondition = 15, RULE_eventDateCondition = 16, 
	RULE_relationalOperator = 17, RULE_dateRule = 18, RULE_coordinateRule = 19, 
	RULE_returnExpression = 20, RULE_havingExpression = 21, RULE_number = 22, 
	RULE_unary_operator = 23, RULE_unsigned_number = 24, RULE_expressionRule = 25, 
	RULE_parExpression = 26, RULE_conditionalAndExpression = 27, RULE_equalityExpression = 28, 
	RULE_relationalExpression = 29, RULE_relationalOp = 30, RULE_additiveExpression = 31, 
	RULE_multiplicativeExpression = 32, RULE_unaryExpression = 33, RULE_unaryExpressionNotPlusMinus = 34, 
	RULE_primary = 35, RULE_calls = 36, RULE_xpath_function = 37, RULE_exl_function = 38, 
	RULE_lucio_function = 39, RULE_literal = 40;
	public static final String[] ruleNames = {
		"query", "termsRule", "termRule", "scientificTerms", "commonNameTerms", 
		"wordsRule", "wordRule", "unfoldClause", "expandClause", "resolveClause", 
		"datasourcesRule", "datasourceRule", "rankRule", "whereExpression", "wexpression", 
		"coordinateCondition", "eventDateCondition", "relationalOperator", "dateRule", 
		"coordinateRule", "returnExpression", "havingExpression", "number", "unary_operator", 
		"unsigned_number", "expressionRule", "parExpression", "conditionalAndExpression", 
		"equalityExpression", "relationalExpression", "relationalOp", "additiveExpression", 
		"multiplicativeExpression", "unaryExpression", "unaryExpressionNotPlusMinus", 
		"primary", "calls", "xpath_function", "exl_function", "lucio_function", 
		"literal"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "'ScientificName'", "'SN'", "'CommonName'", "'CN'", "'coordinate'", 
		"'eventDate'", "'<'", "'<='", "'=='", "'>'", "'>='", "'+'", "'-'", "'||'", 
		"'('", "')'", "'&&'", "'!='", "'='", "'*'", "'/'", "'%'", "'++'", "'--'", 
		"'!'", "'.'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, "LUCIO", "EXL", "XPATH", "SEARCHBY", "EXPAND", 
		"RESOLVE", "UNFOLD", "WITH", "IN", "WHERE", "RETURN", "AND", "IS", "AS", 
		"HAVING", "PRODUCT", "OCCURRENCE", "TAXON", "TRUE", "FALSE", "NULL", "ID", 
		"INT", "FLOAT", "COMMENT", "WS", "STRING", "STRING_DOUBLE_QUOTE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SPQL.g"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	/*    @Override
	    public void reportError(RecognitionException e) throws RecognitionException {
	        throw e;
	    }
	    @Override
	      public void emitErrorMessage(String msg) {
	      throw new SyntaxError(msg);
	  }


	    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException
	    {
	      throw e;
	    }

	    //disable re-sync
	    @Override
	  protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
	  {
	    throw new MismatchedTokenException(ttype, input);
	  }

	  protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException
	  {
	    throw new MismatchedTokenException(ttype, input);
	   }


	  @Override
	  public void recover(IntStream input, RecognitionException re) {
	    throw new SyntaxError(re.getMessage());
	  }*/

	public SPQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class QueryContext extends ParserRuleContext {
		public Query result;
		public TermsRuleContext t;
		public DatasourcesRuleContext d;
		public WhereExpressionContext we;
		public ReturnExpressionContext rt;
		public HavingExpressionContext he;
		public TerminalNode SEARCHBY() { return getToken(SPQLParser.SEARCHBY, 0); }
		public TerminalNode EOF() { return getToken(SPQLParser.EOF, 0); }
		public TermsRuleContext termsRule() {
			return getRuleContext(TermsRuleContext.class,0);
		}
		public TerminalNode IN() { return getToken(SPQLParser.IN, 0); }
		public TerminalNode WHERE() { return getToken(SPQLParser.WHERE, 0); }
		public TerminalNode RETURN() { return getToken(SPQLParser.RETURN, 0); }
		public TerminalNode HAVING() { return getToken(SPQLParser.HAVING, 0); }
		public DatasourcesRuleContext datasourcesRule() {
			return getRuleContext(DatasourcesRuleContext.class,0);
		}
		public WhereExpressionContext whereExpression() {
			return getRuleContext(WhereExpressionContext.class,0);
		}
		public ReturnExpressionContext returnExpression() {
			return getRuleContext(ReturnExpressionContext.class,0);
		}
		public HavingExpressionContext havingExpression() {
			return getRuleContext(HavingExpressionContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitQuery(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((QueryContext)_localctx).result =  new Query();
				setState(83);
				match(SEARCHBY);
				setState(84);
				((QueryContext)_localctx).t = termsRule();
				_localctx.result.setTerms(((QueryContext)_localctx).t.terms);
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
						setState(86);
						match(IN);
						setState(87);
						((QueryContext)_localctx).d = datasourcesRule();
						_localctx.result.setDatasources(((QueryContext)_localctx).d.datasources);
					}
				}

				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
						setState(92);
						match(WHERE);
						setState(93);
						((QueryContext)_localctx).we = whereExpression();
						_localctx.result.setWhereExpression(new WhereExpression(((QueryContext)_localctx).we.conditions));
					}
				}

				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==RETURN) {
					{
						setState(98);
						match(RETURN);
						setState(99);
						((QueryContext)_localctx).rt = returnExpression();
						_localctx.result.setReturnType(((QueryContext)_localctx).rt.returnType);
					}
				}

				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
						setState(104);
						match(HAVING);
						setState(105);
						((QueryContext)_localctx).he = havingExpression();
						_localctx.result.setHavingExpression(((QueryContext)_localctx).he.expression);
					}
				}

				setState(110);
				match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermsRuleContext extends ParserRuleContext {
		public List<Term> terms;
		public TermRuleContext t;
		public List<TermRuleContext> termRule() {
			return getRuleContexts(TermRuleContext.class);
		}
		public TermRuleContext termRule(int i) {
			return getRuleContext(TermRuleContext.class,i);
		}
		public TermsRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termsRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterTermsRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitTermsRule(this);
		}
	}

	public final TermsRuleContext termsRule() throws RecognitionException {
		TermsRuleContext _localctx = new TermsRuleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_termsRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((TermsRuleContext)_localctx).terms =  new ArrayList<Term>();
				setState(113);
				((TermsRuleContext)_localctx).t = termRule();
				_localctx.terms.add(((TermsRuleContext)_localctx).t.term);
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
						{
							setState(115);
							match(T__0);
							setState(116);
							((TermsRuleContext)_localctx).t = termRule();
							_localctx.terms.add(((TermsRuleContext)_localctx).t.term);
						}
					}
					setState(123);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermRuleContext extends ParserRuleContext {
		public Term term;
		public ScientificTermsContext scientificTerms() {
			return getRuleContext(ScientificTermsContext.class,0);
		}
		public CommonNameTermsContext commonNameTerms() {
			return getRuleContext(CommonNameTermsContext.class,0);
		}
		public TermRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterTermRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitTermRule(this);
		}
	}

	public final TermRuleContext termRule() throws RecognitionException {
		TermRuleContext _localctx = new TermRuleContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_termRule);
		try {
			setState(130);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
					setState(124);
					((TermRuleContext)_localctx).term =  scientificTerms().term;
				}
				break;
			case T__3:
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
					setState(127);
					((TermRuleContext)_localctx).term =  commonNameTerms().term;
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ScientificTermsContext extends ParserRuleContext {
		public Term term;
		public WordsRuleContext w;
		public UnfoldClauseContext u;
		public ExpandClauseContext e;
		public WordsRuleContext wordsRule() {
			return getRuleContext(WordsRuleContext.class,0);
		}
		public UnfoldClauseContext unfoldClause() {
			return getRuleContext(UnfoldClauseContext.class,0);
		}
		public ExpandClauseContext expandClause() {
			return getRuleContext(ExpandClauseContext.class,0);
		}
		public ScientificTermsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scientificTerms; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterScientificTerms(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitScientificTerms(this);
		}
	}

	public final ScientificTermsContext scientificTerms() throws RecognitionException {
		ScientificTermsContext _localctx = new ScientificTermsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_scientificTerms);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((ScientificTermsContext)_localctx).term =  new Term(SCIENTIFIC_NAME);
				setState(133);
				_la = _input.LA(1);
				if ( !(_la==T__1 || _la==T__2) ) {
					_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(134);
				((ScientificTermsContext)_localctx).w = wordsRule();
				_localctx.term.setWords(((ScientificTermsContext)_localctx).w.words);
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==UNFOLD) {
					{
						setState(136);
						((ScientificTermsContext)_localctx).u = unfoldClause();
						_localctx.term.setUnfoldClause(((ScientificTermsContext)_localctx).u.clause);
					}
				}

				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPAND) {
					{
						setState(141);
						((ScientificTermsContext)_localctx).e = expandClause();
						_localctx.term.setExpandClause(((ScientificTermsContext)_localctx).e.clause);
					}
				}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommonNameTermsContext extends ParserRuleContext {
		public Term term;
		public WordsRuleContext w;
		public ResolveClauseContext r;
		public ExpandClauseContext e;
		public WordsRuleContext wordsRule() {
			return getRuleContext(WordsRuleContext.class,0);
		}
		public ResolveClauseContext resolveClause() {
			return getRuleContext(ResolveClauseContext.class,0);
		}
		public ExpandClauseContext expandClause() {
			return getRuleContext(ExpandClauseContext.class,0);
		}
		public CommonNameTermsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commonNameTerms; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterCommonNameTerms(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitCommonNameTerms(this);
		}
	}

	public final CommonNameTermsContext commonNameTerms() throws RecognitionException {
		CommonNameTermsContext _localctx = new CommonNameTermsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_commonNameTerms);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((CommonNameTermsContext)_localctx).term =  new Term(COMMON_NAME);
				setState(147);
				_la = _input.LA(1);
				if ( !(_la==T__3 || _la==T__4) ) {
					_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(148);
				((CommonNameTermsContext)_localctx).w = wordsRule();
				_localctx.term.setWords(((CommonNameTermsContext)_localctx).w.words);
				{
					setState(150);
					((CommonNameTermsContext)_localctx).r = resolveClause();
					_localctx.term.setResolveClause(((CommonNameTermsContext)_localctx).r.clause);
				}
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPAND) {
					{
						setState(153);
						((CommonNameTermsContext)_localctx).e = expandClause();
						_localctx.term.setExpandClause(((CommonNameTermsContext)_localctx).e.clause);
					}
				}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WordsRuleContext extends ParserRuleContext {
		public List<String> words;
		public WordRuleContext w;
		public List<WordRuleContext> wordRule() {
			return getRuleContexts(WordRuleContext.class);
		}
		public WordRuleContext wordRule(int i) {
			return getRuleContext(WordRuleContext.class,i);
		}
		public WordsRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wordsRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterWordsRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitWordsRule(this);
		}
	}

	public final WordsRuleContext wordsRule() throws RecognitionException {
		WordsRuleContext _localctx = new WordsRuleContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_wordsRule);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
				((WordsRuleContext)_localctx).words =  new ArrayList();
				setState(159);
				((WordsRuleContext)_localctx).w = wordRule();
				_localctx.words.add(((WordsRuleContext)_localctx).w.word);
				setState(167);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
							{
								setState(161);
								match(T__0);
								setState(162);
								((WordsRuleContext)_localctx).w = wordRule();
								_localctx.words.add(((WordsRuleContext)_localctx).w.word);
							}
						} 
					}
					setState(169);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WordRuleContext extends ParserRuleContext {
		public String word;
		public Token STRING;
		public TerminalNode STRING() { return getToken(SPQLParser.STRING, 0); }
		public WordRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wordRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterWordRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitWordRule(this);
		}
	}

	public final WordRuleContext wordRule() throws RecognitionException {
		WordRuleContext _localctx = new WordRuleContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_wordRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(170);
				((WordRuleContext)_localctx).STRING = match(STRING);
				((WordRuleContext)_localctx).word =  (((WordRuleContext)_localctx).STRING!=null?((WordRuleContext)_localctx).STRING.getText():null).substring(1,(((WordRuleContext)_localctx).STRING!=null?((WordRuleContext)_localctx).STRING.getText():null).length()-1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnfoldClauseContext extends ParserRuleContext {
		public UnfoldClause clause;
		public DatasourceRuleContext d;
		public TerminalNode UNFOLD() { return getToken(SPQLParser.UNFOLD, 0); }
		public TerminalNode WITH() { return getToken(SPQLParser.WITH, 0); }
		public DatasourceRuleContext datasourceRule() {
			return getRuleContext(DatasourceRuleContext.class,0);
		}
		public UnfoldClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unfoldClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterUnfoldClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitUnfoldClause(this);
		}
	}

	public final UnfoldClauseContext unfoldClause() throws RecognitionException {
		UnfoldClauseContext _localctx = new UnfoldClauseContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_unfoldClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
				((UnfoldClauseContext)_localctx).clause =  new UnfoldClause();
				setState(174);
				match(UNFOLD);
				setState(175);
				match(WITH);
				setState(176);
				((UnfoldClauseContext)_localctx).d = datasourceRule();
				_localctx.clause.setDatasource(((UnfoldClauseContext)_localctx).d.datasource);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpandClauseContext extends ParserRuleContext {
		public ExpandClause clause;
		public DatasourcesRuleContext d;
		public TerminalNode EXPAND() { return getToken(SPQLParser.EXPAND, 0); }
		public TerminalNode WITH() { return getToken(SPQLParser.WITH, 0); }
		public DatasourcesRuleContext datasourcesRule() {
			return getRuleContext(DatasourcesRuleContext.class,0);
		}
		public ExpandClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expandClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterExpandClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitExpandClause(this);
		}
	}

	public final ExpandClauseContext expandClause() throws RecognitionException {
		ExpandClauseContext _localctx = new ExpandClauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_expandClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((ExpandClauseContext)_localctx).clause =  new ExpandClause();
				setState(180);
				match(EXPAND);
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITH) {
					{
						setState(181);
						match(WITH);
						setState(182);
						((ExpandClauseContext)_localctx).d = datasourcesRule();
						_localctx.clause.setDatasources(((ExpandClauseContext)_localctx).d.datasources);
					}
				}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ResolveClauseContext extends ParserRuleContext {
		public ResolveClause clause;
		public DatasourcesRuleContext d;
		public TerminalNode RESOLVE() { return getToken(SPQLParser.RESOLVE, 0); }
		public TerminalNode WITH() { return getToken(SPQLParser.WITH, 0); }
		public DatasourcesRuleContext datasourcesRule() {
			return getRuleContext(DatasourcesRuleContext.class,0);
		}
		public ResolveClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resolveClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterResolveClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitResolveClause(this);
		}
	}

	public final ResolveClauseContext resolveClause() throws RecognitionException {
		ResolveClauseContext _localctx = new ResolveClauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_resolveClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((ResolveClauseContext)_localctx).clause =  new ResolveClause();
				setState(188);
				match(RESOLVE);
				setState(193);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITH) {
					{
						setState(189);
						match(WITH);
						setState(190);
						((ResolveClauseContext)_localctx).d = datasourcesRule();
						_localctx.clause.setDatasources(((ResolveClauseContext)_localctx).d.datasources);
					}
				}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatasourcesRuleContext extends ParserRuleContext {
		public List<String> datasources;
		public DatasourceRuleContext d;
		public List<DatasourceRuleContext> datasourceRule() {
			return getRuleContexts(DatasourceRuleContext.class);
		}
		public DatasourceRuleContext datasourceRule(int i) {
			return getRuleContext(DatasourceRuleContext.class,i);
		}
		public DatasourcesRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datasourcesRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterDatasourcesRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitDatasourcesRule(this);
		}
	}

	public final DatasourcesRuleContext datasourcesRule() throws RecognitionException {
		DatasourcesRuleContext _localctx = new DatasourcesRuleContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_datasourcesRule);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
				((DatasourcesRuleContext)_localctx).datasources =  new ArrayList();
				setState(196);
				((DatasourcesRuleContext)_localctx).d = datasourceRule();
				_localctx.datasources.add(((DatasourcesRuleContext)_localctx).d.datasource);
				setState(204);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
							{
								setState(198);
								match(T__0);
								setState(199);
								((DatasourcesRuleContext)_localctx).d = datasourceRule();
								_localctx.datasources.add(((DatasourcesRuleContext)_localctx).d.datasource);
							}
						} 
					}
					setState(206);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatasourceRuleContext extends ParserRuleContext {
		public String datasource;
		public Token ID;
		public TerminalNode ID() { return getToken(SPQLParser.ID, 0); }
		public DatasourceRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datasourceRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterDatasourceRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitDatasourceRule(this);
		}
	}

	public final DatasourceRuleContext datasourceRule() throws RecognitionException {
		DatasourceRuleContext _localctx = new DatasourceRuleContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_datasourceRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(207);
				((DatasourceRuleContext)_localctx).ID = match(ID);
				((DatasourceRuleContext)_localctx).datasource =  (((DatasourceRuleContext)_localctx).ID!=null?((DatasourceRuleContext)_localctx).ID.getText():null);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RankRuleContext extends ParserRuleContext {
		public String rank;
		public Token ID;
		public TerminalNode ID() { return getToken(SPQLParser.ID, 0); }
		public RankRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rankRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterRankRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitRankRule(this);
		}
	}

	public final RankRuleContext rankRule() throws RecognitionException {
		RankRuleContext _localctx = new RankRuleContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_rankRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(210);
				((RankRuleContext)_localctx).ID = match(ID);
				((RankRuleContext)_localctx).rank =  (((RankRuleContext)_localctx).ID!=null?((RankRuleContext)_localctx).ID.getText():null);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereExpressionContext extends ParserRuleContext {
		public List<Condition> conditions;
		public WexpressionContext left;
		public WexpressionContext right;
		public List<WexpressionContext> wexpression() {
			return getRuleContexts(WexpressionContext.class);
		}
		public WexpressionContext wexpression(int i) {
			return getRuleContext(WexpressionContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(SPQLParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(SPQLParser.AND, i);
		}
		public WhereExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterWhereExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitWhereExpression(this);
		}
	}

	public final WhereExpressionContext whereExpression() throws RecognitionException {
		WhereExpressionContext _localctx = new WhereExpressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_whereExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				((WhereExpressionContext)_localctx).conditions =  new ArrayList<Condition>();
				setState(214);
				((WhereExpressionContext)_localctx).left = wexpression();
				_localctx.conditions.add(((WhereExpressionContext)_localctx).left.condition);
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AND) {
					{
						{
							setState(216);
							match(AND);
							setState(217);
							((WhereExpressionContext)_localctx).right = wexpression();
							_localctx.conditions.add(((WhereExpressionContext)_localctx).right.condition);
						}
					}
					setState(224);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WexpressionContext extends ParserRuleContext {
		public Condition condition;
		public CoordinateConditionContext bc;
		public EventDateConditionContext dc;
		public CoordinateConditionContext coordinateCondition() {
			return getRuleContext(CoordinateConditionContext.class,0);
		}
		public EventDateConditionContext eventDateCondition() {
			return getRuleContext(EventDateConditionContext.class,0);
		}
		public WexpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wexpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterWexpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitWexpression(this);
		}
	}

	public final WexpressionContext wexpression() throws RecognitionException {
		WexpressionContext _localctx = new WexpressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_wexpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(231);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__5:
				{
					setState(225);
					((WexpressionContext)_localctx).bc = coordinateCondition();
					((WexpressionContext)_localctx).condition =  ((WexpressionContext)_localctx).bc.condition;
				}
				break;
				case T__6:
				{
					setState(228);
					((WexpressionContext)_localctx).dc = eventDateCondition();
					((WexpressionContext)_localctx).condition =  ((WexpressionContext)_localctx).dc.condition;
				}
				break;
				default:
					throw new NoViableAltException(this);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CoordinateConditionContext extends ParserRuleContext {
		public Condition condition;
		public RelationalOperatorContext o;
		public CoordinateRuleContext c;
		public RelationalOperatorContext relationalOperator() {
			return getRuleContext(RelationalOperatorContext.class,0);
		}
		public CoordinateRuleContext coordinateRule() {
			return getRuleContext(CoordinateRuleContext.class,0);
		}
		public CoordinateConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinateCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterCoordinateCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitCoordinateCondition(this);
		}
	}

	public final CoordinateConditionContext coordinateCondition() throws RecognitionException {
		CoordinateConditionContext _localctx = new CoordinateConditionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_coordinateCondition);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(233);
				match(T__5);
				setState(234);
				((CoordinateConditionContext)_localctx).o = relationalOperator();
				setState(235);
				((CoordinateConditionContext)_localctx).c = coordinateRule();
				((CoordinateConditionContext)_localctx).condition =  new Condition(COORDINATE,((CoordinateConditionContext)_localctx).o.operator,((CoordinateConditionContext)_localctx).c.coordinate);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EventDateConditionContext extends ParserRuleContext {
		public Condition condition;
		public RelationalOperatorContext o;
		public DateRuleContext d;
		public RelationalOperatorContext relationalOperator() {
			return getRuleContext(RelationalOperatorContext.class,0);
		}
		public DateRuleContext dateRule() {
			return getRuleContext(DateRuleContext.class,0);
		}
		public EventDateConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventDateCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterEventDateCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitEventDateCondition(this);
		}
	}

	public final EventDateConditionContext eventDateCondition() throws RecognitionException {
		EventDateConditionContext _localctx = new EventDateConditionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_eventDateCondition);
		try {
			enterOuterAlt(_localctx, 1);

			setState(238);
			match(T__6);
			setState(239);
			((EventDateConditionContext)_localctx).o = relationalOperator();
			setState(240);
			((EventDateConditionContext)_localctx).d = dateRule();
			((EventDateConditionContext)_localctx).condition =  new Condition(EVENT_DATE,((EventDateConditionContext)_localctx).o.operator,((EventDateConditionContext)_localctx).d.date);

		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationalOperatorContext extends ParserRuleContext {
		public RelationalOperator operator;
		public RelationalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterRelationalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitRelationalOperator(this);
		}
	}

	public final RelationalOperatorContext relationalOperator() throws RecognitionException {
		RelationalOperatorContext _localctx = new RelationalOperatorContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_relationalOperator);
		try {
			setState(253);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
					setState(243);
					match(T__7);
					((RelationalOperatorContext)_localctx).operator = LT;
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
					setState(245);
					match(T__8);
					((RelationalOperatorContext)_localctx).operator = LE;
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
					setState(247);
					match(T__9);
					((RelationalOperatorContext)_localctx).operator = EQ;
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
					setState(249);
					match(T__10);
					((RelationalOperatorContext)_localctx).operator = GT;
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
					setState(251);
					match(T__11);
					((RelationalOperatorContext)_localctx).operator = GE;
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DateRuleContext extends ParserRuleContext {
		public ParserDate date;
		public Token STRING;
		public TerminalNode STRING() { return getToken(SPQLParser.STRING, 0); }
		public DateRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dateRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterDateRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitDateRule(this);
		}
	}

	public final DateRuleContext dateRule() throws RecognitionException {
		DateRuleContext _localctx = new DateRuleContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_dateRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(255);
				((DateRuleContext)_localctx).STRING = match(STRING);
				((DateRuleContext)_localctx).date =  new ParserDate((((DateRuleContext)_localctx).STRING!=null?((DateRuleContext)_localctx).STRING.getText():null).substring(1,(((DateRuleContext)_localctx).STRING!=null?((DateRuleContext)_localctx).STRING.getText():null).length()-1));
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CoordinateRuleContext extends ParserRuleContext {
		public ParserCoordinate coordinate;
		public NumberContext lat;
		public NumberContext lon;
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public CoordinateRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinateRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterCoordinateRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitCoordinateRule(this);
		}
	}

	public final CoordinateRuleContext coordinateRule() throws RecognitionException {
		CoordinateRuleContext _localctx = new CoordinateRuleContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_coordinateRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(258);
				((CoordinateRuleContext)_localctx).lat = number();
				setState(259);
				match(T__0);
				setState(260);
				((CoordinateRuleContext)_localctx).lon = number();
				((CoordinateRuleContext)_localctx).coordinate =  new ParserCoordinate((((CoordinateRuleContext)_localctx).lat!=null?_input.getText(((CoordinateRuleContext)_localctx).lat.start,((CoordinateRuleContext)_localctx).lat.stop):null), (((CoordinateRuleContext)_localctx).lon!=null?_input.getText(((CoordinateRuleContext)_localctx).lon.start,((CoordinateRuleContext)_localctx).lon.stop):null));
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnExpressionContext extends ParserRuleContext {
		public ReturnType returnType;
		public TerminalNode PRODUCT() { return getToken(SPQLParser.PRODUCT, 0); }
		public TerminalNode OCCURRENCE() { return getToken(SPQLParser.OCCURRENCE, 0); }
		public TerminalNode TAXON() { return getToken(SPQLParser.TAXON, 0); }
		public ReturnExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterReturnExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitReturnExpression(this);
		}
	}

	public final ReturnExpressionContext returnExpression() throws RecognitionException {
		ReturnExpressionContext _localctx = new ReturnExpressionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_returnExpression);
		try {
			setState(269);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PRODUCT:
				enterOuterAlt(_localctx, 1);
				{
					setState(263);
					match(PRODUCT);
					((ReturnExpressionContext)_localctx).returnType =  ReturnType.PRODUCT;
				}
				break;
			case OCCURRENCE:
				enterOuterAlt(_localctx, 2);
				{
					setState(265);
					match(OCCURRENCE);
					((ReturnExpressionContext)_localctx).returnType =  ReturnType.OCCURRENCE;
				}
				break;
			case TAXON:
				enterOuterAlt(_localctx, 3);
				{
					setState(267);
					match(TAXON);
					((ReturnExpressionContext)_localctx).returnType =  ReturnType.TAXON;
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HavingExpressionContext extends ParserRuleContext {
		public HavingExpression expression;
		public ExpressionRuleContext e;
		public ExpressionRuleContext expressionRule() {
			return getRuleContext(ExpressionRuleContext.class,0);
		}
		public HavingExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_havingExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterHavingExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitHavingExpression(this);
		}
	}

	public final HavingExpressionContext havingExpression() throws RecognitionException {
		HavingExpressionContext _localctx = new HavingExpressionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_havingExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(271);
				((HavingExpressionContext)_localctx).e = expressionRule();
				((HavingExpressionContext)_localctx).expression =  new HavingExpression((((HavingExpressionContext)_localctx).e!=null?_input.getText(((HavingExpressionContext)_localctx).e.start,((HavingExpressionContext)_localctx).e.stop):null));
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public Unsigned_numberContext unsigned_number() {
			return getRuleContext(Unsigned_numberContext.class,0);
		}
		public Unary_operatorContext unary_operator() {
			return getRuleContext(Unary_operatorContext.class,0);
		}
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(275);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__12 || _la==T__13) {
					{
						setState(274);
						unary_operator();
					}
				}

				setState(277);
				unsigned_number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_operatorContext extends ParserRuleContext {
		public Unary_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterUnary_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitUnary_operator(this);
		}
	}

	public final Unary_operatorContext unary_operator() throws RecognitionException {
		Unary_operatorContext _localctx = new Unary_operatorContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_unary_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(279);
				_la = _input.LA(1);
				if ( !(_la==T__12 || _la==T__13) ) {
					_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unsigned_numberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SPQLParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(SPQLParser.FLOAT, 0); }
		public Unsigned_numberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unsigned_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterUnsigned_number(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitUnsigned_number(this);
		}
	}

	public final Unsigned_numberContext unsigned_number() throws RecognitionException {
		Unsigned_numberContext _localctx = new Unsigned_numberContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_unsigned_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(281);
				_la = _input.LA(1);
				if ( !(_la==INT || _la==FLOAT) ) {
					_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionRuleContext extends ParserRuleContext {
		public List<ConditionalAndExpressionContext> conditionalAndExpression() {
			return getRuleContexts(ConditionalAndExpressionContext.class);
		}
		public ConditionalAndExpressionContext conditionalAndExpression(int i) {
			return getRuleContext(ConditionalAndExpressionContext.class,i);
		}
		public ExpressionRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterExpressionRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitExpressionRule(this);
		}
	}

	public final ExpressionRuleContext expressionRule() throws RecognitionException {
		ExpressionRuleContext _localctx = new ExpressionRuleContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_expressionRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(283);
				conditionalAndExpression();
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__14) {
					{
						{
							setState(284);
							match(T__14);
							setState(285);
							conditionalAndExpression();
						}
					}
					setState(290);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParExpressionContext extends ParserRuleContext {
		public ExpressionRuleContext expressionRule() {
			return getRuleContext(ExpressionRuleContext.class,0);
		}
		public ParExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterParExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitParExpression(this);
		}
	}

	public final ParExpressionContext parExpression() throws RecognitionException {
		ParExpressionContext _localctx = new ParExpressionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_parExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(291);
				match(T__15);
				setState(292);
				expressionRule();
				setState(293);
				match(T__16);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionalAndExpressionContext extends ParserRuleContext {
		public List<EqualityExpressionContext> equalityExpression() {
			return getRuleContexts(EqualityExpressionContext.class);
		}
		public EqualityExpressionContext equalityExpression(int i) {
			return getRuleContext(EqualityExpressionContext.class,i);
		}
		public ConditionalAndExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalAndExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterConditionalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitConditionalAndExpression(this);
		}
	}

	public final ConditionalAndExpressionContext conditionalAndExpression() throws RecognitionException {
		ConditionalAndExpressionContext _localctx = new ConditionalAndExpressionContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_conditionalAndExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(295);
				equalityExpression();
				setState(300);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__17) {
					{
						{
							setState(296);
							match(T__17);
							setState(297);
							equalityExpression();
						}
					}
					setState(302);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EqualityExpressionContext extends ParserRuleContext {
		public List<RelationalExpressionContext> relationalExpression() {
			return getRuleContexts(RelationalExpressionContext.class);
		}
		public RelationalExpressionContext relationalExpression(int i) {
			return getRuleContext(RelationalExpressionContext.class,i);
		}
		public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equalityExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitEqualityExpression(this);
		}
	}

	public final EqualityExpressionContext equalityExpression() throws RecognitionException {
		EqualityExpressionContext _localctx = new EqualityExpressionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_equalityExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(303);
				relationalExpression();
				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9 || _la==T__18) {
					{
						{
							setState(304);
							_la = _input.LA(1);
							if ( !(_la==T__9 || _la==T__18) ) {
								_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							setState(305);
							relationalExpression();
						}
					}
					setState(310);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationalExpressionContext extends ParserRuleContext {
		public List<AdditiveExpressionContext> additiveExpression() {
			return getRuleContexts(AdditiveExpressionContext.class);
		}
		public AdditiveExpressionContext additiveExpression(int i) {
			return getRuleContext(AdditiveExpressionContext.class,i);
		}
		public List<RelationalOpContext> relationalOp() {
			return getRuleContexts(RelationalOpContext.class);
		}
		public RelationalOpContext relationalOp(int i) {
			return getRuleContext(RelationalOpContext.class,i);
		}
		public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitRelationalExpression(this);
		}
	}

	public final RelationalExpressionContext relationalExpression() throws RecognitionException {
		RelationalExpressionContext _localctx = new RelationalExpressionContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_relationalExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(311);
				additiveExpression();
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7 || _la==T__10) {
					{
						{
							setState(312);
							relationalOp();
							setState(313);
							additiveExpression();
						}
					}
					setState(319);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationalOpContext extends ParserRuleContext {
		public RelationalOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterRelationalOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitRelationalOp(this);
		}
	}

	public final RelationalOpContext relationalOp() throws RecognitionException {
		RelationalOpContext _localctx = new RelationalOpContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_relationalOp);
		try {
			setState(326);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
					setState(320);
					match(T__7);
					setState(321);
					match(T__19);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
					setState(322);
					match(T__10);
					setState(323);
					match(T__19);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
					setState(324);
					match(T__7);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
					setState(325);
					match(T__10);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AdditiveExpressionContext extends ParserRuleContext {
		public List<MultiplicativeExpressionContext> multiplicativeExpression() {
			return getRuleContexts(MultiplicativeExpressionContext.class);
		}
		public MultiplicativeExpressionContext multiplicativeExpression(int i) {
			return getRuleContext(MultiplicativeExpressionContext.class,i);
		}
		public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitAdditiveExpression(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_additiveExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(328);
				multiplicativeExpression();
				setState(333);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__12 || _la==T__13) {
					{
						{
							setState(329);
							_la = _input.LA(1);
							if ( !(_la==T__12 || _la==T__13) ) {
								_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							setState(330);
							multiplicativeExpression();
						}
					}
					setState(335);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiplicativeExpressionContext extends ParserRuleContext {
		public List<UnaryExpressionContext> unaryExpression() {
			return getRuleContexts(UnaryExpressionContext.class);
		}
		public UnaryExpressionContext unaryExpression(int i) {
			return getRuleContext(UnaryExpressionContext.class,i);
		}
		public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitMultiplicativeExpression(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_multiplicativeExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(336);
				unaryExpression();
				setState(341);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) {
					{
						{
							setState(337);
							_la = _input.LA(1);
							if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) ) {
								_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							setState(338);
							unaryExpression();
						}
					}
					setState(343);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExpressionContext extends ParserRuleContext {
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() {
			return getRuleContext(UnaryExpressionNotPlusMinusContext.class,0);
		}
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitUnaryExpression(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_unaryExpression);
		try {
			setState(353);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__12:
				enterOuterAlt(_localctx, 1);
				{
					setState(344);
					match(T__12);
					setState(345);
					unaryExpression();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 2);
				{
					setState(346);
					match(T__13);
					setState(347);
					unaryExpression();
				}
				break;
			case T__23:
				enterOuterAlt(_localctx, 3);
				{
					setState(348);
					match(T__23);
					setState(349);
					unaryExpression();
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 4);
				{
					setState(350);
					match(T__24);
					setState(351);
					unaryExpression();
				}
				break;
			case T__15:
			case T__25:
			case LUCIO:
			case EXL:
			case XPATH:
			case TRUE:
			case FALSE:
			case NULL:
			case ID:
			case INT:
			case FLOAT:
			case STRING:
			case STRING_DOUBLE_QUOTE:
				enterOuterAlt(_localctx, 5);
				{
					setState(352);
					unaryExpressionNotPlusMinus();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExpressionNotPlusMinusContext extends ParserRuleContext {
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpressionNotPlusMinus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterUnaryExpressionNotPlusMinus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitUnaryExpressionNotPlusMinus(this);
		}
	}

	public final UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() throws RecognitionException {
		UnaryExpressionNotPlusMinusContext _localctx = new UnaryExpressionNotPlusMinusContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_unaryExpressionNotPlusMinus);
		try {
			setState(358);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
					setState(355);
					match(T__25);
					setState(356);
					unaryExpression();
				}
				break;
			case T__15:
			case LUCIO:
			case EXL:
			case XPATH:
			case TRUE:
			case FALSE:
			case NULL:
			case ID:
			case INT:
			case FLOAT:
			case STRING:
			case STRING_DOUBLE_QUOTE:
				enterOuterAlt(_localctx, 2);
				{
					setState(357);
					primary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public ParExpressionContext parExpression() {
			return getRuleContext(ParExpressionContext.class,0);
		}
		public CallsContext calls() {
			return getRuleContext(CallsContext.class,0);
		}
		public Xpath_functionContext xpath_function() {
			return getRuleContext(Xpath_functionContext.class,0);
		}
		public Exl_functionContext exl_function() {
			return getRuleContext(Exl_functionContext.class,0);
		}
		public Lucio_functionContext lucio_function() {
			return getRuleContext(Lucio_functionContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitPrimary(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(366);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__15:
				{
					setState(360);
					parExpression();
				}
				break;
				case ID:
				{
					setState(361);
					calls();
				}
				break;
				case XPATH:
				{
					setState(362);
					xpath_function();
				}
				break;
				case EXL:
				{
					setState(363);
					exl_function();
				}
				break;
				case LUCIO:
				{
					setState(364);
					lucio_function();
				}
				break;
				case TRUE:
				case FALSE:
				case NULL:
				case INT:
				case FLOAT:
				case STRING:
				case STRING_DOUBLE_QUOTE:
				{
					setState(365);
					literal();
				}
				break;
				default:
					throw new NoViableAltException(this);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CallsContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(SPQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(SPQLParser.ID, i);
		}
		public CallsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calls; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterCalls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitCalls(this);
		}
	}

	public final CallsContext calls() throws RecognitionException {
		CallsContext _localctx = new CallsContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_calls);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(368);
				match(ID);
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__26) {
					{
						{
							setState(369);
							match(T__26);
							setState(370);
							match(ID);
						}
					}
					setState(375);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_functionContext extends ParserRuleContext {
		public TerminalNode XPATH() { return getToken(SPQLParser.XPATH, 0); }
		public TerminalNode STRING_DOUBLE_QUOTE() { return getToken(SPQLParser.STRING_DOUBLE_QUOTE, 0); }
		public Xpath_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterXpath_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitXpath_function(this);
		}
	}

	public final Xpath_functionContext xpath_function() throws RecognitionException {
		Xpath_functionContext _localctx = new Xpath_functionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_xpath_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(376);
				match(XPATH);
				setState(377);
				match(T__15);
				setState(378);
				match(STRING_DOUBLE_QUOTE);
				setState(379);
				match(T__16);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exl_functionContext extends ParserRuleContext {
		public TerminalNode EXL() { return getToken(SPQLParser.EXL, 0); }
		public TerminalNode STRING_DOUBLE_QUOTE() { return getToken(SPQLParser.STRING_DOUBLE_QUOTE, 0); }
		public Exl_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exl_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterExl_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitExl_function(this);
		}
	}

	public final Exl_functionContext exl_function() throws RecognitionException {
		Exl_functionContext _localctx = new Exl_functionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_exl_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(381);
				match(EXL);
				setState(382);
				match(T__15);
				setState(383);
				match(STRING_DOUBLE_QUOTE);
				setState(384);
				match(T__16);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Lucio_functionContext extends ParserRuleContext {
		public TerminalNode LUCIO() { return getToken(SPQLParser.LUCIO, 0); }
		public TerminalNode STRING_DOUBLE_QUOTE() { return getToken(SPQLParser.STRING_DOUBLE_QUOTE, 0); }
		public Lucio_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lucio_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterLucio_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitLucio_function(this);
		}
	}

	public final Lucio_functionContext lucio_function() throws RecognitionException {
		Lucio_functionContext _localctx = new Lucio_functionContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_lucio_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(386);
				match(LUCIO);
				setState(387);
				match(T__15);
				setState(388);
				match(STRING_DOUBLE_QUOTE);
				setState(389);
				match(T__16);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SPQLParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(SPQLParser.FLOAT, 0); }
		public TerminalNode STRING_DOUBLE_QUOTE() { return getToken(SPQLParser.STRING_DOUBLE_QUOTE, 0); }
		public TerminalNode STRING() { return getToken(SPQLParser.STRING, 0); }
		public TerminalNode TRUE() { return getToken(SPQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SPQLParser.FALSE, 0); }
		public TerminalNode NULL() { return getToken(SPQLParser.NULL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPQLListener ) ((SPQLListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(391);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << INT) | (1L << FLOAT) | (1L << STRING) | (1L << STRING_DOUBLE_QUOTE))) != 0)) ) {
					_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
			"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\39\u018c\4\2\t\2\4"+
					"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
					"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
					"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
					"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
					"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\3\2\3\2"+
					"\3\2\3\2\3\2\3\2\3\2\3\2\5\2]\n\2\3\2\3\2\3\2\3\2\5\2c\n\2\3\2\3\2\3\2"+
					"\3\2\5\2i\n\2\3\2\3\2\3\2\3\2\5\2o\n\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3"+
					"\3\3\7\3z\n\3\f\3\16\3}\13\3\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0085\n\4\3\5"+
					"\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u008e\n\5\3\5\3\5\3\5\5\5\u0093\n\5\3\6\3"+
					"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u009f\n\6\3\7\3\7\3\7\3\7\3\7\3"+
					"\7\3\7\7\7\u00a8\n\7\f\7\16\7\u00ab\13\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
					"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00bc\n\n\3\13\3\13\3\13\3\13\3\13"+
					"\3\13\5\13\u00c4\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u00cd\n\f\f\f\16"+
					"\f\u00d0\13\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
					"\3\17\7\17\u00df\n\17\f\17\16\17\u00e2\13\17\3\20\3\20\3\20\3\20\3\20"+
					"\3\20\5\20\u00ea\n\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22"+
					"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u0100\n\23\3\24"+
					"\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\5\26"+
					"\u0110\n\26\3\27\3\27\3\27\3\30\5\30\u0116\n\30\3\30\3\30\3\31\3\31\3"+
					"\32\3\32\3\33\3\33\3\33\7\33\u0121\n\33\f\33\16\33\u0124\13\33\3\34\3"+
					"\34\3\34\3\34\3\35\3\35\3\35\7\35\u012d\n\35\f\35\16\35\u0130\13\35\3"+
					"\36\3\36\3\36\7\36\u0135\n\36\f\36\16\36\u0138\13\36\3\37\3\37\3\37\3"+
					"\37\7\37\u013e\n\37\f\37\16\37\u0141\13\37\3 \3 \3 \3 \3 \3 \5 \u0149"+
					"\n \3!\3!\3!\7!\u014e\n!\f!\16!\u0151\13!\3\"\3\"\3\"\7\"\u0156\n\"\f"+
					"\"\16\"\u0159\13\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\5#\u0164\n#\3$\3$\3$\5$"+
					"\u0169\n$\3%\3%\3%\3%\3%\3%\5%\u0171\n%\3&\3&\3&\7&\u0176\n&\f&\16&\u0179"+
					"\13&\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\2\2+\2"+
					"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJL"+
					"NPR\2\t\3\2\4\5\3\2\6\7\3\2\17\20\3\2\64\65\4\2\f\f\25\25\3\2\27\31\5"+
					"\2\60\62\64\6589\u018c\2T\3\2\2\2\4r\3\2\2\2\6\u0084\3\2\2\2\b\u0086\3"+
					"\2\2\2\n\u0094\3\2\2\2\f\u00a0\3\2\2\2\16\u00ac\3\2\2\2\20\u00af\3\2\2"+
					"\2\22\u00b5\3\2\2\2\24\u00bd\3\2\2\2\26\u00c5\3\2\2\2\30\u00d1\3\2\2\2"+
					"\32\u00d4\3\2\2\2\34\u00d7\3\2\2\2\36\u00e9\3\2\2\2 \u00eb\3\2\2\2\"\u00f0"+
					"\3\2\2\2$\u00ff\3\2\2\2&\u0101\3\2\2\2(\u0104\3\2\2\2*\u010f\3\2\2\2,"+
					"\u0111\3\2\2\2.\u0115\3\2\2\2\60\u0119\3\2\2\2\62\u011b\3\2\2\2\64\u011d"+
					"\3\2\2\2\66\u0125\3\2\2\28\u0129\3\2\2\2:\u0131\3\2\2\2<\u0139\3\2\2\2"+
					">\u0148\3\2\2\2@\u014a\3\2\2\2B\u0152\3\2\2\2D\u0163\3\2\2\2F\u0168\3"+
					"\2\2\2H\u0170\3\2\2\2J\u0172\3\2\2\2L\u017a\3\2\2\2N\u017f\3\2\2\2P\u0184"+
					"\3\2\2\2R\u0189\3\2\2\2TU\b\2\1\2UV\7!\2\2VW\5\4\3\2W\\\b\2\1\2XY\7&\2"+
					"\2YZ\5\26\f\2Z[\b\2\1\2[]\3\2\2\2\\X\3\2\2\2\\]\3\2\2\2]b\3\2\2\2^_\7"+
					"\'\2\2_`\5\34\17\2`a\b\2\1\2ac\3\2\2\2b^\3\2\2\2bc\3\2\2\2ch\3\2\2\2d"+
					"e\7(\2\2ef\5*\26\2fg\b\2\1\2gi\3\2\2\2hd\3\2\2\2hi\3\2\2\2in\3\2\2\2j"+
					"k\7,\2\2kl\5,\27\2lm\b\2\1\2mo\3\2\2\2nj\3\2\2\2no\3\2\2\2op\3\2\2\2p"+
					"q\7\2\2\3q\3\3\2\2\2rs\b\3\1\2st\5\6\4\2t{\b\3\1\2uv\7\3\2\2vw\5\6\4\2"+
					"wx\b\3\1\2xz\3\2\2\2yu\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|\5\3\2\2"+
					"\2}{\3\2\2\2~\177\5\b\5\2\177\u0080\b\4\1\2\u0080\u0085\3\2\2\2\u0081"+
					"\u0082\5\n\6\2\u0082\u0083\b\4\1\2\u0083\u0085\3\2\2\2\u0084~\3\2\2\2"+
					"\u0084\u0081\3\2\2\2\u0085\7\3\2\2\2\u0086\u0087\b\5\1\2\u0087\u0088\t"+
					"\2\2\2\u0088\u0089\5\f\7\2\u0089\u008d\b\5\1\2\u008a\u008b\5\20\t\2\u008b"+
					"\u008c\b\5\1\2\u008c\u008e\3\2\2\2\u008d\u008a\3\2\2\2\u008d\u008e\3\2"+
					"\2\2\u008e\u0092\3\2\2\2\u008f\u0090\5\22\n\2\u0090\u0091\b\5\1\2\u0091"+
					"\u0093\3\2\2\2\u0092\u008f\3\2\2\2\u0092\u0093\3\2\2\2\u0093\t\3\2\2\2"+
					"\u0094\u0095\b\6\1\2\u0095\u0096\t\3\2\2\u0096\u0097\5\f\7\2\u0097\u0098"+
					"\b\6\1\2\u0098\u0099\5\24\13\2\u0099\u009a\b\6\1\2\u009a\u009e\3\2\2\2"+
					"\u009b\u009c\5\22\n\2\u009c\u009d\b\6\1\2\u009d\u009f\3\2\2\2\u009e\u009b"+
					"\3\2\2\2\u009e\u009f\3\2\2\2\u009f\13\3\2\2\2\u00a0\u00a1\b\7\1\2\u00a1"+
					"\u00a2\5\16\b\2\u00a2\u00a9\b\7\1\2\u00a3\u00a4\7\3\2\2\u00a4\u00a5\5"+
					"\16\b\2\u00a5\u00a6\b\7\1\2\u00a6\u00a8\3\2\2\2\u00a7\u00a3\3\2\2\2\u00a8"+
					"\u00ab\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\r\3\2\2\2"+
					"\u00ab\u00a9\3\2\2\2\u00ac\u00ad\78\2\2\u00ad\u00ae\b\b\1\2\u00ae\17\3"+
					"\2\2\2\u00af\u00b0\b\t\1\2\u00b0\u00b1\7$\2\2\u00b1\u00b2\7%\2\2\u00b2"+
					"\u00b3\5\30\r\2\u00b3\u00b4\b\t\1\2\u00b4\21\3\2\2\2\u00b5\u00b6\b\n\1"+
					"\2\u00b6\u00bb\7\"\2\2\u00b7\u00b8\7%\2\2\u00b8\u00b9\5\26\f\2\u00b9\u00ba"+
					"\b\n\1\2\u00ba\u00bc\3\2\2\2\u00bb\u00b7\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
					"\23\3\2\2\2\u00bd\u00be\b\13\1\2\u00be\u00c3\7#\2\2\u00bf\u00c0\7%\2\2"+
					"\u00c0\u00c1\5\26\f\2\u00c1\u00c2\b\13\1\2\u00c2\u00c4\3\2\2\2\u00c3\u00bf"+
					"\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\25\3\2\2\2\u00c5\u00c6\b\f\1\2\u00c6"+
					"\u00c7\5\30\r\2\u00c7\u00ce\b\f\1\2\u00c8\u00c9\7\3\2\2\u00c9\u00ca\5"+
					"\30\r\2\u00ca\u00cb\b\f\1\2\u00cb\u00cd\3\2\2\2\u00cc\u00c8\3\2\2\2\u00cd"+
					"\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\27\3\2\2"+
					"\2\u00d0\u00ce\3\2\2\2\u00d1\u00d2\7\63\2\2\u00d2\u00d3\b\r\1\2\u00d3"+
					"\31\3\2\2\2\u00d4\u00d5\7\63\2\2\u00d5\u00d6\b\16\1\2\u00d6\33\3\2\2\2"+
					"\u00d7\u00d8\b\17\1\2\u00d8\u00d9\5\36\20\2\u00d9\u00e0\b\17\1\2\u00da"+
					"\u00db\7)\2\2\u00db\u00dc\5\36\20\2\u00dc\u00dd\b\17\1\2\u00dd\u00df\3"+
					"\2\2\2\u00de\u00da\3\2\2\2\u00df\u00e2\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0"+
					"\u00e1\3\2\2\2\u00e1\35\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3\u00e4\5 \21"+
					"\2\u00e4\u00e5\b\20\1\2\u00e5\u00ea\3\2\2\2\u00e6\u00e7\5\"\22\2\u00e7"+
					"\u00e8\b\20\1\2\u00e8\u00ea\3\2\2\2\u00e9\u00e3\3\2\2\2\u00e9\u00e6\3"+
					"\2\2\2\u00ea\37\3\2\2\2\u00eb\u00ec\7\b\2\2\u00ec\u00ed\5$\23\2\u00ed"+
					"\u00ee\5(\25\2\u00ee\u00ef\b\21\1\2\u00ef!\3\2\2\2\u00f0\u00f1\7\t\2\2"+
					"\u00f1\u00f2\5$\23\2\u00f2\u00f3\5&\24\2\u00f3\u00f4\b\22\1\2\u00f4#\3"+
					"\2\2\2\u00f5\u00f6\7\n\2\2\u00f6\u0100\b\23\1\2\u00f7\u00f8\7\13\2\2\u00f8"+
					"\u0100\b\23\1\2\u00f9\u00fa\7\f\2\2\u00fa\u0100\b\23\1\2\u00fb\u00fc\7"+
					"\r\2\2\u00fc\u0100\b\23\1\2\u00fd\u00fe\7\16\2\2\u00fe\u0100\b\23\1\2"+
					"\u00ff\u00f5\3\2\2\2\u00ff\u00f7\3\2\2\2\u00ff\u00f9\3\2\2\2\u00ff\u00fb"+
					"\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100%\3\2\2\2\u0101\u0102\78\2\2\u0102\u0103"+
					"\b\24\1\2\u0103\'\3\2\2\2\u0104\u0105\5.\30\2\u0105\u0106\7\3\2\2\u0106"+
					"\u0107\5.\30\2\u0107\u0108\b\25\1\2\u0108)\3\2\2\2\u0109\u010a\7-\2\2"+
					"\u010a\u0110\b\26\1\2\u010b\u010c\7.\2\2\u010c\u0110\b\26\1\2\u010d\u010e"+
					"\7/\2\2\u010e\u0110\b\26\1\2\u010f\u0109\3\2\2\2\u010f\u010b\3\2\2\2\u010f"+
					"\u010d\3\2\2\2\u0110+\3\2\2\2\u0111\u0112\5\64\33\2\u0112\u0113\b\27\1"+
					"\2\u0113-\3\2\2\2\u0114\u0116\5\60\31\2\u0115\u0114\3\2\2\2\u0115\u0116"+
					"\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\5\62\32\2\u0118/\3\2\2\2\u0119"+
					"\u011a\t\4\2\2\u011a\61\3\2\2\2\u011b\u011c\t\5\2\2\u011c\63\3\2\2\2\u011d"+
					"\u0122\58\35\2\u011e\u011f\7\21\2\2\u011f\u0121\58\35\2\u0120\u011e\3"+
					"\2\2\2\u0121\u0124\3\2\2\2\u0122\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123"+
					"\65\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u0126\7\22\2\2\u0126\u0127\5\64"+
					"\33\2\u0127\u0128\7\23\2\2\u0128\67\3\2\2\2\u0129\u012e\5:\36\2\u012a"+
					"\u012b\7\24\2\2\u012b\u012d\5:\36\2\u012c\u012a\3\2\2\2\u012d\u0130\3"+
					"\2\2\2\u012e\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f9\3\2\2\2\u0130\u012e"+
					"\3\2\2\2\u0131\u0136\5<\37\2\u0132\u0133\t\6\2\2\u0133\u0135\5<\37\2\u0134"+
					"\u0132\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2"+
					"\2\2\u0137;\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013f\5@!\2\u013a\u013b"+
					"\5> \2\u013b\u013c\5@!\2\u013c\u013e\3\2\2\2\u013d\u013a\3\2\2\2\u013e"+
					"\u0141\3\2\2\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140=\3\2\2\2"+
					"\u0141\u013f\3\2\2\2\u0142\u0143\7\n\2\2\u0143\u0149\7\26\2\2\u0144\u0145"+
					"\7\r\2\2\u0145\u0149\7\26\2\2\u0146\u0149\7\n\2\2\u0147\u0149\7\r\2\2"+
					"\u0148\u0142\3\2\2\2\u0148\u0144\3\2\2\2\u0148\u0146\3\2\2\2\u0148\u0147"+
					"\3\2\2\2\u0149?\3\2\2\2\u014a\u014f\5B\"\2\u014b\u014c\t\4\2\2\u014c\u014e"+
					"\5B\"\2\u014d\u014b\3\2\2\2\u014e\u0151\3\2\2\2\u014f\u014d\3\2\2\2\u014f"+
					"\u0150\3\2\2\2\u0150A\3\2\2\2\u0151\u014f\3\2\2\2\u0152\u0157\5D#\2\u0153"+
					"\u0154\t\7\2\2\u0154\u0156\5D#\2\u0155\u0153\3\2\2\2\u0156\u0159\3\2\2"+
					"\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158C\3\2\2\2\u0159\u0157"+
					"\3\2\2\2\u015a\u015b\7\17\2\2\u015b\u0164\5D#\2\u015c\u015d\7\20\2\2\u015d"+
					"\u0164\5D#\2\u015e\u015f\7\32\2\2\u015f\u0164\5D#\2\u0160\u0161\7\33\2"+
					"\2\u0161\u0164\5D#\2\u0162\u0164\5F$\2\u0163\u015a\3\2\2\2\u0163\u015c"+
					"\3\2\2\2\u0163\u015e\3\2\2\2\u0163\u0160\3\2\2\2\u0163\u0162\3\2\2\2\u0164"+
					"E\3\2\2\2\u0165\u0166\7\34\2\2\u0166\u0169\5D#\2\u0167\u0169\5H%\2\u0168"+
					"\u0165\3\2\2\2\u0168\u0167\3\2\2\2\u0169G\3\2\2\2\u016a\u0171\5\66\34"+
					"\2\u016b\u0171\5J&\2\u016c\u0171\5L\'\2\u016d\u0171\5N(\2\u016e\u0171"+
					"\5P)\2\u016f\u0171\5R*\2\u0170\u016a\3\2\2\2\u0170\u016b\3\2\2\2\u0170"+
					"\u016c\3\2\2\2\u0170\u016d\3\2\2\2\u0170\u016e\3\2\2\2\u0170\u016f\3\2"+
					"\2\2\u0171I\3\2\2\2\u0172\u0177\7\63\2\2\u0173\u0174\7\35\2\2\u0174\u0176"+
					"\7\63\2\2\u0175\u0173\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2\2\2"+
					"\u0177\u0178\3\2\2\2\u0178K\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017b\7"+
					" \2\2\u017b\u017c\7\22\2\2\u017c\u017d\79\2\2\u017d\u017e\7\23\2\2\u017e"+
					"M\3\2\2\2\u017f\u0180\7\37\2\2\u0180\u0181\7\22\2\2\u0181\u0182\79\2\2"+
					"\u0182\u0183\7\23\2\2\u0183O\3\2\2\2\u0184\u0185\7\36\2\2\u0185\u0186"+
					"\7\22\2\2\u0186\u0187\79\2\2\u0187\u0188\7\23\2\2\u0188Q\3\2\2\2\u0189"+
					"\u018a\t\b\2\2\u018aS\3\2\2\2\37\\bhn{\u0084\u008d\u0092\u009e\u00a9\u00bb"+
					"\u00c3\u00ce\u00e0\u00e9\u00ff\u010f\u0115\u0122\u012e\u0136\u013f\u0148"+
					"\u014f\u0157\u0163\u0168\u0170\u0177";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}