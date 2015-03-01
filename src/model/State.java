package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State {

	public static class CellLocation {

		private int rowIndex;
		private int columnIndex;

		public CellLocation() {
		}

		public CellLocation(int rowIndex, int columnIndex) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		public int getRowIndex() {
			return rowIndex;
		}

		public int getColumnIndex() {
			return columnIndex;
		}
	}


	private byte[] allCells;
	private int hashCode = -1;
	private int dimension;
	
	public State() {
	}

	public State(byte[] cells) {
		// make a copy of the array
		allCells = new byte[cells.length];
		System.arraycopy(cells, 0, allCells, 0, cells.length);
		dimension = (int) Math.sqrt(cells.length);
		
	}

	public byte getCellValue(CellLocation cell) {
		return getCellValue(cell.rowIndex, cell.columnIndex);
	}
	
	public byte getCellValue(int rowIndex, int columnIndex){
		return allCells[rowIndex * dimension + columnIndex];
	}

	public void setCellValue(CellLocation cell, byte value) {
		allCells[cell.getRowIndex() * dimension + cell.getColumnIndex()] = value;
		reset();
	}

	public byte[] getAllCells() {
		return allCells;
	}

	private CellLocation getEmptyCellLocation() {
		for (int i = 0; i < allCells.length; i++) {
			if (allCells[i] == 0) {
				return new CellLocation(i/dimension, i % dimension);
			}

		}

		throw new RuntimeException("No Empty cell found");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			State s2 = (State)obj;
			return Arrays.equals(allCells, s2.allCells);
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (hashCode == -1){
			int result = 17;
			for (int i = 0; i < allCells.length; i++) {
					result = 31 * result + allCells[i];
			}
			hashCode = result;
		}
		
		return hashCode;
	}

	public List<Action> getPossibleActions() {
		List<Action> actions = new ArrayList<Action>();

		CellLocation emptyCell = getEmptyCellLocation();

		if (emptyCell.getRowIndex() > 0) {
			CellLocation upCell = new CellLocation(emptyCell.getRowIndex() - 1,
					emptyCell.getColumnIndex());
			actions.add(new Action(upCell, Move.DOWN));
		}

		if (emptyCell.getRowIndex() < dimension - 1) {
			CellLocation upCell = new CellLocation(emptyCell.getRowIndex() + 1,
					emptyCell.getColumnIndex());
			actions.add(new Action(upCell, Move.UP));
		}

		if (emptyCell.getColumnIndex() > 0) {
			CellLocation upCell = new CellLocation(emptyCell.getRowIndex(),
					emptyCell.getColumnIndex() - 1);
			actions.add(new Action(upCell, Move.RIGHT));
		}

		if (emptyCell.getColumnIndex() < dimension - 1) {
			CellLocation upCell = new CellLocation(emptyCell.getRowIndex(),
					emptyCell.getColumnIndex() + 1);
			actions.add(new Action(upCell, Move.LEFT));
		}

		return actions;
	}

	@Override
	public String toString() {
		return Arrays.toString(allCells);
	}

	private void reset(){
		hashCode = -1;
	}

	public int getDimension() {
		return dimension;
	}
}