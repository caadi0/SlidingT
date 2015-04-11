package impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import constants.Constants;
import queues.PQueue;
import algorithms.EuclideanDistance;
import model.Grid2D;
import model.GridAction;
import model.Node;
import model.State;
import model.State.CellLocation;

public class NavigateMap {
	
	HashMap<Node, Boolean> expandedPQ = new HashMap<Node, Boolean>();
	Integer pathLength = 0;
	
	public static void main(String[] args) {
		new NavigateMap();
		new GridSMHA();
	}
	
	public NavigateMap() {
		System.out.println("O = Free Path , X = Blocked Path");
		Grid2D.initializeMap();
		Grid2D.printMap();
		State startState = new State();
		startState.setPresentLocation(new CellLocation(0, 0));
		
		PriorityQueue<Node> pq = PQueue.createQueue();
		
		Node n1 = new Node(startState, 1.0);
		n1.setCost(0.0);
		n1.setHeuristicCost(Double.valueOf(""+EuclideanDistance.calculate(startState)));
		pq.add(n1);
		
		while (pq.isEmpty() == false) {

			Node queueHead = pq.remove();
			expandedPQ.put(queueHead, true);
			State queueHeadState = queueHead.getState();

			// If reached goal state
			if (queueHead.getState().getPresentLocation().getColumnIndex() == Constants.gridMaxYAxis -1 && 
					queueHead.getState().getPresentLocation().getRowIndex() == Constants.gridMaxXAxis -1) {
				setMapPath(queueHead);
				break;
			} else {
				List<GridAction> listOfPossibleActions = queueHeadState
						.getPossibleActionsForMapNavigation();
				Iterator<GridAction> actIter = listOfPossibleActions.iterator();
				while (actIter.hasNext()) {
					GridAction actionOnState = actIter.next();
					State newState = actionOnState.applyTo(queueHeadState);
					Node newNode = new Node(newState, 1.0);
					if (!expandedPQ.containsKey(newNode)) {
						newNode.setHeuristicCost((double) EuclideanDistance
								.calculate(newState));
						newNode.setParent(queueHead);
						if(actionOnState.getMove().toString().equalsIgnoreCase("UP") 
								|| actionOnState.getMove().toString().equalsIgnoreCase("DOWN")
								|| actionOnState.getMove().toString().equalsIgnoreCase("LEFT")
								|| actionOnState.getMove().toString().equalsIgnoreCase("RIGHT"))
							newNode.setCost(queueHead.getCost() + 1);
						else
							newNode.setCost(queueHead.getCost() + 1.414);
						pq.offer(newNode);
					}
				}
			}
		}
		System.out.println("");
		System.out.println("Path is :-");
		System.out.println("");
		Grid2D.printMap();
		System.out.println("number of expanded states is :-"+expandedPQ.size());
		System.out.println("Length is :-"+pathLength);
	}
	
	private void setMapPath(Node node) {
		if(node.getParent() != null)
		{
			pathLength++;
			setMapPath(node.getParent());
		}
		Grid2D.setMapValue(node.getState().getPresentLocation().getRowIndex(), 
				node.getState().getPresentLocation().getColumnIndex(), "-");
	}

}
