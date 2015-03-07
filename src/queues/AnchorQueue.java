package queues;

import java.util.Comparator;

import algorithms.ManhattanDistance;
import model.Node;
import constants.Constants;

public class AnchorQueue extends java.util.PriorityQueue<Node>{
	
	private static final long serialVersionUID = 1L;

	public static class HeuristicComparator implements Comparator<Node>{

		@Override
		public int compare(Node o1, Node o2) {

				int result = ((Double)(o1.getCost() + Constants.w1* ManhattanDistance.calculate(o1.getState()) - 
						(o2.getCost() + Constants.w1* ManhattanDistance.calculate(o2.getState())))).intValue();	
	//			int result = (ManhattanDistance.calculate(o1.getState()) - ( ManhattanDistance.calculate(o2.getState())));	
				
				if (result == 0){
					//Ties among minimal f values are resolved in favor of the deepest node in the search tree
					//i.e. the closest node to the goal
					if(o2.getCost() - o1.getCost() > 0)
						return 1;
					else 
						return -1;					
				}
				
				return result;

		}
	}
	
	public static java.util.PriorityQueue<Node> createQueue() {
		return new java.util.PriorityQueue<Node>(10000, new HeuristicComparator());
	}

}
