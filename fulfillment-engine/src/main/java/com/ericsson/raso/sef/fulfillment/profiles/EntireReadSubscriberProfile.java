package com.ericsson.raso.sef.fulfillment.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.client.air.command.GetAccountDetailsCommand;
import com.ericsson.raso.sef.client.air.command.GetBalanceAndDateCommand;
import com.ericsson.raso.sef.client.air.command.GetUsageThresholdsAndCountersCmd;
import com.ericsson.raso.sef.client.air.request.GetAccountDetailsRequest;
import com.ericsson.raso.sef.client.air.request.GetBalanceAndDateRequest;
import com.ericsson.raso.sef.client.air.request.GetUsageThresholdsAndCountersRequest;
import com.ericsson.raso.sef.client.air.response.AccountFlags;
import com.ericsson.raso.sef.client.air.response.DedicatedAccountInformation;
import com.ericsson.raso.sef.client.air.response.GetAccountDetailsResponse;
import com.ericsson.raso.sef.client.air.response.GetBalanceAndDateResponse;
import com.ericsson.raso.sef.client.air.response.GetUsageThresholdsAndCountersResponse;
import com.ericsson.raso.sef.client.air.response.OfferInformation;
import com.ericsson.raso.sef.client.air.response.SubDedicatedInfo;
import com.ericsson.raso.sef.client.air.response.UsageCounterUsageThresholdInformation;
import com.ericsson.raso.sef.client.air.response.UsageThresholdInformation;
import com.ericsson.raso.sef.core.ResponseCode;
import com.ericsson.raso.sef.core.SmException;
import com.ericsson.raso.sef.fulfillment.commons.FulfillmentException;
import com.ericsson.sef.bes.api.entities.Product;

public class EntireReadSubscriberProfile extends BlockingFulfillment<Product> {
	private static final long serialVersionUID = 2168287678631825571L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EntireReadSubscriberProfile.class);

	//Get Account Details...
	private static final String READ_SUBSCRIBER_ACTIVATION_DATE = "READ_SUBSCRIBER_ACTIVATION_DATE";
	private static final String READ_SUBSCRIBER_SUPERVISION_EXPIRY_DATE = "READ_SUBSCRIBER_SUPERVISION_EXPIRY_DATE";
	private static final String READ_SUBSCRIBER_SERVICE_FEE_EXPIRY_DATE = "READ_SUBSCRIBER_SERVICE_FEE_EXPIRY_DATE";
	private static final String READ_SUBSCRIBER_ACTIVATION_STATUS_FLAG = "READ_SUBSCRIBER_ACTIVATION_STATUS_FLAG";
	private static final String READ_SUBSCRIBER_NEGATIVE_BARRING_STATUS_FLAG = "READ_SUBSCRIBER_NEGATIVE_BARRING_STATUS_FLAG";
	private static final String READ_SUBSCRIBER_SUPERVISION_PERIOD_WARNING_ACTIVE_FLAG = "READ_SUBSCRIBER_SUPERVISION_PERIOD_WARNING_ACTIVE_FLAG";
	private static final String READ_SUBSCRIBER_SERVICE_FEE_PERIOD_WARNING_ACTIVE_FLAG = "READ_SUBSCRIBER_SERVICE_FEE_PERIOD_WARNING_ACTIVE_FLAG";
	private static final String READ_SUBSCRIBER_SUPERVISION_PERIOD_EXPIRY_FLAG = "READ_SUBSCRIBER_SUPERVISION_PERIOD_EXPIRY_FLAG";
	private static final String READ_SUBSCRIBER_SERVICE_FEE_PERIOD_EXPIRY_FLAG = "READ_SUBSCRIBER_SERVICE_FEE_PERIOD_EXPIRY_FLAG";
	private static final String READ_SUBSCRIBER_TWO_STEP_ACTIVATION_FLAG = "READ_SUBSCRIBER_TWO_STEP_ACTIVATION_FLAG";
	private static final String	READ_SUBSCRIBER_OFFER_INFO	= "READ_SUBSCRIBER_OFFER_INFO";
	private static final String	READ_SUBSCRIBER_SERVICE_OFFERING	= "READ_SUBSCRIBER_SERVICE_OFFERING";	
	//Get Balance & Dates...
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_ID	= "READ_BALANCES_DEDICATED_ACCOUNT_ID";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_VALUE_1	= "READ_BALANCES_DEDICATED_ACCOUNT_VALUE_1";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_VALUE_2	= "READ_BALANCES_DEDICATED_ACCOUNT_VALUE_2";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_EXPIRY_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_EXPIRY_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_START_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_START_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_PAM_SERVICE_ID	= "READ_BALANCES_DEDICATED_ACCOUNT_PAM_SERVICE_ID";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_OFFER_ID	= "READ_BALANCES_DEDICATED_ACCOUNT_OFFER_ID";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_PRODUCT_ID	= "READ_BALANCES_DEDICATED_ACCOUNT_PRODUCT_ID";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_REAL_MONEY_FLAG	= "READ_BALANCES_DEDICATED_ACCOUNT_REAL_MONEY_FLAG";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_1	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_1";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_2	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_2";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_1	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_1";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_2	= "READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_2";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_1	= "READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_1";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_2	= "READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_2";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_START_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_START_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_EXPIRY_DATE	= "READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_EXPIRY_DATE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_1	= "READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_1";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_2	= "READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_2";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_UNIT_TYPE	= "READ_BALANCES_DEDICATED_ACCOUNT_UNIT_TYPE";
	private static final String	READ_BALANCES_DEDICATED_ACCOUNT_COMPOSITE_DA_FLAG	= "READ_BALANCES_DEDICATED_ACCOUNT_COMPOSITE_DA_FLAG";
	private static final String READ_BALANCES_OFFER_INFO_OFFER_ID = "READ_BALANCES_OFFER_ID";
	private static final String READ_BALANCES_OFFER_INFO_START_DATE = "READ_BALANCES_START_DATE";
	private static final String READ_BALANCES_OFFER_INFO_EXPIRY_DATE = "READ_BALANCES_EXPIRY_DATE";
	private static final String READ_BALANCES_OFFER_INFO_START_DATE_TIME = "READ_BALANCES_START_DATE_TIME";
	private static final String READ_BALANCES_OFFER_INFO_EXPIRY_DATE_TIME = "READ_BALANCES_EXPIRY_DATE_TIME";
	

	public EntireReadSubscriberProfile(String name) {
		super(name);
	}
	@Override
	public List<Product> fulfill(Product e, Map<String, String> map) throws FulfillmentException {
		List<Product> returned = new ArrayList<Product>();
		returned.add(e);
		return returned;	
	}

	@Override
	public List<Product> prepare(Product e, Map<String, String> map) throws FulfillmentException {
		List<Product> returned = new ArrayList<Product>();
		returned.add(e);
		return returned;	
	}

	@Override
	public List<Product> query(Product e, Map<String, String> map) throws FulfillmentException {
		LOGGER.debug("Request for Get Account Details...");

		HashMap<String, String> details = new HashMap<String, String>();

		if (map == null || map.isEmpty())
			throw new FulfillmentException("ffe", new ResponseCode(1001, "runtime parameters 'metas' missing in request!!"));

		// Get account details
		GetAccountDetailsRequest accountDetailsRequest = new GetAccountDetailsRequest();
		String subscriberId = map.get("SUBSCRIBER_ID");
		if (subscriberId == null)
			throw new FulfillmentException("ffe", new ResponseCode(1002, "runtime parameter 'SUBSCRIBER_ID' missing in request!!"));
		accountDetailsRequest.setSubscriberNumber(subscriberId);
		accountDetailsRequest.setSubscriberNumberNAI(1);

		GetAccountDetailsResponse accountDetailsResponse = null;
		GetAccountDetailsCommand accountDetailsCommand = new GetAccountDetailsCommand(accountDetailsRequest);

		try {
			accountDetailsResponse = accountDetailsCommand.execute();
		} catch (SmException e1) {
			LOGGER.debug("AIR Exception: " + e1.getMessage(), e);
			throw new FulfillmentException(e1.getComponent(), new ResponseCode(e1.getStatusCode().getCode(), e1.getStatusCode().getMessage()));
		}

		this.processAccountDetailsResponse(details, accountDetailsResponse);

		LOGGER.debug("Query request for read balances...");

		// Get balance and dates
		GetBalanceAndDateRequest balanceAndDateRequest = new GetBalanceAndDateRequest();
		balanceAndDateRequest.setSubscriberNumber(subscriberId);
		balanceAndDateRequest.setSubscriberNumberNAI(1);

		GetBalanceAndDateResponse balanceAndDateResponse = null;
		GetBalanceAndDateCommand balanceAndDateCommand = new GetBalanceAndDateCommand(balanceAndDateRequest);

		try {
			balanceAndDateResponse = balanceAndDateCommand.execute();
		} catch (SmException e1) {
			LOGGER.debug("AIR Exception: " + e1.getMessage(), e);
			throw new FulfillmentException(e1.getComponent(), new ResponseCode(e1.getStatusCode().getCode(), e1.getStatusCode().getMessage()));
		}

		this.processBalanceAndDateResponse(details, balanceAndDateResponse);
		
		// Usage Counter and Thresholds
		
		GetUsageThresholdsAndCountersRequest usageRequest = new GetUsageThresholdsAndCountersRequest();
		usageRequest.setSubscriberNumber(subscriberId);
		usageRequest.setSubscriberNumberNAI(1);
		
		
		GetUsageThresholdsAndCountersResponse usageResponse = null;
		
		GetUsageThresholdsAndCountersCmd usageCommand = new GetUsageThresholdsAndCountersCmd(usageRequest);
		
		try {
			usageResponse = usageCommand.execute();
		} catch (SmException e1) {
			LOGGER.debug("AIR Exception: " + e1.getMessage(), e1);
			throw new FulfillmentException(e1.getComponent(), new ResponseCode(e1.getStatusCode().getCode(), e1.getStatusCode().getMessage()));
		}
		this.processUsageThreadholdAndCountersResponse(details, usageResponse);

		List<Product> products = new ArrayList<Product>();
		Product product = new Product();
		product.setResourceName(e.getResourceName());
		product.setQuotaDefined(-1);
		product.setValidity(-1);product.setMetas(details);
		products.add(product);
		return products; 


	}

	private void processUsageThreadholdAndCountersResponse(HashMap<String, String> details, GetUsageThresholdsAndCountersResponse response) {
		
		details.put("CURRENCY1", response.getCurrency1());
		details.put("CURRENCY2", response.getCurrency2());
		
		
		String usageInfo = "";
		for (UsageCounterUsageThresholdInformation uct: response.getUsageCounterUsageThresholdInformation()) {
			usageInfo += (usageInfo.isEmpty()?"":"|+|") + uct.getUsageCounterID() 
				+ "," + ((uct.getProductID()==null)?"null":uct.getProductID()) 
				+ "," + uct.getAssociatedPartyID() 
				+ "," + uct.getUsageCounterMonetaryValue1() 
				+ "," + uct.getUsageCounterMonetaryValue2()
				+ "," + uct.getUsageCounterValue();
			
			String utInfo = "";
			for (UsageThresholdInformation ut: uct.getUsageThresholdInformation()) {
				utInfo += utInfo.isEmpty()?"":"+" + ut.getUsageThresholdID() 
						+ ":" + ut.getAssociatedPartyID()
						+ ":" + ut.getUsageThresholdMonetaryValue1()
						+ ":" + ut.getUsageThresholdMonetaryValue2()
						+ ":" + ut.getUsageThresholdValue()
						+ ":" + ut.getUsageThresholdSource();
			}
			usageInfo += utInfo;
		}
		
		details.put("USAGE_COUNT_THRESHOLD", usageInfo);
	}
	
	
	@Override
	public List<Product> revert(Product e, Map<String, String> map) throws FulfillmentException {
		List<Product> returned = new ArrayList<Product>();
		returned.add(e);
		return returned;	
	}

	private void processAccountDetailsResponse(HashMap<String, String> accountDetails, GetAccountDetailsResponse response) {
		// direct attributes...
		LOGGER.debug("processAccountDetailsResponse ."+response);
	
		
		if (response.getActivationDate() != null)
			accountDetails.put(READ_SUBSCRIBER_ACTIVATION_DATE, "" + response.getActivationDate().getTime());

		if (response.getSupervisionExpiryDate() != null)
			accountDetails.put(READ_SUBSCRIBER_SUPERVISION_EXPIRY_DATE, "" + response.getSupervisionExpiryDate().getTime());

		if (response.getServiceFeeExpiryDate() != null)
			accountDetails.put(READ_SUBSCRIBER_SERVICE_FEE_EXPIRY_DATE, "" + response.getServiceFeeExpiryDate().getTime());

		LOGGER.debug("Packed all date attributes...");

		
		// service offerings
		int index = 0;
		for (com.ericsson.raso.sef.client.air.response.ServiceOffering serviceOffering: response.getServiceOfferings()) {
			LOGGER.debug("extracting service offering: " + serviceOffering.getServiceOfferingID());
			accountDetails.put(READ_SUBSCRIBER_SERVICE_OFFERING + "." + ++index, "" + serviceOffering.getServiceOfferingID() + "," + serviceOffering.isServiceOfferingActiveFlag());
		}
		LOGGER.debug("Packed all service offerings...");

		

		// account flags
		AccountFlags accountFlags = response.getAccountFlags();
		Boolean flag = accountFlags.isActivationStatusFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_ACTIVATION_STATUS_FLAG, "" + flag);

		flag = accountFlags.isNegativeBarringStatusFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_NEGATIVE_BARRING_STATUS_FLAG, "" + flag);

		flag = accountFlags.isSupervisionPeriodWarningActiveFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_SUPERVISION_PERIOD_WARNING_ACTIVE_FLAG, "" + flag);

		flag = accountFlags.isServiceFeePeriodWarningActiveFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_SERVICE_FEE_PERIOD_WARNING_ACTIVE_FLAG, "" + flag);

		flag = accountFlags.isSupervisionPeriodExpiryFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_SUPERVISION_PERIOD_EXPIRY_FLAG, "" + flag);

		flag = accountFlags.isServiceFeePeriodExpiryFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_SERVICE_FEE_PERIOD_EXPIRY_FLAG, "" + flag);

		flag = accountFlags.isTwoStepActivationFlag();
		if (flag != null)
			accountDetails.put(READ_SUBSCRIBER_TWO_STEP_ACTIVATION_FLAG, "" + flag);

		LOGGER.debug("Packed all account flags...");

		
		
		// offer info...
		index = 0;
		if (response.getOfferInformationList() != null) {
		
			LOGGER.debug("OfferInformationList is not null....Yay!!!!!!!!.." );
			
			for (OfferInformation offerInformation: response.getOfferInformationList()) {
				accountDetails.put((READ_SUBSCRIBER_OFFER_INFO) + "." + (++index), (offerInformation.getOfferID() + 
						"," + ((offerInformation.getStartDate()==null)?"null":offerInformation.getStartDate().getTime()) + 
						"," + ((offerInformation.getStartDateTime()==null)?"null":offerInformation.getStartDateTime().getTime()) + 
						"," + ((offerInformation.getExpiryDate()==null)?"null":offerInformation.getExpiryDate().getTime()) + 
						"," + ((offerInformation.getExpiryDateTime()==null)?"null":offerInformation.getExpiryDateTime().getTime())));
			}
		}
		else
			LOGGER.debug("OfferInformationList is null." );

		
		LOGGER.debug("Packed all offer info..." + accountDetails.toString());
	}


	private void processBalanceAndDateResponse(HashMap<String, String> balanceAndDateInfo, GetBalanceAndDateResponse response) {
		// Dedicated Accounts...
		int index = 0;
		for (DedicatedAccountInformation daInformation: response.getDedicatedAccountInformation()) {
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_ID + "." + ++index, "" + daInformation.getDedicatedAccountID());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_VALUE_1 + "." + index, "" + daInformation.getDedicatedAccountValue1());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_VALUE_2 + "." + index, "" + daInformation.getDedicatedAccountValue2());

			if(daInformation.getExpiryDate() != null)
				balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_EXPIRY_DATE + "." + index, "" + daInformation.getExpiryDate().getTime());

			if(daInformation.getStartDate() != null)
				balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_START_DATE + "." + index, "" + daInformation.getStartDate().getTime());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_PAM_SERVICE_ID + "." + index, "" + daInformation.getPamServiceID());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_OFFER_ID + "." + index, "" + daInformation.getOfferID());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_PRODUCT_ID + "." + index, "" + daInformation.getProductID());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_REAL_MONEY_FLAG + "." + index, "" + daInformation.isDedicatedAccountRealMoneyFlag());

			if(daInformation.getClosestExpiryDate() != null)
				balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_DATE + "." + index, "" + daInformation.getClosestExpiryDate().getTime());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_1 + "." + index, "" + daInformation.getClosestExpiryValue1());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_EXPIRY_VALUE_2 + "." + index, "" + daInformation.getClosestExpiryValue2());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_DATE + "." + index, "" + daInformation.getClosestAccessibleDate());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_1 + "." + index, "" + daInformation.getClosestExpiryValue1());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_CLOSEST_ACCESSIBLE_VALUE_2 + "." + index, "" + daInformation.getClosestExpiryValue2());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_1 + "." + index, "" + daInformation.getDedicatedAccountActiveValue1());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_ACTIVE_VALUE_2 + "." + index, "" + daInformation.getDedicatedAccountActiveValue2());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_UNIT_TYPE + "." + index, "" + daInformation.getDedicatedAccountUnitType());
			balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_COMPOSITE_DA_FLAG + "." + index, "" + daInformation.isCompositeDedicatedAccountFlag());

			int subindex = 0;
			if(daInformation.getSubDedicatedAccountInformation() != null) {
				for (SubDedicatedInfo subDedicatedInfo: daInformation.getSubDedicatedAccountInformation()) {
					if(subDedicatedInfo != null) {
						if(subDedicatedInfo.getDedicatedAccountValue1() != null)
							balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_1 + "." + index+ "." + ++subindex, "" + subDedicatedInfo.getDedicatedAccountValue1());

						if(subDedicatedInfo.getDedicatedAccountValue2() != null)
							balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_VALUE_2 + "." + index+ "." + subindex, "" + subDedicatedInfo.getDedicatedAccountValue2());

						if(subDedicatedInfo.getStartDate() != null)
							balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_START_DATE + "." + index+ "." +  subindex, "" + subDedicatedInfo.getStartDate().getTime());

						if(subDedicatedInfo.getExpiryDate() != null)
							balanceAndDateInfo.put(READ_BALANCES_DEDICATED_ACCOUNT_SUB_DA_EXPIRY_DATE + "." + index+ "." +  subindex, "" + subDedicatedInfo.getExpiryDate().getTime());
					}
				}
			}
			
			String daInfo ="" + daInformation.getDedicatedAccountID() 
  					+ "," + daInformation.getDedicatedAccountValue1()
  					+ "," + daInformation.getDedicatedAccountValue2()
  					+ "," + ((daInformation.getStartDate()==null)?"null":daInformation.getStartDate().getTime())
  					+ "," + ((daInformation.getExpiryDate()==null)?"null":daInformation.getExpiryDate().getTime())
  					+ "," + ((daInformation.getPamServiceID()==null)?"null":daInformation.getPamServiceID())
  					+ "," + ((daInformation.getOfferID()==null)?"null":daInformation.getOfferID())
  					+ "," + ((daInformation.getProductID()==null)?"null":daInformation.getProductID())
  					+ "," + daInformation.isDedicatedAccountRealMoneyFlag()
  					+ "," + ((daInformation.getClosestExpiryDate()==null)?"null":daInformation.getClosestExpiryDate().getTime())
  					+ "," + ((daInformation.getClosestExpiryValue1()==null)?"null":daInformation.getClosestExpiryValue1())
  					+ "," + ((daInformation.getClosestExpiryValue2()==null)?"null":daInformation.getClosestExpiryValue2())
  					+ "," + ((daInformation.getClosestAccessibleDate()==null)?"null":daInformation.getClosestAccessibleDate())
  					+ "," + ((daInformation.getClosestAccessibleValue1()==null)?"null":daInformation.getClosestAccessibleValue1())
  					+ "," + ((daInformation.getClosestAccessibleValue2()==null)?"null":daInformation.getClosestAccessibleValue2())
  					+ "," + ((daInformation.getDedicatedAccountActiveValue1()==null)?"null":daInformation.getDedicatedAccountActiveValue1())
  					+ "," + ((daInformation.getDedicatedAccountActiveValue2()==null)?"null":daInformation.getDedicatedAccountActiveValue2())
  					+ "," + ((daInformation.getDedicatedAccountUnitType()==null)?"null":daInformation.getDedicatedAccountUnitType())
  					+ "," + daInformation.isCompositeDedicatedAccountFlag()	+ ":+:";
  			
  			if(daInformation.getSubDedicatedAccountInformation() != null) {
  				String subDA = "";
  				for (SubDedicatedInfo subDedicatedInfo: daInformation.getSubDedicatedAccountInformation()) {
  					subDA += (subDA.isEmpty()?"":"|||");
  					subDA += ((subDedicatedInfo.getDedicatedAccountValue1()==null)?"null":subDedicatedInfo.getDedicatedAccountValue1())
  							+ "," + ((subDedicatedInfo.getDedicatedAccountValue2()==null)?"null":subDedicatedInfo.getDedicatedAccountValue2())
  							+ "," + ((subDedicatedInfo.getStartDate()==null)?"null":subDedicatedInfo.getStartDate().getTime())
  							+ "," + ((subDedicatedInfo.getExpiryDate()==null)?"null":subDedicatedInfo.getExpiryDate().getTime());
  					daInfo += subDA; 
  				}
  			}
  			balanceAndDateInfo.put("DA" + "." + ++index, daInfo); 
		}
		LOGGER.debug("Packed all dedicated accounts...");
		// offer info...
		index = 0;
		for (OfferInformation offerInformation: response.getOfferInformationList()) {
			if(offerInformation != null) {
				balanceAndDateInfo.put(READ_BALANCES_OFFER_INFO_OFFER_ID + "." + ++index, "" + offerInformation.getOfferID());

				if(offerInformation.getStartDate() != null)
					balanceAndDateInfo.put(READ_BALANCES_OFFER_INFO_START_DATE + "." + index, "" + offerInformation.getStartDate().getTime());

				if(offerInformation.getStartDateTime() != null)
					balanceAndDateInfo.put(READ_BALANCES_OFFER_INFO_START_DATE_TIME + "." + index, "" + offerInformation.getStartDateTime().getTime());

				if(offerInformation.getExpiryDate() != null)
					balanceAndDateInfo.put(READ_BALANCES_OFFER_INFO_EXPIRY_DATE + "." + index, "" + offerInformation.getExpiryDate().getTime());

				if(offerInformation.getExpiryDateTime() != null)
					balanceAndDateInfo.put(READ_BALANCES_OFFER_INFO_EXPIRY_DATE_TIME + "." + index, "" + offerInformation.getExpiryDateTime().getTime());
			}
		}
		
		


		
		
		LOGGER.debug("Packed all offer info...");
}

	@Override
	public String toString() {
		return "EntireReadSubscriberProfile []";
	}


}
