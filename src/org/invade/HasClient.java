/*
 * HasClient.java
 *
 * Created on July 25, 2005, 2:31 PM
 *
 */

package org.invade;

import org.invade.gameserver.Client;

public interface HasClient {
    public boolean confirmDisconnect();
    public Client getClient();
    public void setClient(Client client);
}
