package com.shimizukenta.gofstatepattern;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventDrivenGoFStatePatternContext<T extends EventDrivenGoFState<U>, U>
		extends AbstractGoFStatePatternContext<T>
		implements EventDrivenGoFStatePatternContext<T, U> {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	protected ExecutorService executorService() {
		return execServ;
	}
	
	protected void executorLoop(InterruptableRunnable r) {
		try {
			for ( ;; ) {
				r.run();
			}
		}
		catch ( InterruptedException ignore ) {
		}
	}
	
	private boolean opened;
	private boolean closed;
	
	public AbstractEventDrivenGoFStatePatternContext() {
		super();
		this.opened = false;
		this.closed = false;
	}
	
	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
			
			super.setState(getEntryState());
		}
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
			
			try {
				execServ.shutdown();
				if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
					execServ.shutdownNow();
					if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
						throw new IOException("ExecutorService#shutdown failed.");
					}
				}
			}
			catch ( InterruptedException giveup ) {
			}
		}
	}
	
	@Override
	public boolean isOpen() {
		synchronized ( this ) {
			return this.opened && ! this.closed;
		}
	}
	
	@Override
	public boolean isClosed() {
		synchronized ( this ) {
			return this.closed;
		}
	}
	
	@Override
	public void fire(U trigger) {
		synchronized ( this ) {
			this.presentState().fire(trigger);
		}
	}
	
	/**
	 * Returns Entry state, GoF Prototype pattern.
	 * 
	 * @return entry state
	 */
	abstract protected T getEntryState();
	
}
