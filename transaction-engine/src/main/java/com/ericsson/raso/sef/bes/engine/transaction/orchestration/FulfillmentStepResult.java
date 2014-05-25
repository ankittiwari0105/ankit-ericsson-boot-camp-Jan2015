package com.ericsson.raso.sef.bes.engine.transaction.orchestration;

import java.util.Set;

import com.ericsson.raso.sef.bes.prodcat.entities.AtomicProduct;


public class FulfillmentStepResult extends AbstractStepResult {
	private static final long	serialVersionUID	= -2540018090411449909L;

	private Set<AtomicProduct> fulfillmentResult = null;
	
	
	FulfillmentStepResult(StepExecutionException resultantFault, Set<AtomicProduct> result) {
		super(resultantFault);
		this.fulfillmentResult = result;
	}


	public Set<AtomicProduct> getFulfillmentResult() {
		return fulfillmentResult;
	}


	public void setFulfillmentResult(Set<AtomicProduct> fulfillmentResult) {
		this.fulfillmentResult = fulfillmentResult;
	}

	

		
	
}