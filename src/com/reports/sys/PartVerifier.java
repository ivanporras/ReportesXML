package com.reports.sys;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import com.lib.file.Fwriter;
import com.lib.connection.Query;
import com.lib.util.DateFormat;
import com.lib.util.Dates;
import com.lib.util.FileUtilities;
import com.lib.vs.Dialog;
import com.reports.connection.Views;
import com.reports.util.Properties;
import com.reports.visual.ReportsFrame;

public class PartVerifier implements Views{
	private Query query;
	private Fwriter writer;
	private String logPath="C:\\CREE-log";

	public PartVerifier(){
		query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
	}

	public Hashtable<String,Part>  getParts(){
		Hashtable<String,Part> existingParts=new Hashtable<String,Part>();
		try{
			ResultSet mesParts=query.execute(GETMESPARTS);
			ResultSet sqlParts;
			ArrayList<String> partsNotFound=new ArrayList<String>();
			String part="",type="",kelvin="";
			while(mesParts.next()){
				part=mesParts.getString("part");
				type=mesParts.getString("type");
				kelvin=mesParts.getString("kelvin");
				if(type.equalsIgnoreCase("LED")){
					if(!kelvin.equals("UNDEFINED")){
						existingParts.put(part,new Part(part,type,kelvin));
					}else{
						sqlParts=query.execute(GETSQLPARTS+"'"+part+"'");
						if(sqlParts.next()){
							existingParts.put(part,new Part(sqlParts.getString("part"),sqlParts.getString("type"),sqlParts.getString("kelvin")));
						}else{
							System.out.println(part+" - "+kelvin+" - "+type);
							partsNotFound.add(part);
						}
					}
				}else if(type.equalsIgnoreCase("DRIVER")){
					existingParts.put(part,new Part(part,type,kelvin));
				}else if(type.equalsIgnoreCase("ELM")){
					existingParts.put(part,new Part(part,type,kelvin));
				}
			}
			if(partsNotFound.size()>0){
				writeLog(partsNotFound);
				if(!ReportsFrame.getAutoClose())
					Dialog.showDialog(partsNotFound.size()+" PARTES NO ENCONTRADAS, LOG CREADO EN: "+logPath,"ERROR",JOptionPane.ERROR_MESSAGE,null);
				existingParts.clear();
			}
		}catch(Exception e){
			e.printStackTrace();
			if(!ReportsFrame.getAutoClose()){
				Dialog.showDialog("ERROR OBTENIENDO PARTES: "+e.toString(), "Error", JOptionPane.ERROR_MESSAGE, null);
			}
		}
		return existingParts;
	}

	public void writeLog(ArrayList<String> parts){
		FileUtilities.createDirectory(logPath);
		FileUtilities.deleteFile(logPath+"\\"+Dates.getTodaysDate(DateFormat.NORMAL));
		writer=new Fwriter(logPath+"\\"+Dates.getTodaysDate(DateFormat.NORMAL));
		for(String part:parts){
			writer.writeLine("PART: "+part,true);
		}
	}
}
