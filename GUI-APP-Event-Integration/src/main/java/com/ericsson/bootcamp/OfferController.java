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
	
	OfferManagerEx offerManager=new OfferManagerEx();
	public static String offerStoreLocation = null;;
	
	private SecureSerializationHelper ssh = null;

	private void createOffer(){
		try{
		System.out.println("Enter Offer Name :");
		//String offerName="Vekomy16";
		 Scanner scanIn2 = new Scanner(System.in);
		String offerName = scanIn2.nextLine();
	 
	       //scanIn2.close(); 
		System.out.println("offername is:"+offerName);
		Offer offer = new Offer(offerName);
		offer.setOfferState(State.IN_CREATION);

		this.prepareOffer(offer);
		try {
			
		  offerManager.createOffer(offer);
		  System.out.println("Offer Created Successfully");
		  startOffer();
		} catch (CatalogException e) {
			e.printStackTrace();
		}
	   } catch (CatalogException ce) {
			ce.printStackTrace();
		}

		return ;
	}
	private void updateOffer(){
		try {
			System.out.println("Enter Updated Offer Name :");
			//String offerName="Vekomy16";
			 Scanner scanIn2 = new Scanner(System.in);
			String updatedOfferName = scanIn2.nextLine();
			Offer offer = this.offerManager.getOfferById((updatedOfferName));

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
				System.out.println("Offer Updated Successfully");
				startOffer();
			} catch (CatalogException e) {
				e.printStackTrace();
			}

		} catch (CatalogException ce) {
			ce.printStackTrace();
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
		offer.addProduct(product); 
		Owner owner = null;
		ImmediateTermination it = new ImmediateTermination();
		
	}
	
	private boolean isNull(String string) {
		// TODO Auto-generated method stub
		return false;
	}
	public void startOffer(){
		
		
		System.out.println("Select Any Operation hear");
		System.out.println("1.Create  2.Update ");
		System.out.println("Enter U r choice :");
		
		 
		   String sWhatever;
	       Scanner scanIn = new Scanner(System.in);
	       sWhatever = scanIn.nextLine();
	 
	       switch (sWhatever) {
	                    	case "Create":
	                    		createOffer();
	                    		break;
	   	                    case "Update":
	   	                    	updateOffer();
	   	                    	break;
	                   	}
		
	}

	public static void main(String[] args){
		
		
		OfferController co = new OfferController();
		co.startOffer();
	}

	
	
}
