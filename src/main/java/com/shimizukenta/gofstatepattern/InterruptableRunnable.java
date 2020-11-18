package com.shimizukenta.gofstatepattern;

/**
 * 
 * @author kenta-shimizu
 *
 */
public interface InterruptableRunnable {
	public void run() throws InterruptedException;
}
