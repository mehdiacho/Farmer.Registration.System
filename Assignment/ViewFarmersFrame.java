package Assignment;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class ViewFarmersFrame extends JFrame implements ActionListener, ItemListener {

    /*Instance variables*/

    public String sql;
    public final String sql1 = "SELECT * FROM farmers";
    public final String sql2 = "SELECT * FROM farmers WHERE registration_status = 'Approved'";
    public final String sql3 = "SELECT * FROM farmers WHERE registration_status = 'Pending'";


    String all = "All Farmers", reg = "Registered Farmers", pend = "Pending Registration";
    private final String[] options = {all, reg, pend};
    Object[][] allRows;
    final String[] columnNames = {"Farmer ID", "First Name", "Last Name", "Contact", "Date-of-Birth", "Gender", "Postal Address"
            , "District", "Sub-District", "Email Address", "Farm Size", "Farming Area(Area Debushed)", "Registration Status"};
    Container container = getContentPane();
    JLabel titleLabel = new JLabel("Admin");
    JComboBox<String> viewComboBox = new JComboBox<>(options);
    JComboBox<String> combo = new JComboBox<>();
    JButton menuBtn = new JButton("Menu");


    DefaultTableModel dm = new DefaultTableModel();
    JTable farmers= new JTable(dm);

    JScrollPane pane = new JScrollPane(farmers);
    JButton refreshBtn = new JButton("Refresh");
    JButton deleteBtn = new JButton("Delete");
    JButton editBtn = new JButton("Edit");
    JButton approveBtn = new JButton("Approve");
    JButton rejectBtn = new JButton("Reject");
    //TableColumn verdict;




    public ViewFarmersFrame(){

        sql = sql1;
        allRows = setTableData(sql);
        setTitle("Admin Page");
        setBounds(33,34,1300,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        //setDefaultState();
        addActionEvent();
        combo.addItem("Approve");
        combo.addItem("Reject");
        combo.addItem("Hold");
        dm.setDataVector(allRows, columnNames);

        //farmers.setSelectionModel();
        farmers.setRowSelectionAllowed(true);



    }

    public ViewFarmersFrame(String query, String selectedView){
        sql = query;
        allRows = setTableData(sql);
        setTitle("Admin Page");
        setBounds(33,34,1300,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        //setDefaultState();
        addActionEvent();
        combo.addItem("Accept");
        combo.addItem("Reject");
        combo.addItem("Hold");
        dm.setDataVector(allRows, columnNames);
        farmers.setRowSelectionAllowed(true);
        viewComboBox.setSelectedItem(selectedView);



    }
     /*public ViewFarmersFrame(String selectedView){
        //sql = query;
        allRows = setTableData(sql);
        setTitle("Admin Page");
        setBounds(10,10,1300,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        //setDefaultState();
        addActionEvent();
        combo.addItem("Accept");
        combo.addItem("Reject");
        combo.addItem("Hold");
        dm.setDataVector(allRows, columnNames);
        TableColumn verdict = farmers.getColumnModel().getColumn(13);
        verdict.setCellEditor(new DefaultCellEditor(combo));
        farmers.setRowSelectionAllowed(true);
        viewComboBox.setSelectedItem(selectedView);



    }*/
    public void setLayoutManager()/*method to set layout manager*/ {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize()/*method to set components bounds*/ {

        //Setting location and Size of each component using setBounds() method.
        titleLabel.setBounds(50, 50, 300, 70);
        viewComboBox.setBounds(50, 200, 150, 30);
        refreshBtn.setBounds(50, 250, 100, 30);
        deleteBtn.setBounds(50, 300, 100, 30);
        editBtn.setBounds(50, 350, 100, 30);
        menuBtn.setBounds(50, 620, 100, 30);
        pane.setBounds(250, 64, 1000, 400);
        approveBtn.setBounds(250, 620, 150, 30);
        rejectBtn.setBounds(420, 620, 150, 30);
        //allFarmersTable.setBounds(250, 200, 1000, 100);
        //registeredFarmersTable.setBounds(250, 200, 1000, 100);
        //pendingFarmersTable.setBounds(250, 200, 1000, 100);


    }
    public void addComponentsToContainer()/*method to add components to a container*/ {
        //Adding each component to the Container
        container.add(titleLabel);
        container.add(viewComboBox);
        container.add(pane);
        container.add(refreshBtn);
        container.add(deleteBtn);
        container.add(editBtn);
        container.add(menuBtn);
        container.add(approveBtn);
        container.add(rejectBtn);
        //container.add(allFarmersTable);
        //container.add(registeredFarmersTable);
        //container.add(pendingFarmersTable);


    }
    public void addActionEvent()/*method to add an ActionListener or ItemListener to a component*/ {
        //adding Action listener to components
        viewComboBox.addItemListener(this);
        viewComboBox.addActionListener(this);
        refreshBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        editBtn.addActionListener(this);
        combo.addActionListener(this);
        farmers.setSelectionMode(1);
        menuBtn.addActionListener(this);
        approveBtn.addActionListener(this);
        rejectBtn.addActionListener(this);
    }
    private void deleteFarmerInDatabase(int currentId) /*method to delete a farmer from the database*/{

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "DELETE FROM farmers WHERE `farmers`.`farmer_id` = ?";
        Connection con;
        PreparedStatement preparedStatement;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully...

            Statement stmt = con.createStatement();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, currentId);



            //insert row into table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0) {
                JOptionPane.showMessageDialog(this, "Delete Completed", "Successful", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(this, "Delete Failed, Try again later!", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }

    }


    private Object[][]  setTableData(String sql)/*method to retrieve rows of farmer info from the database*/{
        ArrayList<Object[]> table = new ArrayList<>(1);
        Object[] tableRow = new Object[14];
        //String tableRows[] = new String[13];
        int currentId, idNum, contactNum, farmSize, farmingArea, i, j;
        //String firstName, lastName, dob, gender, postalAddress, district, subDistrict, emailAddress, registrationStatus;
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";



        //UPDATE farmers SET farmer_id=?, first_name=?, last_name=?, contact=?, date_of_birth=?, gender=?, postal_address=?, " +
        //                    "district=?, sub_district=?, email_address=?, farm_size=?, farming_area=?
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;

        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                tableRow = new Object[]
                        {   rs.getString(1),
                                rs.getString(2)/*firstName*/,
                                rs.getString(3)/*lastName*/,
                                rs.getInt(4)/*contactNum*/,
                                rs.getString(5)/*dob*/,
                                rs.getString(6)/*gender*/,
                                rs.getString(7)/*postalAddress*/,
                                rs.getString(8)/*district*/,
                                rs.getString(9)/*subDistrict*/,
                                rs.getString(10)/*emailAddress*/,
                                rs.getInt(11)/*farmSize*/,
                                rs.getInt(12)/*farmingArea*/,
                                rs.getString(13)/*registrationStatus*/};
                table.add(tableRow);



            }
            rs.close();
            pstmt.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        Object[][] fullTable = new Object[table.size()][13];
        for(i = 0; i < table.size(); i++){
            fullTable[i] = table.get(i);
        }

        return fullTable;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Integer> approved = new ArrayList<>(1);
        ArrayList<Integer> rejected = new ArrayList<>(1);
        ArrayList<Integer> pending = new ArrayList<>(1);
        if(e.getSource() == refreshBtn){

            String option = (String) viewComboBox.getSelectedItem();
            if(option.equals(all)){
                ViewFarmersFrame f = new ViewFarmersFrame(sql1, all);
                dispose();
            }
            if(option.equals(reg)){
                ViewFarmersFrame f = new ViewFarmersFrame(sql2, reg);
                dispose();
            }
            if(option.equals(pend)){
                ViewFarmersFrame f = new ViewFarmersFrame(sql3, pend);
                dispose();
            }
        }

        if(e.getSource() == combo) {
            String status = (String) combo.getSelectedItem();
            if (status.equals("Approve")) {

                //dm.getValueAt(0,0);
                approved.add(farmers.getSelectedRow());

                if (status.equals("Reject")) {
                    rejected.add(farmers.getSelectedRow());
                }
                if (status.equals("Hold")) {
                    pending.add(farmers.getSelectedRow());
                }
            }
        }

        if(e.getSource()== deleteBtn){
            int currentId = Integer.parseInt(farmers.getValueAt(farmers.getSelectedRow(), 0).toString());
            deleteFarmerInDatabase(currentId);
            refreshBtn.doClick();

        }

        if(e.getSource() == editBtn){
            int id = Integer.parseInt(farmers.getValueAt(farmers.getSelectedRow(), 0).toString());
            UpdateFrame update = new UpdateFrame(id, Objects.requireNonNull(viewComboBox.getSelectedItem()).toString(), sql);
            dispose();
        }

        if(e.getSource() == menuBtn){
            AdminLandingFrame frame = new AdminLandingFrame();
            dispose();
        }
        if(e.getSource() == approveBtn){
            String verdict = "Approved";
            int id = Integer.parseInt(farmers.getValueAt(farmers.getSelectedRow(), 0).toString());
            updateFarmerInDatabase(id, verdict);
            refreshBtn.doClick();
        }
        if(e.getSource() == rejectBtn){
            String verdict = "Rejected";
            int id = Integer.parseInt(farmers.getValueAt(farmers.getSelectedRow(), 0).toString());
            updateFarmerInDatabase(id, verdict);
            refreshBtn.doClick();
        }
    }


    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    private void updateFarmerInDatabase(int currentId, String verdict)/*method to update a farmer's info in the database*/ {

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "UPDATE farmers SET registration_status=? " +
                "WHERE farmer_id=?";
        Connection con;
        PreparedStatement preparedStatement;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully...

            Statement stmt = con.createStatement();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, verdict);
            preparedStatement.setInt(2, currentId);



            //insert row into table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0) {
                JOptionPane.showMessageDialog(this, "Update Completed", "Successful", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(this, "Update Failed, Try again later!", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }

    }


}