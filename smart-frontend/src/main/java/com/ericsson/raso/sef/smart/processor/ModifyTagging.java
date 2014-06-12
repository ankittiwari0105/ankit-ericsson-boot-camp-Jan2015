package com.ericsson.raso.sef.smart.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.core.RequestContextLocalStore;
import com.ericsson.raso.sef.core.SefCoreServiceResolver;
import com.ericsson.raso.sef.core.SmException;
import com.ericsson.raso.sef.smart.SmartServiceResolver;
import com.ericsson.raso.sef.smart.commons.SmartConstants;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberInfo;
import com.ericsson.raso.sef.smart.subscriber.response.SubscriberResponseStore;
import com.ericsson.raso.sef.smart.usecase.ModifyTaggingRequest;
import com.ericsson.sef.bes.api.entities.Meta;
import com.ericsson.sef.bes.api.subscriber.ISubscriberRequest;
import com.hazelcast.core.ISemaphore;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.CommandResponseData;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.CommandResult;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.IntParameter;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.Operation;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.OperationResult;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.ParameterList;
import com.nsn.ossbss.charge_once.wsdl.entity.tis.xsd._1.TransactionResult;

public class ModifyTagging implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(ModifyTagging.class);

	@Override
	public void process(Exchange exchange) throws SmException {

		ModifyTaggingRequest request = (ModifyTaggingRequest) exchange.getIn().getBody();
		String requestId = RequestContextLocalStore.get().getRequestId();
		//SubscriberInfo validSubscriberInfo = validateSubscriber(requestId, request.getCustomerId());
		//ContractState localState = null;
		/* if(validSubscriberInfo != null){
			 localState = validSubscriberInfo.getLocalState();
			 logger.debug("Status of Valid Subscriber is :", localState);
		} */
		Integer tag = Integer.valueOf(request.getTagging());

		// SubscriberManagement subscriberManagement = SmartContext.getSubscriberManagement();
		List<String> keys = new ArrayList<String>();
		keys.add(SmartConstants.GRACE_ENDDATE);

		List<Meta> metas = new ArrayList<Meta>();
		metas.add(new Meta("tagging", String.valueOf(request.getTagging())));
		metas.add(new Meta("eventInfo", String.valueOf(request.getEventInfo())));
		metas.add(new Meta("messageId", String.valueOf(request.getMessageId())));

		String tagging = String.valueOf(request.getTagging());
		String handle_life_cycle=null;
		switch(tagging) {
			case "0":
				handle_life_cycle="resetBit";
				
				break;
			case "1":
				handle_life_cycle="forcedDeleteBit";
				break;
			case "2":
				handle_life_cycle="barGeneralBit";
				break;
			case "3":
				handle_life_cycle="barIrmBit";
				break;
			case "4":
				handle_life_cycle="barOtherBit";
				break;
			case "5":
				handle_life_cycle="specialFraudBit";
				break;
			case "6":
				handle_life_cycle="accountBlockingBit";
				break;
			case "7":
				handle_life_cycle="recycleBit";
				break;
			default:
				
		}
		metas.add(new Meta("HANDLE_LIFE_CYCLE", handle_life_cycle));
		
		logger.debug("Usecase Metas: " + metas);

		// subscriberManagement.updateSubscriber(request.getCustomerId(), metas);

		updateSubscriber(requestId, request.getCustomerId(), metas);
        
		exchange.getOut().setBody(createResponse(tag, request.isTransactional()));

	}

	private CommandResponseData createResponse(int tag, boolean isTransactional) {
		CommandResponseData responseData = new CommandResponseData();
		CommandResult result = new CommandResult();
		responseData.setCommandResult(result);

		OperationResult operationResult = new OperationResult();

		if (isTransactional) {
			TransactionResult transactionResult = new TransactionResult();
			result.setTransactionResult(transactionResult);
			transactionResult.getOperationResult().add(operationResult);
		} else {
			result.setOperationResult(operationResult);
		}

		Operation operation = new Operation();
		operation.setModifier("Tagging");
		operation.setName("Modify");
		operationResult.getOperation().add(operation);

		ParameterList parameterList = new ParameterList();
		List<Object> dataSetList = parameterList.getParameterOrBooleanParameterOrByteParameter();

		IntParameter intParameter = new IntParameter();
		intParameter.setName("ResultTagging");
		intParameter.setValue(tag);
		dataSetList.add(intParameter);

		operation.setParameterList(parameterList);

		return responseData;
	}

	private SubscriberInfo updateSubscriber(String requestId, String customer_id, List<Meta> metas) {
		logger.info("Invoking update subscriber on tx-engine subscriber interface");
		
		ISubscriberRequest iSubscriberRequest = SmartServiceResolver.getSubscriberRequest();
		SubscriberInfo subInfo = new SubscriberInfo();
		
		SubscriberResponseStore.put(requestId, subInfo);
		iSubscriberRequest.handleLifeCycle(requestId, customer_id, null, metas);
		
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
