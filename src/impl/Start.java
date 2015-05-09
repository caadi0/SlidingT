package impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import queues.ClearQueues;
import algorithms.AStar;
import model.State;
import model.StateConstants;
import jxl.*; 
import jxl.write.*;

public class Start {
	

	public void solveHeuristic() {
		
	}

	public static void main(String[] args) throws Exception 
	{
		
		Start.init();
	}
	
	private static void init() throws Exception
	{
		WritableWorkbook  writableWorkbook = Workbook.createWorkbook(new File("output1.ods"));
		WritableSheet sheet = writableWorkbook.createSheet("first sheet", 0);
//		WritableSheet sheet = workbook.createSheet("First Sheet", 0);
		Label label = new Label(0, 0, "Serial SMHA*"); 
		sheet.addCell(label); 
//		sheet.getRows();
		Label label2 = new Label(1,0,"parallel SMHA*"); 
		sheet.addCell(label2);
		
		Long t1, t2;
		
		for(int i=0;i<1;i++)
		{
		StateConstants.nodeMap.clear();
		StateConstants.stateMap.clear();
		StateConstants.SynchronisedNodeMap.clear();
		State randomState = HeuristicSolverUtility.createRandom(3);
		
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		
		t1 = System.currentTimeMillis();
		SMHA smha = new SMHA();
		smha.SMHAstar(randomState);
		t2 = System.currentTimeMillis();
		System.out.println("Time for SMHA*"+(t2-t1));
		Label l1 = new Label(0, i+1, ""+(t2-t1));
		sheet.addCell(l1);
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
		StateConstants.stateMap.clear();
		StateConstants.stateMap.put(randomState.hashCode(), randomState);
		t1 = System.currentTimeMillis();
		java.lang.Boolean result = new ParallelSMHAStar(). ParallelSMHAStar(randomState);
		t2 = System.currentTimeMillis();
		if(result)
			System.out.println("Time for parallel SMHA*"+(t2-t1));
		else
			System.out.println("no results found");

//		System.out.println("Time spent waiting:"+parallelSMHAStar.timeSpentWaiting);
//		System.out.println("time in expansions:"+parallelSMHAStar.totalTimeInExpansions);
//		System.out.println("wait count is: "+parallelSMHAStar.waitCount);
		Label l2 = new Label(1,i+1, ""+(t2-t1));
		sheet.addCell(l2);
		}
		writableWorkbook.write();
		writableWorkbook.close();
	}

	
//	private static void init() throws Exception
//	{
//		Workbook workbook = Workbook.getWorkbook(new File("output1.ods"));
//		WritableWorkbook  writableWorkbook = Workbook.createWorkbook(new File("output1.ods"), workbook);
//		WritableSheet sheet = writableWorkbook.getSheet(0);
//		int rowCount = sheet.getRows();
////		WritableSheet sheet = workbook.createSheet("First Sheet", 0);
//		Label label = new Label(0, 0, "Serial SMHA*"); 
////		sheet.addCell(label); 
////		sheet.getRows();
//		Label label2 = new Label(1,0,"parallel SMHA*"); 
////		sheet.addCell(label2);
//		
//		Long t1, t2;
//		
//		for(int i=0;i<1;i++)
//		{
//		StateConstants.nodeMap.clear();
//		StateConstants.stateMap.clear();
//		StateConstants.SynchronisedNodeMap.clear();
//		State randomState = HeuristicSolverUtility.createRandom(3);
//		
//		System.out.println("Random State");
//		HeuristicSolverUtility.printState(randomState);
//		
//		t1 = System.currentTimeMillis();
//		SMHA smha = new SMHA();
//		smha.SMHAstar(randomState);
//		t2 = System.currentTimeMillis();
//		System.out.println("Time for SMHA*"+(t2-t1));
//		Label l1 = new Label(0, i+rowCount, ""+(t2-t1));
//		sheet.addCell(l1);
////		t1 = System.currentTimeMillis();
////		AStar.solveUsingAStar(randomState);
////		t2 = System.currentTimeMillis();
////		System.out.println("Time for A*"+(t2-t1));
////		ClearQueues.clear();
////
////		t1 = System.currentTimeMillis();
////		IMHA.IMHAStar(randomState);
////		t2 = System.currentTimeMillis();
////		System.out.println("Time for IMHA*"+(t2-t1));
//		StateConstants.stateMap.clear();
//		StateConstants.stateMap.put(randomState.hashCode(), randomState);
//		t1 = System.currentTimeMillis();
//		java.lang.Boolean result = new ParallelSMHAStar(). ParallelSMHAStar(randomState);
//		t2 = System.currentTimeMillis();
//		if(result)
//			System.out.println("Time for parallel SMHA*"+(t2-t1));
//		else
//			System.out.println("no results found");
//
////		System.out.println("Time spent waiting:"+parallelSMHAStar.timeSpentWaiting);
////		System.out.println("time in expansions:"+parallelSMHAStar.totalTimeInExpansions);
////		System.out.println("wait count is: "+parallelSMHAStar.waitCount);
//		Label l2 = new Label(1,i+rowCount, ""+(t2-t1));
//		sheet.addCell(l2);
//		}
//		writableWorkbook.write();
//		workbook.close();
//		writableWorkbook.close();
//	}
	
}
