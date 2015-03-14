package algorithms;

import model.State;
import model.State.CellLocation;
import constants.Constants;

public class LongDistance {
	public static int calculate(State s) {

		CellLocation location = s.getPresentLocation();
		int xPos = location.getColumnIndex();
		int yPos = location.getRowIndex();

		int distance = Double.valueOf(Constants.gridMaxXAxis
				- xPos + Constants.gridMaxYAxis - yPos).intValue();
		return distance;
		
	}
}
