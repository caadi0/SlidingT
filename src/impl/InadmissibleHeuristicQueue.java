package impl;

import impl.AnchorQueue.HeuristicComparator;

import java.util.Comparator;

public class InadmissibleHeuristicQueue {
	
	private final long serialVersionUID = 1L;

	public class HeuristicComparator implements Comparator<Node>{	
		
		int heuristic = 0;

		@Override
		public int compare(Node o1, Node o2) {

				int result = ((Double)(o1.getCost() + Constants.w1* RandomHeuristicGenerator.generateRandomHeuristic(heuristic, o1.getState()) - 
						(o2.getCost() + Constants.w1* RandomHeuristicGenerator.generateRandomHeuristic(heuristic, o2.getState())))).intValue();	
	//			int result = (ManhattanDistance.calculate(o1.getState()) - ( ManhattanDistance.calculate(o2.getState())));	
				
				if (result == 0){
					//Ties among minimal f values are resolved in favor of the deepest node in the search tree
					//i.e. the closest node to the goal
					result =  o2.getCost() - o1.getCost();			
					
				}
				
				return result;
			
		}
	}
	
	protected java.util.PriorityQueue<Node> createQueue() {
		return new java.util.PriorityQueue<Node>(10000, new HeuristicComparator());
	}

}
