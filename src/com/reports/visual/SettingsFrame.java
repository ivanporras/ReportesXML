package com.reports.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import com.lib.connection.Query;
import com.reports.sys.ListBuilder;
import com.reports.util.Properties;


//VS4E -- DO NOT REMOVE THIS LINE!
public class SettingsFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JLabel labPartNum;
	private JTextField searchField;
	private JButton searchButton;
	private JList allList;
	private JScrollPane allListScroll;
	private DefaultListModel listModelAll;
	private DefaultListModel listModelSelected;
	private JList selectedList;
	private JScrollPane selectedListScroll;
	private JButton addButton;
	private String currentSelected="";
	private JRadioButton driverRadio;
	private JRadioButton ledRadio;
	private ButtonGroup group=new ButtonGroup();
	private String radioSelected="DRIVER";
	private JButton deleteButton;
	private JLabel kelvinLabel;
	private JTextField kelvinText;
	private ListBuilder builder;
	private Query query;
	
	public SettingsFrame() {
		super("Settings");
		initComponents();
		group.add(driverRadio);
		group.add(ledRadio);
		deleteButton.setEnabled(false);
		kelvinText.setEnabled(false);
		addListeners();
		setResizable(false);
		setVisible(true);
		setSize(453, 400);
		try {
			query=new Query(Properties.getJDBC(),Properties.getSRV(),Properties.getDbUser(),Properties.getDbPass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void addListeners(){
		searchButton.addActionListener(this);
		addButton.addActionListener(this);
		driverRadio.addActionListener(this);
		ledRadio.addActionListener(this);
		deleteButton.addActionListener(this);
		searchField.addActionListener(this);
		selectedList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {			
				currentSelected= selectedList.getSelectedValue().toString();
				if(currentSelected==null)
					deleteButton.setEnabled(false);
				else
					deleteButton.setEnabled(true);
			}
		});
	}

	private void initComponents() {
		setTitle("Settings");
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setResizable(false);
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(searchButton(), new Constraints(new Leading(317, 12, 12), new Leading(7, 12, 12)));
		add(getJRadioButton0(), new Constraints(new Leading(149, 10, 10), new Leading(30, 12, 12)));
		add(getJRadioButton1(), new Constraints(new Leading(228, 12, 12), new Leading(30, 12, 12)));
		add(labPartNum(), new Constraints(new Leading(8, 100, 10, 10), new Leading(12, 12, 12)));
		add(getJButton1(), new Constraints(new Leading(319, 85, 10, 10), new Leading(333, 25, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Leading(13, 156, 10, 10), new Leading(104, 223, 12, 12)));
		add(getJButton0(), new Constraints(new Leading(195, 10, 10), new Leading(201, 10, 10)));
		add(getJScrollPane1(), new Constraints(new Leading(284, 157, 12, 12), new Leading(110, 216, 12, 12)));
		add(searchField(), new Constraints(new Leading(111, 202, 10, 10), new Leading(10, 12, 12)));
		add(getJLabel0(), new Constraints(new Leading(142, 10, 10), new Leading(74, 12, 12)));
		add(getJTextField0(), new Constraints(new Leading(191, 95, 12, 12), new Leading(72, 12, 12)));
		initGroup();
		setSize(447, 372);
	}

	private JTextField getJTextField0() {
		if (kelvinText == null) {
			kelvinText = new JTextField();
		}
		return kelvinText;
	}

	private JLabel getJLabel0() {
		if (kelvinLabel == null) {
			kelvinLabel = new JLabel();
			kelvinLabel.setText("Kelvin:");
		}
		return kelvinLabel;
	}


	private void initGroup() {
		group = new ButtonGroup();
		group.add(getJRadioButton0());
		group.add(getJRadioButton1());
		group.add(getJRadioButton0());
		group.add(getJRadioButton1());
	}


	private JButton getJButton1() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setText("BORRAR");
		}
		return deleteButton;
	}


	private JRadioButton getJRadioButton1() {
		if (ledRadio == null) {
			ledRadio = new JRadioButton();
			ledRadio.setText("LED");
		}
		return ledRadio;
	}


	private JRadioButton getJRadioButton0() {
		if (driverRadio == null) {
			driverRadio = new JRadioButton();
			driverRadio.setText("DRIVER");
			driverRadio.setSelected(true);
		}
		return driverRadio;
	}


	private JButton getJButton0() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("ADD");
		}
		return addButton;
	}

	private JScrollPane getJScrollPane1() {
		if (selectedListScroll == null) {
			selectedListScroll = new JScrollPane();
			selectedListScroll.setViewportView(getJList1());
		}
		return selectedListScroll;
	}

	private JList getJList1() {
		if (selectedList == null) {
			listModelSelected = new DefaultListModel();
			selectedList = new JList(listModelSelected);
			selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			builder=new ListBuilder();
			/*
			 * Comment next line to see giu
			 */
			//builder.reload("select Part_Number from Cree_PartNumbers where TYPE='DRIVER'",listModelSelected);
		}
		return selectedList;
	}


	private JScrollPane getJScrollPane0() {
		if (allListScroll == null) {
			allListScroll = new JScrollPane();
			allListScroll.setViewportView(getJList0());
		}
		return allListScroll;
	}

	private JList getJList0() {
		if (allList == null) {
			listModelAll = new DefaultListModel();  
			allList = new JList(listModelAll);
			allList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			//new ListBuilder().reloadList("select Part_Number from VW_CREE_PartNumber",listModelAll);
		}
		return allList;
	}

	private JButton searchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("BUSCAR");
		}
		return searchButton;
	}

	private JTextField searchField() {
		if (searchField == null) {
			searchField = new JTextField();
		}
		return searchField;
	}

	private JLabel labPartNum() {
		if (labPartNum == null) {
			labPartNum = new JLabel();
			labPartNum.setText("Numero de parte:");
		}
		return labPartNum;
	}

	public void driverSelected(boolean clearData){
		radioSelected="DRIVER";
		kelvinText.setEnabled(false);
		kelvinText.setText("");
		if(clearData){
			searchField.setText("");
			listModelAll.removeAllElements();
		}
		builder=new ListBuilder();
		builder.reload("select Part_Number from Cree_PartNumbers where TYPE='DRIVER'",listModelSelected);
	}
	
	
	public void ledSelected(boolean clearData){
		radioSelected="LED";
		kelvinText.setEnabled(true);
		builder=new ListBuilder();
		if(clearData){
			searchField.setText("");
			listModelAll.removeAllElements();
		}
		builder.reload("select Part_Number from Cree_PartNumbers where TYPE='LED'",listModelSelected);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command=event.getActionCommand();
		if(command.equals("BUSCAR")){
			builder=new ListBuilder();
			builder.reload("select Part_Number from VW_CREE_PartNumber where Part_Number LIKE '"+searchField.getText()+"%' and Part_Number NOT LIKE '%PNL' and Part_Number NOT LIKE '%-BOX'", listModelAll);
			if(builder.isDriver()){
				driverRadio.setSelected(true);
				driverSelected(false);
			}
			else{
				ledRadio.setSelected(true);
				ledSelected(false);
			}
		}
		else if(command.equals("ADD")){
			if(!searchField.getText().equals("")&&!listModelAll.isEmpty()){
				String part=searchField.getText();
				if(builder.isDriver())
					driverRadio.setSelected(true);
				else
					ledRadio.setSelected(true);
				if(listModelSelected.contains(part)){
					JOptionPane.showMessageDialog(this,"El número de parte ya existe","Error",JOptionPane.ERROR_MESSAGE);
				}
				else{
					if((!kelvinText.getText().equals("")&&radioSelected.equals("LED"))||(radioSelected.equals("DRIVER"))){
						query.insert("INSERT into Cree_PartNumbers(TYPE,Part_Number,Kelvin) values('"+radioSelected+"','"+part+"','"+kelvinText.getText()+"')");
						listModelSelected.add(0,part);
					}
					else
						JOptionPane.showMessageDialog(this,"Llenar el campo de Kelvin","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
			else
				JOptionPane.showMessageDialog(this,"No se encontraron resultados","Error",JOptionPane.ERROR_MESSAGE);
		}
		else if(command.equals("DRIVER")){
			driverSelected(true);
		}
		else if(command.equals("LED")){
			ledSelected(true);
		}
		else if(command.equals("BORRAR")){
			query.update("delete from Cree_PartNumbers where Part_Number='"+currentSelected+"'");
			listModelSelected.remove(listModelSelected.indexOf(currentSelected));
		}
		else{
			builder=new ListBuilder();
			builder.reload("select Part_Number from VW_CREE_PartNumber where Part_Number LIKE '"+searchField.getText()+"%' and Part_Number NOT LIKE '%PNL' and Part_Number NOT LIKE '%-BOX'", listModelAll);
		}
	}
}
