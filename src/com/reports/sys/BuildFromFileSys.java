package com.reports.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import com.lib.connection.Query;
import com.lib.email.Email;
import com.lib.file.ApacheFTP;
import com.reports.connection.Views;
import com.reports.util.Properties;
import com.reports.util.ReportDate;
import com.reports.util.SystemValues;
import com.reports.visual.ReportsFrame;

public class BuildFromFileSys extends Thread implements SystemValues,Views{
	private String fileName;
	private ApacheFTP uploader;
	private String lastExt;
	private File file;
	private Query query;

	public BuildFromFileSys(String lastExt,File file) {
		this.file=file;
		this.lastExt=lastExt;
		fileName=PATH+ReportDate.getTodaysDate("FILENAME")+lastExt.toUpperCase()+".XML";
		uploader=new ApacheFTP(Properties.getFtpDomain(),Properties.getDbUser(),Properties.getFtpPass());
		query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
	}

	@Override
	public void run() {
		ReportsFrame.switchProgress();
		ReportsFrame.switchButton();
		String sCurrentLine; 
		BufferedReader br = null;
		new File("CreeReports").mkdirs();
		try {
			br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.equals("")){
					String insertQuery=INSERTFILE.replaceAll("@SERIALNUM@", sCurrentLine);
					query.execute(insertQuery);
				}
			}
			ResultSet result=query.execute("select * from Cree_TempData for xml path('Boards'), type, elements, ROOT ('NewDataSet')");
			new SQLXml().createXML(result, fileName);
			query.execute("delete from Cree_TempData");
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
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
