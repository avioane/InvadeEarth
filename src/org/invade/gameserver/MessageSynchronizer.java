/*
 * MessageSynchronizer.java
 *
 * Created on April 3, 2007, 12:35 PM
 */

package org.invade.gameserver;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonathan Crosmer
 */
public class MessageSynchronizer {
    
    private String initialGameState = null;
    private List<String> moveLog = new ArrayList<String>();
    private int gameStartTimestamp = 0;
    private int lastTimestamp = 0;
    
    // lastTimestamp increases when a new game is started or when a new move
    // is added.  So value 0 = no game has been started, 1 = the first game
    // has been started, 2 = first move has been applied (or another new game), etc.
    
    // Singleton
    private static MessageSynchronizer instance = new MessageSynchronizer();
    private MessageSynchronizer() {}
    public static MessageSynchronizer getInstance() { return instance; }
    
    public synchronized int addMove(String move, int clientLastTimestamp)
    throws SimultaneousMessageException {
        if( lastTimestamp != clientLastTimestamp ) {
            throw new SimultaneousMessageException("Bad timestamp (client: " + clientLastTimestamp
                    + ", server: " + lastTimestamp);
        }
        lastTimestamp++;
        moveLog.add(move);
        return lastTimestamp;
    }
    
    public synchronized int newGame(String gameData, int clientLastTimestamp)
    throws SimultaneousMessageException {
        if( lastTimestamp != clientLastTimestamp ) {
            throw new SimultaneousMessageException("Bad timestamp (client: " + clientLastTimestamp
                    + ", server: " + lastTimestamp);
        }
        initialGameState = gameData;
        moveLog.clear();
        lastTimestamp++;
        gameStartTimestamp = lastTimestamp;
        return lastTimestamp;
    }
    
    public synchronized String getInitialGameState() {
        return initialGameState;
    }
    
    public synchronized int getGameStartTimestamp() {
        return gameStartTimestamp;
    }
    
    public synchronized int getLastTimestamp() {
        return lastTimestamp;
    }
    
    public synchronized List<String> getMoves(int startIndex) {
        List<String> result = new ArrayList<String>();
        if( startIndex < 0 ) {
            startIndex = 0;
        }
        for(int i = startIndex; i < moveLog.size(); ++i) {
            result.add(moveLog.get(i));
        }
        return result;
    }
    
}
