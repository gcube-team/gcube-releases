/*
 * IndexException.java
 *
 * $Author: tsakas $
 * $Date: 2007/12/20 14:37:39 $
 * $Id: IndexException.java,v 1.1 2007/12/20 14:37:39 tsakas Exp $
 *
 * <pre>
 *             Copyright (c) : 2006 Fast Search & Transfer ASA
 *                             ALL RIGHTS RESERVED
 * </pre>
 */

package org.gcube.indexmanagement.common;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Exception used when the Index fails.
 * @author aardag
 *
 */
public class IndexException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
     * The throwable cause for the exception.
     */
    private Throwable cause;

    /**
     * Default constructor.
     */
    public IndexException() {
        super();
    }

    /**
     * Constructs with message.
     * @param message - The message to throw in the exception
     */
    public IndexException(final String message) {
        super(message);
    }

    /**
     * Constructs with chained exception.
     * @param causeIn - The throwable cause in the exception.
     */
    public IndexException(final Throwable causeIn) {
        super(causeIn.toString());
        this.cause = causeIn;
    }

    /**
     * Constructs with message and exception.
     * @param message - the message in the exception.
     * @param  causeIn - the cause in the exception.
     */
    public IndexException(final String message, final Throwable causeIn) {
        super(message, causeIn);
    }

    /**
     * Retrieves nested exception.
     * @return - the cause.
     */
    public final Throwable getException() {
        return cause;
    }
    /**
     * Print the stack trace to System.err.
     */
    public final void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Print the stack trace to a print stream.
     * @param ps - the print stream.
     */
    public final void printStackTrace(final PrintStream ps) {
        synchronized (ps) {
            super.printStackTrace(ps);
            if (cause != null) {
                ps.println("--- Nested Exception ---");
                cause.printStackTrace(ps);
            }
        }
    }

    /**
     * Print the stack trace to a print writer.
     * @param pw - the print writer.
     */
    public void printStackTrace(final PrintWriter pw) {
        synchronized (pw) {
            super.printStackTrace(pw);
            if (cause != null) {
                pw.println("--- Nested Exception ---");
                cause.printStackTrace(pw);
            }
        }
    }
}
