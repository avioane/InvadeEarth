/*
 * GodstormRules.java
 *
 * Created on August 26, 2005, 11:03 AM
 *
 */

package org.invade.rules;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import org.invade.Board;
import org.invade.Card;
import org.invade.CommonBoardEvents;
import org.invade.DiceType;
import org.invade.Die;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameAlgorithm;
import org.invade.GameThread;
import org.invade.MapIcon;
import org.invade.MoveVerifier;
import org.invade.PlayableDeck;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.SuppressedProperty;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.miraclecards.*;


public class GodstormRules extends DefaultRules {
    
    public String toString() {
        return "Pantheon (Unfinished)";
    }
    
    public static final SpecialUnit TEMPLE
            = new SpecialUnit("Temple", 5, Color.CYAN, false, true, true, -1, 1, 12, "temple.png");
    
    public static final SpecialUnit WAR
            = new SpecialUnit("God of War", 3, Color.RED, true, false, false, 1, -1, -1, "war.png");
    
    public static final SpecialUnit MAGIC
            = new SpecialUnit("Goddess of Magic", 3, new Color(250, 230, 0),
            true, false, false, 1, -1, -1, "magic.png");
    
    public static final SpecialUnit SKY
            = new SpecialUnit("God of the Sky", 3, new Color(200, 112, 255),
            true, false, false, 1, -1, -1, "sky.png");
    
    public static final SpecialUnit DEATH
            = new SpecialUnit("God of Death", 3, new Color(50, 255, 120),
            true, false, false, 1, -1, -1, "death.png");
    
    /* These cannot be purchased; they exist only on the map. */
    public static final SpecialUnit CRYPT
            = new SpecialUnit("Crypt", 5, Color.GRAY, false, true, true, -1, -1, -1, "crypt.png");
    
    public static final SpecialUnit ALTAR
            = new SpecialUnit("Altar", 5, new Color(255, 128, 0), false, true, true, -1, -1, -1, "altar.png");
    
    public static final SpecialUnit GATE
            = new SpecialUnit("Gate", 5, Color.BLACK, false, true, true, -1, -1, -1, "gate.png");
    
    public GodstormRules() {
        setNumberDevastated(0);
        setRandomTerritoryAssignment(true);
        setDefenderAcknowledgeInvasions(true);
        setEvenDistributionSpecial(2);
    }
    
    private int numberPlagued = 4;
    private int baseGodswarDice = 3;
    
    public GameAlgorithm createGameAlgorithm() {
        return new GodstormGameAlgorithm();
    }
    
    public List<PlayableDeck> getStartingDecks(Board board) {
        List<PlayableDeck> result = new ArrayList<PlayableDeck>();
        
        PlayableDeck warDeck = new PlayableDeck();
        warDeck.setName("War God Miracle");
        warDeck.setPrice(2);
        warDeck.setRequiredUnit(WAR);
        warDeck.add(
                new VisionsBecomeReal(0),
                new EaglesTakeWing(1),
                new FaithShallDeliver(1, 2),
                new TheSirenCalls(3),
                new PrayersAreAnswered(0, 6),
                new TheFaithfulGather(0, 2),
                new AnEmpireIsBorn(0),
                new HeroesAreBorn(0, 3),
                new OurFaithIsOurStrength(0, 2),
                new TheSunGrowsCold(0),
                new StormsRage(0, 6),
                new ChampionsArise(0),
                new TimeStandsStill(4, 1),
                new TheLandIsPurged(2),
                new TheTempleFalls(0),
                new AShrineIsPlundered(1),
                new OnlyAshRemains(0)
                );
        result.add(warDeck);
        
        PlayableDeck deathDeck = new PlayableDeck();
        deathDeck.setName("Death God Miracle");
        deathDeck.setPrice(2);
        deathDeck.setRequiredUnit(DEATH);
        deathDeck.add(
                new CropsWither(2),
                new CropsWither(2),
                new TheDeadWalk(2, 6),
                new TheDeadWalk(2, 6),
                new RagnarokCometh(3),
                new TheYoungGrowOld(2),
                new DeathsDoorCloses(2),
                new BloodCoatsTheLand(1),
                new TheGroundShakes(2),
                new TheSeaIsYourTomb(3, "Atlantis"),
                new TheMightyHaveFallen(4, 3),
                new DiamondsTurnToRust(3),
                new FireRainsDown(4),
                new TheGodsForsakeUs(3),
                new ParadiseIsLost(2),
                new TheTrojanHorseIsRevealed(2, 6),
                new TheWatersBoil(4)
                );
        result.add(deathDeck);
        
        PlayableDeck skyDeck = new PlayableDeck();
        skyDeck.setName("Sky God Miracle");
        skyDeck.setPrice(2);
        skyDeck.setRequiredUnit(SKY);
        skyDeck.add(
                new EnemiesConvert(2, 2),
                new YourIdolIsFalse(1),
                new TheFogLifts(0, 4),
                new TheFogLifts(0, 4),
                new TheMightyTurnMeek(0),
                new TheMightyTurnMeek(0),
                new FireSearsTheSky(0, 4),
                new FireSearsTheSky(0, 4),
                new SwordsBecomePlowshares(1, 6),
                new SwordsBecomePlowshares(1, 6),
                new ThousandWorkersAmass(0),
                new ThousandWorkersAmass(0),
                new MenBecomeWarriors(1, 3),
                new MenBecomeWarriors(1, 3),
                new LavaErupts(2, 6),
                new ThisGroundIsSacred(3)
                );
        result.add(skyDeck);
        
        PlayableDeck magicDeck = new PlayableDeck();
        magicDeck.setName("Magic Goddess Miracle");
        magicDeck.setPrice(2);
        magicDeck.setRequiredUnit(MAGIC);
        magicDeck.add(
                new DrawEachTurn("The Golden Fleece", warDeck),
                new DrawEachTurn("Arawn's Cauldron of Life", deathDeck),
                new DrawEachTurn("The Great Pyramids", skyDeck),
                new Stonehenge(),
                new TheWineOfEternalLife(1),
                new FreyasTearsOfGold(1),
                new PandorasBox(deathDeck),
                new TheTabletsOfDestiny(),
                new GungnirSpearOfOdin(),
                new Excalibur(2),
                new MjolnirHammerOfThor(3),
                new TheBookOfTheDead(),
                new TheTreeOfLife(),
                new AegisShieldOfAthena()
                );
        result.add(magicDeck);
        
        return result;
    }
    
    
    // Used by miracles to check whether the effects should be blocked
    // (e.g. by Aegis, Shield of Athena)
    public static boolean checkForBlock(Board board, GameThread gameThread)
    throws EndGameException {
        /* Make copy so we do not get concurrent modification exceptions
         * when cards are "used up" */
        List<Card> cards = new ArrayList<Card>(board.getCardsInPlay());
        for( Card card : cards ) {
            if( card instanceof BlockMiracle ) {
                if( ((BlockMiracle)card).checkForBlock(board, gameThread) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    class GodstormGameAlgorithm extends DefaultGameAlgorithm {
        
        // So the sky labor can only be completed once per turn
        protected boolean skyLaborComplete = false;
        
        // This stuff happens at the beginning of the game
        public void devastateTerritories() throws EndGameException {
            
            board.sendMessage("BETA WARNING:  This rule set is not fully tested.");
            board.sendMessage("Please report any bugs you find.");
            
            super.devastateTerritories();
            afflictTerritoriesWithPlague();
            
            // Create the heavens
            for( Player player : board.getPlayers() ) {
                Heaven heaven = new Heaven();
                board.setCurrentPlayer(player);
                heaven.play(board, gameThread);
            }
            
            // Add rules that are implemented by cards
            new ForcePlayMagicCards().play(board, gameThread);
            RelicTerritoryBonus relicTerritoryBonus = new RelicTerritoryBonus();
            relicTerritoryBonus.addBonus("Mjolnir, Hammer of Thor", "Thule", 1);
            relicTerritoryBonus.addBonus("The Tree of Life", "Babylon", 1);
            relicTerritoryBonus.addBonus("Pandora's Box", "Graecia", 1);
            relicTerritoryBonus.addBonus("Stonehenge", "Anglia", 1);
            relicTerritoryBonus.addBonus("The Great Pyramids", "Egypt", 1);
            relicTerritoryBonus.play(board, gameThread);
        }
        
        public void afflictTerritoriesWithPlague() throws EndGameException {
            for( int i = 0; i < getNumberPlagued(); ++i ) {
                Territory territory = board.getTerritoryDeck(TerritoryType.LAND).draw();
                territory.setPlague(true);
                board.getTerritoryDeck(TerritoryType.LAND).discard(territory);
            }
            Collections.shuffle(board.getTerritoryDeck(TerritoryType.LAND).getCards(),
                    board.getRandom());
        }
        
        public void receiveStartingSpecialUnits() throws EndGameException {
            Force starting = new Force();
            starting.getSpecialUnits().add(TEMPLE);
            starting.getSpecialUnits().add(WAR);
            for( Player player : board.getPlayers() ) {
                player.getReinforcements().add(starting);
            }
        }
        
        public void receiveSupplies() throws EndGameException {
            Player player = board.getCurrentPlayer();
            
            CommonBoardEvents.checkCardsInPlay(board, gameThread);
            CommonBoardEvents.placeReinforcements(board, gameThread);
            
            // Temples generate one unit and one faith (energy) per turn.
            // Also, players resurrect one unit from each crypt
            // to any temple each turn.
            int resurrection = 0;
            for( Territory territory : board.getTerritories() ) {
                if( territory.getOwner() == player ) {
                    for( SpecialUnit unit : territory.getForce().getSpecialUnits() ) {
                        if( unit == TEMPLE ) {
                            territory.getForce().addRegularUnits(1);
                            player.addEnergy(1);
                        }
                        if( unit == CRYPT ) {
                            int maxPerCrypt = 1;
                            if( hasCardInPlay(board.getCurrentPlayer(), TheScalesOfOsiris.class) ) {
                                maxPerCrypt *= 2;
                            }
                            int amount = Math.min(maxPerCrypt, territory.getForce().getRegularUnits() - 1);
                            if( amount > 0 ) {
                                resurrection += amount;
                                territory.getForce().setRegularUnits(
                                        territory.getForce().getRegularUnits() - amount);
                            }
                        }
                    }
                }
            }
            
            // Force placement of resurrected units in temples
            if( resurrection > 0
                    && board.getUnitCount(board.getCurrentPlayer(), TEMPLE) > 0 ) {
                board.sendMessage("The priests of " + player + " celebrate a resurrection (" + resurrection + ")");
                MoveVerifier old = board.getMoveVerifier();
                board.setMoveVerifier(new ResurrectionMoveVerifier());
                player.getReinforcements().addRegularUnits(resurrection);
                CommonBoardEvents.placeReinforcements(board, gameThread);
                board.setMoveVerifier(old);
            }
            
            super.receiveSupplies();
        }
        
        public void declareInvasions() throws EndGameException {
            skyLaborComplete = false;
            board.sendMessage("Play miracle cards");
            CommonBoardEvents.getAcknowledgement(board, gameThread, TurnMode.ACKNOWLEDGE_EACH_PLAYER);
            sufferPlague();
            super.declareInvasions();
        }
        
        public void sufferPlague() throws EndGameException {
            for(Territory territory : board.getTerritoriesOwned(board.getCurrentPlayer())) {
                CommonBoardEvents.sufferPlague(board, territory);
            }
        }
        
        public void battle(Force attackForce, Force defenseForce) throws EndGameException {
            if( hasDeity(attackForce) && hasDeity(defenseForce) ) {
                wageGodswar(attackForce, defenseForce);
                return;
            }
            super.battle(attackForce, defenseForce);
        }
        
        public void beforeRemovingUnits() throws EndGameException {
            if(board.getMoveVerifier() instanceof InvadeTheUnderworldMoveVerifier) {
                return;
            }
            CommonBoardEvents.checkCardsInPlay(board, gameThread);
            Player attacker = board.getAttackingTerritory().getOwner();
            Player defender = board.getDefendingTerritory().getOwner();
            if( !board.getAttackingTerritory().getForce().getSpecialUnits().contains(DEATH) ) {
                Heaven heaven = Heaven.getHeaven(board, defender);
                heaven.addUnits(
                        board.getDefendingTerritory().getNumberToDestroy());
            }
            Heaven heaven = Heaven.getHeaven(board, attacker);
            heaven.addUnits(board.getAttackingTerritory().getNumberToDestroy());
        }
        
        public void afterConqueringTerritory() throws EndGameException {
            CommonBoardEvents.sufferPlague(board, board.getDefendingTerritory());
        }
        
        public boolean attackerWinsTies(Force attackForce, Force defenseForce) {
            return attackForce.getSpecialUnits().contains(WAR);
        }
        
        public void afterRoll(Force attackForce, Force defenseForce)
        throws EndGameException {
            if( hasTriple(board.getAttackerDice()) ) {
                laborCompleted(MAGIC, board.getAttackingTerritory().getOwner(),
                        "roll at least 3 of the same number");
            }
            if( hasTriple(board.getDefenderDice()) ) {
                laborCompleted(MAGIC, board.getDefendingTerritory().getOwner(),
                        "roll at least 3 of the same number");
            }
            boolean rerolled = false;
            if( board.getAttackingTerritory().getForce().getSpecialUnits().contains(MAGIC)
            || hasCardInPlay(board.getAttackingTerritory().getOwner(), GungnirSpearOfOdin.class) ) {
                if( hasOne(board.getAttackerDice()) ) {
                    rerolled = true;
                    rerollOnes(board.getAttackerDice());
                }
            }
            if( board.getDefendingTerritory().getForce().getSpecialUnits().contains(MAGIC)
            || board.getDefendingTerritory().getForce().getSpecialUnits().contains(TEMPLE)
            || hasCardInPlay(board.getDefendingTerritory().getOwner(), GungnirSpearOfOdin.class) ) {
                if( hasOne(board.getDefenderDice()) ) {
                    rerolled = true;
                    rerollOnes(board.getDefenderDice());
                }
            }
            if(rerolled) {
                afterRoll(attackForce, defenseForce);
            }
        }
        
        public boolean hasTriple(List<Die> dice) {
            List<Die> diceCopy = new ArrayList<Die>(dice);
            Collections.sort(diceCopy);
            int value = 0;
            int copies = 0;
            for( Die die : diceCopy ) {
                if( die.getValue() == value ) {
                    copies++;
                } else {
                    copies = 1;
                    value = die.getValue();
                }
                if( copies >= 3 ) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasOne(List<Die> dice) throws EndGameException {
            for(Die die : dice) {
                if(die.getValue() == 1) {
                    return true;
                }
            }
            return false;
        }
        
        public void rerollOnes(List<Die> dice) throws EndGameException {
            for( Die die : dice ) {
                if( die.getValue() == 1 ) {
                    die.roll(board.getRandom());
                }
            }
            Collections.sort(dice);
            Collections.reverse(dice);
        }
        
        public void wageGodswar(Force attackForce, Force defenseForce) throws EndGameException {
            
            board.sendMessage("Godswar!");
            
            // Choose a god to send in
            // Temporarily set the "defending territory" to the attacking one
            // because other code assumes that CHOOSE_COMMANDER means choose
            // one from the defending territory
            Territory defendingTerritory = board.getDefendingTerritory();
            board.setTurnMode(TurnMode.CHOOSE_COMMANDER);
            board.setDefendingTerritory(board.getAttackingTerritory());
            board.setCurrentPlayer(board.getDefendingTerritory().getOwner());
            Force force = (Force)gameThread.take();
            SpecialUnit attackingGod = force.getSpecialUnits().get(0);
            board.setDefendingTerritory(defendingTerritory);
            board.setCurrentPlayer(board.getDefendingTerritory().getOwner());
            force = (Force)gameThread.take();
            SpecialUnit defendingGod = force.getSpecialUnits().get(0);
            
            int attackDice = getBaseGodswarDice();
            int defenseDice = getBaseGodswarDice();
            
            attackDice += Collections.frequency(attackForce.getSpecialUnits(), SKY)
            + board.getUnitCount(board.getAttackingTerritory().getOwner(), ALTAR);
            
            defenseDice += Collections.frequency(defenseForce.getSpecialUnits(), SKY)
            + board.getUnitCount(board.getDefendingTerritory().getOwner(), ALTAR);
            
            board.getAttackerDice().clear();
            board.getDefenderDice().clear();
            for( int i = 0; i < attackDice; ++i ) {
                board.getAttackerDice().add(new Die(board.getRandom(), DiceType.SIX_SIDED));
            }
            for( int i = 0; i < defenseDice; ++i ) {
                board.getDefenderDice().add(new Die(board.getRandom(), DiceType.SIX_SIDED));
            }
            afterRoll(attackForce, defenseForce);
            
            int attackValue = 0;
            int defenseValue = 0;
            for( Die die : board.getAttackerDice() ) {
                attackValue += die.getValue();
            }
            for( Die die : board.getDefenderDice() ) {
                defenseValue += die.getValue();
            }
            attackValue += board.getAttackingTerritory().getForce().getMobileIndependentSize();
            defenseValue += board.getDefendingTerritory().getForce().getMobileIndependentSize();
            for( Card card : board.getCardsInPlay() ) {
                if( card instanceof GodswarBonus ) {
                    attackValue += ((GodswarBonus)card).getGodswarBonusValue(board.getAttackingTerritory().getOwner());
                    defenseValue += ((GodswarBonus)card).getGodswarBonusValue(board.getDefendingTerritory().getOwner());
                }
            }
            board.sendMessage("Result:  " + attackValue + " vs. " + defenseValue);
            
            if( attackValue > defenseValue || (attackValue == defenseValue
                    && attackerWinsTies(attackForce, defenseForce)) ) {
                defenseForce.getSpecialUnits().remove(defendingGod);
                board.getDefendingTerritory().getForce().getSpecialUnits().remove(defendingGod);
                board.sendMessage(defendingGod.toString() + " is defeated");
                if( ! skyLaborComplete ) {
                    skyLaborComplete = true;
                    laborCompleted(SKY, board.getAttackingTerritory().getOwner(), "defeat an enemy god on your turn");
                }
            } else {
                attackForce.getSpecialUnits().remove(attackingGod);
                board.getAttackingTerritory().getForce().getSpecialUnits().remove(attackingGod);
                board.sendMessage(attackingGod.toString() + " is defeated");
            }
            
            board.setCurrentPlayer(board.getAttackingTerritory().getOwner());
            board.setTurnMode(TurnMode.BATTLE_RESULTS);
            gameThread.take();
            
            // Force battle round if one side has lost all gods
            if( ! (hasDeity(board.getDefendingTerritory().getForce())
            && hasDeity(board.getAttackingTerritory().getForce())) ) {
                battle(attackForce, defenseForce);
            }
            
        }
        
        public void declareFreeMoves() throws EndGameException {
            super.declareFreeMoves();
            board.setAttackingTerritory(null);
            board.setDefendingTerritory(null);
            checkAllForElimination();
            if( board.getCurrentPlayer().isAlive() ) {
                embarkFromHeaven();
                invadeTheUnderworld();
            }
        }
        
        public void embarkFromHeaven() throws EndGameException {
            Player player = board.getCurrentPlayer();
            Heaven heavenCard = Heaven.getHeaven(board, player);
            if( heavenCard.getUnits() > 0 ) {
                player.getReinforcements().addRegularUnits(heavenCard.getUnits());
                heavenCard.setUnits(0);
                for(Territory territory : board.getTerritories()) {
                    if(territory.getType() == TerritoryType.HEAVEN) {
                        territory.setOwner(player);
                        territory.getForce().clear();
                        territory.getForce().addRegularUnits(1);
                    }
                }
                board.sendMessage("Embark from Heaven");
                board.setMoveVerifier(new EmbarkFromHeavenMoveVerifier());
                CommonBoardEvents.placeReinforcements(board, gameThread);
                board.setMoveVerifier(getMoveVerifier());
            }
        }
        
        public void invadeTheUnderworld() throws EndGameException {
            board.sendMessage("Invade the Underworld");
            board.setMoveVerifier(new InvadeTheUnderworldMoveVerifier());
            super.declareInvasions();
            board.setMoveVerifier(getMoveVerifier());
            
            // Return armies (if any) to heaven card
            Player player = board.getCurrentPlayer();
            Heaven heavenCard = Heaven.getHeaven(board, player);
            for(Territory territory : board.getTerritoriesOwned(
                    player, TerritoryType.HEAVEN)) {
                heavenCard.addUnits(territory.getForce().getRegularUnits() - 1);
                territory.getForce().clear();
                territory.update();
            }
            
            for(Territory territory : board.getTerritoriesOwned(player)) {
                if(territory.getForce().getSpecialUnits().contains(CRYPT)) {
                    laborCompleted(DEATH, player, "control a crypt at the end of your turn");
                    break;
                }
            }
        }
        
        public void receiveContestedTerritoryBonus() throws EndGameException {
            laborCompleted(WAR, board.getCurrentPlayer(), "conquer 3 or more territories on your turn");
        }
        
        public void laborCompleted(SpecialUnit god, Player player, String laborDescription) throws EndGameException {
            if( hasAnywhere(player, god) ) {
                for( PlayableDeck deck : board.getDecks() ) {
                    if( deck.getRequiredUnit() == god ) {
                        Card card = deck.draw();
                        if( card != null ) {
                            board.sendMessage(player.getName()
                            + " completes a Labor for the " + god + " ("
                                    + laborDescription + ")");
                            player.getCards().add(card);
                        }
                    }
                }
                CommonBoardEvents.checkCardsInPlay(board, gameThread);
            }
        }
        
        public boolean hasAnywhere(Player player, SpecialUnit specialUnit) {
            for( Territory territory : board.getTerritoriesOwned(player) ) {
                if( territory.getForce().getSpecialUnits().contains(specialUnit) ) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasCardInPlay(Player player, Class<? extends Card> clazz) {
            for( Card card : board.getCardsInPlay() ) {
                if( clazz.isInstance(card) && card.getPlayer() == player ) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    
    
    public static boolean hasDeity(Force force) {
        for( SpecialUnit unit : force.getSpecialUnits() ) {
            if( isDeity(unit) ) {
                return true;
            }
        }
        return false;
    }
    
    // This is a proper method, not a property
    @SuppressedProperty public static boolean isDeity(SpecialUnit unit) {
        return unit == WAR || unit == MAGIC || unit == SKY || unit == DEATH;
    }
    
    
    public MoveVerifier getMoveVerifier() {
        return new GodstormMoveVerifier();
    }
    
    
    
    public List<SpecialUnit> getUnitsForPurchase() {
        return Arrays.asList(new SpecialUnit[]{
            TEMPLE, WAR, MAGIC, SKY, DEATH });
    }
    
    public List<SpecialUnit> getUnitsUsed() {
        return Arrays.asList(new SpecialUnit[]{
            TEMPLE, WAR, MAGIC, SKY, DEATH, ALTAR, CRYPT, GATE });
    }
    
    @SuppressedProperty public String getCurrencyName() {
        return "Faith";
    }
    
    @SuppressedProperty public String getTurnName() {
        return "Epoch";
    }
    
    
    
    
    public int getNumberPlagued() {
        return numberPlagued;
    }
    
    public void setNumberPlagued(int numberPlagued) {
        this.numberPlagued = numberPlagued;
    }
    
    public int getBaseGodswarDice() {
        return baseGodswarDice;
    }
    
    public void setBaseGodswarDice(int baseGodswarDice) {
        this.baseGodswarDice = baseGodswarDice;
    }
    
    public Icon getDevastatedIcon() {
        return MapIcon.SUNK_MARKER;
    }
    
}
