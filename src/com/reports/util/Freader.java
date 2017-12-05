package com.reports.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Freader implements SystemValues{
	private String direc=PATH+LOGFILE_NAME;
	
	public String reader(){
		BufferedReader br = null;	
		String sCurrentLine = null; 
		String line=null;
		try {
			br = new BufferedReader(new FileReader(direc)); 
			while ((sCurrentLine = br.readLine()) != null) {	
				line=sCurrentLine;
			}
		}catch(FileNotFoundException e){
			System.out.println("Archivo no existe");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return line;
	}
	
	
}
