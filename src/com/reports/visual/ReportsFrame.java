package com.reports.visual;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.reports.sys.BuildFromDate;
import com.reports.sys.BuildFromFile;
import com.reports.sys.BuildFromFileSys;
import com.reports.util.Freader;

public class ReportsFrame extends Frame implements WindowListener,ActionListener{
	public static final int WIDTH=560,HEIGHT=630;
	private static final long serialVersionUID = 1L;
	private static Button launchButton;
	private static Button launchFromFile;
	private static JPanel panButtonSystem;
	private static JPanel panButtonFile;
	private static JPanel mainPanel;
	private static JPanel infoPanel;
	private static JPanel panButtonFileSys;
	private static Button launchFromFileSys;
	private static TextField nomField;
	private static TextField sinceTime;
	private static Label nomLabel;
	private static Label labelTime;
	private static JProgressBar progress;
	private static MenuBar menuBar;
	private static Menu menu;
	private static MenuItem item;
	private static JFileChooser fc;
	private static TextField boardType;
	private static TextField materialName;
	private static TextField ledRecipe;
	private static Label lBoardType;
	private static Label lMaterialName;
	private static Label lLedRecipe;
	private static Checkbox emailCheck;
	private static Checkbox ftpCheck;
	private static boolean autoClose;
	
	//	private static Checkbox insertionCheck;
	public ReportsFrame(){
		super("Reportes CREE");
		setSize(WIDTH,HEIGHT);
		setLayout(new BorderLayout());
		menuBar=new MenuBar();
		menu=new Menu("CONFIGURAR");
		item = new MenuItem("AGREGAR PARTES");
		item.addActionListener(this);
		menu.add(item);
		menuBar.add(menu);
		//PROGRESS
		progress=new JProgressBar();
		progress.setVisible(false);
		//Check
		emailCheck=new Checkbox("Enviar e-mail", false);
		ftpCheck=new Checkbox("Cargar a ftp",false);
		//		insertionCheck=new Checkbox("Insersión");
		//		insertionCheck.setState(true);
		//PANEL
		mainPanel=new JPanel();
		infoPanel=new JPanel();
		panButtonSystem=new JPanel();
		panButtonFile=new JPanel();
		panButtonFileSys=new JPanel();
		panButtonSystem.setLayout(new GridLayout(2,2));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		panButtonFile.setLayout(new GridLayout(4,2));
		panButtonSystem.setBorder(BorderFactory.createTitledBorder("Reporte automático")); 
		panButtonFileSys.setBorder(BorderFactory.createTitledBorder("Reporte a partir de archivo con seriales(Existentes)"));
		panButtonFile.setBorder(BorderFactory.createTitledBorder("Reporte a partir de archivo con seriales(Inexistentes)")); 
		infoPanel.setBorder(BorderFactory.createTitledBorder("Información general")); 
		//BUTTON
		launchFromFile=new Button("Hacer reporte desde archivo");
		launchButton=new Button("Hacer reporte");
		launchFromFileSys=new Button("Hacer reporte desde archivo con seriales existentes");
		launchFromFileSys.addActionListener(this);
		launchButton.addActionListener(this);
		launchFromFile.addActionListener(this);
		//TEXTFIELDS
		boardType=new TextField(15);
		materialName=new TextField(15);
		ledRecipe=new TextField(15);
		nomField=new TextField(5);
		sinceTime=new TextField(10);
		sinceTime.addActionListener(this);
		//LABELS
		lBoardType=new Label("Tipo:");
		lMaterialName=new Label("Número de parte:");
		lLedRecipe=new Label("Led Recipe:");
		nomLabel=new Label("Continuidad de nombre de archivo:");
		labelTime=new Label("Desde la fecha: ");
		//ADD TO PANELS
		infoPanel.add(nomLabel);
		infoPanel.add(nomField);
		infoPanel.add(emailCheck);
		infoPanel.add(ftpCheck);
		//		infoPanel.add(insertionCheck);
		panButtonFileSys.add(launchFromFileSys);
		panButtonSystem.add(labelTime);
		panButtonSystem.add(sinceTime);
		panButtonSystem.add(new Label(""));
		panButtonSystem.add(launchButton);
		panButtonFile.add(lBoardType);
		panButtonFile.add(boardType);
		panButtonFile.add(lMaterialName);
		panButtonFile.add(materialName);
		panButtonFile.add(lLedRecipe);
		panButtonFile.add(ledRecipe);
		panButtonFile.add(new Label(""));
		panButtonFile.add(launchFromFile);
		mainPanel.add(panButtonSystem);
		mainPanel.add(panButtonFile);
		mainPanel.add(panButtonFileSys);
		//FRAME
		addWindowListener(this);
		this.setMenuBar(menuBar);
		this.add("North",infoPanel);
		this.add("Center",mainPanel);
		this.add("South",progress);
		ImageIcon img = new ImageIcon("res/sanmina.PNG");
		this.setIconImage(img.getImage());
		SwingUtilities.updateComponentTreeUI( this );
		setResizable(false);
		setVisible(true);
	}

	public ReportsFrame(String fromDate,String fileName) {
		this();
		nomField.setText(fileName);
		emailCheck.setState(true);
		ftpCheck.setState(true);
		sinceTime.setText(fromDate);
	}

	public static boolean sendEmail(){
		if(emailCheck.getState()){
			return true;
		}else {
			return false;
		}
	}

	public static boolean upload(){
		if(ftpCheck.getState()){
			return true;
		}else{
			return false;
		}
	}

	//	public static boolean insertion(){
	//		if(insertionCheck.getState()){
	//			return true;
	//		}else{
	//			return false;
	//		}
	//	}

	public static void switchProgress(){
		if(progress.isVisible()){
			progress.setVisible(false);
			progress.setIndeterminate(false);
		}else{
			progress.setVisible(true);
			progress.setIndeterminate(true);
		}
	}

	public static void switchButton(){
		if(launchButton.isEnabled()){
			launchButton.setEnabled(false);
			launchFromFileSys.setEnabled(false);
			launchFromFile.setEnabled(false);
		}else{
			launchButton.setEnabled(true);
			launchFromFileSys.setEnabled(true);
			launchFromFile.setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd=event.getActionCommand();
		if(!sinceTime.getText().matches("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}")){
			sinceTime.setText("MM/DD/YYYY");
			JOptionPane.showMessageDialog(this,"La fecha ingresada no es válida","ERROR",JOptionPane.ERROR_MESSAGE);
		}
		else if(cmd.equals("Hacer reporte")){
			BuildFromDate re=new BuildFromDate(nomField.getText(),sinceTime.getText(),false);
			re.start();
		}
		else if(cmd.equals("AGREGAR PARTES")){
			new SettingsFrame();
		}else if(cmd.equals("Hacer reporte desde archivo")){
			fc = new JFileChooser();
			File file=null;
			int returnVal = fc.showOpenDialog(ReportsFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				if(!boardType.getText().equals("")&&!materialName.getText().equals("")&&!ledRecipe.getText().equals("")){
					new BuildFromFile(nomField.getText(),boardType.getText(),materialName.getText(),ledRecipe.getText(),file).start();
				}else{
					JOptionPane.showMessageDialog(null,"Llena los campos!");
				}
			}
		}
		else if(cmd.equals("Hacer reporte desde archivo con seriales existentes")){
			fc=new JFileChooser();
			File file=null;
			int returnVal = fc.showOpenDialog(ReportsFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				new BuildFromFileSys(nomField.getText(),file).start();
			}
		} else{
			loadDate();
		}
	}

	public void loadDate(){
		Freader freader=new Freader();
		String date=freader.reader();
		sinceTime.setText(date);
	}

	public static void main(String args[]){
		if(args.length==0){
			ReportsFrame main=new ReportsFrame();
			ReportsFrame.autoClose=false;
			main.loadDate();
		}else{
			new ReportsFrame(args[0],args[1]);
			ActionEvent myActionEvent = new ActionEvent(launchButton,ActionEvent.ACTION_PERFORMED,launchButton.getActionCommand());
			ReportsFrame.autoClose=true;
			for(ActionListener action:launchButton.getActionListeners()){
				action.actionPerformed(myActionEvent);
			}
		}
	}

	public static boolean getAutoClose(){
		return autoClose;
	}
	
	@Override
	public void windowActivated(WindowEvent event) {
	}

	@Override
	public void windowClosed(WindowEvent event) {
	}

	@Override
	public void windowClosing(WindowEvent event) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent event) {
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
	}

	@Override
	public void windowIconified(WindowEvent event) {
	}

	@Override
	public void windowOpened(WindowEvent event) {
	}

}
