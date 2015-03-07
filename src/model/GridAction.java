package model;

import model.State.CellLocation;

public class GridAction {
	
	private Move m;

	public GridAction(Move m) {
		this.m = m;
	}

	public Move getMove() {
		return m;
	}

	/**
	 * Apply this action to a state and return the new state
	 */
	public State applyTo(State s) {
		CellLocation presCellLocation = s.getPresentLocation();

		CellLocation nextCell = m.getNextCellLocation(presCellLocation);
		State newState = new State();
		newState.setPresentLocation(nextCell);
		
		return newState;
	}

	public boolean isInverse(Action a) {
		return a != null && m.getInverse() == a.getMove();
	}

}
