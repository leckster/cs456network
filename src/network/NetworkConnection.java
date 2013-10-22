/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 * This class describes a connection between two network nodes
 */
public class NetworkConnection {

	String node1;
	Side side1;
	String node2;
	Side side2;

	public enum Side {

		Left, Right, Top, Bottom
	}

	/**
	 * Creates a new connection
	 *
	 * @param node1 the name of the first node to be connected
	 * @param side1 specifies the side of node1 to which the connection is to be
	 * attached
	 * @param node2 the name of the second node to be connected
	 * @param side2 specifies the side of node2 to which the connection is to be
	 * attached
	 */
	public NetworkConnection(String node1, Side side1, String node2, Side side2) {
		this.node1 = node1;
		this.side1 = side1;

		this.node2 = node2;
		this.side2 = side2;
	}
	
	@Override
	public String toString() {
		return "C \""+this.node1+"\" "+getSideString(this.side1)+" \""+this.node2+"\" "+getSideString(this.side2);
	}
	
	private String getSideString(Side s) {
		if(s == Side.Left) {
			return "L";
		} else if(s == Side.Right) {
			return "R";
		} else if(s == Side.Top) {
			return "T";
		} else if(s == Side.Bottom) {
			return "B";
		} else {
			return "R";
		}
	}
}