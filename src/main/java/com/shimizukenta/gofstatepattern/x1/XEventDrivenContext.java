package com.shimizukenta.gofstatepattern.x1;

import java.io.Closeable;
import java.io.IOException;

import com.shimizukenta.gofstatepattern.GoFStatePatternContext;

public interface XEventDrivenContext<
	T extends XState<E, A, V>,
	E,
	A extends XAction<E, V>,
	V
	> extends GoFStatePatternContext<T>, Closeable {
	
	/**
	 * Open Context
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException;
	
	public boolean addTryActionListener(XTryActionListener l);
	public boolean removeTryActionListener(XTryActionListener l);
	
	public boolean addActionResultListener(XActionResultListener l);
	public boolean removeActionResultListener(XActionResultListener l);
	
}
