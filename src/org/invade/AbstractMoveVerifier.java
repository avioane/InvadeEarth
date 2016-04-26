/*
 * AbstractMoveVerifier.java
 *
 * Created on August 3, 2005, 4:43 PM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.Arrays;
import org.invade.resources.ResourceAnchor;
import java.net.URL;

public abstract class AbstractMoveVerifier implements MoveVerifier {
    
    public void verify(Board board, Object move) throws IllegalMoveException {
        try {
            verifyWithAssumptions(board, move);
        } catch(ClassCastException e) {
            throw new IllegalMoveException("Wrong data type:  " + move.toString());
        }
    }
    
    public boolean isLegal(Board board, Object move) {
        try {
            verify(board, move);
            return true;
        } catch(IllegalMoveException e) {}
        return false;
    }
    
    /* Performs verification, but assumes that moves[] contains the correct
     * number and type of objects.  May throw a ClassCastException or
     * ArrayIndexOutOfBoundsException if these conditions are violated. */
    public abstract void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException;
    
   
    /* A convenience method to assert a condition. */
    public static void verify(boolean condition, String message)
    throws IllegalMoveException {
        if( ! condition ) {
            throw new IllegalMoveException(message);
        }
    }
    
    /* Returns true if move is a sentinel or false if move is not
     * an instance of SpecialMove.  Otherwise, an exception is thrown. */
    public static boolean checkForSentinel(SpecialMove sentinel, Object move)
    throws IllegalMoveException {
        if( move instanceof SpecialMove ) {
            verify(move.equals(sentinel),
                    "Wrong sentinel object:  " + move.toString());
            return true;
        }
        return false;
    }
    
    public static SpecialMove verifySentinel(SpecialMove sentinel, Object move)
    throws IllegalMoveException {
        checkForSentinel(sentinel, move);
        SpecialMove castCheck = (SpecialMove)move;
        // return the value to make sure a zealous optimizer doesn't 
        // erase the preceeding cast check; we want any ClassCastException
        // to happen here
        return castCheck;
    }
    
}

