/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author Phoenix
 */
public class Network extends Component {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		if(args.length > 1) {
			NetworkModel.Test();
		}
		
		String fileName = args[0];
		
		
		JFrame F = new JFrame("Showing Network from file:" + fileName);
		
		
		NetworkModel network = null;
		try {
			network = new NetworkModel(fileName);
		} catch (Exception ex) {
			//Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("Invald file name.");
			return;
		}
		
		NetworkView view = new NetworkView(network);
		
		F.add(view);
		
		F.setBounds(100, 100, 1000, 600);
		F.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent evt)
				{
					System.exit(0);
				}
			}
		);
		F.setVisible(true);
		
	}
	
}
