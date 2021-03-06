package impl;

import java.io.FileOutputStream;
import java.io.PrintStream;

import queues.ClearQueues;
import algorithms.AStar;
import model.State;

public class Start {
	

	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws Exception 
	{
		
		Start.init();
	}
	
	private static void init() throws Exception
	{

		
		State randomState = HeuristicSolverUtility.createRandom(3);
		
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		SMHA smha = new SMHA();
		smha.SMHAstar(randomState);
		AStar.solveUsingAStar(randomState);
		ClearQueues.clear();
		IMHA.IMHAStar(randomState);
	}
	
}
