package com.reports.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Fwriter implements SystemValues{

	private FileWriter writer;
	private File file;

	public Fwriter(){
		file=new File(PATH+LOGFILE_NAME);
	}


	public void writer(String lineToWrite){
		try {
			writer = new FileWriter(file,true);
			writer = new FileWriter(file,true);
			writer.append(lineToWrite);
			writer.flush();
			writer.append(System.getProperty("line.separator"));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
