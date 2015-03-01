package impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import constants.Constants;
import algorithms.ManhattanDistance;
import model.Action;
import model.Node;
import model.State;

public class Start {
	
	private HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private Node nGoal = null;
	private Node nStart = null;
	private int pathLength = 0;
	private PrintStream out = null;
	private State goalState = null;

	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws Exception 
	{
		Start heuristicSolver = new Start();
		heuristicSolver.init();
	}
	
	private void init() throws Exception
	{
		out = new PrintStream(new FileOutputStream("C:\\Users\\Shipra\\Desktop\\output.txt"));
		System.setOut(out);
		
		State randomState = createRandom(3);
		System.out.println("Random State");
		printState(randomState);
		
		goalState = createGoalState(3);
		System.out.println("Goal State");
		printState(goalState);
		
		nStart = new Node(randomState, Constants.w1);
		nStart.setCost(0);;

		nGoal = new Node(goalState, Constants.w1);
		
		SMHAstar();
		AStar();
		IMHA.IMHAStar(randomState);
	}
	
	private void AStar()
	{
		System.out.println("A*");
		nGoal = new Node(goalState , 1.0);
		PriorityQueue<Node> pq1 = PQueue.createQueue();
		pq1.add(nStart);	
		
		PriorityQueue<Node> expandedPQ = PQueue.createQueue();
		
		while(pq1.isEmpty() == false) {
//			printAllHeuriticValuesInQueue(pq);
			Node queueHead = pq1.poll();
			pq1.remove(queueHead);
			expandedPQ.add(queueHead);
			State queueHeadState = queueHead.getState();
//			System.out.println("Heuristic Cost of removed element : "+ ManhattanDistance.calculate(queueHeadState));
//			System.out.println("------------");
			if(queueHead.getState().equals(goalState)) {
				System.out.println(" Moves ");
				pathLength = 0;
				printPathLength(queueHead);
				System.out.println("A* no of moves is"+pathLength);
				break;
			} else {
				List<Action> listOfPossibleActions = queueHeadState.getPossibleActions();
				Iterator<Action> actIter = listOfPossibleActions.iterator();
				while(actIter.hasNext()) {
					Action actionOnState = actIter.next();
					State newState = actionOnState.applyTo(queueHeadState);
					Node newNode = new Node(newState, Constants.w1);
					if(!expandedPQ.contains(newNode)) {
						newNode.setParent(queueHead);
						newNode.setAction(actionOnState);
						pq1.offer(newNode);	
					}
				}
			}
		}
	}
	
	private void SMHAstar() 
	{
		PriorityQueue<Node> pq = AnchorQueue.createQueue();
		pq.add(nStart);	
		
		List<PriorityQueue<Node>> pqList = new ArrayList<PriorityQueue<Node>>();
		
		for(int i=0; i<Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
		{
			PriorityQueue<Node> prq = InadmissibleHeuristicQueue.createQueue();
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
					printPathLength(nGoal);
					System.out.println("path length is :"+pathLength);
					
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
		for(PriorityQueue<Node> pq: listPQ)
		{
			pq.remove(toBeExpanded);
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
			visited.put(newState.hashCode(), true);
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
			}
			if(newNode.hashCode() == nGoal.hashCode())
//				nGoal.setCost(newNode.getCost());
				nGoal = newNode;
		}
	}
	
	
	
	private void removeNodeForSimilarStateFromQueue(PriorityQueue<Node> pq, Node searchNode)
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
		
		int minKeyAnchor = anchorKey(anchor);
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
	
	private void printPath(Node node) {
		if(node.getParent() != null) {
			printPath(node.getParent());
			System.out.println("Tile "+node.getAction().getMove());
		}
		printState(node.getState());
		pathLength++;
	}
	
	private void printPathLength(Node node) {
		if(node.getParent() != null) {
			printPathLength(node.getParent());
			pathLength++;
		}
	}
	
	private int anchorKey(Node anchor)
	{
		return ((Double)(anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()))).intValue();
	}
	
	private Double inadmissibleNodeKey(Node inadmissible, int heuristic)
	{
		return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
				(heuristic, inadmissible.getState());
	}
	
	private void printAllHeuriticValuesInQueue(PriorityQueue<Node> q) {
		Iterator<Node> qIter = q.iterator();
		System.out.print("Heuristic values present in Queue  :   ");
		while(qIter.hasNext()) {
			System.out.print(ManhattanDistance.calculate(qIter.next().getState())+" ");
		}
		System.out.println("");
	}
	
	private void printState(State state) {
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
	
	public State createRandom(int dimension) {
		State s = new State(createGoalState(dimension).getAllCells());
		Action old = null;
		
		for (int i = 0; i < 500; i++) {
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

		return s;
	}
	
	private State createGoalState(int dimension){
		
		int nbrOfCells = dimension * dimension;
		byte[] goalCells = new byte[nbrOfCells];
		for (byte i = 1; i < goalCells.length; i++) {
			goalCells[i - 1] = i;
		}
		goalCells[nbrOfCells - 1] = 0;
		
		return new State(goalCells);
		
	}
	
}
