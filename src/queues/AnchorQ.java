package queues;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AnchorQ<E> extends PriorityQueue<E>{
	
	final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	 public AnchorQ(int initialCapacity,
             Comparator<? super E> comparator) {
		 super(initialCapacity, comparator);
	 }

		
//		synchronized
		public boolean removeOverriden(Object o) {
			Boolean result = false;
			if(!this.isEmpty() && o != null)
			{
				readWriteLock.writeLock().lock();
				result = super.remove(o);
				readWriteLock.writeLock().unlock();
			}
			return result;
		}
		
//		synchronized
		public boolean addOverriden(E e) {
			Boolean result = false;
			if(e != null)
			{
				readWriteLock.writeLock().lock();
				result = super.add(e);
				readWriteLock.writeLock().unlock();
			}
			return result;
		}
		
//		synchronized
		public E peekOverriden() {
			E result = null;
			if(!this.isEmpty())
			{
				readWriteLock.readLock().lock();
				result = super.peek();
				readWriteLock.readLock().unlock();
			}
			return result;
		}

}
