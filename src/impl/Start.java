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

		Long t1, t2;
		
		State randomState = HeuristicSolverUtility.createRandom(4);
		
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		
		t1 = System.currentTimeMillis();
		SMHA smha = new SMHA();
		smha.SMHAstar(randomState);
		t2 = System.currentTimeMillis();
		System.out.println("Time for SMHA*"+(t2-t1));
		
//		t1 = System.currentTimeMillis();
//		AStar.solveUsingAStar(randomState);
//		t2 = System.currentTimeMillis();
//		System.out.println("Time for A*"+(t2-t1));
//		ClearQueues.clear();
//
//		t1 = System.currentTimeMillis();
//		IMHA.IMHAStar(randomState);
//		t2 = System.currentTimeMillis();
//		System.out.println("Time for IMHA*"+(t2-t1));
		
		t1 = System.currentTimeMillis();
		ParallelSMHAStar parallelSMHAStar = new ParallelSMHAStar(randomState);
		t2 = System.currentTimeMillis();
		System.out.println("Time for parallel SMHA*"+(t2-t1));
		System.out.println("Time spent waiting:"+parallelSMHAStar.timeSpentWaiting);
		
	}
	
}
