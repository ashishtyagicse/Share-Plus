package com.shareplus.models;

import java.io.Serializable;

public class SharableSerializable implements Serializable{
	public Sharable S = null;

	public SharableSerializable(Sharable s) {
		super();
		S = s;
	}
	public Sharable getS() {
		return S;
	}
	public void setS(Sharable s) {
		S = s;
	}	
}
