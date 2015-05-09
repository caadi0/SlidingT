package model;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronisedNode extends ReentrantReadWriteLock{
	
	private State state;
	//private Move move;
	private int cost = 214748364;
	private SynchronisedNode parent = null;
	private Action nextAction;
	private Double heuristicCost;
	private Double weight;
	
	public SynchronisedNode() {}
	
	/**
	 * @param state
	 * @param weight - Weight for calculating Key Value
	 */
	public SynchronisedNode(State state , Double weight) {
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
		if (obj instanceof SynchronisedNode){
			return state.equals(((SynchronisedNode)obj).state);
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
	
	public void setParent(SynchronisedNode parent) {
		this.parent = parent;
		setCost(parent.getCost() + 1);
	}
	
	public void setCost(int cost)
	{
//		this.writeLock().lock();
		this.cost = cost;
//		this.writeLock().unlock();
	}
	
	public SynchronisedNode getParent() {
		return parent;
	}
	
	public int getCost() {
		this.readLock().lock();
		int cValue = cost;
		this.readLock().unlock();
		return cValue;
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