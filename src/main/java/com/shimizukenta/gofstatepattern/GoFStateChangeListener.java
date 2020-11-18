package com.shimizukenta.gofstatepattern;

import java.util.EventListener;

/**
 * 
 * @author kenta-shimizu
 *
 */
public interface GoFStateChangeListener<T extends GoFState> extends EventListener {
	
	public void changed(T previousState, T presentState);
}
