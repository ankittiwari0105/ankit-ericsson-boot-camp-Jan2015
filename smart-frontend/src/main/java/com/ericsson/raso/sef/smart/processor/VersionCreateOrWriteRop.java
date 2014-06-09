package com.ericsson.raso.sef.smart.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.core.RequestContextLocalStore;
import com.ericsson.raso.sef.core.ResponseCode;
import com.ericsson.raso.sef.core.SefCoreServiceResolver;
import com.ericsson.raso.sef.core.SmException;
import com.ericsson.raso.sef.smart.SmartServiceResolver;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberInfo;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberResponseStore;
import com.ericsson.raso.sef.smart.usecase.VersionCreateOrWriteROPRequest;
import com.ericsson.sef.bes.api.entities.Meta;
import com.ericsson.sef.bes.api.subscriber.ISubscriberRequest;
import com.hazelcast.core.ISemaphore;

public class VersionCreateOrWriteRop implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(VersionCreateOrWriteRop.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		
			VersionCreateOrWriteROPRequest request = (VersionCreateOrWriteROPRequest) exchange.getIn().getBody();
				List<Meta> metas = new ArrayList<Meta>();
				metas.add(new Meta("key", String.valueOf(request.getKey())));
				metas.add(new Meta("vValidFrom", request.getvValidFrom()));
				metas.add(new Meta("vInvalidFrom", request.getvInvalidFrom()));
				metas.add(new Meta("s_OfferId", request.getS_OfferId()));
				metas.add(new Meta("category", request.getCategory()));
				metas.add(new Meta("messageId", String.valueOf(request.getMessageId())));
				metas.add(new Meta("precision", String.valueOf(request.getPrecision())));	
				metas.add(new Meta("expiryDate", DateUtil.convertISOToSimpleDateFormat(request.getExpiryDate())));
				String requestId = RequestContextLocalStore.get().getRequestId();
			    updateSubscriber(requestId, request.getCustomerId(), metas);
			    logger.info("Sending subscriber response");
				DummyProcessor.response(exchange);
		
	}
	private SubscriberInfo updateSubscriber(String requestId, String customer_id,List<Meta> metas) throws SmException {
		logger.info("Invoking update subscriber on tx-engine subscriber interface");
		ISubscriberRequest iSubscriberRequest = SmartServiceResolver.getSubscriberRequest();
		SubscriberInfo subInfo = new SubscriberInfo();
		SubscriberResponseStore.put(requestId, subInfo);
		iSubscriberRequest.updateSubscriber(requestId, customer_id, metas);
		
		ISemaphore semaphore = SefCoreServiceResolver.getCloudAwareCluster().getSemaphore(requestId);
		
		try {
		semaphore.init(0);
		semaphore.acquire();
		} catch(InterruptedException e) {
			
		}
		logger.info("Check if response received for update subscriber");
		SubscriberInfo subscriberInfo = (SubscriberInfo) SubscriberResponseStore.remove(requestId);
		if(subscriberInfo != null){
		System.out.println(subscriberInfo.getStatus());
		if(subscriberInfo.getStatus() != null){
		if(subscriberInfo.getStatus().getCode() > 0){
			ResponseCode resonseCode = new ResponseCode(subscriberInfo.getStatus().getCode(),subscriberInfo.getStatus().getDescription());
			throw new SmException(resonseCode);
		}
	}
}
		return subscriberInfo;
	}
	
}
