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

/**
 * Defines a generic StringTokenizer-style interface.
 * @param <E> type of tokens
 * @author Shawn Boyce
 * @since 1.0
 * @see java.util.StringTokenizer
 */
public interface Tokenizer<E>
{
    /**
     * Indicates if more tokens are available.
     * @return true if more tokens; false if not
     */
    boolean hasMoreTokens();


    /**
     * Returns the next token.
     * @return next token string
     * @throws java.util.NoSuchElementException if no more tokens available
     */
    E nextToken();


    /**
     * Indicates how many tokens are left.
     * Note the value returned is not constant;
     * calls to {@link #nextToken} will cause the count to reduce.
     * @return number of tokens left
     */
    int countTokens();
}
