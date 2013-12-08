/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 *
 * @author Phoenix
 */
public class NewNodeCommand implements CommandObj {

	private int nodeIndex;
	private NetworkNode node;
	private NetworkModel model;

	public NewNodeCommand(NetworkNode node, NetworkModel network) {
		this.node = node;
		this.model = network;
		this.nodeIndex = network.nNodes();
	}

	@Override
	public void _do() {
		System.out.println("New Node DO/ Redo. Index: " + model.nNodes());
		model.nodes.add(node);
	}

	@Override
	public void _undo() {
		System.out.println("New Node Undo: " + nodeIndex);
		model.nodes.remove(nodeIndex);
	}
}
