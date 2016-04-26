/*
 * GameLoader.java
 *
 * Created on February 13, 2006, 10:25 AM
 *
 */

package org.invade;

import org.jdom.Document;

/* Used by PlayersDialog when the dialog is closed.  This is better
 * than passing the entire GUI to the PlayersDialog. */
public interface GameLoader {
     void loadFile(boolean newGame, boolean replay, boolean customizeRules,
             Document document);
     void clearGame();
}
