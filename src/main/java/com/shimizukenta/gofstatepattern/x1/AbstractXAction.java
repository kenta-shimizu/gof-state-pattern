package com.shimizukenta.gofstatepattern.x1;

public abstract class AbstractXAction<E, V> implements XAction<E, V> {
	
	private String name;
	private XActionBehavior successBehavior;
	private String successState;
	private XActionBehavior failedBehavior;
	private String failedState;
	
	public String name() {
		synchronized ( this ) {
			return this.name;
		}
	}
	
	public AbstractXAction() {
		this.successBehavior = XActionBehavior.NEXT;
		this.successState = "";
		this.failedBehavior = XActionBehavior.BREAK;
		this.failedState = "";
	}

	@Override
	public XActionBehavior success() {
		synchronized ( this ) {
			return this.successBehavior;
		}
	}

	@Override
	public String successState() {
		synchronized ( this ) {
			return this.successState;
		}
	}

	@Override
	public XActionBehavior failed() {
		synchronized ( this ) {
			return this.failedBehavior;
		}
	}

	@Override
	public String failedState() {
		synchronized ( this ) {
			return this.failedState;
		}
	}
	
	public void name(CharSequence name) {
		synchronized ( this ) {
			this.name = name == null ? null : name.toString();
		}
	}
	
	public void setSuccess(CharSequence cs) {
		
		synchronized ( this ) {
			
			if ( cs != null ) {
				
				String s = cs.toString();
				
				XActionBehavior b = XActionBehavior.get(cs);
				
				switch ( b ) {
				case NEXT:
				case BREAK: {
					this.successBehavior = b;
					break;
				}
				case CHANGE_STATE: {
					this.successBehavior = b;
					this.successState = s;
					break;
				}
				}
			}
		}
	}
	
	public void setFailed(CharSequence cs) {
		
		synchronized ( this ) {
			
			if ( cs != null ) {
				
				String s = cs.toString();
				
				XActionBehavior b = XActionBehavior.get(cs);
				
				switch ( b ) {
				case NEXT:
				case BREAK: {
					this.failedBehavior = b;
					break;
				}
				case CHANGE_STATE: {
					this.failedBehavior = b;
					this.failedState = s;
					break;
				}
				}
			}
		}
	}
	
}
