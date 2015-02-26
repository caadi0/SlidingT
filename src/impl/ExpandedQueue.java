package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ExpandedQueue extends LinkedList<Node> {
	
	private static final long serialVersionUID = 1774547694456622409L;
	private static HashMap<Integer, ArrayList<Node>> listOfExpandedNodesForAQueue = new HashMap<Integer, ArrayList<Node>>();
//	private static List<LinkedList<Node>> listOfExpandedNodesForAQueue = new LinkedList<LinkedList<Node>>(Collection<E>);
	
	public static ArrayList<Node> getListForQueue(Integer queueIndex) {
		ArrayList<Node> linkedList = listOfExpandedNodesForAQueue.get(queueIndex);
		if(linkedList == null) {
			linkedList = new ArrayList<Node>();
			listOfExpandedNodesForAQueue.put(queueIndex, linkedList);
		}
		return linkedList;
	}
	
	public static void insertIntoQueue(Integer index , Node n) {
		ArrayList<Node> linkedList = getListForQueue(index);
		linkedList.add(n);
	}
	
	public static Boolean contains(Integer index, Node n) {
		ArrayList<Node> linkedList = getListForQueue(index);
		if(linkedList.contains(n))
			return true;
		return false;					
	}
	

}
