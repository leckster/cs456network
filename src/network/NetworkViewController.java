/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import network.NetworkView.Mode;

/**
 *
 * @author Leckie Gunter
 */
public class NetworkViewController extends JPanel implements ActionListener {

	private JFrame F = null;
	private NetworkModel network = null;
	private NetworkView view = null;
	private String fileName = null;
	final JFileChooser fc = new JFileChooser();
	
	private JMenuItem undo;
	private JMenuItem redo;

	public NetworkViewController() {
	}

	public NetworkViewController(String fName) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		fc.setFileFilter(filter);
		File file = new File(fName);
		this.fileName = file.getAbsolutePath();
		F = new JFrame("Showing Network from file:" + fileName);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open", KeyEvent.VK_O);
		JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
		JMenuItem saveAs = new JMenuItem("Save As", KeyEvent.VK_A);

		open.addActionListener(this);
		save.addActionListener(this);
		saveAs.addActionListener(this);

		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		
		JMenu editMenu = new JMenu("Edit");
		undo = new JMenuItem("Undo", KeyEvent.VK_U);
		redo = new JMenuItem("Redo", KeyEvent.VK_R);
		
		undo.setEnabled(false);
		redo.setEnabled(false);
		
		undo.addActionListener(this);
		redo.addActionListener(this);
		
		editMenu.add(undo);
		editMenu.add(redo);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		F.setJMenuBar(menuBar);


		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(80, F.getHeight()));
		JButton selectMode = makeNavigationButton("cursor", "selectMode",
				"Enter Select Mode",
				"Select");
		JButton drawNodeMode = makeNavigationButton("oval", "drawNodeMode",
				"Draw a new Node",
				"New Node");
		JButton drawConnectionMode = makeNavigationButton("curve", "drawConnectionMode",
				"Draw a new Connection",
				"New Connection");
		JButton rotateMode = makeNavigationButton("rotate", "rotateMode",
				"Enter Rotate and Scale Mode",
				"Rotate");

		buttonPanel.add(selectMode);
		buttonPanel.add(drawNodeMode);
		buttonPanel.add(drawConnectionMode);
		buttonPanel.add(rotateMode);

		F.add(buttonPanel, BorderLayout.WEST);

		if (NetworkModels.modelExists(fileName)) {
			System.out.println("Model Exists: " + fileName);
			network = NetworkModels.getModelForKey(fileName);
		} else {
			try {
				network = new NetworkModel(fileName);
				network.save();
				NetworkModels.addModel(fileName, network);
			} catch (Exception ex) {
				Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("Invalid file name: " + fileName);
				return;
			}
		}
		network.addControllerListener(this);
		view = new NetworkView(network);

		F.add(view);

		F.setBounds(100, 100, 500, 400);
		F.addWindowListener(
				new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (network.numberOfNetworkListeners() == 1) {
					if (network.unsavedChanges()) {
						int confirm = JOptionPane.showOptionDialog(F,
								"Would you like to save any unsaved changes?",
								"Exit Confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, null, null);
						if (confirm == JOptionPane.YES_OPTION) {
							if (fileName != null) {
								NetworkModels.removeModel(fileName);
							}
							network.removeNetworkViewListener(view);
							System.out.println("YES");
							network.save();
							F.dispose();
							//Save and Close
						} else if (confirm == JOptionPane.NO_OPTION) {
							if (fileName != null) {
								NetworkModels.removeModel(fileName);
							}
							network.removeNetworkViewListener(view);
							System.out.println("NO");
							F.dispose();
							//Don't save and Close
						} else if (confirm == JOptionPane.CLOSED_OPTION || confirm == JOptionPane.CANCEL_OPTION) {
							System.out.println("Close");

							//Don't save and Don't close
						}
					} else {
						if (fileName != null) {
							NetworkModels.removeModel(fileName);
						}
						network.removeNetworkViewListener(view);
						F.dispose();
					}
				} else {
					network.removeNetworkViewListener(view);
					F.dispose();
				}
				if (NetworkModels.noMoreModels()) {
					System.out.println("No more models :)");
					System.exit(0);
				}
			}
		});
		F.setVisible(true);
		view.setSize();
		F.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	}

	private JButton makeNavigationButton(String imageName,
			String actionCommand,
			String toolTipText,
			String altText) {
		//Look for the image.
		String imgLocation = "resources/"
				+ imageName
				+ ".png";
		URL imageURL = NetworkViewController.class.getResource(imgLocation);

		//Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setSize(25, 25);

		//button.setBorderPainted(false);
		//button.setContentAreaFilled(false);
		//button.setFocusPainted(false);
		//button.setOpaque(false);
		button.setFocusable(false);

		if (imageURL != null) {//image found
			try {
				Image im = ImageIO.read(new File(imageURL.getFile()));
				ImageIcon img = new ImageIcon();
				img.setImage(im.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH));
				button.setIcon(img);
			} catch (IOException ex) {
				Logger.getLogger(NetworkViewController.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {                                     //no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}
	
	public void updateEditMenuButtons() {
		if(network.canUndo()) {
			undo.setEnabled(true);
		} else {
			undo.setEnabled(false);
		}
		if(network.canRedo()) {
			redo.setEnabled(true);
		} else {
			redo.setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println(e.paramString());
		switch (command) {
			case "Open":
				this.openFile();
				break;
			case "Save":
				this.saveFile();
				break;
			case "Save As":
				this.saveAsFile();
				break;
			case "Undo":
				this.undo();
				break;
			case "Redo":
				this.redo();
				break;
			case "selectMode":
				this.changeMode(Mode.Select);
				break;
			case "drawNodeMode":
				this.changeMode(Mode.AddNode);
				break;
			case "drawConnectionMode":
				this.changeMode(Mode.AddConnection);
				break;
			case "rotateMode":
				this.changeMode(Mode.Rotate);
				break;
			default:

				break;
		}
	}

	private void changeMode(Mode mode) {
		this.view.setMode(mode);
	}

	private void openFile() {
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			//This is where a real application would open the file.
			System.out.println("Opening: " + file.getAbsolutePath() + ".");
			new NetworkViewController(file.getAbsolutePath());
		} else {
			System.out.println("Open command cancelled by user.");
		}

	}

	private void saveFile() {
		network.save();
	}

	private void saveAsFile() {
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			//This is where a real application would open the file.
			System.out.println("Saving over: " + file.getAbsolutePath() + ".");
			this.makeNewNetwork(file.getAbsolutePath());
		} else {
			System.out.println("Save command cancelled by user.");
		}
	}
	
	private void undo(){
		this.network.undo();
	}
	
	private void redo() {
		this.network.redo();
	}

	private void makeNewNetwork(String absolutePath) {
		this.fileName = absolutePath;
		network.saveToNewLocation(fileName);
		if (network.numberOfNetworkListeners() == 1) {
			NetworkModels.removeModel(network.getFileName());
		}
		try {
			network = new NetworkModel(fileName);
			network.save();
			view.changeNetwork(network);
			NetworkModels.addModel(fileName, network);
			NetworkModels.printMap();
		} catch (Exception ex) {
			Logger.getLogger(NetworkViewController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
