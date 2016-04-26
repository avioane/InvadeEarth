/*
 * BetaAgent.java
 *
 * Created on August 7, 2005, 2:50 PM
 *
 */

package org.invade.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Card;
import org.invade.Continent;
import org.invade.Force;
import org.invade.ForcePlacement;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryDuple;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.commandcards.Armageddon;
import org.invade.commandcards.AssassinBomb;
import org.invade.commandcards.AssembleMODs;
import org.invade.commandcards.ContinentalBomb;
import org.invade.commandcards.FrequencyJam;
import org.invade.commandcards.InvadeEarth;
import org.invade.commandcards.Reinforcements;
import org.invade.commandcards.RocketStrike;
import org.invade.commandcards.ScatterBomb;
import org.invade.commandcards.StealthMODs;
import org.invade.miraclecards.AShrineIsPlundered;
import org.invade.miraclecards.AnEmpireIsBorn;
import org.invade.miraclecards.HeroesAreBorn;
import org.invade.miraclecards.OnlyAshRemains;
import org.invade.miraclecards.StormsRage;
import org.invade.miraclecards.TheGodsForsakeUs;
import org.invade.rules.ClassicRules;
import org.invade.rules.DefaultRules;
import org.invade.rules.GodstormRules;

/**
 *
 * @author Jonathan Crosmer
 */
public class BetaAgent extends RandomAgent {
    
    public String toString() {
        return "Beta Agent";
    }
    
    public BetaAgent() {
        // Disable random attacking
        setMaxRandomAttacksPerTurn(0);
        setMinRandomAttacksPerTurn(0);
    }
    
    public Object getMove(Board board) {
        
        // Any cards we attempt to play may have subsequent required choices
        Card card = board.getLastCardUsed();
        if( card != null ) {
            if(board.getTurnMode().equals(TurnMode.CHOOSE_PLAYER)) {
                // For FrequencyJam
                List<Player> players = new ArrayList<Player>(board.getPlayers());
                Collections.shuffle(players);
                for(Player player : players ) {
                    if(player != board.getCurrentPlayer()) {
                        return player;
                    }
                }
            } else if(board.getTurnMode().equals(TurnMode.CHOOSE_TERRITORY)) {
                Territory territory = pickAnImportantTerritory(board);
                if( territory != null ) {
                    return territory;
                }
            }
        } else {
            // Play cards, if possible
            card = playCard(board);
            if( card != null ) {
                return card;
            }
        }
        
        if(board.getTurnMode().equals(TurnMode.CLAIM_TERRITORIES)) {
            Territory territory = pickAnImportantTerritory(board);
            if( territory != null ) {
                return territory;
            }
        } else if(board.getTurnMode().equals(TurnMode.DESTROY_A_REGULAR_UNIT)) {
            List<Territory> territories = board.getTerritoriesOwned(board.getCurrentPlayer());
            Collections.sort(territories, new Comparator<Territory>() {
                public int compare(Territory first, Territory second) {
                    return second.getForce().getRegularUnits()
                    - first.getForce().getRegularUnits();
                }
            });
            for(Territory territory : territories) {
                if( territory.getForce().getRegularUnits() > 1 ) {
                    return territory;
                }
            }
        } else if(board.getTurnMode().equals(TurnMode.REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)) {
            // Use normal evaluation of territories except with special units
            // Attempt to place special units in highly populated territories
            Force force = board.getCurrentPlayer().getReinforcements().getOneUnit();
            List<Territory> territories = evaluate(board).getTerritories();
            if( force.getRegularUnits() <= 0 ) {
                // Sort territories so that those with the most units are first
                Collections.sort(territories, new Comparator<Territory>(){
                    public int compare(Territory first, Territory second) {
                        return second.getForce().getRegularUnits() - first.getForce().getRegularUnits();
                    }
                });
                // Scramble the first few so that not all commanders go in the same place
                // if we have more than a few territories
                if( territories.size() >= 4 ) {                    
                    Collections.shuffle(territories.subList(0, 3));
                }
            }
            for( Territory territory : territories ) {
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
                        int forceSize = from.getForce().getRegularUnits()
                        - (int)(from.getForce().getSpecialUnits().size() * getSpecialUnitMultiplier());
                        if( (forceSize > getMinAttackStrength()
                        || to.getOwner() == Player.NEUTRAL)
                        && from.getForce().getSpecialUnits().size()
                        < from.getForce().getRegularUnits() ) {
                            if( map.get(to) > getMinAttackMotivation()
                            + forceSize * getUnitMultiplier() ) {
                                return invasion;
                            }
                        }
                        /* Last turn make as many attacks as possible
                         * with regular units. */
                        int lastYear = board.getRules() instanceof ClassicRules
                                ? ((ClassicRules)board.getRules()).getEndYear()
                                : -1;
                        if( isBerserkOnLastTurn() && board.getYear() >= 0
                                && board.getYear() == lastYear
                                && from.getForce().getRegularUnits() > 0 ) {
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
                    if( from.getForce().getSize() > getMinAttackStrength() ) {
                        TerritoryDuple freeMove = new TerritoryDuple(from, to);
                        if( board.getMoveVerifier().isLegal(board, freeMove) ) {
                            return freeMove;
                        }
                    }
                }
            }
            return SpecialMove.END_MOVE;
        } else if(board.getTurnMode().equals(TurnMode.COMPLETE_FREE_MOVE) ) {
            return getForceToMoveIn(board);
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_ATTACK_FORCE)) {
            Force defaultForce = board.getAttackingTerritory().getForce().getDefaultAttack(board.getRules());
            // If territory cannot fall this turn, throw in everything we have
            if( board.getDefendingTerritory().getForce().getMobileIndependentSize()
            > board.getRules().getMaxDefenseDice() ) {
                return defaultForce;
            }
            // If territory is not a "dead end", we send in everything
            // Here we will let "dead end" be a territory where the getForceToMoveIn()
            // method would refuse a free move
            Force force = getForceToMoveIn(board);
            if( ! force.isEmpty() ) {
                return defaultForce;
            }
            // If territory might fall and it is a dead end, we do not want
            // to send in everything; try sending only regular units
            if( board.getAttackingTerritory().getForce().getRegularUnits() > 1 ) {
                force = new Force();
                force.setRegularUnits(
                        Math.min(board.getAttackingTerritory().getForce().getRegularUnits() - 1,
                        board.getRules().getMaxAttackDice()));
                return force;
            }
            // No regular units; send only one arbitrary unit
            return board.getAttackingTerritory().getForce().getOneMobileIndependentUnit();
            
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_ORDER)) {
            if( board.getRules() instanceof ClassicRules ) {
                if( ((ClassicRules)board.getRules()).getEndYear() == board.getYear() ) {
                    return board.getTurnChoices().get( board.getTurnChoices().size() - 1 );
                }
            }
            return board.getTurnChoices().get(0);
        } else if(board.getTurnMode().equals(TurnMode.BID)) {
            int maxEnergy = 0;
            for( Player player : board.getPlayers() ) {
                if( player != board.getCurrentPlayer() ) {
                    maxEnergy = Math.max(maxEnergy, player.getEnergy());
                }
            }
            if( board.getCurrentPlayer().getEnergy() > maxEnergy ) {
                return Integer.valueOf(maxEnergy + 1);
            }
        } else if(board.getTurnMode().equals(TurnMode.BUY_CARDS)
        || board.getTurnMode().equals(TurnMode.BID)) {
            if( board.getCurrentPlayer().getEnergy() >= getMinEnergyForPurchase() ) {
                return super.getMove(board);
            } else {
                return SpecialMove.END_MOVE;
            }
        } else if(board.getTurnMode().equals(TurnMode.BUY_UNITS)) {
            if( board.getCurrentPlayer().getEnergy() >= getMinEnergyForUnitPurchase() ) {
                List<SpecialUnit> units = new ArrayList<SpecialUnit>(board.getRules().getUnitsForPurchase());
                Collections.shuffle(units);
                for( SpecialUnit unit : units ) {
                    if( board.getMoveVerifier().isLegal(board, unit)
                    && !unit.equals( DefaultRules.SPACE_STATION )
                    && !unit.equals( GodstormRules.TEMPLE ) ) {
                        return unit;
                    }
                }
            }
            return SpecialMove.END_MOVE;
        }
        return super.getMove(board);
    }
    
    protected Card playCard(Board board) {
        // Play Armageddon first to conserve energy
        for( Card card : board.getCurrentPlayer().getCards() ) {
            if( card instanceof Armageddon
                    && board.getMoveVerifier().isLegal(board, card) ) {
                return card;
            }
        }
        for( Card card : board.getCurrentPlayer().getCards() ) {
            if( board.getMoveVerifier().isLegal(board, card) ) {
                if( card instanceof RocketStrike
                        || card instanceof Reinforcements
                        || card instanceof AssembleMODs
                        || card instanceof AssassinBomb
                        || card instanceof FrequencyJam
                        || card instanceof StormsRage
                        || card instanceof AnEmpireIsBorn
                        || card instanceof HeroesAreBorn
                        || card instanceof OnlyAshRemains 
                        || card instanceof AShrineIsPlundered
                        || card instanceof TheGodsForsakeUs ) {
                    
                    return card;
                }
                // Do not play StealthMOD's to help enemies
                if( card instanceof StealthMODs) {
                    if( board.getDefendingTerritory().getOwner() == board.getCurrentPlayer() ) {
                        return card;
                    }
                } else if( card instanceof InvadeEarth ) {
                    List<Territory> territories = board.getTerritoriesOwned(
                            board.getCurrentPlayer(), TerritoryType.MOON);
                    int moonStrength = 0;
                    for( Territory territory : territories ) {
                        moonStrength += territory.getForce().getRegularUnits();
                    }
                    if( moonStrength >= territories.size() * 2 + 4 ) {
                        return card;
                    }
                } else if( card instanceof ScatterBomb
                        || card instanceof ContinentalBomb ) {
                    TerritoryType type = card instanceof ScatterBomb ?
                        ((ScatterBomb)card).getTerritoryType()
                        : ((ContinentalBomb)card).getTerritoryType();
                    List<Territory> hostile = board.getTerritoriesHostileTo(
                            board.getCurrentPlayer(), type );
                    List<Territory> owned = board.getTerritoriesOwned(
                            board.getCurrentPlayer(), type );
                    if( hostile.size() > owned.size() ) {
                        return card;
                    }
                } else if( card instanceof AutomaticCard ) {
                    return card;
                }
            }
        }
        return null;
    }
    
    private double adjacentValue = 1.0;
    private double friendlyContinentValue = 1.2;
    private double hostileContinentValue = -0.4;
    private double oneNeutralLeftInContinent = 25.0;
    private double unitMultiplier = -0.75;
    private double dominationUnitMultiplier = -0.1;
    private double specialUnitMultiplier = 2.5;
    private double freeMoveChance = .90;
    private double minAttackMotivation = 1.5;
    private int minAttackStrength = 2;
    private double neutralValue = 7.0;
    private double continentLockedInValue = 0.0;
    private double continentNotLockedInValue = -.3;
    private double dominationThreshold = 0.55;
    private int minEnergyForPurchase = 3;
    private int minEnergyForUnitPurchase = 6;
    private boolean berserkOnLastTurn = true;
    private double leaveWithImmobileUnits = 2.0;
    private double leaveBehindContinentBorder = 0.5;
    private double lockedInValue = -30.0;
    
    // since landing sites are handled separately we have to do this
    private double landingSiteValue = 6.0; 
    
    protected Territory pickAnImportantTerritory(Board board) {
        for( Territory territory : evaluate(board).getTerritories() ) {
            if( board.getMoveVerifier().isLegal(board, territory) ) {
                return territory;
            }
        }
        return null;
    }
    
    protected Force getForceToMoveIn(Board board) {
        // Let's not walk into a plague
        if( board.getDefendingTerritory().isPlague() ) {
            return new Force();
        }
        /* If defending territory is surrounded by friendly territories,
         * do not move in units */
        for(Territory adjacent : board.getDefendingTerritory().getAdjacent()) {
            if(adjacent.getOwner() != board.getAttackingTerritory().getOwner()) {
                // If some units are immoble or we are leaving a border territory,
                // leave some regular units behind
                Force freeMove = board.getAttackingTerritory().getForce().getDefaultFreeMove();
                double leaveBehind = 0.0;
                for(SpecialUnit unit : board.getAttackingTerritory().getForce().getSpecialUnits()) {
                    if( !unit.isMobile() ) {
                        leaveBehind += getLeaveWithImmobileUnits();
                    }
                }
                for( Territory adjacent2 : board.getAttackingTerritory().getAdjacent() ) {
                    if( adjacent2.getContinent() != board.getAttackingTerritory().getContinent()
                    && board.getAttackingTerritory().getContinent() != null ) {
                        leaveBehind += getLeaveBehindContinentBorder() * Math.max(1,
                                board.getAttackingTerritory().getContinent().getBonus());
                    }
                }
                freeMove.setRegularUnits(freeMove.getRegularUnits() - (int)leaveBehind);
                if(freeMove.getRegularUnits() < 0) {
                    // Not enough units to leave behind;
                    // decline free move
                    return new Force();
                }
                return freeMove;
            }
        }
        return new Force();
    }
    
    protected TerritoryValueMap evaluate(Board board) {
        TerritoryValueMap result = new TerritoryValueMap();
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() == board.getCurrentPlayer() ) {
                result.addValue(territory, getAdjacentValue() * territory.getAdjacent().size());
            }
            if( territory.getContinent() != null ) {
                int neutralLeftInContinent = 0;
                for( Territory sameContinent : board.getContinent(territory.getContinent()) ) {
                    if( sameContinent.getOwner() == board.getCurrentPlayer() ) {
                        result.addValue(territory, getFriendlyContinentValue());
                    } else if( sameContinent.getOwner() != Player.NEUTRAL ) {
                        result.addValue(territory, getHostileContinentValue());
                    }
                    if( sameContinent.getOwner() == Player.NEUTRAL ) {
                        neutralLeftInContinent++;
                    }
                }
                if( neutralLeftInContinent == 1 && territory.getOwner() == Player.NEUTRAL ) {
                    result.addValue(territory, getOneNeutralLeftInContinent() );
                }
                result.addValue(territory, getContinentValue(board, territory.getContinent()));
            }            
            boolean domination =
                    (double)board.getTerritoriesOwned(board.getCurrentPlayer()).size()
                    / (double)board.getTerritories().size() >= getDominationThreshold();
            if( territory.getOwner() == board.getCurrentPlayer()
            && isLockedIn(territory, ! domination) ) {
                result.addValue(territory, getLockedInValue());
            }
            result.addValue(territory,
                    (domination ? getDominationUnitMultiplier() : getUnitMultiplier())
                    * territory.getForce().getRegularUnits());
            if( board.getTurnMode() == TurnMode. DECLARE_FREE_MOVES
                    || board.getTurnMode() == TurnMode.COMPLETE_FREE_MOVE ) {
                result.addValue(territory, -1 * getSpecialUnitMultiplier()
                * territory.getForce().getSpecialUnits().size());
            } else {
                result.addValue(territory, getSpecialUnitMultiplier()
                * territory.getForce().getSpecialUnits().size());
            }
            if( territory.isLandingSite() ) {
                result.addValue(territory, getLandingSiteValue());
            }
            if( territory.isPlague() ) {
                result.set(territory, 0.0);
            }
        }
        for( Territory territory : board.getTerritoriesOwned(Player.NEUTRAL) ) {
            result.addValue(territory, getNeutralValue());
        }
        return result;
    }
    
    
    protected boolean isLockedIn(Territory territory, boolean considerContinentBorders ) {
        if( territory.isLandingSite() ) {
            return false;
        }
        for( Territory adjacent : territory.getAdjacent() ) {
            if( adjacent.getOwner() != territory.getOwner()
            || (considerContinentBorders && adjacent.getContinent() != territory.getContinent()) ) {
                return false;
            }
        }
        return true;
    }
    
    protected double getContinentValue(Board board, Continent continent) {
        double result = 0.0;
        for( Territory territory : board.getContinent(continent) ) {
            if( isLockedIn(territory, true) ) {
                result += getContinentLockedInValue();
            } else {
                result += getContinentNotLockedInValue();
            }
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
    
    public double getContinentLockedInValue() {
        return continentLockedInValue;
    }
    
    public void setContinentLockedInValue(double continentLockedInValue) {
        this.continentLockedInValue = continentLockedInValue;
    }
    
    public double getContinentNotLockedInValue() {
        return continentNotLockedInValue;
    }
    
    public void setContinentNotLockedInValue(double continentNotLockedInValue) {
        this.continentNotLockedInValue = continentNotLockedInValue;
    }
    
    public double getDominationThreshold() {
        return dominationThreshold;
    }
    
    public void setDominationThreshold(double dominationThreshold) {
        this.dominationThreshold = dominationThreshold;
    }
    
    public double getDominationUnitMultiplier() {
        return dominationUnitMultiplier;
    }
    
    public void setDominationUnitMultiplier(double dominationUnitMultiplier) {
        this.dominationUnitMultiplier = dominationUnitMultiplier;
    }
    
    public int getMinEnergyForPurchase() {
        return minEnergyForPurchase;
    }
    
    public void setMinEnergyForPurchase(int minEnergyForPurchase) {
        this.minEnergyForPurchase = minEnergyForPurchase;
    }
    
    public int getMinEnergyForUnitPurchase() {
        return minEnergyForUnitPurchase;
    }
    
    public void setMinEnergyForUnitPurchase(int minEnergyForUnitPurchase) {
        this.minEnergyForUnitPurchase = minEnergyForUnitPurchase;
    }
    
    public boolean isBerserkOnLastTurn() {
        return berserkOnLastTurn;
    }
    
    public void setBerserkOnLastTurn(boolean berserkOnLastTurn) {
        this.berserkOnLastTurn = berserkOnLastTurn;
    }
    
    public void setLeaveWithImmobileUnits(double leaveWithImmobileUnits) {
        this.leaveWithImmobileUnits = leaveWithImmobileUnits;
    }
    
    public double getLeaveBehindContinentBorder() {
        return leaveBehindContinentBorder;
    }
    
    public void setLeaveBehindContinentBorder(double leaveBehindContinentBorder) {
        this.leaveBehindContinentBorder = leaveBehindContinentBorder;
    }
    
    public double getLeaveWithImmobileUnits() {
        return leaveWithImmobileUnits;
    }
    
    public double getOneNeutralLeftInContinent() {
        return oneNeutralLeftInContinent;
    }
    
    public void setOneNeutralLeftInContinent(double oneNeutralLeftInContinent) {
        this.oneNeutralLeftInContinent = oneNeutralLeftInContinent;
    }
    
    public double getSpecialUnitMultiplier() {
        return specialUnitMultiplier;
    }
    
    public void setSpecialUnitMultiplier(double specialUnitMultiplier) {
        this.specialUnitMultiplier = specialUnitMultiplier;
    }
    
    public double getLockedInValue() {
        return lockedInValue;
    }
    
    public void setLockedInValue(double lockedInValue) {
        this.lockedInValue = lockedInValue;
    }

    public double getLandingSiteValue() {
        return landingSiteValue;
    }

    public void setLandingSiteValue(double landingSiteValue) {
        this.landingSiteValue = landingSiteValue;
    }
    
}
