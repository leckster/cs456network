/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.NetworkConnection.Side;

/**
 * Objects of this class contain information about a network nodes and their
 * connections.
 */
public class NetworkModel {

	ArrayList<NetworkNode> nodes;
	ArrayList<NetworkConnection> connections;
	ArrayList<NetworkViewInterface> networkListeners;
	ArrayList<NetworkViewController> controllerListeners;
	Stack<CommandObj> done_actions;
	Stack<CommandObj> undone_actions;
	String filename;
	boolean changesSaved;
	boolean construction_mode = false;
	Timer timer;
	NameChangeTimerTask task = null;
	String oldNodeName;
	AffineTransform transform = new AffineTransform();

	/**
	 * Creates an empty network model that has a unique default file name and no
	 * contents
	 */
	public NetworkModel() {
		this.filename = getUniqueName();
		this.nodes = new ArrayList<>();
		this.connections = new ArrayList<>();
		this.networkListeners = new ArrayList<>();
		this.controllerListeners = new ArrayList<>();
		this.done_actions = new Stack();
		this.undone_actions = new Stack();
		this.changesSaved = true;
		this.oldNodeName = "";
		this.timer = new Timer();
		this.task = null;
		this.transform = new AffineTransform();
	}

	/**
	 * This function is called whenever setters are called in the model
	 *
	 * This should alert the View to repaint.
	 */
	public void update() {
		//go through list of listeners and call
		for (int i = 0; i < networkListeners.size(); i++) {
			networkListeners.get(i).updateView();
		}
		for (int i = 0; i < controllerListeners.size(); i++) {
			controllerListeners.get(i).updateEditMenuButtons();
		}
	}

	private void resetTextIndex() {
		for (int i = 0; i < networkListeners.size(); i++) {
			networkListeners.get(i).resetTextIndex();
		}
	}

	public void addTransform(AffineTransform t) {
		AffineTransform newTransform = new AffineTransform(this.transform);
		newTransform.concatenate(t);
		CommandObj command = new RotateScaleCommand(this.transform, newTransform, this);
		addNewCommand(command);
	}

	public void resetTransform() {
		CommandObj command = new RotateScaleCommand(this.transform, new AffineTransform(), this);
		addNewCommand(command);
	}

	public void setNewNodeLocation(int index, int x, int y) {
		this.changesSaved = false;
		this.nodes.get(index).setLocation(x, y);
	}

	void setNodeName(int index, String oldNodeName, String newNodeName) throws Exception {
		if (this.task == null) {
			this.oldNodeName = oldNodeName;
			this.task = new NameChangeTimerTask(this.oldNodeName, newNodeName, index, this);
		} else {
			System.out.println("Task Canceled, new Task set");
			this.task.cancel();
			this.task = new NameChangeTimerTask(this.oldNodeName, newNodeName, index, this);
		}
		this.timer.schedule(this.task, 2000);
		for (int i = 0; i < this.nConnections(); i++) {
			NetworkConnection conn = this.getConnection(i);
			if (conn.getNodeOne().equals(oldNodeName)) {
				conn.setNodeOne(newNodeName);
			}
			if (conn.getNodeTwo().equals(newNodeName)) {
				conn.setNodeTwo(newNodeName);
			}
		}
		this.getNode(index).setName(newNodeName);
	}

	public void addNameChangeCommand(String oldName, String newName, int index) {
		System.out.println("Name Change Command Added");
		this.task = null;
		CommandObj command = new ChangeNodeNameCommand(oldName, newName, index, this);
		addNewCommand(command);
		resetTextIndex();
	}

	public void changeNodeLocation(int index, int newX, int newY, int oldX, int oldY) {
		CommandObj command = new MoveNodeCommand(this, index, new Point(newX, newY), new Point(oldX, oldY));
		addNewCommand(command);
	}

	private void addNewCommand(CommandObj command) {
		command._do();
		this.done_actions.push(command);
		this.undone_actions.clear();
		this.changesSaved = false;
		this.update();
	}

	public boolean canUndo() {
		return !this.done_actions.empty();
	}

	public boolean canRedo() {
		return !this.undone_actions.empty();
	}

	public void undo() {
		if (!this.canUndo()) {
			return;
		}
		CommandObj command = this.done_actions.pop();
		command._undo();
		this.undone_actions.push(command);
		this.changesSaved = false;
		this.update();
	}

	public void redo() {
		if (!this.canRedo()) {
			return;
		}
		CommandObj command = this.undone_actions.pop();
		command._do();
		this.done_actions.push(command);
		this.changesSaved = false;
		this.update();
	}

	/**
	 * adds a network view to the list of listeners.
	 *
	 * @param newView
	 */
	public void addNetworkViewListener(NetworkViewInterface newView) {
		this.networkListeners.add(newView);
	}

	public void addControllerListener(NetworkViewController controller) {
		this.controllerListeners.add(controller);
	}

	public void removeControllerListener(NetworkViewController controller) {
		this.controllerListeners.remove(controller);
	}

	/**
	 * removes a network view from the list of listeners
	 *
	 * @param view
	 */
	public void removeNetworkViewListener(NetworkViewInterface view) {
		this.networkListeners.remove(view);
	}

	public int numberOfNetworkListeners() {
		return this.networkListeners.size();
	}

	private String getUniqueName() {
		for (int i = 0; i < 10000; i++) {
			String fileName = "network" + i + ".txt";
			File file = new File(fileName);
			if (!file.exists()) {
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(fileName, "UTF-8");
				} catch (FileNotFoundException ex) {
					Logger.getLogger(NetworkModel.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(NetworkModel.class.getName()).log(Level.SEVERE, null, ex);
				}
				writer.close();
				return fileName;
			}
		}

		return "";
	}

	/**
	 * Reads the specific file and creates a new NetworkModel object that
	 * contains all of the information in the file. If there is no such file
	 * then an exception should be thrown.
	 *
	 * @param fileName the name of the file to be read.
	 */
	public NetworkModel(String fileName) throws Exception {
		this.timer = new Timer();

		this.nodes = new ArrayList<>();
		this.connections = new ArrayList<>();
		this.networkListeners = new ArrayList<>();
		this.controllerListeners = new ArrayList<>();
		this.done_actions = new Stack();
		this.undone_actions = new Stack();
		this.construction_mode = true;
		this.filename = fileName;


		File file = new File(this.filename);
		if (!file.exists()) {
			//System.out.println("FileName: "+this.filename);
			throw new FileNotFoundException();
		}

		BufferedReader reader = new BufferedReader(new FileReader(this.filename));
		String line = null;
		while ((line = reader.readLine()) != null) {
			processLine(line);
		}
		this.construction_mode = false;
	}

	private void processLine(String line) {
		Scanner s = new Scanner(line);
		String lineType = s.next();
		if (lineType.equalsIgnoreCase("N")) {
			double xCenter = Double.parseDouble(s.next());
			double yCenter = Double.parseDouble(s.next());
			s.useDelimiter("\"");
			String name = s.next();
			name = s.next();

			NetworkNode newNode = new NetworkNode(name, xCenter, yCenter);
			newNode.setNetwork(this);

			this.addNode(newNode);
		} else if (lineType.equalsIgnoreCase("C")) {
			s.useDelimiter("\"");
			s.next();
			String node1 = s.next();
			s.reset();
			s.next();
			Side side1 = getSide(s.next());
			s.useDelimiter("\"");
			s.next();
			String node2 = s.next();
			s.reset();
			s.next();
			Side side2 = getSide(s.next());

			NetworkConnection newConnection = new NetworkConnection(node1, side1, node2, side2);

			this.addConnection(newConnection);
		} else {
			//ERROR because the line type is not N or C.
		}
	}

	private Side getSide(String side) {

		switch (side) {
			case "R":
				return Side.Right;
			case "L":
				return Side.Left;
			case "T":
				return Side.Top;
			case "B":
				return Side.Bottom;
			default:
				return Side.Right;
		}
	}

	/**
	 * Returns the name of the file associated with this model.
	 */
	public String getFileName() {
		return this.filename;
	}

	/**
	 * Changes the file name associated with this model
	 *
	 * @param newFileName the new file name
	 */
	public void setFileName(String newFileName) {
		this.filename = newFileName;
	}

	/**
	 * Saves the contents of this model to its file.
	 */
	public void save() {
		try {
			System.out.println("Writing to file: " + this.filename);
			FileWriter writer = new FileWriter(new File(this.filename), false);
			for (int i = 0; i < this.nodes.size(); i++) {
				NetworkNode node = this.nodes.get(i);
				writer.write(node.toString());
				System.out.println(node.toString());
			}
			for (int i = 0; i < this.connections.size(); i++) {
				NetworkConnection connection = this.connections.get(i);
				writer.write(connection.toString());
				System.out.println(connection.toString());
			}
			writer.close();
			this.changesSaved = true;
		} catch (IOException ex) {
			Logger.getLogger(NetworkModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void saveToNewLocation(String absolutePath) {
		try {
			System.out.println("Writing to file: " + absolutePath);
			FileWriter writer = new FileWriter(new File(absolutePath), false);
			for (int i = 0; i < this.nodes.size(); i++) {
				NetworkNode node = this.nodes.get(i);
				writer.write(node.toString());
				System.out.println(node.toString());
			}
			for (int i = 0; i < this.connections.size(); i++) {
				NetworkConnection connection = this.connections.get(i);
				writer.write(connection.toString());
				System.out.println(connection.toString());
			}
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(NetworkModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns true if there are unsaved changes.
	 */
	public boolean unsavedChanges() {
		return !this.changesSaved;
	}

	/**
	 * Adds the specified NetworkNode to the list of network objects
	 *
	 * @param newNode
	 */
	public void addNode(NetworkNode newNode) {
		if (construction_mode) {
			this.nodes.add(newNode);
		} else {
			CommandObj command = new NewNodeCommand(newNode, this);
			addNewCommand(command);
		}
		this.changesSaved = false;
		this.update();
	}

	/**
	 * Returns the number of network node objects in this model.
	 */
	public int nNodes() {
		return this.nodes.size();
	}

	/**
	 * Returns the specified NetworkNode. Indexes begin at zero.
	 *
	 * @param i index of the desired object. Must be less than nNodes()
	 */
	public NetworkNode getNode(int i) { // throws Exception {
		if (i >= this.nodes.size() || i < 0) {
			//throw new Exception();
		}
		return this.nodes.get(i);
	}

	public NetworkNode getNodeWithName(String name) {
		for (int i = 0; i < this.nodes.size(); i++) {
			if (this.nodes.get(i).getName().equals(name)) {
				return this.nodes.get(i);
			}
		}
		return this.nodes.get(0);
	}

	/**
	 * Removes the specified object from the list of nodes. Also removes any
	 * connections to the removed node.
	 *
	 * @param i the index of the object to be removed.
	 */
	public void removeNode(int i) {
		this.changesSaved = false;
		NetworkNode removedNode = this.nodes.remove(i);

		for (int j = this.connections.size() - 1; j >= 0; j--) {
			NetworkConnection connection = this.connections.get(j);
			if (connection.node1 == removedNode.getName() || connection.node2 == removedNode.getName()) {
				this.removeConnection(j);
			}
		}
	}

	/**
	 * Adds the specified NetworkConnection to the list of connections
	 *
	 * @param newConnection
	 */
	public void addConnection(NetworkConnection newConnection) {
		if (construction_mode) {
			this.connections.add(newConnection);
		} else {
			CommandObj command = new NewConnectionCommand(newConnection, this);
			addNewCommand(command);
		}
		this.changesSaved = false;
		this.update();
	}

	/**
	 * Returns the number of network connections in this model.
	 */
	public int nConnections() {
		return this.connections.size();
	}

	/**
	 * Returns the specified NetworkConnection. Indexes begin at zero.
	 *
	 * @param i index of the desired object. Must be less than nConnections()
	 */
	public NetworkConnection getConnection(int i) {
		if (i >= this.connections.size() || i < 0) {
			//throw new Exception();
		}
		return this.connections.get(i);
	}

	/**
	 * Removes the specified object from the list of connections
	 *
	 * @param i the index of the object to be removed.
	 */
	public void removeConnection(int i) {
		this.changesSaved = false;
		this.connections.remove(i);
	}

	/**
	 * This method is a regression test to verify that this class is implemented
	 * correctly. It should test all of the methods including the exceptions. It
	 * should be completely self checking. This should write "testing
	 * NetworkModel" to System.out before it starts and "NetworkModel OK" to
	 * System.out when the test terminates correctly. Nothing else should appear
	 * on a correct test. Other messages should report any errors discovered.
	 *
	 */
	public static void Test() {
		System.out.println("testing NetworkModel");
		boolean allTestsPass = true;

		allTestsPass = testAddConnection();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testAddNode();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testGetConnection();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testGetFileName();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testGetNode();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testNConnections();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testNNodes();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testRemoveConnection();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testRemoveNode();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testSave();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testSetFileName();
		if (!allTestsPass) {
			return;
		}
		allTestsPass = testUnsavedChanges();
		if (!allTestsPass) {
			return;
		}

		if (allTestsPass) {
			System.out.println("NetworkModel OK");
		}

	}

	private static boolean testAddConnection() {
		NetworkModel m = new NetworkModel();

		m.addConnection(new NetworkConnection("node1", Side.Right, "node2", Side.Left));

		return true;
	}

	private static boolean testAddNode() {

		NetworkModel m = new NetworkModel();

		m.addNode(new NetworkNode("node1", 100, 200));
		m.addNode(new NetworkNode("node2", 200, 300));

		return true;
	}

	private static boolean testGetConnection() {
		NetworkModel m = new NetworkModel();

		m.addConnection(new NetworkConnection("node1", Side.Right, "node2", Side.Left));

		try {
			m.getConnection(2);
		} catch (Exception ex) {
		}

		try {
			m.getConnection(m.nConnections() - 1);
		} catch (Exception ex) {
			System.out.println("testGetConnection(): Caught Exception for get connection inside of range");
			return false;
		}

		return true;
	}

	private static boolean testGetFileName() {
		NetworkModel m1 = new NetworkModel();
		NetworkModel m2 = new NetworkModel();
		NetworkModel m3;
		try {
			m3 = new NetworkModel("network.txt");
		} catch (Exception ex) {
			System.out.println("testGetFileName(): Caught Exception for making new NetworkModel with valid name");
			return false;
		}

		if (!m3.getFileName().equals("network.txt")) {
			System.out.println("testGetFileName(): NetworkModel file name is not equal to privided valid file name");
			return false;
		}

		try {
			m3 = new NetworkModel("fakeFile.txt");
		} catch (Exception ex) {
			//valid that exception is thrown.
		}


		if (m1.getFileName().equals(m2.getFileName())) {
			System.out.println("testGetFileName(): Filenames not unique");
			return false;
		}

		return true;
	}

	private static boolean testGetNode() {
		NetworkModel m = new NetworkModel();

		m.addNode(new NetworkNode("node1", 100, 200));
		m.addNode(new NetworkNode("node2", 200, 300));

		try {
			m.getNode(2);
		} catch (Exception ex) {
			//valid for exception to be thrown
		}

		try {
			m.getNode(m.nNodes() - 1);
		} catch (Exception ex) {
			System.out.println("testGetNode(): Caught Exception for get node inside of range");
			return false;
		}

		return true;
	}

	private static boolean testNConnections() {
		NetworkModel m = new NetworkModel();

		m.addConnection(new NetworkConnection("node1", Side.Right, "node2", Side.Left));

		if (m.nConnections() != 1) {
			System.out.println("testNConnections(): number of connections invalid");
			return false;
		}

		return true;
	}

	private static boolean testNNodes() {
		NetworkModel m = new NetworkModel();

		m.addNode(new NetworkNode("node1", 100, 200));
		m.addNode(new NetworkNode("node2", 200, 300));

		if (m.nNodes() != 2) {
			System.out.println("testNNodes(): number of nodes invalid");
			return false;
		}
		return true;
	}

	private static boolean testRemoveConnection() {
		NetworkModel m = new NetworkModel();

		m.addConnection(new NetworkConnection("node1", Side.Right, "node2", Side.Left));
		m.addConnection(new NetworkConnection("node1", Side.Right, "node2", Side.Left));

		m.removeConnection(1);
		m.removeConnection(0);

		return true;
	}

	private static boolean testRemoveNode() {
		NetworkModel m = new NetworkModel();

		m.addNode(new NetworkNode("node1", 100, 200));
		m.addNode(new NetworkNode("node2", 200, 300));

		m.removeNode(1);
		m.removeNode(0);

		return true;
	}

	private static boolean testSave() {
		NetworkModel m = new NetworkModel();

		m.addNode(new NetworkNode("node1", 100, 200));
		m.addNode(new NetworkNode("node2", 200, 300));

		m.save();

		m.removeNode(1);

		m.save();

		return true;
	}

	private static boolean testSetFileName() {
		NetworkModel m = new NetworkModel();

		m.setFileName("test.txt");

		return true;
	}

	private static boolean testUnsavedChanges() {
		NetworkModel m = new NetworkModel();

		if (m.unsavedChanges() == true) {
			System.out.println("testUnsavedChanges(): there are unsaved changes after NetworkModel create");
			return false;
		}
		m.addNode(new NetworkNode("node1", 100, 200));
		if (m.unsavedChanges() == false) {
			System.out.println("testUnsavedChanges(): changes are not unsaved after addNode()");
			return false;
		}
		m.save();
		if (m.unsavedChanges() == true) {
			System.out.println("testUnsavedChanges(): there are unsaved changes after save()");
			return false;
		}
		return true;
	}
}