/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.util.TimerTask;

/**
 *
 * @author Leckie Gunter
 */
public class NameChangeTimerTask extends TimerTask {
	
	String oldName;
	String newName;
	int index;
	NetworkModel network;
	
	
	public NameChangeTimerTask(String oldName, String newName, int index, NetworkModel network){
		this.oldName = oldName;
		this.newName = newName;
		this.index = index;
		this.network = network;
	}
	
	@Override
	public void run() {
		network.addNameChangeCommand(oldName, newName, index);
	}

}
