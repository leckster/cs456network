/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import network.NetworkConnection.Side;

/**
 *
 * @author Leckie Gunter
 */
public class NetworkView extends JPanel{
	
	private NetworkModel network;
	
	public NetworkView(NetworkModel network) {
		
		this.network = network;
		
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
	
	@Override
	public void processMouseEvent(MouseEvent evnt) {
		
		if(evnt.getID() == MouseEvent.MOUSE_RELEASED) {
			Point mouseLoc = evnt.getPoint();
			GeometryDescriptor gd = pointGeometry(mouseLoc);
			System.out.println(gd.toString());
		}
	}
	
	@Override
	public void paint(Graphics g) {
		
		g.setColor(Color.blue);
		
		for(int i = 0; i < this.network.nodes.size(); i++) {
			try {
				paintNode(g, this.network.getNode(i));
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for(int i = 0; i < this.network.connections.size(); i++) {
			try {
				paintConnection(g, this.network.getConnection(i));
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		
	}
	
	public GeometryDescriptor pointGeometry(Point mouseLoc) {
		GeometryDescriptor objectClicked = new GeometryDescriptor();
		
		for(int i = this.network.connections.size() - 1; i >= 0; i--) {
			try {
				NetworkConnection c = this.network.getConnection(i);
				if(containsPoint(c, mouseLoc)) {
					objectClicked = new GeometryDescriptor(c, i);
					return objectClicked;
				}
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for(int i = this.network.nodes.size() - 1; i >= 0; i--) {
			try {
				NetworkNode n = this.network.getNode(i);
				if(containsPoint(n, mouseLoc)) {
					int textLocation = getTextLocation(n, mouseLoc);
					objectClicked = new GeometryDescriptor(n, textLocation, i);
					return objectClicked;
				}
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return objectClicked;
	}
	
	private boolean containsPoint(NetworkConnection c, Point mouseLoc) {
		int xMax, xMin, yMax, yMin;
		Graphics g = this.getGraphics();
		XYPoints p = getXYPointsFromConnection(g, c);
		
		if(p.x1 > p.x2) {
			xMax = p.x1;
			xMin = p.x2;
		} else {
			xMin = p.x1;
			xMax = p.x2;
		}
		if(p.y1 > p.y2) {
			yMax = p.y1;
			yMin = p.y2;
		} else {
			yMin = p.y1;
			yMax = p.y2;
		}
		
		//Check bounding box
		if(mouseLoc.getX() < xMin || mouseLoc.getX() > xMax || mouseLoc.getY() < yMin || mouseLoc.getY() > yMax ) {
			return false;
		}
		
		//second check closest point
		if(distToSegment(mouseLoc, new Point(p.x1, p.y1), new Point(p.x2, p.y2)) > 5) {
			return false;
		}
		
		return true;
	}
	
	private float sqr(float x) { return x * x; }
	private float dist2(Point v, Point w) { return sqr(v.x - w.x) + sqr(v.y - w.y); }
	private float distToSegmentSquared(Point p, Point v, Point w) {
		float l2 = dist2(v, w);
		if (l2 == 0) return dist2(p, v);
		float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
		
		if (t < 0) return dist2(p, v);
		if (t > 1) return dist2(p, w);
		return dist2(p, new Point( (int)(v.x + t * (w.x - v.x)), (int)(v.y + t * (w.y - v.y)) ));
	}
	private int distToSegment(Point p, Point v, Point w) { 
		return (int)Math.sqrt(distToSegmentSquared(p, v, w)); 
	}
	
	private int getTextLocation(NetworkNode n, Point mouseLoc) {
		
		Graphics g = this.getGraphics();
		
		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int)n.getX()-textWidth/2;
		int textRight = (int)n.getX()+textWidth/2;
		
		int textBase = (int)n.getY()+FM.getHeight()/2;
		int textTop = (int)n.getY()-FM.getHeight()/2;
		
		//test bounding box of text
		if(mouseLoc.getX() < textLeft || mouseLoc.getX() > textRight || mouseLoc.getY() < textTop || mouseLoc.getY() > textBase ) {
			return -1;
		}
		
		//find character index
		int xOffset = mouseLoc.x - textLeft;
		int totalCharWidth = 0;
		for(int i = 0; i < n.getName().length(); i++) {
			totalCharWidth += FM.charWidth(n.getName().charAt(i));
			if(totalCharWidth > xOffset) return i;
		}
		
		return -1;
	}
	
	private boolean containsPoint(NetworkNode n, Point mouseLoc) {
		int xMax, xMin, yMax, yMin;
		
		Graphics g = this.getGraphics();
		
		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int)n.getX()-textWidth/2;
		int textRight = (int)n.getX()+textWidth/2;
		
		int textBase = (int)n.getY()+FM.getHeight()/2;
		int textTop = (int)n.getY()-FM.getHeight()/2;
		
		int margin = 10;
		
		xMin = textLeft - margin;
		xMax = xMin + (textWidth + margin * 2);
		yMin = textTop - margin;
		yMax = yMin + (textHeight + margin * 2);
		
		//Check bounding box
		if(mouseLoc.getX() < xMin || mouseLoc.getX() > xMax || mouseLoc.getY() < yMin || mouseLoc.getY() > yMax ) {
			return false;
		}
		
		//Check if inside actual oval
		Point center = new Point( xMin + textWidth/2 + margin, yMin + textHeight/2 + margin);

		double _xRadius = textWidth/2 + margin;
		double _yRadius = textHeight/2 + margin;

		if (_xRadius <= 0.0 || _yRadius <= 0.0)
			return false;
		Point normalized = new Point((int)(mouseLoc.getX() - center.getX()), (int)(mouseLoc.getY() - center.getY()));

		if(((double)(normalized.getX() * normalized.getX() / (_xRadius * _xRadius))
			+ ((double)(normalized.getY() * normalized.getY()) / (_yRadius * _yRadius))) > 1.0) 
		{
			return false;
		}
		
		//Check string location
		
		return true;
	}
	
	private void paintNode(Graphics g, NetworkNode n) {
		
		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int)n.getX()-textWidth/2;
		int textRight = (int)n.getX()+textWidth/2;
		
		int textBase = (int)n.getY()+FM.getHeight()/2;
		int textTop = (int)n.getY()-FM.getHeight()/2;
		
		g.drawString(n.getName(),(int)textLeft, (int)textBase);
		
		int margin = 10;
		
		int borderLeft = textLeft - margin;
		int borderWidth = textWidth + margin * 2;
		int borderTop = textTop - margin/2;
		int borderHeight = textHeight + margin * 2;
		
		g.drawOval(borderLeft, borderTop, borderWidth, borderHeight);
	}
	
	private void paintConnection(Graphics g, NetworkConnection c) {
		
		XYPoints p = getXYPointsFromConnection(g, c);
		
		g.drawLine(p.x1, p.y1, p.x2, p.y2);
	}
	
	private XYPoints getXYPointsFromConnection(Graphics g, NetworkConnection c) {
		String node1 = c.node1;
		String node2 = c.node2;
		
		FontMetrics FM = g.getFontMetrics();
		int n1Width = FM.stringWidth(node1);
		int n2Width = FM.stringWidth(node2);
		int textHeight = FM.getHeight();
		
		NetworkNode n1 = this.network.getNodeWithName(node1);
		NetworkNode n2 = this.network.getNodeWithName(node2);
		
		int n1Left = (int)n1.getX()-n1Width/2;
		int n2Left = (int)n2.getX()-n2Width/2;
		
		int n1Top = (int)n1.getY()-FM.getHeight()/2;
		int n2Top = (int)n2.getY()-FM.getHeight()/2;
		
		int margin = 10;
		
		int n1borderLeft = n1Left - margin;
		int n1borderWidth = n1Width + margin * 2;
		int n1borderTop = n1Top - margin/2;
		int n1borderHeight = textHeight + margin * 2;
		
		int n2borderLeft = n2Left - margin;
		int n2borderWidth = n2Width + margin * 2;
		int n2borderTop = n2Top - margin/2;
		int n2borderHeight = textHeight + margin * 2;
		
		int x1,y1,x2,y2;
		
		if(c.side1 == Side.Top) {
			x1 = n1borderLeft + n1borderWidth/2;
			y1 = n1borderTop;
		}else if (c.side1 == Side.Left) {
			x1 = n1borderLeft;
			y1 = n1borderTop + n1borderHeight/2;
		}else if (c.side1 == Side.Right) {
			x1 = n1borderLeft + n1borderWidth;
			y1 = n1borderTop + n1borderHeight/2;
		}else {
			x1 = n1borderLeft + n1borderWidth/2;
			y1 = n1borderTop + n1borderHeight;
		}
		
		if(c.side2 == Side.Top) {
			x2 = n2borderLeft + n2borderWidth/2;
			y2 = n2borderTop;
		}else if (c.side2 == Side.Left) {
			x2 = n2borderLeft;
			y2 = n2borderTop + n2borderHeight/2;
		}else if (c.side2 == Side.Right) {
			x2 = n2borderLeft + n2borderWidth;
			y2 = n2borderTop + n2borderHeight/2;
		}else {
			x2 = n2borderLeft + n2borderWidth/2;
			y2 = n2borderTop + n2borderHeight;
		}
		return new XYPoints(x1, y1, x2, y2);
	}
	
}

class XYPoints {
	public int x1, y1, x2, y2;
	public XYPoints (int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
