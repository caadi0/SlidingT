package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import constants.Constants;

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
	private CellLocation presentCellLocation;
	
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

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof State) {
//			State s2 = (State)obj;
//			return Arrays.equals(allCells, s2.allCells);
//		}
//
//		return false;
//	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			State s2 = (State)obj;
			if(getPresentLocation().getColumnIndex() == s2.getPresentLocation().getColumnIndex() &&
					getPresentLocation().getRowIndex() == s2.getPresentLocation().getRowIndex())
				return true;
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		if (hashCode == -1){
			hashCode = 10 * getPresentLocation().getRowIndex() + getPresentLocation().getColumnIndex();
		}
		
		return hashCode;
	}

//	@Override
//	public int hashCode() {
//		if (hashCode == -1){
//			int result = 17;
//			for (int i = 0; i < allCells.length; i++) {
//					result = 31 * result + allCells[i];
//			}
//			hashCode = result;
//		}
//		
//		return hashCode;
//	}

	public List<Action> getPossibleActionsForTilePuzzle() {
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
	
	public List<GridAction> getPossibleActionsForMapNavigation() {
		List<GridAction> actions = new ArrayList<GridAction>();

		CellLocation presentLocation = getPresentLocation();
		int xCordinate = presentLocation.getRowIndex();
		int yCordinate = presentLocation.getColumnIndex();
		
		if(presentLocation.rowIndex > 0) {
			if(!Grid2D.isCellBlocked(xCordinate - 1, yCordinate))
				actions.add(new GridAction(Move.UP));
		}
		if(presentLocation.rowIndex > 0 && presentLocation.columnIndex > 0) {
			if(!Grid2D.isCellBlocked(xCordinate - 1, yCordinate - 1))
				actions.add(new GridAction(Move.UP_LEFT));
		}
		if(presentLocation.rowIndex < Constants.gridMaxXAxis - 1) {
			if(!Grid2D.isCellBlocked(xCordinate + 1, yCordinate))
				actions.add(new GridAction(Move.DOWN));
		}
		if(presentLocation.rowIndex < Constants.gridMaxXAxis - 1 && presentLocation.columnIndex < Constants.gridMaxYAxis - 1) {
			if(!Grid2D.isCellBlocked(xCordinate + 1, yCordinate + 1))
				actions.add(new GridAction(Move.DOWN_RIGHT));
		}
		if(presentLocation.columnIndex > 0) {
			if(!Grid2D.isCellBlocked(xCordinate , yCordinate - 1))
				actions.add(new GridAction(Move.LEFT));
		}
		if(presentLocation.columnIndex < Constants.gridMaxYAxis - 1 ) {
			if(!Grid2D.isCellBlocked(xCordinate , yCordinate + 1))
				actions.add(new GridAction(Move.RIGHT));
		}
		if(presentLocation.rowIndex > 0 && presentLocation.columnIndex < Constants.gridMaxYAxis - 1) {
			if(!Grid2D.isCellBlocked(xCordinate - 1, yCordinate + 1))
				actions.add(new GridAction(Move.UP_RIGHT));
		}
		if(presentLocation.rowIndex < Constants.gridMaxXAxis - 1 && presentLocation.columnIndex > 0) {
			if(!Grid2D.isCellBlocked(xCordinate + 1, yCordinate - 1))
				actions.add(new GridAction(Move.DOWN_LEFT));
		}
		return actions;
	}
	
	public CellLocation getPresentLocation() {
		return this.presentCellLocation;
	}
	
	public void setPresentLocation(CellLocation location) {
		this.presentCellLocation = location;
	}

//	@Override
//	public String toString() {
//		return Arrays.toString(allCells);
//	}
	
	@Override
	public String toString() {
		return "( " + getPresentLocation().getRowIndex() + " , " + getPresentLocation().getColumnIndex()+" )";
	}

	private void reset(){
		hashCode = -1;
	}

	public int getDimension() {
		return dimension;
	}
}