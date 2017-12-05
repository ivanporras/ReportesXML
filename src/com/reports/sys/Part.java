package com.reports.sys;

import java.util.ArrayList;

public class Part {
	private String partNumber;
	private String type;
	private String kelvin;
	private String bng;
	private ArrayList<String> serials;
	private ArrayList<String> revisiones;
	private String sentToOracle;
	
	public Part(String partNumber,String type,String kelvin){
		this.partNumber=partNumber;
		this.type=type;
		this.kelvin=kelvin;
	
		serials=new ArrayList<String>();
		revisiones = new ArrayList<String>();
	}
	public ArrayList<String> getSerials() {
		return serials;
	}
	public void addSerial(String serial) {
		this.serials.add(serial);
	}
	public ArrayList<String> getRevisiones() {
		return revisiones;
	}
	public void addRevisiones(String rev) {
		this.revisiones.add(rev);
	}
	public String getSentToOracle() {
		return sentToOracle;
	}
	public void setSentToOracle(String sentToOracle) {
		this.sentToOracle = sentToOracle;
	}
	public String getBng() {
		return bng;
	}
	public void setBng(String bng) {
		this.bng = bng;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKelvin() {
		return kelvin;
	}
	public void setKelvin(String kelvin) {
		this.kelvin = kelvin;
	}
	
}
