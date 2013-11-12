/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.HashMap;

public class NetworkModels {

	private static HashMap<String, NetworkModel> currentNetworks = new HashMap<>();
	private static HashMap<String, Integer> numberOfNetworks = new HashMap<>();

	public static boolean modelExists(String fileName) {
		return currentNetworks.containsKey(fileName);
	}

	public static NetworkModel getModelForKey(String fileName) {
		return currentNetworks.get(fileName);
	}

	public static void addModel(String fileName, NetworkModel model) {
		currentNetworks.put(fileName, model);
		Integer count = numberOfNetworks.get(fileName);
		if (count != null) {
			count += 1;
		} else {
			count = 1;
		}
		numberOfNetworks.put(fileName, count);
	}

	public static void removeModel(String fileName) {
		currentNetworks.remove(fileName);
		Integer count = numberOfNetworks.get(fileName);
		if (count != null && count > 0) {
			count -= 1;
		} else {
			count = 0;
		}
		numberOfNetworks.put(fileName, count);
	}
	
	public static int getCoutForKey(String fileName) {
		Integer count = numberOfNetworks.get(fileName);
		if (count != null) {
			return count;
		} else {
			return 0;
		}
	}

	public static boolean noMoreModels() {
		return currentNetworks.isEmpty();
	}
}
