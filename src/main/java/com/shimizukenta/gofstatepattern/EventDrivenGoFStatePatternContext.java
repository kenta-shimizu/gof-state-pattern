package com.shimizukenta.gofstatepattern;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface is implements of Event Driven GoF State pattern engine.
 * 
 * <p>
 * To start, {@link #open()}.<br />
 * To end, {@link #close()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface EventDrivenGoFStatePatternContext<T extends EventDrivenGoFState<E>, E>
		extends GoFStatePatternContext<T>, Closeable {
	
	/**
	 * Start engine.
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException;
	
	/**
	 * Returns {@code true} if opened and <strong>not</strong> closed.
	 * 
	 * @return {@code true} if opened and not closed
	 */
	public boolean isOpen();
	
	/**
	 * Returns {@code true} if closed.
	 * 
	 * @return {@code true} if closed
	 */
	public boolean isClosed();
	
	/**
	 * Fire Event Trigger.
	 * 
	 * @param trigger
	 * @throws InterruptedException
	 */
	public void fire(E trigger) throws InterruptedException;
	
}
