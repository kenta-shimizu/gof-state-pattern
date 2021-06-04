package com.shimizukenta.gofstatepattern.x1;

public final class XActionResult {
	
	private boolean isCancelled;
	private XActionBehavior behavior;
	private String nextState;
	
	private XActionResult(boolean cancelled, XActionBehavior behavior, String nextState) {
		this.isCancelled = cancelled;
		this.behavior = behavior;
		this.nextState = nextState;
	}
	
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	public XActionBehavior behavior() {
		return this.behavior;
	}
	
	public String nextState() {
		return this.nextState;
	}
	
	private static class SingletonHolder {
		private static final XActionResult cancelled = new XActionResult(true, XActionBehavior.BREAK, "");
		private static final XActionResult next = new XActionResult(false, XActionBehavior.NEXT, "");
	}
	
	public static XActionResult cencelled() {
		return SingletonHolder.cancelled;
	}
	
	public static XActionResult next() {
		return SingletonHolder.next;
	}
	
	public static XActionResult build(XActionBehavior behavior, String nextState) {
		return new XActionResult(false, behavior, nextState);
	}
	
}
