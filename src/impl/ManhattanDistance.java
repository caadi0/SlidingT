package impl;

public class ManhattanDistance {

	protected static int calculate(State s) {
		int counter = 0;
		byte[] allCells = s.getAllCells();
		int dimension = s.getDimension();

		for (int i = 0; i < allCells.length; i++) {
			int value = allCells[i];
			if (value == 0) {
				continue;
			}

			int row = i / dimension;
			int column = i % dimension;
			int expectedRow = (value - 1) / dimension;
			int expectedColumn = (value - 1) % dimension;

			int difference = Math.abs(row - expectedRow)
					+ Math.abs(column - expectedColumn);
			counter += difference;

		}

		return counter;
	}

}