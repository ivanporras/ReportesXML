package com.reports.sys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.reports.sys.Configurations;
import com.reports.sys.XMLTime;
import com.lib.connection.Query;
import com.lib.email.Email;
import com.lib.file.ApacheFTP;
import com.lib.file.JschSFTP;
import com.lib.vs.Dialog;
import com.reports.connection.Operations;
import com.reports.connection.Views;
import com.reports.json.JSON;
import com.reports.util.Fwriter;
import com.reports.util.Properties;
import com.reports.util.ReportDate;
import com.reports.util.SystemValues;
import com.reports.visual.ReportsFrame;

public class BuildFromDate extends Thread implements SystemValues,Views{
	private String fileName;
	private ApacheFTP uploader;
	private ApacheFTP checkFile;
	private String lastExt;
	private String sinceDate;
	private Query query;
	private String NameMaterial;
	private String recipeLed;
	private int queueReplication;
	private String dateReplication;
	private int currentMinute;
	private int replicationMinute;
	private static int PART_INCORRECT = 99; // the serial's partnumber is bad
	private static int REV_INCORRECT = 100; // the serial's revision is bad
	private static int DATAFILE_GENERATED=1;
	private static int SERIAL_NOT_FOUND=101;
	JschSFTP sftp=new JschSFTP();
	Configurations configs=XMLTime.loadConfigurations();
	public static final String SFTP_PATH="/Home/SanminaMex/ShipOut/";

	public BuildFromDate(String lastExt,String sinceDate,boolean autoClose){
		query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
		fileName=PATH+ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML";
		this.lastExt=lastExt;
		this.sinceDate=sinceDate;
		uploader=new ApacheFTP(Properties.getFtpDomain(),Properties.getFtpUser(),Properties.getFtpPass());
		checkFile=new ApacheFTP(Properties.getFtpDomain(),Properties.getFtpUser(),Properties.getFtpPass());		
	}

	@Override
	public void run(){
		boolean duplicate=false;
		ReportsFrame.switchProgress();
		ReportsFrame.switchButton();
		Email em=new Email();
		try { 
			Hashtable<String,Part> parts=new PartVerifier().getParts();
			new File("CreeReports").mkdirs();
			Fwriter fwriter=new Fwriter();
			// MES replication 
			ResultSet dataReplication = query.execute(GETREPLICATION);
			try {
				if(dataReplication.next()){
					 dateReplication = dataReplication.getString("dttm_end_sql");
					 queueReplication = Integer.parseInt(dataReplication.getString("queue_qty"));
					 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 try {
						Date d = formatter.parse(dateReplication);
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						c.add(c.MINUTE, 5);
						replicationMinute = c.get(Calendar.MINUTE);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					 Calendar calendar = Calendar.getInstance();
					 currentMinute = calendar.get(Calendar.MINUTE);
				}
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		if(queueReplication < 4000 & replicationMinute >= currentMinute){
						/** part number validation */
			if(parts.size()>0){
				Operations qBuilder=new Operations();
				String pkIdLed="",pkIdDriver="",pkIdELM="",  endTime=ReportDate.getTodaysDate("DB");
				Part part;
				Enumeration<String> keys=parts.keys();
				while(keys.hasMoreElements()){
					part=parts.get(keys.nextElement());
					if(part.getType().equals("LED")){
						pkIdLed+=qBuilder.getPkId(part.getPartNumber())+",";
					}else if(part.getType().equals("DRIVER")){
						pkIdDriver+=qBuilder.getPkId(part.getPartNumber())+",";
					}else{
						pkIdELM += qBuilder.getPkId(part.getPartNumber())+",";
					}
				}
				pkIdLed=pkIdLed.substring(0,pkIdLed.lastIndexOf(","));
				pkIdDriver=pkIdDriver.substring(0,pkIdDriver.lastIndexOf(","));
				if(!duplicate){
					parts=qBuilder.getSerials(pkIdLed,pkIdDriver,pkIdELM, sinceDate,endTime,parts);
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
					Document doc = docBuilder.newDocument();
					doc.setXmlStandalone(true);					
					Element rootElement = doc.createElement("NewDataSet");
					doc.appendChild(rootElement);
					Enumeration<String> partsKey=parts.keys();
					while (partsKey.hasMoreElements()) {
						part=parts.get(partsKey.nextElement());
						int c = 0;
						for(String sn:part.getSerials()){							
							/** data validation */
							ResultSet SerialData = query.execute(GETMESDATA+"'"+sn+"'");
							NameMaterial = part.getPartNumber();
							if(part.getType().equalsIgnoreCase("DRIVER") || part.getType().equalsIgnoreCase("ELM")){		
								recipeLed = part.getRevisiones().get(c);
							}else{
								recipeLed = part.getKelvin()+"_"+part.getRevisiones().get(c);
							}	
							boolean validationsFail = false;
							try {
								if(SerialData.next()){
								    if(!SerialData.getString("part_number").equals(NameMaterial)){
								    	query.insert(" INSERT INTO [SFDC_Reporting].[dbo].[CREE_BinningDataTemp] ([BoardType],[SentToOracleDateTime],[MaterialName],[LotID],[LEDRecipe],[Status]) "
											     + " VALUES('"+part.getType()+"','"+part.getSentToOracle()+"','"+NameMaterial+"','"+sn+"','"+recipeLed+"',"+PART_INCORRECT+")");
								    	validationsFail = true;
								    }
								    if(part.getType().equals("LED")){
								    	if(!SerialData.getString("revision").equals(part.getRevisiones().get(c))){
									    	query.insert(" INSERT INTO [SFDC_Reporting].[dbo].[CREE_BinningDataTemp] ([BoardType],[SentToOracleDateTime],[MaterialName],[LotID],[LEDRecipe],[Status]) "
												     + " VALUES('"+part.getType()+"','"+part.getSentToOracle()+"','"+NameMaterial+"','"+sn+"','"+recipeLed+"',"+REV_INCORRECT+")");
									    	validationsFail = true;
									    }
								    }
								    if(validationsFail == false){
										query.insert(" INSERT INTO [SFDC_Reporting].[dbo].[CREE_BinningDataTemp] ([BoardType],[SentToOracleDateTime],[MaterialName],[LotID],[LEDRecipe],[Status]) "
											     + " VALUES('"+part.getType()+"','"+part.getSentToOracle()+"','"+NameMaterial+"','"+sn+"','"+recipeLed+"',"+DATAFILE_GENERATED+")");
									}
								}
								else{
									query.insert(" INSERT INTO [SFDC_Reporting].[dbo].[CREE_BinningDataTemp] ([BoardType],[SentToOracleDateTime],[MaterialName],[LotID],[LEDRecipe],[Status]) "
										     + " VALUES('"+part.getType()+"','"+part.getSentToOracle()+"','"+NameMaterial+"','"+sn+"','"+recipeLed+"',"+SERIAL_NOT_FOUND+")");
									validationsFail = true;
								}
					if(validationsFail == false){		
									Element board = doc.createElement("Boards");
									rootElement.appendChild(board);
									Element boardsType = doc.createElement("BoardType");
									boardsType.appendChild(doc.createTextNode(part.getType()));
									board.appendChild(boardsType);
									Element sentToOracle = doc.createElement("SentToOracleDateTime");
									sentToOracle.appendChild(doc.createTextNode(part.getSentToOracle()));
									board.appendChild(sentToOracle);
									Element materialName = doc.createElement("MaterialName");
									/**conditions good*/
									if(part.getPartNumber().contains("-")){
										if(part.getPartNumber().substring(part.getPartNumber().lastIndexOf("-")+1).length()>=1){
											materialName.appendChild(doc.createTextNode(part.getPartNumber().substring(4,part.getPartNumber().lastIndexOf("-")).toUpperCase()));
										}else{
											materialName.appendChild(doc.createTextNode(part.getPartNumber().substring(4).toUpperCase()));
										}
									}else if(part.getPartNumber().contains("_")){
										if(part.getPartNumber().substring(part.getPartNumber().lastIndexOf("_")+1).length()>=1){
											materialName.appendChild(doc.createTextNode(part.getPartNumber().substring(4,part.getPartNumber().lastIndexOf("_")).toUpperCase()));									
										}else{
											materialName.appendChild(doc.createTextNode(part.getPartNumber().substring(4).toUpperCase()));
										}
									}else if(!part.getPartNumber().contains("_")&&!part.getPartNumber().contains("_")){
										materialName.appendChild(doc.createTextNode(part.getPartNumber().substring(4).toUpperCase()));
									}
									/***/
									
									board.appendChild(materialName);
									Element lotId = doc.createElement("LotID");
									lotId.appendChild(doc.createTextNode(sn));
									board.appendChild(lotId);
									Element ledRecipe = doc.createElement("LEDRecipe");
									if(part.getType().equalsIgnoreCase("DRIVER") || part.getType().equalsIgnoreCase("ELM")){
										ledRecipe.appendChild(doc.createTextNode(part.getRevisiones().get(c).toUpperCase()));
									}else{
										ledRecipe.appendChild(doc.createTextNode(part.getKelvin().toUpperCase()+"_"+part.getRevisiones().get(c).toUpperCase()));
									}					
									board.appendChild(ledRecipe);
					}
										} catch (DOMException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										c++;
										/** execute stored procedure to send report */
								}
							}
					
					// execute result
					Connection con = query.getConnection();
					CallableStatement cs = null;
					try {
						cs = con.prepareCall("{call dbo.SendReportDataFile()}");
						cs.execute();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
							sftp.connect(configs.getFtpHost(), configs.getFtpUser(), configs.getFtpPass(),22);	
							if(sftp.isConnected()){
								File fileShipOut = new File(fileName);
								sftp.copy(SFTP_PATH+fileShipOut.getName(),fileShipOut.getAbsolutePath());
							}
							if(ReportsFrame.sendEmail()){
								em.setRecipients(RECIPIENTS);
								em.send(null,emailBody,EMAILSUBJECT);
								String datafile = checkFile.CkeckFile(ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML");
								String mailBodyFileValidation="File(s):";
								Email mailValidationFile = new Email();
								mailValidationFile.setRecipients(RECIPIENTS_FILE);
								if(!datafile.equals("")){
									mailBodyFileValidation += "DATA FILE " + datafile +" EXISTS IN ftp2.sanmina-sci.com"; 
									mailValidationFile.send(null, mailBodyFileValidation, "DATA FILE VALIDATION");
								}else{
									mailBodyFileValidation += "DATA FILE " + datafile +" NOT EXISTS IN ftp2.sanmina-sci.com PLEASE CHECK IT"; 
									mailValidationFile.send(null, mailBodyFileValidation, "DATA FILE VALIDATION");
								}
							}
							fwriter.writer(ReportDate.getTodaysDate("DB"));
							if(!ReportsFrame.getAutoClose()){
								Dialog.showDialog("REPORTE CREADO!","EXITO",JOptionPane.INFORMATION_MESSAGE,null);
								
							}
						}else{
							em.setRecipients(RECIPIENTS_ERRORS);
							em.send(null, "FTP connection error.", "CREE XML ERROR");
							Dialog.showDialog("ERROR AL CONECTARSE A FTP","ERROR",JOptionPane.ERROR_MESSAGE,null );
						}
					}else{
						fwriter.writer(ReportDate.getTodaysDate("DB"));
						if(!ReportsFrame.getAutoClose()){
							Dialog.showDialog("REPORTE CREADO!","EXITO", JOptionPane.INFORMATION_MESSAGE,null);
						}
					}
				}else{
					Dialog.showDialog("REPORTE NO CREADO!", "SERIALES DUPLICADOS", JOptionPane.ERROR_MESSAGE, null);
				}
			}else{
				em.setRecipients(RECIPIENTS_ERRORS);
				em.send(null, "The report wasn't created because there are part numbers pending to be added.", "CREE XML ERROR");
				Dialog.showDialog("AGREGA LOS NUMEROS DE PARTE FALTANTES", "ERROR", JOptionPane.WARNING_MESSAGE, null);
			}
			}else{
				em.setRecipients(RECIPIENTS_ERRORS);
				em.send(null, "The report wasn't created issues in the replication.", "CREE XML ERROR");
				Dialog.showDialog("PROBLEMAS CON LA REPLICATION", "ERROR", JOptionPane.WARNING_MESSAGE, null);
			}
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}  finally {
			ReportsFrame.switchProgress();
			ReportsFrame.switchButton();
			if(ReportsFrame.getAutoClose()){
				System.exit(0);
			}
		}
	}

	public Hashtable<String,Part> insertSerials(Hashtable<String,Part> parts,ResultSet serials){
		try{
			String partNumber="";
			Part part;
			while(serials.next()){
				partNumber=serials.getString("MaterialName");
				part=parts.get(partNumber);
				part.setBng(serials.getString("LEDRecipe"));
				part.setSentToOracle(serials.getString("SentToOracleDateTime"));
				part.addSerial(serials.getString("LotID"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return parts;
	}
}
