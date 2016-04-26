/*
 * WindowCenteringUtility.java
 *
 * Created on July 22, 2005, 12:46 PM
 *
 */

package org.invade;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class WindowCenteringUtility {
    private WindowCenteringUtility() {}
    
    public static void center(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();
        
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        
        window.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }
    
}
