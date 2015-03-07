package impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import queues.ExpandedQueue;
import queues.PQueue;
import model.Action;
import model.Node;
import model.State;
import constants.Constants;

public class IMHA {
	
	static Integer totalStatesExpanded = 0;
	
	/*
	 * Index = heuristic Number
	 * Value = goal state cost
	 */
	private static HashMap<Integer, Double> goalCostForHeuristic = new HashMap<Integer, Double>();
	
	private static Double getGoalCostForIndex(Integer index) {
		return goalCostForHeuristic.get(index);
	}
	
	private static void setGoalCostForIndex(Integer index, Double cost) {
		goalCostForHeuristic.put(index, cost);
	}
	
	public static void IMHAStar(State startState) throws Exception {
		// testing code for 20 heuristics 19 - inadmissible and 1 admissible
		int heuristicCount = Constants.NumberOfInadmissibleHeuristicsForSMHAStar;
		State randomState = startState;
		
		// Inserting Goal Node into empty queues
		for(int i = 0; i < heuristicCount ; i++) {
			Node initialNode = new Node(randomState, Constants.w1);
			initialNode.setCost(0.0);
			initialNode.setHeuristicCost(RandomHeuristicGenerator.generateRandomHeuristic(i, initialNode.getState()));
			PriorityQueue<Node> pq = PQueue.getQueueForIndex(i);
			pq.add(initialNode);
			goalCostForHeuristic.put(i, Double.POSITIVE_INFINITY);
		}
		
		Boolean breakFromWhileLoop = false;
		while(!PQueue.getQueueForIndex(0).isEmpty() && breakFromWhileLoop == false) {
			if(Constants.debug) {
				System.out.print("Printing elements for queue : "+0 + "  :  ");
				HeuristicSolverUtility.printAllHeuriticValuesInQueue(PQueue.getQueueForIndex(0));
			}
			// Paper : line 18
			for(Integer i = 1 ; i <heuristicCount ; i++) {
				if(Constants.debug) {
					System.out.print("Printing elements for queue : "+i + "  :  ");
					HeuristicSolverUtility.printAllHeuriticValuesInQueue(PQueue.getQueueForIndex(i));
				}
				
				// Paper : line 19
				if(PQueue.getQueueForIndex(i).peek().getKey() <= Constants.w2 * PQueue.getQueueForIndex(0).peek().getKey()) {
					// Paper : line 20
					if(getGoalCostForIndex(i) <= PQueue.getQueueForIndex(i).peek().getKey()) {
						if(Constants.debug) {
							System.out.println("Getting results from Random Heuristic Search Number : " +i);
							HeuristicSolverUtility.printPath(PQueue.getGoalStateFromQueue(i));
						}

						System.out.println("Path length using IMHA is : "+HeuristicSolverUtility.printPathLength(PQueue.getGoalStateFromQueue(i)));
						breakFromWhileLoop = true;
						break;
					}
					Node openN = PQueue.getQueueForIndex(i).peek();
					expand(openN, i);
				} else {
					// line 25
					if(getGoalCostForIndex(0) <= PQueue.getQueueForIndex(0).peek().getKey()) {
					
						if(Constants.debug) {
							System.out.println("Getting results from Anchor Search");
							HeuristicSolverUtility.printPath(PQueue.getGoalStateFromQueue(0));
						}
						System.out.println("Path length using Random heuristic is : "+HeuristicSolverUtility.printPathLength(PQueue.getGoalStateFromQueue(0)));
						breakFromWhileLoop = true;
						break;
					}
					Node n = PQueue.getQueueForIndex(0).peek();
					expand(n, 0);
				}
			}
			if(Constants.debug)
				System.out.println("");
		}
	}
	
	public static void expand(Node n, Integer i) {
		
		PQueue.getQueueForIndex(i).remove(n);
		State state = n.getState();
		ExpandedQueue.insertIntoExpandedQueue(i, n);
		
		List<Action> listOfPossibleActions = state.getPossibleActionsForTilePuzzle();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		Boolean insert = true;
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			Node newNode = new Node(newState , Constants.w1);
			if(!ExpandedQueue.doesExpandedQueueContainNode(i,newNode)) {
				newNode.setHeuristicCost(RandomHeuristicGenerator.generateRandomHeuristic(i, newNode.getState()));
				newNode.setParent(n);
				Node findingExistingNode;
				try {
					findingExistingNode = PQueue.getStateWithSameArrangementFromQueue(newNode, i);
					if(findingExistingNode != null) {
						if(findingExistingNode.getCost() > newNode.getCost()) {
							PQueue.getQueueForIndex(i).remove(findingExistingNode);
						} else 
							insert = false;
					}
				} catch (Exception e) {
					// No action needed
				}
				totalStatesExpanded++;
				if(newState.equals(HeuristicSolverUtility.generateGoalState(3))) 
					setGoalCostForIndex(i, newNode.getCost());
				newNode.setAction(actionOnState);
				if(insert)
					PQueue.getQueueForIndex(i).add(newNode);
			}
		}
	}

}
