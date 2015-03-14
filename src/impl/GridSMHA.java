package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import model.Grid2D;
import model.GridAction;
import model.Node;
import model.State;
import model.State.CellLocation;
import queues.AnchorQueue;
import queues.InadmissibleHeuristicQueue;
import algorithms.EuclideanDistance;
import constants.Constants;

public class GridSMHA {
	
	PriorityQueue<Node> anchorQueue ;
	List<PriorityQueue<Node>> inadmissibleQueueList;
	HashMap<Node, Boolean> visited ;
	HashMap<Integer, Boolean> expandedByAnchor;
	HashMap<Integer, Boolean> expandedByInadmissible;
	Node nGoal = null;
	
	
	public GridSMHA()
	{
		Grid2D.setMapToInitial();
		anchorQueue = AnchorQueue.createQueue();
		inadmissibleQueueList = new ArrayList<PriorityQueue<Node>>();
		visited = new HashMap<Node, Boolean>();
		expandedByAnchor = new HashMap<Integer, Boolean>();
		expandedByInadmissible = new HashMap<Integer, Boolean>();
		
		State startState = new State();
		startState.setPresentLocation(new CellLocation(0, 0));
		
		State goalState = new State();
		goalState.setPresentLocation(new CellLocation(Constants.gridMaxXAxis - 1, Constants.gridMaxYAxis - 1));
		nGoal = new Node(goalState, Constants.w1);
		nGoal.setHeuristicCost(0.0);
		
		Node startNode = new Node(startState, Constants.w1);
		startNode.setCost(0.0);
		startNode.setHeuristicCost(Double.valueOf(""+EuclideanDistance.calculate(startState)));
		anchorQueue.add(startNode);
		for(int i=0; i<Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
		{
			PriorityQueue<Node> prq = InadmissibleHeuristicQueue.createQueue(i+1);
			prq.add(startNode);
			inadmissibleQueueList.add(prq);
		}
		visited.put(startNode, true);
		
		while(!anchorQueue.isEmpty())
		{
			int i = 0;
			for(PriorityQueue<Node> inadmissibleQ: inadmissibleQueueList)
			{
				i++;
				PriorityQueue<Node> selected = null;
				if(inadmissibleQ.isEmpty() && anchorQueue.isEmpty())
					continue;
				if(expandAnchor(inadmissibleQ.peek(), i))
				{
					selected = anchorQueue;
					expandedByAnchor.put(selected.peek().hashCode(), true);
				}
				else
				{
					selected = inadmissibleQ;
					expandedByInadmissible.put(selected.peek().hashCode(), true);
				}
				// If reached goal state
				if (nGoal.getCost() <= selected.peek().getCost()) {
					setMapPath(nGoal);
					System.out.println("");
					System.out.println("Path is :-");
					System.out.println("");
					Grid2D.printMap();
					System.out.println(("number of expanded states is :-"+expandedByAnchor.size())+","+expandedByInadmissible.size());
					return;
				}
				
				expandNode(anchorQueue, inadmissibleQueueList, selected.peek());
			}
		}
		System.out.println("");
		System.out.println("Path is :-");
		System.out.println("");
		Grid2D.printMap();
		System.out.println("number of expanded states is :-"+(expandedByAnchor.size()+expandedByInadmissible.size()));
		return;
	}
		
	private void setMapPath(Node node) {
		if(node.getParent() != null)
			setMapPath(node.getParent());
		Grid2D.setMapValue(node.getState().getPresentLocation().getRowIndex(), 
				node.getState().getPresentLocation().getColumnIndex(), "-");
	}

	private static void removeNodeForSimilarStateFromQueue(PriorityQueue<Node> pq, Node searchNode)
	{
		List<Node> removeList = new ArrayList<Node>();
		for(Node node: pq)
		{
			if(node.equals(searchNode))
				removeList.add(node);
		}
		pq.removeAll(removeList);
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
		List<GridAction> listOfPossibleActions = state
				.getPossibleActionsForMapNavigation();
		Iterator<GridAction> actIter = listOfPossibleActions.iterator();
		
		
//		List<Action> listOfPossibleActions = state.getPossibleActionsForTilePuzzle();
//		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			GridAction actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			Node newNode = new Node(newState, Constants.w1);
//			if(visited.get(newState.hashCode()) == null)
//			{
//				 initialise cost to infinity and parent to null;
//			}
			visited.put(newNode, true);
//			System.out.println("Visited:");
//			printState(newState);
			if(((actionOnState.getMove().toString().equalsIgnoreCase("UP") 
								|| actionOnState.getMove().toString().equalsIgnoreCase("DOWN")
								|| actionOnState.getMove().toString().equalsIgnoreCase("LEFT")
								|| actionOnState.getMove().toString().equalsIgnoreCase("RIGHT")) &&
					
					newNode.getCost() > toBeExpanded.getCost()+1) || 
					(newNode.getCost() > toBeExpanded.getCost()+1.414))
			{
				newNode.setParent(toBeExpanded);
				if(!expandedByAnchor.containsKey(newNode.hashCode()))
				{
					removeNodeForSimilarStateFromQueue(anchorPQ, newNode);
					anchorPQ.add(newNode);
					if(!expandedByInadmissible.containsKey(newNode.hashCode()))
					{
						addOrUpdateNodeToInadmissibleQueues(listPQ, newNode);
					}
				}
				if(newNode.equals(nGoal))
//					nGoal.setCost(newNode.getCost());
					nGoal = newNode;
			}
			
		}
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
	
	private Boolean expandAnchor(Node inadmissible, int heuristic)
	{
		Node anchor = anchorQueue.peek();
		if(inadmissible == null)
			return true;
		else if(anchor == null)
			return false;
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
		return (anchor.getCost() + Constants.w1* EuclideanDistance.calculate(anchor.getState()));
	}
	
	private Double inadmissibleNodeKey(Node inadmissible, int heuristic)
	{
		return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
				(heuristic, inadmissible.getState());
	}
	
//	public static void main(String args[])
//	{
//		new GridSMHA();
//	}
}
