package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.State;
import algorithms.EuclideanDistance;
import algorithms.LongDistance;

public class RandomHeuristicGenerator {

	// Contains random numbers for each heuristic to multiply LC and MD
	private static HashMap<Integer, List<Double>> randomNumberMap = new HashMap<Integer, List<Double>>();

	public static Double generateRandomHeuristic(Integer heuristicID,
			State state) {
		List<Double> randNums = getRandomNumbersForHeuristic(heuristicID);

		if (heuristicID == 0)
			return (double) EuclideanDistance.calculate(state);
		return randNums.get(0) * EuclideanDistance.calculate(state)
				+ randNums.get(1) * LongDistance.calculate(state);
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
