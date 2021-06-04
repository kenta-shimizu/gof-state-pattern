package com.shimizukenta.gofstatepattern.x1;

public enum XActionBehavior {
	
	NEXT("next"),
	BREAK("break"),
	CHANGE_STATE(""),
	
	;
	
	private String s;
	private XActionBehavior(String s) {
		this.s = s;
	}
	
	public static XActionBehavior get(CharSequence cs) {
		if ( cs != null ) {
			String s = cs.toString();
			for ( XActionBehavior b : values() ) {
				if ( b.s.equalsIgnoreCase(s) ) {
					return b;
				}
			}
		}
		return CHANGE_STATE;
	}
	
}
