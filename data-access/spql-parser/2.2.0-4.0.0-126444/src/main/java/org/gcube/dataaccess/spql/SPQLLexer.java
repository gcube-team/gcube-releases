// $ANTLR 3.5 /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g 2013-08-01 14:41:40

package org.gcube.dataaccess.spql;
import org.gcube.dataaccess.spql.model.error.SyntaxError;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SPQLLexer extends Lexer {
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


	    @Override
	      public void emitErrorMessage(String msg) {
	      throw new SyntaxError(msg);
	  }


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public SPQLLexer() {} 
	public SPQLLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public SPQLLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g"; }

	// $ANTLR start "T__37"
	public final void mT__37() throws RecognitionException {
		try {
			int _type = T__37;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:14:7: ( '!' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:14:9: '!'
			{
			match('!'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__37"

	// $ANTLR start "T__38"
	public final void mT__38() throws RecognitionException {
		try {
			int _type = T__38;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:15:7: ( '!=' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:15:9: '!='
			{
			match("!="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__38"

	// $ANTLR start "T__39"
	public final void mT__39() throws RecognitionException {
		try {
			int _type = T__39;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:16:7: ( '%' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:16:9: '%'
			{
			match('%'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__39"

	// $ANTLR start "T__40"
	public final void mT__40() throws RecognitionException {
		try {
			int _type = T__40;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:17:7: ( '&&' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:17:9: '&&'
			{
			match("&&"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__40"

	// $ANTLR start "T__41"
	public final void mT__41() throws RecognitionException {
		try {
			int _type = T__41;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:18:7: ( '(' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:18:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__41"

	// $ANTLR start "T__42"
	public final void mT__42() throws RecognitionException {
		try {
			int _type = T__42;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:19:7: ( ')' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:19:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__42"

	// $ANTLR start "T__43"
	public final void mT__43() throws RecognitionException {
		try {
			int _type = T__43;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:20:7: ( '*' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:20:9: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__43"

	// $ANTLR start "T__44"
	public final void mT__44() throws RecognitionException {
		try {
			int _type = T__44;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:21:7: ( '+' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:21:9: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__44"

	// $ANTLR start "T__45"
	public final void mT__45() throws RecognitionException {
		try {
			int _type = T__45;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:22:7: ( '++' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:22:9: '++'
			{
			match("++"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__45"

	// $ANTLR start "T__46"
	public final void mT__46() throws RecognitionException {
		try {
			int _type = T__46;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:23:7: ( ',' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:23:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__46"

	// $ANTLR start "T__47"
	public final void mT__47() throws RecognitionException {
		try {
			int _type = T__47;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:24:7: ( '-' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:24:9: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__47"

	// $ANTLR start "T__48"
	public final void mT__48() throws RecognitionException {
		try {
			int _type = T__48;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:25:7: ( '--' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:25:9: '--'
			{
			match("--"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__48"

	// $ANTLR start "T__49"
	public final void mT__49() throws RecognitionException {
		try {
			int _type = T__49;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:26:7: ( '.' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:26:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__49"

	// $ANTLR start "T__50"
	public final void mT__50() throws RecognitionException {
		try {
			int _type = T__50;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:27:7: ( '/' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:27:9: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__50"

	// $ANTLR start "T__51"
	public final void mT__51() throws RecognitionException {
		try {
			int _type = T__51;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:28:7: ( '<' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:28:9: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__51"

	// $ANTLR start "T__52"
	public final void mT__52() throws RecognitionException {
		try {
			int _type = T__52;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:29:7: ( '<=' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:29:9: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__52"

	// $ANTLR start "T__53"
	public final void mT__53() throws RecognitionException {
		try {
			int _type = T__53;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:30:7: ( '=' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:30:9: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__53"

	// $ANTLR start "T__54"
	public final void mT__54() throws RecognitionException {
		try {
			int _type = T__54;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:31:7: ( '==' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:31:9: '=='
			{
			match("=="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__54"

	// $ANTLR start "T__55"
	public final void mT__55() throws RecognitionException {
		try {
			int _type = T__55;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:32:7: ( '>' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:32:9: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__55"

	// $ANTLR start "T__56"
	public final void mT__56() throws RecognitionException {
		try {
			int _type = T__56;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:33:7: ( '>=' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:33:9: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__56"

	// $ANTLR start "T__57"
	public final void mT__57() throws RecognitionException {
		try {
			int _type = T__57;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:34:7: ( 'CN' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:34:9: 'CN'
			{
			match("CN"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__57"

	// $ANTLR start "T__58"
	public final void mT__58() throws RecognitionException {
		try {
			int _type = T__58;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:35:7: ( 'CommonName' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:35:9: 'CommonName'
			{
			match("CommonName"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__58"

	// $ANTLR start "T__59"
	public final void mT__59() throws RecognitionException {
		try {
			int _type = T__59;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:36:7: ( 'SN' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:36:9: 'SN'
			{
			match("SN"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__59"

	// $ANTLR start "T__60"
	public final void mT__60() throws RecognitionException {
		try {
			int _type = T__60;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:37:7: ( 'ScientificName' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:37:9: 'ScientificName'
			{
			match("ScientificName"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__60"

	// $ANTLR start "T__61"
	public final void mT__61() throws RecognitionException {
		try {
			int _type = T__61;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:38:7: ( 'coordinate' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:38:9: 'coordinate'
			{
			match("coordinate"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__61"

	// $ANTLR start "T__62"
	public final void mT__62() throws RecognitionException {
		try {
			int _type = T__62;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:39:7: ( 'eventDate' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:39:9: 'eventDate'
			{
			match("eventDate"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__62"

	// $ANTLR start "T__63"
	public final void mT__63() throws RecognitionException {
		try {
			int _type = T__63;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:40:7: ( '||' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:40:9: '||'
			{
			match("||"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__63"

	// $ANTLR start "LUCIO"
	public final void mLUCIO() throws RecognitionException {
		try {
			int _type = LUCIO;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:250:6: ( 'LUCIO' | 'lucio' )
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0=='L') ) {
				alt1=1;
			}
			else if ( (LA1_0=='l') ) {
				alt1=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:250:8: 'LUCIO'
					{
					match("LUCIO"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:250:18: 'lucio'
					{
					match("lucio"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LUCIO"

	// $ANTLR start "EXL"
	public final void mEXL() throws RecognitionException {
		try {
			int _type = EXL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:251:4: ( 'EXL' | 'exl' )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0=='E') ) {
				alt2=1;
			}
			else if ( (LA2_0=='e') ) {
				alt2=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:251:6: 'EXL'
					{
					match("EXL"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:251:14: 'exl'
					{
					match("exl"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXL"

	// $ANTLR start "XPATH"
	public final void mXPATH() throws RecognitionException {
		try {
			int _type = XPATH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:252:6: ( 'XPATH' | 'xpath' )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='X') ) {
				alt3=1;
			}
			else if ( (LA3_0=='x') ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:252:8: 'XPATH'
					{
					match("XPATH"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:252:18: 'xpath'
					{
					match("xpath"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "XPATH"

	// $ANTLR start "SEARCHBY"
	public final void mSEARCHBY() throws RecognitionException {
		try {
			int _type = SEARCHBY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:253:9: ( 'SEARCH BY' | 'search by' )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0=='S') ) {
				alt4=1;
			}
			else if ( (LA4_0=='s') ) {
				alt4=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:253:11: 'SEARCH BY'
					{
					match("SEARCH BY"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:253:25: 'search by'
					{
					match("search by"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SEARCHBY"

	// $ANTLR start "EXPAND"
	public final void mEXPAND() throws RecognitionException {
		try {
			int _type = EXPAND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:254:7: ( 'EXPAND' | 'expande' )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0=='E') ) {
				alt5=1;
			}
			else if ( (LA5_0=='e') ) {
				alt5=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:254:9: 'EXPAND'
					{
					match("EXPAND"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:254:20: 'expande'
					{
					match("expande"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPAND"

	// $ANTLR start "RESOLVE"
	public final void mRESOLVE() throws RecognitionException {
		try {
			int _type = RESOLVE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:255:8: ( 'RESOLVE' | 'resolve' )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0=='R') ) {
				alt6=1;
			}
			else if ( (LA6_0=='r') ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:255:10: 'RESOLVE'
					{
					match("RESOLVE"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:255:22: 'resolve'
					{
					match("resolve"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RESOLVE"

	// $ANTLR start "UNFOLD"
	public final void mUNFOLD() throws RecognitionException {
		try {
			int _type = UNFOLD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:256:7: ( 'UNFOLD' | 'unfold' )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='U') ) {
				alt7=1;
			}
			else if ( (LA7_0=='u') ) {
				alt7=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}

			switch (alt7) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:256:9: 'UNFOLD'
					{
					match("UNFOLD"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:256:20: 'unfold'
					{
					match("unfold"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UNFOLD"

	// $ANTLR start "WITH"
	public final void mWITH() throws RecognitionException {
		try {
			int _type = WITH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:257:5: ( 'WITH' | 'with' )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0=='W') ) {
				alt8=1;
			}
			else if ( (LA8_0=='w') ) {
				alt8=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:257:7: 'WITH'
					{
					match("WITH"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:257:16: 'with'
					{
					match("with"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WITH"

	// $ANTLR start "IN"
	public final void mIN() throws RecognitionException {
		try {
			int _type = IN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:258:4: ( 'IN' | 'in' )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0=='I') ) {
				alt9=1;
			}
			else if ( (LA9_0=='i') ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:258:6: 'IN'
					{
					match("IN"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:258:13: 'in'
					{
					match("in"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IN"

	// $ANTLR start "WHERE"
	public final void mWHERE() throws RecognitionException {
		try {
			int _type = WHERE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:259:7: ( 'WHERE' | 'where' )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0=='W') ) {
				alt10=1;
			}
			else if ( (LA10_0=='w') ) {
				alt10=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:259:9: 'WHERE'
					{
					match("WHERE"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:259:19: 'where'
					{
					match("where"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHERE"

	// $ANTLR start "RETURN"
	public final void mRETURN() throws RecognitionException {
		try {
			int _type = RETURN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:260:8: ( 'RETURN' | 'return' )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0=='R') ) {
				alt11=1;
			}
			else if ( (LA11_0=='r') ) {
				alt11=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:260:10: 'RETURN'
					{
					match("RETURN"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:260:21: 'return'
					{
					match("return"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RETURN"

	// $ANTLR start "AND"
	public final void mAND() throws RecognitionException {
		try {
			int _type = AND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:261:5: ( 'AND' | 'and' )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0=='A') ) {
				alt12=1;
			}
			else if ( (LA12_0=='a') ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:261:7: 'AND'
					{
					match("AND"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:261:15: 'and'
					{
					match("and"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AND"

	// $ANTLR start "IS"
	public final void mIS() throws RecognitionException {
		try {
			int _type = IS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:262:4: ( 'IS' | 'is' )
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0=='I') ) {
				alt13=1;
			}
			else if ( (LA13_0=='i') ) {
				alt13=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}

			switch (alt13) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:262:6: 'IS'
					{
					match("IS"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:262:13: 'is'
					{
					match("is"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IS"

	// $ANTLR start "AS"
	public final void mAS() throws RecognitionException {
		try {
			int _type = AS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:263:4: ( 'AS' | 'as' )
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0=='A') ) {
				alt14=1;
			}
			else if ( (LA14_0=='a') ) {
				alt14=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}

			switch (alt14) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:263:6: 'AS'
					{
					match("AS"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:263:13: 'as'
					{
					match("as"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AS"

	// $ANTLR start "HAVING"
	public final void mHAVING() throws RecognitionException {
		try {
			int _type = HAVING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:264:8: ( 'HAVING' | 'having' )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0=='H') ) {
				alt15=1;
			}
			else if ( (LA15_0=='h') ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:264:10: 'HAVING'
					{
					match("HAVING"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:264:21: 'having'
					{
					match("having"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HAVING"

	// $ANTLR start "PRODUCT"
	public final void mPRODUCT() throws RecognitionException {
		try {
			int _type = PRODUCT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:265:8: ( 'Product' | 'PRODUCT' | 'product' )
			int alt16=3;
			int LA16_0 = input.LA(1);
			if ( (LA16_0=='P') ) {
				int LA16_1 = input.LA(2);
				if ( (LA16_1=='r') ) {
					alt16=1;
				}
				else if ( (LA16_1=='R') ) {
					alt16=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA16_0=='p') ) {
				alt16=3;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:265:10: 'Product'
					{
					match("Product"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:265:22: 'PRODUCT'
					{
					match("PRODUCT"); 

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:265:34: 'product'
					{
					match("product"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PRODUCT"

	// $ANTLR start "OCCURRENCE"
	public final void mOCCURRENCE() throws RecognitionException {
		try {
			int _type = OCCURRENCE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:266:11: ( 'Occurrence' | 'OCCURRENCE' | 'occurrence' )
			int alt17=3;
			int LA17_0 = input.LA(1);
			if ( (LA17_0=='O') ) {
				int LA17_1 = input.LA(2);
				if ( (LA17_1=='c') ) {
					alt17=1;
				}
				else if ( (LA17_1=='C') ) {
					alt17=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA17_0=='o') ) {
				alt17=3;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}

			switch (alt17) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:266:13: 'Occurrence'
					{
					match("Occurrence"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:266:28: 'OCCURRENCE'
					{
					match("OCCURRENCE"); 

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:266:43: 'occurrence'
					{
					match("occurrence"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OCCURRENCE"

	// $ANTLR start "TAXON"
	public final void mTAXON() throws RecognitionException {
		try {
			int _type = TAXON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:267:6: ( 'Taxon' | 'TAXON' | 'taxon' )
			int alt18=3;
			int LA18_0 = input.LA(1);
			if ( (LA18_0=='T') ) {
				int LA18_1 = input.LA(2);
				if ( (LA18_1=='a') ) {
					alt18=1;
				}
				else if ( (LA18_1=='A') ) {
					alt18=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 18, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA18_0=='t') ) {
				alt18=3;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:267:8: 'Taxon'
					{
					match("Taxon"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:267:18: 'TAXON'
					{
					match("TAXON"); 

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:267:28: 'taxon'
					{
					match("taxon"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TAXON"

	// $ANTLR start "TRUE"
	public final void mTRUE() throws RecognitionException {
		try {
			int _type = TRUE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:271:5: ( 'true' | 'TRUE' )
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0=='t') ) {
				alt19=1;
			}
			else if ( (LA19_0=='T') ) {
				alt19=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}

			switch (alt19) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:271:9: 'true'
					{
					match("true"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:271:18: 'TRUE'
					{
					match("TRUE"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TRUE"

	// $ANTLR start "FALSE"
	public final void mFALSE() throws RecognitionException {
		try {
			int _type = FALSE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:275:5: ( 'false' | 'FALSE' )
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0=='f') ) {
				alt20=1;
			}
			else if ( (LA20_0=='F') ) {
				alt20=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}

			switch (alt20) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:275:9: 'false'
					{
					match("false"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:275:19: 'FALSE'
					{
					match("FALSE"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FALSE"

	// $ANTLR start "NULL"
	public final void mNULL() throws RecognitionException {
		try {
			int _type = NULL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:279:5: ( 'null' | 'NULL' )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0=='n') ) {
				alt21=1;
			}
			else if ( (LA21_0=='N') ) {
				alt21=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:279:9: 'null'
					{
					match("null"); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:279:19: 'NULL'
					{
					match("NULL"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NULL"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:282:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:282:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:282:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( ((LA22_0 >= '0' && LA22_0 <= '9')||(LA22_0 >= 'A' && LA22_0 <= 'Z')||LA22_0=='_'||(LA22_0 >= 'a' && LA22_0 <= 'z')) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop22;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:285:5: ( ( '0' .. '9' )+ )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:285:7: ( '0' .. '9' )+
			{
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:285:7: ( '0' .. '9' )+
			int cnt23=0;
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( ((LA23_0 >= '0' && LA23_0 <= '9')) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt23 >= 1 ) break loop23;
					EarlyExitException eee = new EarlyExitException(23, input);
					throw eee;
				}
				cnt23++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "FLOAT"
	public final void mFLOAT() throws RecognitionException {
		try {
			int _type = FLOAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
			int alt30=3;
			alt30 = dfa30.predict(input);
			switch (alt30) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
					{
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:9: ( '0' .. '9' )+
					int cnt24=0;
					loop24:
					while (true) {
						int alt24=2;
						int LA24_0 = input.LA(1);
						if ( ((LA24_0 >= '0' && LA24_0 <= '9')) ) {
							alt24=1;
						}

						switch (alt24) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt24 >= 1 ) break loop24;
							EarlyExitException eee = new EarlyExitException(24, input);
							throw eee;
						}
						cnt24++;
					}

					match('.'); 
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:25: ( '0' .. '9' )*
					loop25:
					while (true) {
						int alt25=2;
						int LA25_0 = input.LA(1);
						if ( ((LA25_0 >= '0' && LA25_0 <= '9')) ) {
							alt25=1;
						}

						switch (alt25) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop25;
						}
					}

					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:37: ( EXPONENT )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0=='E'||LA26_0=='e') ) {
						alt26=1;
					}
					switch (alt26) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:289:37: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:290:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
					{
					match('.'); 
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:290:13: ( '0' .. '9' )+
					int cnt27=0;
					loop27:
					while (true) {
						int alt27=2;
						int LA27_0 = input.LA(1);
						if ( ((LA27_0 >= '0' && LA27_0 <= '9')) ) {
							alt27=1;
						}

						switch (alt27) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt27 >= 1 ) break loop27;
							EarlyExitException eee = new EarlyExitException(27, input);
							throw eee;
						}
						cnt27++;
					}

					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:290:25: ( EXPONENT )?
					int alt28=2;
					int LA28_0 = input.LA(1);
					if ( (LA28_0=='E'||LA28_0=='e') ) {
						alt28=1;
					}
					switch (alt28) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:290:25: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:291:9: ( '0' .. '9' )+ EXPONENT
					{
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:291:9: ( '0' .. '9' )+
					int cnt29=0;
					loop29:
					while (true) {
						int alt29=2;
						int LA29_0 = input.LA(1);
						if ( ((LA29_0 >= '0' && LA29_0 <= '9')) ) {
							alt29=1;
						}

						switch (alt29) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt29 >= 1 ) break loop29;
							EarlyExitException eee = new EarlyExitException(29, input);
							throw eee;
						}
						cnt29++;
					}

					mEXPONENT(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FLOAT"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:295:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
			int alt34=2;
			int LA34_0 = input.LA(1);
			if ( (LA34_0=='/') ) {
				int LA34_1 = input.LA(2);
				if ( (LA34_1=='/') ) {
					alt34=1;
				}
				else if ( (LA34_1=='*') ) {
					alt34=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 34, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 34, 0, input);
				throw nvae;
			}

			switch (alt34) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:295:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
					{
					match("//"); 

					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:295:14: (~ ( '\\n' | '\\r' ) )*
					loop31:
					while (true) {
						int alt31=2;
						int LA31_0 = input.LA(1);
						if ( ((LA31_0 >= '\u0000' && LA31_0 <= '\t')||(LA31_0 >= '\u000B' && LA31_0 <= '\f')||(LA31_0 >= '\u000E' && LA31_0 <= '\uFFFF')) ) {
							alt31=1;
						}

						switch (alt31) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
							{
							if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop31;
						}
					}

					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:295:28: ( '\\r' )?
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0=='\r') ) {
						alt32=1;
					}
					switch (alt32) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:295:28: '\\r'
							{
							match('\r'); 
							}
							break;

					}

					match('\n'); 
					_channel=HIDDEN;
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:296:9: '/*' ( options {greedy=false; } : . )* '*/'
					{
					match("/*"); 

					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:296:14: ( options {greedy=false; } : . )*
					loop33:
					while (true) {
						int alt33=2;
						int LA33_0 = input.LA(1);
						if ( (LA33_0=='*') ) {
							int LA33_1 = input.LA(2);
							if ( (LA33_1=='/') ) {
								alt33=2;
							}
							else if ( ((LA33_1 >= '\u0000' && LA33_1 <= '.')||(LA33_1 >= '0' && LA33_1 <= '\uFFFF')) ) {
								alt33=1;
							}

						}
						else if ( ((LA33_0 >= '\u0000' && LA33_0 <= ')')||(LA33_0 >= '+' && LA33_0 <= '\uFFFF')) ) {
							alt33=1;
						}

						switch (alt33) {
						case 1 :
							// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:296:42: .
							{
							matchAny(); 
							}
							break;

						default :
							break loop33;
						}
					}

					match("*/"); 

					_channel=HIDDEN;
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:299:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:299:9: ( ' ' | '\\t' | '\\r' | '\\n' )
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "STRING"
	public final void mSTRING() throws RecognitionException {
		try {
			int _type = STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:307:5: ( '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\'' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:307:8: '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\''
			{
			match('\''); 
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:307:13: ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )*
			loop35:
			while (true) {
				int alt35=3;
				int LA35_0 = input.LA(1);
				if ( (LA35_0=='\\') ) {
					alt35=1;
				}
				else if ( ((LA35_0 >= '\u0000' && LA35_0 <= '&')||(LA35_0 >= '(' && LA35_0 <= '[')||(LA35_0 >= ']' && LA35_0 <= '\uFFFF')) ) {
					alt35=2;
				}

				switch (alt35) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:307:15: ESC_SEQ
					{
					mESC_SEQ(); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:307:25: ~ ( '\\\\' | '\\'' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop35;
				}
			}

			match('\''); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING"

	// $ANTLR start "STRING_DOUBLE_QUOTE"
	public final void mSTRING_DOUBLE_QUOTE() throws RecognitionException {
		try {
			int _type = STRING_DOUBLE_QUOTE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:311:5: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:311:8: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
			{
			match('\"'); 
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:311:12: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
			loop36:
			while (true) {
				int alt36=3;
				int LA36_0 = input.LA(1);
				if ( (LA36_0=='\\') ) {
					alt36=1;
				}
				else if ( ((LA36_0 >= '\u0000' && LA36_0 <= '!')||(LA36_0 >= '#' && LA36_0 <= '[')||(LA36_0 >= ']' && LA36_0 <= '\uFFFF')) ) {
					alt36=2;
				}

				switch (alt36) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:311:14: ESC_SEQ
					{
					mESC_SEQ(); 

					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:311:24: ~ ( '\\\\' | '\"' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop36;
				}
			}

			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING_DOUBLE_QUOTE"

	// $ANTLR start "EXPONENT"
	public final void mEXPONENT() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:317:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:317:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
			{
			if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:317:22: ( '+' | '-' )?
			int alt37=2;
			int LA37_0 = input.LA(1);
			if ( (LA37_0=='+'||LA37_0=='-') ) {
				alt37=1;
			}
			switch (alt37) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
					{
					if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:317:33: ( '0' .. '9' )+
			int cnt38=0;
			loop38:
			while (true) {
				int alt38=2;
				int LA38_0 = input.LA(1);
				if ( ((LA38_0 >= '0' && LA38_0 <= '9')) ) {
					alt38=1;
				}

				switch (alt38) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt38 >= 1 ) break loop38;
					EarlyExitException eee = new EarlyExitException(38, input);
					throw eee;
				}
				cnt38++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPONENT"

	// $ANTLR start "HEX_DIGIT"
	public final void mHEX_DIGIT() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:320:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HEX_DIGIT"

	// $ANTLR start "ESC_SEQ"
	public final void mESC_SEQ() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:324:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
			int alt39=3;
			int LA39_0 = input.LA(1);
			if ( (LA39_0=='\\') ) {
				switch ( input.LA(2) ) {
				case '\"':
				case '\'':
				case '\\':
				case 'b':
				case 'f':
				case 'n':
				case 'r':
				case 't':
					{
					alt39=1;
					}
					break;
				case 'u':
					{
					alt39=2;
					}
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					{
					alt39=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}

			switch (alt39) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:324:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
					{
					match('\\'); 
					if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:325:9: UNICODE_ESC
					{
					mUNICODE_ESC(); 

					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:326:9: OCTAL_ESC
					{
					mOCTAL_ESC(); 

					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ESC_SEQ"

	// $ANTLR start "OCTAL_ESC"
	public final void mOCTAL_ESC() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:331:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
			int alt40=3;
			int LA40_0 = input.LA(1);
			if ( (LA40_0=='\\') ) {
				int LA40_1 = input.LA(2);
				if ( ((LA40_1 >= '0' && LA40_1 <= '3')) ) {
					int LA40_2 = input.LA(3);
					if ( ((LA40_2 >= '0' && LA40_2 <= '7')) ) {
						int LA40_4 = input.LA(4);
						if ( ((LA40_4 >= '0' && LA40_4 <= '7')) ) {
							alt40=1;
						}

						else {
							alt40=2;
						}

					}

					else {
						alt40=3;
					}

				}
				else if ( ((LA40_1 >= '4' && LA40_1 <= '7')) ) {
					int LA40_3 = input.LA(3);
					if ( ((LA40_3 >= '0' && LA40_3 <= '7')) ) {
						alt40=2;
					}

					else {
						alt40=3;
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 40, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 40, 0, input);
				throw nvae;
			}

			switch (alt40) {
				case 1 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:331:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:332:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 3 :
					// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:333:9: '\\\\' ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OCTAL_ESC"

	// $ANTLR start "UNICODE_ESC"
	public final void mUNICODE_ESC() throws RecognitionException {
		try {
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:338:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
			// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:338:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
			{
			match('\\'); 
			match('u'); 
			mHEX_DIGIT(); 

			mHEX_DIGIT(); 

			mHEX_DIGIT(); 

			mHEX_DIGIT(); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UNICODE_ESC"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:8: ( T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | LUCIO | EXL | XPATH | SEARCHBY | EXPAND | RESOLVE | UNFOLD | WITH | IN | WHERE | RETURN | AND | IS | AS | HAVING | PRODUCT | OCCURRENCE | TAXON | TRUE | FALSE | NULL | ID | INT | FLOAT | COMMENT | WS | STRING | STRING_DOUBLE_QUOTE )
		int alt41=55;
		alt41 = dfa41.predict(input);
		switch (alt41) {
			case 1 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:10: T__37
				{
				mT__37(); 

				}
				break;
			case 2 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:16: T__38
				{
				mT__38(); 

				}
				break;
			case 3 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:22: T__39
				{
				mT__39(); 

				}
				break;
			case 4 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:28: T__40
				{
				mT__40(); 

				}
				break;
			case 5 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:34: T__41
				{
				mT__41(); 

				}
				break;
			case 6 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:40: T__42
				{
				mT__42(); 

				}
				break;
			case 7 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:46: T__43
				{
				mT__43(); 

				}
				break;
			case 8 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:52: T__44
				{
				mT__44(); 

				}
				break;
			case 9 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:58: T__45
				{
				mT__45(); 

				}
				break;
			case 10 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:64: T__46
				{
				mT__46(); 

				}
				break;
			case 11 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:70: T__47
				{
				mT__47(); 

				}
				break;
			case 12 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:76: T__48
				{
				mT__48(); 

				}
				break;
			case 13 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:82: T__49
				{
				mT__49(); 

				}
				break;
			case 14 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:88: T__50
				{
				mT__50(); 

				}
				break;
			case 15 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:94: T__51
				{
				mT__51(); 

				}
				break;
			case 16 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:100: T__52
				{
				mT__52(); 

				}
				break;
			case 17 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:106: T__53
				{
				mT__53(); 

				}
				break;
			case 18 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:112: T__54
				{
				mT__54(); 

				}
				break;
			case 19 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:118: T__55
				{
				mT__55(); 

				}
				break;
			case 20 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:124: T__56
				{
				mT__56(); 

				}
				break;
			case 21 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:130: T__57
				{
				mT__57(); 

				}
				break;
			case 22 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:136: T__58
				{
				mT__58(); 

				}
				break;
			case 23 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:142: T__59
				{
				mT__59(); 

				}
				break;
			case 24 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:148: T__60
				{
				mT__60(); 

				}
				break;
			case 25 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:154: T__61
				{
				mT__61(); 

				}
				break;
			case 26 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:160: T__62
				{
				mT__62(); 

				}
				break;
			case 27 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:166: T__63
				{
				mT__63(); 

				}
				break;
			case 28 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:172: LUCIO
				{
				mLUCIO(); 

				}
				break;
			case 29 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:178: EXL
				{
				mEXL(); 

				}
				break;
			case 30 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:182: XPATH
				{
				mXPATH(); 

				}
				break;
			case 31 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:188: SEARCHBY
				{
				mSEARCHBY(); 

				}
				break;
			case 32 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:197: EXPAND
				{
				mEXPAND(); 

				}
				break;
			case 33 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:204: RESOLVE
				{
				mRESOLVE(); 

				}
				break;
			case 34 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:212: UNFOLD
				{
				mUNFOLD(); 

				}
				break;
			case 35 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:219: WITH
				{
				mWITH(); 

				}
				break;
			case 36 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:224: IN
				{
				mIN(); 

				}
				break;
			case 37 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:227: WHERE
				{
				mWHERE(); 

				}
				break;
			case 38 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:233: RETURN
				{
				mRETURN(); 

				}
				break;
			case 39 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:240: AND
				{
				mAND(); 

				}
				break;
			case 40 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:244: IS
				{
				mIS(); 

				}
				break;
			case 41 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:247: AS
				{
				mAS(); 

				}
				break;
			case 42 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:250: HAVING
				{
				mHAVING(); 

				}
				break;
			case 43 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:257: PRODUCT
				{
				mPRODUCT(); 

				}
				break;
			case 44 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:265: OCCURRENCE
				{
				mOCCURRENCE(); 

				}
				break;
			case 45 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:276: TAXON
				{
				mTAXON(); 

				}
				break;
			case 46 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:282: TRUE
				{
				mTRUE(); 

				}
				break;
			case 47 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:287: FALSE
				{
				mFALSE(); 

				}
				break;
			case 48 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:293: NULL
				{
				mNULL(); 

				}
				break;
			case 49 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:298: ID
				{
				mID(); 

				}
				break;
			case 50 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:301: INT
				{
				mINT(); 

				}
				break;
			case 51 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:305: FLOAT
				{
				mFLOAT(); 

				}
				break;
			case 52 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:311: COMMENT
				{
				mCOMMENT(); 

				}
				break;
			case 53 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:319: WS
				{
				mWS(); 

				}
				break;
			case 54 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:322: STRING
				{
				mSTRING(); 

				}
				break;
			case 55 :
				// /Users/lucio/imarineWPS/spql-parser/src/main/resources/SPQL.g:1:329: STRING_DOUBLE_QUOTE
				{
				mSTRING_DOUBLE_QUOTE(); 

				}
				break;

		}
	}


	protected DFA30 dfa30 = new DFA30(this);
	protected DFA41 dfa41 = new DFA41(this);
	static final String DFA30_eotS =
		"\5\uffff";
	static final String DFA30_eofS =
		"\5\uffff";
	static final String DFA30_minS =
		"\2\56\3\uffff";
	static final String DFA30_maxS =
		"\1\71\1\145\3\uffff";
	static final String DFA30_acceptS =
		"\2\uffff\1\2\1\1\1\3";
	static final String DFA30_specialS =
		"\5\uffff}>";
	static final String[] DFA30_transitionS = {
			"\1\2\1\uffff\12\1",
			"\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
			"",
			"",
			""
	};

	static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
	static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
	static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
	static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
	static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
	static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
	static final short[][] DFA30_transition;

	static {
		int numStates = DFA30_transitionS.length;
		DFA30_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
		}
	}

	protected class DFA30 extends DFA {

		public DFA30(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 30;
			this.eot = DFA30_eot;
			this.eof = DFA30_eof;
			this.min = DFA30_min;
			this.max = DFA30_max;
			this.accept = DFA30_accept;
			this.special = DFA30_special;
			this.transition = DFA30_transition;
		}
		@Override
		public String getDescription() {
			return "288:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
		}
	}

	static final String DFA41_eotS =
		"\1\uffff\1\66\5\uffff\1\70\1\uffff\1\72\1\73\1\76\1\100\1\102\1\104\4"+
		"\60\1\uffff\34\60\1\uffff\1\164\23\uffff\1\165\1\60\1\167\23\60\1\u008f"+
		"\1\u0090\1\u008f\1\u0090\1\60\1\u0092\1\60\1\u0092\21\60\2\uffff\1\60"+
		"\1\uffff\4\60\1\u00aa\3\60\1\u00aa\16\60\2\uffff\1\u00bc\1\uffff\1\u00bc"+
		"\26\60\1\uffff\15\60\1\u00e0\1\60\1\u00e0\1\60\1\uffff\12\60\1\u00ed\1"+
		"\60\1\u00ed\2\60\2\u00f1\6\60\2\u00f8\1\60\2\u00fa\7\60\1\uffff\2\u0102"+
		"\10\60\2\u010b\1\uffff\1\u010b\2\u010c\1\uffff\6\60\1\uffff\1\u0113\1"+
		"\uffff\2\60\1\u0115\1\60\1\u0115\2\u0117\1\uffff\2\u0118\6\60\2\uffff"+
		"\2\60\1\uffff\2\60\1\u0113\1\uffff\1\u0123\1\uffff\1\u0123\2\uffff\3\u0124"+
		"\7\60\2\uffff\6\60\1\u0132\3\60\1\u0136\1\60\1\u0138\1\uffff\3\u0139\1"+
		"\uffff\1\60\2\uffff\2\60\1\u013d\1\uffff";
	static final String DFA41_eofS =
		"\u013e\uffff";
	static final String DFA41_minS =
		"\1\11\1\75\5\uffff\1\53\1\uffff\1\55\1\60\1\52\3\75\1\116\1\105\1\157"+
		"\1\166\1\uffff\1\125\1\165\1\130\1\120\1\160\1\145\1\105\1\145\1\116\1"+
		"\156\1\110\1\150\1\116\1\156\1\116\1\156\1\101\1\141\1\122\1\162\1\103"+
		"\1\143\1\101\2\141\1\101\1\165\1\125\1\uffff\1\56\23\uffff\1\60\1\155"+
		"\1\60\1\151\1\101\1\157\1\145\1\154\1\103\1\143\1\114\1\101\2\141\1\123"+
		"\1\163\1\106\1\146\1\124\1\105\1\164\1\145\4\60\1\104\1\60\1\144\1\60"+
		"\1\126\1\166\1\157\1\117\1\157\1\143\1\103\1\143\1\170\1\130\1\125\1\170"+
		"\1\165\1\154\1\114\1\154\1\114\2\uffff\1\155\1\uffff\1\145\1\122\1\162"+
		"\1\156\1\60\1\141\1\111\1\151\1\60\1\101\1\124\1\164\1\162\1\117\1\125"+
		"\1\157\1\165\1\117\1\157\1\110\1\122\1\150\1\162\2\uffff\1\60\1\uffff"+
		"\1\60\1\111\1\151\1\144\1\104\1\144\1\165\1\125\1\165\1\157\1\117\1\105"+
		"\1\157\1\145\1\163\1\123\1\154\1\114\1\157\1\156\1\103\1\144\1\164\1\uffff"+
		"\1\156\1\117\1\157\1\116\1\110\1\150\1\143\1\114\1\122\1\154\1\162\1\114"+
		"\1\154\1\60\1\105\1\60\1\145\1\uffff\1\116\1\156\1\165\1\125\1\165\1\162"+
		"\1\122\1\162\1\156\1\116\1\60\1\156\1\60\1\145\1\105\2\60\1\156\1\164"+
		"\1\110\1\151\1\104\1\144\2\60\1\104\2\60\1\150\1\126\1\116\1\166\1\156"+
		"\1\104\1\144\1\uffff\2\60\1\107\1\147\1\143\1\103\1\143\1\162\1\122\1"+
		"\162\2\60\1\uffff\3\60\1\uffff\1\116\1\151\1\40\1\156\1\141\1\145\1\uffff"+
		"\1\60\1\uffff\1\40\1\105\1\60\1\145\3\60\1\uffff\2\60\1\164\1\124\1\164"+
		"\1\145\1\105\1\145\2\uffff\1\141\1\146\1\uffff\1\141\1\164\1\60\1\uffff"+
		"\1\60\1\uffff\1\60\2\uffff\3\60\1\156\1\116\1\156\1\155\1\151\1\164\1"+
		"\145\2\uffff\1\143\1\103\1\143\1\145\1\143\1\145\1\60\1\145\1\105\1\145"+
		"\1\60\1\116\1\60\1\uffff\3\60\1\uffff\1\141\2\uffff\1\155\1\145\1\60\1"+
		"\uffff";
	static final String DFA41_maxS =
		"\1\174\1\75\5\uffff\1\53\1\uffff\1\55\1\71\1\57\3\75\1\157\1\143\1\157"+
		"\1\170\1\uffff\1\125\1\165\1\130\1\120\1\160\1\145\1\105\1\145\1\116\1"+
		"\156\1\111\1\151\1\123\1\163\1\123\1\163\1\101\1\141\2\162\2\143\1\141"+
		"\1\162\1\141\1\101\1\165\1\125\1\uffff\1\145\23\uffff\1\172\1\155\1\172"+
		"\1\151\1\101\1\157\1\145\1\160\1\103\1\143\1\120\1\101\2\141\1\124\1\164"+
		"\1\106\1\146\1\124\1\105\1\164\1\145\4\172\1\104\1\172\1\144\1\172\1\126"+
		"\1\166\1\157\1\117\1\157\1\143\1\103\1\143\1\170\1\130\1\125\1\170\1\165"+
		"\1\154\1\114\1\154\1\114\2\uffff\1\155\1\uffff\1\145\1\122\1\162\1\156"+
		"\1\172\1\141\1\111\1\151\1\172\1\101\1\124\1\164\1\162\1\117\1\125\1\157"+
		"\1\165\1\117\1\157\1\110\1\122\1\150\1\162\2\uffff\1\172\1\uffff\1\172"+
		"\1\111\1\151\1\144\1\104\1\144\1\165\1\125\1\165\1\157\1\117\1\105\1\157"+
		"\1\145\1\163\1\123\1\154\1\114\1\157\1\156\1\103\1\144\1\164\1\uffff\1"+
		"\156\1\117\1\157\1\116\1\110\1\150\1\143\1\114\1\122\1\154\1\162\1\114"+
		"\1\154\1\172\1\105\1\172\1\145\1\uffff\1\116\1\156\1\165\1\125\1\165\1"+
		"\162\1\122\1\162\1\156\1\116\1\172\1\156\1\172\1\145\1\105\2\172\1\156"+
		"\1\164\1\110\1\151\1\104\1\144\2\172\1\104\2\172\1\150\1\126\1\116\1\166"+
		"\1\156\1\104\1\144\1\uffff\2\172\1\107\1\147\1\143\1\103\1\143\1\162\1"+
		"\122\1\162\2\172\1\uffff\3\172\1\uffff\1\116\1\151\1\40\1\156\1\141\1"+
		"\145\1\uffff\1\172\1\uffff\1\40\1\105\1\172\1\145\3\172\1\uffff\2\172"+
		"\1\164\1\124\1\164\1\145\1\105\1\145\2\uffff\1\141\1\146\1\uffff\1\141"+
		"\1\164\1\172\1\uffff\1\172\1\uffff\1\172\2\uffff\3\172\1\156\1\116\1\156"+
		"\1\155\1\151\1\164\1\145\2\uffff\1\143\1\103\1\143\1\145\1\143\1\145\1"+
		"\172\1\145\1\105\1\145\1\172\1\116\1\172\1\uffff\3\172\1\uffff\1\141\2"+
		"\uffff\1\155\1\145\1\172\1\uffff";
	static final String DFA41_acceptS =
		"\2\uffff\1\3\1\4\1\5\1\6\1\7\1\uffff\1\12\12\uffff\1\33\34\uffff\1\61"+
		"\1\uffff\1\65\1\66\1\67\1\2\1\1\1\11\1\10\1\14\1\13\1\15\1\63\1\64\1\16"+
		"\1\20\1\17\1\22\1\21\1\24\1\23\57\uffff\1\62\1\25\1\uffff\1\27\27\uffff"+
		"\1\44\1\50\1\uffff\1\51\27\uffff\1\35\21\uffff\1\47\43\uffff\1\43\14\uffff"+
		"\1\56\3\uffff\1\60\6\uffff\1\34\1\uffff\1\36\7\uffff\1\45\10\uffff\1\55"+
		"\1\57\2\uffff\1\37\3\uffff\1\40\1\uffff\1\46\1\uffff\1\42\1\52\12\uffff"+
		"\1\41\1\53\15\uffff\1\32\3\uffff\1\26\1\uffff\1\31\1\54\3\uffff\1\30";
	static final String DFA41_specialS =
		"\u013e\uffff}>";
	static final String[] DFA41_transitionS = {
			"\2\62\2\uffff\1\62\22\uffff\1\62\1\1\1\64\2\uffff\1\2\1\3\1\63\1\4\1"+
			"\5\1\6\1\7\1\10\1\11\1\12\1\13\12\61\2\uffff\1\14\1\15\1\16\2\uffff\1"+
			"\42\1\60\1\17\1\60\1\26\1\55\1\60\1\44\1\40\2\60\1\24\1\60\1\57\1\50"+
			"\1\46\1\60\1\32\1\20\1\52\1\34\1\60\1\36\1\27\2\60\6\uffff\1\43\1\60"+
			"\1\21\1\60\1\22\1\54\1\60\1\45\1\41\2\60\1\25\1\60\1\56\1\51\1\47\1\60"+
			"\1\33\1\31\1\53\1\35\1\60\1\37\1\30\2\60\1\uffff\1\23",
			"\1\65",
			"",
			"",
			"",
			"",
			"",
			"\1\67",
			"",
			"\1\71",
			"\12\74",
			"\1\75\4\uffff\1\75",
			"\1\77",
			"\1\101",
			"\1\103",
			"\1\105\40\uffff\1\106",
			"\1\111\10\uffff\1\107\24\uffff\1\110",
			"\1\112",
			"\1\113\1\uffff\1\114",
			"",
			"\1\115",
			"\1\116",
			"\1\117",
			"\1\120",
			"\1\121",
			"\1\122",
			"\1\123",
			"\1\124",
			"\1\125",
			"\1\126",
			"\1\130\1\127",
			"\1\132\1\131",
			"\1\133\4\uffff\1\134",
			"\1\135\4\uffff\1\136",
			"\1\137\4\uffff\1\140",
			"\1\141\4\uffff\1\142",
			"\1\143",
			"\1\144",
			"\1\146\37\uffff\1\145",
			"\1\147",
			"\1\151\37\uffff\1\150",
			"\1\152",
			"\1\154\20\uffff\1\155\16\uffff\1\153",
			"\1\156\20\uffff\1\157",
			"\1\160",
			"\1\161",
			"\1\162",
			"\1\163",
			"",
			"\1\74\1\uffff\12\61\13\uffff\1\74\37\uffff\1\74",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\166",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\170",
			"\1\171",
			"\1\172",
			"\1\173",
			"\1\174\3\uffff\1\175",
			"\1\176",
			"\1\177",
			"\1\u0080\3\uffff\1\u0081",
			"\1\u0082",
			"\1\u0083",
			"\1\u0084",
			"\1\u0085\1\u0086",
			"\1\u0087\1\u0088",
			"\1\u0089",
			"\1\u008a",
			"\1\u008b",
			"\1\u008c",
			"\1\u008d",
			"\1\u008e",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0091",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0093",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0094",
			"\1\u0095",
			"\1\u0096",
			"\1\u0097",
			"\1\u0098",
			"\1\u0099",
			"\1\u009a",
			"\1\u009b",
			"\1\u009c",
			"\1\u009d",
			"\1\u009e",
			"\1\u009f",
			"\1\u00a0",
			"\1\u00a1",
			"\1\u00a2",
			"\1\u00a3",
			"\1\u00a4",
			"",
			"",
			"\1\u00a5",
			"",
			"\1\u00a6",
			"\1\u00a7",
			"\1\u00a8",
			"\1\u00a9",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00ab",
			"\1\u00ac",
			"\1\u00ad",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00ae",
			"\1\u00af",
			"\1\u00b0",
			"\1\u00b1",
			"\1\u00b2",
			"\1\u00b3",
			"\1\u00b4",
			"\1\u00b5",
			"\1\u00b6",
			"\1\u00b7",
			"\1\u00b8",
			"\1\u00b9",
			"\1\u00ba",
			"\1\u00bb",
			"",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00bd",
			"\1\u00be",
			"\1\u00bf",
			"\1\u00c0",
			"\1\u00c1",
			"\1\u00c2",
			"\1\u00c3",
			"\1\u00c4",
			"\1\u00c5",
			"\1\u00c6",
			"\1\u00c7",
			"\1\u00c8",
			"\1\u00c9",
			"\1\u00ca",
			"\1\u00cb",
			"\1\u00cc",
			"\1\u00cd",
			"\1\u00ce",
			"\1\u00cf",
			"\1\u00d0",
			"\1\u00d1",
			"\1\u00d2",
			"",
			"\1\u00d3",
			"\1\u00d4",
			"\1\u00d5",
			"\1\u00d6",
			"\1\u00d7",
			"\1\u00d8",
			"\1\u00d9",
			"\1\u00da",
			"\1\u00db",
			"\1\u00dc",
			"\1\u00dd",
			"\1\u00de",
			"\1\u00df",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00e1",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00e2",
			"",
			"\1\u00e3",
			"\1\u00e4",
			"\1\u00e5",
			"\1\u00e6",
			"\1\u00e7",
			"\1\u00e8",
			"\1\u00e9",
			"\1\u00ea",
			"\1\u00eb",
			"\1\u00ec",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00ee",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00ef",
			"\1\u00f0",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00f2",
			"\1\u00f3",
			"\1\u00f4",
			"\1\u00f5",
			"\1\u00f6",
			"\1\u00f7",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00f9",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u00fb",
			"\1\u00fc",
			"\1\u00fd",
			"\1\u00fe",
			"\1\u00ff",
			"\1\u0100",
			"\1\u0101",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0103",
			"\1\u0104",
			"\1\u0105",
			"\1\u0106",
			"\1\u0107",
			"\1\u0108",
			"\1\u0109",
			"\1\u010a",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\1\u010d",
			"\1\u010e",
			"\1\u010f",
			"\1\u0110",
			"\1\u0111",
			"\1\u0112",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\1\u010f",
			"\1\u0114",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0116",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0119",
			"\1\u011a",
			"\1\u011b",
			"\1\u011c",
			"\1\u011d",
			"\1\u011e",
			"",
			"",
			"\1\u011f",
			"\1\u0120",
			"",
			"\1\u0121",
			"\1\u0122",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0125",
			"\1\u0126",
			"\1\u0127",
			"\1\u0128",
			"\1\u0129",
			"\1\u012a",
			"\1\u012b",
			"",
			"",
			"\1\u012c",
			"\1\u012d",
			"\1\u012e",
			"\1\u012f",
			"\1\u0130",
			"\1\u0131",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0133",
			"\1\u0134",
			"\1\u0135",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\1\u0137",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			"",
			"\1\u013a",
			"",
			"",
			"\1\u013b",
			"\1\u013c",
			"\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
			""
	};

	static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
	static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
	static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
	static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
	static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
	static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
	static final short[][] DFA41_transition;

	static {
		int numStates = DFA41_transitionS.length;
		DFA41_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
		}
	}

	protected class DFA41 extends DFA {

		public DFA41(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 41;
			this.eot = DFA41_eot;
			this.eof = DFA41_eof;
			this.min = DFA41_min;
			this.max = DFA41_max;
			this.accept = DFA41_accept;
			this.special = DFA41_special;
			this.transition = DFA41_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | LUCIO | EXL | XPATH | SEARCHBY | EXPAND | RESOLVE | UNFOLD | WITH | IN | WHERE | RETURN | AND | IS | AS | HAVING | PRODUCT | OCCURRENCE | TAXON | TRUE | FALSE | NULL | ID | INT | FLOAT | COMMENT | WS | STRING | STRING_DOUBLE_QUOTE );";
		}
	}

}
