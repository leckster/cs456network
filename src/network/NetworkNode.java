/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 * Objects of this class describe a single node in a network.
*
 */
public class NetworkNode {

	double xCenter;
	double yCenter;
	String name;
	NetworkModel network;

	/**
	 * Creates a network node
	 *
	 * @param nodeName the name that the node will be identified by. Names are
	 * exact and case sensitive.
	 * @param xCenter the X coordinate of the center of the node in pixels
	 * @param yCenter the Y coordinate of the center of the node in pixels
	 */
	public NetworkNode(String nodeName, double xCenter, double yCenter) {
		this.name = nodeName;
		this.xCenter = xCenter;
		this.yCenter = yCenter;
	}
	
	@Override
	public String toString() {
		return "N "+this.xCenter+" "+this.yCenter+" \""+this.name+"\" ";
	}

	/**
	 * @return name of the node
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Changes the name of the node
	 *
	 * @param newName
	 */
	public void setName(String newName) {
		this.name = newName;
		alertModel();
	}

	/**
	 * @return the X coordinate of the center of the node
	 */
	public double getX() {
		return this.xCenter;
	}

	/**
	 * @return the Y coordinate of the center of the node
	 */
	public double getY() {
		return this.yCenter;
	}

	/**
	 * Changes the location of the center of the node
	 */
	public void setLocation(double xCenter, double yCenter) {
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		alertModel();
	}

	/**
	 * Sets a reference to the network model that this node belongs to
	 *
	 * @param network
	 */
	public void setNetwork(NetworkModel network) {
		this.network = network;
		alertModel();
	}

	/**
	 * @return the network that this node belongs to
	 */
	public NetworkModel getNetwork() {
		return this.network;
	}
	
	private void alertModel() {
		this.network.update();
	}
}