/*
 * HelpMenu.java
 *
 * Created on August 2, 2005, 1:16 PM
 *
 */

package org.invade;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.invade.external.BrowserLauncher;

public class HelpMenu extends JMenu implements ActionListener {
    
    private Frame window;
    
    private JMenuItem helpItem;
    private JMenuItem supportItem;
    private JMenuItem aboutItem;
    
    public HelpMenu(Frame window) {
        
        setText("Help");
        this.window = window;
        
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(this);
        helpItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        add(helpItem);
        
        supportItem = new JMenuItem("Support");
        supportItem.addActionListener(this);
        add(supportItem);
        
        addSeparator();
        
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        add(aboutItem);
        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if( actionEvent.getSource() == helpItem ) {
            try {
                BrowserLauncher.openURL("http://www.smileygames.net/invadeearth/manual");
            } catch(IOException e) {
                System.err.println("Error opening help:");
                e.printStackTrace();
            }
        } else if( actionEvent.getSource() == supportItem ) {
            try {
                BrowserLauncher.openURL("http://forum.smileygames.net/list.php?10");
            } catch(IOException e) {
                System.err.println("Error opening support page:");
                e.printStackTrace();
            }
        } else if( actionEvent.getSource() == aboutItem ) {
            new About(window).setVisible(true);
        }
    }
    
}
