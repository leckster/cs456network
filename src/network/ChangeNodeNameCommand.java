/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

/**
 *
 * @author Leckie Gunter
 */
public class ChangeNodeNameCommand implements CommandObj {
	
	private int nodeIndex;
	private NetworkModel model;
	private String oldName;
	private String newName;
	
	public ChangeNodeNameCommand(String oldName, String newName, int index, NetworkModel network) {
		this.oldName = oldName;
		this.newName = newName;
		this.nodeIndex = index;
		this.model = network;
	}
	
	@Override
	public void _do() {
		System.out.println("Change Node Name DO");
		model.getNode(nodeIndex).setName(newName);
		for (int i = 0; i < model.nConnections(); i++) {
			NetworkConnection conn = model.getConnection(i);
			if (conn.getNodeOne().equals(oldName)) {
				conn.setNodeOne(newName);
			}
			if (conn.getNodeTwo().equals(oldName)) {
				conn.setNodeTwo(newName);
			}
		}
		
	}

	@Override
	public void _undo() {
		System.out.println("Change Node Name UNDO");
		model.getNode(nodeIndex).setName(oldName);
		for (int i = 0; i < model.nConnections(); i++) {
			NetworkConnection conn = model.getConnection(i);
			if (conn.getNodeOne().equals(newName)) {
				conn.setNodeOne(oldName);
			}
			if (conn.getNodeTwo().equals(newName)) {
				conn.setNodeTwo(oldName);
			}
		}
	}

}
