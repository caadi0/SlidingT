package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import queues.PQueue;
import algorithms.ManhattanDistance;
import constants.Constants;
import model.Action;
import model.Node;
import model.State;
import mpi.*;

public class AnchorHeuristic {
	
	
	public static void main(String[] args) {
		MPI.Init(args);
		int me = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		System.out.println("Hi from <"+me+">");
		if(me == 1)
		{
			AnchorHeuristic ah = new AnchorHeuristic();
		}
		else 
		{
			RandomHeuristic rh = new RandomHeuristic(me, 0.0, Constants.CommunicationInterval);
		}
		
	}
	
	PriorityQueue<Node> nodePriorityQueue = PQueue.createQueue();
	HashMap<Integer, Node> listOfNodesMap = new HashMap<Integer, Node>();
	HashMap<Integer, Node> listOfExpandedNodesMap = new HashMap<Integer, Node>();
	Boolean isRunning = false;
	
	State goalState = HeuristicSolverUtility.generateGoalState(3);
	
	public AnchorHeuristic()
	{
		State randomState = HeuristicSolverUtility.createRandom(3);
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		Double initialBound = Constants.w1 * ManhattanDistance.calculate(randomState);
		Node initialNode = new Node(randomState, Constants.w1);
		
		initialNode.setCost(0);
		initialNode.setHeuristicCost((double) ManhattanDistance.calculate(randomState));
		
		// Adding to the list
		nodePriorityQueue.add(initialNode);
		listOfNodesMap.put(initialNode.hashCode(), initialNode);
		
		isRunning = true;
		
		hearMergeEvent();
		for(int i = 0 ; i < Constants.NumberOfInadmissibleHeuristicsForSMHAStar ; i++)
		{
			startChild(i, initialBound, Constants.CommunicationInterval);
		}
		run();
	}
	
	private void startChild(int queueID, Double initialBound, int communicationInterval)
	{
		// Start Stuff here
	}
	
	private void run()
	{
		while(isRunning)
		{
			while (nodePriorityQueue.isEmpty() == false) 
			{
				Node queueHead = nodePriorityQueue.remove();
				listOfExpandedNodesMap.put(queueHead.hashCode(), queueHead);
				State queueHeadState = queueHead.getState();

				// If reached goal state
				if (queueHead.getState().equals(goalState)) 
				{
					System.out.println("Path length using A* is : "
							+ HeuristicSolverUtility.printPathLength(queueHead));
					break;
				} 
				else 
				{
					List<Action> listOfPossibleActions = queueHeadState
							.getPossibleActions();
					Iterator<Action> actIter = listOfPossibleActions.iterator();
					while (actIter.hasNext()) {
						Action actionOnState = actIter.next();
						State newState = actionOnState.applyTo(queueHeadState);
						Node newNode = new Node(newState, Constants.w1);
						if (!listOfExpandedNodesMap.containsKey(newNode.hashCode())) {
							newNode.setHeuristicCost((double) ManhattanDistance
									.calculate(newState));
							newNode.setParent(queueHead);
							newNode.setAction(actionOnState);
							nodePriorityQueue.offer(newNode);
						}
					}
				}
			}
		}
	}
	
	private void hearMergeEvent()
	{
//		MPI.COMM_WORLD.
		List<Node> listOfReceivedNodes = new ArrayList<Node>();
		isRunning = false;
		merge(listOfReceivedNodes);
	}
	
	private void merge(List<Node> listOfReceivedNodes)
	{
		Iterator<Node> nodeIter = listOfReceivedNodes.iterator();
		while(nodeIter.hasNext())
		{
			Node node = nodeIter.next();
			Node existingNode = listOfNodesMap.get(node.hashCode());
			if(existingNode != null)
			{
				if(existingNode.getCost() < node.getCost())
				{
					// Nothing to do here
				}
				else
				{
					existingNode.setCost(node.getCost());
					existingNode.setParent(node.getParent());
				}
			}
			else
			{
				nodePriorityQueue.add(node);
				listOfNodesMap.put(node.hashCode(), node);
			}
		}
		isRunning = true;
		run();
	}

}
