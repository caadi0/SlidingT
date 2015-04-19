package algorithms;

import constants.Constants;
import model.State;
import model.State.CellLocation;

public class EuclideanDistance {

	public static Double calculate(State s) {

		CellLocation location = s.getPresentLocation();
		int xPos = location.getColumnIndex();
		int yPos = location.getRowIndex();

		int distance = Double.valueOf((Math.pow(Constants.gridMaxXAxis
				- xPos, 2.0)
				+ Math.pow(Constants.gridMaxYAxis - yPos, 2.0))).intValue();
		
		return Math.sqrt(distance);
		
	}

}
