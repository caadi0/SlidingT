package impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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

public class ParallelSMHAWithExpansionDelay {
	
	PriorityBlockingQueue<AtomicNode> anchorQueue = null;
	List<PriorityBlockingQueue<AtomicNode>> queueList = null;
	AtomicNode startNode = null;
	AtomicNode goalNode = null;
	List<AtomicNode> randomGoalNodes = null;
	ListeningExecutorService executorService = null;
	Long startTime = null;
	Long endTime = null; 
	private Boolean synchronisedBlockExecuting = false;
	private Integer currentHeuristic = 0;
	private Long totalTimeForObtainingStates = 0l;
	private Long totalExpansionTime = 0l;
	Boolean var = false;
	Boolean timedOut = false;
	PrintWriter printWriter = null;
	Integer expansionCount = 0;
//	Timer timer = null;

ParallelSMHAWithExpansionDelay(State randomState, PrintWriter out)  
{
	printWriter = out;
	Timer timer = new Timer();
	timer.schedule(new TimerTask() {
		
		@Override
		public void run() {
			timedOut = true;
			printWriter.println("parallel SMHA* with expansion delay timed out");
			printWriter.println("total expansion count: "+expansionCount);
			printWriter.flush();
			System.exit(0);
		}
	}, 600000);
	startTime = System.currentTimeMillis();
	initialise(randomState);
	
	executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8));
	executorService.submit(new Callable<Void>() {

		@Override
		public Void call() throws Exception {
			try {
				obtainStateForExpansion();
			} catch (Exception e) {
				e.printStackTrace();
				executorService.shutdown();
			}
			return null;
		}
	});
}

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
	
	State goalState = HeuristicSolverUtility.generateGoalState(Constants.dimension);
	printWriter.println("goal state is: "+goalState.hashCode());
	goalNode = createAtomicNode(goalState, Constants.w1);
	printNeighbours(goalNode);
	
	addToAnchor(startNode);
	
	createNewGoalStates();
	int i=0;
	for(PriorityBlockingQueue<AtomicNode> q: queueList)
	{
		i++;
		addToRandom(startNode, i, q);
	}
}

private void createNewGoalStates() {
	randomGoalNodes = new ArrayList<AtomicNode>();
	for(int i=1;i<=Constants.NumberOfInadmissibleHeuristicsForSMHAStar;i++) {
		State state = HeuristicSolverUtility.createRandom(Constants.dimension, Constants.randomisationFactor);
		AtomicNode randomGoalNode = createAtomicNode(state, Constants.w1);
		randomGoalNodes.add(randomGoalNode);
	}
}
//synchronized 
private void obtainStateForExpansion() throws Exception
{
//	try {
		while(!anchorQueue.isEmpty()) {
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
		synchronized(ParallelSMHAWithExpansionDelay.this){
			int tmpvar = anchorQueue.size();
//		printWriter.println("anchor size queue is: "+tmpvar);
		AtomicNode anchorNode = anchorQueue.peek();
		while(anchorNode != null && (anchorNode.getExpandedByAnchor() || anchorNode.getExpandedByInadmissible()))
		{
			anchorQueue.remove();
			anchorNode = anchorQueue.peek();
		}
				
		AtomicNode inadmissibleNode = queueList.get(currentHeuristic-1).peek();
		while(inadmissibleNode != null && (inadmissibleNode.getExpandedByAnchor() || inadmissibleNode.getExpandedByInadmissible()))
		{
			queueList.get(currentHeuristic-1).remove();
			inadmissibleNode = queueList.get(currentHeuristic-1).peek();
		}
		
		if(anchorNode == null && inadmissibleNode == null )
		{
			synchronisedBlockExecuting = false;
			return;
		}
		result = getExpandAnchor(anchorNode, inadmissibleNode, h);
		
		if(result)
		{
			anchorQueue.remove();
			nodeSelectedForExpansion = anchorNode;
			nodeSelectedForExpansion.setExpandedByAnchor(true);
			printWriter.println(nodeSelectedForExpansion.hashCode());
			expansionCount++;

		}
		else
		{
			queueList.get(currentHeuristic-1).remove();
			nodeSelectedForExpansion = inadmissibleNode;
			nodeSelectedForExpansion.setExpandedByInadmissible(true);
			printWriter.println(nodeSelectedForExpansion.hashCode()+" heuristic: "+h);
			expansionCount++;
		}
		
		// removal handled later
//		anchorQueue.remove(nodeSelectedForExpansion);
//		for(PriorityBlockingQueue<AtomicNode> queue: queueList)
//		{
//			queue.remove(nodeSelectedForExpansion);
//		}
		
		
		}
		
		final AtomicNode expansionNode = nodeSelectedForExpansion;
		
		
		if(result)
		{
			testTerminationCondition(anchorKey(expansionNode));
		}
		else if(!result)
		{
			testTerminationCondition(inadmissibleNodeKey(expansionNode, h));
		}
		if(timedOut)
//			return;
			throw new Exception("terminate");
		var = false;
//		System.out.println("before listenable future");
		ListenableFuture<Integer> listenableFuture = executorService.submit(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				obtainNeighbouringStates(expansionNode);
				var = true;
				return h-1;
			}
		});
		
//		if(var)
//			System.out.println("synchronous");
		Futures.addCallback(listenableFuture, new FutureCallback<Integer>() {
			  public void onSuccess(Integer index) {
			    if(!synchronisedBlockExecuting && !anchorQueue.isEmpty())
			    {
//			    	System.out.println("anchor queue repopulated");
			    	
			    		try {
							obtainStateForExpansion();
						} catch (Exception e) {
							executorService.shutdown();
						}
			    }
			  }
			  public void onFailure(Throwable thrown) {
			    System.out.println("failure");
			  }
			}, executorService);
		Long timediff = System.nanoTime()-beg;
//		System.out.println("time taken for obtaining state : "+(timediff));
		totalTimeForObtainingStates+= timediff;
		}
//		System.out.println("anchor queue empty");
		synchronisedBlockExecuting = false;
//	if(!anchorQueue.isEmpty())
//		obtainStateForExpansion();
//	else
//	{
//		System.out.println("anchor queue empty");
//		synchronisedBlockExecuting = false;
//	}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
	
}

private void testTerminationCondition(Double key) {
	
	Boolean result = false;
	if(goalNode.getCost() <= key)
		result = true;
	for(AtomicNode goalNode: randomGoalNodes) {
		if(goalNode.getCost() + Constants.randomisationFactor <= key)
			result = true;
	}
	
	if(result)
	{
		printWriter.println("path found");
//		executorService.shutdownNow();
		endTime = System.currentTimeMillis();
		printWriter.println("time taken is for parallel SMHA* : "+(endTime-startTime));

		printWriter.println("total expansion time: "+totalExpansionTime);
		printWriter.println("total state obtaining time: "+totalTimeForObtainingStates);
		printWriter.println("total expansion count: "+expansionCount);
		printWriter.flush();

		System.exit(0);
	}
}

private void obtainNeighbouringStates(AtomicNode toBeExpanded) throws InterruptedException
{
	Long beg = System.nanoTime();
	State state = toBeExpanded.getState();
	List<Action> listOfPossibleActions = state.getPossibleActions();
	Iterator<Action> actIter = listOfPossibleActions.iterator();
	while(actIter.hasNext()) {
		Action actionOnState = actIter.next();
		State newState = actionOnState.applyTo(state);
		Long time = System.nanoTime();
		AtomicNode atomicNode = createAtomicNode(newState, Constants.w1);
		System.out.println("AtomicNode creation time is : "+(System.nanoTime() - time));
		int initcost = atomicNode.getCost();
		int exninitcost = toBeExpanded.getCost();
		time = System.nanoTime();
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
		System.out.println("Cost updation time is : "+(System.nanoTime() - time));
		if(!atomicNode.getExpandedByAnchor())
		{
			synchronized(atomicNode)
			{
				time = System.nanoTime();
				addToAnchor(atomicNode);
				System.out.println("Time taken to add to anchor : "+(System.nanoTime() - time));
				if(!atomicNode.getExpandedByInadmissible())
				{
					time = System.nanoTime();

					for(PriorityBlockingQueue<AtomicNode> queue: queueList) // add optimisation condition here
					{
						time = System.nanoTime();
						int heuristic = queueList.indexOf(queue) + 1;
						System.out.println("heuristic computation time: "+(System.nanoTime() - time));
//						if((inadmissibleNodeKey(atomicNode, heuristic) <= 
//								Constants.w2*anchorKey(atomicNode)))
//						{
						time = System.nanoTime();
							addToRandom(atomicNode, heuristic, queue);
						System.out.println("time to add to one random queue : "+(System.nanoTime() - time));
//						}
					}
					
					System.out.println("Time taken to add to randoms : "+(System.nanoTime() - time));
				}
			}
		}
	}
//	System.out.println("out of obtain neighbours");
	Long timediff = System.nanoTime() - beg;
//	if(anchorQueue.size() > 1000)
//	Thread.sleep(3);
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
//		printWriter.println("anchorKey : "+anchorKey);
//		printWriter.println("inadmissibleNodeKey : "+inadmissibleNodeKey);
		if(Constants.w2*anchorKey >= inadmissibleNodeKey)
		{
			return false;
		}
		else
			return true;
	
}

private void addToAnchor(AtomicNode atomicNode)
{
	if(atomicNode.insertedIntoQueues[0] == 0)
	{
		anchorQueue.offer(atomicNode);
		atomicNode.insertedIntoQueues[0] = 1;
	}
}

private void addToRandom(AtomicNode atomicNode, int heuristic, PriorityBlockingQueue<AtomicNode> queue)
{
	if(atomicNode.insertedIntoQueues[heuristic] == 0)
	{
		queue.offer(atomicNode);
		atomicNode.insertedIntoQueues[heuristic] = 1;
	}
}

private Double anchorKey(AtomicNode anchor)
{
	return (anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()));
}

private Double inadmissibleNodeKey(AtomicNode inadmissible, int heuristic)
{
	return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
			(heuristic, inadmissible.getState(), randomGoalNodes.get(heuristic-1).getState());
}

public void printNeighbours(AtomicNode atomicNode) {
	printWriter.println("neighbours are :");
	State state = atomicNode.getState();
	List<Action> listOfPossibleActions = state.getPossibleActions();
	Iterator<Action> actIter = listOfPossibleActions.iterator();
	while(actIter.hasNext()) {
		Action actionOnState = actIter.next();
		State newState = actionOnState.applyTo(state);
		printWriter.println(newState.hashCode());
	}
}

public static void main(String args[]) throws IOException
{
	PrintWriter out =  new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Shipra\\Desktop\\outputTmp.txt", false)));
//	System.out.println("start");
//	System.setOut(new PrintStream("C:\\Users\\Shipra\\Desktop\\outputTmp.txt"));
	
	State randomState = HeuristicSolverUtility.createRandom(Constants.dimension);
	System.out.println(HeuristicSolverUtility.isStateSolvable(randomState));
	try {
		new ParallelSMHAWithExpansionDelay(randomState, out);
	} catch (Exception e) {
		System.exit(0);
		System.out.println(e);
		e.printStackTrace();
	}
}



}