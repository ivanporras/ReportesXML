package com.reports.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportDate {

	public static String getTodaysDate(String type){
		SimpleDateFormat dateFormat;
		if(type.equalsIgnoreCase("FILENAME"))
			dateFormat = new SimpleDateFormat("MMddyyyy");
		else if(type.equalsIgnoreCase("COMPLETE"))
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		else
			dateFormat=new SimpleDateFormat("MM/dd/yyyy");
		return dateFormat.format(new Date());
	}
	
	public static String getTime(){
		SimpleDateFormat date= new SimpleDateFormat("hh:mm:ss");
		return date.format(new Date());
	}
}
