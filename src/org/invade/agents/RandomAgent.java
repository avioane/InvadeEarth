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
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryDuple;
import org.invade.TurnMode;

public class RandomAgent extends LazyRandomAgent {
    
    public String toString() {
        return "Random Agent";
    }
    
    private int minRandomAttacksPerTurn = 3;
    private int maxRandomAttacksPerTurn = 6;
    
    private int attacksThisTurn = 0;
    
    public Object getMove(Board board) {
        Card card = playCard(board);
        if( card != null ) {
            return card;
        }
        if(board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS)) {
            
            if( attacksThisTurn >= getMaxRandomAttacksPerTurn() ) {
                return SpecialMove.END_MOVE;
            }
            List<Territory> myTerritories = board.getTerritoriesOwned(board.getCurrentPlayer());
            Collections.shuffle(myTerritories);
            for( Territory mine : myTerritories ) {
                List<Territory> enemyTerritories = new ArrayList<Territory>(board.getTerritories());
                enemyTerritories.removeAll(myTerritories);
                Collections.shuffle(enemyTerritories);
                for( Territory enemy : enemyTerritories ) {
                    TerritoryDuple invasion = new TerritoryDuple(mine, enemy);
                    if( board.getMoveVerifier().isLegal(board, invasion) ) {
                        attacksThisTurn++;
                        return invasion;
                    }
                }
            }
            
        } else if(board.getTurnMode().equals(TurnMode.BUY_CARDS)) {
            List<PlayableDeck> decks = new ArrayList<PlayableDeck>(board.getDecks());
            Collections.shuffle(decks);
            for( PlayableDeck deck : decks ) {
                if( deck.canBuy(board) ) {
                    return deck;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.BUY_UNITS)) {
            List<SpecialUnit> units = new ArrayList<SpecialUnit>(board.getRules().getUnitsForPurchase());
            Collections.shuffle(units);
            for( SpecialUnit unit : units ) {
                if( board.getMoveVerifier().isLegal(board, unit) ) {
                    return unit;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.REINFORCEMENTS)) {
            attacksThisTurn = getMaxRandomAttacksPerTurn() -
                    random.nextInt(getMaxRandomAttacksPerTurn()
                    - getMinRandomAttacksPerTurn() + 1) - getMinRandomAttacksPerTurn();
            
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_PLAYER)) {
            List<Player> players = new ArrayList<Player>(board.getPlayers());
            Collections.shuffle(players);
            for(Player player : players ) {
                if( board.getMoveVerifier().isLegal(board, player) ) {
                    return player;
                }
            }
            
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_TERRITORY)) {
            List<Territory> territories = new ArrayList<Territory>(board.getTerritories());
            Collections.shuffle(territories);
            for( Territory territory : territories ) {
                if( board.getMoveVerifier().isLegal(board, territory) ) {
                    return territory;
                }
            }
        }
        
        return super.getMove(board);
    }
    
    protected Card playCard(Board board) {
        for( Card card : board.getCurrentPlayer().getCards() ) {
            if( card instanceof AutomaticCard
            && board.getMoveVerifier().isLegal(board, card) ) {
                return card;
            }
        }
        return null;
    }
    
    public int getMinRandomAttacksPerTurn() {
        return minRandomAttacksPerTurn;
    }
    
    public void setMinRandomAttacksPerTurn(int minRandomAttacksPerTurn) {
        this.minRandomAttacksPerTurn = minRandomAttacksPerTurn;
    }
    
    public int getMaxRandomAttacksPerTurn() {
        return maxRandomAttacksPerTurn;
    }
    
    public void setMaxRandomAttacksPerTurn(int maxRandomAttacksPerTurn) {
        this.maxRandomAttacksPerTurn = maxRandomAttacksPerTurn;
    }
    
}
