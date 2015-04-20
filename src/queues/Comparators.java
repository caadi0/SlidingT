package queues;

import impl.RandomHeuristicGenerator;

import java.util.Comparator;

import model.Node;
import model.SynchronisedNode;
import algorithms.ManhattanDistance;
import constants.Constants;

public class Comparators {

	public static class AnchorHeuristicComparator implements Comparator<SynchronisedNode>{

		@Override
		public int compare(SynchronisedNode o1, SynchronisedNode o2) {

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
	
	public static class InadmissibleHeuristicComparator implements Comparator<SynchronisedNode> {	
		
		int heuristic = 0;
		
		public InadmissibleHeuristicComparator(int heuristic)
		{
			this.heuristic = heuristic;
		}

		@Override
		public int compare(SynchronisedNode o1, SynchronisedNode o2) {

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
		
		public void setHeuristic(int h)
		{
			heuristic = h;
		}
	}
	
}
