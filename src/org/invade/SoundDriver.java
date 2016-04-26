/*
 * SoundDriver.java
 *
 * Created on February 20, 2006, 1:30 PM
 *
 */

package org.invade;

import java.io.IOException;
import java.io.InputStream;
import org.invade.resources.ResourceAnchor;
import sun.audio.*;

public class SoundDriver {
    
    private static boolean soundEnabled = true;
    
    public static void play(String soundResource) {
        if( isSoundEnabled() ) {
            try {
                InputStream in = ResourceAnchor.class.getResourceAsStream("sounds/" + soundResource);
                AudioStream as = new AudioStream(in);
                AudioPlayer.player.start(as);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static synchronized boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public static synchronized void setSoundEnabled(boolean newValue) {
        soundEnabled = newValue;
    }
}
