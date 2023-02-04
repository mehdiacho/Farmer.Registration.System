package Assignment;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PrintFarmerFrame extends JFrame implements ActionListener, ItemListener {



    public String sql, choice, v1, v2, v3, v4;
    public final String sql1 = "SELECT * FROM farmers";
    public final String sql2 = "SELECT * FROM farmers WHERE registration_status = 'Approved'";
    public final String sql3 = "SELECT * FROM farmers WHERE registration_status = 'Pending'";


    String all = "All Farmers", reg = "Registered Farmers", pend = "Pending Registration";
    private final String[] options = {"District", "Sub-District", "Gender", "All", "None"};
    Object[][] allRows;
    final String[] columnNames = {"Farmer ID", "First Name", "Last Name", "Contact", "Date-of-Birth", "Gender", "Postal Address"
            , "District", "Sub-District", "Email Address", "Farm Size", "Farming Area(Area Debushed)", "Registration Status", "Select"};

    private final String[] districts
            = { "Central", "Chobe", "Ghanzi", "Kgalagadi", "Kgatleng", "Kweneng", "North-East", "North-West/Ngamiland", "South-East", "Southern"};

    private final String[] sex = {"Male", "Female"};
    Container container = getContentPane();
    JLabel titleLabel = new JLabel("Print Farmer List");



    DefaultTableModel dm = new DefaultTableModel(){
        public Class getColumnClass(int column) {
            if(column==13){
                return Boolean.class;
            }
            else{
                return String.class;
            }
        }
    };
    ListSelectionModel model = new ListSelectionModel() {

        @Override
        public void setSelectionInterval(int index0, int index1) {

        }

        @Override
        public void addSelectionInterval(int index0, int index1) {

        }

        @Override
        public void removeSelectionInterval(int index0, int index1) {

        }

        @Override
        public int getMinSelectionIndex() {
            return 0;
        }

        @Override
        public int getMaxSelectionIndex() {
            return 0;
        }

        @Override
        public boolean isSelectedIndex(int index) {
            return false;
        }

        @Override
        public int getAnchorSelectionIndex() {
            return 0;
        }

        @Override
        public void setAnchorSelectionIndex(int index) {

        }

        @Override
        public int getLeadSelectionIndex() {
            return 0;
        }

        @Override
        public void setLeadSelectionIndex(int index) {

        }

        @Override
        public void clearSelection() {

        }

        @Override
        public boolean isSelectionEmpty() {
            return false;
        }

        @Override
        public void insertIndexInterval(int index, int length, boolean before) {

        }

        @Override
        public void removeIndexInterval(int index0, int index1) {

        }

        @Override
        public void setValueIsAdjusting(boolean valueIsAdjusting) {

        }

        @Override
        public boolean getValueIsAdjusting() {
            return false;
        }

        @Override
        public void setSelectionMode(int selectionMode) {

        }

        @Override
        public int getSelectionMode() {
            return 0;
        }

        @Override
        public void addListSelectionListener(ListSelectionListener x) {

        }

        @Override
        public void removeListSelectionListener(ListSelectionListener x) {

        }
    };
    JTable farmers= new JTable(dm);

    JComboBox<String> filterByComboBox = new JComboBox<>(options);
    JComboBox<String> districtComboBox = new JComboBox<>(districts);
    JComboBox<String> subDistrictComboBox = new JComboBox<>();
    JComboBox<String> genderComboBox = new JComboBox<>(sex);
    JButton printBtn = new JButton("Print");
    JButton menuBtn = new JButton("Menu");
    JScrollPane pane = new JScrollPane(farmers);
    JLabel filterByLabel = new JLabel("Filter By");
    JLabel districtLabel = new JLabel("District");
    JLabel subDistrictLabel = new JLabel("Sub-District");
    JLabel genderLabel = new JLabel("Gender");
    JButton filterBtn = new JButton("Filter");
    JButton resetBtn = new JButton("Reset");



    //TableColumn verdict;
    public PrintFarmerFrame(){

        allRows = setTableData(sql1);
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
        setDefaultState();
        addActionEvent();
        dm.setDataVector(allRows, columnNames);
        //farmers.setSelectionModel();
        farmers.setRowSelectionAllowed(true);



    }
    public PrintFarmerFrame(String query, String selectedView, String filterValue){
        sql = query;
        allRows = setTableData(sql, filterValue);
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
        setDefaultState();
        addActionEvent();
        dm.setDataVector(allRows, columnNames);
        farmers.setRowSelectionAllowed(true);
        filterByComboBox.setSelectedItem(selectedView);



    }
    public PrintFarmerFrame(String query, String selectedView, String v1, String v2, String v3){
        sql = query;
        allRows = setTableData(sql, v1, v2, v3);
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
        setDefaultState();
        addActionEvent();
        dm.setDataVector(allRows, columnNames);
        farmers.setRowSelectionAllowed(true);
        filterByComboBox.setSelectedItem(selectedView);



    }

    public void setLayoutManager()/*method to set the layout manager of the container*/ {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize()/*method to set the bounds of components*/ {

        //Setting location and Size of each component using setBounds() method.
        titleLabel.setBounds(50, 50, 300, 70);
        filterByLabel.setBounds(50, 200, 100, 30);
        filterByComboBox.setBounds(120, 200, 150, 30);
        districtLabel.setBounds(50, 250, 100, 30);
        districtComboBox.setBounds(120, 250, 150, 30);
        subDistrictLabel.setBounds(50, 300, 100, 30);
        subDistrictComboBox.setBounds(120, 300, 150, 30);
        genderLabel.setBounds(50, 350, 100, 30);
        genderComboBox.setBounds(120, 350, 150, 30);
        printBtn.setBounds(50, 620, 100, 30);
        menuBtn.setBounds(1150, 620, 100, 30);
        resetBtn.setBounds(50, 570, 100, 30);
        filterBtn.setBounds(50, 520, 100, 30);
        pane.setBounds(300, 64, 950, 400);
        //allFarmersTable.setBounds(250, 200, 1000, 100);
        //registeredFarmersTable.setBounds(250, 200, 1000, 100);
        //pendingFarmersTable.setBounds(250, 200, 1000, 100);


    }
    public void addComponentsToContainer()/*method to add components to the container*/ {
        //Adding each component to the Container
        container.add(titleLabel);
        container.add(filterByComboBox);
        container.add(pane);
        container.add(districtComboBox);
        container.add(subDistrictComboBox);
        container.add(genderComboBox);
        container.add(printBtn);
        container.add(menuBtn);
        container.add(filterByLabel);
        container.add(districtLabel);
        container.add(subDistrictLabel);
        container.add(genderLabel);
        container.add(filterBtn);
        container.add(resetBtn);


        //container.add(allFarmersTable);
        //container.add(registeredFarmersTable);
        //container.add(pendingFarmersTable);


    }

    public void setDefaultState()/*method to set the default state of the components in the container*/{
        filterByComboBox.setSelectedItem(null);
        districtComboBox.setSelectedItem(null);
        subDistrictComboBox.setSelectedItem(null);
        genderComboBox.setSelectedItem(null);
        filterByComboBox.setEnabled(true);
        districtComboBox.setEnabled(false);
        subDistrictComboBox.setEnabled(false);
        genderComboBox.setEnabled(false);
    }
    public void addActionEvent()/*method to add an ItemListener or ActionListener to a component*/ {
        //adding Action listener to components
        filterByComboBox.addItemListener(this);
        filterByComboBox.addActionListener(this);
        districtComboBox.addActionListener(this);
        districtComboBox.addItemListener(this);
        subDistrictComboBox.addActionListener(this);
        genderComboBox.addActionListener(this);
        farmers.setSelectionMode(1);
        printBtn.addActionListener(this);
        menuBtn.addActionListener(this);
        filterBtn.addActionListener(this);
        resetBtn.addActionListener(this);
    }

    public String[] subDistrictSelector(String s)/*method to enforce dependency of sub-district selection on district selection*/{
        String[] subList = new String[0];
        if(s != null) {
            switch (s) {
                case "Central":
                    subList = new String[]{"Bobirwa", "Boteti", "Lerala", "Mahalapye", "Mmadinare", "Mmaphashalala", "Mogorosi", "Nata", "Paje", "Rakops", "Sebina",
                            "Sefhare", "Serowe/Palapye", "Shoshong", "Taupye", "Tonota", "Tutume", "orapa"};

                    break;

                case "Chobe":
                    subList = new String[]{"Kasane"};
                    break;

                case "Ghanzi":
                    subList = new String[]{"Charleshill", "Ghanzi headquarters at New Xhade"};
                    break;

                case "Kgalagadi":
                    subList = new String[]{"Hukuntsi", "Kang", "Tshabong", "Khawa", "Werda", "Makopong", "Omaweneno", "Tsabong", "Kolonkwane", "Middlepits", "Khuis", "Gachibana", "Bokspits",
                            "Struizendam", "Phepheng/Draaihoek", "Maubelo", "Kokotsha", "Maralaleng", "Maleshe"};
                    break;

                case "Kgatleng":
                    subList = new String[]{"Mmathubudukwane", "Mochudi", "Bokaa", "Oodi", "Modipane", "Mabalane", "Sikwane", "Malolwane", "Ramonaka",
                            "Oliphants Drift/ Dikgonnye", "Artesia", "Malotwana", "Leshibitse", "Ramathabaki", "Kgomodiatshaba"};
                    break;

                case "Kweneng":
                    subList = new String[]{"Letlhakeng", "Molepolole"};
                    break;

                case "North-East":
                    subList = new String[]{"Francistown", "Masunga"};
                    break;

                case "North-West/Ngamiland":
                    subList = new String[]{"Ngamiland East", "Ngamiland West", "Okavango"};
                    break;

                case "South-East":
                    subList = new String[]{"Gaborone", "Mogobane", "Otse", "Ramotswa", "Tlokweng"};
                    break;

                case "Southern":
                    subList = new String[]{"Goodhope", "Jwaneng", "Kanye", "Mabutsane", "Moshupa"};
                    break;

                case default:
                    subList = new String[]{"Bobirwa", "Boteti", "Lerala", "Mahalapye"};
                    break;
            }
        }
            return subList;
        
    }

    private Object[][]  setTableData(String sql)/*method to retrieve rows of farmers info from the database*/{
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
    private Object[][]  setTableData(String sql, String filterValue)/*method to retrieve rows of farmers info from the database*/{
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
            pstmt.setString(1, filterValue);
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
    private Object[][]  setTableData(String sql, String v1, String v2, String v3)/*method to retrieve rows of farmers info from the database*/{
        ArrayList<Object[]> table = new ArrayList<>(1);
        Object[] tableRow = new Object[14];

        int  i;

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
            pstmt.setString(1, v1);
            pstmt.setString(2, v2);
            pstmt.setString(3, v3);
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

        if(e.getSource() == filterByComboBox){

            String option = (String) filterByComboBox.getSelectedItem();
            assert option != null;
            if(option.equals("District")){
               districtComboBox.setSelectedItem(null);
               subDistrictComboBox.setSelectedItem(null);
               genderComboBox.setSelectedItem(null);
               districtComboBox.setEnabled(true);
               subDistrictComboBox.setEnabled(false);
               genderComboBox.setEnabled(false);

            }
            if(option.equals("Sub-District")){
                districtComboBox.setSelectedItem(null);
                subDistrictComboBox.setSelectedItem(null);
                genderComboBox.setSelectedItem(null);
                districtComboBox.setEnabled(true);
                subDistrictComboBox.setEnabled(true);
                genderComboBox.setEnabled(false);
            }
            if(option.equals("Gender")){
                districtComboBox.setSelectedItem(null);
                subDistrictComboBox.setSelectedItem(null);
                genderComboBox.setSelectedItem(null);
                districtComboBox.setEnabled(false);
                subDistrictComboBox.setEnabled(false);
                genderComboBox.setEnabled(true);
            }
            if(option.equals("All")){
                districtComboBox.setSelectedItem(null);
                subDistrictComboBox.setSelectedItem(null);
                genderComboBox.setSelectedItem(null);
                districtComboBox.setEnabled(true);
                subDistrictComboBox.setEnabled(true);
                genderComboBox.setEnabled(true);
            }
            if(option.equals("None")){
                districtComboBox.setSelectedItem(null);
                subDistrictComboBox.setSelectedItem(null);
                genderComboBox.setSelectedItem(null);
                districtComboBox.setEnabled(false);
                subDistrictComboBox.setEnabled(false);
                genderComboBox.setEnabled(false);
            }
        }

        if (e.getSource() == printBtn) {
            //String filename = "Farmers by " + choice + ".txt";

            File file = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

            }

            BufferedWriter bfw;
            try {

                assert file != null;
                if (!file.exists()) {
                    file.createNewFile();
                }
                bfw = new BufferedWriter(new FileWriter(file));
                bfw.write(Arrays.toString(columnNames) + "\n");
                for (Object[] s : allRows) {
                    bfw.write(Arrays.toString(s) + "\n");
                }

                bfw.close();
                JOptionPane.showMessageDialog(null, "Successful!");
            } catch (IOException f) {
                // TODO Auto-generated catch block
                f.printStackTrace();
            }
            if (e.getSource() == menuBtn) {
                AdminLandingFrame frame = new AdminLandingFrame();
                dispose();
            }
            if (e.getSource() == resetBtn) {
                setDefaultState();
            }
            if (e.getSource() == filterBtn) {
                String choice = Objects.requireNonNull(filterByComboBox.getSelectedItem()).toString();
                String sql, comboBoxValue;
                if (choice.equals("District")) {
                    sql = "SELECT * FROM farmers WHERE district=?";
                    comboBoxValue = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                } else if (choice.equals("Sub-District")) {
                    sql = "SELECT * FROM farmers WHERE sub_district=?";
                    comboBoxValue = Objects.requireNonNull(subDistrictComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                } else if (choice.equals("Gender")) {
                    sql = "SELECT * FROM farmers WHERE gender=?";
                    comboBoxValue = Objects.requireNonNull(genderComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                } else if (choice.equals("All")) {
                    sql = "SELECT * FROM farmers WHERE district=? AND sub_district=? AND gender=?";
                    v2 = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
                    v3 = Objects.requireNonNull(subDistrictComboBox.getSelectedItem()).toString();
                    v4 = Objects.requireNonNull(genderComboBox.getSelectedItem()).toString();
                /*PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                dispose();*/
                } else {
                    sql = "SELECT * FROM farmers";
                }
            }


        }
        if(e.getSource() == menuBtn){
            AdminLandingFrame frame = new AdminLandingFrame();
            dispose();
        }
        if(e.getSource() == resetBtn){
            setDefaultState();
        }
        if(e.getSource() == filterBtn) {
            String choice = Objects.requireNonNull(filterByComboBox.getSelectedItem()).toString();
            String sql, comboBoxValue;
            switch (choice) {
                case "District" -> {
                    sql = "SELECT * FROM farmers WHERE district=?";
                    comboBoxValue = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                    break;
                }
                case "Sub-District" -> {
                    sql = "SELECT * FROM farmers WHERE sub_district=?";
                    comboBoxValue = Objects.requireNonNull(subDistrictComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                    break;
                }
                case "Gender" -> {
                    sql = "SELECT * FROM farmers WHERE gender=?";
                    comboBoxValue = Objects.requireNonNull(genderComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, comboBoxValue);
                    dispose();
                    break;
                }
                case "All" -> {
                    sql = "SELECT * FROM farmers WHERE district=? AND sub_district=? AND gender=? ";
                    v1 = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
                    v2 = Objects.requireNonNull(subDistrictComboBox.getSelectedItem()).toString();
                    v3 = Objects.requireNonNull(genderComboBox.getSelectedItem()).toString();
                    PrintFarmerFrame frame = new PrintFarmerFrame(sql, choice, v1, v2, v3);
                    dispose();
                    break;
                }
                default -> {
                    PrintFarmerFrame frame = new PrintFarmerFrame();
                    dispose();
                    break;
                }
            }

        }


    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        subDistrictComboBox.removeAllItems();
        if(e.getSource() == districtComboBox) {

            String district = (String) districtComboBox.getSelectedItem();
            assert district != null;
            String[] subList = subDistrictSelector(district);

            for(String s: subList){
                subDistrictComboBox.addItem(s);
            }
        }

    }

    private void updateFarmerInDatabase(int currentId, String verdict) /*method to update farmer info in the database*/{

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