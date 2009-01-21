package org.apache.ode.simpel.util;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.EarlyExitException;

/**
 * ANTLR-specific error message builder. Analyzes parser exceptions raised by ANTLR and
 * produces a user-friendly error message for SimPEL.
 */
public class ErrorMessageBuilder {

    public static String msg(RecognitionException e, String[] tokenNames, String paraphrase) {
        String msg = null;
        if (e instanceof NoViableAltException) {
            if (tokenNames == null) msg = "Invalid character";
            else msg = "Syntax error, unexpected token " + tokenNames[e.token.getType()];
        } else if (e instanceof MismatchedTokenException) {
            MismatchedTokenException mte = (MismatchedTokenException) e;
            msg = "Syntax error, unexpected token " + tokenNames[e.token.getType()] + ", expecting " + tokenNames[mte.expecting];
        } else if (e instanceof EarlyExitException) {
            msg = "At least one element is required at " + tokenNames[e.token.getType()];
        }
        if (msg != null && paraphrase != null) msg = msg + " " + paraphrase;
        return msg;
    }
}
