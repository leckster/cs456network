/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.HashMap;

public class NetworkModels {

	private static HashMap<String, NetworkModel> currentNetworks = new HashMap<>();

	public static boolean modelExists(String fileName) {
		return currentNetworks.containsKey(fileName);
	}

	public static NetworkModel getModelForKey(String fileName) {
		return currentNetworks.get(fileName);
	}

	public static void addModel(String fileName, NetworkModel model) {
		currentNetworks.put(fileName, model);
	}

	public static void removeModel(String fileName) {
		currentNetworks.remove(fileName);
	}

	public static boolean noMoreModels() {
		return currentNetworks.isEmpty();
	}

	public static void printMap() {
		for (String key : currentNetworks.keySet()) {
			System.out.println(key + " : " + currentNetworks.get(key).toString());
		}
	}
}
