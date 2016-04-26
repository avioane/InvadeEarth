/*
 * PlayableDeck.java
 *
 * Created on July 10, 2005, 6:15 PM
 *
 */

package org.invade;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import org.invade.rules.DefaultRules;

/**
 *
 * @author Jonathan Crosmer
 */
public class PlayableDeck extends Deck<Card> {
    
    private boolean deckDisabled = false;
    private int price = 1;
    private SpecialUnit requiredUnit = null;
    private DefaultListCellRenderer listCellRendererComponent;
    
    public static final javax.swing.ListCellRenderer RENDERER = new ListCellRenderer();
    
    public PlayableDeck() {
        setListCellRendererComponent(new DefaultListCellRenderer());
        getListCellRendererComponent().setOpaque(true);
    }
    
    public int getPrice(Board board) {
        int extra = 0;
        for(Card card : board.getCardsInPlay()) {
            if(card instanceof EconomicCard) {
                extra += ((EconomicCard)card).getCardPriceChange(board);
            }
        }
        return price + extra;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public SpecialUnit getRequiredUnit() {
        return requiredUnit;
    }
    
    public void setRequiredUnit(SpecialUnit requiredUnit) {
        this.requiredUnit = requiredUnit;
        getListCellRendererComponent().setIcon(requiredUnit.getIcon());
    }
    
    public void setName(String name) {
        super.setName(name);
    }
    
    public void add(Card ... moreCards) {
        for( Card card : moreCards ) {
            getCards().add(card);
            card.setDeck(this);
        }
    }
    
    /* Returns true if the current player has the required commander (if any)
     * for this deck and the deck is not empty. */
    public boolean canDraw(Board board) {
        return (requiredUnit == null ||
                board.getUnitCount(board.getCurrentPlayer(), requiredUnit) > 0)
                && (!getCards().isEmpty());
    }
    
    /* Returns true if the current player "canDraw" a card and has enough
     * energy to pay for it. */
    public boolean canBuy(Board board) {
        return canDraw(board) && (board.getCurrentPlayer().getEnergy() >= getPrice(board));
    }
    
    public Icon getIcon() {
        if( getRequiredUnit() != null ) {
            return getRequiredUnit().getIcon();
        }
        return DefaultRules.SPACE_STATION.getIcon();
    }
    
    public Color getColor() {
        if( getRequiredUnit() != null ) {
            return getRequiredUnit().getColor();
        }
        return Color.BLACK;
    }
    
    public void setDisabled(boolean disabled) {
        setDeckDisabled(disabled);
    }
    
    private static class ListCellRenderer implements javax.swing.ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value == null) {
                return new DefaultListCellRenderer();
            }
            Component component = ((PlayableDeck)value).getListCellRendererComponent();            
            ((DefaultListCellRenderer)component).setText(((PlayableDeck)value).getName()+":  " + ((PlayableDeck)value).getCards().size());
            if(((PlayableDeck)value).isDeckDisabled()) {
                component.setBackground(Color.LIGHT_GRAY);
                component.setForeground(Color.GRAY);
            }else {
                if (isSelected) {
                    component.setBackground(list.getSelectionBackground());
                    component.setForeground(list.getSelectionForeground());
                } else {
                    component.setBackground(list.getBackground());
                    component.setForeground(list.getForeground());
                }
            }
            return component;
        }
    }
    
    public DefaultListCellRenderer getListCellRendererComponent() {
        return listCellRendererComponent;
    }
    
    public void setListCellRendererComponent(DefaultListCellRenderer listCellRendererComponent) {
        this.listCellRendererComponent = listCellRendererComponent;
    }

    public boolean isDeckDisabled() {
        return deckDisabled;
    }

    public void setDeckDisabled(boolean deckDisabled) {
        this.deckDisabled = deckDisabled;
    }
    
}
