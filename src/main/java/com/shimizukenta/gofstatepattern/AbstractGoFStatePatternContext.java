package com.shimizukenta.gofstatepattern;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractGoFStatePatternContext<T extends GoFState> implements GoFStatePatternContext<T> {
	
	private T present;
	
	public AbstractGoFStatePatternContext() {
		this.present = null;
	}
	
	@Override
	public void setState(T state) {
		synchronized ( this ) {
			T prev = present;
			this.present = state;
			notifyStateChanged(prev, this.present);
		}
	}
	
	@Override
	public T presentState() {
		synchronized ( this ) {
			return this.present;
		}
	}
	
	private final Collection<GoFStateChangeListener<? super T>> stateChangeListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addStateChangeListener(GoFStateChangeListener<? super T> l) {
		return stateChangeListeners.add(l);
	}
	
	@Override
	public boolean removeStateChangeListener(GoFStateChangeListener<? super T> l) {
		return stateChangeListeners.remove(l);
	}
	
	protected void notifyStateChanged(T previousState, T changedState) {
		stateChangeListeners.forEach(l -> {
			l.changed(previousState, changedState);
		});
	}
	
}
