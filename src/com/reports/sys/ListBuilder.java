package com.reports.sys;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;

import com.lib.connection.Query;
import com.reports.util.Properties;

public class ListBuilder{
	private ResultSet resultQuery;
	private String[] parts;
	private boolean isDriver;
	private Query query;

	public ListBuilder(){
		query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
	}
	
	public boolean isDriver() {
		return isDriver;
	}

	public void setDriver(boolean isDriver) {
		this.isDriver = isDriver;
	}

	public int countParts(String qry){
		int counter=0;
		ResultSet resultCount=query.execute(qry);
		if(resultCount!=null)
			try {
				while(resultCount.next()){
					counter++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return counter;
	}

	public void reload(String query,DefaultListModel listModel){
		String[] data=new ListBuilder().loadParts(query);
		listModel.removeAllElements();
		for(int i=0;i<data.length;i++){
			listModel.addElement(data[i]);
			if(data[i].contains("-"))
				isDriver=false;
			else
				isDriver=true;
		} 
	}

	public String[] loadParts(String qry){
		int length=0;
		Query query=null;
		try {
			query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		resultQuery=query.execute(qry);
		length=countParts(qry);
		int counter=0;
		parts=new String[length];
		try {
			while(resultQuery.next()){
				parts[counter]=resultQuery.getString("Part_Number");
				counter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parts;
	}

	public String[] loadSelected(){
		return parts;
	}
}
