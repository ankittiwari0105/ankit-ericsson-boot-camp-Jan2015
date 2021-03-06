package com.ericsson.raso.sef.client.air.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.raso.sef.client.air.internal.CsAirContext;
import com.ericsson.raso.sef.client.air.request.InstallSubscriberRequest;
import com.ericsson.raso.sef.client.air.response.InstallSubscriberResponse;
import com.ericsson.raso.sef.core.ResponseCode;
import com.ericsson.raso.sef.core.SmException;
import com.ericsson.raso.sef.plugin.xmlrpc.XmlRpcException;

public class InstallSubscriberCommand extends AbstractAirCommand<InstallSubscriberResponse>{
	Logger logger = LoggerFactory.getLogger(InstallSubscriberCommand.class);
	private InstallSubscriberRequest request;
	
	public InstallSubscriberCommand(InstallSubscriberRequest installSubscriberRequest) {
		this.request = installSubscriberRequest;
	}

	@Override
	public InstallSubscriberResponse execute() throws SmException {
		InstallSubscriberResponse response =  new InstallSubscriberResponse();
		try {
			CsAirContext.getAirClient().execute(request, response);
		} catch (XmlRpcException e) {
			logger.debug("XMLRpc execution failure", e);
			throw new SmException(CsAirContext.getSection(), new ResponseCode(e.code, e.getMessage()), e.linkedException);
		}
		return response;
	}
}
