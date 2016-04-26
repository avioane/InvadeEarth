/*
 * ClassicRules.java
 *
 * Created on July 30, 2005, 9:15 PM
 *
 */

package org.invade.rules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.CommonBoardEvents;
import org.invade.CommonBoardMethods;
import org.invade.DiceType;
import org.invade.Die;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.ForcePlacement;
import org.invade.ForceVacuum;
import org.invade.GameAlgorithm;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryDuple;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.classic.BonusCard;

/**
 *
 * @author Jonathan Crosmer
 */
public class ClassicRules extends AbstractRules {
    
    public String toString() {
        return "Classic";
    }
    
    private int evenDistributionRegular = 1;
    private int endYear = -1;                     // < 0 indicates no end year
    private int startingRegularUnits = 40;        // for two players
    private int startingUnitsSubtrahend = 5;      // for each player after the second
    private int minStartingUnits = 20;
    private int contestedTerritoriesNeeded = 1;
    private int maxFreeMovesPerTurn = 1;             // < 0 indicates unlimited
    private boolean acknowledgeInvasions = false;
    private boolean defenderAcknowledgeInvasions = false;
    private boolean acknowledgeInvasionOnlyWhenNecessary = true;
    private int wildCardCount = 2;
    private int bonusValues[] = {4, 6, 8, 12, 15};
    private int bonusAddend = 5;                  // < 0 indicates reset result index
    private int maxCardsAtTurnStart = 4;          // < 0 indicates unlimited
    private int maxCardsAfterCapture = 5;         // < 0 indicates unlimited
    private int ownTerritoryCardBonus = 2;
    private boolean onlyOneOwnTerritoryBonus = true;
    private boolean randomTerritoryAssignment = false;
    
    public GameAlgorithm createGameAlgorithm() {
        return new ClassicGameAlgorithm();
    }
    
    public List<PlayableDeck> getStartingDecks(Board board) {
        List<PlayableDeck> result = new ArrayList<PlayableDeck>();
        PlayableDeck deck = new PlayableDeck();
        deck.setName(BonusCard.DEFAULT_DECK_NAME);
        BonusCard.Type types[] = BonusCard.Type.values();
        int index = 0;
        for(Territory territory : board.getTerritories()) {
            if( territory.getType().equals(TerritoryType.LAND) ) {
                deck.add(new BonusCard(types[index], territory));
                index++;
                index %= types.length;
            }
        }
        for( int i = 0; i < getWildCardCount(); ++i ) {
            deck.add(new BonusCard());
        }
        result.add(deck);
        return result;
    }
    
    public int getEvenDistributionRegular() {
        return evenDistributionRegular;
    }
    
    public void setEvenDistributionRegular(int evenDistributionRegular) {
        this.evenDistributionRegular = evenDistributionRegular;
    }
    
    public int getEndYear() {
        return endYear;
    }
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    
    public int getStartingRegularUnits() {
        return startingRegularUnits;
    }
    
    public void setStartingRegularUnits(int startingRegularUnits) {
        this.startingRegularUnits = startingRegularUnits;
    }
    
    public int getStartingUnitsSubtrahend() {
        return startingUnitsSubtrahend;
    }
    
    public void setStartingUnitsSubtrahend(int startingUnitsSubtrahend) {
        this.startingUnitsSubtrahend = startingUnitsSubtrahend;
    }
    
    public int getMinStartingUnits() {
        return minStartingUnits;
    }
    
    public void setMinStartingUnits(int minStartingUnits) {
        this.minStartingUnits = minStartingUnits;
    }
    
    public int getContestedTerritoriesNeeded() {
        return contestedTerritoriesNeeded;
    }
    
    public void setContestedTerritoriesNeeded(int contestedTerritoriesNeeded) {
        this.contestedTerritoriesNeeded = contestedTerritoriesNeeded;
    }
    
    public int getMaxFreeMovesPerTurn() {
        return maxFreeMovesPerTurn;
    }
    
    public void setMaxFreeMovesPerTurn(int maxFreeMovesPerTurn) {
        this.maxFreeMovesPerTurn = maxFreeMovesPerTurn;
    }
    
    public boolean isAcknowledgeInvasions() {
        return acknowledgeInvasions;
    }
    
    public void setAcknowledgeInvasions(boolean acknowledgeInvasions) {
        this.acknowledgeInvasions = acknowledgeInvasions;
    }
    
    public boolean isDefenderAcknowledgeInvasions() {
        return defenderAcknowledgeInvasions;
    }
    
    public void setDefenderAcknowledgeInvasions(boolean defenderAcknowledgeInvasions) {
        this.defenderAcknowledgeInvasions = defenderAcknowledgeInvasions;
    }
    
    public int getWildCardCount() {
        return wildCardCount;
    }
    
    public void setWildCardCount(int wildCardCount) {
        this.wildCardCount = wildCardCount;
    }
    
    public int[] getBonusValues() {
        return bonusValues;
    }
    
    public void setBonusValues(int[] bonusValues) {
        this.bonusValues = bonusValues;
    }
    
    public int getBonusAddend() {
        return bonusAddend;
    }
    
    public void setBonusAddend(int bonusAddend) {
        this.bonusAddend = bonusAddend;
    }
    
    public int getMaxCardsAtTurnStart() {
        return maxCardsAtTurnStart;
    }
    
    public void setMaxCardsAtTurnStart(int maxCardsAtTurnStart) {
        this.maxCardsAtTurnStart = maxCardsAtTurnStart;
    }
    
    public int getMaxCardsAfterCapture() {
        return maxCardsAfterCapture;
    }
    
    public void setMaxCardsAfterCapture(int maxCardsAfterCapture) {
        this.maxCardsAfterCapture = maxCardsAfterCapture;
    }
    
    public int getOwnTerritoryCardBonus() {
        return ownTerritoryCardBonus;
    }
    
    public void setOwnTerritoryCardBonus(int ownTerritoryCardBonus) {
        this.ownTerritoryCardBonus = ownTerritoryCardBonus;
    }
    
    public boolean isOnlyOneOwnTerritoryBonus() {
        return onlyOneOwnTerritoryBonus;
    }
    
    public void setOnlyOneOwnTerritoryBonus(boolean onlyOneOwnTerritoryBonus) {
        this.onlyOneOwnTerritoryBonus = onlyOneOwnTerritoryBonus;
    }
    
    public boolean isRandomTerritoryAssignment() {
        return randomTerritoryAssignment;
    }
    
    public void setRandomTerritoryAssignment(boolean randomTerritoryAssignment) {
        this.randomTerritoryAssignment = randomTerritoryAssignment;
    }
    
    public boolean isAcknowledgeInvasionOnlyWhenNecessary() {
        return acknowledgeInvasionOnlyWhenNecessary;
    }
    
    public void setAcknowledgeInvasionOnlyWhenNecessary(boolean acknowledgeInvasionOnlyWhenNecessary) {
        this.acknowledgeInvasionOnlyWhenNecessary = acknowledgeInvasionOnlyWhenNecessary;
    }
    
    
    
    class ClassicGameAlgorithm implements GameAlgorithm {
        
        protected Board board;
        protected GameThread gameThread;
        
        
        private int bonusIndex = 0;
        private boolean cardAtEndOfTurn = false;
        
        public void startGame(Board board, GameThread gameThread) throws EndGameException {
            this.board = board;
            this.gameThread = gameThread;
            board.sendMessage(" -- Game start -- ");
            board.resetDecks();
            receiveStartingRegularUnits();
            claimTerritories();
            checkAllForElimination();
            evenDistribution(getEvenDistributionRegular());
            while (CommonBoardMethods.areMultiplePlayersAlive(board)
            && (getEndYear() < 0 || board.getYear() < getEndYear()) ) {
                board.setYear(board.getYear() + 1);
                for( Player player : board.getPlayers() ) {
                    board.setTurnMode(TurnMode.TURN_START);
                    CommonBoardEvents.checkCardsInPlay(board, gameThread);
                    if( !player.isAlive() ) {
                        continue;
                    }
                    cardAtEndOfTurn = false;
                    board.setCurrentPlayer(player);
                    board.setAttackingTerritory(null);
                    board.setDefendingTerritory(null);
                    receiveSupplies();
                    checkAllForElimination();
                    if( !player.isAlive() ) {
                        continue;
                    }
                    CommonBoardEvents.placeReinforcements(board, gameThread);
                    if( getMaxCardsAtTurnStart() < 0
                            || BonusCard.getBonusCards(player.getCards()).size()
                            > getMaxCardsAtTurnStart() ) {
                        forcePlayCard();
                    }
                    declareInvasions();
                    checkAllForElimination();
                    if( !player.isAlive() ) {
                        continue;
                    }
                    if( cardAtEndOfTurn ) {
                        drawCard();
                    }
                    declareFreeMoves();
                    checkAllForElimination();
                }
            }
            board.setTurnMode(TurnMode.GAME_OVER);
            board.sendMessage(" -- Game over --");
        }
        
        public void receiveStartingRegularUnits() throws EndGameException {
            int count = getStartingRegularUnits();
            count -= (board.getPlayers().size() - 2) * getStartingUnitsSubtrahend();
            count = Math.min(Math.max(count, getMinStartingUnits()), getStartingRegularUnits());
            CommonBoardEvents.receiveStartingRegularUnits(board, count);
        }
        
        public void claimTerritories() throws EndGameException {
            Collections.shuffle(board.getPlayers(), board.getRandom());
            board.setCurrentPlayer( board.getPlayers().get(0) );
            List<Territory> territories = board.getTerritoriesOwned(Player.NEUTRAL, TerritoryType.LAND);
            while( ! territories.isEmpty()
            && board.getCurrentPlayer().getReinforcements().getRegularUnits() > 0 ) {
                board.setTurnMode(TurnMode.CLAIM_TERRITORIES);
                
                Territory territory;
                if( isRandomTerritoryAssignment() ) {
                    territory = territories.get(board.getRandom().nextInt(territories.size()));
                } else {
                    territory = (Territory)gameThread.take();
                }
                territory.setOwner(board.getCurrentPlayer());
                territories.remove(territory);
                
                //dan added this
                board.setAttackingTerritory(territory);
                
                board.getCurrentPlayer().getReinforcements().addRegularUnits(-1);
                territory.getForce().addRegularUnits(1);
                CommonBoardEvents.checkCardsInPlay(board, gameThread);
                board.nextPlayer();
            }
            //dan added this
            board.setAttackingTerritory(null);
        }
        
        public void evenDistribution(int number) throws EndGameException {
            while(true) {
                boolean allPlaced = true;
                for( Player player : board.getPlayers() ) {
                    allPlaced &= player.getReinforcements().isEmpty();
                }
                if( allPlaced ) {
                    break;
                }
                board.setTurnMode(TurnMode.EVEN_REINFORCEMENTS);
                board.setNumberToPlace(number);
                while( !board.getCurrentPlayer().getReinforcements().isEmpty() &&
                        board.getNumberToPlace() > 0 ) {
                    ForcePlacement duple = (ForcePlacement)gameThread.take();
                    Territory territory = duple.getTerritory();
                    Force force = duple.getForce();
                    board.getCurrentPlayer().getReinforcements().subtract(force);
                    territory.getForce().add(force);
                    board.setNumberToPlace( board.getNumberToPlace() - force.getSize() );
                }
                board.nextPlayer();
            }
            
        }
        
        public void receiveSupplies() throws EndGameException {
            CommonBoardEvents.checkCardsInPlay(board, gameThread);
            Player player = board.getCurrentPlayer();
            int supply = CommonBoardMethods.getBasicSupply(board, board.getCurrentPlayer())
            + CommonBoardMethods.getContinentBonuses(board, board.getCurrentPlayer());
            player.getReinforcements().addRegularUnits(supply);
            board.sendMessage(player.getName() + " receives " + supply + " units");
        }
        
        public void drawCard() throws EndGameException {
            boolean canDraw = false;
            for( PlayableDeck deck : board.getDecks() ) {
                if( deck.canDraw(board) ) {
                    canDraw = true;
                }
            }
            if( ! canDraw ) {
                return;
            }
            
            board.setTurnMode(TurnMode.DRAW_CARD);
            Object taken = gameThread.take();
            if( taken != SpecialMove.END_MOVE ) {
                PlayableDeck deck = (PlayableDeck)taken;
                board.sendMessage(board.getCurrentPlayer().getName() + " draws a " + deck);
                board.getCurrentPlayer().getCards().add( deck.draw() );
            }
        }
        
        public void declareInvasions() throws EndGameException {
            Player current = board.getCurrentPlayer();
            board.setBeforeFirstInvasion(true);
            board.setBeforeFirstCard(true);
            int contestedTerritoriesTaken = 0;
            while(true) {
                CommonBoardEvents.checkCardsInPlay(board, gameThread);
                
                board.setCurrentPlayer(current);
                board.setTurnMode(TurnMode.DECLARE_INVASIONS);
                board.setDefendingTerritory(null);
                
                Object taken = gameThread.take();
                if( taken == SpecialMove.END_MOVE ) {
                    break;
                }
                if( taken instanceof Card ) {
                    ((Card)taken).play(board, gameThread);
                    board.setBeforeFirstCard(false);
                    finishSet();
                    continue;
                }
                
                board.setBeforeFirstInvasion(false);
                board.setAttackingTerritory(((TerritoryDuple)taken).getFirst());
                board.setDefendingTerritory(((TerritoryDuple)taken).getSecond());
                Player defender = board.getDefendingTerritory().getOwner();
                
                board.sendMessage(current.getName() + " invades "
                        + board.getDefendingTerritory().getName() + " from "
                        + board.getAttackingTerritory().getName() );
                
                boolean contested = (board.getDefendingTerritory().getForce().getMobileIndependentSize() > 0);
                
                if( isAcknowledgeInvasions()
                && board.getDefendingTerritory().getOwner() != Player.NEUTRAL ) {
                    board.setTurnMode(TurnMode.ACKNOWLEDGE_INVASION);
                    List<Player> ask = new ArrayList<Player>();
                    for(Player player : board.getPlayers()) {
                        if( (player != board.getAttackingTerritory().getOwner())
                        && player.isAlive() ) {
                            if(isAcknowledgeInvasionOnlyWhenNecessary()) {
                                for(Card card : player.getCards()) {
                                    board.setCurrentPlayer(player);
                                    if(card.canPlay(board)) {
                                        ask.add(player);
                                        break;
                                    }
                                }
                            } else if( isDefenderAcknowledgeInvasions() ) {
                                if( player == board.getDefendingTerritory().getOwner() ) {
                                    ask.add(player);
                                }
                            } else {
                                ask.add(player);
                            }
                        }
                    }
                    
                    CommonBoardEvents.getAcknowledgements(board, gameThread,
                            TurnMode.ACKNOWLEDGE_INVASION,
                            ask);
                }
                if( CommonBoardMethods.isInvasionBlockedByCard(board, board.getAttackingTerritory(), board.getDefendingTerritory())
                || board.getAttackingTerritory().getForce().getMobileIndependentSize() <= 1 ) {
                    continue;
                }
                
                board.setTurnMode(TurnMode.DECLARE_ATTACK_FORCE);
                board.setCurrentPlayer( board.getAttackingTerritory().getOwner() );
                Force attackForce = board.getPlayers().contains(board.getCurrentPlayer())
                ? (Force)gameThread.take()
                : board.getAttackingTerritory().getForce().getDefaultAttack(board.getRules());
                
                if(board.getDefendingTerritory().getForce().getMobileIndependentSize() > 0) {
                    board.setTurnMode(TurnMode.DECLARE_DEFENSE_FORCE);
                    board.setCurrentPlayer(board.getDefendingTerritory().getOwner());
                    Force defenseForce = board.getPlayers().contains(board.getCurrentPlayer())
                    ? (Force)gameThread.take()
                    : board.getDefendingTerritory().getForce().getDefaultDefense(board.getRules());
                    board.setCurrentPlayer(board.getAttackingTerritory().getOwner());
                    
                    // Here is where we roll the dice!
                    battle(attackForce, defenseForce);
                    
                    board.setCurrentPlayer(board.getAttackingTerritory().getOwner());
                }
                
                if( board.getDefendingTerritory().getForce().getMobileIndependentSize() <= 0 ) {
                    if(contested) {
                        board.sendMessage(board.getDefendingTerritory().getName()
                        + " falls");
                    }
                    
                    attackForce = attackForce.getMobileForce();
                    board.getDefendingTerritory().setOwner(board.getAttackingTerritory().getOwner());
                    board.getAttackingTerritory().getForce().subtract(attackForce);
                    board.getDefendingTerritory().getForce().add(attackForce);
                    
                    for( SpecialUnit special : new ArrayList<SpecialUnit>(board.getDefendingTerritory().getForce().getSpecialUnits()) ) {
                        if( special.getMaxOwnable() >= 0
                                && board.getUnitCount(current, special) > special.getMaxOwnable() ) {
                            board.getDefendingTerritory().getForce().getSpecialUnits().remove(special);
                        }
                    }
                    
                    if( board.getAttackingTerritory().getForce().getMobileIndependentSize() > 1 ) {
                        freeMove(board.getAttackingTerritory(), board.getDefendingTerritory());
                    }
                    
                    afterConqueringTerritory();
                    CommonBoardEvents.checkCardsInPlay(board, gameThread);
                    
                    checkForElimination(defender);
                    
                    if( contested && board.getDefendingTerritory().getType() != TerritoryType.UNDERWORLD ) {
                        ++contestedTerritoriesTaken;
                        if( contestedTerritoriesTaken == getContestedTerritoriesNeeded() ) {
                            receiveContestedTerritoryBonus();
                        }
                    }
                    board.setAttackingTerritory(board.getDefendingTerritory());
                    board.setDefendingTerritory(null);
                }
            }
            board.setBeforeFirstInvasion(false);
        }
        
        public void battle(Force attackForce, Force defenseForce) throws EndGameException {
            board.getAttackerDice().clear();
            for( DiceType dieType : getAttackDice(attackForce,
                    board.getAttackingTerritory(), board.getDefendingTerritory()) ) {
                board.getAttackerDice().add(new Die(board.getRandom(), dieType));
            }
            Collections.sort(board.getAttackerDice());
            Collections.reverse(board.getAttackerDice());
            board.getDefenderDice().clear();
            for( DiceType dieType : getDefenseDice(defenseForce,
                    board.getAttackingTerritory(), board.getDefendingTerritory()) ) {
                board.getDefenderDice().add(new Die(board.getRandom(), dieType));
            }
            Collections.sort(board.getDefenderDice());
            Collections.reverse(board.getDefenderDice());
            
            afterRoll(attackForce, defenseForce);
            
            for( int i = 0; i < Math.min(board.getAttackerDice().size(),
                    board.getDefenderDice().size()); ++i ) {
                if( board.getAttackerDice().get(i).getValue()
                > board.getDefenderDice().get(i).getValue()
                || (board.getAttackerDice().get(i).getValue()
                == board.getDefenderDice().get(i).getValue()
                && attackerWinsTies(attackForce, defenseForce))) {
                    board.getDefendingTerritory().addNumberToDestroy(1);
                } else {
                    board.getAttackingTerritory().addNumberToDestroy(1);
                }
            }
            
            board.setTurnMode(TurnMode.BATTLE_RESULTS);
            gameThread.take();
            
            beforeRemovingUnits();
            CommonBoardEvents.checkForDestroyed(board, gameThread, board.getAttackingTerritory());
            CommonBoardEvents.checkForDestroyed(board, gameThread, board.getDefendingTerritory());
        }
        
        public void beforeRemovingUnits() throws EndGameException {}
        
        // Can be used in subclasses to reroll certain dice, etc.
        public void afterRoll(Force attackForce, Force defenseForce)
        throws EndGameException {}
        
        public void afterConqueringTerritory() throws EndGameException {}
        
        public boolean attackerWinsTies(Force attackForce, Force defenseForce) {
            return false;
        }
        
        public void receiveContestedTerritoryBonus() throws EndGameException {
            cardAtEndOfTurn = true;
        }
        
        public void declareFreeMoves() throws EndGameException {
            
            board.setTurnMode(TurnMode.DECLARE_FREE_MOVES);
            CommonBoardEvents.checkCardsInPlay(board, gameThread);
            
            Player current = board.getCurrentPlayer();
            int freeMoves = getMaxFreeMovesPerTurn() + current.getFreeMoves();
            current.setFreeMoves(0);
            int i = 0;
            while(freeMoves < 0 || i < freeMoves) {
                board.setCurrentPlayer(current);
                board.setTurnMode(TurnMode.DECLARE_FREE_MOVES);
                Object taken = gameThread.take();
                if( taken == SpecialMove.END_MOVE ) {
                    break;
                } else if( taken instanceof Card ) {
                    ((Card)taken).play(board, gameThread);
                } else {
                    if(freeMove(((TerritoryDuple)taken).getFirst(), ((TerritoryDuple)taken).getSecond())) {
                        ++i;
                    }
                }
                if( freeMoves >= 0 ) {
                    // Player might have used a card that awards an additional free move
                    freeMoves += current.getFreeMoves();
                    current.setFreeMoves(0);
                }
            }
        }
        
        public boolean freeMove(Territory from, Territory to) throws EndGameException {
            board.setAttackingTerritory(from);
            board.setDefendingTerritory(to);
            
            board.setTurnMode(TurnMode.COMPLETE_FREE_MOVE);
            Force force = (Force)gameThread.take();
            if( force.isEmpty() ) {
                return false;
            }
            from.getForce().subtract(force);
            to.getForce().add(force);
            board.sendMessage(board.getCurrentPlayer().getName() + " fortifies "
                    + to.getName() + " from " + from.getName());
            return true;
        }
        
        public void checkForElimination(Player player) throws EndGameException {
            if( ! player.isAlive() ) {
                return;
            }
            List<Territory> territories = board.getTerritoriesOwned(player);
            for( Territory territory : new ArrayList<Territory>(territories) ) {
                if( EnumSet.of(TerritoryType.HEAVEN, TerritoryType.UNDERWORLD).contains(territory.getType()) ) {
                    territories.remove(territory);
                }
            }
            if( territories.isEmpty()
            && board.getPlayers().contains(player) ) {
                board.sendMessage("All units under the command of "
                        + player.getName() + " have surrendered" );
            /* "Bonus" will be -2 for second place, -3 for third, etc.,
             * so point system will rank players by reverse order of
             * elimination. */
                player.setBonusPoints( - board.getLivingPlayers().size() );
                player.setAlive(false);
                player.setEnergy(0);
                player.setReinforcements(new ForceVacuum());  // Goodbye units
                player.setFreeMoves(0);
                for( Card card : player.getCards() ) {
                    surrenderCard(card);
                }
                player.getCards().clear();
                afterCardSurrender();
                // Remove cards that target this player
                for( Card card : board.getCardsInPlay() ) {
                    if( card.getPlayer() == player ) {
                        if( card.getDeck() != null ) {
                            card.getDeck().discard(card);
                        }
                    }
                }
                for( Territory territory : board.getTerritoriesOwned(player, TerritoryType.UNDERWORLD) ) {
                    territory.getForce().setRegularUnits(0);
                    territory.setOwner(Player.NEUTRAL);
                    for( SpecialUnit special : new ArrayList<SpecialUnit>(territory.getForce().getSpecialUnits()) ) {
                        if( ! special.isNeutral() ) {
                            territory.getForce().getSpecialUnits().remove(special);
                        }
                    }
                }
            }
        }
        
        public void surrenderCard(Card card) {
            if( board.getCurrentPlayer() == null ) {
                card.getDeck().discard(card);
            } else {
                board.getCurrentPlayer().getCards().add(card);
            }
        }
        
        public void afterCardSurrender() throws EndGameException {
            while( BonusCard.getBonusCards(board.getCurrentPlayer().getCards()).size()
            > getMaxCardsAfterCapture() ) {
                forcePlayCard();
                finishSet();
            }
        }
        
        public void checkAllForElimination() throws EndGameException {
            for( Player player : board.getPlayers() ) {
                checkForElimination(player);
            }
        }
        
        public void finishSet() throws EndGameException {
            checkForSet();
            while( isPlayingSet() ) {
                forcePlayCard();
                checkForSet();
            }
        }
        
        public boolean isPlayingSet() {
            return ! BonusCard.getBonusCards(board.getCardsInPlay()).isEmpty();
        }
        
        public void checkForSet() throws EndGameException {
            List<BonusCard> cardSet = BonusCard.getBonusCards(board.getCardsInPlay());
            if( cardSet.size() == BonusCard.SET_SIZE ) {
                receiveBonus(cardSet);
                for( Card card : cardSet ) {
                    board.getCardsInPlay().remove(card);
                    card.getDeck().discard(card);
                }
            }
        }
        
        public void receiveBonus(List<BonusCard> cardSet) throws EndGameException {
            for( BonusCard bonusCard : cardSet ) {
                if( bonusCard.getTerritory() != null
                        && bonusCard.getTerritory().getOwner()
                        == board.getCurrentPlayer() ) {
                    bonusCard.getTerritory().getForce().addRegularUnits(getOwnTerritoryCardBonus());
                    if( isOnlyOneOwnTerritoryBonus() ) {
                        break;
                    }
                }
            }
            int bonus = getCardBonus();
            board.sendMessage(board.getCurrentPlayer().getName() + " receives "
                    + bonus + " units");
            board.getCurrentPlayer().getReinforcements().addRegularUnits(bonus);
            CommonBoardEvents.placeReinforcements(board, gameThread);
        }
        
        public int getCardBonus() {
            if( getBonusValues().length == 0 ) {
                if( getBonusAddend() <= 0 ) {
                    return 0;
                }
                bonusIndex++;
                return bonusIndex * getBonusAddend();
            }
            int result = 0;
            if( bonusIndex < getBonusValues().length ) {
                result = getBonusValues()[bonusIndex];
            } else if(bonusAddend < 0) {
                bonusIndex = 0;
                result = getBonusValues()[0];
            } else {
                result = getBonusValues()[getBonusValues().length - 1]
                        + (bonusIndex - (getBonusValues().length - 1)) * bonusAddend;
            }
            bonusIndex++;
            return result;
        }
        
        public void forcePlayCard() throws EndGameException {
            board.setTurnMode(TurnMode.FORCE_PLAY_CARD);
            ((Card)gameThread.take()).play(board, gameThread);
            finishSet();
        }
        
    }
    
    
    public List<DiceType> getAttackDice(Force force, Territory from, Territory to) {
        List<DiceType> result = new ArrayList<DiceType>();
        for( SpecialUnit special : force.getSpecialUnits() ) {
            if( special.isMobile() && special.isIndependentUnit() ) {
                if( hasAttackBonus(special, from.getType()) ||
                        hasAttackBonus(special, to.getType()) ) {
                    result.add(DiceType.EIGHT_SIDED);
                } else {
                    result.add(DiceType.SIX_SIDED);
                }
            }
        }
        if( force.getRegularUnits() > 0 ) {
            for( int i = 0; i < force.getRegularUnits() && result.size() < getMaxAttackDice(); ++i ) {
                result.add(DiceType.SIX_SIDED);
            }
        }
        Collections.sort(result);
        while( result.size() > getMaxAttackDice() ) {
            result.remove(0);
        }
        return result;
    }
    
    public List<DiceType> getDefenseDice(Force force, Territory from, Territory to) {
        List<DiceType> result = new ArrayList<DiceType>();
        boolean maxPower = false;
        for( SpecialUnit special : force.getSpecialUnits() ) {
            if( special.isMobile() && special.isIndependentUnit() ) {
                result.add(DiceType.EIGHT_SIDED);
            }
            if( special == DefaultRules.SPACE_STATION
                    || special == AmoebaRules.ALIEN_LEADER ) {
                maxPower = true;
            }
        }
        if( force.getRegularUnits() > 0 ) {
            for( int i = 0; i < force.getRegularUnits() && result.size() < getMaxDefenseDice(); ++i ) {
                result.add(DiceType.SIX_SIDED);
            }
        }
        if( maxPower ) {
            for( int i = 0; i < result.size(); ++i ) {
                result.set(i, DiceType.EIGHT_SIDED);
            }
        }
        Collections.sort(result);
        while( result.size() > getMaxDefenseDice() ) {
            result.remove(0);
        }
        return result;
    }
    
    public boolean hasAttackBonus(SpecialUnit specialUnit,
            TerritoryType territoryType) {
        return false;
    }
    
}
