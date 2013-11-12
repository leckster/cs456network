/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

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

	public NetworkViewController() {
	}

	public NetworkViewController(String fName) {
		File file = new File(fName);
		this.fileName = file.getAbsolutePath();
		F = new JFrame("Showing Network from file:" + fileName);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open", KeyEvent.VK_O);
		JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
		JMenuItem saveAs = new JMenuItem("Save As", KeyEvent.VK_A);

		open.addActionListener(this);
		save.addActionListener(this);
		saveAs.addActionListener(this);

		menu.add(open);
		menu.add(save);
		menu.add(saveAs);
		menuBar.add(menu);
		F.setJMenuBar(menuBar);

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
		F.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
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
			default:

				break;
		}
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

	private void makeNewNetwork(String absolutePath) {
		this.fileName = absolutePath;
		network.saveToNewLocation(fileName);
		try {
			network = new NetworkModel(fileName);
			network.save();
			view.changeNetwork(network);
			System.out.println(NetworkModels.getCoutForKey(fileName));
			NetworkModels.addModel(fileName, network);
			System.out.println(NetworkModels.getCoutForKey(fileName));
		} catch (Exception ex) {
			Logger.getLogger(NetworkViewController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
