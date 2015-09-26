package algorithms;

import model.State;

public class GenericManhattanDistance {

	public static int calculate(State s, State goal) {
		int dimension = s.getDimension();
		int[] destination = new int[dimension*dimension];
		
		//populate destination from goal
		int counter = 0;
		int heuristic = 0;
		for(int i=0;i<dimension;i++)
		{
			for(int j=0;j<dimension;j++)
			{
				destination[goal.getCellValue(i, j)] = counter;
				counter++;
			}
		}
		
		
		for(int i=0;i<dimension;i++) {
			for(int j=0;j<dimension;j++) {
				int value = destination[s.getCellValue(i, j)];
				int expectedRow = (value) / dimension;
				int expectedColumn = (value) % dimension;
				heuristic += Math.abs(i - expectedRow)
						+ Math.abs(j - expectedColumn);
			}
		}
		
		return heuristic;
	}
	
	public static void main(String args[]) {
		int dimension = 3;
		int[] source = new int[dimension*dimension];
		int[] destination = new int[dimension*dimension];
		int heuristic = 0;
		source[0] = 4;
		source[1] = 2;
		source[2] = 5;
		source[3] = 6;
		source[4] = 1;
		source[5] = 7;
		source[6] = 8;
		source[7] = 3;
		source[8] = 0;
		destination[0] = 8;
		destination[1] = 0;
		destination[2] = 1;
		destination[3] = 2;
		destination[4] = 3;
		destination[5] = 4;
		destination[6] = 5;
		destination[7] = 6;
		destination[8] = 7;
		
		for(int i=0;i<dimension;i++) {
			for(int j=0;j<dimension;j++) {
				
			}
		}
		
		System.out.println(heuristic);
	}
}
