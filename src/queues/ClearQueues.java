package queues;

public class ClearQueues {
	
	public static void clear() {
		ExpandedQueue.listOfExpandedNodesForAQueue.clear();
		PQueue.priorityQueueMap.clear();
	}

}
