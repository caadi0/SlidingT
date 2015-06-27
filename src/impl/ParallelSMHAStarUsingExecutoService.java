package impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import model.Action;
import model.AtomicNode;
import model.State;
import model.StateConstants;
import queues.SynchronisedAnchorQueue;
import queues.SynchronisedInadmissibleQueue;
import algorithms.ManhattanDistance;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import constants.Constants;

//possible ways or reducing running time:

//do not synchronise nodes while creation, i.e. use SMHA logic instead of parallelSMHA logic

public class ParallelSMHAStarUsingExecutoService {
	
	
	PriorityBlockingQueue<AtomicNode> anchorQueue = null;
	List<PriorityBlockingQueue<AtomicNode>> queueList = null;
	AtomicNode startNode = null;
	AtomicNode goalNode = null;
	ListeningExecutorService executorService = null;
	Long startTime = null;
	Long endTime = null; 
	private Boolean synchronisedBlockExecuting = false;
	private Integer currentHeuristic = 0;
	private Long totalTimeForObtainingStates = 0l;
	private Long totalExpansionTime = 0l;
	Boolean var = false;


ParallelSMHAStarUsingExecutoService(State randomState)
{
	startTime = System.currentTimeMillis();
	initialise(randomState);
	
	executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4));
	executorService.submit(new Callable<Void>() {

		@Override
		public Void call() throws Exception {
			obtainStateForExpansion();
			return null;
		}
	});}

private void initialise(State randomState)
{
	anchorQueue = SynchronisedAnchorQueue.createQueue();
	queueList = new ArrayList<PriorityBlockingQueue<AtomicNode>>();
	for(int i = 1; i<= Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
	{
		PriorityBlockingQueue<AtomicNode> inadmissibleQ = SynchronisedInadmissibleQueue.createQueue(i);
		queueList.add(inadmissibleQ);
	}
	
	startNode = createAtomicNode(randomState, Constants.w1);
	startNode.setCost(0);
	
	State goalState = HeuristicSolverUtility.generateGoalState(3);
	goalNode = createAtomicNode(goalState, Constants.w1);
	
	anchorQueue.add(startNode);
	for(PriorityBlockingQueue<AtomicNode> q: queueList)
	{
		q.add(startNode);
	}
}

//synchronized 
private void obtainStateForExpansion()
{
//	System.out.println("In"+Thread.currentThread().getName());
	Long beg = System.nanoTime();
	synchronisedBlockExecuting = true;
	if(currentHeuristic == Constants.NumberOfInadmissibleHeuristicsForSMHAStar)
	{
		currentHeuristic = 1;
	}
	else
	{
		currentHeuristic++;
	}
	final Integer h = currentHeuristic;

	AtomicNode nodeSelectedForExpansion = null;
	Boolean result = null;
	synchronized(this){
	AtomicNode anchorNode = anchorQueue.peek();
	AtomicNode inadmissibleNode = queueList.get(currentHeuristic-1).peek();
	if(anchorNode == null && inadmissibleNode == null)
	{
		synchronisedBlockExecuting = false;
		System.out.println("time taken for obtaining state : "+(new Date().getTime()-beg));
//		System.out.println("out");
		return;
	}
	result = getExpandAnchor(anchorNode, inadmissibleNode, h);
	
	if(result)
	{
		nodeSelectedForExpansion = anchorNode;
		nodeSelectedForExpansion.setExpandedByAnchor(true);
	}
	else
	{
		nodeSelectedForExpansion = inadmissibleNode;
		nodeSelectedForExpansion.setExpandedByInadmissible(true);
	}
	
	anchorQueue.remove(nodeSelectedForExpansion);
	for(PriorityBlockingQueue<AtomicNode> queue: queueList)
	{
		queue.remove(nodeSelectedForExpansion);
	}
	}
	
	final AtomicNode expansionNode = nodeSelectedForExpansion;
	
	
	if(result && goalNode.getCost() <= anchorKey(expansionNode))
	{
		System.out.println("path found");
		executorService.shutdownNow();
		endTime = System.currentTimeMillis();
		System.out.println("time taken is for parallel SMHA* : "+(endTime-startTime));

		System.out.println("total expansion time: "+totalExpansionTime);
		System.out.println("total state obtaining time: "+totalTimeForObtainingStates);
//		System.out.println("out");

		return;
	}
	else if(!result && goalNode.getCost() <= inadmissibleNodeKey(expansionNode, h))
	{
		System.out.println("path found");
		executorService.shutdownNow();
		endTime = System.currentTimeMillis();
		System.out.println("time taken is for parallel SMHA* : "+(endTime-startTime));
		System.out.println("total expansion time: "+totalExpansionTime);
		System.out.println("total state obtaining time: "+totalTimeForObtainingStates);
//		System.out.println("out");

		return;
	}
	var = false;
	ListenableFuture<Integer> listenableFuture = executorService.submit(new Callable<Integer>() {

		@Override
		public Integer call() throws Exception {
			obtainNeighbouringStates(expansionNode);
			var = true;
			return h-1;
		}
	});
	
	if(var)
		System.out.println("synchronous");
	Futures.addCallback(listenableFuture, new FutureCallback<Integer>() {
		  public void onSuccess(Integer index) {
		    if(!synchronisedBlockExecuting && !anchorQueue.isEmpty())
		    		obtainStateForExpansion();
		  }
		  public void onFailure(Throwable thrown) {
		    System.out.println("failure");
		  }
		}, executorService);
	
//	System.out.println("time taken for obtaining state"+(System.nanoTime() - time));
//	System.out.println("out");
	Long timediff = System.nanoTime()-beg;
	System.out.println("time taken for obtaining state : "+(timediff));
	totalTimeForObtainingStates+= timediff;
	if(!anchorQueue.isEmpty())
		obtainStateForExpansion();
	else
		synchronisedBlockExecuting = false;
	
}

private void obtainNeighbouringStates(AtomicNode toBeExpanded)
{
//	System.out.println("in obtain neighbours");
	Long beg = System.nanoTime();
	State state = toBeExpanded.getState();
	List<Action> listOfPossibleActions = state.getPossibleActions();
	Iterator<Action> actIter = listOfPossibleActions.iterator();
	while(actIter.hasNext()) {
		Action actionOnState = actIter.next();
		State newState = actionOnState.applyTo(state);
		Long time = System.nanoTime();
		AtomicNode atomicNode = createAtomicNode(newState, Constants.w1);
//		System.out.println("time taken in new node creation: "+(System.nanoTime()-time));
		int initcost = atomicNode.getCost();
		int exninitcost = toBeExpanded.getCost();
		while(initcost > exninitcost+1)
		{
			if(atomicNode.getAtomicCOst().compareAndSet(initcost, toBeExpanded.getAtomicCOst().get()+1))
			{
				atomicNode.setParent(toBeExpanded);
				break;
			}
			initcost = atomicNode.getCost();
			exninitcost = toBeExpanded.getCost();
		}
		
		if(!atomicNode.getExpandedByAnchor())
		{
//			anchorQueue.remove(atomicNode); // is this really necessary,
			if(!anchorQueue.contains(atomicNode))
				anchorQueue.add(atomicNode);
			if(!atomicNode.getExpandedByInadmissible())
			{
				for(PriorityBlockingQueue<AtomicNode> queue: queueList) // add optimisation condition here
				{
//					queue.remove(atomicNode);
					if((inadmissibleNodeKey(atomicNode, queueList.indexOf(queue)) <= 
							Constants.w2*anchorKey(atomicNode)) &&
						!queue.contains(atomicNode))
						queue.add(atomicNode);
				}
			}
		}
	}
//	System.out.println("out of obtain neighbours");
	Long timediff = System.nanoTime() - beg;
	System.out.println("time taken for expansion: "+(timediff));
	totalExpansionTime+=timediff;
}

private AtomicNode createAtomicNode(State state, Double weight)
{
	if(StateConstants.AtomicNodeMap.get(state.hashCode()) != null)
	{
		return StateConstants.AtomicNodeMap.get(state.hashCode());
	}
	else
	{
		AtomicNode atomicNode = new AtomicNode(state, weight);
	
		StateConstants.AtomicNodeMap.putIfAbsent(state.hashCode(), atomicNode);
		return StateConstants.AtomicNodeMap.get(state.hashCode());
	}
	
}



private Boolean getExpandAnchor(AtomicNode anchorNode, AtomicNode inadmissibleNode, int heuristic)
{
	if(inadmissibleNode == null)
		return true;
	if(anchorNode == null)
		return false;
	Double anchorKey = anchorKey(anchorNode);
	Double inadmissibleNodeKey = inadmissibleNodeKey(inadmissibleNode, heuristic);
	if(anchorKey > inadmissibleNodeKey)
	{
		return false;
	}
	else
		return true;
	
}

private Double anchorKey(AtomicNode anchor)
{
	return (anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()));
}

private Double inadmissibleNodeKey(AtomicNode inadmissible, int heuristic)
{
	return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
			(heuristic, inadmissible.getState());
}

public static void main(String args[])
{
	System.out.println("start");
	new ParallelSMHAStarUsingExecutoService(HeuristicSolverUtility.createRandom(3));
}

}