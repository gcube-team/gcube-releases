package org.gcube.common.core.state;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * An extension of {@link ReentrantReadWriteLock} that allows <em>preemptive</em> acquisition of write locks.</p>
 * <p>
 * A thread acquires the write lock preemptively (cf. {@link GCUBEWriteLock#lockPreemptively()}) if its actions 
 * preempt those of other threads that synchronise with it (e.g. removal of guarded state). 
 * When the preemptive thread releases the write lock, threads that were waiting for it or will request it in the future 
 * are immediately interrupted, <em>unless</em> the preemptive thread declares its own failure (cf. {@link GCUBEWriteLock#cancelPreemptive()}).
 * <p>
 * The possibility of lock preemption implies that all  locking operations are interruptible in principle.
 * Accordingly, {@link GCUBEWriteLock#lock()} and {@link GCUBEReadLock#lock()} are deprecated in favour of 
 * {@link GCUBEWriteLock#lockInterruptibly()} and {@link GCUBEReadLock#lockInterruptibly()}. If used, they may throw
 * a {@link LockPreemptedException}.     
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * 
 * */
public class GCUBEReadWriteLock extends ReentrantReadWriteLock {

	/** Class logger. */
	protected GCUBELog logger = new GCUBELog(GCUBEReadWriteLock.class);
	
	/**Default serial version ID */
	private static final long serialVersionUID = 1L;
 
    /** The write lock. */
    private GCUBEWriteLock writeLock;
    /** The read lock. */
    private GCUBEReadLock readLock;
    
	/** The thread that performs the preemptive task. */
	protected volatile Thread preemptingThread;
	/** Indicates completion of preemptive task.*/
	protected volatile boolean preempted;
	
	
	/**Creates an instance.*/
	public GCUBEReadWriteLock() {
		super();
		writeLock = new GCUBEWriteLock(this);
		readLock = new GCUBEReadLock(this);
	}
	
    /**
     * Creates an instance with a given fairness policy.
     * @param fair <code>true</code> if this lock should use a fair ordering policy
     */
    public GCUBEReadWriteLock(boolean fair) {
    	super(fair);
    	writeLock = new GCUBEWriteLock(this);
		readLock = new GCUBEReadLock(this);
    }
    
	/**
	 * Sets the resource's logger.
	 * @param logger the logger.
	 */
	public void setLogger(GCUBELog logger) {this.logger=logger;}
    
    /** {@inheritDoc}*/
	public GCUBEWriteLock writeLock()  {return writeLock;}
	
	/** {@inheritDoc}*/
	public GCUBEReadLock readLock()  {return readLock;}
	
	/**
	 * Used internally to check whether the lock has been requested preemptively by a thread other
	 * than the current one.
	 * @throws LockPreemptedException if the lock has been requested preemptively by a thread other
	 * than the current one.
	 */
	protected boolean isPreempted() {return this.preempted;}
	
	/**
	 * An extension of {@link ReentrantReadWriteLock.WriteLock} that supports 
	 * preemptive requests and acquisitions.
	 * @author Fabio Simeoni (University of Strathclyde)
	 **/
	public static class GCUBEReadLock extends ReentrantReadWriteLock.ReadLock {
		
		/**Serial version ID.*/
		private static final long serialVersionUID = 1L;
		
		/**The parent lock. */
		GCUBEReadWriteLock parent;
		
		/**
		 * Creates an instance from the parent {@link GCUBEReadWriteLock}.
		 * @param rwLock the parent lock.
		 */
		GCUBEReadLock(GCUBEReadWriteLock rwLock){
			super(rwLock);
			this.parent=rwLock;
		}
		
		/**
		 * Forces interruptibility on the standard semantics of {@link Lock#lock} to cater for lock preemption.
		 * @throws LockPreemptedException if the lock is preempted.
		 * @deprecated use {@link #lockInterruptibly()} instead.
		 * */
		public void lock() throws LockPreemptedException {
			try {
				this.lockInterruptibly();
			}catch(InterruptedException e) {throw new LockPreemptedException();} 
		}
		
		/** Extends the semantics of {@link Lock#tryLock()} to cater for lock preemption.
		 * @return <code>false</code> if the lock is already held by another thread or has been preempted.*/
		@Override public boolean tryLock() {
			if (this.parent.isPreempted()) return false;
			return super.tryLock();//no need to interrupt if queue is empty; 
		}
		
		/**{@inheritDoc}*/
		public void lockInterruptibly() throws InterruptedException {
			if (this.parent.isPreempted()) throw new InterruptedException();
			this.parent.logger.trace(Thread.currentThread().getName()+" locked");
			super.lockInterruptibly();
		}
		
	}
	/**
	 * An extension of {@link ReentrantReadWriteLock.WriteLock} that supports 
	 * preemptive requests and acquisitions.
	 * @author Fabio Simeoni (University of Strathclyde)
	 **/
	public static class GCUBEWriteLock extends ReentrantReadWriteLock.WriteLock {
		
		/**Default serial version ID*/
		private static final long serialVersionUID = 1L;
		/**The parent lock. */
		GCUBEReadWriteLock parent;
		
		/**
		 * Creates an instance from the parent {@link GCUBEReadWriteLock}.
		 * @param rwLock the parent lock.
		 */
		GCUBEWriteLock(GCUBEReadWriteLock rwLock){
			super(rwLock);
			this.parent=rwLock;
		}

			
		/**
		 * Forces interruptibility on the standard semantics of {@link Lock#lock} to cater for lock preemption.
		 * @throws LockPreemptedException if the lock is preempted.
		 * @deprecated use {@link #lockInterruptibly()} instead.
		 * */
		public void lock() throws LockPreemptedException {
			try {
				this.lockInterruptibly();
			}catch(InterruptedException e) {throw new LockPreemptedException();} 
		}
		
		/** Extends the semantics of {@link Lock#tryLock()} to cater for lock preemption.
		 * @return <code>false</code> if the lock is already held by another thread or has been preempted.*/
		@Override public boolean tryLock() {
			if (this.parent.isPreempted()) return false;
			return super.tryLock();//no need to interrupt if queue is empty; 
		}

		
		/**{@inheritDoc}*/
		public void lockInterruptibly() throws InterruptedException {
			if (this.parent.isPreempted()) throw new InterruptedException();
			super.lockInterruptibly();
			this.parent.logger.trace(Thread.currentThread().getName()+" locked");
		}
		
		/**
		 * Acquires the lock preemptively.
		 * @throws LockPreemptedException if the lock is preempted.
		 */
		public void lockPreemptively() throws InterruptedException {
			if (this.parent.isPreempted()) throw new InterruptedException();
			this.lockInterruptibly();
			this.parent.preemptingThread=Thread.currentThread();
		}
		
		/**{@inheritDoc}*/
		@Override public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
			if (this.parent.isPreempted()) throw new InterruptedException();
			return super.tryLock(timeout,unit);
		}
		
		/**
		 * Extends {@link WriteLock#unlock()} to interrupt writer and reader threads in queue for the lock
		 * when invoked by the preemptive thread.
		 */
		@Override public void unlock() {
			this.parent.logger.trace(Thread.currentThread().getName()+" unlocked");
			if (Thread.currentThread()==this.parent.preemptingThread) {//interrupts all waiting threads
				this.parent.preempted=true;
				synchronized (this) {//synchronise to freeze writer's queue
					for (Thread waitingThread : this.parent.getQueuedWriterThreads()) 
						if (waitingThread!=this.parent.preemptingThread) waitingThread.interrupt();
				}
				synchronized (this.parent.readLock) {//synchronize to freeze reader's queue
					for (Thread waitingThread : this.parent.getQueuedReaderThreads()) waitingThread.interrupt();
				}
				
			}//queue frozen hereafter by check on preemption
			super.unlock();
		}
		
		/**
		 * Indicates failure of the preemptive thread.
		 * @throws IllegalMonitorStateException if the calling thread is not the preemptive thread or
		 * it is the preemptive thread but it has already released the lock.
		 */
		public void cancelPreemptive() throws IllegalMonitorStateException {
			if (Thread.currentThread()!=this.parent.preemptingThread) throw new IllegalMonitorStateException();
			this.parent.preemptingThread=null;
			this.parent.preempted=false;
		}
	}

	/**
	 * Signals that a lock cannot be acquired because another thread has previously 
	 * requested it exclusively.
	 * @author Fabio Simeoni (University of Strathclyde)
	 *
	 */
	static class LockPreemptedException extends RuntimeException{		
		private static final long serialVersionUID = 1L;
	}

}


