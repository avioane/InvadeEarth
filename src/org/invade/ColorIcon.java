
package org.invade;

import java.awt.*;
import javax.swing.Icon;

public abstract class ColorIcon implements Icon {
    
    public static final int SIZE = 12;   
    
    public abstract Color getColor();
    
    public int getIconHeight() { return SIZE; }
    public int getIconWidth() { return (SIZE * 3) / 2; }
    
    public void paintIcon(Component component, Graphics page, int x, int y) {
        page.setColor(getColor());
        page.fillRect(x, y, SIZE, SIZE);
        page.setColor(Color.BLACK);
        page.drawRect(x, y, SIZE, SIZE);
    }
}