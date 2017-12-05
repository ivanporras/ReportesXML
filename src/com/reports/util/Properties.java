package com.reports.util;

import java.util.Hashtable;
import com.lib.file.ProjectProperties;

public class Properties {
	
//	public static ArrayList<String> setProperties(){
//		ProjectProperties pjp=new ProjectProperties();
//		pjp.addProperty("FTP_DOMAIN", "ftp2.sanmina.com");
//		pjp.addProperty("FTP_USER", "p447cree");
//		pjp.addProperty("FTP_PASS", "Bv57z3m");
//		pjp.addProperty("JDBC", "net.sourceforge.jtds.jdbc.Driver");
//		pjp.addProperty("SRV", "jdbc:jtds:sqlserver://143.116.204.184;DatabaseName=sfdc_reporting");
//		pjp.addProperty("DB_USER", "sfdc_reporting");
//		pjp.addProperty("DB_PASS", "Sanmina1");
//		pjp.createProperties("com/reports/util/config.properties", false,false);
//		ArrayList<String> values=pjp.getConfigurationValues("/com/reports/util/config.properties",false);
//		return values;
//	}
	
	public static String getFtpDomain(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("FTP_DOMAIN");
	}	
	
	public static String getFtpUser(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("FTP_USER");
	}
	
	public static String getFtpPass(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("FTP_PASS");
	}
	
	public static String getJDBC(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("JDBC");
	}
	
	public static String getSRV(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("SRV");
	}
	
	public static String getDbUser(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("DB_USER");
	}
	
	public static String getDbPass(){
		Hashtable<String,String> configurations=new ProjectProperties().getConfigurations("/com/reports/util/config.properties",false);
		return configurations.get("DB_PASS");
	}
}
