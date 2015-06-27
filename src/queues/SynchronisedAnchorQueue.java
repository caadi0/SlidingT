package queues;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import algorithms.ManhattanDistance;
import model.AtomicNode;
import constants.Constants;

public class SynchronisedAnchorQueue extends PriorityBlockingQueue<AtomicNode>{
	
	private static final long serialVersionUID = 1L;

	public static class HeuristicComparator implements Comparator<AtomicNode>{

		@Override
		public int compare(AtomicNode o1, AtomicNode o2) {

				int result = ((Double)(o1.getCost() + Constants.w1* ManhattanDistance.calculate(o1.getState()) - 
						(o2.getCost() + Constants.w1* ManhattanDistance.calculate(o2.getState())))).intValue();	
	//			int result = (ManhattanDistance.calculate(o1.getState()) - ( ManhattanDistance.calculate(o2.getState())));	
				
				if (result == 0){
					//Ties among minimal f values are resolved in favor of the deepest node in the search tree
					//i.e. the closest node to the goal
					result =  o2.getCost() - o1.getCost();			
					
				}
				
				return result;

		}
	}
	
	public static PriorityBlockingQueue<AtomicNode> createQueue() {
		return new PriorityBlockingQueue<AtomicNode>(10000, new HeuristicComparator());
	}

}
