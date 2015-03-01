package queues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import model.Node;

public class ExpandedQueue extends LinkedList<Node> {

	private static final long serialVersionUID = 1774547694456622409L;

	public static HashMap<Integer, ArrayList<Node>> listOfExpandedNodesForAQueue = new HashMap<Integer, ArrayList<Node>>();;

	/**
	 * Inserts into expanded queue for heuristic id = queueNumber, or creates a
	 * new list if doesn't exist already
	 * @param queueNumber
	 *            - Also Heuristic Number
	 * @return gets list of Already Expanded Nodes for Queue Number (Also
	 *         Heuristic Number)
	 */
	public static ArrayList<Node> getListOfNodesFromExpandedQueue(
			Integer queueNumber) {
		ArrayList<Node> listOfNodes = listOfExpandedNodesForAQueue
				.get(queueNumber);
		if (listOfNodes == null) {
			listOfNodes = new ArrayList<Node>();
			listOfExpandedNodesForAQueue.put(queueNumber, listOfNodes);
		}
		return listOfNodes;
	}

	/**
	 * @param queueNumber
	 *            - Also Heuristic Number
	 * @param node
	 */
	public static void insertIntoExpandedQueue(Integer queueNumber, Node node) {
		ArrayList<Node> linkedList = getListOfNodesFromExpandedQueue(queueNumber);
		linkedList.add(node);
	}

	/**
	 * @param queueNumber
	 * @param node
	 * @return true or false depending on whether element is contained in the Queue
	 */
	public static Boolean doesExpandedQueueContainNode(Integer queueNumber,
			Node node) {
		ArrayList<Node> linkedList = getListOfNodesFromExpandedQueue(queueNumber);
		if(linkedList == null)
			return false;
		if (linkedList.contains(node))
			return true;
		
		return false;
	}

}
