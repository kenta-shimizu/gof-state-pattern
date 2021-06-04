package com.shimizukenta.gofstatepattern.x1;

public interface XAction<E, V> extends XActionExecutor<E, V> {
	
	public String name();
	public XActionBehavior success();
	public String successState();
	public XActionBehavior failed();
	public String failedState();
	
}
