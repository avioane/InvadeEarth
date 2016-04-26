/*
 * Card.java
 *
 * Created on July 10, 2005, 5:20 PM
 *
 */

package org.invade;
import java.awt.Frame;

/**
 *
 * @author Jonathan Crosmer
 */
public interface Card {
    public boolean canPlay(Board board);
    public void play(Board board, GameThread gameThread) throws EndGameException;
    public String getName(boolean hidden, boolean inPlay);
    public PlayableDeck getDeck();
    public void setDeck(PlayableDeck deck);
    public void displayCard(Frame parent, MapCanvas mapCanvas);
    
    /* Some cards may be "active" for a period of time.  The GameThread should
     * regularly call checkForAction() on each card that is active, that is,
     * whenever there exists a possibility that the rules allow an active card 
     * to perform some action.
     * The getPlayer() method returns the target player for cards that have
     * such a target; this information might be used to determine whether a
     * card should be "hidden" from the current player or to block or allow
     * certain moves.
     */
    public void checkForAction(Board board, GameThread gameThread) throws EndGameException;
    public Player getPlayer();
    
    /* When a card is active, it can be displayed at any time.  This method
     * allows display to be restricted.  Most cards should return true so that
     * players can view them.  Cards that are placed "face down" (like 
     * Secret Missions) should return false during the turn of a player not
     * authorized to view them. */
    public boolean canDisplayWhenActive(Board board);
}
