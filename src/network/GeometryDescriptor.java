/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

/**
 *
 * @author Leckie Gunter
 */
class GeometryDescriptor {
	
	public enum Type { CONNECTION, NODE };
	
	Type type;
	NetworkConnection connection;
	NetworkNode node;
	int textLocation;
	int index;
	
	public GeometryDescriptor() {
		type = null;
		connection = null;
		node = null;
		textLocation = -1;
		index = -1;
	}
	
	@Override
	public String toString() {
		return (connection != null) ? "connection at index " + index : ((node != null) ? "node at index " + index + " with text index " + textLocation : "nothing Selected");
	}

	GeometryDescriptor(NetworkConnection c, int i) {
		type = Type.CONNECTION;
		connection = c;
		node = null;
		textLocation = 0;
		index = i;
	}

	GeometryDescriptor(NetworkNode n, int textLoc, int i) {
		type = Type.NODE;
		connection = null;
		node = n;
		textLocation = textLoc;
		index = i;
	}
}
