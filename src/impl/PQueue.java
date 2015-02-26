package impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class PQueue extends java.util.PriorityQueue<Node>{
	
	private static final long serialVersionUID = 1L;
	
	private static HashMap<Integer, PriorityQueue<Node>> priorityQueueMap = new HashMap<Integer, PriorityQueue<Node>>();

	public static class HeuristicComparator implements Comparator<Node>{

		@Override
		public int compare(Node o1, Node o2) {
			int result = (o1.getCost() + ManhattanDistance.calculate(o1.getState()) - (o2.getCost() + ManhattanDistance.calculate(o2.getState())));	
//			int result = (ManhattanDistance.calculate(o1.getState()) - ( ManhattanDistance.calculate(o2.getState())));	
			
			if (result == 0){
				//Ties among minimal f values are resolved in favor of the deepest node in the search tree
				//i.e. the closest node to the goal
				result =  o2.getCost() - o1.getCost();			
				
			}
			
			return result;
			
		}
	}
	
	protected static java.util.PriorityQueue<Node> createQueue() {
		return new java.util.PriorityQueue<Node>(10000, new HeuristicComparator());
	}
	
	protected static java.util.PriorityQueue<Node> getQueueForIndex(Integer index) {
		PriorityQueue<Node> pq = priorityQueueMap.get(index);
		if(pq == null) {
			pq = createQueue();
			priorityQueueMap.put(index, pq);
		}
		return pq;
	}
	
	protected static Node getStateWithSameArrangementFromQueue(Node node, Integer index) throws Exception {
		PriorityQueue<Node> q = getQueueForIndex(index);
		Iterator<Node> iter = q.iterator();
		while(iter.hasNext()) {
			Node n = iter.next();
			if(n.equals(node))
				return n;
		}
		throw new Exception("Element not matched exception");
	}

}
