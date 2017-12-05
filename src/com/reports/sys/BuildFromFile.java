package com.reports.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lib.email.Email;
import com.lib.file.ApacheFTP;
import com.reports.util.Properties;
import com.reports.util.ReportDate;
import com.reports.util.SystemValues;
import com.reports.visual.ReportsFrame;

public class BuildFromFile extends Thread implements SystemValues{
	private String fileName;
	private String boardType;
	private String material;
	private String recipe;
	private ApacheFTP uploader;
	private String lastExt;
	private File file;

	public BuildFromFile(String lastExt, String boardType, String materialName, String ledRecipe,File file){
		this.file=file;
		fileName=PATH+ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML";
		this.boardType=boardType;
		this.material=materialName;
		this.lastExt=lastExt;
		this.recipe=ledRecipe;
		uploader=new ApacheFTP(Properties.getFtpDomain(),Properties.getDbUser(),Properties.getFtpPass());
	}

	@Override
	public void run(){
		ReportsFrame.switchProgress();
		ReportsFrame.switchButton();
		BufferedReader br = null;	
		try { 
			String sCurrentLine; 
			new File("CreeReports").mkdirs();
			br = new BufferedReader(new FileReader(file)); 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("NewDataSet");
			doc.appendChild(rootElement);
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.equals("")){
					Element board = doc.createElement("Boards");
					rootElement.appendChild(board);
					Element boardsType = doc.createElement("BoardType");
					boardsType.appendChild(doc.createTextNode(boardType));
					board.appendChild(boardsType);
					Element sentToOracle = doc.createElement("SentToOracleDateTime");
					sentToOracle.appendChild(doc.createTextNode(ReportDate.getTodaysDate("Complete")+"T"+ReportDate.getTime()+"-06:00"));
					board.appendChild(sentToOracle);
					Element materialName = doc.createElement("MaterialName");
					materialName.appendChild(doc.createTextNode(material));
					board.appendChild(materialName);
					Element lotId = doc.createElement("LotID");
					lotId.appendChild(doc.createTextNode(sCurrentLine));
					board.appendChild(lotId);
					Element ledRecipe = doc.createElement("LEDRecipe");
					ledRecipe.appendChild(doc.createTextNode(recipe));
					board.appendChild(ledRecipe);
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.INDENT, XML_IDENT);
			transformer.setOutputProperty(OutputKeys.STANDALONE, XML_STANDALONE);
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING);
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			Result result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
			String emailBody=EMAILBODY.replace("@FILENAME@",ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML");
			emailBody=emailBody.replace("@DATE@", ReportDate.getTodaysDate("NORMALFORMAT"));
			if(ReportsFrame.upload()){
				if(uploader.ftpFile(fileName,ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML")){	
					if(ReportsFrame.sendEmail()){
						Email em=new Email();
						em.setRecipients(RECIPIENTS);
						em.send(null,emailBody,EMAILSUBJECT);
					}
					JOptionPane.showMessageDialog(null,"REPORTE COPIADO!");
				}else{
					JOptionPane.showMessageDialog(null, "ERROR AL CONECTARSE A FTP","ERROR",JOptionPane.ERROR_MESSAGE );
				}
			}else{
				JOptionPane.showMessageDialog(null,"REPORTE CREADO!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				ReportsFrame.switchProgress();
				ReportsFrame.switchButton();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
