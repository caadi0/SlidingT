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

public class HeuristicSolver {
	
	private static HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private static HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private static HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private static Node nGoal = null;
	private static int pathLength = 0;

	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Shipra\\Desktop\\output.txt"));
		System.setOut(out);
		State randomState = HeuristicSolver.createRandom(3);
		System.out.println("Random State");
		HeuristicSolver.printState(randomState);
		
		State goalState = HeuristicSolver.createGoalState(3);
		System.out.println("Goal State");
		HeuristicSolver.printState(goalState);
		
		Node nStart = new Node(randomState);
		nStart.setCost(0);;

		nGoal = new Node(goalState);
		
		PriorityQueue<Node> pq = AnchorQueue.createQueue();
		pq.add(nStart);	
		
		List<PriorityQueue<Node>> pqList = new ArrayList<PriorityQueue<Node>>();
		
		for(int i=0; i<Constants.NoH; i++)
		{
			PriorityQueue<Node> prq = InadmissibleHeuristicQueue.createQueue();
			prq.add(nStart);
			pqList.add(prq);
		}
		
		visited.put(nStart.hashCode(), true);
		System.out.println("Visited:");
		HeuristicSolver.printState(nStart.getState());
		
		while(pq.isEmpty() == false) {
			
			int i = 0;
			for(PriorityQueue<Node> p: pqList)
			{
				i++;
				PriorityQueue<Node> selected = null;
				if(expandAnchor(pq.peek(), p.peek(), i))
				{
					selected = p;
					expandedByAnchor.put(selected.peek().hashCode(), true);
					System.out.println("Expanded by anchor:");
					HeuristicSolver.printState(selected.peek().getState());
				}
				else
				{
					selected = pq;
					expandedByInadmissible.put(selected.peek().hashCode(), true);
					System.out.println("Expanded by inadmissible heuristic: ");
					HeuristicSolver.printState(selected.peek().getState());
				}
				if(nGoal.getCost() <= selected.peek().getCost())
				{
					printPath(nGoal);
					System.out.println("path length is :"+pathLength);
					return;
				}
				Node node = selected.remove();
				
				expandNode(pq, pqList, node);
			}
	
		}
		System.out.println("anchor queue emptied");
	}
	
	private static void expandNode(PriorityQueue<Node> anchorPQ, List<PriorityQueue<Node>> listPQ, Node toBeExpanded)
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
			Node newNode = new Node(newState);
			if(visited.get(newState.hashCode()) == null)
			{
				// initialise cost to infinity and parent to null;
			}
			visited.put(newState.hashCode(), true);
			System.out.println("Visited:");
			HeuristicSolver.printState(newState);
			if(newNode.getCost() > toBeExpanded.getCost()+1)
			{
				newNode.setParent(toBeExpanded);
				if(expandedByAnchor.get(newState.hashCode()) == null)
				{
					anchorPQ.add(newNode);
					if(expandedByInadmissible.get(newState.hashCode()) == null)
					{
						addNodeToInadmissibleQueues(listPQ, newNode);
					}
				}
			}
			if(newNode.hashCode() == nGoal.hashCode())
//				nGoal.setCost(newNode.getCost());
				nGoal = newNode;
		}
	}
	
	private static void addNodeToInadmissibleQueues(List<PriorityQueue<Node>> listPQ, Node toBeAdded)
	{
		int heuristic = 0;
		for(PriorityQueue<Node> pq: listPQ)
		{
			heuristic++;
			if(inadmissibleNodeKey(toBeAdded, heuristic) <= anchorKey(toBeAdded))
			{
				pq.remove(toBeAdded);
				pq.add(toBeAdded);
			}
		}
	}
	
	
	private static Boolean expandAnchor(Node anchor, Node inadmissible, int heuristic)
	{
		if(inadmissible == null)
			return false;
		
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
	
	private static void printPath(Node node) {
		if(node.getParent() != null) {
			printPath(node.getParent());
//			System.out.println("Tile "+node.getAction().getMove());
		}
		printState(node.getState());
		pathLength++;
	}
	
	private static int anchorKey(Node anchor)
	{
		return ((Double)(anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()))).intValue();
	}
	
	private static Double inadmissibleNodeKey(Node inadmissible, int heuristic)
	{
		return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
				(heuristic, inadmissible.getState());
	}
	
	private static void printAllHeuriticValuesInQueue(PriorityQueue<Node> q) {
		Iterator<Node> qIter = q.iterator();
		System.out.print("Heuristic values present in Queue  :   ");
		while(qIter.hasNext()) {
			System.out.print(ManhattanDistance.calculate(qIter.next().getState())+" ");
		}
		System.out.println("");
	}
	
	private static void printState(State state) {
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
	
	public static State createRandom(int dimension) {
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
	
	private static State createGoalState(int dimension){
		
		int nbrOfCells = dimension * dimension;
		byte[] goalCells = new byte[nbrOfCells];
		for (byte i = 1; i < goalCells.length; i++) {
			goalCells[i - 1] = i;
		}
		goalCells[nbrOfCells - 1] = 0;
		
		return new State(goalCells);
		
	}
	
}
