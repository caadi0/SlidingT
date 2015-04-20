package model;

import model.State.CellLocation;

public class Action {

	private Move m;
	private CellLocation cell;

	public Action() {
	}

	public Action(CellLocation cell, Move m) {
		this.m = m;
		this.cell = cell;
	}

	public CellLocation getCellLocation() {
		return cell;
	}

	public Move getMove() {
		return m;
	}

	/**
	 * Apply this action to a state and return the new state
	 */
	public State applyTo(State s) {
		byte value = s.getCellValue(cell);

		CellLocation nextCell = m.getNextCellLocation(cell);

		State newState = new State(s.getAllCells());
		newState.setCellValue(nextCell, value);
		newState.setCellValue(cell, (byte) 0);

		if(StateConstants.stateMap.get(newState.hashCode()) == null )
		{
			StateConstants.stateMap.put(newState.hashCode(), newState);
		}
		else
		{
			newState = StateConstants.stateMap.get(newState.hashCode());
		}
		return newState;
	}

	@Override
	public String toString() {
		return m + "(" + cell.getRowIndex() + "," + cell.getColumnIndex() + ")";
	}

	public boolean isInverse(Action a) {
		return a != null && m.getInverse() == a.getMove();
	}
}