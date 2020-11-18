package com.shimizukenta.gofstatepattern;

/**
 * This interface is implementation of event driven state.
 * 
 * @author kenta-shimizu
 *
 */
public interface EventDrivenGoFState<T> extends GoFState {
	
	/**
	 * Returns {@code true} if entry state.
	 * 
	 * @return {@code true} if entry state
	 */
	public boolean isEntry();
	
	/**
	 * Fire Event Trigger.
	 * 
	 * @param trigger
	 */
	public void fire(T trigger);
	
}
