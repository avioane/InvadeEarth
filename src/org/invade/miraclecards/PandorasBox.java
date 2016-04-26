/*
 * DrawEachTurn.java
 *
 * Created on March 28, 2006, 2:53 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.PlayableDeck;
import org.invade.SpecialUnit;
import org.invade.TurnMode;
import org.invade.commandcards.AbstractCommandCard;

public class PandorasBox extends MagicCard {
    
    private PlayableDeck drawType;
    
    public PandorasBox(PlayableDeck drawType) {
        super("Pandora's Box");
        this.drawType = drawType;
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        if( ! drawType.getCards().isEmpty() ) {
            Card card = drawType.draw();
            if( !(card instanceof AbstractCommandCard) ) {
                // should never happen
                return;
            }
            AbstractCommandCard playableCard = (AbstractCommandCard)card;
            getPlayer().getCards().add(playableCard);
            int cost = playableCard.getPowerUpCost(board);
            getPlayer().addEnergy(cost);
            SpecialUnit required = playableCard.getRequiredCommander();
            playableCard.setRequiredCommander(null);
            TurnMode mode = board.getTurnMode();
            board.setTurnMode(TurnMode.ACKNOWLEDGE_EACH_PLAYER);
            boolean canPlay = playableCard.canPlay(board);
            playableCard.setRequiredCommander(required);
            board.setTurnMode(mode);
            if( canPlay ) { 
                board.sendMessage("Pandora's Box is opened!");
                playableCard.play(board, gameThread);
            } else {
                // don't let them keep the energy if they don't play the card
                getPlayer().addEnergy(-cost);
                getPlayer().getCards().remove(playableCard);
                drawType.discard(playableCard);
            }
            
        }
    }
    
    public String getDescriptionString() {
        return "Draw a " + drawType.getName()
        + " card at the start of each of your turns.  Play that card immediately"
                + " without sacrificing any faith tokens.";
    }
    
}
