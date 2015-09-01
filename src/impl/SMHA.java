package impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import model.Action;
import model.AtomicNode;
import model.Node;
import model.State;
import model.StateConstants;
import queues.AnchorQueue;
import queues.InadmissibleHeuristicQueue;
import algorithms.ManhattanDistance;
import constants.Constants;

public class SMHA {
	

	private HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private Node nGoal = null;
	private int pathLength = 0;
	private Long startTime = null;
	private Long endTime = null;
	private Boolean timedOut = false;
	PrintWriter printWriter = null;
	public void SMHAstar(State randomState, PrintWriter out) throws InterruptedException 
	{
		printWriter = out;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				printWriter.println("serial smha timed out");
				printWriter.flush();
				timedOut = true;
				return;
			}
		}, 60000);
		startTime = System.currentTimeMillis();
		Node nStart = createNode(randomState, Constants.w1);
		nStart.setCost(0);
		
		State goalState = HeuristicSolverUtility.generateGoalState(7);
		printWriter.println("goal state is: "+goalState.hashCode());
//		System.out.println("Goal State");
//		HeuristicSolverUtility.printState(goalState);
		nGoal = createNode(goalState, Constants.w1);
		printNeighbours(nGoal);
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
				Node node = null;
				//for debugging
//				printWriter.println("available states in heuristic "+i+" queue are:");
//				for(Node n: p)
//				{
//					printWriter.print(n.hashCode()+" ");
//				}
				
				if(expandAnchor(pq.peek(), p.peek(), i))
				{
					selected = pq;
					expandedByAnchor.put(selected.peek().hashCode(), true);
//					System.out.println("Expanded by anchor:");
//					printState(selected.peek().getState());
					
					if(nGoal.getCost() <= anchorKey(selected.peek()))
					{
						pathLength = HeuristicSolverUtility.printPathLength(nGoal);
//						System.out.println("path length using SMHA is :"+pathLength);
						endTime = System.currentTimeMillis();
						printWriter.println("time taken for serial SMHA* is"+(endTime - startTime));
						printWriter.flush();
						timer.cancel();
						return;
					}
					if(timedOut)
						return;
					
					node = selected.remove();
					printWriter.println(node.hashCode());
				}
				else
				{
					selected = p;
					expandedByInadmissible.put(selected.peek().hashCode(), true);
//					System.out.println("Expanded by inadmissible heuristic: ");
//					printState(selected.peek().getState());
					if(nGoal.getCost() <= inadmissibleNodeKey(selected.peek(), i))
					{
						pathLength = HeuristicSolverUtility.printPathLength(nGoal);
//						System.out.println("path length using SMHA is :"+pathLength);
						endTime = System.currentTimeMillis();
						printWriter.println("time taken for serial SMHA* is"+(endTime - startTime));
						printWriter.flush();
						timer.cancel();
						return;
					}
					if(timedOut)
						return;
					
					node = selected.remove();
					printWriter.println(node.hashCode()+" heuristic :"+i);
				}
//				if(nGoal.getCost() <= selected.peek().getCost())
//				{
//					pathLength = HeuristicSolverUtility.printPathLength(nGoal);
////					System.out.println("path length using SMHA is :"+pathLength);
//					endTime = System.currentTimeMillis();
//					System.out.println("time taken for serial SMHA* is"+(endTime - startTime));
//					return;
//				}
				
				expandNode(pq, pqList, node);
			}
	
		}
		System.out.println("anchor queue emptied");
		
		
	}
	
	private Node createNode(State state, Double weight)
	{
		if(StateConstants.nodeMap.get(state.hashCode()) != null)
		{
			return StateConstants.nodeMap.get(state.hashCode());
		}
		else
		{
			Node node = new Node(state, weight);
			StateConstants.nodeMap.put(state.hashCode(), node);
			return StateConstants.nodeMap.get(state.hashCode());
		}
		
	}
	
	private void expandNode(PriorityQueue<Node> anchorPQ, List<PriorityQueue<Node>> listPQ, Node toBeExpanded) throws InterruptedException
	{
		anchorPQ.remove(toBeExpanded);
//		removeNodeForSimilarStateFromQueue(anchorPQ, toBeExpanded);
		for(PriorityQueue<Node> pq: listPQ)
		{
			pq.remove(toBeExpanded);
//			removeNodeForSimilarStateFromQueue(pq, toBeExpanded);
		}
		State state = toBeExpanded.getState();
		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			Node newNode = createNode(newState, Constants.w1);
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
//					removeNodeForSimilarStateFromQueue(anchorPQ, newNode);
					anchorPQ.remove(newNode);
					anchorPQ.add(newNode);
					if(expandedByInadmissible.get(newNode.hashCode()) == null)
					{
						addOrUpdateNodeToInadmissibleQueues(listPQ, newNode);
					}
				}
//				not necessary coz using NodeMap now
//				if(newNode.hashCode() == nGoal.hashCode())
//					nGoal = newNode;
				
//				printWriter.println(newNode.hashCode()+" updated by "+toBeExpanded.hashCode()+" to "+newNode);
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
//				removeNodeForSimilarStateFromQueue(pq, toBeAdded);
				pq.remove(toBeAdded);
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
//		printWriter.println("minKeyAnchor : "+minKeyAnchor);
//		printWriter.println("minKeyInadmissible : "+minKeyInadmissible);
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
	
	public void printNeighbours(Node atomicNode) {
		printWriter.println("neighbours are :");
		State state = atomicNode.getState();
		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			printWriter.println(newState.hashCode());
		}
	}

	public static void main(String args[]) throws IOException, InterruptedException
	{
		PrintWriter out =  new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Shipra\\Desktop\\outputNew.txt", false)));
		out.println("start");
		new SMHA().SMHAstar(HeuristicSolverUtility.createRandom(7), out);
	}
	
//	private static void removeNodeForSimilarStateFromQueue(PriorityQueue<Node> pq, Node searchNode)
//	{
//		List<Node> removeList = new ArrayList<Node>();
//		for(Node node: pq)
//		{
//			if(node.hashCode() == searchNode.hashCode())
//				removeList.add(node);
//		}
//		pq.removeAll(removeList);
//	}
}
