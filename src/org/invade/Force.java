/*
 * Force.java
 *
 * Created on June 24, 2005, 10:32 AM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Force {
    private int regularUnits = 0;
    private List<SpecialUnit> specialUnits = new ArrayList<SpecialUnit>();
    
    public void clear() {
        regularUnits = 0;
        specialUnits.clear();
    }
    
    public int getRegularUnits() {
        return regularUnits;
    }
    
    public void setRegularUnits(int regularUnits) {
        this.regularUnits = regularUnits;
    }
    
    public void addRegularUnits(int regularUnits) {
        this.regularUnits += regularUnits;
    }
    
    public List<SpecialUnit> getSpecialUnits() {
        return specialUnits;
    }
    
    public boolean isEmpty() {
        return (regularUnits <= 0) && specialUnits.isEmpty();
    }
    
    public void add(Force addend) {
        regularUnits += addend.regularUnits;
        specialUnits.addAll(addend.specialUnits);
    }
    
    public void subtract(Force subtrahend) {
        regularUnits -= subtrahend.regularUnits;
        for( SpecialUnit special : subtrahend.specialUnits ) {
            specialUnits.remove(special);
        }
    }
    
    public Force getMobileForce() {
        Force result = new Force();
        result.regularUnits = regularUnits;
        for( SpecialUnit special : specialUnits ) {
            if( special.isMobile() && ! special.isNeutral() ) {
                result.specialUnits.add(special);
            }
        }
        return result;
    }
    
    public Force getMobileIndependentForce() {
        Force result = new Force();
        result.regularUnits = regularUnits;
        for( SpecialUnit special : specialUnits ) {
            if( special.isMobile() && ! special.isNeutral() && special.isIndependentUnit() ) {
                result.specialUnits.add(special);
            }
        }
        return result;
    }
    
    public int getMobileIndependentSize() {
        int result = regularUnits;
        for( SpecialUnit special : specialUnits ) {
            if( special.isMobile() && special.isIndependentUnit() && ! special.isNeutral() ) {
                result++;
            }
        }
        return result;
    }
    
    public int getSize() {
        return regularUnits + specialUnits.size();
    }
    
    public Force getDefaultAttack(Rules rules) {
        Force result = new Force();
        for( SpecialUnit special : specialUnits ) {
            if( special.isMobile() ) {
                result.specialUnits.add(special);
            }
        }
        if( regularUnits < 1 && result.specialUnits.size() > 0 ) {
            result.specialUnits.remove(0);
            return result;
        }
        result.regularUnits += Math.min(Math.max(0, rules.getMaxAttackDice() - result.getMobileIndependentSize()),
                Math.max(0, regularUnits - 1));
        return result;
    }
    
    public Force getDefaultDefense(Rules rules) {
        Force result = new Force();
        result.specialUnits.addAll(specialUnits);
        result.regularUnits += Math.min(Math.max(0, rules.getMaxDefenseDice() - result.getMobileIndependentSize()), regularUnits);
        return result;
    }
    
    public Force getDefaultDestroyed(int number) {
        Force result = new Force();
        result.regularUnits += Math.min(number, regularUnits);
        for( SpecialUnit special : specialUnits ) {
            if( result.getMobileIndependentSize() >= number ) {
                break;
            }
            if( special.isMobile() && special.isIndependentUnit() ) {
                result.specialUnits.add(special);
            }
        }
        return result;
    }
    
    public Force getDefaultFreeMove() {
        int maxUnits = getMobileIndependentSize() - 1;
        Force result = new Force();
        for( SpecialUnit special : specialUnits ) {
            if( special.isMobile() ) {
                result.specialUnits.add(special);
                if( result.specialUnits.size() >= maxUnits ) {
                    return result;
                }
            }
        }
        result.regularUnits += Math.min(maxUnits - result.getMobileIndependentSize(), Math.max(regularUnits - 1, 0));
        return result;
    }
    
    public Force getDefaultPlace(int number) {
        Force result = new Force();
        result.regularUnits += Math.min(number, regularUnits);
        for( SpecialUnit special : specialUnits ) {
            if( result.getSize() >= number ) {
                break;
            }
            result.specialUnits.add(special);
        }
        return result;
    }
    
    public Force getOneUnit() {
        Force result = new Force();
        if( getSpecialUnits().isEmpty() ) {
            result.addRegularUnits(1);
        } else {
            result.getSpecialUnits().add(getSpecialUnits().get(0));
        }
        return result;
    }
    
    public Force getOneMobileIndependentUnit() {        
        Force result = new Force();
        if( getSpecialUnits().isEmpty() ) {
            result.addRegularUnits(1);
        } else {
            for( SpecialUnit unit : getSpecialUnits() ) {
                if( unit.isMobile() && unit.isIndependentUnit() ) {
                    result.getSpecialUnits().add(unit);
                    break;
                }
            }
        }
        return result;
    }

    protected Object clone() {
        Force result = new Force();
        result.setRegularUnits(getRegularUnits());
        result.getSpecialUnits().addAll(getSpecialUnits());
        return result;
    }
    
    public String toString() {
        return "(" + getRegularUnits() + ", " + getSpecialUnits() + ")";
    }
}
