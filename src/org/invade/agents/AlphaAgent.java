/*
 * AlphaAgent.java
 *
 * Created on August 7, 2005, 2:50 PM
 *
 */

package org.invade.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.invade.Board;
import org.invade.Force;
import org.invade.ForcePlacement;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.Territory;
import org.invade.TerritoryDuple;
import org.invade.TurnMode;
import org.invade.rules.ClassicRules;

/**
 *
 * @author Jonathan Crosmer
 */
public class AlphaAgent extends RandomAgent {
    
    public String toString() {
        return "Alpha Agent";
    }
    
    public AlphaAgent() {
        // Disable random attacking
        setMaxRandomAttacksPerTurn(0);
        setMinRandomAttacksPerTurn(0);
    }
    
    public Object getMove(Board board) {
        if(board.getTurnMode().equals(TurnMode.CLAIM_TERRITORIES)) {
            if( board.getTerritoriesOwned(board.getCurrentPlayer()).isEmpty() ) {
                return super.getMove(board);
            }
            for( Territory territory : evaluate(board).getTerritories() ) {
                if( board.getMoveVerifier().isLegal(board, territory) ) {
                    return territory;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)) {
            Force force = board.getCurrentPlayer().getReinforcements().getOneUnit();
            for( Territory territory : evaluate(board).getTerritories() ) {
                ForcePlacement placement = new ForcePlacement(territory, force);
                if( board.getMoveVerifier().isLegal(board, placement) ) {
                    return placement;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS)) {
            TerritoryValueMap map = evaluate(board);
            for( Territory to : map.getTerritories() ) {
                for( Territory from : board.getTerritories() ) {
                    TerritoryDuple invasion = new TerritoryDuple(from, to);
                    if( board.getMoveVerifier().isLegal(board, invasion) ) {
                        int forceSize = from.getForce().getMobileIndependentSize();
                        if( forceSize > getMinAttackStrength()
                                && map.get(to) > getMinAttackMotivation()
                                + forceSize * getUnitMultiplier() ) {
                            return invasion;
                        }
                    }
                }
            }
            return super.getMove(board);
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES)) {
            if( Math.random() >= getFreeMoveChance() ) {
                return SpecialMove.END_MOVE;
            }
            TerritoryValueMap map = evaluate(board);
            List<Territory> reverseOrder = new ArrayList<Territory>(map.getTerritories());
            Collections.reverse(reverseOrder);
            for( Territory to : map.getTerritories() ) {
                for( Territory from : reverseOrder ) {
                    TerritoryDuple freeMove = new TerritoryDuple(from, to);
                    if( board.getMoveVerifier().isLegal(board, freeMove) ) {
                        return freeMove;
                    }
                }
            }
            return SpecialMove.END_MOVE;
        }
        return super.getMove(board);
    }
    
    private double adjacentValue = 1.0;
    private double friendlyContinentValue = 2.0;
    private double hostileContinentValue = 0.2;
    private double unitMultiplier = -0.5;
    private double freeMoveChance = .80;
    private double minAttackMotivation = 7.0;
    private int minAttackStrength = 3;
    private double neutralValue = 5.0;
    
    protected TerritoryValueMap evaluate(Board board) {
        TerritoryValueMap result = new TerritoryValueMap();
        for( Territory territory : board.getTerritories() ) {
            boolean lockedIn = true;
            for( Territory adjacent : territory.getAdjacent() ) {
                if( adjacent.getOwner() == board.getCurrentPlayer() ) {
                    result.addValue(territory, getAdjacentValue());
                } else {
                    lockedIn = false;
                }
            }
            if( territory.getContinent() != null ) {
                for( Territory sameContinent : board.getContinent(territory.getContinent()) ) {
                    if( sameContinent.getOwner() == board.getCurrentPlayer() ) {
                        result.addValue(territory, getFriendlyContinentValue());
                    } else if( sameContinent.getOwner() != Player.NEUTRAL ) {
                        result.addValue(territory, getHostileContinentValue());
                    }
                }
            }
            if( territory.getOwner() == board.getCurrentPlayer() && lockedIn ) {
                result.set(territory, 0.0);
            }
            result.addValue(territory, getUnitMultiplier() * territory.getForce().getSize());
        }
        for( Territory territory : board.getTerritoriesOwned(Player.NEUTRAL) ) {
            result.addValue(territory, getNeutralValue());
        }
        return result;
    }

    public double getAdjacentValue() {
        return adjacentValue;
    }

    public void setAdjacentValue(double adjacentValue) {
        this.adjacentValue = adjacentValue;
    }

    public double getFriendlyContinentValue() {
        return friendlyContinentValue;
    }

    public void setFriendlyContinentValue(double friendlyContinentValue) {
        this.friendlyContinentValue = friendlyContinentValue;
    }

    public double getHostileContinentValue() {
        return hostileContinentValue;
    }

    public void setHostileContinentValue(double hostileContinentValue) {
        this.hostileContinentValue = hostileContinentValue;
    }

    public double getUnitMultiplier() {
        return unitMultiplier;
    }

    public void setUnitMultiplier(double unitMultiplier) {
        this.unitMultiplier = unitMultiplier;
    }

    public double getFreeMoveChance() {
        return freeMoveChance;
    }

    public void setFreeMoveChance(double freeMoveChance) {
        this.freeMoveChance = freeMoveChance;
    }

    public double getMinAttackMotivation() {
        return minAttackMotivation;
    }

    public void setMinAttackMotivation(double minAttackMotivation) {
        this.minAttackMotivation = minAttackMotivation;
    }

    public int getMinAttackStrength() {
        return minAttackStrength;
    }

    public void setMinAttackStrength(int minAttackStrength) {
        this.minAttackStrength = minAttackStrength;
    }

    public double getNeutralValue() {
        return neutralValue;
    }

    public void setNeutralValue(double neutralValue) {
        this.neutralValue = neutralValue;
    }
    
}
