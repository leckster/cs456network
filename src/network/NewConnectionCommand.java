/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 *
 * @author Leckie Gunter
 */
public class NewConnectionCommand implements CommandObj {

	private int connectionIndex;
	private NetworkConnection connection;
	private NetworkModel model;

	public NewConnectionCommand(NetworkConnection connection, NetworkModel network) {
		this.connection = connection;
		this.model = network;
		this.connectionIndex = network.nConnections();
	}

	@Override
	public void _do() {
		System.out.println("New Connection DO/ Redo. Index: " + model.nConnections());
		model.connections.add(connection);
	}

	@Override
	public void _undo() {
		System.out.println("New Connection Undo: " + connectionIndex);
		model.connections.remove(connectionIndex);
	}
}
