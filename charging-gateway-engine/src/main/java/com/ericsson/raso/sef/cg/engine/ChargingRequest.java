package com.ericsson.raso.sef.cg.engine;

import java.util.List;

import com.ericsson.pps.diameter.dccapi.command.Ccr;
import com.ericsson.pps.diameter.rfcapi.base.avp.Avp;

public class ChargingRequest {

	private String messageId;
	private String sessionId;
	private Ccr sourceCcr;
	private Ccr scapCcr;
	private List<Avp> scapResult;
	private List<Avp> nsnResult;
	private String msisdn;
	private Operation operation;
	private String hostId;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Ccr getSourceCcr() {
		return sourceCcr;
	}

	public void setSourceCcr(Ccr sourceCcr) {
		this.sourceCcr = sourceCcr;
	}

	public Ccr getScapCcr() {
		return scapCcr;
	}

	public void setScapCcr(Ccr scapCcr) {
		this.scapCcr = scapCcr;
	}

	public List<Avp> getScapResult() {
		return scapResult;
	}

	public void setScapResult(List<Avp> scapResult) {
		this.scapResult = scapResult;
	}

	public List<Avp> getNsnResult() {
		return nsnResult;
	}

	public void setNsnResult(List<Avp> nsnResult) {
		this.nsnResult = nsnResult;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public Operation getOperation() {
		return operation;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}