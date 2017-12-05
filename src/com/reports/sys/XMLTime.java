package com.reports.sys;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.lib.util.Printer;

public class XMLTime {

	public static void  saveTimeToXML(Configurations  time) {
		try {
			File file = new File("config.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Configurations.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(time, file);
			jaxbMarshaller.marshal(time, System.out);
		} catch (JAXBException e) {
			Printer.println("Error saving configurations file: "+e.toString());
			e.printStackTrace();
		}
	}

	public static Configurations loadConfigurations() {
		Configurations config=null;
		try {
			File file = new File("config.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Configurations.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			if(file.exists()){
				config = (Configurations) jaxbUnmarshaller.unmarshal(file);
			}
		} catch (JAXBException e) {
			Printer.println("Error loading configurations: "+e.toString());
			e.printStackTrace();
		}
		return config;
	}
}
