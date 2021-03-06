package com.ericsson.sef.bes.api.entities;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="product")
public final class Product implements Serializable {
	private static final long	serialVersionUID	= -7676120441931768658L;

	private String name = null;
	private String resourceName = null;
	private long quotaDefined = -1;
	private long quotaConsumed = -1;
	private long validity = 0L;
	
	// added by esatnar for allowing FFE to send metas back to TXE.
	private Map<String, String> metas = null;
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getResourceName() {
		return resourceName;
	}
	
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public long getQuotaDefined() {
		return quotaDefined;
	}

	public void setQuotaDefined(long quotaDefined) {
		this.quotaDefined = quotaDefined;
	}

	public long getQuotaConsumed() {
		return quotaConsumed;
	}

	public void setQuotaConsumed(long quotaConsumed) {
		this.quotaConsumed = quotaConsumed;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long validity) {
		this.validity = validity;
	}

	public Map<String, String> getMetas() {
		return metas;
	}

	public void setMetas(Map<String, String> metas) {
		this.metas = metas;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", resourceName=" + resourceName + ", quotaDefined=" + quotaDefined + ", quotaConsumed="
				+ quotaConsumed + ", validity=" + validity + ", metas=" + metas + "]";
	}
	
	
	
}
