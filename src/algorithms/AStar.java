package algorithms;

import impl.HeuristicSolverUtility;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import queues.PQueue;
import model.Action;
import model.Node;
import model.State;

public class AStar {

	private static Double weight = 1.0;

	/**
	 * @param randomState
	 *            Solves the Problem using A star algorithm
	 */
	public static void solveUsingAStar(State randomState) {

		// Generate goal state
		State goalState = HeuristicSolverUtility.generateGoalState(3);

		PriorityQueue<Node> pq = PQueue.createQueue();
		Node n1 = new Node(randomState, weight);
		n1.setCost(0);
		n1.setHeuristicCost((double) ManhattanDistance.calculate(randomState));
		pq.add(n1);

		PriorityQueue<Node> expandedPQ = PQueue.createQueue();

		while (pq.isEmpty() == false) {

			Node queueHead = pq.remove();
			expandedPQ.add(queueHead);
			State queueHeadState = queueHead.getState();

			// If reached goal state
			if (queueHead.getState().equals(goalState)) {
				System.out.println(" Moves ");
				HeuristicSolverUtility.printPath(queueHead);
				System.out.println("Path length using A* is : "
						+ HeuristicSolverUtility.printPathLength(queueHead));
				break;
			} else {
				List<Action> listOfPossibleActions = queueHeadState
						.getPossibleActions();
				Iterator<Action> actIter = listOfPossibleActions.iterator();
				while (actIter.hasNext()) {
					Action actionOnState = actIter.next();
					State newState = actionOnState.applyTo(queueHeadState);
					Node newNode = new Node(newState, weight);
					if (!expandedPQ.contains(newNode)) {
						newNode.setHeuristicCost((double) ManhattanDistance
								.calculate(queueHeadState));
						newNode.setParent(queueHead);
						newNode.setAction(actionOnState);
						pq.offer(newNode);
					}
				}
			}
		}
	}

}
