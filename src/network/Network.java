/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.*;

/**
 *
 * @author Phoenix
 */
public class Network extends Component {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		if (args.length > 1) {
			NetworkModel.Test();
		}

		String fileName = args[0];

		NetworkViewController NVC = new NetworkViewController(fileName);
//		NetworkViewController NVC2 = new NetworkViewController(fileName);

	}
}
