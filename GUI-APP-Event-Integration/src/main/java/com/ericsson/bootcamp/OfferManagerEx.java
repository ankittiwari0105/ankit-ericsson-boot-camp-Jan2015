
package com.ericsson.bootcamp;

import java.io.Serializable;

import com.ericsson.raso.sef.bes.prodcat.CatalogException;
import com.ericsson.raso.sef.bes.prodcat.OfferContainer;
import com.ericsson.raso.sef.bes.prodcat.entities.Offer;
import com.ericsson.raso.sef.bes.prodcat.entities.State;
import com.ericsson.raso.sef.bes.prodcat.service.IOfferAdmin;
import com.ericsson.raso.sef.core.FrameworkException;
import com.ericsson.raso.sef.core.SecureSerializationHelper;

public class OfferManagerEx implements IOfferAdmin {
	
	public static String offerStoreLocation = null;;

	//private SecureSerializationHelperEx ssh = null;
	SecureSerializationHelperEx ssh = new SecureSerializationHelperEx();
	private OfferContainer container = new OfferContainer();

	public OfferManagerEx() {
		// TODO: fetch the store location from config, once the config services is ready....

		System.out.println("Offer Manager Start1======>");
		offerStoreLocation="D:/Offerccm/offerStore.ccm";
		System.out.println("offerstore location: " + offerStoreLocation);
		ssh = new SecureSerializationHelperEx();
		System.out.println("ssh is:"+ssh);
		if (ssh.fileExists(offerStoreLocation)) {
			try {
				this.container = (OfferContainer) ssh.fetchFromFile(offerStoreLocation);
				container.listAllOffers();
				System.out.println("offers is:"+container.toString());
			} catch (FrameworkException e) {
				// TODO: LOgger on this error...
		
			}
		}
		
	}
	
	private String getStoreLocation() {
		//String offerStoreLocation = System.getenv("SEF_CATALOG_HOME");
		String offerStoreLocation ="D:/Offerccm";
		System.out.print("sef path is:"+offerStoreLocation);
		String filename = "offerStore.ccm";
		String finalfile = "";
		String your_os = System.getProperty("os.name").toLowerCase();
		if(your_os.indexOf("win") >= 0){
			finalfile = offerStoreLocation + "\\" + filename;
		}else if(your_os.indexOf( "nix") >=0 || your_os.indexOf( "nux") >=0){
			finalfile = offerStoreLocation + "/" + filename;
		}else{
			finalfile = offerStoreLocation + "/" + filename;
		}
		
		return finalfile;
	}
	
	//-----------============ Admin Functions ========================================================================================================
	
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#changeLifeCycle(java.lang.String, int, com.ericsson.raso.sef.bes.prodcat.entities.State)
	 */
	@Override
	public void changeLifeCycle(String offerId, int vesion, State newState) throws CatalogException {
		container.changeLifeCycle(offerId, vesion, newState);
		
		try {
			this.ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#createOffer(com.ericsson.raso.sef.bes.prodcat.entities.Offer)
	 */
	@Override
	public void createOffer(Offer offer) throws CatalogException {
		container.createOffer(offer);
		
		try {
			ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
			System.out.println("offercontainer is:"+this.container.toString());
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
	}
		
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#deleteOffer(java.lang.String)
	 */
	@Override
	@Deprecated
	public void deleteOffer(String offerId) throws CatalogException {
		container.deleteOffer(offerId);
		
		try {
			this.ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#updateOffer(com.ericsson.raso.sef.bes.prodcat.entities.Offer)
	 */
	@Override
	public void updateOffer(Offer offer) throws CatalogException {
		container.updateOffer(offer);
		//System.out.println("update is:"+container.);
		
		try {
			this.ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
	}
	
	//-----------============ Admin & Catalog Functions ==============================================================================================
	
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#getOfferById(java.lang.String)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferCatalog#getOfferById(java.lang.String)
	 */
	@Override
	public Offer getOfferById(String id) {
		return container.getOfferById(id);
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#getOfferById(java.lang.String, int)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferCatalog#getOfferById(java.lang.String, int)
	 */
	@Override
	public Offer getOfferById(String id, int version) {
		return container.getOfferById(id, version);
	}

	/* (non-Javadoc)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferAdmin#offerExists(java.lang.String)
	 * @see com.ericsson.raso.sef.bes.prodcat.IOfferCatalog#offerExists(java.lang.String)
	 */
	@Override
	public boolean offerExists(String offerId) {
		return container.offerExists(offerId);
	}

		
		
	
}
