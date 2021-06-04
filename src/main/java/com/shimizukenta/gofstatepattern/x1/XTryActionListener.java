package com.shimizukenta.gofstatepattern.x1;

import java.util.EventListener;

public interface XTryActionListener extends EventListener {
	
	public void tryActioned(String actionName);
}
