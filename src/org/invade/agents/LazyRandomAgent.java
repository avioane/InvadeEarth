/*
 * LazyRandomAgent.java
 *
 * Created on August 3, 2005, 10:15 AM
 *
 */

package org.invade.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.invade.Board;
import org.invade.Card;
import org.invade.Force;
import org.invade.ForcePlacement;
import org.invade.PlayableDeck;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;

public class LazyRandomAgent extends LazyAgent {
    
    public String toString() {
        return "Lazy Random Agent";
    }
    
    protected Random random = new Random();
    
    public Object getMove(Board board) {
        if(board.getTurnMode().equals(TurnMode.CLAIM_TERRITORIES)) {
            List<Territory> territories =
                    board.getTerritoriesOwned(Player.NEUTRAL, TerritoryType.LAND);
            return territories.get(random.nextInt(territories.size()));
            
        } else if(board.getTurnMode().equals(TurnMode.REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)) {
            /* This agent will always place one unit at a time.  The
             * GameAlgorithm should allow the agent to move again until
             * units are exhausted.  This will insure that the agent will
             * always be able to place all of the available units. */
            Force force = board.getCurrentPlayer().getReinforcements().getOneUnit();
            List<Territory> territories = board.getTerritoriesOwned(board.getCurrentPlayer());
            Territory territory;
            ForcePlacement placement;
            do {
                territory = territories.remove(random.nextInt(territories.size()));
                placement = new ForcePlacement(territory, force);
            } while( ! board.getMoveVerifier().isLegal(board, placement) );
            return placement;
            
        } else if(board.getTurnMode().equals(TurnMode.BID) ) {
            return Integer.valueOf(random.nextInt(board.getCurrentPlayer().getEnergy() + 1));
            
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_ORDER) ) {
            return board.getTurnChoices().get(random.nextInt(board.getTurnChoices().size()));
            
        } else if(board.getTurnMode().equals(TurnMode.DRAW_CARD) ) {
            // Shuffle the deck order so we do not choose the same deck each time
            List<PlayableDeck> shuffledDecks = new ArrayList<PlayableDeck>( board.getDecks() );
            Collections.shuffle(shuffledDecks);
            for( PlayableDeck deck : shuffledDecks ) {
                if( deck.canDraw(board) ) {
                    return deck;
                }
            }
            return SpecialMove.END_MOVE;
            
        } else if(board.getTurnMode().equals(TurnMode.FORCE_PLAY_CARD) ) {
            // Play the first card possible
            for( Card card : board.getCurrentPlayer().getCards() ) {
                if( board.getMoveVerifier().isLegal(board, card) ) {
                    return card;
                }
            }
            
        } else if(board.getTurnMode().equals(TurnMode.DESTROY_A_REGULAR_UNIT) ) {
            List<Territory> territories = board.getTerritoriesOwned(board.getCurrentPlayer());
            Collections.shuffle(territories);
            for( Territory territory : territories ) {
                if( territory.getForce().getRegularUnits() > 0 ) {
                    return territory;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_COMMANDER)) {
            for(SpecialUnit unit : board.getDefendingTerritory().getForce().getSpecialUnits()) {
                Force force = new Force();
                force.getSpecialUnits().add(unit);
                if(board.getMoveVerifier().isLegal(board, force)) {
                    return force;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_TERRITORY)) {
            for( Territory territory : board.getTerritories() ) {
                if( board.getMoveVerifier().isLegal(board, territory) ) {
                    return territory;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_YES_NO)) {
            return SpecialMove.YES;
        }
        
        return super.getMove(board);
    }
    
}
