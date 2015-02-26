package impl;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class IMHA {
	
	/*
	 * Index = heuristic Number
	 * Value = goal state cost
	 */
	private static HashMap<Integer, Integer> goalCostForHeuristic = new HashMap<Integer, Integer>();
	
	private static Integer getGoalCostForIndex(Integer index) {
		return goalCostForHeuristic.get(index);
	}
	
	private static void setGoalCostForIndex(Integer index, Integer cost) {
		goalCostForHeuristic.put(index, cost);
	}
	
	private static Double getKey(Node n, Integer i) {
		Double heuristicCost = 0.0;
		if(i == 0) {
			heuristicCost = Double.valueOf(ManhattanDistance.calculate(n.getState())+"");
		} else {
			heuristicCost = RandomHeuristicGenerator.generateRandomHeuristic(i, n.getState());
		}
		return n.getCost() + heuristicCost;
	}
	
	public static void IMHAStar() throws Exception {
		// testing code for 10 heuristics 9 - inadmissible and 1 admissible
		int heuristicCount = 2;
		State randomState = HeuristicSolver.createRandom(3);
		Node initialNode = new Node(randomState);
		HeuristicSolver.printPath(initialNode);
		for(int i = 0; i < heuristicCount ; i++) {
			PriorityQueue<Node> pq = PQueue.getQueueForIndex(i);
			pq.add(initialNode);
			goalCostForHeuristic.put(i, Integer.MAX_VALUE);
		}
		Boolean breakFromWhileLoop = false;
		while(!PQueue.getQueueForIndex(0).isEmpty() && breakFromWhileLoop == false) {
			for(Integer i = 1 ; i <heuristicCount ; i++) {
				if(getKey(PQueue.getQueueForIndex(i).peek() , i) <= Contants.w2 * getKey(PQueue.getQueueForIndex(0).peek() , 0)) {
					if(getGoalCostForIndex(i) <= getKey(PQueue.getQueueForIndex(0).peek() , 0)) {
						HeuristicSolver.printPath(PQueue.getStateWithSameArrangementFromQueue(getGoalNode(), i));
						breakFromWhileLoop = true;
						break;
					}
					Node openN = PQueue.getQueueForIndex(i).peek();
					expand(openN, i);
				} else {
					if(getGoalCostForIndex(0) <= getKey(PQueue.getQueueForIndex(0).peek() , 0)) {
						HeuristicSolver.printPath(PQueue.getStateWithSameArrangementFromQueue(getGoalNode(), i));
						breakFromWhileLoop = true;
						break;
					}
					Node n = PQueue.getQueueForIndex(0).peek();
					expand(n, i);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Aaditya\\Desktop\\output.txt"));
		System.setOut(out);
		IMHA.IMHAStar();
	}
	
	public static Node getGoalNode() {
		return new Node(HeuristicSolver.createGoalState(3));
	}
	
	public static void expand(Node n, Integer i) {
		
		PQueue.getQueueForIndex(i).remove(n);
		State state = n.getState();
		ExpandedQueue.insertIntoQueue(i, n);
		
		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			Node newNode = new Node(newState);
			HeuristicSolver.printState(newState);
			if(!ExpandedQueue.contains(i,newNode)) {
				if(newNode.equals(HeuristicSolver.createGoalState(3))) 
					setGoalCostForIndex(i, newNode.getCost());
				newNode.setParent(n);
				newNode.setAction(actionOnState);
				PQueue.getQueueForIndex(i).add(newNode);
			}
		}
	}

}
