package com.shimizukenta.gofstatepattern.x1;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractXState<E, A extends XAction<E, V>, V> implements XState<E, A, V> {
	
	private boolean isEntry;
	private String name;
	
	public AbstractXState() {
		this.isEntry(false);
		this.name(null);
	}
	
	private final Map<String, String> eventMap = new HashMap<>();
	
	public final void addEventMap(CharSequence trigger, CharSequence next) {
		synchronized ( eventMap ) {
			if ( trigger != null && next != null ) {
				this.eventMap.put(trigger.toString(), next.toString());
			}
		}
	}
	
	@Override
	public final String getNext(CharSequence trigger) {
		synchronized ( eventMap ) {
			if ( trigger != null ) {
				return this.eventMap.get(trigger.toString());
			}
			return null;
		}
	}
	
	@Override
	public final boolean hasTrigger(CharSequence trigger) {
		synchronized ( eventMap ) {
			if ( trigger != null ) {
				return this.eventMap.containsKey(trigger.toString());
			}
			return false;
		}
	}
	
	@Override
	public Set<String> triggers() {
		synchronized ( this ) {
			return this.eventMap.keySet();
		}
	}
	
	private final List<A> beforeActions = new CopyOnWriteArrayList<>();
	
	public final boolean addBeforeAction(A a) {
		return this.beforeActions.add(a);
	}
	
	public final boolean removeBeforeAction(A a) {
		return this.beforeActions.remove(a);
	}
	
	@Override
	public final List<A> beforeActions() {
		return Collections.unmodifiableList(this.beforeActions);
	}
	
	private final List<A> afterActions = new CopyOnWriteArrayList<>();
	
	public final boolean addAfterAction(A a) {
		return this.afterActions.add(a);
	}
	
	public final boolean removeAfterAction(A a) {
		return this.afterActions.remove(a);
	}
	
	@Override
	public final List<A> afterActions() {
		return Collections.unmodifiableList(this.afterActions);
	}
	
	
	@Override
	public final String name() {
		return this.name;
	}
	
	public final void name(CharSequence cs) {
		synchronized ( this ) {
			if ( cs != null ) {
				this.name = cs.toString();
			}
		}
	}
	
	public final void isEntry(boolean f) {
		synchronized ( this ) {
			this.isEntry = f;
		}
	}
	
	@Override
	public final boolean isEntry() {
		synchronized ( this ) {
			return this.isEntry;
		}
	}
	
	@Override
	public final String beforeChangedAction(E trigger) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final String afterChangedAction(E trigger) {
		throw new UnsupportedOperationException();
	}
	
}
