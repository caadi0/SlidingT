package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.State;
import algorithms.GenericLinearConflict;
import algorithms.GenericManhattanDistance;
import algorithms.LinearConflict;
import algorithms.ManhattanDistance;

public class RandomHeuristicGenerator {

	// Contains random numbers for each heuristic to multiply LC and MD
	private static HashMap<Integer, List<Double>> randomNumberMap = new HashMap<Integer, List<Double>>();

	public static Double generateRandomHeuristic(Integer heuristicID,
			State state) {
		List<Double> randNums = getRandomNumbersForHeuristic(heuristicID);

		if (heuristicID == 0)
			return (double) ManhattanDistance.calculate(state);
		return randNums.get(0) * ManhattanDistance.calculate(state)
				+ randNums.get(1) * LinearConflict.calculate(state);
	}
	
	public static Double generateRandomHeuristic(Integer heuristicID,
			State state, State goalState) {
		List<Double> randNums = getRandomNumbersForHeuristic(heuristicID);

		if (heuristicID == 0)
			return (double) GenericManhattanDistance.calculate(state, goalState);
		return randNums.get(0) * GenericManhattanDistance.calculate(state, goalState)
				+ randNums.get(1) * GenericLinearConflict.calculate(state, goalState);
	}

	
	private static List<Double> getRandomNumbersForHeuristic(Integer id) {
		List<Double> listOfRandomNumbers = new ArrayList<Double>();
		if(randomNumberMap.containsKey(id)) {
			listOfRandomNumbers = randomNumberMap.get(id);
		} else {
			listOfRandomNumbers.add(Math.random());
			listOfRandomNumbers.add(Math.random());
			randomNumberMap.put(id, listOfRandomNumbers);
		}
		return listOfRandomNumbers;
	}
}
