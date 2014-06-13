package com.ericsson.raso.sef.smart.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.core.Constants;
import com.ericsson.raso.sef.core.RequestContextLocalStore;
import com.ericsson.raso.sef.core.SefCoreServiceResolver;
import com.ericsson.raso.sef.core.db.model.ContractState;
import com.ericsson.raso.sef.smart.ErrorCode;
import com.ericsson.raso.sef.smart.ExceptionUtil;
import com.ericsson.raso.sef.smart.SmartServiceResolver;
import com.ericsson.raso.sef.smart.commons.SmartConstants;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberInfo;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberResponseStore;
import com.ericsson.raso.sef.smart.subscription.response.PurchaseResponse;
import com.ericsson.raso.sef.smart.subscription.response.RequestCorrelationStore;
import com.ericsson.raso.sef.smart.usecase.ModifyCustomerGraceRequest;
import com.ericsson.sef.bes.api.entities.Meta;
import com.ericsson.sef.bes.api.subscriber.ISubscriberRequest;
import com.hazelcast.core.ISemaphore;

public class ModifyCustomerGrace implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(ModifyCustomerGrace.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		
			
			ModifyCustomerGraceRequest request = (ModifyCustomerGraceRequest) exchange.getIn().getBody();
			
			String requestId = RequestContextLocalStore.get().getRequestId();
			//SubscriberInfo subscriberInfo = readSubscriber(requestId,request.getCustomerId(),null);
			
			
			List<String> keys = new ArrayList<String>();
			keys.add(SmartConstants.GRACE_ENDDATE);
			
			List<Meta> metas = new ArrayList<Meta>();
			//metas.add(new Meta("daysOfExtension" , String.valueOf(request.getDaysOfExtension())));
			metas.add(new Meta("EX_DATA_2" , String.valueOf(request.getDaysOfExtension())));
			metas.add(new Meta("eventInfo" , String.valueOf(request.getEventInfo())));
			metas.add(new Meta("messageId" , String.valueOf(request.getMessageId())));
			metas.add(new Meta("HANDLE_LIFE_CYCLE", "ModifyCustomerGrace"));
			
			ISubscriberRequest iSubscriberRequest = SmartServiceResolver.getSubscriberRequest();
			//ISubscriptionRequest iSubscriptionRequest = SmartServiceResolver.getSubscriptionRequest();
			logger.info("Before read subscriber call");
			SubscriberInfo subscriberObj=readSubscriber(requestId, request.getCustomerId(), metas);
			if(subscriberObj ==  null)
				throw ExceptionUtil.toSmException(ErrorCode.invalidOperationState);
			logger.info("Recieved a SubscriberInfo Object and it is not null");
			if(ContractState.apiValue("PRE_ACTIVE").toString().equals(subscriberObj.getSubscriber().getContractState().toString()))
				throw ExceptionUtil.toSmException(ErrorCode.invalidOperationState);
			
					 Map<String, String> subscriberMetas = subscriberObj.getMetas();
						int index = 0;
						String offers = "";
						for (String key: subscriberMetas.keySet()) {
							if (key.contains(".")) {
								if (key.startsWith("READ_SUBSCRIBER_OFFER_INFO_OFFER_ID"))
									offers += "," + subscriberMetas.get(key) + " ";
							}
						}
						if (!offers.contains(",2 ")) {
							throw ExceptionUtil.toSmException(ErrorCode.invalidCustomerLifecycleState);
							
						}
					String resultId=iSubscriberRequest.handleLifeCycle(requestId, request.getCustomerId(), ContractState.GRACE.getName(), metas);
				     PurchaseResponse response = new PurchaseResponse();
					logger.debug("Got past event class....");
						RequestCorrelationStore.put(resultId, response);
						String date=subscriberObj.getSubscriber().getMetas().get("graceEndDate");
						String newDate=DateUtil.addDaysToDate(date,request.getDaysOfExtension());
						metas.add(new Meta("daysOfExtension",String.valueOf(newDate)));
						SubscriberInfo subscriberInfo= updateSubscriber(requestId, request.getCustomerId(), metas, Constants.ModifyCustomerPreActive);
						if(subscriberInfo.getSubscriber() == null){
							throw ExceptionUtil.toSmException(ErrorCode.invalidOperationState);
						}
						DummyProcessor.response(exchange);
					}
					
			
			

	private SubscriberInfo updateSubscriber(String requestId,String customer_id, List<Meta> metas,String useCase) {
		logger.info("Invoking update subscriber on tx-engine subscriber interface");
		ISubscriberRequest iSubscriberRequest = SmartServiceResolver.getSubscriberRequest();
		SubscriberInfo subInfo = new SubscriberInfo();
		SubscriberResponseStore.put(requestId, subInfo);
		iSubscriberRequest.updateSubscriber(requestId, customer_id, metas,useCase);
		ISemaphore semaphore = SefCoreServiceResolver.getCloudAwareCluster().getSemaphore(requestId);
		try {
			semaphore.init(0);
			semaphore.acquire();
		} catch (InterruptedException e) {

		}
		logger.info("Check if response received for update subscriber");
		SubscriberInfo subscriberInfo = (SubscriberInfo) SubscriberResponseStore
				.remove(requestId);
		return subscriberInfo;
	}
	
	private SubscriberInfo readSubscriber(String requestId,String customer_id, List<Meta> metas) {
		logger.info("Invoking update subscriber on tx-engine subscriber interface");
		ISubscriberRequest iSubscriberRequest = SmartServiceResolver.getSubscriberRequest();
		SubscriberInfo subInfo = new SubscriberInfo();
		SubscriberResponseStore.put(requestId, subInfo);
		iSubscriberRequest.readSubscriber(requestId, customer_id, metas);
		ISemaphore semaphore = SefCoreServiceResolver.getCloudAwareCluster().getSemaphore(requestId);
		try {
			semaphore.init(0);
			semaphore.acquire();
		} catch (InterruptedException e) {

		}
		logger.info("Check if response received for update subscriber");
		SubscriberInfo subscriberInfo = (SubscriberInfo) SubscriberResponseStore.remove(requestId);
		return subscriberInfo;
	}
}
