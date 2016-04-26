/*
 * TerritoryValueMap.java
 *
 * Created on August 7, 2005, 2:53 PM
 *
 */

package org.invade.agents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.invade.Territory;

/**
 *
 * @author Jonathan Crosmer
 */
public class TerritoryValueMap {
    
    Map<Territory, Double> values = new HashMap<Territory, Double>();
    
    public void clear() {
        values.clear();
    }
    
    public void set(Territory territory, double value) {
        values.put(territory, value);
    }
    
    public void addValue(Territory territory, double addend) {
        values.put(territory, get(territory) + addend);
    }
    
    public double get(Territory territory) {
        Double value = values.get(territory);
        return value == null ? 0.0 : value;
    }
    
    // Sorted from highest to lowest
    public List<Territory> getTerritories() {
        List<Territory> result = new ArrayList<Territory>(values.keySet());
        Collections.sort(result, new Comparator<Territory>() {
            public int compare(Territory first, Territory second) {
                return (int)Math.signum(values.get(second) - values.get(first));
            }
        });
        return result;
    }
        
}
