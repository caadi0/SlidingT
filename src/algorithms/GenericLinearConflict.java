package algorithms;

import model.State;

public class GenericLinearConflict {

	public static int calculate(State s, State goal) {
		int dimension = s.getDimension();
		int[] source = new int[dimension*dimension];
		int[] destination = new int[dimension*dimension];
		
		//populate destination from goal
		int counter = 0;
		int heuristic = 0;
		for(int i=0;i<dimension;i++)
		{
			for(int j=0;j<dimension;j++)
			{
				destination[goal.getCellValue(i, j)] = counter;
				counter++;
			}
		}
		
		//populate source from s
		counter = 0;
		for(int i=0;i<dimension;i++)
		{
			for(int j=0;j<dimension;j++)
			{
				destination[counter] = s.getCellValue(i, j);
				counter++;
			}
		}
		
		for(int i=0;i<dimension*dimension;i++)
		{
			for(int j=i+1;j<dimension*dimension;j++)
			{
				if(destination[source[i]] > destination[source[j]])
					heuristic++;
			}
		}
		
		return heuristic;
	}
	
	public static void main(String args[]) {
		int dimension = 3;
		int[] source = new int[dimension*dimension];
		int[] destination = new int[dimension*dimension];
		int heuristic = 0;
		source[0] = 6;
		source[1] = 7;
		source[2] = 2;
		source[3] = 8;
		source[4] = 1;
		source[5] = 4;
		source[6] = 0;
		source[7] = 3;
		source[8] = 5;
		destination[0] = 8;
		destination[1] = 0;
		destination[2] = 1;
		destination[3] = 2;
		destination[4] = 3;
		destination[5] = 4;
		destination[6] = 5;
		destination[7] = 6;
		destination[8] = 7;
		
		for(int i=0;i<dimension*dimension;i++)
		{
			for(int j=i+1;j<dimension*dimension;j++)
			{
				if(destination[source[i]] > destination[source[j]])
					heuristic++;
			}
		}
		
		System.out.println(heuristic);
	}
}
