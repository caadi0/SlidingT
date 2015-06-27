//package impl;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Executors;
//import java.util.concurrent.PriorityBlockingQueue;
//
//import model.Action;
//import model.AtomicNode;
//import model.State;
//import model.StateConstants;
//import queues.SynchronisedAnchorQueue;
//import queues.SynchronisedInadmissibleQueue;
//import algorithms.ManhattanDistance;
//
//import com.google.common.util.concurrent.FutureCallback;
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.common.util.concurrent.ListeningExecutorService;
//import com.google.common.util.concurrent.MoreExecutors;
//
//import constants.Constants;
//
////possible ways or reducing running time:
//
////do not synchronise nodes while creation, i.e. use SMHA logic instead of parallelSMHA logic
//
//public class ATrialParallel {
//	
//	
//	PriorityBlockingQueue<AtomicNode> anchorQueue = null;
//	List<PriorityBlockingQueue<AtomicNode>> queueList = null;
//	AtomicNode startNode = null;
//	AtomicNode goalNode = null;
//	ListeningExecutorService executorService = null;
//	Boolean wassWaiting = false;
//	Long startTime = null;
//	Long endTime = null; 
//	static Integer counter = 0;
//
//ATrialParallel()
//{
//	startTime = System.currentTimeMillis();
//	executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
//	initialise();
//	
//	
////	obtainStateForExpansion(waitingExpansionList.get(0));
//	
//}
//
//private void initialise()
//{
//	anchorQueue = SynchronisedAnchorQueue.createQueue();
//	queueList = new ArrayList<PriorityBlockingQueue<AtomicNode>>();
//	for(int i = 1; i<= Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
//	{
//		PriorityBlockingQueue<AtomicNode> inadmissibleQ = SynchronisedInadmissibleQueue.createQueue(i);
//		queueList.add(inadmissibleQ);
//	}
//	
//	State randomState = HeuristicSolverUtility.createRandom(3);
//	startNode = createAtomicNode(randomState, Constants.w1);
//	startNode.setCost(0);
//	
//	State goalState = HeuristicSolverUtility.generateGoalState(3);
//	goalNode = createAtomicNode(goalState, Constants.w1);
//	
//	anchorQueue.add(startNode);
//	while(true){
//		
//		while(anchorQueue.isEmpty()){
//			System.out.println("empty"+counter++);
//		}
//
//		for(PriorityBlockingQueue<AtomicNode> q: queueList)
//		{
//			if(!anchorQueue.isEmpty())
//			{
//				q.add(startNode);
//				obtainStateForExpansion(q);
//			}
//		}
//	}
//}
//
//private void obtainStateForExpansion(PriorityBlockingQueue<AtomicNode> q)
//{
//	if(q == null)
//		return;
//	final Integer h = queueList.indexOf(q) + 1;
//	//obtain state for expansion and remove from other queues to avoid re-expansion.
//	AtomicNode anchorNode = anchorQueue.peek();
//	AtomicNode inadmissibleNode = q.peek();
//	Boolean result = getExpandAnchor(anchorNode, inadmissibleNode, h);
//	AtomicNode nodeSelectedForExpansion = null;
//	if(result)
//	{
//		nodeSelectedForExpansion = anchorNode;
//		nodeSelectedForExpansion.setExpandedBY(0);
//	}
//	else
//	{
//		nodeSelectedForExpansion = inadmissibleNode;
//		nodeSelectedForExpansion.setExpandedBY(h);
//	}
//	
//	anchorQueue.remove(nodeSelectedForExpansion);
//	for(PriorityBlockingQueue<AtomicNode> queue: queueList)
//	{
//		queue.remove(nodeSelectedForExpansion);
//	}
//	
//	final AtomicNode expansionNode = nodeSelectedForExpansion;
//	
//	if(goalNode.getCost() <= expansionNode.getCost())
//	{
////		pathLength = HeuristicSolverUtility.printPathLength(nGoal);
////		System.out.println("path length using SMHA is :"+pathLength);
//		System.out.println("path found");
//		executorService.shutdownNow();
//		endTime = System.currentTimeMillis();
//		System.out.println("time taken is: "+(endTime-startTime));
//		return;
//	}
//	
//	executorService.submit(new Callable<Integer>() {
//
//		@Override
//		public Integer call() throws Exception {
//			obtainNeighbouringStates(expansionNode);
//			return h-1;
//		}
//	});
//}
//
//private void obtainNeighbouringStates(AtomicNode toBeExpanded)
//{
//	State state = toBeExpanded.getState();
//	List<Action> listOfPossibleActions = state.getPossibleActions();
//	Iterator<Action> actIter = listOfPossibleActions.iterator();
//	while(actIter.hasNext()) {
//		Action actionOnState = actIter.next();
//		State newState = actionOnState.applyTo(state);
//		AtomicNode atomicNode = createAtomicNode(newState, Constants.w1);
//		int initcost = atomicNode.getCost();
//		int exninitcost = toBeExpanded.getCost();
//		while(initcost > exninitcost+1)
//		{
//			if(atomicNode.getAtomicCOst().compareAndSet(initcost, toBeExpanded.getAtomicCOst().get()+1))
//			{
//				atomicNode.setParent(toBeExpanded);
//				break;
//			}
//			initcost = atomicNode.getCost();
//			exninitcost = toBeExpanded.getCost();
//		}
//		
//		if(!atomicNode.getExpandedBY().equals(0))
//		{
//			anchorQueue.remove(atomicNode); // is this really necessary, 
//			anchorQueue.add(atomicNode);
//			if(atomicNode.getExpandedBY() < 0)
//			{
//				for(PriorityBlockingQueue<AtomicNode> queue: queueList) // add optimisation condition here
//				{
//					queue.remove(atomicNode);
//					queue.add(atomicNode);
//				}
//			}
//		}
//	}
//}
//
//private AtomicNode createAtomicNode(State state, Double weight)
//{
//	if(StateConstants.AtomicNodeMap.get(state.hashCode()) != null)
//		return StateConstants.AtomicNodeMap.get(state.hashCode());
//	else
//	{
//		AtomicNode atomicNode = new AtomicNode(state, weight);
//	
//		StateConstants.AtomicNodeMap.putIfAbsent(state.hashCode(), atomicNode);
//		return StateConstants.AtomicNodeMap.get(state.hashCode());
//	}
//}
//
//
//
//private Boolean getExpandAnchor(AtomicNode anchorNode, AtomicNode inadmissibleNode, int heuristic)
//{
//	
//	Double anchorKey = anchorKey(anchorNode);
//	Double inadmissibleNodeKey = inadmissibleNodeKey(inadmissibleNode, heuristic);
//	
//	if(anchorKey > inadmissibleNodeKey)
//	{
//		return false;
//	}
//	else
//		return true;
//}
//
//private Double anchorKey(AtomicNode anchor)
//{
//	return (anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()));
//}
//
//private Double inadmissibleNodeKey(AtomicNode inadmissible, int heuristic)
//{
//	return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
//			(heuristic, inadmissible.getState());
//}
//
//public static void main(String args[])
//{
//	new ATrialParallel();
//}
//
//}