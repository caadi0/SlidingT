package model;

import model.State.CellLocation;

public enum Move {

	UP(0, -1), DOWN(0, 1), RIGHT(1, 0), LEFT(-1, 0), UP_LEFT(-1, -1), UP_RIGHT(1,-1), DOWN_RIGHT(1,1), DOWN_LEFT(-1,1);

	private int horizontalMove;
	private int verticalMove;

	private Move(int horizontal, int vertical) {
		this.horizontalMove = horizontal;
		this.verticalMove = vertical;
	}

	public int getHorizontalMove() {
		return horizontalMove;
	}

	public int getVerticalMove() {
		return verticalMove;
	}

	public CellLocation getNextCellLocation(CellLocation currentLocation) {
		return new CellLocation(getNextRow(currentLocation.getRowIndex()),
				getNextColumn(currentLocation.getColumnIndex()));
	}

	private int getNextRow(int currentRow) {
		return currentRow + verticalMove;
	}

	private int getNextColumn(int currentColumn) {
		return currentColumn + horizontalMove;
	}

	public Move getInverse() {
		switch (this) {
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP_LEFT:
			return DOWN_RIGHT;
		case DOWN_RIGHT:
			return UP_LEFT; 
		case UP_RIGHT:
			return DOWN_LEFT;
		case DOWN_LEFT:
			return UP_RIGHT;

		}
		return null;
	}
}