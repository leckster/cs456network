/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import network.NetworkConnection.Side;

/**
 *
 * @author Leckie Gunter
 */
public class NetworkView extends JPanel implements NetworkViewInterface {

	public enum Mode {

		Select,
		AddNode,
		AddConnection
	}
	private NetworkModel network;
	private NetworkNode selectedNode;
	private NetworkConnection selectedConnection;
	private int index;
	private int textLocation;
	private Point mousePressLocation;
	private Point offsetPoint;
	private NetworkNode connectionFirstNode;
	private Side connectionFirstSide;
	private Point dragToLocation;
	private Side dragToSide = Side.Left;
	private int xOffset;
	private int yOffset;
	private Graphics graphics;
	private Mode mode;

	public NetworkView(NetworkModel network) {

		this.network = network;
		this.selectedNode = null;
		this.selectedConnection = null;
		this.index = 0;
		this.textLocation = -1;
		this.graphics = null;

		this.network.addNetworkViewListener(this);

		setFocusable(true);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	public void changeNetwork(NetworkModel network) {
		this.network.removeNetworkViewListener(this);
		this.network = network;
		this.network.addNetworkViewListener(this);
	}

	void setMode(Mode mode) {
		if (this.mode != mode) {
			System.out.println("Mode changed to: " + mode);
			this.mode = mode;
			this.selectedNode = null;
			this.selectedConnection = null;
			this.repaint();
		}
	}

	@Override
	public void updateView() {
		this.repaint();
	}

	@Override
	public void processKeyEvent(KeyEvent evnt) {

		if (evnt.getID() == KeyEvent.KEY_PRESSED && this.mode == Mode.Select) {
			if (textLocation != -1) {
				if (evnt.getKeyCode() == 8) {//delete backspaces
					deleteCharacter();
				} else if (evnt.getKeyCode() <= 91 && evnt.getKeyCode() >= 65 || evnt.getKeyCode() == 32) { //'a' through 'z' and 'spacebar'
					insertCharacter(evnt.getKeyChar());
				}

				if (evnt.getKeyCode() == 37) { // Left Arrow
					if (textLocation > 0) {
						textLocation--;
					}
					this.repaint();
				}
				if (evnt.getKeyCode() == 39) { // Right Arrow
					if (textLocation < this.selectedNode.getName().length()) {
						textLocation++;
					}
					this.repaint();
				}
			}
		}
	}

	private void deleteCharacter() {
		try {
			if (this.textLocation > 0) {
				String nodeName = this.network.getNode(this.index).getName();
				String newNodeName = new StringBuilder(nodeName).deleteCharAt(this.textLocation - 1).toString();
				for (int i = 0; i < this.network.nConnections(); i++) {
					NetworkConnection conn = this.network.getConnection(i);
					if (conn.getNodeOne().equals(nodeName)) {
						conn.setNodeOne(newNodeName);
					}
					if (conn.getNodeTwo().equals(nodeName)) {
						conn.setNodeTwo(newNodeName);
					}
				}
				this.network.getNode(index).setName(newNodeName);
				this.textLocation--;
			}
		} catch (Exception ex) {
			Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void insertCharacter(char character) {
		try {
			String nodeName = this.network.getNode(this.index).getName();
			String newNodeName = new StringBuilder(nodeName).insert(this.textLocation, character).toString();
			for (int i = 0; i < this.network.nConnections(); i++) {
				NetworkConnection conn = this.network.getConnection(i);
				if (conn.getNodeOne().equals(nodeName)) {
					conn.setNodeOne(newNodeName);
				}
				if (conn.getNodeTwo().equals(nodeName)) {
					conn.setNodeTwo(newNodeName);
				}
			}
			this.network.getNode(index).setName(newNodeName);
			this.textLocation++;

		} catch (Exception ex) {
			Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void processMouseMotionEvent(MouseEvent evnt) {
		if (evnt.getID() == MouseEvent.MOUSE_DRAGGED) {
			if (this.mode == Mode.Select) {
				//if there is a selected node. Update location to new loc
				if (this.selectedNode != null) {
					if (this.mousePressLocation.distance(evnt.getPoint()) > 3) {
						if (this.offsetPoint == null) {
							this.offsetPoint = evnt.getPoint();
							this.xOffset = (int) this.selectedNode.getX() - this.offsetPoint.x;
							this.yOffset = (int) this.selectedNode.getY() - this.offsetPoint.y;

						}
						Point mouseLoc = evnt.getPoint();
						this.network.setNewNodeLocation(this.index, mouseLoc.x + xOffset, mouseLoc.y + yOffset);
					}
				}
			} else if (this.mode == Mode.AddConnection) {
				this.dragToLocation = evnt.getPoint();
				Point mouseLoc = evnt.getPoint();
				for (int i = this.network.nNodes() - 1; i >= 0; i--) {
					NetworkNode node = this.network.getNode(i);
					if (isCloseEnoughToNode(node, mouseLoc)) {
						Side side = getConnectionSide(node, mouseLoc);
						if (side != null) {
							System.out.println("Setting New Drag To Side: " + side.name());
							this.dragToSide = side;
							break;
						}
					}
				}
				this.repaint();
				//TODO. add logic for when dragging while adding a connection
			}
		}
	}

	@Override
	public void processMouseEvent(MouseEvent evnt) {
		if (this.mode == Mode.Select) {
			if (evnt.getID() == MouseEvent.MOUSE_PRESSED) {
				this.mousePressLocation = evnt.getPoint();
				Point mouseLoc = evnt.getPoint();
				GeometryDescriptor gd = pointGeometry(mouseLoc);

				System.out.println(gd.toString());
				if (gd.node != null) {
					this.textLocation = gd.textLocation;
					this.index = gd.index;
					this.selectedNode = gd.node;
					this.selectedConnection = null;
				} else if (gd.connection != null) {
					this.index = gd.index;
					this.selectedConnection = gd.connection;
					this.selectedNode = null;
				} else {
					this.selectedConnection = null;
					this.selectedNode = null;
				}
				this.repaint();
			}

			if (evnt.getID() == MouseEvent.MOUSE_RELEASED) {
				this.offsetPoint = null;
			}
		} else if (this.mode == Mode.AddNode) {
			if (evnt.getID() == MouseEvent.MOUSE_PRESSED) {
				Point mouseLoc = evnt.getPoint();
				NetworkNode newNode = new NetworkNode("New Node", mouseLoc.x, mouseLoc.y);
				newNode.setNetwork(this.network);
				this.network.addNode(newNode);
			}
		} else if (this.mode == Mode.AddConnection) {
			if (evnt.getID() == MouseEvent.MOUSE_PRESSED) {
				this.mousePressLocation = evnt.getPoint();
				Point mouseLoc = evnt.getPoint();
				for (int i = this.network.nNodes() - 1; i >= 0; i--) {
					NetworkNode node = this.network.getNode(i);
					if (isCloseEnoughToNode(node, mouseLoc)) {
						Side side = getConnectionSide(node, mouseLoc);
						if (side != null) {
							System.out.println("Setting New Connection First Node: " + node.getName() + " - " + side.name());
							this.connectionFirstNode = node;
							this.connectionFirstSide = side;
							this.mousePressLocation = getNodeSidePoint(node, side);
							return;
						}
					}
				}
				this.connectionFirstNode = null;
				this.connectionFirstSide = null;
				this.mousePressLocation = null;
			} else if (evnt.getID() == MouseEvent.MOUSE_RELEASED) {
				this.dragToLocation = null;
				this.dragToSide = Side.Left;
				if (this.mousePressLocation != null) {
					System.out.println("Node: " + this.connectionFirstNode.getName() + " - " + this.connectionFirstSide.name());
					Point mouseLoc = evnt.getPoint();
					for (int i = this.network.nNodes() - 1; i >= 0; i--) {
						NetworkNode node = this.network.getNode(i);
						if (isCloseEnoughToNode(node, mouseLoc)) {
							Side side = getConnectionSide(node, mouseLoc);
							if (side != null) {
								this.network.addConnection(new NetworkConnection(this.connectionFirstNode.getName(), this.connectionFirstSide, node.getName(), side));
								return;
							}
						}
					}
				}
				this.mousePressLocation = null;
			}
		}

	}
	/*
	 * Returns a the side of the node that this mouse location is closest to if it is within 15 pixels of the center of the side
	 * Returns null if the point is not near any of the four sides of this node.
	 */

	private Side getConnectionSide(NetworkNode n, Point mouseLoc) {

		Point top = getNodeSidePoint(n, Side.Top);
		if (mouseLoc.distance(top) < 15) {
			return Side.Top;
		}
		Point left = getNodeSidePoint(n, Side.Left);
		if (mouseLoc.distance(left) < 15) {
			return Side.Left;
		}
		Point right = getNodeSidePoint(n, Side.Right);
		if (mouseLoc.distance(right) < 15) {
			return Side.Right;
		}
		Point bottom = getNodeSidePoint(n, Side.Bottom);
		if (mouseLoc.distance(bottom) < 15) {
			return Side.Bottom;
		}
		return null;
	}

	/*
	 * Returns the point representing the center of the given side for a given node
	 */
	private Point getNodeSidePoint(NetworkNode n, Side side) {
		System.out.println("getNodeSidePoint: " + n.getName() + " -- " + side.name());
		Graphics g = this.graphics;
		FontMetrics FM = g.getFontMetrics();
		int width = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();

		int n1Left = (int) n.getX() - width / 2;
		int n1Top = (int) n.getY() - FM.getHeight() / 2;

		int margin = 10;

		int n1borderLeft = n1Left - margin;
		int n1borderWidth = width + margin * 2;
		int n1borderTop = n1Top - margin / 2;
		int n1borderHeight = textHeight + margin * 2;

		int x1, y1;

		if (side == Side.Top) {
			x1 = n1borderLeft + n1borderWidth / 2;
			y1 = n1borderTop;
		} else if (side == Side.Left) {
			x1 = n1borderLeft;
			y1 = n1borderTop + n1borderHeight / 2;
		} else if (side == Side.Right) {
			x1 = n1borderLeft + n1borderWidth;
			y1 = n1borderTop + n1borderHeight / 2;
		} else {
			x1 = n1borderLeft + n1borderWidth / 2;
			y1 = n1borderTop + n1borderHeight;
		}
		return new Point(x1, y1);
	}

	private boolean isCloseEnoughToNode(NetworkNode n, Point mouseLoc) {
		int xMax, xMin, yMax, yMin;

		Graphics g = this.graphics;

		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int) n.getX() - textWidth / 2;
		int textTop = (int) n.getY() - FM.getHeight() / 2;

		int margin = 10;

		xMin = textLeft - margin - 15;
		xMax = xMin + (textWidth + (margin * 2)) + 30;
		yMin = textTop - margin / 2 - 15;
		yMax = yMin + (textHeight + (margin * 2)) + 30;

		//Check bounding box
		if (mouseLoc.getX() < xMin || mouseLoc.getX() > xMax || mouseLoc.getY() < yMin || mouseLoc.getY() > yMax) {
			return false;
		}
		return true;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		this.graphics = g;

		for (int i = 0; i < this.network.nNodes(); i++) {
			try {
				if (this.selectedNode != null && this.index == i) {
					paintNode(g, this.network.getNode(i), true, this.textLocation);
				} else {
					paintNode(g, this.network.getNode(i), false, -1);
				}
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for (int i = 0; i < this.network.nConnections(); i++) {
			try {
				if (this.selectedConnection != null && this.index == i) {
					paintConnection(g, this.network.getConnection(i), true);
				} else {
					paintConnection(g, this.network.getConnection(i), false);
				}
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if (this.dragToLocation != null && connectionFirstNode != null) {
			System.out.println("Drawing new drag location");
			paintNewConnection(g);
		}

	}

	public GeometryDescriptor pointGeometry(Point mouseLoc) {
		GeometryDescriptor objectClicked = new GeometryDescriptor();

		for (int i = this.network.nConnections() - 1; i >= 0; i--) {
			try {
				NetworkConnection c = this.network.getConnection(i);
				if (containsPoint(c, mouseLoc)) {
					objectClicked = new GeometryDescriptor(c, i);
					return objectClicked;
				}
			} catch (Exception ex) {
				Logger.getLogger(NetworkView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for (int i = this.network.nNodes() - 1; i >= 0; i--) {
			try {
				NetworkNode n = this.network.getNode(i);
				if (containsPoint(n, mouseLoc)) {
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

	private int min(int[] array) {
		int min = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
			}
		}
		return min;
	}

	private int max(int[] array) {
		int max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}

	private boolean containsPoint(NetworkConnection c, Point mouseLoc) {
		int xMax, xMin, yMax, yMin;
		Graphics g = this.graphics;

		XYPoints p = getXYPointsFromConnection(g, c);

		int c1x = getBezierX(p.x1, c.side1);
		int c1y = getBezierY(p.y1, c.side1);
		int c2x = getBezierX(p.x2, c.side2);
		int c2y = getBezierY(p.y2, c.side2);

		Point c1 = new Point(c1x, c1y);
		Point c2 = new Point(c2x, c2y);

		int[] xPoints = {p.x1, p.x2, c1x, c2x};
		int[] yPoints = {p.y1, p.y2, c1y, c2y};

		xMax = max(xPoints);
		xMin = min(xPoints);

		yMax = max(yPoints);
		yMin = min(yPoints);

//		System.out.println("X-min: " + xMin);
//		System.out.println("X-max: " + xMax);
//		System.out.println("Y-min: " + yMin);
//		System.out.println("Y-max: " + yMax);

		//Check bounding box
		if (mouseLoc.getX() < xMin || mouseLoc.getX() > xMax || mouseLoc.getY() < yMin || mouseLoc.getY() > yMax) {
			return false;
		}

		//TODO. Change this to be dist to Bezier curve
		//second check closest point
		if (closeToCurve(p, c1, c2, mouseLoc)) {
			return true;
		}
		return false;

//		if (distToSegment(mouseLoc, new Point(p.x1, p.y1), new Point(p.x2, p.y2)) > 5) {
//			return false;
//		}
//
//		return true;
	}

	private boolean closeToCurve(XYPoints p, Point c1, Point c2, Point mouseLoc) {

		Point anchor1 = new Point(p.x1, p.y1);
		Point anchor2 = new Point(p.x2, p.y2);
		for (float u = 0; u < 1; u += .01) {
			double posx = Math.pow(u, 3) * (anchor2.x + 3 * (c1.x - c2.x) - anchor1.x)
					+ 3 * Math.pow(u, 2) * (anchor1.x - 2 * c1.x + c2.x)
					+ 3 * u * (c1.x - anchor1.x) + anchor1.x;

			double posy = Math.pow(u, 3) * (anchor2.y + 3 * (c1.y - c2.y) - anchor1.y)
					+ 3 * Math.pow(u, 2) * (anchor1.y - 2 * c1.y + c2.y)
					+ 3 * u * (c1.y - anchor1.y) + anchor1.y;

			if (mouseLoc.distance(posx, posy) < 3) {
				return true;
			}
		}
		return false;
	}

	private float sqr(float x) {
		return x * x;
	}

	private float dist2(Point v, Point w) {
		return sqr(v.x - w.x) + sqr(v.y - w.y);
	}

	private float distToSegmentSquared(Point p, Point v, Point w) {
		float l2 = dist2(v, w);
		if (l2 == 0) {
			return dist2(p, v);
		}
		float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;

		if (t < 0) {
			return dist2(p, v);
		}
		if (t > 1) {
			return dist2(p, w);
		}
		return dist2(p, new Point((int) (v.x + t * (w.x - v.x)), (int) (v.y + t * (w.y - v.y))));
	}

	private int distToSegment(Point p, Point v, Point w) {
		return (int) Math.sqrt(distToSegmentSquared(p, v, w));
	}

	private int getTextLocation(NetworkNode n, Point mouseLoc) {

		Graphics g = this.graphics;

		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textLeft = (int) n.getX() - textWidth / 2;
		int textRight = (int) n.getX() + textWidth / 2;

		int textBase = (int) n.getY() + FM.getHeight() / 2;
		int textTop = (int) n.getY() - FM.getHeight() / 2;

		//test bounding box of text
		if (mouseLoc.getX() < textLeft || mouseLoc.getX() > textRight || mouseLoc.getY() < textTop || mouseLoc.getY() > textBase) {
			return -1;
		}

		//find character index
		int xOffset = mouseLoc.x - textLeft;
		int totalCharWidth = 0;
		for (int i = 0; i < n.getName().length(); i++) {
			totalCharWidth += FM.charWidth(n.getName().charAt(i));
			if (totalCharWidth > xOffset) {
				return i;
			}
		}

		return -1;
	}

	private boolean containsPoint(NetworkNode n, Point mouseLoc) {
		int xMax, xMin, yMax, yMin;

		Graphics g = this.graphics;

		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int) n.getX() - textWidth / 2;
		int textTop = (int) n.getY() - FM.getHeight() / 2;

		int margin = 10;

		xMin = textLeft - margin;
		xMax = xMin + (textWidth + (margin * 2));
		yMin = textTop - margin / 2;
		yMax = yMin + (textHeight + (margin * 2));

		//Check bounding box
		if (mouseLoc.getX() < xMin || mouseLoc.getX() > xMax || mouseLoc.getY() < yMin || mouseLoc.getY() > yMax) {
			return false;
		}

		//Check if inside actual oval
		Point center = new Point(xMin + textWidth / 2 + margin, yMin + textHeight / 2 + margin);

		double _xRadius = textWidth / 2 + margin;
		double _yRadius = textHeight / 2 + margin;

		if (_xRadius <= 0.0 || _yRadius <= 0.0) {
			return false;
		}
		Point normalized = new Point((int) (mouseLoc.getX() - center.getX()), (int) (mouseLoc.getY() - center.getY()));

		if (((double) (normalized.getX() * normalized.getX() / (_xRadius * _xRadius))
				+ ((double) (normalized.getY() * normalized.getY()) / (_yRadius * _yRadius))) > 1.0) {
			return false;
		}

		//Check string location

		return true;
	}

	private void paintNode(Graphics g, NetworkNode n, boolean highlighted, int textLocation) {

		if (highlighted) {
			g.setColor(Color.blue);
		} else {
			g.setColor(Color.black);
		}

		FontMetrics FM = g.getFontMetrics();
		int textWidth = FM.stringWidth(n.getName());
		int textHeight = FM.getHeight();
		int textLeft = (int) n.getX() - textWidth / 2;

		int textBase = (int) n.getY() + FM.getHeight() / 2;
		int textTop = (int) n.getY() - FM.getHeight() / 2;

		g.drawString(n.getName(), textLeft, textBase);
		if (textLocation != -1) {
			drawCourser(n.getName(), textLeft, textBase, textLocation);
		}
		int margin = 10;

		int borderLeft = textLeft - margin;
		int borderWidth = textWidth + margin * 2;
		int borderTop = textTop - margin / 2;
		int borderHeight = textHeight + margin * 2;

		g.drawOval(borderLeft, borderTop, borderWidth, borderHeight);
	}

	private void drawCourser(String nodeName, int textLeft, int textBase, int textLocation) {
		Graphics g = this.graphics;
		g.setColor(Color.black);

		FontMetrics FM = g.getFontMetrics();
		textBase = textBase + 3;
		//find character index
		int xOffset = 0;
		for (int i = 0; i <= nodeName.length(); i++) {
			if (textLocation == i) {
				//draw bar at xOffset
				int x = textLeft + xOffset;
				int yMin = textBase - FM.getHeight();
				g.drawLine(x, yMin, x, textBase);
				break;
			}
			xOffset += FM.charWidth(nodeName.charAt(i));

		}
		g.setColor(Color.blue);
	}

	private void paintNewConnection(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.blue);

		int x1 = this.mousePressLocation.x;
		int y1 = this.mousePressLocation.y;
		int x2 = this.dragToLocation.x;
		int y2 = this.dragToLocation.y;

		int c1x = getBezierX(x1, this.connectionFirstSide);
		int c1y = getBezierY(y1, this.connectionFirstSide);
		int c2x = getBezierX(x2, this.dragToSide);
		int c2y = getBezierY(y2, this.dragToSide);

		Path2D path = new GeneralPath();
		path.moveTo(x1, y1);
		path.curveTo(c1x, c1y, c2x, c2y, x2, y2);

		g2d.draw(path);
	}

	private void paintConnection(Graphics g, NetworkConnection c, boolean highlighted) {
		Graphics2D g2d = (Graphics2D) g;
		if (highlighted) {
			g.setColor(Color.blue);
		} else {
			g.setColor(Color.black);
		}

		XYPoints p = getXYPointsFromConnection(g, c);

		int c1x = getBezierX(p.x1, c.side1);
		int c1y = getBezierY(p.y1, c.side1);
		int c2x = getBezierX(p.x2, c.side2);
		int c2y = getBezierY(p.y2, c.side2);

		Path2D path = new GeneralPath();
		path.moveTo(p.x1, p.y1);
		path.curveTo(c1x, c1y, c2x, c2y, p.x2, p.y2);

		g2d.draw(path);
		//g.drawLine(p.x1, p.y1, p.x2, p.y2);
	}

	private int getBezierX(int x, Side side) {
		switch (side) {
			case Right:
				x += 100;
				break;
			case Left:
				x -= 100;
				break;
			default:
				break;
		}
		return x;
	}

	private int getBezierY(int y, Side side) {
		switch (side) {
			case Top:
				y -= 100;
				break;
			case Bottom:
				y += 100;
				break;
			default:
				break;
		}
		return y;
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

		int n1Left = (int) n1.getX() - n1Width / 2;
		int n2Left = (int) n2.getX() - n2Width / 2;

		int n1Top = (int) n1.getY() - FM.getHeight() / 2;
		int n2Top = (int) n2.getY() - FM.getHeight() / 2;

		int margin = 10;

		int n1borderLeft = n1Left - margin;
		int n1borderWidth = n1Width + margin * 2;
		int n1borderTop = n1Top - margin / 2;
		int n1borderHeight = textHeight + margin * 2;

		int n2borderLeft = n2Left - margin;
		int n2borderWidth = n2Width + margin * 2;
		int n2borderTop = n2Top - margin / 2;
		int n2borderHeight = textHeight + margin * 2;

		int x1, y1, x2, y2;

		if (c.side1 == Side.Top) {
			x1 = n1borderLeft + n1borderWidth / 2;
			y1 = n1borderTop;
		} else if (c.side1 == Side.Left) {
			x1 = n1borderLeft;
			y1 = n1borderTop + n1borderHeight / 2;
		} else if (c.side1 == Side.Right) {
			x1 = n1borderLeft + n1borderWidth;
			y1 = n1borderTop + n1borderHeight / 2;
		} else {
			x1 = n1borderLeft + n1borderWidth / 2;
			y1 = n1borderTop + n1borderHeight;
		}

		if (c.side2 == Side.Top) {
			x2 = n2borderLeft + n2borderWidth / 2;
			y2 = n2borderTop;
		} else if (c.side2 == Side.Left) {
			x2 = n2borderLeft;
			y2 = n2borderTop + n2borderHeight / 2;
		} else if (c.side2 == Side.Right) {
			x2 = n2borderLeft + n2borderWidth;
			y2 = n2borderTop + n2borderHeight / 2;
		} else {
			x2 = n2borderLeft + n2borderWidth / 2;
			y2 = n2borderTop + n2borderHeight;
		}
		return new XYPoints(x1, y1, x2, y2);
	}
}

class XYPoints {

	public int x1, y1, x2, y2;

	public XYPoints(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
