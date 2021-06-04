package com.shimizukenta.gofstatepattern;

/**
 * This interface is implements of Event Driven GoF State pattern engine.
 * 
 * <p>
 * To fire event, {@link #fire(Object)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface EventDrivenGoFStatePatternContext<T extends EventDrivenGoFState<E>, E>
		extends GoFStatePatternContext<T> {
	
	/**
	 * Fire Event Trigger.
	 * 
	 * @param trigger
	 * @throws InterruptedException
	 */
	public void fire(E trigger) throws InterruptedException;
	
}
