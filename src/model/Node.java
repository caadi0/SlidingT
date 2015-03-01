package model;

public class Node {
	
	private State state;
	//private Move move;
	private int cost = 2147483647;
	private Node parent = null;
	private Action nextAction;
	private Double heuristicCost;
	private Double weight;
	
	public Node() {}
	
	/**
	 * @param state
	 * @param weight - Weight for calculating Key Value
	 */
	public Node(State state , Double weight) {
		this.state = state;
		this.weight = weight;
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
	
	public void setCost(int cost)
	{
		this.cost = cost;
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

	public Double getKey() {
		return (getCost() + this.weight * heuristicCost);
	}

	public void setHeuristicCost(Double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

}