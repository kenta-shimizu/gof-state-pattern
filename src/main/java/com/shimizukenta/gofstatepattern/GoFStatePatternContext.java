package com.shimizukenta.gofstatepattern;

/**
 * This interface is implements GoF State pattern.
 * 
 * <p>
 * To set stete, {@link #setState(GoFState)}.<br />
 * To get present state, {@link #presentState()}.<br />
 * </p>
 * <p>
 * To receive state changed notification, {@link #addStateChangeListener(GoFStateChangeListener)}.
 * </p>
 * 
 * @author kenta-shimizu
 *
 * @param <T>
 */
public interface GoFStatePatternContext<T extends GoFState> {
	
	/**
	 * State setter.
	 * 
	 * @param state
	 */
	public void setState(T state);
	
	/**
	 * Returns present state.
	 * 
	 * @return present state
	 */
	public T presentState();
	
	/**
	 * Add state change listener.
	 * 
	 * <p>
	 * Not accept {@code null}.
	 * </p>
	 * <p>
	 * This listener is blocking method.<br />
	 * pass through quickly.<br />
	 * </p>
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addStateChangeListener(GoFStateChangeListener<? super T> l);
	
	/**
	 * Remove state change listener.
	 * 
	 * <p>
	 * Not accept {@code null}.
	 * </p>
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeStateChangeListener(GoFStateChangeListener<? super T> l);
	
}
