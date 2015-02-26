package impl;

public class Node {
	
	private State state;
	//private Move move;
	private int cost = 0;
	private int heuristic = -1;
	private Node parent = null;
	private Action nextAction;
	
	public Node() {}
	
	public Node(State state/*, Move m*/) {
		this.state = state;
		//this.move = m;
	}
	
	public State getState() {
		return state;
	}

	
	/**
	 * equality based on state
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node){
			return state.equals(((Node)obj).state);
		}
		return false;
	}
	
	/**
	 * equality based on state
	 */
	@Override
	public int hashCode() {
		return state.hashCode();
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
		cost = parent.getCost() + 1;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public int getCost() {
		return cost;
	}

	public Action getAction() {
		return nextAction;
	}
	
	public void setAction(Action next){
		this.nextAction = next;
	}

	public int getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
		
	}

}