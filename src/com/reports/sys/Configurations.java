package com.reports.sys;

import java.util.Hashtable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configurations {
	Hashtable<String,String[]> times;
	String[] emails;
	String ftpHost;
	String ftpUser;
	String ftpPass;
	String minute;
	
	public String getMinute() {
		return minute;
	}

	@XmlElement
	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	@XmlElement
	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	@XmlElement
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPass() {
		return ftpPass;
	}

	@XmlElement
	public void setFtpPass(String ftpPass) {
		this.ftpPass = ftpPass;
	}

	public Hashtable<String,String[]> getTimes(){
		return times;
	}
	
	@XmlElement
	public void setTimes(Hashtable<String,String[]> times){
		this.times=times;
	}
	
	@XmlElement
	public void setEmails(String[] emails){
		this.emails=emails;
	}
	
	public String[] getEmails(){
		return emails;
	}
}
