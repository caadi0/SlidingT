package impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import algorithms.EuclideanDistance;
import model.Action;
import model.Grid2D;
import model.GridAction;
import model.Node;
import model.State;
import model.State.CellLocation;
import queues.ExpandedQueue;
import queues.PQueue;
import constants.Constants;

public class IMHAGrid {

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
	
	public static void IMHAStar() throws Exception {
		// testing code for 20 heuristics 19 - inadmissible and 1 admissible
		int heuristicCount = Constants.NumberOfInadmissibleHeuristicsForSMHAStar;
		Grid2D.initializeMap();
		Grid2D.printMap();
		
		
		
		// Inserting Goal Node into empty queues
		for(int i = 0; i < heuristicCount ; i++) {
			
			State startState = new State();
			startState.setPresentLocation(new CellLocation(0, 0));
			
			PriorityQueue<Node> pq = PQueue.getQueueForIndex(i);
			
			Node n1 = new Node(startState, Constants.w1);
			n1.setCost(0.0);
			n1.setHeuristicCost(EuclideanDistance.calculate(startState));
			pq.add(n1);
			
			goalCostForHeuristic.put(i, Double.POSITIVE_INFINITY);
		}
		
		Boolean breakFromWhileLoop = false;
		while(!PQueue.getQueueForIndex(0).isEmpty() && breakFromWhileLoop == false) {

			// Paper : line 18
			for(Integer i = 1 ; i <heuristicCount ; i++) {
				
				// Paper : line 19
				if(PQueue.getQueueForIndex(i).peek().getKey() <= Constants.w2 * PQueue.getQueueForIndex(0).peek().getKey()) {
					// Paper : line 20
					if(getGoalCostForIndex(i) <= PQueue.getQueueForIndex(i).peek().getKey()) {

//						System.out.println("Path length using IMHA is : "+HeuristicSolverUtility.printPathLength(PQueue.getGoalStateFromQueue(i)));
//						TODO : for grid planner, modify
						breakFromWhileLoop = true;
						break;
					}
					Node openN = PQueue.getQueueForIndex(i).peek();
					expand(openN, i);
				} else {
					// line 25
					if(getGoalCostForIndex(0) <= PQueue.getQueueForIndex(0).peek().getKey()) {
					
//						System.out.println("Path length using Random heuristic is : "+HeuristicSolverUtility.printPathLength(PQueue.getGoalStateFromQueue(0)));
//						TODO : for grid planner, modify
						breakFromWhileLoop = true;
						break;
					}
					Node n = PQueue.getQueueForIndex(0).peek();
					expand(n, 0);
				}
			}
		}
	}
	
	public static void expand(Node n, Integer i) {
		
		PQueue.getQueueForIndex(i).remove(n);
		State state = n.getState();
		ExpandedQueue.insertIntoExpandedQueue(i, n);
		
		List<GridAction> listOfPossibleActions = state.getPossibleActionsForMapNavigation();
		Iterator<GridAction> actIter = listOfPossibleActions.iterator();
		Boolean insert = true;
		while(actIter.hasNext()) {
			GridAction actionOnState = actIter.next();
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
				if(insert)
					PQueue.getQueueForIndex(i).add(newNode);
			}
		}
	}

}
