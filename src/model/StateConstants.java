package model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StateConstants {
	
//	public static HashMap<Integer, State> stateMap = new HashMap<Integer, State>();
	public static ConcurrentHashMap<Integer, AtomicNode> AtomicNodeMap = new ConcurrentHashMap<Integer, AtomicNode>() ;
	public static HashMap<Integer, SynchronisedNode> SynchronisedNodeMap = new HashMap<Integer, SynchronisedNode>();
	public static HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
}
