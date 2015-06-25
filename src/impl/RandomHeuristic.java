package impl;

import java.util.List;

import model.State;

public class RandomHeuristic 
{
	private Double bound;
	private int numberOfPermutations;
	private int queueID;
	
	
	public RandomHeuristic(int queueID, Double bound, int numberOfPermutations)
	{
		this.bound = bound;
		this.numberOfPermutations = numberOfPermutations;
	}
	
	private void mergeStates(List<State> listOfStatestoMerge)
	{
		
	}
	
	public static void main(String[] args) {
		
	}

}
