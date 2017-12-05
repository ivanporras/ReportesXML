package com.reports.util;


public interface SystemValues {
	/*
	 * Do not change the @XXXX@ values!!
	 */
	//FILES
	public static final String BK_PATH="Z:\\";
	public static final String PATH="CreeReports\\";
	public static final String LOGFILE_NAME="Logs.txt";
	//XML VALUES	
	public static final String XML_IDENT="YES";
	public static final String XML_STANDALONE="YES";
	public static final String XML_ENCODING="ISO-8859-1";
	//EMAIL VALUES 
	public static final String[] RECIPIENTS=new String[]{
		"ivan.porras@sanmina.com"
	};
	public static final String[] RECIPIENTS_FILE = new String[]{		
		"alberto.delacerda@sanmina.com",
		"cristian.delgado@sanmina.com",
		"Victor.luis@cree.com",
		"todd.engen@cree.com",
		"enrique.ramirez@sanmina.com",
		"victor.muzquiz@sanmina.com",
		"ivan.porras@sanmina.com",
		"julio_ramirez@cree.com"
	};
	
	
	public static final String[] RECIPIENTS_ERRORS=new String[]{
		"ivan.porras@sanmina.com",
		"enrique.ramirez@sanmina.com",
		"victor.muzquiz@sanmina.com",
		"alberto.delacerda@sanmina.com",
		"cristian.delgado@sanmina.com"
	};
	public static final String EMAILBODY="The CREE .xml file: \"@FILENAME@\" has been uploaded to:  ftp://ftp2.sanmina.com on the date: @DATE@";
	public static final String EMAILSUBJECT="NOTIFICATION";

}
