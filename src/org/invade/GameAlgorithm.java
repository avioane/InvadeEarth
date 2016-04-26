/*
 * GameAlgorithm.java
 *
 * Created on July 29, 2005, 1:07 PM
 *
 */

package org.invade;

public interface GameAlgorithm {
    public void startGame(Board board, GameThread gameThread) throws EndGameException;
}
