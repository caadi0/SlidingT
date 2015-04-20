package queues;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InadmissibleHeuristicQ<E> extends PriorityQueue<E>{

	final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	 public InadmissibleHeuristicQ(int initialCapacity,
             Comparator<? super E> comparator) {
		 super(initialCapacity, comparator);
	 }

		
//		synchronized
		public boolean removeOverriden(Object o) {
			Boolean result = false;
			readWriteLock.writeLock().lock();
			if(!this.isEmpty())
			{
				result = super.remove(o);
			}
			readWriteLock.writeLock().unlock();
			return result;
		}
		
//		synchronized
		public boolean addOverriden(E e) {
			Boolean result = false;
			readWriteLock.writeLock().lock();
			result = super.add(e);
			readWriteLock.writeLock().unlock();
			return result;
		}
		
//		synchronized
		public E peekOverriden() {
			E result = null;
			readWriteLock.readLock().lock();
			if(!this.isEmpty())
				result = super.peek();
			readWriteLock.readLock().unlock();
			return result;
		}

}
