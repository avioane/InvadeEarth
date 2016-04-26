/*
 * BlockMiracle.java
 *
 * Created on March 22, 2007, 10:14 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;

/**
 *
 * @author Jonathan Crosmer
 */
public interface BlockMiracle {
    public boolean checkForBlock(Board board, GameThread gameThread) 
    throws EndGameException;
}
