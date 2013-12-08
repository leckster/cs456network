/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.awt.geom.AffineTransform;

/**
 *
 * @author Leckie Gunter
 */
public class RotateScaleCommand implements CommandObj {
	
	AffineTransform oldTransform;
	AffineTransform newTransform;
	NetworkModel model;
	
	public RotateScaleCommand(AffineTransform oldT, AffineTransform newT, NetworkModel network) {
		oldTransform = oldT;
		newTransform = newT;
		model = network;
	}
	
	@Override
	public void _do() {
		model.transform = newTransform;
	}

	@Override
	public void _undo() {
		model.transform = oldTransform;
	}

}
