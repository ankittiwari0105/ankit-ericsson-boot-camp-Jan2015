package com.ericsson.sef.bes.api.subscriber;

import java.util.Map;
import java.util.Set;

import com.ericsson.sef.bes.api.entities.Subscriber;;

public interface ISubscriberRequest {
	
	public abstract String readSubscriber(String subscriberId);
	
	public abstract String readSubscriberMeta(String subscriberId, Set<String> metaNames);
	
	public abstract String createSubscriber(Subscriber subscriber);
	
	public abstract String updateSubscriber(String subscriberId, Map<String, String> metas);
	
	public abstract String deleteSubscriber(String subscriberId);
	
	public abstract String handleLifeCycle (String subscriberId, String lifeCycleState, Map<String, String> metas);

}
