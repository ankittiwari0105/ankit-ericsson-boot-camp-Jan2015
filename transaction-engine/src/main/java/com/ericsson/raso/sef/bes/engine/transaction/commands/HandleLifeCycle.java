package com.ericsson.raso.sef.bes.engine.transaction.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.bes.engine.transaction.Constants;
import com.ericsson.raso.sef.bes.engine.transaction.ServiceResolver;
import com.ericsson.raso.sef.bes.engine.transaction.TransactionException;
import com.ericsson.raso.sef.bes.engine.transaction.entities.HandleLifeCycleRequest;
import com.ericsson.raso.sef.bes.engine.transaction.entities.HandleLifeCycleResponse;
import com.ericsson.raso.sef.bes.engine.transaction.orchestration.Orchestration;
import com.ericsson.raso.sef.bes.engine.transaction.orchestration.OrchestrationManager;
import com.ericsson.raso.sef.bes.prodcat.CatalogException;
import com.ericsson.raso.sef.bes.prodcat.SubscriptionLifeCycleEvent;
import com.ericsson.raso.sef.bes.prodcat.entities.Offer;
import com.ericsson.raso.sef.bes.prodcat.service.IOfferCatalog;
import com.ericsson.raso.sef.bes.prodcat.tasks.Persistence;
import com.ericsson.raso.sef.bes.prodcat.tasks.PersistenceMode;
import com.ericsson.raso.sef.bes.prodcat.tasks.TransactionTask;
import com.ericsson.raso.sef.core.FrameworkException;
import com.ericsson.sef.bes.api.entities.TransactionStatus;
import com.ericsson.sef.bes.api.subscriber.ISubscriberResponse;

public class HandleLifeCycle extends AbstractTransaction{
	private static final Logger LOGGER = LoggerFactory.getLogger(HandleLifeCycle.class);
	public HandleLifeCycle(String requestId, String subscriberId,String lifeCycleState,Map<String,String> metas) {
		super(requestId, new HandleLifeCycleRequest(requestId, subscriberId,metas));
		this.setResponse(new HandleLifeCycleResponse(requestId,true));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128250929086409651L;

	@Override
	public Boolean execute() throws TransactionException {
		

		List<TransactionTask> tasks = new ArrayList<TransactionTask>(); 
		com.ericsson.raso.sef.core.db.model.Subscriber subscriberEntity;
		try {
			subscriberEntity = ((HandleLifeCycleRequest)this.getRequest()).persistableEntity();
			tasks.add(new Persistence<com.ericsson.raso.sef.core.db.model.Subscriber>(PersistenceMode.SAVE, subscriberEntity, subscriberEntity.getMsisdn()));
			
			IOfferCatalog catalog = ServiceResolver.getOfferCatalog();
			Offer workflow = catalog.getOfferById(Constants.HANDLE_LIFE_CYCLE.name());
			if (workflow != null) {
				String subscriberId = ((HandleLifeCycleRequest)this.getRequest()).getSubscriberId();
				try {
					tasks.addAll(workflow.execute(subscriberId, SubscriptionLifeCycleEvent.PURCHASE, true,new HashMap<String,Object>()));
				} catch (CatalogException e) {
					this.getResponse().setReturnFault( new TransactionException(this.getRequestId(), "Unable to pack the workflow tasks for this use-case", e));
				}
			}
			
			Orchestration execution = OrchestrationManager.getInstance().createExecutionProfile(this.getRequestId(), tasks);
			
			OrchestrationManager.getInstance().submit(this, execution);
		} catch (FrameworkException e1) {
			this.getResponse().setReturnFault( new TransactionException(this.getRequestId(), "Unable to pack the workflow tasks for this use-case", e1));
		}
		return true;
	
		
	}

	@Override
	public void sendResponse() {
		
		TransactionStatus txnStatus=null;
		Boolean result = ((HandleLifeCycleResponse)this.getResponse()).getResult();
		LOGGER.debug("Invoking Handlelifecycle subscriber response!!");
		ISubscriberResponse subscriberClient = ServiceResolver.getSubscriberResponseClient();
		subscriberClient.handleLifeCycle(this.getRequestId(),
				                        txnStatus, 
				                        result);
		LOGGER.debug("Handlelifecycle susbcriber response posted");
		
	}

}