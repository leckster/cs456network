/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Phoenix
 */
public class NetworkModelTest {
	
	public NetworkModelTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of getFileName method, of class NetworkModel.
	 */
	@Test
	public void testGetFileName() {
		System.out.println("getFileName");
		NetworkModel instance = new NetworkModel();
		String expResult = "";
		String result = instance.getFileName();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setFileName method, of class NetworkModel.
	 */
	@Test
	public void testSetFileName() {
		System.out.println("setFileName");
		String newFileName = "";
		NetworkModel instance = new NetworkModel();
		instance.setFileName(newFileName);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of save method, of class NetworkModel.
	 */
	@Test
	public void testSave() {
		System.out.println("save");
		NetworkModel instance = new NetworkModel();
		instance.save();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of unsavedChanges method, of class NetworkModel.
	 */
	@Test
	public void testUnsavedChanges() {
		System.out.println("unsavedChanges");
		NetworkModel instance = new NetworkModel();
		boolean expResult = false;
		boolean result = instance.unsavedChanges();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of addNode method, of class NetworkModel.
	 */
	@Test
	public void testAddNode() {
		System.out.println("addNode");
		NetworkNode newNode = null;
		NetworkModel instance = new NetworkModel();
		instance.addNode(newNode);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of nNodes method, of class NetworkModel.
	 */
	@Test
	public void testNNodes() {
		System.out.println("nNodes");
		NetworkModel instance = new NetworkModel();
		int expResult = 0;
		int result = instance.nNodes();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getNode method, of class NetworkModel.
	 */
	@Test
	public void testGetNode() {
		System.out.println("getNode");
		int i = 0;
		NetworkModel instance = new NetworkModel();
		NetworkNode expResult = null;
		NetworkNode result = instance.getNode(i);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getNodeWithName method, of class NetworkModel.
	 */
	@Test
	public void testGetNodeWithName() {
		System.out.println("getNodeWithName");
		String name = "";
		NetworkModel instance = new NetworkModel();
		NetworkNode expResult = null;
		NetworkNode result = instance.getNodeWithName(name);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of removeNode method, of class NetworkModel.
	 */
	@Test
	public void testRemoveNode() {
		System.out.println("removeNode");
		int i = 0;
		NetworkModel instance = new NetworkModel();
		instance.removeNode(i);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of addConnection method, of class NetworkModel.
	 */
	@Test
	public void testAddConnection() {
		System.out.println("addConnection");
		NetworkConnection newConnection = null;
		NetworkModel instance = new NetworkModel();
		instance.addConnection(newConnection);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of nConnections method, of class NetworkModel.
	 */
	@Test
	public void testNConnections() {
		System.out.println("nConnections");
		NetworkModel instance = new NetworkModel();
		int expResult = 0;
		int result = instance.nConnections();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getConnection method, of class NetworkModel.
	 */
	@Test
	public void testGetConnection() {
		System.out.println("getConnection");
		int i = 0;
		NetworkModel instance = new NetworkModel();
		NetworkConnection expResult = null;
		NetworkConnection result = instance.getConnection(i);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of removeConnection method, of class NetworkModel.
	 */
	@Test
	public void testRemoveConnection() {
		System.out.println("removeConnection");
		int i = 0;
		NetworkModel instance = new NetworkModel();
		instance.removeConnection(i);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of Test method, of class NetworkModel.
	 */
	@Test
	public void testTest() {
		System.out.println("Test");
		NetworkModel.Test();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}