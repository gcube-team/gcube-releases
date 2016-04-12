// $ANTLR 3.5 /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g 2013-08-01 14:41:37

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



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SPQLParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "AS", "COMMENT", "ESC_SEQ", 
		"EXL", "EXPAND", "EXPONENT", "FALSE", "FLOAT", "HAVING", "HEX_DIGIT", 
		"ID", "IN", "INT", "IS", "LUCIO", "NULL", "OCCURRENCE", "OCTAL_ESC", "PRODUCT", 
		"RESOLVE", "RETURN", "SEARCHBY", "STRING", "STRING_DOUBLE_QUOTE", "TAXON", 
		"TRUE", "UNFOLD", "UNICODE_ESC", "WHERE", "WITH", "WS", "XPATH", "'!'", 
		"'!='", "'%'", "'&&'", "'('", "')'", "'*'", "'+'", "'++'", "','", "'-'", 
		"'--'", "'.'", "'/'", "'<'", "'<='", "'='", "'=='", "'>'", "'>='", "'CN'", 
		"'CommonName'", "'SN'", "'ScientificName'", "'coordinate'", "'eventDate'", 
		"'||'"
	};
	public static final int EOF=-1;
	public static final int T__37=37;
	public static final int T__38=38;
	public static final int T__39=39;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int T__51=51;
	public static final int T__52=52;
	public static final int T__53=53;
	public static final int T__54=54;
	public static final int T__55=55;
	public static final int T__56=56;
	public static final int T__57=57;
	public static final int T__58=58;
	public static final int T__59=59;
	public static final int T__60=60;
	public static final int T__61=61;
	public static final int T__62=62;
	public static final int T__63=63;
	public static final int AND=4;
	public static final int AS=5;
	public static final int COMMENT=6;
	public static final int ESC_SEQ=7;
	public static final int EXL=8;
	public static final int EXPAND=9;
	public static final int EXPONENT=10;
	public static final int FALSE=11;
	public static final int FLOAT=12;
	public static final int HAVING=13;
	public static final int HEX_DIGIT=14;
	public static final int ID=15;
	public static final int IN=16;
	public static final int INT=17;
	public static final int IS=18;
	public static final int LUCIO=19;
	public static final int NULL=20;
	public static final int OCCURRENCE=21;
	public static final int OCTAL_ESC=22;
	public static final int PRODUCT=23;
	public static final int RESOLVE=24;
	public static final int RETURN=25;
	public static final int SEARCHBY=26;
	public static final int STRING=27;
	public static final int STRING_DOUBLE_QUOTE=28;
	public static final int TAXON=29;
	public static final int TRUE=30;
	public static final int UNFOLD=31;
	public static final int UNICODE_ESC=32;
	public static final int WHERE=33;
	public static final int WITH=34;
	public static final int WS=35;
	public static final int XPATH=36;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public SPQLParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public SPQLParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return SPQLParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g"; }


	   /* @Override
	    public void reportError(RecognitionException e) throws RecognitionException {
	        throw e;
	    }*/
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
	  }



	// $ANTLR start "query"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:78:1: query returns [Query result] : SEARCHBY t= terms ( IN d= datasources )? ( WHERE we= whereExpression )? ( RETURN rt= returnExpression )? ( HAVING he= havingExpression )? EOF ;
	public final Query query() throws RecognitionException {
		Query result = null;


		List<Term> t =null;
		List<String> d =null;
		List<Condition> we =null;
		ReturnType rt =null;
		HavingExpression he =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:78:30: ( SEARCHBY t= terms ( IN d= datasources )? ( WHERE we= whereExpression )? ( RETURN rt= returnExpression )? ( HAVING he= havingExpression )? EOF )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:78:32: SEARCHBY t= terms ( IN d= datasources )? ( WHERE we= whereExpression )? ( RETURN rt= returnExpression )? ( HAVING he= havingExpression )? EOF
			{
			result = new Query();
			match(input,SEARCHBY,FOLLOW_SEARCHBY_in_query65); 
			pushFollow(FOLLOW_terms_in_query69);
			t=terms();
			state._fsp--;

			result.setTerms(t);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:80:7: ( IN d= datasources )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==IN) ) {
				alt1=1;
			}
			switch (alt1) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:80:8: IN d= datasources
					{
					match(input,IN,FOLLOW_IN_in_query81); 
					pushFollow(FOLLOW_datasources_in_query85);
					d=datasources();
					state._fsp--;

					result.setDatasources(d);
					}
					break;

			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:81:7: ( WHERE we= whereExpression )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==WHERE) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:81:8: WHERE we= whereExpression
					{
					match(input,WHERE,FOLLOW_WHERE_in_query99); 
					pushFollow(FOLLOW_whereExpression_in_query103);
					we=whereExpression();
					state._fsp--;

					result.setWhereExpression(new WhereExpression(we));
					}
					break;

			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:82:7: ( RETURN rt= returnExpression )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==RETURN) ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:82:8: RETURN rt= returnExpression
					{
					match(input,RETURN,FOLLOW_RETURN_in_query117); 
					pushFollow(FOLLOW_returnExpression_in_query121);
					rt=returnExpression();
					state._fsp--;

					result.setReturnType(rt);
					}
					break;

			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:83:7: ( HAVING he= havingExpression )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==HAVING) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:83:8: HAVING he= havingExpression
					{
					match(input,HAVING,FOLLOW_HAVING_in_query134); 
					pushFollow(FOLLOW_havingExpression_in_query138);
					he=havingExpression();
					state._fsp--;

					result.setHavingExpression(he);
					}
					break;

			}

			match(input,EOF,FOLLOW_EOF_in_query150); 
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "query"



	// $ANTLR start "terms"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:87:1: terms returns [List<Term> terms] :t= term ( ',' t= term )* ;
	public final List<Term> terms() throws RecognitionException {
		List<Term> terms = null;


		Term t =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:87:34: (t= term ( ',' t= term )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:87:36: t= term ( ',' t= term )*
			{
			terms = new ArrayList<Term>();
			pushFollow(FOLLOW_term_in_terms185);
			t=term();
			state._fsp--;

			terms.add(t);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:88:37: ( ',' t= term )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==46) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:88:38: ',' t= term
					{
					match(input,46,FOLLOW_46_in_terms190); 
					pushFollow(FOLLOW_term_in_terms194);
					t=term();
					state._fsp--;

					terms.add(t);
					}
					break;

				default :
					break loop5;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return terms;
	}
	// $ANTLR end "terms"



	// $ANTLR start "term"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:90:1: term returns [Term term] : (t= scientificTerms |t= commonNameTerms );
	public final Term term() throws RecognitionException {
		Term term = null;


		Term t =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:90:25: (t= scientificTerms |t= commonNameTerms )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( ((LA6_0 >= 59 && LA6_0 <= 60)) ) {
				alt6=1;
			}
			else if ( ((LA6_0 >= 57 && LA6_0 <= 58)) ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:90:27: t= scientificTerms
					{
					pushFollow(FOLLOW_scientificTerms_in_term217);
					t=scientificTerms();
					state._fsp--;

					term = t;
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:90:60: t= commonNameTerms
					{
					pushFollow(FOLLOW_commonNameTerms_in_term225);
					t=commonNameTerms();
					state._fsp--;

					term = t;
					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return term;
	}
	// $ANTLR end "term"



	// $ANTLR start "scientificTerms"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:92:1: scientificTerms returns [Term term] : ( 'ScientificName' | 'SN' ) w= words (u= unfoldClause )? (e= expandClause )? ;
	public final Term scientificTerms() throws RecognitionException {
		Term term = null;


		List<String> w =null;
		UnfoldClause u =null;
		ExpandClause e =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:92:36: ( ( 'ScientificName' | 'SN' ) w= words (u= unfoldClause )? (e= expandClause )? )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:92:38: ( 'ScientificName' | 'SN' ) w= words (u= unfoldClause )? (e= expandClause )?
			{
			term = new Term(SCIENTIFIC_NAME);
			if ( (input.LA(1) >= 59 && input.LA(1) <= 60) ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			pushFollow(FOLLOW_words_in_scientificTerms249);
			w=words();
			state._fsp--;

			term.setWords(w);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:93:61: (u= unfoldClause )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==UNFOLD) ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:93:62: u= unfoldClause
					{
					pushFollow(FOLLOW_unfoldClause_in_scientificTerms256);
					u=unfoldClause();
					state._fsp--;

					term.setUnfoldClause(u);
					}
					break;

			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:93:115: (e= expandClause )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==EXPAND) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:93:116: e= expandClause
					{
					pushFollow(FOLLOW_expandClause_in_scientificTerms265);
					e=expandClause();
					state._fsp--;

					term.setExpandClause(e);
					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return term;
	}
	// $ANTLR end "scientificTerms"



	// $ANTLR start "commonNameTerms"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:95:1: commonNameTerms returns [Term term] : ( 'CommonName' | 'CN' ) w= words (r= resolveClause ) (e= expandClause )? ;
	public final Term commonNameTerms() throws RecognitionException {
		Term term = null;


		List<String> w =null;
		ResolveClause r =null;
		ExpandClause e =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:95:36: ( ( 'CommonName' | 'CN' ) w= words (r= resolveClause ) (e= expandClause )? )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:95:38: ( 'CommonName' | 'CN' ) w= words (r= resolveClause ) (e= expandClause )?
			{
			term = new Term(COMMON_NAME);
			if ( (input.LA(1) >= 57 && input.LA(1) <= 58) ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			pushFollow(FOLLOW_words_in_commonNameTerms294);
			w=words();
			state._fsp--;

			term.setWords(w);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:96:57: (r= resolveClause )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:96:58: r= resolveClause
			{
			pushFollow(FOLLOW_resolveClause_in_commonNameTerms301);
			r=resolveClause();
			state._fsp--;

			term.setResolveClause(r);
			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:96:112: (e= expandClause )?
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==EXPAND) ) {
				alt9=1;
			}
			switch (alt9) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:96:113: e= expandClause
					{
					pushFollow(FOLLOW_expandClause_in_commonNameTerms309);
					e=expandClause();
					state._fsp--;

					term.setExpandClause(e);
					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return term;
	}
	// $ANTLR end "commonNameTerms"



	// $ANTLR start "words"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:98:1: words returns [List<String> words] :w= word ( ',' w= word )* ;
	public final List<String> words() throws RecognitionException {
		List<String> words = null;


		String w =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:98:36: (w= word ( ',' w= word )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:98:38: w= word ( ',' w= word )*
			{
			words = new ArrayList();
			pushFollow(FOLLOW_word_in_words331);
			w=word();
			state._fsp--;

			words.add(w);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:99:33: ( ',' w= word )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==46) ) {
					int LA10_2 = input.LA(2);
					if ( (LA10_2==STRING) ) {
						alt10=1;
					}

				}

				switch (alt10) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:99:34: ',' w= word
					{
					match(input,46,FOLLOW_46_in_words336); 
					pushFollow(FOLLOW_word_in_words340);
					w=word();
					state._fsp--;

					words.add(w);
					}
					break;

				default :
					break loop10;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return words;
	}
	// $ANTLR end "words"



	// $ANTLR start "word"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:101:1: word returns [String word] : STRING ;
	public final String word() throws RecognitionException {
		String word = null;


		Token STRING1=null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:101:28: ( STRING )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:101:30: STRING
			{
			STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_word358); 
			word = (STRING1!=null?STRING1.getText():null).substring(1,(STRING1!=null?STRING1.getText():null).length()-1);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return word;
	}
	// $ANTLR end "word"



	// $ANTLR start "unfoldClause"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:103:1: unfoldClause returns [UnfoldClause clause] : UNFOLD WITH d= datasource ;
	public final UnfoldClause unfoldClause() throws RecognitionException {
		UnfoldClause clause = null;


		String d =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:103:43: ( UNFOLD WITH d= datasource )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:103:45: UNFOLD WITH d= datasource
			{
			clause = new UnfoldClause();
			match(input,UNFOLD,FOLLOW_UNFOLD_in_unfoldClause382); 
			match(input,WITH,FOLLOW_WITH_in_unfoldClause384); 
			pushFollow(FOLLOW_datasource_in_unfoldClause388);
			d=datasource();
			state._fsp--;

			clause.setDatasource(d);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return clause;
	}
	// $ANTLR end "unfoldClause"



	// $ANTLR start "expandClause"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:106:1: expandClause returns [ExpandClause clause] : EXPAND ( WITH d= datasources )? ;
	public final ExpandClause expandClause() throws RecognitionException {
		ExpandClause clause = null;


		List<String> d =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:106:43: ( EXPAND ( WITH d= datasources )? )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:106:45: EXPAND ( WITH d= datasources )?
			{
			clause = new ExpandClause();
			match(input,EXPAND,FOLLOW_EXPAND_in_expandClause419); 
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:107:22: ( WITH d= datasources )?
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==WITH) ) {
				alt11=1;
			}
			switch (alt11) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:107:23: WITH d= datasources
					{
					match(input,WITH,FOLLOW_WITH_in_expandClause422); 
					pushFollow(FOLLOW_datasources_in_expandClause426);
					d=datasources();
					state._fsp--;

					clause.setDatasources(d);
					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return clause;
	}
	// $ANTLR end "expandClause"



	// $ANTLR start "resolveClause"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:109:1: resolveClause returns [ResolveClause clause] : RESOLVE ( WITH d= datasources )? ;
	public final ResolveClause resolveClause() throws RecognitionException {
		ResolveClause clause = null;


		List<String> d =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:109:45: ( RESOLVE ( WITH d= datasources )? )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:109:47: RESOLVE ( WITH d= datasources )?
			{
			clause = new ResolveClause();
			match(input,RESOLVE,FOLLOW_RESOLVE_in_resolveClause460); 
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:110:23: ( WITH d= datasources )?
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==WITH) ) {
				alt12=1;
			}
			switch (alt12) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:110:24: WITH d= datasources
					{
					match(input,WITH,FOLLOW_WITH_in_resolveClause463); 
					pushFollow(FOLLOW_datasources_in_resolveClause467);
					d=datasources();
					state._fsp--;

					clause.setDatasources(d);
					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return clause;
	}
	// $ANTLR end "resolveClause"



	// $ANTLR start "datasources"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:112:1: datasources returns [List<String> datasources] :d= datasource ( ',' d= datasource )* ;
	public final List<String> datasources() throws RecognitionException {
		List<String> datasources = null;


		String d =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:112:48: (d= datasource ( ',' d= datasource )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:112:51: d= datasource ( ',' d= datasource )*
			{
			datasources = new ArrayList();
			pushFollow(FOLLOW_datasource_in_datasources501);
			d=datasource();
			state._fsp--;

			datasources.add(d);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:113:61: ( ',' d= datasource )*
			loop13:
			while (true) {
				int alt13=2;
				int LA13_0 = input.LA(1);
				if ( (LA13_0==46) ) {
					int LA13_2 = input.LA(2);
					if ( (LA13_2==ID) ) {
						alt13=1;
					}

				}

				switch (alt13) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:113:62: ',' d= datasource
					{
					match(input,46,FOLLOW_46_in_datasources506); 
					pushFollow(FOLLOW_datasource_in_datasources510);
					d=datasource();
					state._fsp--;

					datasources.add(d);
					}
					break;

				default :
					break loop13;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return datasources;
	}
	// $ANTLR end "datasources"



	// $ANTLR start "datasource"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:115:1: datasource returns [String datasource] : ID ;
	public final String datasource() throws RecognitionException {
		String datasource = null;


		Token ID2=null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:115:39: ( ID )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:115:41: ID
			{
			ID2=(Token)match(input,ID,FOLLOW_ID_in_datasource526); 
			datasource = (ID2!=null?ID2.getText():null);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return datasource;
	}
	// $ANTLR end "datasource"



	// $ANTLR start "rank"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:117:1: rank returns [String rank] : ID ;
	public final String rank() throws RecognitionException {
		String rank = null;


		Token ID3=null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:117:27: ( ID )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:117:29: ID
			{
			ID3=(Token)match(input,ID,FOLLOW_ID_in_rank539); 
			rank = (ID3!=null?ID3.getText():null);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return rank;
	}
	// $ANTLR end "rank"



	// $ANTLR start "whereExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:119:1: whereExpression returns [List<Condition> conditions] :left= wexpression ( AND right= wexpression )* ;
	public final List<Condition> whereExpression() throws RecognitionException {
		List<Condition> conditions = null;


		Condition left =null;
		Condition right =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:119:53: (left= wexpression ( AND right= wexpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:119:55: left= wexpression ( AND right= wexpression )*
			{
			conditions = new ArrayList<Condition>();
			pushFollow(FOLLOW_wexpression_in_whereExpression574);
			left=wexpression();
			state._fsp--;

			conditions.add(left);
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:120:65: ( AND right= wexpression )*
			loop14:
			while (true) {
				int alt14=2;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==AND) ) {
					alt14=1;
				}

				switch (alt14) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:120:66: AND right= wexpression
					{
					match(input,AND,FOLLOW_AND_in_whereExpression579); 
					pushFollow(FOLLOW_wexpression_in_whereExpression585);
					right=wexpression();
					state._fsp--;

					conditions.add(right);
					}
					break;

				default :
					break loop14;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return conditions;
	}
	// $ANTLR end "whereExpression"



	// $ANTLR start "wexpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:1: wexpression returns [Condition condition] : (bc= coordinateCondition |dc= eventDateCondition ) ;
	public final Condition wexpression() throws RecognitionException {
		Condition condition = null;


		Condition bc =null;
		Condition dc =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:42: ( (bc= coordinateCondition |dc= eventDateCondition ) )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:45: (bc= coordinateCondition |dc= eventDateCondition )
			{
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:45: (bc= coordinateCondition |dc= eventDateCondition )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==61) ) {
				alt15=1;
			}
			else if ( (LA15_0==62) ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:46: bc= coordinateCondition
					{
					pushFollow(FOLLOW_coordinateCondition_in_wexpression603);
					bc=coordinateCondition();
					state._fsp--;

					condition = bc;
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:121:101: dc= eventDateCondition
					{
					pushFollow(FOLLOW_eventDateCondition_in_wexpression611);
					dc=eventDateCondition();
					state._fsp--;

					condition = dc;
					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "wexpression"



	// $ANTLR start "coordinateCondition"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:123:1: coordinateCondition returns [Condition condition] : 'coordinate' o= relationalOperator c= coordinate ;
	public final Condition coordinateCondition() throws RecognitionException {
		Condition condition = null;


		RelationalOperator o =null;
		ParserCoordinate c =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:123:50: ( 'coordinate' o= relationalOperator c= coordinate )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:123:52: 'coordinate' o= relationalOperator c= coordinate
			{
			match(input,61,FOLLOW_61_in_coordinateCondition625); 
			pushFollow(FOLLOW_relationalOperator_in_coordinateCondition629);
			o=relationalOperator();
			state._fsp--;

			pushFollow(FOLLOW_coordinate_in_coordinateCondition633);
			c=coordinate();
			state._fsp--;

			condition = new Condition(COORDINATE,o,c);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "coordinateCondition"



	// $ANTLR start "eventDateCondition"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:125:1: eventDateCondition returns [Condition condition] : 'eventDate' o= relationalOperator d= date ;
	public final Condition eventDateCondition() throws RecognitionException {
		Condition condition = null;


		RelationalOperator o =null;
		ParserDate d =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:125:49: ( 'eventDate' o= relationalOperator d= date )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:125:51: 'eventDate' o= relationalOperator d= date
			{
			match(input,62,FOLLOW_62_in_eventDateCondition647); 
			pushFollow(FOLLOW_relationalOperator_in_eventDateCondition651);
			o=relationalOperator();
			state._fsp--;

			pushFollow(FOLLOW_date_in_eventDateCondition655);
			d=date();
			state._fsp--;

			condition = new Condition(EVENT_DATE,o,d);
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "eventDateCondition"



	// $ANTLR start "relationalOperator"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:127:1: relationalOperator returns [RelationalOperator operator] : ( '<' | '<=' | '==' | '>' | '>=' );
	public final RelationalOperator relationalOperator() throws RecognitionException {
		RelationalOperator operator = null;


		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:127:57: ( '<' | '<=' | '==' | '>' | '>=' )
			int alt16=5;
			switch ( input.LA(1) ) {
			case 51:
				{
				alt16=1;
				}
				break;
			case 52:
				{
				alt16=2;
				}
				break;
			case 54:
				{
				alt16=3;
				}
				break;
			case 55:
				{
				alt16=4;
				}
				break;
			case 56:
				{
				alt16=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}
			switch (alt16) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:128:13: '<'
					{
					match(input,51,FOLLOW_51_in_relationalOperator681); 
					operator =LT;
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:129:13: '<='
					{
					match(input,52,FOLLOW_52_in_relationalOperator700); 
					operator =LE;
					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:130:13: '=='
					{
					match(input,54,FOLLOW_54_in_relationalOperator719); 
					operator =EQ;
					}
					break;
				case 4 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:131:13: '>'
					{
					match(input,55,FOLLOW_55_in_relationalOperator738); 
					operator =GT;
					}
					break;
				case 5 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:132:13: '>='
					{
					match(input,56,FOLLOW_56_in_relationalOperator757); 
					operator =GE;
					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return operator;
	}
	// $ANTLR end "relationalOperator"



	// $ANTLR start "date"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:134:1: date returns [ParserDate date] : STRING ;
	public final ParserDate date() throws RecognitionException {
		ParserDate date = null;


		Token STRING4=null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:134:32: ( STRING )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:134:34: STRING
			{
			STRING4=(Token)match(input,STRING,FOLLOW_STRING_in_date773); 
			date = new ParserDate((STRING4!=null?STRING4.getText():null).substring(1,(STRING4!=null?STRING4.getText():null).length()-1));
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return date;
	}
	// $ANTLR end "date"



	// $ANTLR start "coordinate"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:136:1: coordinate returns [ParserCoordinate coordinate] : lat= FLOAT ',' lon= FLOAT ;
	public final ParserCoordinate coordinate() throws RecognitionException {
		ParserCoordinate coordinate = null;


		Token lat=null;
		Token lon=null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:136:49: (lat= FLOAT ',' lon= FLOAT )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:136:51: lat= FLOAT ',' lon= FLOAT
			{
			lat=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_coordinate788); 
			match(input,46,FOLLOW_46_in_coordinate790); 
			lon=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_coordinate794); 
			coordinate = new ParserCoordinate((lat!=null?lat.getText():null), (lon!=null?lon.getText():null));
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return coordinate;
	}
	// $ANTLR end "coordinate"



	// $ANTLR start "returnExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:138:1: returnExpression returns [ReturnType returnType] : ( PRODUCT | OCCURRENCE | TAXON );
	public final ReturnType returnExpression() throws RecognitionException {
		ReturnType returnType = null;


		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:138:49: ( PRODUCT | OCCURRENCE | TAXON )
			int alt17=3;
			switch ( input.LA(1) ) {
			case PRODUCT:
				{
				alt17=1;
				}
				break;
			case OCCURRENCE:
				{
				alt17=2;
				}
				break;
			case TAXON:
				{
				alt17=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}
			switch (alt17) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:139:13: PRODUCT
					{
					match(input,PRODUCT,FOLLOW_PRODUCT_in_returnExpression820); 
					returnType = ReturnType.PRODUCT;
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:140:13: OCCURRENCE
					{
					match(input,OCCURRENCE,FOLLOW_OCCURRENCE_in_returnExpression838); 
					returnType = ReturnType.OCCURRENCE;
					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:141:13: TAXON
					{
					match(input,TAXON,FOLLOW_TAXON_in_returnExpression856); 
					returnType = ReturnType.TAXON;
					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return returnType;
	}
	// $ANTLR end "returnExpression"



	// $ANTLR start "havingExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:143:1: havingExpression returns [HavingExpression expression] : e= expression ;
	public final HavingExpression havingExpression() throws RecognitionException {
		HavingExpression expression = null;


		ParserRuleReturnScope e =null;

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:143:56: (e= expression )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:143:58: e= expression
			{
			pushFollow(FOLLOW_expression_in_havingExpression873);
			e=expression();
			state._fsp--;

			expression = new HavingExpression((e!=null?input.toString(e.start,e.stop):null));
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return expression;
	}
	// $ANTLR end "havingExpression"


	public static class expression_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "expression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:145:1: expression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
	public final SPQLParser.expression_return expression() throws RecognitionException {
		SPQLParser.expression_return retval = new SPQLParser.expression_return();
		retval.start = input.LT(1);

		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:146:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:146:9: conditionalAndExpression ( '||' conditionalAndExpression )*
			{
			pushFollow(FOLLOW_conditionalAndExpression_in_expression891);
			conditionalAndExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:147:9: ( '||' conditionalAndExpression )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==63) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:147:10: '||' conditionalAndExpression
					{
					match(input,63,FOLLOW_63_in_expression902); 
					pushFollow(FOLLOW_conditionalAndExpression_in_expression904);
					conditionalAndExpression();
					state._fsp--;

					}
					break;

				default :
					break loop18;
				}
			}

			}

			retval.stop = input.LT(-1);

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expression"



	// $ANTLR start "parExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:151:1: parExpression : '(' expression ')' ;
	public final void parExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:152:5: ( '(' expression ')' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:152:9: '(' expression ')'
			{
			match(input,41,FOLLOW_41_in_parExpression943); 
			pushFollow(FOLLOW_expression_in_parExpression945);
			expression();
			state._fsp--;

			match(input,42,FOLLOW_42_in_parExpression947); 
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "parExpression"



	// $ANTLR start "conditionalAndExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:155:1: conditionalAndExpression : equalityExpression ( '&&' equalityExpression )* ;
	public final void conditionalAndExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:156:5: ( equalityExpression ( '&&' equalityExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:156:9: equalityExpression ( '&&' equalityExpression )*
			{
			pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression967);
			equalityExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:157:9: ( '&&' equalityExpression )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==40) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:157:10: '&&' equalityExpression
					{
					match(input,40,FOLLOW_40_in_conditionalAndExpression979); 
					pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression981);
					equalityExpression();
					state._fsp--;

					}
					break;

				default :
					break loop19;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "conditionalAndExpression"



	// $ANTLR start "equalityExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:161:1: equalityExpression : relationalExpression ( ( '==' | '!=' ) relationalExpression )* ;
	public final void equalityExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:162:5: ( relationalExpression ( ( '==' | '!=' ) relationalExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:162:9: relationalExpression ( ( '==' | '!=' ) relationalExpression )*
			{
			pushFollow(FOLLOW_relationalExpression_in_equalityExpression1012);
			relationalExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:163:9: ( ( '==' | '!=' ) relationalExpression )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==38||LA20_0==54) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:164:13: ( '==' | '!=' ) relationalExpression
					{
					if ( input.LA(1)==38||input.LA(1)==54 ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_relationalExpression_in_equalityExpression1091);
					relationalExpression();
					state._fsp--;

					}
					break;

				default :
					break loop20;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "equalityExpression"



	// $ANTLR start "relationalExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:171:1: relationalExpression : additiveExpression ( relationalOp additiveExpression )* ;
	public final void relationalExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:172:5: ( additiveExpression ( relationalOp additiveExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:172:9: additiveExpression ( relationalOp additiveExpression )*
			{
			pushFollow(FOLLOW_additiveExpression_in_relationalExpression1123);
			additiveExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:173:9: ( relationalOp additiveExpression )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==51||LA21_0==55) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:173:10: relationalOp additiveExpression
					{
					pushFollow(FOLLOW_relationalOp_in_relationalExpression1135);
					relationalOp();
					state._fsp--;

					pushFollow(FOLLOW_additiveExpression_in_relationalExpression1137);
					additiveExpression();
					state._fsp--;

					}
					break;

				default :
					break loop21;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "relationalExpression"



	// $ANTLR start "relationalOp"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:177:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
	public final void relationalOp() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:178:5: ( '<' '=' | '>' '=' | '<' | '>' )
			int alt22=4;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==51) ) {
				int LA22_1 = input.LA(2);
				if ( (LA22_1==53) ) {
					alt22=1;
				}
				else if ( (LA22_1==EXL||(LA22_1 >= FALSE && LA22_1 <= FLOAT)||LA22_1==ID||LA22_1==INT||(LA22_1 >= LUCIO && LA22_1 <= NULL)||(LA22_1 >= STRING && LA22_1 <= STRING_DOUBLE_QUOTE)||LA22_1==TRUE||(LA22_1 >= XPATH && LA22_1 <= 37)||LA22_1==41||(LA22_1 >= 44 && LA22_1 <= 45)||(LA22_1 >= 47 && LA22_1 <= 48)) ) {
					alt22=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 22, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA22_0==55) ) {
				int LA22_2 = input.LA(2);
				if ( (LA22_2==53) ) {
					alt22=2;
				}
				else if ( (LA22_2==EXL||(LA22_2 >= FALSE && LA22_2 <= FLOAT)||LA22_2==ID||LA22_2==INT||(LA22_2 >= LUCIO && LA22_2 <= NULL)||(LA22_2 >= STRING && LA22_2 <= STRING_DOUBLE_QUOTE)||LA22_2==TRUE||(LA22_2 >= XPATH && LA22_2 <= 37)||LA22_2==41||(LA22_2 >= 44 && LA22_2 <= 45)||(LA22_2 >= 47 && LA22_2 <= 48)) ) {
					alt22=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 22, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}

			switch (alt22) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:178:10: '<' '='
					{
					match(input,51,FOLLOW_51_in_relationalOp1169); 
					match(input,53,FOLLOW_53_in_relationalOp1171); 
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:179:10: '>' '='
					{
					match(input,55,FOLLOW_55_in_relationalOp1183); 
					match(input,53,FOLLOW_53_in_relationalOp1185); 
					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:180:9: '<'
					{
					match(input,51,FOLLOW_51_in_relationalOp1196); 
					}
					break;
				case 4 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:181:9: '>'
					{
					match(input,55,FOLLOW_55_in_relationalOp1207); 
					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "relationalOp"



	// $ANTLR start "additiveExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:184:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
	public final void additiveExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:185:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:185:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1228);
			multiplicativeExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:186:9: ( ( '+' | '-' ) multiplicativeExpression )*
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0==44||LA23_0==47) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:187:13: ( '+' | '-' ) multiplicativeExpression
					{
					if ( input.LA(1)==44||input.LA(1)==47 ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1304);
					multiplicativeExpression();
					state._fsp--;

					}
					break;

				default :
					break loop23;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "additiveExpression"



	// $ANTLR start "multiplicativeExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:194:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
	public final void multiplicativeExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:195:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:196:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1342);
			unaryExpression();
			state._fsp--;

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:197:9: ( ( '*' | '/' | '%' ) unaryExpression )*
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==39||LA24_0==43||LA24_0==50) ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:198:13: ( '*' | '/' | '%' ) unaryExpression
					{
					if ( input.LA(1)==39||input.LA(1)==43||input.LA(1)==50 ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1437);
					unaryExpression();
					state._fsp--;

					}
					break;

				default :
					break loop24;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "multiplicativeExpression"



	// $ANTLR start "unaryExpression"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:210:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
	public final void unaryExpression() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:211:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
			int alt25=5;
			switch ( input.LA(1) ) {
			case 44:
				{
				alt25=1;
				}
				break;
			case 47:
				{
				alt25=2;
				}
				break;
			case 45:
				{
				alt25=3;
				}
				break;
			case 48:
				{
				alt25=4;
				}
				break;
			case EXL:
			case FALSE:
			case FLOAT:
			case ID:
			case INT:
			case LUCIO:
			case NULL:
			case STRING:
			case STRING_DOUBLE_QUOTE:
			case TRUE:
			case XPATH:
			case 37:
			case 41:
				{
				alt25=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 25, 0, input);
				throw nvae;
			}
			switch (alt25) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:211:9: '+' unaryExpression
					{
					match(input,44,FOLLOW_44_in_unaryExpression1469); 
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression1472);
					unaryExpression();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:212:9: '-' unaryExpression
					{
					match(input,47,FOLLOW_47_in_unaryExpression1482); 
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression1484);
					unaryExpression();
					state._fsp--;

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:213:9: '++' unaryExpression
					{
					match(input,45,FOLLOW_45_in_unaryExpression1494); 
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression1496);
					unaryExpression();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:214:9: '--' unaryExpression
					{
					match(input,48,FOLLOW_48_in_unaryExpression1506); 
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression1508);
					unaryExpression();
					state._fsp--;

					}
					break;
				case 5 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:215:9: unaryExpressionNotPlusMinus
					{
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1518);
					unaryExpressionNotPlusMinus();
					state._fsp--;

					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "unaryExpression"



	// $ANTLR start "unaryExpressionNotPlusMinus"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:218:1: unaryExpressionNotPlusMinus : ( '!' unaryExpression | primary );
	public final void unaryExpressionNotPlusMinus() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:219:5: ( '!' unaryExpression | primary )
			int alt26=2;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==37) ) {
				alt26=1;
			}
			else if ( (LA26_0==EXL||(LA26_0 >= FALSE && LA26_0 <= FLOAT)||LA26_0==ID||LA26_0==INT||(LA26_0 >= LUCIO && LA26_0 <= NULL)||(LA26_0 >= STRING && LA26_0 <= STRING_DOUBLE_QUOTE)||LA26_0==TRUE||LA26_0==XPATH||LA26_0==41) ) {
				alt26=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}

			switch (alt26) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:219:9: '!' unaryExpression
					{
					match(input,37,FOLLOW_37_in_unaryExpressionNotPlusMinus1538); 
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1540);
					unaryExpression();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:220:9: primary
					{
					pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1550);
					primary();
					state._fsp--;

					}
					break;

			}
		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "unaryExpressionNotPlusMinus"



	// $ANTLR start "primary"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:226:1: primary : ( parExpression | calls | xpath_function | exl_function | lucio_function | literal ) ;
	public final void primary() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:226:8: ( ( parExpression | calls | xpath_function | exl_function | lucio_function | literal ) )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:226:11: ( parExpression | calls | xpath_function | exl_function | lucio_function | literal )
			{
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:226:11: ( parExpression | calls | xpath_function | exl_function | lucio_function | literal )
			int alt27=6;
			switch ( input.LA(1) ) {
			case 41:
				{
				alt27=1;
				}
				break;
			case ID:
				{
				alt27=2;
				}
				break;
			case XPATH:
				{
				alt27=3;
				}
				break;
			case EXL:
				{
				alt27=4;
				}
				break;
			case LUCIO:
				{
				alt27=5;
				}
				break;
			case FALSE:
			case FLOAT:
			case INT:
			case NULL:
			case STRING:
			case STRING_DOUBLE_QUOTE:
			case TRUE:
				{
				alt27=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}
			switch (alt27) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:226:13: parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary1567);
					parExpression();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:227:9: calls
					{
					pushFollow(FOLLOW_calls_in_primary1577);
					calls();
					state._fsp--;

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:228:9: xpath_function
					{
					pushFollow(FOLLOW_xpath_function_in_primary1587);
					xpath_function();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:229:9: exl_function
					{
					pushFollow(FOLLOW_exl_function_in_primary1597);
					exl_function();
					state._fsp--;

					}
					break;
				case 5 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:230:9: lucio_function
					{
					pushFollow(FOLLOW_lucio_function_in_primary1607);
					lucio_function();
					state._fsp--;

					}
					break;
				case 6 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:231:9: literal
					{
					pushFollow(FOLLOW_literal_in_primary1617);
					literal();
					state._fsp--;

					}
					break;

			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "primary"



	// $ANTLR start "calls"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:234:1: calls : ID ( '.' ID )* ;
	public final void calls() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:234:6: ( ID ( '.' ID )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:234:8: ID ( '.' ID )*
			{
			match(input,ID,FOLLOW_ID_in_calls1630); 
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:234:11: ( '.' ID )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==49) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:234:12: '.' ID
					{
					match(input,49,FOLLOW_49_in_calls1633); 
					match(input,ID,FOLLOW_ID_in_calls1635); 
					}
					break;

				default :
					break loop28;
				}
			}

			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "calls"



	// $ANTLR start "xpath_function"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:236:1: xpath_function : XPATH '(' STRING_DOUBLE_QUOTE ')' ;
	public final void xpath_function() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:236:15: ( XPATH '(' STRING_DOUBLE_QUOTE ')' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:236:17: XPATH '(' STRING_DOUBLE_QUOTE ')'
			{
			match(input,XPATH,FOLLOW_XPATH_in_xpath_function1644); 
			match(input,41,FOLLOW_41_in_xpath_function1646); 
			match(input,STRING_DOUBLE_QUOTE,FOLLOW_STRING_DOUBLE_QUOTE_in_xpath_function1648); 
			match(input,42,FOLLOW_42_in_xpath_function1650); 
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "xpath_function"



	// $ANTLR start "exl_function"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:237:1: exl_function : EXL '(' STRING_DOUBLE_QUOTE ')' ;
	public final void exl_function() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:237:13: ( EXL '(' STRING_DOUBLE_QUOTE ')' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:237:15: EXL '(' STRING_DOUBLE_QUOTE ')'
			{
			match(input,EXL,FOLLOW_EXL_in_exl_function1656); 
			match(input,41,FOLLOW_41_in_exl_function1658); 
			match(input,STRING_DOUBLE_QUOTE,FOLLOW_STRING_DOUBLE_QUOTE_in_exl_function1660); 
			match(input,42,FOLLOW_42_in_exl_function1662); 
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "exl_function"



	// $ANTLR start "lucio_function"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:238:1: lucio_function : LUCIO '(' STRING_DOUBLE_QUOTE ')' ;
	public final void lucio_function() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:238:15: ( LUCIO '(' STRING_DOUBLE_QUOTE ')' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:238:17: LUCIO '(' STRING_DOUBLE_QUOTE ')'
			{
			match(input,LUCIO,FOLLOW_LUCIO_in_lucio_function1668); 
			match(input,41,FOLLOW_41_in_lucio_function1670); 
			match(input,STRING_DOUBLE_QUOTE,FOLLOW_STRING_DOUBLE_QUOTE_in_lucio_function1672); 
			match(input,42,FOLLOW_42_in_lucio_function1674); 
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "lucio_function"



	// $ANTLR start "literal"
	// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:240:1: literal : ( INT | FLOAT | STRING_DOUBLE_QUOTE | STRING | TRUE | FALSE | NULL );
	public final void literal() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:241:5: ( INT | FLOAT | STRING_DOUBLE_QUOTE | STRING | TRUE | FALSE | NULL )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
			{
			if ( (input.LA(1) >= FALSE && input.LA(1) <= FLOAT)||input.LA(1)==INT||input.LA(1)==NULL||(input.LA(1) >= STRING && input.LA(1) <= STRING_DOUBLE_QUOTE)||input.LA(1)==TRUE ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}

		  catch (RecognitionException exception) {
		    throw exception;
		  }

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "literal"

	// Delegated rules



	public static final BitSet FOLLOW_SEARCHBY_in_query65 = new BitSet(new long[]{0x1E00000000000000L});
	public static final BitSet FOLLOW_terms_in_query69 = new BitSet(new long[]{0x0000000202012000L});
	public static final BitSet FOLLOW_IN_in_query81 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_datasources_in_query85 = new BitSet(new long[]{0x0000000202002000L});
	public static final BitSet FOLLOW_WHERE_in_query99 = new BitSet(new long[]{0x6000000000000000L});
	public static final BitSet FOLLOW_whereExpression_in_query103 = new BitSet(new long[]{0x0000000002002000L});
	public static final BitSet FOLLOW_RETURN_in_query117 = new BitSet(new long[]{0x0000000020A00000L});
	public static final BitSet FOLLOW_returnExpression_in_query121 = new BitSet(new long[]{0x0000000000002000L});
	public static final BitSet FOLLOW_HAVING_in_query134 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_havingExpression_in_query138 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_query150 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_term_in_terms185 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_46_in_terms190 = new BitSet(new long[]{0x1E00000000000000L});
	public static final BitSet FOLLOW_term_in_terms194 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_scientificTerms_in_term217 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_commonNameTerms_in_term225 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_scientificTerms241 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_words_in_scientificTerms249 = new BitSet(new long[]{0x0000000080000202L});
	public static final BitSet FOLLOW_unfoldClause_in_scientificTerms256 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_expandClause_in_scientificTerms265 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_commonNameTerms286 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_words_in_commonNameTerms294 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_resolveClause_in_commonNameTerms301 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_expandClause_in_commonNameTerms309 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_word_in_words331 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_46_in_words336 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_word_in_words340 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_STRING_in_word358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNFOLD_in_unfoldClause382 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_WITH_in_unfoldClause384 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_datasource_in_unfoldClause388 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPAND_in_expandClause419 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_WITH_in_expandClause422 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_datasources_in_expandClause426 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RESOLVE_in_resolveClause460 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_WITH_in_resolveClause463 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_datasources_in_resolveClause467 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_datasource_in_datasources501 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_46_in_datasources506 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_datasource_in_datasources510 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_ID_in_datasource526 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_rank539 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_wexpression_in_whereExpression574 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AND_in_whereExpression579 = new BitSet(new long[]{0x6000000000000000L});
	public static final BitSet FOLLOW_wexpression_in_whereExpression585 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_coordinateCondition_in_wexpression603 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_eventDateCondition_in_wexpression611 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_61_in_coordinateCondition625 = new BitSet(new long[]{0x01D8000000000000L});
	public static final BitSet FOLLOW_relationalOperator_in_coordinateCondition629 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_coordinate_in_coordinateCondition633 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_62_in_eventDateCondition647 = new BitSet(new long[]{0x01D8000000000000L});
	public static final BitSet FOLLOW_relationalOperator_in_eventDateCondition651 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_date_in_eventDateCondition655 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_51_in_relationalOperator681 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_relationalOperator700 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_54_in_relationalOperator719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_55_in_relationalOperator738 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_relationalOperator757 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_date773 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_coordinate788 = new BitSet(new long[]{0x0000400000000000L});
	public static final BitSet FOLLOW_46_in_coordinate790 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_FLOAT_in_coordinate794 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PRODUCT_in_returnExpression820 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OCCURRENCE_in_returnExpression838 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TAXON_in_returnExpression856 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_havingExpression873 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_expression891 = new BitSet(new long[]{0x8000000000000002L});
	public static final BitSet FOLLOW_63_in_expression902 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_expression904 = new BitSet(new long[]{0x8000000000000002L});
	public static final BitSet FOLLOW_41_in_parExpression943 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_expression_in_parExpression945 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_parExpression947 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression967 = new BitSet(new long[]{0x0000010000000002L});
	public static final BitSet FOLLOW_40_in_conditionalAndExpression979 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression981 = new BitSet(new long[]{0x0000010000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_equalityExpression1012 = new BitSet(new long[]{0x0040004000000002L});
	public static final BitSet FOLLOW_set_in_equalityExpression1039 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_relationalExpression_in_equalityExpression1091 = new BitSet(new long[]{0x0040004000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_relationalExpression1123 = new BitSet(new long[]{0x0088000000000002L});
	public static final BitSet FOLLOW_relationalOp_in_relationalExpression1135 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_additiveExpression_in_relationalExpression1137 = new BitSet(new long[]{0x0088000000000002L});
	public static final BitSet FOLLOW_51_in_relationalOp1169 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_relationalOp1171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_55_in_relationalOp1183 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_relationalOp1185 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_51_in_relationalOp1196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_55_in_relationalOp1207 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1228 = new BitSet(new long[]{0x0000900000000002L});
	public static final BitSet FOLLOW_set_in_additiveExpression1254 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1304 = new BitSet(new long[]{0x0000900000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1342 = new BitSet(new long[]{0x0004088000000002L});
	public static final BitSet FOLLOW_set_in_multiplicativeExpression1369 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1437 = new BitSet(new long[]{0x0004088000000002L});
	public static final BitSet FOLLOW_44_in_unaryExpression1469 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1472 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_unaryExpression1482 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1484 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_45_in_unaryExpression1494 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1496 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_48_in_unaryExpression1506 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1508 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1518 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_37_in_unaryExpressionNotPlusMinus1538 = new BitSet(new long[]{0x0001B230581A9900L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1540 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1550 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_primary1567 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_calls_in_primary1577 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_xpath_function_in_primary1587 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_exl_function_in_primary1597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lucio_function_in_primary1607 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary1617 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_calls1630 = new BitSet(new long[]{0x0002000000000002L});
	public static final BitSet FOLLOW_49_in_calls1633 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_ID_in_calls1635 = new BitSet(new long[]{0x0002000000000002L});
	public static final BitSet FOLLOW_XPATH_in_xpath_function1644 = new BitSet(new long[]{0x0000020000000000L});
	public static final BitSet FOLLOW_41_in_xpath_function1646 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_STRING_DOUBLE_QUOTE_in_xpath_function1648 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_xpath_function1650 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXL_in_exl_function1656 = new BitSet(new long[]{0x0000020000000000L});
	public static final BitSet FOLLOW_41_in_exl_function1658 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_STRING_DOUBLE_QUOTE_in_exl_function1660 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_exl_function1662 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LUCIO_in_lucio_function1668 = new BitSet(new long[]{0x0000020000000000L});
	public static final BitSet FOLLOW_41_in_lucio_function1670 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_STRING_DOUBLE_QUOTE_in_lucio_function1672 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_lucio_function1674 = new BitSet(new long[]{0x0000000000000002L});
}
