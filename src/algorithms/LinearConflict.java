package algorithms;

import model.State;

public class LinearConflict {

	public static int calculate(State s) {
		
		int heuristic = ManhattanDistance.calculate(s);
		
		heuristic += linearVerticalConflict(s);
		heuristic += linearHorizontalConflict(s);
		
		return heuristic;

	}

   private static int linearVerticalConflict(State s) {
		int dimension = s.getDimension();
		int linearConflict = 0;
		
		for (int row = 0; row < dimension; row++){
			byte max = -1;
			for (int column = 0;  column < dimension; column++){
				byte cellValue = s.getCellValue(row,column);
				//is tile in its goal row ?
				if (cellValue != 0 && (cellValue - 1) / dimension == row){
					if (cellValue > max){
						max = cellValue;
					}else {
						//linear conflict, one tile must move up or down to allow the other to pass by and then back up
						//add two moves to the manhattan distance
						linearConflict += 2;
					}
				}
				
			}
			
		}
		return linearConflict;
	}

   private static int linearHorizontalConflict(State s) {
		
		int dimension = s.getDimension();
		int linearConflict = 0;
		
		for (int column = 0; column < dimension; column++){
			byte max = -1;
			for (int row = 0;  row < dimension; row++){
				byte cellValue = s.getCellValue(row,column);
				//is tile in its goal row ?
				if (cellValue != 0 && cellValue % dimension == column + 1){
					if (cellValue > max){
						max = cellValue;
					}else {
						//linear conflict, one tile must move left or right to allow the other to pass by and then back up
						//add two moves to the manhattan distance
						linearConflict += 2;
					}
				}
				
			}
			
		}
		return linearConflict;
	}
	
	

}