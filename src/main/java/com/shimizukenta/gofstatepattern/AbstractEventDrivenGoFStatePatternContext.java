package com.shimizukenta.gofstatepattern;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventDrivenGoFStatePatternContext<T extends EventDrivenGoFState<E>, E>
		extends AbstractGoFStatePatternContext<T>
		implements EventDrivenGoFStatePatternContext<T, E> {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	protected ExecutorService executorService() {
		return execServ;
	}
	
	private final Collection<T> states;
	
	private boolean opened;
	private boolean closed;
	
	public AbstractEventDrivenGoFStatePatternContext(Collection<? extends T> states) {
		super();
		
		this.states = new HashSet<>(states);
		
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
			
			{
				T entryState = getEntryState();
				
				if ( entryState == null ) {
					
					throw new IOException("Not found entry-state");
					
				} else {
					
					super.setState(entryState);
				}
			}
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
	
	private final Object cancelObj = new Object();
	
	@Override
	public void setState(T state) {
		synchronized ( this ) {
			synchronized (cancelObj) {
				cancelObj.notifyAll();
			}
			super.setState(state);
		}
	}
	
	@Override
	public void fire(E trigger) throws InterruptedException {
		
		synchronized ( this ) {
			
			T next = getState(presentState().getNextStateName(trigger));
			
			if ( next != null ) {
				
				{
					T s = getState(next.beforeChangedAction(trigger));
					if ( s != null ) {
						setState(s);
						return;
					}
				}
				
				setState(next);
				
				executorService().execute(() -> {
					
					Collection<Callable<Void>> tasks = Arrays.asList(
							() -> {
								try {
									T s = getState(next.afterChangedAction(trigger));
									if ( s != null ) {
										setState(s);
									}
								}
								catch ( InterruptedException ignore ) {
								}
								return null;
							},
							() -> {
								try {
									synchronized ( cancelObj ) {
										cancelObj.wait();
									}
								}
								catch ( InterruptedException ignore ) {
								}
								return null;
							}
							);
					
					try {
						executorService().invokeAny(tasks);
					}
					catch ( ExecutionException e ) {
						
						Throwable t = e.getCause();
						
						if ( t instanceof Error ) {
							throw (Error)t;
						}
						if ( t instanceof RuntimeException ) {
							throw (RuntimeException)t;
						}
					}
					catch ( InterruptedException ignore ) {
					}
				});
			}
		}
	}
	
	private T getState(String stateName) {
		if ( stateName != null ) {
			for ( T s : states ) {
				if ( s.name().equals(stateName) ) {
					return s;
				}
			}
		}
		return null;
	}
	
	private T getEntryState() {
		for ( T s : states ) {
			if ( s.isEntry() ) {
				return s;
			}
		}
		return null;
	}
	
}
