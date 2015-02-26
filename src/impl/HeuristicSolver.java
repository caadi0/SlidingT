package impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class HeuristicSolver {
	
	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws FileNotFoundException {
//		PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Aaditya\\Desktop\\output.txt"));
//		System.setOut(out);
		State randomState = HeuristicSolver.createRandom(3);
		System.out.println("Random State");
		HeuristicSolver.printState(randomState);
		
		State goalState = HeuristicSolver.createGoalState(3);
		System.out.println("Goal State");
		HeuristicSolver.printState(goalState);
		
		PriorityQueue<Node> pq = PQueue.createQueue();
		Node n1 = new Node(randomState);
		pq.add(n1);	
		
		PriorityQueue<Node> expandedPQ = PQueue.createQueue();
		
		while(pq.isEmpty() == false) {
//			printAllHeuriticValuesInQueue(pq);
			Node queueHead = pq.poll();
			pq.remove(queueHead);
			expandedPQ.add(queueHead);
			State queueHeadState = queueHead.getState();
//			System.out.println("Heuristic Cost of removed element : "+ ManhattanDistance.calculate(queueHeadState));
//			System.out.println("------------");
			if(queueHead.getState().equals(goalState)) {
				System.out.println(" Moves ");
				printPath(queueHead);
				break;
			} else {
				List<Action> listOfPossibleActions = queueHeadState.getPossibleActions();
				Iterator<Action> actIter = listOfPossibleActions.iterator();
				while(actIter.hasNext()) {
					Action actionOnState = actIter.next();
					State newState = actionOnState.applyTo(queueHeadState);
					Node newNode = new Node(newState);
					if(!expandedPQ.contains(newNode)) {
						newNode.setParent(queueHead);
						newNode.setAction(actionOnState);
						pq.offer(newNode);	
					}
				}
			}
		}
		
	}
	
	private static void printPath(Node node) {
		if(node.getParent() != null) {
			printPath(node.getParent());
			System.out.println("Tile "+node.getAction().getMove());
		}
		printState(node.getState());
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
