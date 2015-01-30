package com.ericsson.raso.bootcamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ericsson.raso.sef.bes.prodcat.CatalogException;
import com.ericsson.raso.sef.bes.prodcat.OfferContainer;
import com.ericsson.raso.sef.bes.prodcat.OfferManager;
import com.ericsson.raso.sef.bes.prodcat.entities.Offer;
import com.ericsson.raso.sef.bes.prodcat.entities.OfferWithIndex;
import com.ericsson.raso.sef.core.FrameworkException;
import com.ericsson.raso.sef.core.SecureSerializationHelper;

public class OfferManagerController implements MessageListener {
	  private QueueConnectionFactory qConFactory;
	  private QueueConnection qCon;
	  private QueueSession qSession;
	  private QueueReceiver qReceiver;
	  private javax.jms.Queue requestQueue;
	  private javax.jms.Queue reponseQueue;	
	  private MessageProducer producer;
	  
	public static String offerStoreLocation = null;;

	private SecureSerializationHelper ssh = null;
	private List<Offer> offerList = null;
	private OfferContainer container = new OfferContainer();
	private OfferManager offerManager = new OfferManager();
	private HashMap<String, Queue<OfferWithIndex>> offerQueueMap = null;
	private static OfferManagerController offerManagerController = null;
	public static OfferManagerController getInstance()
	{
		if(offerManagerController == null)
		{
			offerManagerController = new OfferManagerController();
		}
		return offerManagerController;
	}
	private OfferManagerController()
	{
		offerStoreLocation = getStoreLocation();
		System.out.println("offerstore location: " + offerStoreLocation);
		ssh = new SecureSerializationHelper();
		if (ssh.fileExists(offerStoreLocation)) {
			try {
				this.container = (OfferContainer) ssh.fetchFromFile(offerStoreLocation);
				container.listAllOffers();
				offerList = container.getAllOfferList();
			} catch (FrameworkException e) {
				// TODO: LOgger on this error...
		
			}
		}
		for(Offer offer: offerList)
		{
			OfferWithIndex offerWithIndex =  new OfferWithIndex(offer);
			offerWithIndex.setIndex(0);
			Queue<OfferWithIndex> offerQueue = new LinkedList<OfferWithIndex>();
			offerQueue.add(offerWithIndex);
			offerQueueMap.put(offer.getName(), offerQueue);
		}
		
		init();
	}
	public Offer getOfferById(String name)
	{
		Offer returnOffer = null;
		returnOffer = offerQueueMap.get(name).peek().getOffer();
		return returnOffer;
	}
	public Offer getOfferByIdVersion(String name,int version)
	{
		Offer returnOffer = null;
		Queue<OfferWithIndex> tempQueue = offerQueueMap.get(name);
		for(OfferWithIndex tempOfferWithIndex :tempQueue)
		{
			if(tempOfferWithIndex.getOffer().getVersion() == version)
			{
				returnOffer = tempOfferWithIndex.getOffer();
				break;
			}
		}
		return returnOffer;
	}
	/**
	 * This method should be called when offer is updated or created, after offerContainer already updated the offer, here the offer object 
	 * should already been updated with a new version
	 * @param offer
	 */
	public void insertOffer(Offer offerWithNewVersion)
	{
		OfferWithIndex offerWithIndex = new OfferWithIndex(offerWithNewVersion);
		offerWithIndex.setIndex(0);
		Queue<OfferWithIndex> tempQueue = offerQueueMap.get(offerWithNewVersion.getName());
		tempQueue.add(offerWithIndex);
		offerQueueMap.put(offerWithNewVersion.getName(), tempQueue);
	}
	/**
	 * Get new offer from client side, first increase its version and update the queue
	 * @param offer
	 * @throws CatalogException 
	 */
	public void updateOffer(Offer offer) throws CatalogException
	{
		/*****************************Following logic are copying OfferManager's *********************************/

		container.updateOffer(offer);
		
		try {
			this.ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
		/*Then update controller's own queue*/
		int existingVersion = offerQueueMap.get(offer.getName()).peek().getOffer().getVersion();
		int newVersion = existingVersion++;
		offer.setVersion(newVersion);
		insertOffer(offer);

	
	}
	/**
	 * increase the index of the offer with certain version
	 * @param offerName
	 * @param version
	 */
	public void IncreaseIndex(String offerName,int version)
	{
		Queue<OfferWithIndex> tempQueue = offerQueueMap.get(offerName);
		Iterator<OfferWithIndex> iterator = tempQueue.iterator();
		while(iterator.hasNext())
		{
			OfferWithIndex tempOfferWithIndex = iterator.next();
			if(tempOfferWithIndex.getOffer().getVersion() == version)
			{
				int index = tempOfferWithIndex.getIndex();
					index++;
					tempOfferWithIndex.setIndex(index);
			break;
			}

		}
		offerQueueMap.put(offerName, tempQueue);
		}
	public void decreaseIndex(String offerName,int version)
	{
		Queue<OfferWithIndex> tempQueue = offerQueueMap.get(offerName);
		Iterator<OfferWithIndex> iterator = tempQueue.iterator();
		while(iterator.hasNext())
		{
			OfferWithIndex tempOfferWithIndex = iterator.next();
			if(tempOfferWithIndex.getOffer().getVersion() == version)
			{
				int index = tempOfferWithIndex.getIndex();
				if(index == 1)
				{
					iterator.remove();
				}
				else {
					index--;
					tempOfferWithIndex.setIndex(index);
				}
			break;
			}

		}
		offerQueueMap.put(offerName, tempQueue);
	}
	/***
	 * handler for creating new offer
	 * @param offer
	 * @throws CatalogException
	 */
	public void createOffer(Offer offer) throws CatalogException
	{
		container.createOffer(offer);
		
		try {
			this.ssh.persistToFile(offerStoreLocation, (Serializable) this.container);
		} catch (FrameworkException e) {
			throw new CatalogException("Could not save the changes!!!", e);
		}
		int existingVersion = offerQueueMap.get(offer.getName()).peek().getOffer().getVersion();
		int newVersion = existingVersion++;
		offer.setVersion(newVersion);
		insertOffer(offer);
	}
	private String getStoreLocation() {
		String offerStoreLocation = System.getenv("SEF_CATALOG_HOME");
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
	
	  public void init()
			    throws NamingException, JMSException
		  {
			Hashtable<String, String> env = new Hashtable<String, String>();  
		    env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");  
		    env.put(Context.PROVIDER_URL, "t3://127.0.0.1:7001");  
			Context ctx = new InitialContext(env); 
			
		    qConFactory = (QueueConnectionFactory) ctx.lookup("ericsson/boot-ConnectionFactory");
		    qCon = qConFactory.createQueueConnection();
		    
		    qSession = qCon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		    
		    requestQueue = (javax.jms.Queue) ctx.lookup("ericsson/boot-request-queue");
		    producer = qSession.createProducer(requestQueue); 
		    
		    reponseQueue = (javax.jms.Queue) ctx.lookup("ericsson/boot-response-Queue");
		    qReceiver = qSession.createReceiver(reponseQueue);
		    qReceiver.setMessageListener(this);
		    qCon.start();
		  }

		@Override
		public void onMessage(Message msg) {
			System.out.println("Got a message");
			
		    try {
				if (msg instanceof TextMessage) {
			    	System.out.println("Received offer ID: "+ ((TextMessage)msg).getText());

					ObjectMessage message = qSession.createObjectMessage();
					message.setObject(getOfferById(((TextMessage)msg).getText()));
					producer.send(message);
				}

		    } catch (JMSException e) {
		      System.out.println("Exception thrown in JMS message handing: "+e.getMessage());
		    }
		}
		
		public static void main(String[] args) {
			while (true) {
				
			}
		}
}
