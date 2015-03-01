package queues;

import impl.HeuristicSolverUtility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import model.Node;

public class PQueue extends java.util.PriorityQueue<Node> {

	private static final long serialVersionUID = 1L;

	public static HashMap<Integer, PriorityQueue<Node>> priorityQueueMap = new HashMap<Integer, PriorityQueue<Node>>();

	public static class HeuristicComparator implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {
			Double result;

			result = o1.getKey() - o2.getKey();
			if (result == 0) {
				// Ties among minimal f values are resolved in favor of the
				// deepest node in the search tree
				// i.e. the closest node to the goal
				result = (double) (o2.getCost() - o1.getCost());
			}
			if (result > 0.0)
				return 1;

			return -1;
		}
	}

	public static java.util.PriorityQueue<Node> createQueue() {
		return new java.util.PriorityQueue<Node>(10000,
				new HeuristicComparator());
	}

	public static java.util.PriorityQueue<Node> getQueueForIndex(
			Integer index) {
		PriorityQueue<Node> pq = priorityQueueMap.get(index);
		if (pq == null) {
			pq = createQueue();
			priorityQueueMap.put(index, pq);
		}
		return pq;
	}

	public static Node getStateWithSameArrangementFromQueue(Node node,
			Integer index) throws Exception {
		PriorityQueue<Node> q = getQueueForIndex(index);
		Iterator<Node> iter = q.iterator();
		while (iter.hasNext()) {
			Node n = iter.next();
			if (n.equals(node))
				return n;
		}
		throw new Exception("Element not matched exception");
	}
	
	public static Node getGoalStateFromQueue(
			Integer index) throws Exception {
		
		PriorityQueue<Node> q = getQueueForIndex(index);
		Iterator<Node> iter = q.iterator();
		while (iter.hasNext()) {
			Node n = iter.next();
			if (n.getState().equals(HeuristicSolverUtility.generateGoalState(3)))
				return n;
		}
		throw new Exception("Element not matched exception");
	}

}
