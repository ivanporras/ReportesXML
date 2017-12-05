package com.reports.sys;

import com.lib.file.ApacheFTP;
import com.reports.util.Properties;

public class Test {
 public static void main(String...args){
	 System.out.println(Properties.getFtpDomain()+"-"+Properties.getFtpUser()+"-"+Properties.getFtpPass());
	 ApacheFTP uploader=new ApacheFTP(Properties.getFtpDomain(),Properties.getDbUser(),Properties.getFtpPass());
	 System.out.println(uploader.ftpFile("C:\\Users\\Ivan_Porras\\Desktop\\09172014X.XML", "09172014X.XML"));
 }
}
