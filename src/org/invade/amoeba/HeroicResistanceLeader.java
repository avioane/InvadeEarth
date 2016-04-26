/*
 * HeroicResistanceLeader.java
 *
 * Created on March 14, 2006, 2:21 PM
 *
 */

package org.invade.amoeba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;

public class HeroicResistanceLeader extends AbstractAmoebaCard {
    
    private int unitCost;
    
    public HeroicResistanceLeader(int unitCost) {
        super("Heroic Resistance Leader");
        this.unitCost = unitCost;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Player player = board.getCurrentPlayer();
        final List<Territory> territories = getCandidateTerritories(board);
        final List<SpecialUnit> units = getCandidateUnits(board);
        if((!territories.isEmpty()) && (!units.isEmpty())) {
            board.sendMessage("Gain a commander at the cost of " + unitCost + " units");
            
            board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
            board.setMoveVerifier(new AbstractMoveVerifier(){
                public void verifyWithAssumptions(Board board, Object move)
                throws IllegalMoveException {
                    Territory territory = (Territory)move;
                    DefaultMoveVerifier.verify(territories.contains(territory),
                            "Choose a friendly territory with enough units");
                }
            });
            final Territory territory = (Territory)gameThread.take();
            board.setTurnMode(TurnMode.BUY_UNITS);
            // Note:  This class does allow the player to add a unit
            // that exceeds the MaxPerTerritory limit.  This "bug" is
            // not worth fixing, since it should never appear using any
            // included rule set.  (You cannot place space stations etc. with
            // this card.)
            board.setMoveVerifier(new AbstractMoveVerifier(){
                public void verifyWithAssumptions(Board board, Object move)
                throws IllegalMoveException {
                    if( move instanceof SpecialUnit ) {
                        SpecialUnit chosen = (SpecialUnit)move;
                        DefaultMoveVerifier.verify(units.contains(chosen),
                                "Illegal unit selection");
                    }
                }
            });
            Object object = gameThread.take();
            SpecialUnit unit = object instanceof SpecialUnit ?
                (SpecialUnit)object : units.get(0);
            board.setMoveVerifier(board.getRules().getMoveVerifier());
            board.sendMessage("A " + unit + " rises up in " + territory.getName());
            territory.getForce().addRegularUnits( - unitCost );
            territory.getForce().getSpecialUnits().add(unit);
        }
    }
    
    public List<Territory> getCandidateTerritories(Board board) {
        List<Territory> result = new ArrayList<Territory>();
        for(Territory territory : board.getTerritoriesOwned(board.getCurrentPlayer())) {
            if(territory.getForce().getRegularUnits() >= unitCost) {
                result.add(territory);
            }
        }
        return result;
    }
    
    public List<SpecialUnit> getCandidateUnits(Board board) {
        List<SpecialUnit> result = new ArrayList<SpecialUnit>();
        for(SpecialUnit special : board.getRules().getUnitsForPurchase()) {
            if( (special.getMaxOwnable() < 0
                    || board.getUnitCount(board.getCurrentPlayer(), special) < special.getMaxOwnable())
                    && (special.getMaxTotal() < 0
                    || board.getUnitCount(null, special) < special.getMaxTotal())
                    && (special.isMobile()) ) {
                result.add(special);
            }
        }
        return result;
    }
    
}
