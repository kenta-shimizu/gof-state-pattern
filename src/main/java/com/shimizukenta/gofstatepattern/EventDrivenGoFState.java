package com.shimizukenta.gofstatepattern;

/**
 * This interface is implementation of event driven state.
 * 
 * @author kenta-shimizu
 *
 * @param <E>
 */
public interface EventDrivenGoFState<E> extends GoFState {
	
	/**
	 * Returns {@code true} if entry state.
	 * 
	 * @return {@code true} if entry state
	 */
	public boolean isEntry();
	
	/**
	 * Returns next-state-name if exist, otherwise {@code null}.
	 * 
	 * @param trigger
	 * @return next-state-name if exist, otherwise {@code null}
	 */
	public String getNextStateName(E trigger);
	
	/**
	 * Execute Action before change state.
	 * 
	 * @param trigger
	 * @throws InterruptedException
	 */
	public void beforeChangedAction(E trigger) throws InterruptedException;
	
	/**
	 * Execute action after change state.
	 * 
	 * @param trigger
	 * @throws InterruptedException
	 */
	public void afterChangedAction(E trigger) throws InterruptedException;
	
}
