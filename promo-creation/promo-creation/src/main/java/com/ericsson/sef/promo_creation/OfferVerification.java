package com.ericsson.sef.promo_creation;

import java.util.Map;

import com.ericsson.raso.sef.bes.prodcat.OfferContainer;
import com.ericsson.raso.sef.bes.prodcat.SecureSerializationHelper;
import com.ericsson.raso.sef.bes.prodcat.ServiceRegistry;
import com.ericsson.raso.sef.bes.prodcat.entities.AtomicProduct;
import com.ericsson.raso.sef.bes.prodcat.entities.Offer;
import com.ericsson.raso.sef.bes.prodcat.entities.Product;
import com.ericsson.raso.sef.bes.prodcat.entities.Resource;
import com.ericsson.raso.sef.bes.prodcat.entities.Service;
import com.ericsson.raso.sef.core.FrameworkException;
import com.ericsson.raso.sef.fulfillment.profiles.FulfillmentProfile;
import org.apache.commons.codec.binary.Base64;

public class OfferVerification {

	public static void main(String[] args) {
		try {
			SecureSerializationHelper helper = new SecureSerializationHelper();
			Base64 encoder = new Base64();
			
			OfferContainer offerStore = (OfferContainer) helper.fetchFromFile("C:\\Temp\\offerStore.ccm");
			Map<String, Resource> serviceRegistry = (Map<String, Resource>) helper.fetchFromFile("C:\\Temp\\serviceRegistry.ccm");
			Map<String, FulfillmentProfile<?>> profileRegistry = (Map<String, FulfillmentProfile<?>>) helper.fetchFromFile("C:\\Temp\\profileRegistry.ccm");
			
			System.out.println("Encrypted form: " + new String (encoder.encode(helper.encrypt("639777180104"))));
			
			System.out.println("\n1. Create Subscriber:\n" + offerStore.getOfferById("CREATE_SUBSCRIBER"));
			System.out.println("\n2. Read Subscriber:\n" + offerStore.getOfferById("READ_SUBSCRIBER"));
			System.out.println("\n3. Modify Tagging:\n" + offerStore.getOfferById("MODIFY_TAGGING"));
			System.out.println("\n4. Delete Subscriber:\n" + offerStore.getOfferById("DELETE_TAGGING"));
			
			Offer taggingOffer = offerStore.getOfferById("READ_SUBSCRIBER");
			for (AtomicProduct product: taggingOffer.getAllAtomicProducts()) {
				System.out.println("Product name: " + product.getName());
				System.out.println("Product resource: " + product.getResource());
				
				Service resource = (Service) product.getResource();
				System.out.println("Resource name: " + resource.getName());
				
				for (String profileId: resource.getFulfillmentProfiles()) {
					System.out.println("Profile Id: " + profileId);
					FulfillmentProfile profile = profileRegistry.get(profileId);
					System.out.println("Profile class: " + profile.getClass().getCanonicalName());
					System.out.println("Profile contents: " + profile.toString());
				}
			}
			
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
