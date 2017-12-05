package com.reports.connection;

import java.awt.Frame;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.swing.JOptionPane;

import com.lib.connection.Query;
import com.lib.vs.Dialog;
import com.reports.sys.Part;
import com.reports.util.Properties;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Operations implements Views{
	private Query query;

	public Operations(){ 
		query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
	}

	/**
	 * NOT USED
	 * @param part
	 * @return
	 */
	public ResultSet getXMLResultSet(String beginingTime,String endTime,Frame frame){
		ResultSet resSet=null;
//				try {
//					_BOARDS=VIEW_BOARDS.replaceAll("@INICIO@",beginingTime);
//					_BOARDS=_BOARDS.replaceAll("@FIN@", endTime);
//					_BOARDS=_BOARDS.replaceAll("@PKLED@",getPkId("LED"));
//					BOARDS=VIEWBOARDS.replaceAll("@INICIO@",beginingTime);
//					BOARDS=BOARDS.replaceAll("@FIN@", endTime);
//					BOARDS=BOARDS.replaceAll("@PKDRIVER@",getPkId("DRIVER"));
//					query.insert("Insert into CREE_BinningData(BoardType,SentToOracleDateTime,MaterialName,LotID,LEDRecipe) "+_BOARDS +" Union "+BOARDS);
//					query.update("Update CREE_BinningData set DateImported=CURRENT_TIMESTAMP where DateImported is NULL");
//					System.out.println("(("+_BOARDS+") UNION ("+BOARDS+")) for xml path('Boards'), type, elements, ROOT ('NewDataSet')");
//					resSet=query.execute("(("+_BOARDS+") UNION ("+BOARDS+")) for xml path('Boards'), type, elements, ROOT ('NewDataSet')");	
//				} catch (Exception e) {
//					e.printStackTrace();
//					if(e.toString().contains("duplicate key"))
//						JOptionPane.showMessageDialog(frame,"Existen seriales duplicados desde la fecha indicada!","PROCESO ABORTADO!",JOptionPane.ERROR_MESSAGE);
//				}
		return resSet;
	}
//
//	public boolean insertLotsToSQL(String ledQuery,String driverQuery){
//		try{
//			query.insert("Insert into CREE_BinningData(SentToOracleDateTime,MaterialName,LotID,LEDRecipe) "+ledQuery +" Union "+driverQuery);
//			query.update("Update CREE_BinningData set DateImported=CURRENT_TIMESTAMP where DateImported is NULL");
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			return false;
//		}
//	}


	private String buildPk(String part){
		String pkId="";
		ResultSet pks=query.execute("select Pk_Id from VW_CREE_PartNumber where (Part_Number = '"+part+"')");
		try {
			while(pks.next()){
				pkId+=pks.getString("Pk_Id");
				pkId+=",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pkId;
	}

	public String getPkId(String parte){
		String pkId="";
		if(parte!=null||!"".equals(parte)){
			try {
				pkId=buildPk(parte);
			} catch (Exception e) {
				Dialog.showDialog("ERROR OBTENIENDO PKIDs", "ERROR", JOptionPane.ERROR_MESSAGE, null);
			}
		}
		if(pkId.contains(",")){
			pkId=pkId.substring(0,pkId.lastIndexOf(","));
		}
		return pkId;
	}

	public Hashtable<String,Part> insertSerials(Hashtable<String,Part> parts,ResultSet serials){
		try{
			String partNumber="";
			Part part;
			while(serials.next()){				
				partNumber = serials.getString("MaterialName"); // MaterialName = PartNumber
				part = parts.get(partNumber); 
				part.setSentToOracle(serials.getString("SentToOracleDateTime")); 
				part.addSerial(serials.getString("LotID")); // Unit / Serial
				part.addRevisiones(serials.getString("LEDRecipe")); // Binning
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return parts;
	}


	public Hashtable<String,Part> getSerials(String pkLed,String pkDriver,String pkIdELM,String desdeFecha,String hastaFecha,Hashtable<String,Part> parts){
		ResultSet result=null;
		try{
			/* DRIVER */
			Connection con = query.getConnection();
			CallableStatement cs = null;
			cs = con.prepareCall("{call dbo.insertPkDriver(?)}");
			cs.setString(1, pkDriver);
			cs.executeUpdate();
			cs = con.prepareCall("{call dbo.driver(?,?)}");
			cs.setString(1, desdeFecha);
			cs.setString(2, hastaFecha);
			cs.execute();
			result = cs.getResultSet();
			parts=insertSerials(parts,result);			
			query.insert("insert into logDataFile ([Description])  values ('EXTRACT DRIVER EXECUTED')");
			/* ELM */
			cs = con.prepareCall("{call dbo.insertPkElm(?)}");
			cs.setString(1, pkIdELM);
			cs.executeUpdate();
			cs = con.prepareCall("{call dbo.elm(?,?)}");
			cs.setString(1, desdeFecha);
			cs.setString(2, hastaFecha);
			cs.execute();
			result = cs.getResultSet();
			parts=insertSerials(parts,result);
			query.insert("insert into logDataFile ([Description])  values ('EXTRACT ELM EXECUTED')");
			/* LED */
			cs = con.prepareCall("{call dbo.insertPkLed(?,?)}");
			cs.setString(1, pkLed.substring(0,7499));
			cs.setString(2, pkLed.substring(7500));
			cs.executeUpdate();
			cs=con.prepareCall("{call dbo.led(?,?)}");
			cs.setString(1, desdeFecha);
			cs.setString(2, hastaFecha);
			cs.execute();
			result = cs.getResultSet();
			parts=insertSerials(parts,result);
			query.insert("insert into logDataFile ([Description])  values ('EXTRACT LED EXECUTED')");
			
			
//			PropertiesConfiguration config = new PropertiesConfiguration("c:/Scheculer/config.properties");
//			config.setProperty("TIME", "00 00 15");			
//			config.save();
//	 
//			System.out.println("Config Property Successfully Updated..");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			query.update("DELETE FROM TEMP_PKDRIVER");
			query.update("DELETE FROM TEMP_PKLED");
			query.update("DELETE FROM TEMP_PKELM");
		}
		return parts;
	}

}
