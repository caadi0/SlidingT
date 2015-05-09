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
	
//	private HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByAnchor = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> expandedByInadmissible = new HashMap<Integer, Boolean>();
	private SynchronisedNode nGoal = null;
	private HashMap<Integer, Thread> threadPool= null;
//	private int numberOfRunningThreads = 0;
	private int heauristicCounter = 1;
	private int pathLength = 0;
	public Long timeSpentWaiting = 0l;
	public Long totalTimeInExpansions = 0l;
	public int waitCount = 0;
	
	
	public Boolean ParallelSMHAStar(State randomStartState)
	{
		SynchronisedNode nStart = createSynchronisedNode(randomStartState, Constants.w1, false);
		nStart.setCost(0);
		
		State goalState = HeuristicSolverUtility.generateGoalState(3);
		nGoal = createSynchronisedNode(goalState, Constants.w1, false);
		AnchorQ<SynchronisedNode> anchorQ = new AnchorQ<SynchronisedNode>(10000, new Comparators.AnchorHeuristicComparator());
		anchorQ.addOverriden(nStart);	// no need to obtain lock inside this method here
		
		List<InadmissibleHeuristicQ<SynchronisedNode>> inadmissibleQList = new ArrayList<InadmissibleHeuristicQ<SynchronisedNode>>();
		
		for(int i=0; i<Constants.NumberOfInadmissibleHeuristicsForSMHAStar; i++)
		{
			InadmissibleHeuristicQ<SynchronisedNode> prq = new InadmissibleHeuristicQ<>(10000, new Comparators.InadmissibleHeuristicComparator(i+1));
			prq.addOverriden(nStart);
			inadmissibleQList.add(prq); // no need to obtain lock inside this method here
		}
		
//		visited.put(nStart.hashCode(), true);
		
		threadPool = new HashMap<Integer, Thread>();
		while(true)
		{
			while(threadPool.size() < Constants.NumberOfThreads)
			{
				int startValue = ((heauristicCounter-2+Constants.NumberOfInadmissibleHeuristicsForSMHAStar)
						%Constants.NumberOfInadmissibleHeuristicsForSMHAStar)+1; //verify
				Boolean anchorStateExpanded = false;
				SynchronisedNode anchorPeek = anchorQ.peekOverriden();
				if(anchorPeek != null)
					anchorPeek.writeLock().lock();
				SynchronisedNode inadmissiblePeek = inadmissibleQList.get(heauristicCounter-1).peekOverriden();
				if(inadmissiblePeek != null)
					inadmissiblePeek.writeLock().lock();
				while(anchorPeek != null && expandAnchor(anchorPeek,
						inadmissiblePeek, heauristicCounter))
				{
					waitCount++;
					if(startValue == heauristicCounter)
					{
						if(nGoal.getCost() <= anchorPeek.getCost())
						{
							anchorPeek.writeLock().unlock();
							pathLength = HeuristicSolverUtility.printPathLength(nGoal);
							System.out.println("path length using SMHA is :"+pathLength);
							return true;
						}
						SynchronisedNode selected = anchorPeek;
						expandedByAnchor.put(selected.hashCode(), true);
						Thread thread = new Thread(new ThreadSMHA(0, anchorQ, inadmissibleQList, selected));
						threadPool.put(0, thread);
						thread.start();
						for(InadmissibleHeuristicQ<SynchronisedNode> p: inadmissibleQList)
						{
							p.removeOverriden(selected);
							
						}
						anchorQ.removeOverriden(selected);
						
						anchorStateExpanded = true;
						break;
					}
					
					heauristicCounter = (heauristicCounter%Constants.NumberOfInadmissibleHeuristicsForSMHAStar)+1;
				}
				if(anchorPeek == null)
				{
					Long time = System.currentTimeMillis();
					while(anchorQ.size() == 0)
					{
						Long timenow = System.currentTimeMillis();
						if(timenow - time > 50)
						{
							return false;
						}
					}
					timeSpentWaiting += System.currentTimeMillis() - time;
				}
				else 
				{
					if(!anchorStateExpanded)
					{
						if(nGoal.getCost() <= inadmissiblePeek.getCost()) // inadmissiblePeek already has a write lock , and 
							//you are trying to obtain read lock on it as well!
						{
							inadmissiblePeek.writeLock().unlock();
							pathLength = HeuristicSolverUtility.printPathLength(nGoal);
							System.out.println("path length using SMHA is :"+pathLength);
							return true;
						}
						SynchronisedNode selected = inadmissiblePeek;
						expandedByInadmissible.put(selected.hashCode(), true);
//						HeuristicSolverUtility.printState(selected.getState());
						Thread thread = new Thread(new ThreadSMHA(heauristicCounter, anchorQ, inadmissibleQList, selected));
						threadPool.put(heauristicCounter, thread);

						thread.start();
						
						anchorQ.removeOverriden(selected);
						for(InadmissibleHeuristicQ<SynchronisedNode> p: inadmissibleQList)
						{
							p.removeOverriden(selected);
						}
					}
				}
				
//				System.out.println("thread count is:"+threadPool.size());
				try{
					if(anchorPeek != null)
						anchorPeek.writeLock().unlock();
				}catch(IllegalMonitorStateException exception)
				{
					System.out.println(exception);
				}
				try{
					if(inadmissiblePeek != null)
						inadmissiblePeek.writeLock().unlock();
				}catch(IllegalMonitorStateException exception)
				{
					System.out.println(exception);
				}
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
	
	private  SynchronisedNode createSynchronisedNode(State state, Double weight, Boolean lock)
	{
		synchronized(StateConstants.SynchronisedNodeMap) {
			SynchronisedNode createdNode = null;
			if(StateConstants.SynchronisedNodeMap.get(state.hashCode()) != null)
			{
				createdNode = StateConstants.SynchronisedNodeMap.get(state.hashCode());
			}
			else
			{
				SynchronisedNode SynchronisedNode = new SynchronisedNode(state, weight);
				StateConstants.SynchronisedNodeMap.put(state.hashCode(), SynchronisedNode);
				createdNode = SynchronisedNode;
			}
			if(lock)
			{
				createdNode.writeLock().lock(); //this is a little bit of a waste of time. We only need read lock here, 
				// but in case writing occurs after someone has read previous value but not acted on it yet, it will cause inconsistencies.
			}
			
			return createdNode;
		}
	}
//	
//	public static void main(String args[])
//	{
//		Long t1, t2;
//		State randomState = HeuristicSolverUtility.createRandom(3);
//		System.out.println("Random State");
//		HeuristicSolverUtility.printState(randomState);
//		t1 = System.currentTimeMillis();
//		new ParallelSMHAStar(randomState);
//		t2 = System.currentTimeMillis();
//		System.out.println("Time taken is:"+(t2-t1));
//	}
	
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
			Long time = System.currentTimeMillis();
			expandSynchronisedNode(anchorPQ, listPQ, toBeExpanded);
			totalTimeInExpansions += System.currentTimeMillis() - time;
			threadPool.remove(heuristic);
		}

	}
	
	private void expandSynchronisedNode(AnchorQ<SynchronisedNode> anchorPQ, List<InadmissibleHeuristicQ<SynchronisedNode>> listPQ, SynchronisedNode toBeExpanded)
	{
		
		State state = toBeExpanded.getState();

		List<Action> listOfPossibleActions = state.getPossibleActions();
		Iterator<Action> actIter = listOfPossibleActions.iterator();
		while(actIter.hasNext()) {
			Action actionOnState = actIter.next();
			State newState = actionOnState.applyTo(state);
			SynchronisedNode newSynchronisedNode = createSynchronisedNode(newState, Constants.w1, true);
			toBeExpanded.readLock().lock();
			if(newSynchronisedNode.getCost() > toBeExpanded.getCost()+1)
			{
				newSynchronisedNode.setParent(toBeExpanded);
				newSynchronisedNode.writeLock().unlock();
				toBeExpanded.readLock().unlock();
				if(expandedByAnchor.get(newSynchronisedNode.hashCode()) == null)
				{
					synchronized (anchorPQ) {
						anchorPQ.removeOverriden(newSynchronisedNode);
						anchorPQ.addOverriden(newSynchronisedNode);
					}
					
					if(expandedByInadmissible.get(newSynchronisedNode.hashCode()) == null)
					{
						addOrUpdateSynchronisedNodeToInadmissibleQueues(listPQ, newSynchronisedNode);
					}
				}
			}
			try{
				newSynchronisedNode.writeLock().unlock();
			}catch(IllegalMonitorStateException exception)
			{
//				exception.printStackTrace();
			}
			try{
				toBeExpanded.readLock().unlock();
			}catch(IllegalMonitorStateException exception)
			{
//				exception.printStackTrace();
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
				synchronized (pq) {
					pq.removeOverriden(toBeAdded);
					pq.addOverriden(toBeAdded);
				}
			}
		}
	}
	
	
}
