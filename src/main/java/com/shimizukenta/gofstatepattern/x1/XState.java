package com.shimizukenta.gofstatepattern.x1;

import java.util.List;
import java.util.Set;

import com.shimizukenta.gofstatepattern.EventDrivenGoFState;

public interface XState<
	E,
	A extends XAction<E, V>,
	V
	> extends EventDrivenGoFState<E> {

	public String getNext(CharSequence trigger);
	public boolean hasTrigger(CharSequence trigger);
	public Set<String> triggers();
	public List<A> beforeActions();
	public List<A> afterActions();
	public String name();
	public boolean isEntry();

}