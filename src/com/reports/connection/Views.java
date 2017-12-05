package com.reports.connection;


public interface Views {


	public static final String INSERTFILE="Insert into Cree_TempData(BoardType,SentToOracleDateTime,MaterialName,LotID,LEDRecipe)"+
			" select "+
			" case ((CHARINDEX('-',p.Part_Number))) when 0 then 'DRIVER' else 'LED' end,"+ 
			" (REPLACE(CONVERT(varchar, GETDATE(), 111), '/', '-') + 'T' + CONVERT(varchar, GETDATE(), 108)+ '-06:00'),"+
			" case ((CHARINDEX('-',p.Part_Number))) when 0 then SUBSTRING(p.Part_Number, 5, LEN(p.Part_Number) - 4) else SUBSTRING(p.Part_Number, 5,CHARINDEX('-',p.Part_Number) - 5) end,'@SERIALNUM@',"+
			" case ((CHARINDEX('-',p.Part_Number))) when 0 then CONVERT(varchar, GETDATE(),111) else pn.Kelvin+'K_'+SUBSTRING(p.Part_Number, CHARINDEX('S', p.Part_Number), 3) end"+
			" from MESWEB_GDL1AMAEGW01.MESR.dbo.part as p"+ 
			" Inner Join MESWEB_GDL1AMAEGW01.MESR.dbo.serial as s on s.Part_Id=p.Part_id"+
			" Inner Join  SFDC_Reporting.dbo.Cree_PartNumbers AS pn ON p.Part_Number like pn.Part_Number+'%'"+
			" where s.Serial_Number='@SERIALNUM@'";

	
	public static final String GETMESPARTS="Select * from vw_Parts";
	public static final String GETSQLPARTS="SELECT Part_Number as part, TYPE as type, Kelvin as kelvin from Cree_PartNumbers WHERE Part_Number=";
	public static final String GETMESDATA="SELECT b.part_number,a.revision  FROM MWW01.MesR.dbo.serial a "+ 
										  " INNER JOIN MWW01.MesR.dbo.part b on b.pk_id=a.part_pk_id "+ 
										  " INNER JOIN MWW01.MesR.dbo.location l ON l.pk_id =a.location_pk_id and a.Project_pk_id = b.project_pk_id "+  
										  " WHERE  a.project_pk_id='21' and a.serial_number=";
	public static final String GETREPLICATION="SELECT TOP 1 [dttm_end_sql],[queue_qty]  FROM [MWW01].[MesR].[dbo].[replication_monitor]  where project_pk_id = 21";
	
}
