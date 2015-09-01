package impl;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import model.Action;
import model.Node;
import model.State;
import model.StateConstants;
import model.SynchronisedNode;

public class HeuristicSolverUtility {
	
	/**
	 * @param node
	 */
	public static void printPath(Node node) {
		if(node.getParent() != null) {
			printPath(node.getParent());
			// Print Action Taken
			System.out.println("Tile "+node.getAction().getMove());
		}
		printState(node.getState());
	}
	
	/**
	 * @param node
	 * @return
	 */
	public static Integer printPathLength(Node node) {
		if(node.getParent() != null) {
			return 1 + printPathLength(node.getParent());
		}
		// Source Node Path Length = 0
		return 0;
	}
	
	/**
	 * @param node
	 * @return
	 */
	public static Integer printPathLength(SynchronisedNode node) {
		if(node.getParent() != null) {
			return 1 + printPathLength(node.getParent());
		}
		// Source Node Path Length = 0
		return 0;
	}
	
	/**
	 * @param q
	 * Prints all heuristic values in a Queue
	 */
	public static void printAllHeuriticValuesInQueue(PriorityQueue<Node> q) {
		Iterator<Node> qIter = q.iterator();
		System.out.print("Heuristic values present in Queue  :   ");
		while(qIter.hasNext()) {
			Node n = qIter.next();
			System.out.print(n.getKey().intValue() + " ");
		}
		System.out.println("");
	}
	
	/**
	 * @param state
	 * Prints state cells
	 */
	synchronized
	public static void printState(State state) {
		int counter = 0;
		byte[] b = state.getAllCells();
		for(byte bt : b) {
			System.out.print(bt +" ");
			if((++counter)%3 == 0)
				System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println(" ");
	}
	
	/**
	 * @param dimension
	 * @return generates random state of dimension * dimension
	 */
	public static State createRandom(int dimension) {
		State s = new State(generateGoalState(dimension).getAllCells());
		Action old = null;
		
		for (int i = 0; i < 50; i++) {
			List<Action> actions = s.getPossibleActions();
			// pick an action randomly
			Random random = new Random();
			int index = random.nextInt(actions.size());
			Action a = actions.get(index);
			if (old != null && old.isInverse(a)){
				if (index == 0){
					index = 1;
				}else{
					index--;
				}
				a = actions.get(index);
			}
			s = a.applyTo(s);
			old = a;
		}
//		StateConstants.stateMap.put(s.hashCode(), s);
		return s;
	}
	
	/**
	 * @param dimension
	 * @return returns goal state of { dimension * dimension }
	 * eg. if dimension = 3 <p>
	 * Goal state : <p> 1 2 3 <p>
	 * 				    4 5 6 <p>
	 * 				    7 8 0 
	 */
	public static State generateGoalState(int dimension){
		
		int nbrOfCells = dimension * dimension;
		byte[] goalCells = new byte[nbrOfCells];
		for (byte i = 1; i < goalCells.length; i++) {
			goalCells[i - 1] = i;
		}
		goalCells[nbrOfCells - 1] = 0;
		
		State goalState = new State(goalCells);
//		StateConstants.stateMap.put(goalState.hashCode(), goalState);
		return goalState;
	}
	
	public static Boolean isStateSolvable(State s)
	{
		int parity = 0;
		byte[] arr = s.getAllCells();
		for(int i = 0; i<arr.length;i++)
		{
			for(int j=i+1;j<arr.length;j++)
			{
				if(arr[i]>arr[j])
					parity++;
			}
		}
		if(parity%2 == 0)
			return true;
		return false;
	}
	
}
