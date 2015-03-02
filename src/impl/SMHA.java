package impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import model.Action;
import model.Node;
import model.State;
import algorithms.ManhattanDistance;
import constants.Constants;

public class SMHA {
	

	private HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private Node nGoal = null;
	private int pathLength = 0;
	
	public void SMHAstar(State randomState) 
	{
		Node nStart = new Node(randomState, Constants.w1);
		nStart.setCost(0);
		
		State goalState = HeuristicSolverUtility.generateGoalState(3);
		System.out.println("Goal State");
		HeuristicSolverUtility.printState(goalState);
		nGoal = new Node(goalState, Constants.w1);
		
		PriorityQueue<Node> pq = AnchorQueue.createQueue();
		pq.add(nStart);	
		
		List<PriorityQueue<Node>> pqList = new ArrayList<PriorityQueue<Node>>();
		
		for(int i=0; i<Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
		{
			PriorityQueue<Node> prq = InadmissibleHeuristicQueue.createQueue(i+1);
			prq.add(nStart);
			pqList.add(prq);
		}
		
		visited.put(nStart.hashCode(), true);
//		System.out.println("Visited:");
//		printState(nStart.getState());
		
		while(pq.isEmpty() == false) {
			
			int i = 0;
			for(PriorityQueue<Node> p: pqList)
			{
				i++;
				PriorityQueue<Node> selected = null;
				if(expandAnchor(pq.peek(), p.peek(), i))
				{
					selected = pq;
					expandedByAnchor.put(selected.peek().hashCode(), true);
//					System.out.println("Expanded by anchor:");
//					printState(selected.peek().getState());
				}
				else
				{
					selected = p;
					expandedByInadmissible.put(selected.peek().hashCode(), true);
//					System.out.println("Expanded by inadmissible heuristic: ");
//					printState(selected.peek().getState());
				}
				if(nGoal.getCost() <= selected.peek().getCost())
				{
					pathLength = HeuristicSolverUtility.printPathLength(nGoal);
					System.out.println("path length using SMHA is :"+pathLength);
					
					return;
				}
				Node node = selected.remove();
				
				expandNode(pq, pqList, node);
			}
	
		}
		System.out.println("anchor queue emptied");
		
		
	}
	
	private void expandNode(PriorityQueue<Node> anchorPQ, List<PriorityQueue<Node>> listPQ, Node toBeExpanded)
	{
		anchorPQ.remove(toBeExpanded);
		removeNodeForSimilarStateFromQueue(anchorPQ, toBeExpanded);
		for(PriorityQueue<Node> pq: listPQ)
		{
			pq.remove(toBeExpanded);
			removeNodeForSimilarStateFromQueue(pq, toBeExpanded);
		}
		State state = toBeExpanded.getState();
		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			Node newNode = new Node(newState, Constants.w1);
//			if(visited.get(newState.hashCode()) == null)
//			{
//				 initialise cost to infinity and parent to null;
//			}
			visited.put(newNode.hashCode(), true);
//			System.out.println("Visited:");
//			printState(newState);
			if(newNode.getCost() > toBeExpanded.getCost()+1)
			{
				newNode.setParent(toBeExpanded);
				if(expandedByAnchor.get(newNode.hashCode()) == null)
				{
					removeNodeForSimilarStateFromQueue(anchorPQ, newNode);
					anchorPQ.add(newNode);
					if(expandedByInadmissible.get(newNode.hashCode()) == null)
					{
						addOrUpdateNodeToInadmissibleQueues(listPQ, newNode);
					}
				}
				if(newNode.hashCode() == nGoal.hashCode())
//					nGoal.setCost(newNode.getCost());
					nGoal = newNode;
			}
			
		}
	}
	
	
	
	private static void removeNodeForSimilarStateFromQueue(PriorityQueue<Node> pq, Node searchNode)
	{
		List<Node> removeList = new ArrayList<Node>();
		for(Node node: pq)
		{
			if(node.hashCode() == searchNode.hashCode())
				removeList.add(node);
		}
		pq.removeAll(removeList);
	}
	
	private void addOrUpdateNodeToInadmissibleQueues(List<PriorityQueue<Node>> listPQ, Node toBeAdded)
	{
		int heuristic = 0;
		for(PriorityQueue<Node> pq: listPQ)
		{
			heuristic++;
			if(inadmissibleNodeKey(toBeAdded, heuristic) <= Constants.w2*anchorKey(toBeAdded))
			{
				removeNodeForSimilarStateFromQueue(pq, toBeAdded);
				pq.add(toBeAdded);
			}
		}
	}
	
	
	private Boolean expandAnchor(Node anchor, Node inadmissible, int heuristic)
	{
		if(inadmissible == null)
			return true;
		
		Boolean result = false;
		
		Double minKeyAnchor = anchorKey(anchor);
		Double minKeyInadmissible = inadmissibleNodeKey(inadmissible, heuristic);
		if(minKeyInadmissible <= Constants.w2*minKeyAnchor)
		{
			result = false;
		}
		else
		{
			result = true;
		}
		return result;
	}
	
	private Double anchorKey(Node anchor)
	{
		return (anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()));
	}
	
	private Double inadmissibleNodeKey(Node inadmissible, int heuristic)
	{
		return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
				(heuristic, inadmissible.getState());
	}

}
