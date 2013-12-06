/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.awt.Point;

/**
 *
 * @author Leckie Gunter
 */
public class MoveNodeCommand implements CommandObj{
	private NetworkModel network;
	private int nodeIndex;
	private Point oldLocation;
	private Point newLocation;
	
	public MoveNodeCommand (NetworkModel network, int index, Point newLocation, Point oldLocation){
		this.newLocation = newLocation;
		this.oldLocation = oldLocation;
		this.network = network;
		this.nodeIndex = index;
	}
	
	@Override
	public void _do() {
		network.setNewNodeLocation(nodeIndex, newLocation.x, newLocation.y);
	}

	@Override
	public void _undo() {
		network.setNewNodeLocation(nodeIndex, oldLocation.x, oldLocation.y);
	}

}
