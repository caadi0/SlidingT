package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.Action;
import model.SynchronisedNode;
import model.State;
import model.StateConstants;
import queues.AnchorQ;
import queues.Comparators;
import queues.InadmissibleHeuristicQ;
import algorithms.ManhattanDistance;
import constants.Constants;

public class ParallelSMHAStar {
	
	private HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private SynchronisedNode nGoal = null;
	private HashMap<Integer, Thread> threadPool= null;
//	private int numberOfRunningThreads = 0;
	private int heauristicCounter = 1;
	private int pathLength = 0;
	private Boolean waitedOnce = false;
	public Long timeSpentWaiting = 0l;
	
	
	public ParallelSMHAStar(State randomStartState)
	{
		SynchronisedNode nStart = createSynchronisedNode(randomStartState, Constants.w1);
		nStart.setCost(0);
		
		State goalState = HeuristicSolverUtility.generateGoalState(4);
//		System.out.println("Goal State");
//		HeuristicSolverUtility.printState(goalState);
		nGoal = createSynchronisedNode(goalState, Constants.w1);
		AnchorQ<SynchronisedNode> anchorQ = new AnchorQ<SynchronisedNode>(10000, new Comparators.AnchorHeuristicComparator());
//		AnchorQueue<SynchronisedNode> anchorQ = (AnchorQueue<SynchronisedNode>) AnchorQueue.createQueue();
		anchorQ.addOverriden(nStart);	
		
		List<InadmissibleHeuristicQ<SynchronisedNode>> inadmissibleQList = new ArrayList<InadmissibleHeuristicQ<SynchronisedNode>>();
		
		for(int i=0; i<Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
		{
			InadmissibleHeuristicQ<SynchronisedNode> prq = new InadmissibleHeuristicQ<>(10000, new Comparators.InadmissibleHeuristicComparator(i+1));
			prq.addOverriden(nStart);
			inadmissibleQList.add(prq);
		}
		
		visited.put(nStart.hashCode(), true);
		
		threadPool = new HashMap<Integer, Thread>();
		while(true)
		{
			while(threadPool.size() < Constants.NumberOfThreads)
			{
				int startValue = heauristicCounter;
				Boolean anchorStateExpanded = false;
//				pq.peek().readLock().lock();
				while(anchorQ.peekOverriden() != null && expandAnchor(anchorQ.peekOverriden(),
						inadmissibleQList.get(heauristicCounter-1).peekOverriden(), heauristicCounter))
				{
					heauristicCounter = (heauristicCounter%Constants.NumberOfInadmissibleHeuristicsForSMHAStar)+1;
					if(startValue == heauristicCounter)
					{
						if(nGoal.getCost() <= anchorQ.peekOverriden().getCost())
						{
							pathLength = HeuristicSolverUtility.printPathLength(nGoal);
							System.out.println("path length using SMHA is :"+pathLength);
							
							return;
						}
						SynchronisedNode selected = anchorQ.peekOverriden();
						expandedByAnchor.put(selected.hashCode(), true);
//						HeuristicSolverUtility.printState(selected.getState());
						Thread thread = new Thread(new ThreadSMHA(0, anchorQ, inadmissibleQList, selected));
						thread.start();
						
						threadPool.put(0, thread);
						for(InadmissibleHeuristicQ<SynchronisedNode> p: inadmissibleQList)
						{
							p.removeOverriden(selected);
							
						}
						anchorQ.removeOverriden(selected);
						
						anchorStateExpanded = true;
						break;
					}
					
					// add logic here
//					while(!inadmissibleQList.get(heauristicCounter).peekOverriden().readLock().tryLock())
//					{
//						heauristicCounter = (heauristicCounter%Constants.NumberOfInadmissibleHeuristicsForSMHAStar)+1;
//					}
//					inadmissibleQList.get(heauristicCounter).peekOverriden().readLock().unlock();
					
				}
				if(anchorQ.peekOverriden() == null)
				{
					try {
						if(waitedOnce)
						{
							System.out.println("anchor queue empty");
							return;
						}
					    Thread.sleep(50);                 //1000 milliseconds is one second.
					    timeSpentWaiting+=50;
					    waitedOnce = true;
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
				else 
				{
					
					waitedOnce = false;
					if(!anchorStateExpanded)
					{
						if(nGoal.getCost() <= inadmissibleQList.get(heauristicCounter-1).peekOverriden().getCost())
						{
							pathLength = HeuristicSolverUtility.printPathLength(nGoal);
							System.out.println("path length using SMHA is :"+pathLength);
							
							return;
						}
						SynchronisedNode selected = inadmissibleQList.get(heauristicCounter-1).peekOverriden();
						expandedByInadmissible.put(selected.hashCode(), true);
//						HeuristicSolverUtility.printState(selected.getState());
						Thread thread = new Thread(new ThreadSMHA(heauristicCounter, anchorQ, inadmissibleQList, selected));
						thread.start();
						
						threadPool.put(heauristicCounter, thread);
						
						anchorQ.removeOverriden(selected);
						for(InadmissibleHeuristicQ<SynchronisedNode> p: inadmissibleQList)
						{
							p.removeOverriden(selected);
						}
					}
				}
				
//				if(!firstTime)
//				{
//					try {
//					    Thread.sleep(1000);                 //1000 milliseconds is one second.
//					    firstTime = true;
//					} catch(InterruptedException ex) {
//					    Thread.currentThread().interrupt();
//					}
//				}
			}
		}
	}

	
	private Boolean expandAnchor(SynchronisedNode anchor, SynchronisedNode inadmissible, int heuristic)
	{
		if(inadmissible == null)
			return true;
		
		Boolean result = false;
		
		Double minKeyAnchor = anchorKey(anchor);
		Double minKeyInadmissible = inadmissibleSynchronisedNodeKey(inadmissible, heuristic);
		if(minKeyInadmissible <= Constants.w2*minKeyAnchor)
		{
			result = false;
		}
		else
		{
			result = true;
		}
		return result;
	}
	
	private Double anchorKey(SynchronisedNode anchor)
	{
		return (anchor.getCost() + Constants.w1* ManhattanDistance.calculate(anchor.getState()));
	}
	
	private Double inadmissibleSynchronisedNodeKey(SynchronisedNode inadmissible, int heuristic)
	{
		return inadmissible.getCost() +Constants.w1*RandomHeuristicGenerator.generateRandomHeuristic
				(heuristic, inadmissible.getState());
	}
	
	private SynchronisedNode createSynchronisedNode(State state, Double weight)
	{
		if(StateConstants.SynchronisedNodeMap.get(state.hashCode()) != null)
		{
			return StateConstants.SynchronisedNodeMap.get(state.hashCode());
		}
		else
		{
			SynchronisedNode SynchronisedNode = new SynchronisedNode(state, weight);
			StateConstants.SynchronisedNodeMap.put(state.hashCode(), SynchronisedNode);
			return SynchronisedNode;
		}
	}
	
	public static void main(String args[])
	{
		Long t1, t2;
		State randomState = HeuristicSolverUtility.createRandom(3);
		System.out.println("Random State");
		HeuristicSolverUtility.printState(randomState);
		t1 = System.currentTimeMillis();
		new ParallelSMHAStar(randomState);
		t2 = System.currentTimeMillis();
		System.out.println("Time taken is:"+(t2-t1));
	}
	
	public class ThreadSMHA implements Runnable{
		
		int heuristic = -1;
		AnchorQ<SynchronisedNode> anchorPQ;
		List<InadmissibleHeuristicQ<SynchronisedNode>> listPQ;
		SynchronisedNode toBeExpanded ;
		public ThreadSMHA(int heuristic, AnchorQ<SynchronisedNode> anchorPQ, List<InadmissibleHeuristicQ<SynchronisedNode>> listPQ,
				SynchronisedNode toBeExpanded)
		{
			this.heuristic = heuristic;
			this.anchorPQ = anchorPQ;
			this.listPQ = listPQ;
			this.toBeExpanded = toBeExpanded;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// expand SynchronisedNode, then:
			expandSynchronisedNode(anchorPQ, listPQ, toBeExpanded);
			threadPool.remove(heuristic);
		}

	}
	
	private void expandSynchronisedNode(AnchorQ<SynchronisedNode> anchorPQ, List<InadmissibleHeuristicQ<SynchronisedNode>> listPQ, SynchronisedNode toBeExpanded)
	{
		anchorPQ.removeOverriden(toBeExpanded);
		for(InadmissibleHeuristicQ<SynchronisedNode> pq: listPQ)
		{
			pq.removeOverriden(toBeExpanded);
		}
		
		State state = toBeExpanded.getState();
		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			SynchronisedNode newSynchronisedNode = createSynchronisedNode(newState, Constants.w1);

			visited.put(newSynchronisedNode.hashCode(), true);
			if(newSynchronisedNode.getCost() > toBeExpanded.getCost()+1)
			{
				newSynchronisedNode.setParent(toBeExpanded);
				if(expandedByAnchor.get(newSynchronisedNode.hashCode()) == null)
				{
					anchorPQ.removeOverriden(newSynchronisedNode);
					anchorPQ.addOverriden(newSynchronisedNode);
					if(expandedByInadmissible.get(newSynchronisedNode.hashCode()) == null)
					{
						addOrUpdateSynchronisedNodeToInadmissibleQueues(listPQ, newSynchronisedNode);
					}
				}
//				if(newSynchronisedNode.hashCode() == nGoal.hashCode())
//					nGoal = newSynchronisedNode;
			}
			
		}
	}
	
	private void addOrUpdateSynchronisedNodeToInadmissibleQueues(List<InadmissibleHeuristicQ<SynchronisedNode>> listPQ, SynchronisedNode toBeAdded)
	{
		int heuristic = 0;
		for(InadmissibleHeuristicQ<SynchronisedNode> pq: listPQ)
		{
			heuristic++;
			if(inadmissibleSynchronisedNodeKey(toBeAdded, heuristic) <= Constants.w2*anchorKey(toBeAdded))
			{
//				removeSynchronisedNodeForSimilarStateFromQueue(pq, toBeAdded);
				pq.removeOverriden(toBeAdded);
				pq.addOverriden(toBeAdded);
			}
		}
	}
	
	
}
