package com.reports.sys;

import java.awt.Frame;
import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.reports.connection.Operations;
import com.reports.util.ReportDate;
import com.reports.util.SystemValues;

public class SQLXml implements SystemValues{
	private Operations querys;
	private SQLXML xmlVal;

	public SQLXml(){
		querys=new Operations();
	}

	public void create(String sinceDate,Frame frame,String fileName) throws Exception{
		ResultSet rs=querys.getXMLResultSet(sinceDate,ReportDate.getTodaysDate("DB"),frame);
		if(rs!=null)
			createXML(rs,fileName);
	}

	public void createXML(ResultSet res,String fileName){
		try{
			File fxml=new File(fileName);
			res.next();
			xmlVal=res.getSQLXML(1);
			InputStream binaryStream = xmlVal.getBinaryStream();					
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document result4 = parser.parse(binaryStream);
			result4.setXmlStandalone(true);
			DOMSource domSource2=new DOMSource(result4);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, XML_IDENT);
			transformer.setOutputProperty(OutputKeys.STANDALONE, XML_STANDALONE);
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING);
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");				
			Result result = new StreamResult(fxml);
			transformer.transform(domSource2, result);
		}catch(Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}
