/*
 * SimultaneousMessageException.java
 *
 * Created on April 3, 2007, 7:35 PM
 *
 */

package org.invade.gameserver;

/**
 *
 * @author Jonathan Crosmer
 */
public class SimultaneousMessageException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>SimultaneousMessageException</code> without detail message.
     */
    public SimultaneousMessageException() {
    }
    
    
    /**
     * Constructs an instance of <code>SimultaneousMessageException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SimultaneousMessageException(String msg) {
        super(msg);
    }
}
