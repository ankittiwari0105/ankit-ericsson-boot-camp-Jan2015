package com.ericsson.raso.bootcamp;

import java.util.HashMap;
import java.util.Hashtable;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import  com.ericsson.raso.sef.bes.prodcat.entities.Offer;

//import  com.ericsson.raso.sef.bes.prodcat.entities.Offer;

public class OperatroGUI implements MessageListener {

  private QueueConnectionFactory qConFactory;
  private QueueConnection qCon;
  private QueueSession qSession;
  private QueueReceiver qReceiver;
  private Queue requestQueue;
  private Queue reponseQueue;	
  private MessageProducer producer;
  
  private static boolean gotOffer = false;
  private static Offer offer = null;
  
  public OperatroGUI () 
		    throws NamingException, JMSException 
  {
	  init();
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
    
    requestQueue = (Queue) ctx.lookup("ericsson/boot-request-queue");
    producer = qSession.createProducer(requestQueue); 
    
    reponseQueue = (Queue) ctx.lookup("ericsson/boot-response-Queue");
    qReceiver = qSession.createReceiver(reponseQueue);
    qReceiver.setMessageListener(this);
    qCon.start();
  }

	public void requestOfferById(String id) throws JMSException
	{
		String text = "GetOfferById_id:" + id;
		TextMessage message = qSession.createTextMessage();
		message.setText(text);
		producer.send(message);
	}
	
	public void updateAndSend (Offer offer) throws JMSException {
		offer.setPrice(offer.getPrice());
		ObjectMessage message = qSession.createObjectMessage();
		message.setObject(offer);
		producer.send(message);
	}
	
	@Override
	public void onMessage(Message msg) {
		System.out.println("Got a message");
		
	    try {
			if (msg instanceof TextMessage) {
		    	System.out.println("Received JMS message text: "+ ((TextMessage)msg).getText());
		    } else {
		        Object object = ((ObjectMessage) msg).getObject();
//		        HashMap<String, String> attrMap = (HashMap<String, String>) object;
		        
		        offer = (Offer) object;

				gotOffer = true;
		      }
		    } catch (JMSException e) {
		      System.out.println("Exception thrown in JMS message handing: "+e.getMessage());
		    }
	}
	
	public static void main(String [] args)
		    throws NamingException, JMSException, InterruptedException
	{
		System.out.println("Start the client");
		OperatroGUI obj = new OperatroGUI();
		
		System.out.println("fetch the offer");
		obj.requestOfferById("offerId");
		
		while (false == gotOffer)
		{ 
			Thread.sleep(1000);	
		}
		
		System.out.println("modify the offer and send out");
		obj.updateAndSend(offer);
	}
	
}