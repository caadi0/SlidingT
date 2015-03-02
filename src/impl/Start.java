package impl;

import java.io.FileOutputStream;
import java.io.PrintStream;

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
//		PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Aaditya\\Desktop\\output.txt"));
//		System.setOut(out);
		
		State randomState = HeuristicSolverUtility.createRandom(3);
		
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		SMHA smha = new SMHA();
		smha.SMHAstar(randomState);
		AStar.solveUsingAStar(randomState);
		IMHA.IMHAStar(randomState);
	}
	
}
