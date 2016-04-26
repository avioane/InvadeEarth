/*
 * DefaultRules.java
 *
 * Created on July 12, 2005, 11:04 AM
 *
 */

package org.invade.rules;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.invade.Board;
import org.invade.Card;
import org.invade.CommonBoardEvents;
import org.invade.CommonBoardMethods;
import org.invade.EconomicCard;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameAlgorithm;
import org.invade.GameThread;
import org.invade.PlayableDeck;
import org.invade.Player;
import org.invade.PlayerChoice;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.SuppressedProperty;
import org.invade.commandcards.Armageddon;
import org.invade.commandcards.AssassinBomb;
import org.invade.commandcards.AssembleMODs;
import org.invade.commandcards.CeaseFire;
import org.invade.commandcards.ColonyInfluence;
import org.invade.commandcards.ContinentalBomb;
import org.invade.commandcards.DeathTrap;
import org.invade.commandcards.DecoysRevealed;
import org.invade.commandcards.EnergyCrisis;
import org.invade.commandcards.EnergyExtraction;
import org.invade.commandcards.Evacuation;
import org.invade.commandcards.FrequencyJam;
import org.invade.commandcards.HiddenEnergy;
import org.invade.commandcards.InvadeEarth;
import org.invade.commandcards.MODReduction;
import org.invade.commandcards.Redeployment;
import org.invade.commandcards.Reinforcements;
import org.invade.commandcards.RocketStrike;
import org.invade.commandcards.ScatterBomb;
import org.invade.commandcards.ScoutForces;
import org.invade.commandcards.StealthMODs;
import org.invade.commandcards.StealthStation;
import org.invade.commandcards.TerritorialStation;

public class DefaultRules extends ClassicRules {
    
    public String toString() {
        return "Invade Earth A.D. 2210";
    }
    
    public static final SpecialUnit SPACE_STATION
            = new SpecialUnit("Space Station", 5, Color.BLACK, false, true,
            false, 4, 1, -1, "spacestation.png");
    
    public static final SpecialUnit LAND
            = new SpecialUnit("Land Commander", 3,
            TerritoryType.LAND.getColor(), true, true, false, 1, -1, -1,
            "land.png");
    
    public static final SpecialUnit DIPLOMAT
            = new SpecialUnit("Diplomat", 3, new Color(0, 96, 64),
            true, true, false, 1, -1, -1, "diplomat.png");
    
    public static final SpecialUnit NAVAL
            = new SpecialUnit("Naval Commander", 3,
            TerritoryType.WATER.getColor(), true, true, false, 1, -1, -1,
            "naval.png");
    
    public static final SpecialUnit SPACE
            = new SpecialUnit("Space Commander", 3,
            TerritoryType.MOON.getColor(), true, true, false, 1, -1, -1,
            "space.png");
    
    public static final SpecialUnit NUCLEAR
            = new SpecialUnit("Nuclear Commander", 3, new Color(255, 128, 0),
            true, true, false, 1, -1, -1, "nuclear.png");
    
    public DefaultRules() {
        setEvenDistributionRegular(3);
        setEndYear(5);
        setContestedTerritoriesNeeded(3);
        setAcknowledgeInvasions(true);
        setAllowLongFreeMoves(true);
    }
    
    private int maxCardsPerTurn = 4;              // < 0 indicates unlimited
    private int evenDistributionSpecial = 1;
    private int startingEnergy = 3;
    private int numberDevastated = 4;
    
    public GameAlgorithm createGameAlgorithm() {
        return new DefaultGameAlgorithm();
    }
    
    public java.util.List<PlayableDeck> getStartingDecks(Board board) {
        List<PlayableDeck> result = new ArrayList<PlayableDeck>();
        
        PlayableDeck landDeck = new PlayableDeck();
        landDeck.setName("Land Command Card");
        landDeck.setRequiredUnit(LAND);
        landDeck.add(
                new AssembleMODs(LAND, 1, TerritoryType.LAND),
                new AssembleMODs(LAND, 1, TerritoryType.LAND),
                new AssembleMODs(LAND, 1, TerritoryType.LAND),
                new Reinforcements(LAND, 0, TerritoryType.LAND),
                new Reinforcements(LAND, 0, TerritoryType.LAND),
                new Reinforcements(LAND, 0, TerritoryType.LAND),
                new DeathTrap(LAND, 3, TerritoryType.LAND, "Land Death Trap"),
                new StealthMODs(LAND, 0, TerritoryType.LAND),
                new StealthMODs(LAND, 0, TerritoryType.LAND),
                new StealthMODs(LAND, 0, TerritoryType.LAND),
                new StealthMODs(LAND, 0, TerritoryType.LAND),
                new StealthMODs(LAND, 0, TerritoryType.LAND),
                new StealthStation(LAND, 0, TerritoryType.LAND),
                new ScoutForces(LAND, 0, TerritoryType.LAND),
                new ScoutForces(LAND, 0, TerritoryType.LAND),
                new ScoutForces(LAND, 0, TerritoryType.LAND),
                new ColonyInfluence(LAND, 0),
                new ColonyInfluence(LAND, 0),
                new FrequencyJam(LAND, 0),
                new FrequencyJam(LAND, 0)
                );
        result.add(landDeck);
        
        PlayableDeck navalDeck = new PlayableDeck();
        navalDeck.setName("Naval Command Card");
        navalDeck.setRequiredUnit(NAVAL);
        navalDeck.add(
                new AssembleMODs(NAVAL, 1, TerritoryType.WATER),
                new AssembleMODs(NAVAL, 1, TerritoryType.WATER),
                new AssembleMODs(NAVAL, 1, TerritoryType.WATER),
                new Reinforcements(NAVAL, 0, TerritoryType.WATER),
                new Reinforcements(NAVAL, 0, TerritoryType.WATER),
                new DeathTrap(NAVAL, 3, TerritoryType.WATER, "Water Death Trap"),
                new StealthMODs(NAVAL, 0, TerritoryType.WATER),
                new StealthMODs(NAVAL, 0, TerritoryType.WATER),
                new StealthMODs(NAVAL, 0, TerritoryType.WATER),
                new StealthMODs(NAVAL, 0, TerritoryType.WATER),
                new StealthMODs(NAVAL, 0, TerritoryType.WATER),
                new HiddenEnergy(NAVAL, 0, TerritoryType.WATER),
                new HiddenEnergy(NAVAL, 0, TerritoryType.WATER),
                new HiddenEnergy(NAVAL, 0, TerritoryType.WATER),
                new HiddenEnergy(NAVAL, 0, TerritoryType.WATER),
                new ColonyInfluence(NAVAL, 0),
                new ColonyInfluence(NAVAL, 0),
                new FrequencyJam(NAVAL, 0),
                new FrequencyJam(NAVAL, 0)
                );
        result.add(navalDeck);
        
        PlayableDeck spaceDeck = new PlayableDeck();
        spaceDeck.setName("Space Command Card");
        spaceDeck.setRequiredUnit(SPACE);
        spaceDeck.add(
                new AssembleMODs(SPACE, 1, TerritoryType.MOON),
                new AssembleMODs(SPACE, 1, TerritoryType.MOON),
                new AssembleMODs(SPACE, 1, TerritoryType.MOON),
                new Reinforcements(SPACE, 0, TerritoryType.MOON),
                new Reinforcements(SPACE, 0, TerritoryType.MOON),
                new Reinforcements(SPACE, 0, TerritoryType.MOON),
                new DeathTrap(SPACE, 3, TerritoryType.MOON, "Orbital Mines"),
                new DeathTrap(SPACE, 3, TerritoryType.MOON, "Orbital Mines"),
                new StealthMODs(SPACE, 0, TerritoryType.MOON),
                new StealthMODs(SPACE, 0, TerritoryType.MOON),
                new StealthMODs(SPACE, 0, TerritoryType.MOON),
                new StealthMODs(SPACE, 0, TerritoryType.MOON),
                new StealthMODs(SPACE, 0, TerritoryType.MOON),
                new ColonyInfluence(SPACE, 0),
                new ColonyInfluence(SPACE, 0),
                new FrequencyJam(SPACE, 0),
                new FrequencyJam(SPACE, 0),
                new EnergyExtraction(SPACE, 1, TerritoryType.MOON),
                new InvadeEarth(SPACE, 0),
                new InvadeEarth(SPACE, 0),
                new InvadeEarth(SPACE, 0)
                );
        result.add(spaceDeck);
        
        
        PlayableDeck diplomatDeck = new PlayableDeck();
        diplomatDeck.setName("Diplomat Command Card");
        diplomatDeck.setRequiredUnit(DIPLOMAT);
        diplomatDeck.add(
                new ColonyInfluence(DIPLOMAT, 0),
                new ColonyInfluence(DIPLOMAT, 0),
                new ColonyInfluence(DIPLOMAT, 0),
                new ColonyInfluence(DIPLOMAT, 0),
                new TerritorialStation(DIPLOMAT, 1),
                new TerritorialStation(DIPLOMAT, 1),
                new TerritorialStation(DIPLOMAT, 1),
                new DecoysRevealed(DIPLOMAT, 0),
                new DecoysRevealed(DIPLOMAT, 0),
                new Evacuation(DIPLOMAT, 0),
                new Evacuation(DIPLOMAT, 0),
                new Redeployment(DIPLOMAT, 0),
                new Redeployment(DIPLOMAT, 0),
                new Redeployment(DIPLOMAT, 0),
                new MODReduction(DIPLOMAT, 2),
                new MODReduction(DIPLOMAT, 2),
                new EnergyCrisis(DIPLOMAT, 0),
                new EnergyCrisis(DIPLOMAT, 0),
                new CeaseFire(DIPLOMAT, 2),
                new CeaseFire(DIPLOMAT, 2)
                );
        result.add(diplomatDeck);
        
        PlayableDeck nuclearDeck = new PlayableDeck();
        nuclearDeck.setName("Nuclear Command Card");
        nuclearDeck.setRequiredUnit(NUCLEAR);
        nuclearDeck.add(
                new Armageddon(NUCLEAR, 4),
                new RocketStrike(NUCLEAR, 2, TerritoryType.LAND),
                new RocketStrike(NUCLEAR, 2, TerritoryType.LAND),
                new RocketStrike(NUCLEAR, 2, TerritoryType.WATER),
                new RocketStrike(NUCLEAR, 2, TerritoryType.WATER),
                new RocketStrike(NUCLEAR, 2, TerritoryType.MOON),
                new RocketStrike(NUCLEAR, 2, TerritoryType.MOON),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.LAND, 3),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.LAND, 3),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.LAND, 3),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.WATER, 2),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.WATER, 2),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.MOON, 2),
                new ScatterBomb(NUCLEAR, 1, TerritoryType.MOON, 2),
                new ContinentalBomb(NUCLEAR, 3, TerritoryType.LAND, "The Mother"),
                new ContinentalBomb(NUCLEAR, 3, TerritoryType.WATER, "Aqua Brother"),
                new ContinentalBomb(NUCLEAR, 3, TerritoryType.MOON, "Nicky Boy"),
                new AssassinBomb(NUCLEAR, 1),
                new AssassinBomb(NUCLEAR, 1),
                new AssassinBomb(NUCLEAR, 1)
                );
        result.add(nuclearDeck);
        
        return result;
    }
    
    public int getMaxCardsPerTurn() {
        return maxCardsPerTurn;
    }
    
    public void setMaxCardsPerTurn(int maxCardsPerTurn) {
        this.maxCardsPerTurn = maxCardsPerTurn;
    }
    
    public int getEvenDistributionSpecial() {
        return evenDistributionSpecial;
    }
    
    public void setEvenDistributionSpecial(int evenDistributionSpecial) {
        this.evenDistributionSpecial = evenDistributionSpecial;
    }
    
    public int getStartingEnergy() {
        return startingEnergy;
    }
    
    public void setStartingEnergy(int startingEnergy) {
        this.startingEnergy = startingEnergy;
    }
    
    public int getNumberDevastated() {
        return numberDevastated;
    }
    
    public void setNumberDevastated(int numberDevastated) {
        this.numberDevastated = numberDevastated;
    }
    
    
    
    // Eliminate unused properties so they will not be displayed in RulesDialog
    @SuppressedProperty public int getWildCardCount() { return 0; }
    @SuppressedProperty public int[] getBonusValues() { return new int[]{}; }
    @SuppressedProperty public int getBonusAddend() { return 0; }
    @SuppressedProperty public int getMaxCardsAtTurnStart() { return 0; }
    @SuppressedProperty public int getMaxCardsAfterCapture() { return 0; }
    @SuppressedProperty public int getOwnTerritoryCardBonus() { return 0; }
    @SuppressedProperty public boolean isOnlyOneOwnTerritoryBonus() { return false; }
    
    
    
    class DefaultGameAlgorithm extends ClassicGameAlgorithm {
        
        public void startGame(Board board, GameThread gameThread) throws EndGameException {
            this.board = board;
            this.gameThread = gameThread;
            board.sendMessage(" -- Game start -- ");
            board.resetDecks();
            devastateTerritories();
            receiveStartingEnergy();
            receiveStartingRegularUnits();
            claimTerritories();
            checkAllForElimination();
            evenDistribution(getEvenDistributionRegular());
            board.setCurrentPlayer(board.getPlayers().get(0));
            receiveStartingSpecialUnits();
            evenDistribution(getEvenDistributionSpecial());
            while (CommonBoardMethods.areMultiplePlayersAlive(board)
            && (getEndYear() < 0 || board.getYear() < getEndYear()) ) {
                board.setYear(board.getYear() + 1);
                placeBids();
                chooseTurnOrder();
                for( Player player : board.getPlayers() ) {
                    if( !player.isAlive() ) {
                        continue;
                    }
                    board.setCurrentPlayer(player);
                    board.setAttackingTerritory(null);
                    board.setDefendingTerritory(null);
                    board.setTurnMode(TurnMode.TURN_START);
                    CommonBoardEvents.checkCardsInPlay(board, gameThread);
                    checkAllForElimination();
                    if( !player.isAlive() ) {
                        continue;
                    }
                    receiveSupplies();
                    checkAllForElimination();
                    if( !player.isAlive() ) {
                        continue;
                    }
                    CommonBoardEvents.placeReinforcements(board, gameThread);
                    buyUnits();
                    CommonBoardEvents.checkCardsInPlay(board, gameThread);
                    CommonBoardEvents.placeReinforcements(board, gameThread);
                    buyCards();
                    CommonBoardEvents.checkCardsInPlay(board, gameThread);
                    CommonBoardEvents.placeReinforcements(board, gameThread);
                    declareInvasions();
                    declareFreeMoves();
                    checkAllForElimination();
                }
            }
            
            board.sendMessage("End of game");
            CommonBoardEvents.getAcknowledgements(board, gameThread,
                    TurnMode.ACKNOWLEDGE_GAME_OVER, board.getLivingPlayers());
            board.setTurnMode(TurnMode.GAME_OVER);
            board.sendMessage(" -- Game over --");
        }
        
        public void devastateTerritories() throws EndGameException {
            for( int i = 0; i < getNumberDevastated(); ++i ) {
                // Draw but do not discard
                board.getTerritoryDeck(TerritoryType.LAND).draw().setDevastated(true, board);
            }
        }
        
        public void receiveStartingEnergy() throws EndGameException {
            for( Player player : board.getPlayers() ) {
                player.addEnergy(getStartingEnergy());
            }
        }
        
        public void receiveStartingSpecialUnits() throws EndGameException {
            Force starting = new Force();
            starting.getSpecialUnits().add(SPACE_STATION);
            starting.getSpecialUnits().add(DIPLOMAT);
            starting.getSpecialUnits().add(LAND);
            for( Player player : board.getPlayers() ) {
                player.getReinforcements().add(starting);
            }
        }
        
        public void placeBids() throws EndGameException {
            List<PlayerChoice> choices = new ArrayList<PlayerChoice>();
            board.setTurnMode(TurnMode.BID);
            for( Player player : board.getLivingPlayers() ) {
                board.setCurrentPlayer(player);
                int value = (Integer)gameThread.take();
                choices.add(new PlayerChoice(player, value + getBidModifier(board)));
            }
            
            Collections.shuffle(choices, board.getRandom());   // Tie breakers are random
            Collections.sort(choices);
            Collections.reverse(choices);
            board.getPlayers().removeAll(board.getLivingPlayers());
            for( PlayerChoice bid : choices ) {
                board.getPlayers().add(bid.getPlayer());
                board.setCurrentPlayer(bid.getPlayer());
                bid.getPlayer().addEnergy( - (bid.getChoice() - getBidModifier(board)) );
            }
        }
        
        public int getBidModifier(Board board) {
            int modifier = 0;
            for( Card card : board.getCardsInPlay() ) {
                if( card instanceof EconomicCard
                        && card.getPlayer() == board.getCurrentPlayer() ) {
                    modifier += ((EconomicCard)card).getBidChange(board);
                }
            }
            return modifier;
        }
        
        public void chooseTurnOrder() throws EndGameException {
            List<PlayerChoice> choices = new ArrayList<PlayerChoice>();
            board.setTurnMode(TurnMode.CHOOSE_ORDER);
            board.getTurnChoices().clear();
            for( int i = 1; i <= board.getLivingPlayers().size(); ++i ) {
                board.getTurnChoices().add(i);
            }
            for( Player player : board.getLivingPlayers() ) {
                board.setCurrentPlayer(player);
                int value = (Integer)gameThread.take();
                board.sendMessage(player.getName() + " selects turn order #" + value);
                choices.add(new PlayerChoice(player, value));
                board.getTurnChoices().remove(Integer.valueOf(value));
            }
            
            Collections.sort(choices);
            board.getPlayers().removeAll(board.getLivingPlayers());
            for( PlayerChoice choice : choices ) {
                board.getPlayers().add(choice.getPlayer());
            }
        }
        
        public void receiveSupplies() throws EndGameException {
            Player player = board.getCurrentPlayer();
            int supply = CommonBoardMethods.getBasicSupply(board, board.getCurrentPlayer())
            + CommonBoardMethods.getContinentBonuses(board, board.getCurrentPlayer());
            player.addEnergy(supply);
            player.getReinforcements().addRegularUnits(supply);
            board.sendMessage(player.getName() + " receives " + supply + " units and " + getCurrencyName().toLowerCase());
            
            // Space stations generate one unit per turn
            for( Territory territory : board.getTerritories() ) {
                if( territory.getOwner() == player
                        && territory.getForce().getSpecialUnits().contains(
                        SPACE_STATION) ) {
                    territory.getForce().addRegularUnits(1);
                }
            }
        }
        
        public void buyUnits() throws EndGameException {
            while(true) {
                board.setTurnMode(TurnMode.BUY_UNITS);
                Object taken = gameThread.take();
                if( taken == SpecialMove.END_MOVE ) {
                    break;
                }
                SpecialUnit unit = (SpecialUnit)taken;
                board.sendMessage(board.getCurrentPlayer().getName() + " buys a " + unit);
                board.getCurrentPlayer().addEnergy( - unit.getPrice() );
                board.getCurrentPlayer().getReinforcements().getSpecialUnits().add(unit);
            }
        }
        
        public void buyCards() throws EndGameException {
            for(int i = 0; getMaxCardsPerTurn() < 0
                    || i < getMaxCardsPerTurn(); ++i ) {
                board.setTurnMode(TurnMode.BUY_CARDS);
                Object taken = gameThread.take();
                if( taken == SpecialMove.END_MOVE ) {
                    break;
                }
                PlayableDeck deck = (PlayableDeck)taken;
                board.sendMessage(board.getCurrentPlayer().getName() + " buys a " + deck);
                board.getCurrentPlayer().addEnergy( - deck.getPrice(board) );
                board.getCurrentPlayer().getCards().add( deck.draw() );
            }
        }
        
        public void receiveContestedTerritoryBonus() throws EndGameException {
            board.getCurrentPlayer().addEnergy(1);
            drawCard();
        }
        
        
        public void surrenderCard(Card card) {
            card.getDeck().discard(card);
        }
        
        public void afterCardSurrender() {}
        
    }
    
    
    
    public List<SpecialUnit> getUnitsForPurchase() {
        return Arrays.asList(new SpecialUnit[]{
            SPACE_STATION, LAND, DIPLOMAT, NAVAL, SPACE, NUCLEAR });
    }
    
    public boolean hasAttackBonus(SpecialUnit specialUnit,
            TerritoryType territoryType) {
        if( specialUnit == LAND && territoryType.equals(TerritoryType.LAND) ) {
            return true;
        } else if( specialUnit == NAVAL && territoryType.equals(TerritoryType.WATER) ) {
            return true;
        } else if( specialUnit == SPACE && territoryType.equals(TerritoryType.MOON) ) {
            return true;
        } else if( specialUnit == NUCLEAR ) {
            return true;
        }
        return false;
    }
    
    @SuppressedProperty public String getCurrencyName() {
        return "Energy";
    }
    
    @SuppressedProperty public String getTurnName() {
        return "Year";
    }
    
}
