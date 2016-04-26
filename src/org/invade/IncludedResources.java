/*
 * IncludedResources.java
 *
 * Created on August 5, 2005, 1:31 PM
 *
 */

package org.invade;
import java.util.ArrayList;
import java.util.List;
import org.invade.agents.AlphaAgent;
import org.invade.agents.BetaAgent;
import org.invade.agents.LazyRandomAgent;
import org.invade.agents.RandomAgent;
import org.invade.agents.VigilanteAgent;
import org.invade.rules.AmoebaRules;
import org.invade.rules.ClassicRules;
import org.invade.rules.DefaultRules;
import org.invade.rules.GodstormRules;
import org.invade.rules.SecretMissionRules;

public class IncludedResources {
    
    public static final List<Class> RULES = new ArrayList<Class>();
    static {
        RULES.add(DefaultRules.class);
        RULES.add(ClassicRules.class);
        RULES.add(SecretMissionRules.class);
        RULES.add(AmoebaRules.class);
        RULES.add(GodstormRules.class);
    }
    
    public static final List<Class> AGENTS = new ArrayList<Class>();
    static {
        AGENTS.add(LazyRandomAgent.class);
        AGENTS.add(RandomAgent.class);
        AGENTS.add(AlphaAgent.class);
        AGENTS.add(BetaAgent.class);
        AGENTS.add(VigilanteAgent.class);
    };
    
    public static final String MAP_NAMES[] = {
        "Invade Earth A.D. 2210",        
        "Amoeba Invasion",
        "Mars",
        "Classic",
        "Secret Mission",
        "Pantheon (Beta)"
    };
    
    public static final String MAPS[] = {
        "maps/world.xml",
        "maps/amoebaInvasion.xml",
        "maps/mars.xml",
        "maps/worldClassic.xml",
        "maps/worldSecretMission.xml",
        "maps/godstorm.xml"
    };
    
    public static final String MAP_IMAGES[] = {
        "(No image)",
        "world.jpg",
        "worldClassic.jpg",
        "mars.jpg",
        "godstorm.jpg"
    };
    
    private IncludedResources() {}
}
