package com.ericsson.raso.sef.bes.prodcat.entities;

public class OfferWithIndex {

	private static final long serialVersionUID = -5162219540174748030L;
	public OfferWithIndex(Offer offer) {
		// TODO Auto-generated constructor stub
		this.offer = offer;
	}
	private int index;
	private Offer offer;
	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	

}
