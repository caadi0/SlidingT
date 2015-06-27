package model;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class AtomicNode {
	
	private State state;
	//private Move move;
	private AtomicInteger cost = null; // helper methods remain to be verified
	private AtomicNode parent = null;
	private Action nextAction;
	private Double heuristicCost;
	private Double weight;
	private Boolean expandedByAnchor = false;
	private Boolean expandedByInadmissible = false;
	public AtomicNode() {}
	
	/**
	 * @param state
	 * @param weight - Weight for calculating Key Value
	 */
	public AtomicNode(State state , Double weight) {
		this.state = state;
		this.weight = weight;
		cost = new AtomicInteger(Integer.MAX_VALUE);
	}
	
	public State getState() {
		return state;
	}
	
	/**
	 * equality based on state
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AtomicNode){
			return state.equals(((AtomicNode)obj).state);
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
	
	public void setParent(AtomicNode parent) {
		this.parent = parent;
		setCost(parent.getCost() + 1);
	}
	
	public void setCost(int cost)
	{
		this.cost.set(cost);
	}
	
	public AtomicNode getParent() {
		return parent;
	}
	
	public int getCost() {
		return cost.get();
	}

	public AtomicInteger getAtomicCOst()
	{
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

	public Boolean getExpandedByAnchor() {
		return expandedByAnchor;
	}

	public void setExpandedByAnchor(Boolean expandedByAnchor) {
		this.expandedByAnchor = expandedByAnchor;
	}

	public Boolean getExpandedByInadmissible() {
		return expandedByInadmissible;
	}

	public void setExpandedByInadmissible(Boolean expandedByInadmissible) {
		this.expandedByInadmissible = expandedByInadmissible;
	}

}