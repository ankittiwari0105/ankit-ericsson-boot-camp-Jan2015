package com.ericsson.bootcamp;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;





//import org.json.JSONException;
//import org.json.JSONObject;

import com.ericsson.raso.sef.bes.prodcat.CatalogException;
import com.ericsson.raso.sef.bes.prodcat.OfferContainer;
//import com.ericsson.raso.sef.bes.prodcat.OfferManager;
import com.ericsson.raso.sef.bes.prodcat.entities.AbstractMinimumCommitment;
import com.ericsson.raso.sef.bes.prodcat.entities.AtomicProduct;
import com.ericsson.raso.sef.bes.prodcat.entities.CommitHardDate;
import com.ericsson.raso.sef.bes.prodcat.entities.CommitUntilNDays;
import com.ericsson.raso.sef.bes.prodcat.entities.CommitUntilNRenewals;
import com.ericsson.raso.sef.bes.prodcat.entities.Cost;
import com.ericsson.raso.sef.bes.prodcat.entities.DaysTime;
import com.ericsson.raso.sef.bes.prodcat.entities.EndUser;
import com.ericsson.raso.sef.bes.prodcat.entities.HardDateTime;
import com.ericsson.raso.sef.bes.prodcat.entities.HoursTime;
import com.ericsson.raso.sef.bes.prodcat.entities.ImmediateTermination;
import com.ericsson.raso.sef.bes.prodcat.entities.InfiniteTime;
import com.ericsson.raso.sef.bes.prodcat.entities.LimitedQuota;
import com.ericsson.raso.sef.bes.prodcat.entities.Market;
import com.ericsson.raso.sef.bes.prodcat.entities.NoCommitment;
import com.ericsson.raso.sef.bes.prodcat.entities.NoTermination;
import com.ericsson.raso.sef.bes.prodcat.entities.Offer;
import com.ericsson.raso.sef.bes.prodcat.entities.Opco;
import com.ericsson.raso.sef.bes.prodcat.entities.OpcoGroup;
import com.ericsson.raso.sef.bes.prodcat.entities.Owner;
import com.ericsson.raso.sef.bes.prodcat.entities.Partner;
import com.ericsson.raso.sef.bes.prodcat.entities.Price;
import com.ericsson.raso.sef.bes.prodcat.entities.PricingPolicy;
import com.ericsson.raso.sef.bes.prodcat.entities.Product;
import com.ericsson.raso.sef.bes.prodcat.entities.Resource;
import com.ericsson.raso.sef.bes.prodcat.entities.Service;
import com.ericsson.raso.sef.bes.prodcat.entities.State;
import com.ericsson.raso.sef.bes.prodcat.entities.SubscriberType;
import com.ericsson.raso.sef.bes.prodcat.entities.Tax;
import com.ericsson.raso.sef.bes.prodcat.entities.TaxAbsolute;
import com.ericsson.raso.sef.bes.prodcat.entities.TaxFree;
import com.ericsson.raso.sef.bes.prodcat.entities.TaxPercentage;
import com.ericsson.raso.sef.bes.prodcat.entities.TenantMvno;
import com.ericsson.raso.sef.bes.prodcat.entities.TerminateAfterNDays;
import com.ericsson.raso.sef.bes.prodcat.entities.TerminateAfterNRenewals;
import com.ericsson.raso.sef.bes.prodcat.entities.TerminateHardDate;
import com.ericsson.raso.sef.bes.prodcat.entities.UnlimitedQuota;
import com.ericsson.raso.sef.bes.prodcat.entities.smart.SmartLIfeCyclePricingPolicy;
import com.ericsson.raso.sef.bes.prodcat.entities.smart.SmartSimplePricingPolicy;
import com.ericsson.raso.sef.bes.prodcat.service.IOfferAdmin;
//import com.ericsson.raso.sef.catalog.response.GuiConstants;
//import com.ericsson.raso.sef.catalog.response.GuiConstants;
import com.ericsson.raso.sef.core.DateUtil;
import com.ericsson.raso.sef.core.FrameworkException;
import com.ericsson.raso.sef.core.SecureSerializationHelper;
import com.ericsson.raso.sef.ruleengine.Rule;

public class OfferController {
	//public static void main(String[] args){
	
	
	OfferManagerEx offerManager=new OfferManagerEx();
	public static String offerStoreLocation = null;;
	
	//private OfferManagerEx offerManager = null;

	private SecureSerializationHelper ssh = null;
	//SecureSerializationHelper ssh = new SecureSerializationHelper();
	//private OfferContainer container = new OfferContainer();

	private void createOffer(){
		try{
		System.out.println("Enter Offer Name :");
		String offerName="Vekomy16";
		/* Scanner scanIn2 = new Scanner(System.in);
		 offerName = scanIn2.nextLine();
	 
	       scanIn2.close(); */
		System.out.println("offername is:"+offerName);
		Offer offer = new Offer(offerName);
		offer.setOfferState(State.IN_CREATION);

		this.prepareOffer(offer);
		try {
			
		offerManager.createOffer(offer);
			//response.setStatus(GuiConstants.SUCCESS);
			//response.setMessage("Offer Saved Successfully");
		} catch (CatalogException e) {
			e.printStackTrace();
			//response.setStatus(GuiConstants.FAILURE);
			//response.setMessage(e.getMessage());
		}
	   } catch (CatalogException ce) {
			ce.printStackTrace();
			//response.setStatus(GuiConstants.FAILURE);
			//response.setMessage(ce.getMessage());
		}

		return ;
	}
	private void updateOffer(){
		try {
			//JSONObject json = new JSONObject(str.trim());
			Offer offer = this.offerManager.getOfferById(("vekomy"));

			List<AtomicProduct> atomicProducts = offer.getAllAtomicProducts();
			for (AtomicProduct atomicProduct : atomicProducts) {
				offer.removeProduct(atomicProduct);
			}

			Set<String> externalHandlers = offer.getExternalHandles();

			if (externalHandlers != null) {
				for (String externalHandler : externalHandlers) {
					offer.removeExternalHandle(externalHandler);
				}
			}

			this.prepareOffer(offer);

			try {
				offer.validate(true);
				this.offerManager.updateOffer(offer);
				//response.setStatus(GuiConstants.SUCCESS);
			} catch (CatalogException e) {
				e.printStackTrace();
				//response.setStatus(GuiConstants.FAILURE);
				//response.setMessage(e.getMessage());
			}

		} catch (CatalogException ce) {
			ce.printStackTrace();
			//response.setStatus(GuiConstants.FAILURE);
			//response.setMessage(ce.getMessage());
		}
		return ;
	}
	
	
	
	
	private void prepareOffer(Offer offer)
			throws CatalogException {
		Resource resource; 
		resource = new Service("KaTOK25");
		resource.setDescription("KaTOK25 Calling Circle Profile");
		resource.setConsumable(true);
		resource.setDiscoverable(true);
		resource.setExternallyConsumed(true);
		resource.setConsumptionUnitName("PHP");
		/*FulfillmentProfile fulfillmentProfile;
		fulfillmentProfile = new CallingCircleProfile("KaTOK25");
		fulfillmentProfile.setRefillProfileId("KT25");
		fulfillmentProfile.setRefillType(1);
		fulfillmentProfile.setTransactionAmount("0");
		fulfillmentProfile.setTransactionCurrency(CurrencyCode.PHP);
		fulfillmentProfile.setPurchaseAmount("100");
		fulfillmentProfile.setRenewalAmount("999900");
		
		fulfillmentProfile.setFafIndicatorSponsorMember("300");
		fulfillmentProfile.setFafIndicatorMemberSponsor("301");
		fulfillmentProfile.setFafIndicatorMemberMember("999");
		fulfillmentProfile.setFafAccumulatorId((201));
		fulfillmentProfile.setProdcatOffer("KT25");
		fulfillmentProfile.setMaxMembers(1);
		fulfillmentProfile.setAssociatedPromo("Katok25Promo");
		fulfillmentProfile.setWelcomeMessageEventId("cc-success-enrollment");
		fulfillmentProfile.setA_PartyMemberThresholdBreachMessageEventId("090111519011");
		
		fulfillmentProfile.setCallingCircleCsOfferID(2017);
		fulfillmentProfile.setFreebieType("REFILL");
		fulfillmentProfile.setFreebieRefillID("KF25");
		fulfillmentProfile.setFreebieRefillType(1);
		fulfillmentProfile.setFreebiePlanCode("GR007");
		fulfillmentProfile.setFreebieTransactionAmount("1");
		fulfillmentProfile.setFreebieTransactionCurrency(CurrencyCode.PHP);
		fulfillmentProfile.setFreebieRenewalAmount("1");
		fulfillmentProfile.setFreebiePurchaseAmount("1");*/
		
		//resource.addFulfillmentProfile(fulfillmentProfile.getName());
		//profileRegistry.createProfile(fulfillmentProfile);
		
		
		
		
		
		//templatedOffer = new Offer("ArawArawLoad60");
		offer.setDescription("Araw Araw Load 60 Offer5");
		offer.setAutoTermination(new TerminateAfterNDays(7));
		offer.setMinimumCommitment(new CommitUntilNDays(7));
		offer.setRenewalPeriod(new DaysTime(1));
		offer.setOfferState(State.TESTING);
		offer.setOfferState(State.PUBLISHED);
		offer.setRecurrent(true);
		offer.setCommercial(false);
		offer.addExternalHandle("GR029");
		Product product ;
		product = new AtomicProduct("ArawArawLoad60");
		//product.setQuota(new UnlimitedQuota());
		((AtomicProduct) product).setResource(resource);
		//product.setValidity(new DaysTime(1));

		offer.addProduct(product); 
		//offerManager.createOffer(templatedOffer);

		//offer.setDescription("description");
		//offer.setOfferGroup("offerGroup");

		//boolean recurrent = false;

		/*if (!json.isNull("recurrent")
				&& (json.getString("recurrent").equalsIgnoreCase("true") || json
						.getString("recurrent").equalsIgnoreCase("false"))) {
			recurrent = json.getBoolean("recurrent");
			offer.setRecurrent(recurrent);
		}*/

		/*if (!json.isNull("renewalPeriod")) {

			if (json.getString("renewalPeriod").equalsIgnoreCase("infinite")) {
				if (recurrent) {
					throw new CatalogException(
							"Recurrence is enabled but validity period is infnite!!");
				} else {
					offer.setRenewalPeriod(new InfiniteTime());
				}
			} else if (json.getString("renewalPeriod").equalsIgnoreCase("days")) {

				if (json.get("renewalDays").equals("")) {
					throw new CatalogException(
							"Renewals days can not be empty!!");
				}

				offer.setRenewalPeriod(new DaysTime(json.getInt("renewalDays")));
			} else if (json.getString("renewalPeriod")
					.equalsIgnoreCase("hours")) {
				if (json.get("renewalHours").equals("")) {
					throw new CatalogException(
							"Renewals hours can not be empty!!");
				}

				if (json.getInt("renewalHours") < 0
						|| json.getInt("renewalHours") > 24) {
					throw new CatalogException(
							"Renewals hours should be greater than Zero and less than 24!!");
				}

				offer.setRenewalPeriod(new HoursTime(Byte.valueOf(json
						.getString("renewalHours"))));
			} else if (json.getString("renewalPeriod").equalsIgnoreCase("date")) {
				if (json.get("renewalDate").equals("")) {
					throw new CatalogException(
							"Renewals date can not be empty!!");
				}
				String renewalDateString = json.getString("renewalDate");
				Date renewal_date;
				try {
					renewal_date = DateUtil.toDate(renewalDateString,
							"yyyy-MM-dd");
					Calendar calendar1 = Calendar.getInstance();
					Calendar calendar2 = Calendar.getInstance();
					calendar1.setTime(new Date());
					calendar2.setTime(renewal_date);
					long milliseconds1 = calendar1.getTimeInMillis();
					long milliseconds2 = calendar2.getTimeInMillis();
					long diff = milliseconds2 - milliseconds1;
					int days = (int) (diff / (24 * 60 * 60 * 1000));
					offer.setRenewalPeriod(new DaysTime(days));
				} catch (ParseException e) {
					throw new CatalogException(e);
				}
			}

		}
*/
		/*if (!json.isNull("commercial")
				&& (json.getString("commercial").equalsIgnoreCase("true") || json
						.getString("commercial").equalsIgnoreCase("false"))) {
			offer.setCommercial(json.getBoolean("commercial"));

			if (json.getBoolean("commercial")) {

				if (json.get("cost").equals("")) {
					throw new CatalogException(
							"Cost can not be empty under price!!");
				}

				if (json.getInt("cost") <= 0) {
					throw new CatalogException(
							"Cost should be greater than Zero under price!!");
				}

				Price price = new Price(json.getString("currency"),
						json.getLong("cost"));

				// TODO: policies concept is not clear need to be add

				if (json.getString("pricePolicy").equals("")) {
					throw new CatalogException(
							"Enter Pricing Policy name under price!!");
				}

				if (json.getString("rule").equals("")) {
					throw new CatalogException("Select Rule under price!!");
				}

				PricingPolicy pricingPolicy = null;

				if (json.getString("policyType").equals(
						"SmartLIfeCyclePricingPolicy")) {

					if (json.get("purchaseCost").equals("")) {
						throw new CatalogException(
								"Purchase Cost can not be empty under price!!");
					}

					if (json.getInt("purchaseCost") <= 0) {
						throw new CatalogException(
								"Purchase Cost should be greater than Zero under price!!");
					}

					if (json.get("renewalCost").equals("")) {
						throw new CatalogException(
								"Renewal Cost can not be empty under price!!");
					}

					if (json.getInt("renewalCost") <= 0) {
						throw new CatalogException(
								"Renewal Cost should be greater than Zero under price!!");
					}

					pricingPolicy = new SmartLIfeCyclePricingPolicy(
							json.getString("pricePolicy"), new Cost(
									json.getString("currency"),
									json.getLong("purchaseCost")), new Cost(
									json.getString("currency"),
									json.getLong("renewalCost")));

				} else if (json.getString("policyType").equals(
						"SmartSimplePricingPolicy")) {

					pricingPolicy = new SmartSimplePricingPolicy(
							json.getString("pricePolicy"));

				} else {
					throw new CatalogException(
							"Select Policy Type under price!!");
				}

				pricingPolicy.setRule(new Rule(json.getString("rule")));

				price.addRatingRule(pricingPolicy);

				List<Tax> taxList = new ArrayList<Tax>();

				if (json.isNull("taxes")) {
					throw new CatalogException("Add Taxes under price!!");
				}

				JSONArray taxes = json.getJSONArray("taxes");
				for (int i = 0; i < taxes.length(); i++) {
					JSONObject tax = (JSONObject) taxes.get(i);
					if (tax.getString("taxType").equals("Absolute")) {

						if (tax.getString("value").equals("")) {
							throw new CatalogException(
									"Absolute Tax can not be empty under price!!");
						}

						TaxAbsolute taxAbsolute = new TaxAbsolute();

						taxAbsolute.setTaxAbsolute(tax.getLong("value"));

						taxList.add(taxAbsolute);

					} else if (tax.getString("taxType").equals("Free")) {
						taxList.add(new TaxFree());
					} else if (tax.getString("taxType").equals("Percentage")) {
						if (tax.get("value").equals("")) {
							throw new CatalogException(
									"Percentage Tax can not be empty under price!!");
						}

						if (tax.getInt("value") <= 0
								|| tax.getInt("value") >= 100) {
							throw new CatalogException(
									"Percentage Tax should be between Zero and 100 under price!!");
						}

						TaxPercentage taxPercentage = new TaxPercentage();

						taxPercentage.setTaxPercentile(Byte.valueOf(tax
								.getString("value")));

						taxList.add(taxPercentage);
					}
				}

				price.setTaxes(taxList);

				offer.setPrice(price);
			}
		}*/

		Owner owner = null;

		/*if (!json.isNull("ownerType")) {
			if (json.getString("ownerType").equalsIgnoreCase("EndUser")) {
				owner = new EndUser((String) json.get("ownerName"));
			} else if (json.getString("ownerType").equalsIgnoreCase("Market")) {
				owner = new Market((String) json.get("ownerName"));
			} else if (json.getString("ownerType").equalsIgnoreCase("Opco")) {
				owner = new Opco((String) json.get("ownerName"));
			} else if (json.getString("ownerType")
					.equalsIgnoreCase("OpcoGroup")) {
				owner = new OpcoGroup((String) json.get("ownerName"));
			} else if (json.getString("ownerType").equalsIgnoreCase("Partner")) {
				owner = new Partner((String) json.get("ownerName"));
			} else if (json.getString("ownerType")
					.equalsIgnoreCase("TenantMVO")) {
				owner = new TenantMvno((String) json.get("ownerName"));
			}

			offer.setOwner(owner);
		}*/
		//offer.setOwner(owner);
		// TODO: AbstractAccumulationPolicy need to be add

		// TODO: AbstractSwitchPolicy need to be add

		// NO_TERMINATION,
		// AFTER_X_DAYS,
		// AFTER_X_RENEWALS,
		// HARD_STOP;

		/*if (!json.isNull("autoTermination")
				&& json.get("autoTermination") != null) {
			if (json.get("autoTermination").equals("NO_TERMINATION")) {
				offer.setAutoTermination(new NoTermination());
			} else if (json.get("autoTermination").equals("AFTER_X_DAYS")) {
				if (json.get("autoTerminationDays").equals("")) {
					throw new CatalogException(
							"Auto Termination days can not be empty!!");
				}
				offer.setAutoTermination(new TerminateAfterNDays(json
						.getInt("autoTerminationDays")));
			} else if (json.get("autoTermination").equals("AFTER_X_RENEWALS")) {
				if (json.get("autoTerminationRenewals").equals("")) {
					throw new CatalogException(
							"Auto Termination Renewals can not be empty!!");
				}
				offer.setAutoTermination(new TerminateAfterNRenewals(json
						.getInt("autoTerminationRenewals")));
			} else if (json.get("autoTermination").equals("HARD_STOP")) {

				if (json.get("autoTerminationDate").equals("")) {
					throw new CatalogException(
							"Auto Termination date can not be empty!!");
				}

				String startDateString = json.getString("autoTerminationDate");
				Date hard_stop_date = null;
				try {
					hard_stop_date = DateUtil.toDate(startDateString,
							"yyyy-MM-dd");
					offer.setAutoTermination(new TerminateHardDate(
							hard_stop_date));
				} catch (ParseException e1) {
					e1.printStackTrace();
					throw new CatalogException(e1);
				}
			}
		}*/

		// NO_COMMITMENT,
		// UNTIL_X_DAYS,
		// UNTIL_X_RENEWALS,
		// HARD_LIMIT;

		/*if (!json.isNull("minimumCommitment")
				&& json.get("minimumCommitment") != null) {
			if (json.get("minimumCommitment").equals("NO_COMMITMENT")) {
				offer.setMinimumCommitment(new NoCommitment(
						AbstractMinimumCommitment.Type.NO_COMMITMENT));
			} else if (json.get("minimumCommitment").equals("UNTIL_X_DAYS")) {

				if (json.get("minimumCommitmentDays").equals("")) {
					throw new CatalogException(
							"Commitment days can not be empty!!");
				}
				offer.setMinimumCommitment(new CommitUntilNDays(json
						.getInt("minimumCommitmentDays")));
			} else if (json.get("minimumCommitment").equals("UNTIL_X_RENEWALS")) {
				if (json.get("minimumCommitmentRenewals").equals("")) {
					throw new CatalogException(
							"Commitment Renewals can not be empty!!");
				}
				offer.setMinimumCommitment(new CommitUntilNRenewals(json
						.getInt("minimumCommitmentRenewals")));
			} else if (json.get("minimumCommitment").equals("HARD_LIMIT")) {
				if (json.get("minimumCommitmentDate").equals("")) {
					throw new CatalogException(
							"Commitment Date can not be empty!!");
				}

				String commitmentDateString = json
						.getString("minimumCommitmentDate");
				Date hard_limit_date = null;

				try {
					hard_limit_date = DateUtil.toDate(commitmentDateString,
							"yyyy-MM-dd");
					offer.setMinimumCommitment(new CommitHardDate(
							hard_limit_date));
				} catch (ParseException e1) {
					e1.printStackTrace();
					throw new CatalogException(e1);
				}

			}
		}*/

		/*if (!json.isNull("trialPeriod")) {
			if (json.getString("trialPeriod").equalsIgnoreCase("infinite")) {
				throw new CatalogException("Trial Period cannot be inifite!!");
			} else if (json.getString("trialPeriod").equalsIgnoreCase("days")) {

				if (json.get("trialDays").equals("")) {
					throw new CatalogException("Trial days can not be empty!!");
				}

				offer.setTrialPeriod(new DaysTime(json.getInt("trialDays")));
			} else if (json.getString("trialPeriod").equalsIgnoreCase("hours")) {

				if (json.get("trialHours").equals("")) {
					throw new CatalogException("Trial hours can not be empty!!");
				}

				if (json.getInt("trialHours") < 0
						|| json.getInt("trialHours") > 24) {
					throw new CatalogException(
							"Trial hours should be greater than Zero and less than 24!!");
				}

				offer.setTrialPeriod(new HoursTime(Byte.valueOf(json
						.getString("trialHours"))));
			} else if (json.getString("trialPeriod").equalsIgnoreCase("date")) {

				if (json.get("trialDate").equals("")) {
					throw new CatalogException("Trial Date can not be empty!!");
				}

				String trialDateString = json.getString("trialDate");
				Date trial_date;
				try {
					trial_date = DateUtil.toDate(trialDateString, "yyyy-MM-dd");
					Calendar calendar1 = Calendar.getInstance();
					Calendar calendar2 = Calendar.getInstance();
					calendar1.setTime(new Date());
					calendar2.setTime(trial_date);
					long milliseconds1 = calendar1.getTimeInMillis();
					long milliseconds2 = calendar2.getTimeInMillis();
					long diff = milliseconds2 - milliseconds1;
					int days = (int) (diff / (24 * 60 * 60 * 1000));
					offer.setTrialPeriod(new DaysTime(days));
				} catch (ParseException e) {
					e.printStackTrace();
					throw new CatalogException(e);
				}
			}
		}*/

		ImmediateTermination it = new ImmediateTermination();
		/*if (!json.isNull("PREPAID")
				&& json.getString("PREPAID").equalsIgnoreCase("true"))
			it.setIsAllowed(SubscriberType.PREPAID, true);
		else
			it.setIsAllowed(SubscriberType.PREPAID, false);

		if (!json.isNull("POSTPAID")
				&& json.getString("POSTPAID").equalsIgnoreCase("true"))
			it.setIsAllowed(SubscriberType.POSTPAID, true);
		else
			it.setIsAllowed(SubscriberType.POSTPAID, false);

		if (!json.isNull("HYBRID")
				&& json.getString("HYBRID").equalsIgnoreCase("true"))
			it.setIsAllowed(SubscriberType.HYBRID, true);
		else
			it.setIsAllowed(SubscriberType.HYBRID, false);

		if (!json.isNull("CONVERGANT")
				&& json.getString("CONVERGANT").equalsIgnoreCase("true"))
			it.setIsAllowed(SubscriberType.CONVERGANT, true);
		else
			it.setIsAllowed(SubscriberType.CONVERGANT, false);

		offer.setImmediateTermination(it);
*/
		/*if (offer.getOfferState().name().equals("TESTING")
				|| offer.getOfferState().name().equals("PUBLISHED")) {
			if (json.isNull("products")
					|| !(json.get("products") instanceof JSONArray)
					|| json.getJSONArray("products").length() < 1) {
				throw new CatalogException("Products can not be empty!!");
			}
		}*/
      // String isNull="prod";
      // String atomicProduct="";
      // offer.addProduct(atomicProduct);
       //offer.addProduct(atomicProduct);
		/*if (! isNull("products")
				&& ("products") instanceof JSONArray)) {

			JSONArray products = json.getJSONArray("products");
			int productSize = products.length();

			for (int i = 0; i < productSize; i++) {

				JSONObject jsonProduct = (JSONObject) products.get(i);

				AtomicProduct atomicProduct = new AtomicProduct(
						jsonProduct.getString("name"));

				if (!jsonProduct.getString("owner").equals("")) {
					Owner productOwner = this.ownerManager
							.readOwner(jsonProduct.getString("owner"));
					atomicProduct.setOwner(productOwner);
				}

				if (!jsonProduct.getString("resource").equals("")) {
					Resource productResource = this.serviceRegistry
							.readResource(jsonProduct.getString("resource"));
					atomicProduct.setResource(productResource);
				}

				if (!jsonProduct.getString("quota").equals("")) {
					if (jsonProduct.getString("quota").equals("LIMITED")) {
						atomicProduct.setQuota(new LimitedQuota());
					} else if (jsonProduct.getString("quota").equals(
							"UNLIMITED")) {
						atomicProduct.setQuota(new UnlimitedQuota());
					}
				}

				if (!jsonProduct.isNull("productValidity")) {
					if (jsonProduct.getString("productValidity")
							.equalsIgnoreCase("infinite")) {
						atomicProduct.setValidity(new InfiniteTime());
					} else if (jsonProduct.getString("productValidity")
							.equalsIgnoreCase("days")) {

						if (jsonProduct.get("validityDays").equals("")) {
							throw new CatalogException(
									"Validitys days can not be empty!!");
						}

						atomicProduct.setValidity(new DaysTime(jsonProduct
								.getInt("validityDays")));
					} else if (jsonProduct.getString("productValidity")
							.equalsIgnoreCase("hours")) {
						if (jsonProduct.get("validityHours").equals("")) {
							throw new CatalogException(
									"Validitys hours can not be empty!!");
						}

						if (jsonProduct.getInt("validityHours") < 0
								|| jsonProduct.getInt("validityHours") > 24) {
							throw new CatalogException(
									"Validitys hours should be greater than Zero and less than 24!!");
						}

						atomicProduct.setValidity(new HoursTime(
								Byte.valueOf(jsonProduct
										.getString("validityHours"))));
					} else if (jsonProduct.getString("productValidity")
							.equalsIgnoreCase("date")) {
						if (jsonProduct.get("validityDate").equals("")) {
							throw new CatalogException(
									"Validitys date can not be empty!!");
						}
						String validityDateString = jsonProduct
								.getString("validityDate");
						Date validity_date;
						try {
							validity_date = DateUtil.toDate(validityDateString,
									"yyyy-MM-dd");
							atomicProduct.setValidity(new HardDateTime(
									validity_date));
						} catch (ParseException e) {
							throw new CatalogException(e);
						}
					}

				}

				offer.addProduct(atomicProduct);
			}

		}*/

		/*if (json.isNull("externalHandlers")
				|| !(json.get("externalHandlers") instanceof JSONArray)
				|| json.getJSONArray("externalHandlers").length() < 1) {
			throw new CatalogException("External Handlers can not be empty!!");
		}
		JSONArray externalHandlers = json.getJSONArray("externalHandlers");

		int externalHandlerSize = externalHandlers.length();
		for (int i = 0; i < externalHandlerSize; i++) {
			offer.addExternalHandle((String) externalHandlers.get(i));
		}*/

		/*if (!json.isNull("whitelistUsers")
				&& (json.get("whitelistUsers") instanceof JSONArray)) {
			JSONArray whitelistUsers = json.getJSONArray("whitelistUsers");

			int whitelistUsersSize = whitelistUsers.length();

			Set<String> whitelistUsersSet = new HashSet<String>();

			for (int i = 0; i < whitelistUsersSize; i++) {
				whitelistUsersSet.add((String) whitelistUsers.get(i));
			}
			offer.setWhiteListedUsers(whitelistUsersSet);
		}*/

		/*if (!json.isNull("exitOfferId")
				&& !json.getString("exitOfferId").equals("")) {
			Offer exitOffer = this.offerManager.getOfferById(json
					.getString("exitOfferId"));

			if (exitOffer == null) {
				throw new CatalogException(json.getString("exitOfferId")
						+ " Offer does not exist!!");
			}

			offer.setExit(exitOffer);
		}*/
	
		
	}
	
	private boolean isNull(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args){
		
		OfferController co = new OfferController();
		System.out.println("Select Any Operation hear");
		System.out.println("1.Create  2.Update ");
		System.out.println("Enter U r choice :");
		
		 
		   String sWhatever;
	 
	       Scanner scanIn = new Scanner(System.in);
	       sWhatever = scanIn.nextLine();
	 
	       scanIn.close();            
	      // System.out.println(sWhatever);
	       switch (sWhatever) {
	                    	case "Create":
	                    		System.out.println("create");
	                    		co.createOffer();
	                    		break;
	   	                    case "Update":
	   	                    	System.out.println("update");
	   	                    	co.updateOffer();
	   	                    	break;
	                   	}
		
		
	}

	
	
}
