/*
 * MoveVerifier.java
 *
 * Created on August 3, 2005, 1:04 PM
 *
 */

package org.invade;

public interface MoveVerifier {
    /* This method does nothing if playing move would be legal; otherwise, an
     * IllegalMoveException is thrown.
     */
    public void verify(Board board, Object move) throws IllegalMoveException;
    
    /* Same as verify(...), except that true is returned if verify(...) 
     * would not throw an exception. */
    public boolean isLegal(Board board, Object move);
}
