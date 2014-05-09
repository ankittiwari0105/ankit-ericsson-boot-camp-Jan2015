package com.ericsson.raso.sef.bes.prodcat.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum State implements Serializable {
	IN_CREATION,
	TESTING,
	PUBLISHED,
	DISABLED,
	RETIRED;
	
	public List<State> getNextAllowedTransitions() {
		State[] next = null;
		
		switch(this) {
			case IN_CREATION:
				next = new State[] { TESTING };
				break;
			case TESTING:
				next = new State[] { PUBLISHED, DISABLED, RETIRED };
				break;
			case PUBLISHED:
				next = new State[] { DISABLED, RETIRED };
				break;
			case DISABLED:
				next = new State[] { PUBLISHED, RETIRED };
				break;
			case RETIRED:
				next = new State[] { };
				break;
		}
		
		List<State> nextStates = new ArrayList<State>(5);
		for (State state: next) 
			nextStates.add(state);
		return nextStates;
	}

}
