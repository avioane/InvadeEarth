/*
 * RuleCard.java
 *
 * Created on June 1, 2006, 1:11 PM
 *
 */

package org.invade.miraclecards;

import java.awt.Frame;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.PlayableDeck;
import org.invade.Player;

public abstract class RuleCard implements Card {
    
    public boolean canDisplayWhenActive(Board board) { return false; }

    
    public boolean canPlay(Board board) { return true; }

    
    public void displayCard(Frame parent, MapCanvas mapCanvas) {}

    
    public PlayableDeck getDeck() { return null; }

    
    public Player getPlayer() {
        return Player.NEUTRAL;
    }

    
    public void play(Board board, GameThread gameThread) throws EndGameException {
        board.getCardsInPlay().add(this);
    }

    
    public void setDeck(PlayableDeck deck) {}
    
}
