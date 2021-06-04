package com.shimizukenta.gofstatepattern.x1;

import java.util.EventListener;

public interface XActionResultListener extends EventListener {
	
	public void actioned(String actionName, boolean result);
}
