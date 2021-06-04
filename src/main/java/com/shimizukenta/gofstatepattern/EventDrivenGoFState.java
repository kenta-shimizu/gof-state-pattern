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
	 * Execute Action before change state.
	 * 
	 * @param trigger
	 * @return next-state-name if exist, otherwise {@code null}
	 * @throws InterruptedException
	 */
	public String beforeChangedAction(E trigger) throws InterruptedException;
	
	/**
	 * Execute action after change state.
	 * 
	 * @param trigger
	 * @return next-state-name if exist, otherwise {@code null}
	 * @throws InterruptedException
	 */
	public String afterChangedAction(E trigger) throws InterruptedException;
	
}
