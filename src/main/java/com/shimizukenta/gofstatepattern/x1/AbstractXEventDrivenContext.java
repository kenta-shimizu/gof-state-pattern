package com.shimizukenta.gofstatepattern.x1;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.shimizukenta.gofstatepattern.AbstractEventDrivenGoFStatePatternContext;

public abstract class AbstractXEventDrivenContext<
	T extends XState<E, A, V>,
	E,
	A extends XAction<E, V>,
	V
	> extends AbstractEventDrivenGoFStatePatternContext<T, E> implements XEventDrivenContext<T, E, A, V> {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	protected final ExecutorService executorService() {
		return this.execServ;
	}
	
	private boolean opened;
	private boolean closed;
	
	public AbstractXEventDrivenContext() {
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
		}
		
		{
			final List<T> ss = this.states().stream()
					.filter(s -> s.isEntry())
					.collect(Collectors.toList());
			
			if ( ss.isEmpty() ) {
				
				throw new IOException("State has no entry-State");
				
			} else if ( ss.size() > 1 ) {
				
				throw new IOException("State has >=2 entry-State");
				
			} else {
				
				confirmNextExists();
				
				try {
					fire(null, ss.get(0));
				}
				catch (InterruptedException e) {
					throw new IOException("Initial set failed");
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
		}
		
		IOException ioExcept = null;
		
		try {
			this.execServ.shutdown();
			if ( ! this.execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				this.execServ.shutdownNow();
				if ( ! this.execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					ioExcept = new IOException("ExectorService#shutdown failed");
				}
			}
		}
		catch (InterruptedException giveup ) {
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	private final Collection<T> states = new CopyOnWriteArrayList<>();
	
	public boolean addState(T state) {
		return this.states.add(state);
	}
	
	public boolean removeState(T state) {
		return this.states.remove(state);
	}
	
	protected Collection<T> states() {
		return Collections.unmodifiableCollection(states);
	}
	
	protected final T getState(CharSequence name) {
		synchronized ( states ) {
			if ( name != null ) {
				String s = name.toString();
				for ( T st : states ) {
					if ( st.name().equalsIgnoreCase(s) ) {
						return st;
					}
				}
			}
			return null;
		}
	}
	
	private final Object syncCancel = new Object();
	
	@Override
	public void fire(E trigger) throws InterruptedException {
		fire(trigger, getNextState(trigger));
	}
	
	private void fire(E trigger, T next) throws InterruptedException {
		
		if ( next != null ) {
			
			synchronized ( syncCancel ) {
				syncCancel.notifyAll();
			}
			
			try {
				
				final Collection<Callable<XActionResult>> beforeTasks = Arrays.asList(
						this.actionsTask(next.beforeActions(), trigger, next),
						this.cancelTask());
				
				XActionResult beforeResult = this.execServ.invokeAny(beforeTasks);
				
				this.setState(next);
				
				if ( ! beforeResult.isCancelled() ) {
					
					if ( beforeResult.behavior() == XActionBehavior.CHANGE_STATE ) {
						
						fire(trigger, this.getState(beforeResult.nextState()));
						
					} else {
						
						execServ.execute(() -> {
							
							try {
								final Collection<Callable<XActionResult>> afterTasks = Arrays.asList(
										this.actionsTask(next.afterActions(), trigger, next),
										this.cancelTask());
								
								XActionResult afterResult = this.execServ.invokeAny(afterTasks);
								
								if ( ! afterResult.isCancelled() ) {
									
									if ( afterResult.behavior() == XActionBehavior.CHANGE_STATE ) {
										
										fire(trigger, this.getState(afterResult.nextState()));
									}
								}
							}
							catch ( ExecutionException e ) {
								putThrowable(e);
							}
							catch ( InterruptedException ignore ) {
							}
						});
					}
				}
			}
			catch ( ExecutionException e ) {
				putThrowable(e);
			}
		}
	}
	
	protected void putThrowable(Throwable t) {
		/* Override if use */
	}
	
	abstract protected boolean actionExecute(A action, E trigger, T state) throws InterruptedException;
	
	private Callable<XActionResult> actionsTask(List<A> actions, E trigger, T state) {
		
		return new Callable<XActionResult> () {
			
			public XActionResult call() throws Exception {
				
				try {
					
					for ( A a : actions ) {
						
						if ( actionExecute(a, trigger, state) ) {
							
							switch (a.success()) {
							case NEXT: {
								
								/* Nothing */
								break;
							}
							case BREAK: {
								
								return XActionResult.next();
								/* break; */
							}
							case CHANGE_STATE: {
								
								return XActionResult.build(
										XActionBehavior.CHANGE_STATE,
										a.successState());
								/* break; */
							}
							}
							
						} else {
							
							switch (a.failed()) {
							case NEXT: {
								
								/* Nothing */
								break;
							}
							case BREAK: {
								
								return XActionResult.next();
								/* break; */
							}
							case CHANGE_STATE: {
								
								return XActionResult.build(
										XActionBehavior.CHANGE_STATE,
										a.failedState());
								/* break; */
							}
							}
						}
					}
					
					return XActionResult.next();
				}
				catch ( InterruptedException ignore ) {
				}
				
				return XActionResult.next();
			}
		};
	}
	
	private Callable<XActionResult> cancelTask() {
		
		return new Callable<XActionResult> () {
			
			public XActionResult call() throws Exception {
				try {
					synchronized ( syncCancel ) {
						syncCancel.wait();
						return XActionResult.cencelled();
					}
				}
				catch ( InterruptedException ignore ) {
				}
				
				return XActionResult.build(XActionBehavior.BREAK, "");
			}
		};
	}
	
	private final Collection<XTryActionListener> tryActionListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public final boolean addTryActionListener(XTryActionListener l) {
		return this.tryActionListeners.add(l);
	}
	
	@Override
	public final boolean removeTryActionListener(XTryActionListener l) {
		return this.tryActionListeners.remove(l);
	}
	
	protected void notifyTryAction(String actionName) {
		this.tryActionListeners.forEach(l -> {
			l.tryActioned(actionName);
		});
	}
	
	private final Collection<XActionResultListener> actionResultListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public final boolean addActionResultListener(XActionResultListener l) {
		return this.actionResultListeners.add(l);
	}
	
	@Override
	public final boolean removeActionResultListener(XActionResultListener l) {
		return this.actionResultListeners.remove(l);
	}
	
	protected void notifyActionResult(String actionName, boolean result) {
		this.actionResultListeners.forEach(l -> {
			l.actioned(actionName, result);
		});
	}
	
	private void confirmNextExists() throws IOException {
		
		final Set<String> stateNames = this.states().stream()
				.map(s -> s.name())
				.collect(Collectors.toSet());
		
		for ( T state : states() ) {
			
			final String sn = state.name();
			
			for ( String trig : state.triggers() ) {
				String s = state.getNext(trig);
				if ( ! stateNames.contains(s) ) {
					throw new IOException("\"" + sn + "\" triggers: \"" + s + "\" unknown next");
				}
			}
			
			for ( A a : state.beforeActions() ) {
				
				if ( a.success() == XActionBehavior.CHANGE_STATE ) {
					String s = a.successState();
					if ( ! stateNames.contains(s) ) {
						throw new IOException("\"" + sn + "\" befores: \"" + s + "\" unknown next");
					}
				}
				
				if ( a.failed() == XActionBehavior.CHANGE_STATE ) {
					String s = a.failedState();
					if ( ! stateNames.contains(s) ) {
						throw new IOException("\"" + sn + "\" befores: \"" + s + "\" unknown next");
					}
				}
			}
			
			for ( A a : state.afterActions() ) {
				
				if ( a.success() == XActionBehavior.CHANGE_STATE ) {
					String s = a.successState();
					if ( ! stateNames.contains(s) ) {
						throw new IOException("\"" + sn + "\" afters: \"" + s + "\" unknown next");
					}
				}
				
				if ( a.failed() == XActionBehavior.CHANGE_STATE ) {
					String s = a.failedState();
					if ( ! stateNames.contains(s) ) {
						throw new IOException("\"" + sn + "\" afters: \"" + s + "\" unknown next");
					}
				}
			}

		}
	}
	
}
