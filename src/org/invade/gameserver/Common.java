/*
 * Common.java
 *
 * Created on April 3, 2007, 11:05 AM
 */

package org.invade.gameserver;

/**
 *
 * @author Jonathan Crosmer
 */
public class Common {
    
    public static final String SERVER_HANDLER_CLASS_NAME = getPackageName() + ".ServerHandler";
    public static final String CLIENT_HANDLER_CLASS_NAME = getPackageName() + ".ClientHandler";
    public static String getPackageName() {
        return Common.class.getPackage().getName();
    }
    
}
