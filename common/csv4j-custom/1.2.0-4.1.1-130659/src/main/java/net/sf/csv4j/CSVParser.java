/*
 * Copyright 2007 Shawn Boyce.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.csv4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Parses CSV lines of text.
 * Options are available to choose the
 * <ul>
 * <li>delimiter</li>
 * <li>whether to trim whitespace from the tokens
 * </ul>
 * <p>
 * Notes:
 * Line with trailing comma will result in an empty string field
 * <p>
 * Attempts to follow this suggestion from RFC4180:
 * <quote>
 * Implementors should
 * "be conservative in what you do, be liberal in what you accept from others" (RFC 793)
 * when processing CSV files.
 * </quote>
 * @author Shawn Boyce
 * @since 1.0
 * @see <a href="http//en.wikipedia.org/wiki/Comma-separated_values">Comma Separated Values on Wikipedia</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc4180.txt">RFC 4180</a>
 */
public class CSVParser
{
	/** Field delimiter character. Default is the comma. */
	private final char delimiter;

	/**
	 * Indicates if fields should be trimmed or not.
	 * Quoted content will not be trimmed.
	 * Default is no (true). */
	private final boolean trimFields;


	/**
	 * Constructor with default delimiter (,) and trimFields (true).
	 */
	public CSVParser()
	{
		this( CSVConstants.DEFAULT_DELIMITER, true );
	}


	/**
	 * Constructs the parser with specified options
	 * @param delimiter field separator character (e.g. comma, tab, vertical bar / pipe, space)
	 * @param trimFields indicates if fields are to be trimmed or not
	 */
	public CSVParser( final char delimiter, final boolean trimFields )
	{
		this.delimiter = delimiter;
		this.trimFields = trimFields;
	}


	/**
	 * Assumes this is one line of CSV text
	 * @param line line of text to parse
	 * @return list of tokens/fields; never returns null
	 * @throws ParseException if
	 * <ol>
	 * <li>carriage return/newline found outside of a double quote, and</li>
	 * <li>terminating double quote not found</li>
	 * </ol>
	 */
	public List<String> tokenize( final String line ) throws ParseException
	{
		if ( line == null || line.length() == 0 )
			return Collections.emptyList();

		final List<String> tokens = new ArrayList<String>();

		final StringBuilder tokenBuf = new StringBuilder();
		boolean insideDoubleQuote = false;
		boolean isDoubleQuoteEscapeActive = false;
		final StringBuilder wspBuf = new StringBuilder();

		for ( int ii = 0; ii < line.length(); ii++ )
		{
			final char ch = line.charAt( ii );
			// special characters: comma, newline, double quote
			switch ( ch )
			{          
				case CSVConstants.CARRIAGE_RETURN:
				case CSVConstants.NEWLINE:
					if ( insideDoubleQuote )
					{
						tokenBuf.append( ch );
					}
					else
					{
						throw new ParseException( "unquoted " +
								( ch == '\n' ? "newline" : "carriage return" ) +
								" found at position #" + (ii+1) );
					}
					break;

				case CSVConstants.DOUBLE_QUOTE:
					if ( insideDoubleQuote )
					{
						if ( isDoubleQuoteEscapeActive )
						{
							// previous char was a double quote so this is an escaped quote
							// add the escaped double quote to the token
							tokenBuf.append( ch );
							isDoubleQuoteEscapeActive = false;
						}
						else if ( isNextCharDoubleQuote( line, ii ) )
						{
							// next char is a double quote so this is beginning of an escaped
							// double quote
							isDoubleQuoteEscapeActive = true;
							// ignore this double quote
						}
						else
						{
							// reached the end of a double quoted string
							insideDoubleQuote = false;
						}
					}
					else // not inside double quote
					{
						// beginning of a double quoted string
						insideDoubleQuote = true;

						handleWhitespace( wspBuf, tokenBuf );
					}

					break;

				default:
					if ( insideDoubleQuote )
					{
						// all characters including delimiter are part of the token
						tokenBuf.append( ch );
					}
					else // not inside double quote
					{
						if ( ch == delimiter )
						{
							tokens.add( tokenBuf.toString() );
							tokenBuf.delete( 0, tokenBuf.length() );
							wspBuf.delete( 0, wspBuf.length() ); // discard trailing whitespace
						}
						else if ( Character.isWhitespace( ch ) )
						{
							if ( !this.trimFields )
							{
								tokenBuf.append( ch );
							}
							else // we're trimming
							{
								// leading/trailing whitespace should be ignored
								// but not whitespace in the middle
								wspBuf.append( ch );
							}
						}
						else // not a whitespace char
						{
							handleWhitespace( wspBuf, tokenBuf );
							tokenBuf.append( ch );
						}
					}
				break;
			}
		}

		if ( insideDoubleQuote )
			throw new ParseException( "terminating double quote not found" );

		// add the last token
		tokens.add( tokenBuf.toString() );

		return tokens;
	}

	/**
	 * Assumes this is one line of CSV text
	 * @param line line of text to parse
	 * @return number of fields
	 * @throws ParseException if
	 * <ol>
	 * <li>carriage return/newline found outside of a double quote, and</li>
	 * <li>terminating double quote not found</li>
	 * </ol>
	 */
	public long countFields( final String line ) throws ParseException
	{
		if ( line == null || line.length() == 0 )
			return 0;

		long fieldCounter = 0;

		final StringBuilder tokenBuf = new StringBuilder();
		boolean insideDoubleQuote = false;
		boolean isDoubleQuoteEscapeActive = false;
		final StringBuilder wspBuf = new StringBuilder();

		for ( int ii = 0; ii < line.length(); ii++ )
		{
			final char ch = line.charAt( ii );
			// special characters: comma, newline, double quote
			switch ( ch )
			{          
				case CSVConstants.CARRIAGE_RETURN:
				case CSVConstants.NEWLINE:
					if ( insideDoubleQuote )
					{
						tokenBuf.append( ch );
					}
					else
					{
						throw new ParseException( "unquoted " +
								( ch == '\n' ? "newline" : "carriage return" ) +
								" found at position #" + (ii+1) );
					}
					break;

				case CSVConstants.DOUBLE_QUOTE:
					if ( insideDoubleQuote )
					{
						if ( isDoubleQuoteEscapeActive )
						{
							// previous char was a double quote so this is an escaped quote
							// add the escaped double quote to the token
							tokenBuf.append( ch );
							isDoubleQuoteEscapeActive = false;
						}
						else if ( isNextCharDoubleQuote( line, ii ) )
						{
							// next char is a double quote so this is beginning of an escaped
							// double quote
							isDoubleQuoteEscapeActive = true;
							// ignore this double quote
						}
						else
						{
							// reached the end of a double quoted string
							insideDoubleQuote = false;
						}
					}
					else // not inside double quote
					{
						// beginning of a double quoted string
						insideDoubleQuote = true;

						handleWhitespace( wspBuf, tokenBuf );
					}

					break;

				default:
					if ( insideDoubleQuote )
					{
						// all characters including delimiter are part of the token
						tokenBuf.append( ch );
					}
					else // not inside double quote
					{
						if ( ch == delimiter )
						{
							fieldCounter++;
							//tokens.add( tokenBuf.toString() );

							tokenBuf.delete( 0, tokenBuf.length() );
							wspBuf.delete( 0, wspBuf.length() ); // discard trailing whitespace
						}
						else if ( Character.isWhitespace( ch ) )
						{
							if ( !this.trimFields )
							{
								tokenBuf.append( ch );
							}
							else // we're trimming
							{
								// leading/trailing whitespace should be ignored
								// but not whitespace in the middle
								wspBuf.append( ch );
							}
						}
						else // not a whitespace char
						{
							handleWhitespace( wspBuf, tokenBuf );
							tokenBuf.append( ch );
						}
					}
				break;
			}
		}

		if ( insideDoubleQuote )
			throw new ParseException( "terminating double quote not found" );

		// add the last token
		//tokens.add( tokenBuf.toString() );
		fieldCounter++;

		return fieldCounter;
	}

	/**
	 * Assumes this is one line of CSV text
	 * @param line line of text to parse
	 * @return list of tokens/fields; never returns null
	 * @throws ParseException if
	 * <ol>
	 * <li>carriage return/newline found outside of a double quote, and</li>
	 * <li>terminating double quote not found</li>
	 * </ol>
	 */
	public String toJSon(final String line, long lineNumber) throws ParseException
	{
		if ( line == null || line.length() == 0 )
			return "{}";

		StringBuilder json = new StringBuilder("{");
		
		json.append(JSonUtil.quote("id"));
		json.append(':');
		json.append(String.valueOf(lineNumber));
		json.append(',');

		final StringBuilder tokenBuf = new StringBuilder();
		boolean insideDoubleQuote = false;
		boolean isDoubleQuoteEscapeActive = false;
		final StringBuilder wspBuf = new StringBuilder();

		int fieldNumber = 1;



		for ( int ii = 0; ii < line.length(); ii++ )
		{
			final char ch = line.charAt( ii );
			// special characters: comma, newline, double quote
			switch ( ch )
			{          
				case CSVConstants.CARRIAGE_RETURN:
				case CSVConstants.NEWLINE:
					if ( insideDoubleQuote )
					{
						tokenBuf.append( ch );
					}
					else
					{
						throw new ParseException( "unquoted " +
								( ch == '\n' ? "newline" : "carriage return" ) +
								" found at position #" + (ii+1) );
					}
					break;

				case CSVConstants.DOUBLE_QUOTE:
					if ( insideDoubleQuote )
					{
						if ( isDoubleQuoteEscapeActive )
						{
							// previous char was a double quote so this is an escaped quote
							// add the escaped double quote to the token
							tokenBuf.append( ch );
							isDoubleQuoteEscapeActive = false;
						}
						else if ( isNextCharDoubleQuote( line, ii ) )
						{
							// next char is a double quote so this is beginning of an escaped
							// double quote
							isDoubleQuoteEscapeActive = true;
							// ignore this double quote
						}
						else
						{
							// reached the end of a double quoted string
							insideDoubleQuote = false;
						}
					}
					else // not inside double quote
					{
						// beginning of a double quoted string
						insideDoubleQuote = true;

						handleWhitespace( wspBuf, tokenBuf );
					}

					break;

				default:
					if ( insideDoubleQuote )
					{
						// all characters including delimiter are part of the token
						tokenBuf.append( ch );
					}
					else // not inside double quote
					{
						if ( ch == delimiter )
						{
							json.append(JSonUtil.quote("field"+fieldNumber));
							json.append(':');
							json.append(JSonUtil.quote(tokenBuf.toString()));
							json.append(',');
							fieldNumber++;
							
							tokenBuf.delete( 0, tokenBuf.length() );
							wspBuf.delete( 0, wspBuf.length() ); // discard trailing whitespace
						}
						else if ( Character.isWhitespace( ch ) )
						{
							if ( !this.trimFields )
							{
								tokenBuf.append( ch );
							}
							else // we're trimming
							{
								// leading/trailing whitespace should be ignored
								// but not whitespace in the middle
								wspBuf.append( ch );
							}
						}
						else // not a whitespace char
						{
							handleWhitespace( wspBuf, tokenBuf );
							tokenBuf.append( ch );
						}
					}
				break;
			}
		}

		if ( insideDoubleQuote )
			throw new ParseException( "terminating double quote not found" );

		// add the last token
		json.append(JSonUtil.quote("field"+fieldNumber));
		json.append(':');
		json.append(JSonUtil.quote(tokenBuf.toString()));
		fieldNumber++;
		
		json.append('}');

		return json.toString();
	}
	
	/**
	 * Assumes this is one line of CSV text
	 * @param line line of text to parse
	 * @return number of fields
	 * @throws ParseException if
	 * <ol>
	 * <li>carriage return/newline found outside of a double quote, and</li>
	 * <li>terminating double quote not found</li>
	 * </ol>
	 */
	/*public List<Long> countFieldsLenghts( final String line ) throws ParseException
	{
		if ( line == null || line.length() == 0 )
			return new LinkedList<Long>();

		//long fieldCounter = 0;
		

		final StringBuilder tokenBuf = new StringBuilder();
		boolean insideDoubleQuote = false;
		boolean isDoubleQuoteEscapeActive = false;
		final StringBuilder wspBuf = new StringBuilder();

		for ( int ii = 0; ii < line.length(); ii++ )
		{
			long fieldLenght = 0;
			final char ch = line.charAt( ii );
			// special characters: comma, newline, double quote
			switch ( ch )
			{          
				case CSVConstants.CARRIAGE_RETURN:
				case CSVConstants.NEWLINE:
					if ( insideDoubleQuote )
					{
						tokenBuf.append( ch );
						fieldLenght++;
					}
					else
					{
						throw new ParseException( "unquoted " +
								( ch == '\n' ? "newline" : "carriage return" ) +
								" found at position #" + (ii+1) );
					}
					break;

				case CSVConstants.DOUBLE_QUOTE:
					if ( insideDoubleQuote )
					{
						if ( isDoubleQuoteEscapeActive )
						{
							// previous char was a double quote so this is an escaped quote
							// add the escaped double quote to the token
							tokenBuf.append( ch );
							fieldLenght++;
							isDoubleQuoteEscapeActive = false;
						}
						else if ( isNextCharDoubleQuote( line, ii ) )
						{
							// next char is a double quote so this is beginning of an escaped
							// double quote
							isDoubleQuoteEscapeActive = true;
							// ignore this double quote
						}
						else
						{
							// reached the end of a double quoted string
							insideDoubleQuote = false;
						}
					}
					else // not inside double quote
					{
						// beginning of a double quoted string
						insideDoubleQuote = true;

						handleWhitespace( wspBuf, tokenBuf );
					}

					break;

				default:
					if ( insideDoubleQuote )
					{
						// all characters including delimiter are part of the token
						tokenBuf.append( ch );
					}
					else // not inside double quote
					{
						if ( ch == delimiter )
						{
							fieldCounter++;
							//tokens.add( tokenBuf.toString() );

							tokenBuf.delete( 0, tokenBuf.length() );
							wspBuf.delete( 0, wspBuf.length() ); // discard trailing whitespace
						}
						else if ( Character.isWhitespace( ch ) )
						{
							if ( !this.trimFields )
							{
								tokenBuf.append( ch );
							}
							else // we're trimming
							{
								// leading/trailing whitespace should be ignored
								// but not whitespace in the middle
								wspBuf.append( ch );
							}
						}
						else // not a whitespace char
						{
							handleWhitespace( wspBuf, tokenBuf );
							tokenBuf.append( ch );
						}
					}
				break;
			}
		}

		if ( insideDoubleQuote )
			throw new ParseException( "terminating double quote not found" );

		// add the last token
		//tokens.add( tokenBuf.toString() );
		fieldCounter++;

		return fieldCounter;
	}*/


	/**
	 * Called when a non-whitespace character is found, this method handles
	 * any collected whitespace before the current character.
	 * @param wspBuf current whitespace buffer
	 * @param tokenBuf current token buffer
	 */
	private static void handleWhitespace( final StringBuilder wspBuf, final StringBuilder tokenBuf )
	{
		if ( wspBuf.length() != 0 )
		{
			// found a non-whitespace character
			// if this is the first non-wsp char, we discard beginning wsp
			// if wsp was in middle of the token, then we prepend it to the
			// token before addin the new non-wsp char
			if ( tokenBuf.length() != 0 )
			{
				tokenBuf.append( wspBuf );
			}

			wspBuf.delete( 0, wspBuf.length() );
		}
	}
	
	/**
	 * Called when a non-whitespace character is found, this method handles
	 * any collected whitespace before the current character.
	 * @param wspBuf current whitespace buffer
	 * @param tokenBuf current token buffer
	 */
	@SuppressWarnings("unused")
	private static long handleWhitespaceLenght( final long wspCounter, final long fieldCounter )
	{
		if ( wspCounter != 0 )
		{
			// found a non-whitespace character
			// if this is the first non-wsp char, we discard beginning wsp
			// if wsp was in middle of the token, then we prepend it to the
			// token before addin the new non-wsp char
			if ( fieldCounter != 0 )
			{
				return wspCounter;
			}

		}
		return 0;
	}


	/**
	 * Determines if the next character in the line is a double quote.
	 * @param line line being parsed
	 * @param ii index
	 * @return true if next char is a double quote; false if not
	 */
	private static boolean isNextCharDoubleQuote( final String line, final int ii )
	{
		return ( (ii+1) < line.length() ) && line.charAt( ii+1 ) == CSVConstants.DOUBLE_QUOTE;
	}
}
