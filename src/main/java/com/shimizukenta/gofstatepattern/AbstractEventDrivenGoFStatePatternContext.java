package com.shimizukenta.gofstatepattern;

public abstract class AbstractEventDrivenGoFStatePatternContext<T extends EventDrivenGoFState<E>, E>
		extends AbstractGoFStatePatternContext<T>
		implements EventDrivenGoFStatePatternContext<T, E> {
	
	public AbstractEventDrivenGoFStatePatternContext() {
		super();
	}
	
	abstract protected T getNextState(E trigger);
	
	@Override
	public void fire(E trigger) throws InterruptedException {
		
		synchronized ( this ) {
			
			T next = getNextState(trigger);
			
			if ( next != null ) {
				next.beforeChangedAction(trigger);
				this.setState(next);
				next.afterChangedAction(trigger);
			}
		}
	}
	
}
