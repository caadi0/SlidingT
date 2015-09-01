package impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import model.State;

public class Start {
	
	static int count = 0;
	

	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws Exception 
	{
//		System.setOut(new PrintStream("C:\\Users\\Shipra\\Desktop\\output.txt"));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Shipra\\Desktop\\outputWithGreaterW2.txt", true)));
		out.println("\n");
		final State startState = HeuristicSolverUtility.createRandom(7);
		out.println("Start state is: "+startState.hashCode());
		new SMHA().SMHAstar(startState, out);
		new ParallelSMHAWithExpansionDelay(startState, out);
	}
	
//	private static void func()
//	{
//		count++;
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//			
//				final State startState = HeuristicSolverUtility.createRandom(10);
//				new SMHA().SMHAstar(startState);
//		//		new ParallelSMHAStarUsingExecutoService(startState);
//		//		new SerialSMHAWithArtificialDelay().SMHAstar(startState);
//		//		Thread.sleep(60000);
//				Timer t = new Timer();
//				t.schedule(new TimerTask() {
//					
//					@Override
//					public void run() {
//						StateConstants.AtomicNodeMap.clear();
//						new ParallelSMHAWithExpansionDelay(startState);
//						if(count < 100)
//						func();
//					}
//				}, 7000);
//				
//
//			}
//		}, 67000);
//		
//	}
//	
//	private static void init() throws FileNotFoundException, InterruptedException
//	{
//		System.setOut(new PrintStream("C:\\Users\\Shipra\\Desktop\\output.txt"));
//		final State startState = HeuristicSolverUtility.createRandom(10);
//		new SMHA().SMHAstar(startState);
//		new ParallelSMHAWithExpansionDelay(startState);
//	}

}
