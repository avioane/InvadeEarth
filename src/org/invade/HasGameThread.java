/*
 * HasGameThread.java
 *
 * Created on July 29, 2005, 12:53 PM
 *
 */

package org.invade;

import org.invade.GameThread;

public interface HasGameThread {
    public GameThread getGameThread();    
    public void setGameThread(GameThread gameThread);
}
