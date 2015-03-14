package model;

import constants.Constants;

public class Grid2D {
	
	private static String[][] map = new String[Constants.gridMaxXAxis][Constants.gridMaxYAxis];
	private static String[][] initialMap = new String[Constants.gridMaxXAxis][Constants.gridMaxYAxis];
	
	public static void initializeMap() {
		for(int i=0; i < Constants.gridMaxXAxis; i++){
	          for(int j=0; j < Constants.gridMaxYAxis; j++){
	               String value;
	               if(Math.random() > 0.32){
	                    value = "O";
	               }else{
	                    value = Constants.blocked;
	               }
	               map[i][j] = value;	 
	               initialMap[i][j] = value;
	          }
	     }
		// Initializing Source and Goal State
		map[0][0] = "O";
		map[Constants.gridMaxXAxis - 1][Constants.gridMaxYAxis - 1] = "O";
		initialMap[0][0] = "O";
		initialMap[Constants.gridMaxXAxis - 1][Constants.gridMaxYAxis - 1] = "O";
	}
	
	public static Boolean isCellBlocked(int x, int y) {
		if(map[x][y].equalsIgnoreCase("O"))
			return false;
		return true;
	}
	
	public static void setMapValue(int x, int y, String value) {
		map[x][y] = value;
	}
	
	public static void printMap() {
		for(int i=0; i < Constants.gridMaxXAxis; i++){
	          for(int j=0; j < Constants.gridMaxYAxis; j++){
	        	  System.out.format("%3s", map[i][j]);
	          }
	          System.out.println("");
		}
	}
	
	public static String[][] getMap() {
		return map;
	}
	
	public static void main(String[] args) {
		Grid2D.initializeMap();
		Grid2D.printMap();
	}
	
	public static void setMapToInitial()
	{
		for(int i=0; i < Constants.gridMaxXAxis; i++){
	          for(int j=0; j < Constants.gridMaxYAxis; j++){
	               map[i][j] = initialMap[i][j];	 
	          }
	     }
	}
}
