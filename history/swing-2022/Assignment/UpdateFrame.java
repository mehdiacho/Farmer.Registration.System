package Assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

public class UpdateFrame extends JFrame implements ActionListener, ItemListener {
    public int idNumber;
    private int farmerId;
    private String email;
    private final String currentEmail;
    Container container = getContentPane();
    JLabel titleLabel = new JLabel("Profile Editor");
    JLabel firstNameLabel = new JLabel("First Name");//
    JLabel lastNameLabel = new JLabel("Last Name");//
    JLabel districtLabel = new JLabel("District");//
    JLabel subDistrictLabel = new JLabel("Sub-District");//
    JLabel contactNumLabel = new JLabel("Contact Number");//
    JLabel idNumLabel = new JLabel("ID/Omang");//
    JLabel dobLabel = new JLabel("Date of Birth");//
    JLabel genderLabel = new JLabel("Gender");//
    JLabel postalAddressLabel = new JLabel("Postal Address");//
    JLabel farmSizeLabel = new JLabel("Size of Farm");
    JLabel farmingAreaLabel = new JLabel("Area Debushed");
    JLabel emailAddressLabel = new JLabel("Email Address");

    JTextField firstNameField = new JTextField();//
    JTextField lastNameField = new JTextField();//

    JTextField contactNumField = new JTextField();//
    JTextField idNumField = new JTextField();//
    JTextField postalAddressField = new JTextField();//
    JTextField farmSizeField = new JTextField();
    JTextField farmingAreaField = new JTextField();
    JTextField emailAddressField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton saveBtn = new JButton("Save");
    JButton cancelBtn = new JButton("Cancel");
    private final String[] days
            = { "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25",
            "26", "27", "28", "29", "30",
            "31" };
    private final String[] months
            = { "Jan", "feb", "Mar", "Apr",
            "May", "Jun", "July", "Aug",
            "Sup", "Oct", "Nov", "Dec" };
    private final String[] years
            = { "1995", "1996", "1997", "1998",
            "1999", "2000", "2001", "2002",
            "2003", "2004", "2005", "2006",
            "2007", "2008", "2009", "2010",
            "2011", "2012", "2013", "2014",
            "2015", "2016", "2017", "2018",
            "2019", "2020", "2021", "2022" };
    private final String[] districts
            = { " -- Districts --", "Central", "Chobe", "Ghanzi", "Kgalagadi", "Kgatleng", "Kweneng", "North-East", "North-West/Ngamiland", "South-East", "Southern"};

    String[] subDisDef = {" -- Sub-Districts -- "};


    private final String[] sex = {"Male", "Female"};
    private final String[] status = {"Approve", "Reject", "Hold"};

    JComboBox<String> dayComboBox = new JComboBox<>(days);
    JComboBox<String> monthComboBox = new JComboBox<>(months);
    JComboBox<String> yearComboBox = new JComboBox<>(years);
    JComboBox<String> districtComboBox = new JComboBox<>(districts);
    JComboBox<String> subDistrictComboBox = new JComboBox<>(subDisDef);

    JComboBox<String> genderComboBox = new JComboBox<>(sex);
    JLabel registrationStatusLabel = new JLabel("Registration Status");
    JLabel verdictLabel = new JLabel("Verdict");
    JTextField registrationStatusField = new JTextField();
    JComboBox<String> verdictComboBox = new JComboBox<>(status);
    int a = 50, b = 200, c = 450, d = 600, l1 = 100, l2 = 200, y1 = 50, y2 = 150, y3 = 200, y4 = 250, y5 = 300, y6 = 350, y7 = 400, y8 = 450, y9 = 500;
    public int globalId;
    public String adminQuery, adminSql;

    //Creating constructor of RegistrationFrame() class
    public UpdateFrame() {

        setTitle("Profile Editor Page");
        setBounds(233,9,900,750);
        currentEmail = emailAddressField.getText();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        setEditState();
        setDefaultValues(idNumber);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public UpdateFrame(int idNum, String query, String sql) {
        farmerId = idNum;
        globalId = idNum;
        currentEmail = emailAddressField.getText();
        adminQuery = query;
        adminSql = sql;
        setTitle("Profile Page");
        setBounds(233,9,900,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        setEditState();
        setDefaultValues(idNum);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void setLayoutManager() {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize() {
        //Setting location and Size of each component using setBounds() method.
        titleLabel.setBounds(b+200, y1, 300, 70); //
        firstNameLabel.setBounds(a,y2,l1,30);
        lastNameLabel.setBounds(c,y2,l1,30);
        contactNumLabel.setBounds(a,y3,l1,30);
        idNumLabel.setBounds(c,y3,l1,30);
        dobLabel.setBounds(a,y4,l1,30);
        genderLabel.setBounds(c,y4,l1,30);//
        postalAddressLabel.setBounds(a,y5,l1,30);
        districtLabel.setBounds(c,y5,l1,30);
        subDistrictLabel.setBounds(c,y6,l1,30);
        emailAddressLabel.setBounds(a,y7,l1,30);
        registrationStatusLabel.setBounds(c,y7,l1,30);
        verdictLabel.setBounds(c,y8,l1,30);
        farmSizeLabel.setBounds(a, y9, l1, 30);
        farmingAreaLabel.setBounds(c, y9, l1, 30);
        firstNameField.setBounds(b,y2,l2,30);
        lastNameField.setBounds(d,y2,l2,30);
        contactNumField.setBounds(b,y3,l2,30);
        idNumField.setBounds(d,y3,l2,30);
        dayComboBox.setBounds(b,y4,55,30);
        monthComboBox.setBounds(b+55,y4,60,30);
        yearComboBox.setBounds(b+115,y4,85,30);
        genderComboBox.setBounds(d,y4,100,30);
        postalAddressField.setBounds(b,y5,l2,80);
        districtComboBox.setBounds(d,y5,l2,30);
        subDistrictComboBox.setBounds(d,y6,l2,30);
        emailAddressField.setBounds(b,y7,l2,30);
        registrationStatusField.setBounds(d,y7,l2,30);
        verdictComboBox.setBounds(d,y8,l2,30);
        farmSizeField.setBounds(b,y9, l2, 30);
        farmingAreaField.setBounds(d,y9, l2, 30);


        saveBtn.setBounds(d+50,580,150,30);
        cancelBtn.setBounds(b,580,150,30);


    }
    public void addComponentsToContainer() {
        //Adding each component to the Container
        container.add(titleLabel);
        container.add(firstNameLabel);
        container.add(lastNameLabel);
        container.add(contactNumLabel);
        container.add(idNumLabel);
        container.add(dobLabel);
        container.add(genderLabel);
        container.add(postalAddressLabel);
        container.add(districtLabel);
        container.add(subDistrictLabel);
        container.add(emailAddressLabel);
        container.add(farmSizeLabel);
        container.add(farmingAreaLabel);
        container.add(firstNameField);
        container.add(lastNameField);
        container.add(contactNumField);
        container.add(idNumField);
        container.add(dayComboBox);
        container.add(monthComboBox);
        container.add(yearComboBox);
        container.add(genderComboBox);
        container.add(postalAddressField);
        container.add(districtComboBox);
        container.add(subDistrictComboBox);
        container.add(emailAddressField);
        container.add(farmSizeField);
        container.add(farmingAreaField);
        container.add(saveBtn);
        container.add(cancelBtn);
        container.add(registrationStatusField);
        container.add(registrationStatusLabel);
        container.add(verdictComboBox);
        container.add(verdictLabel);
    }
    public void addActionEvent() {
        //adding Action listener to components
        saveBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        //genderComboBox.addActionListener(this);
        //districtComboBox.addActionListener(this);
        districtComboBox.addItemListener(this);

        //subDistrictComboBox.addActionListener(this);
        verdictComboBox.addActionListener(this);
        verdictComboBox.addItemListener(this);
    }
    public void setDefaultValues(int farmerIdNum) {
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT * FROM farmers WHERE farmer_id = ?";
        String firstName = null, lastName = null, postalAddress = null, gender = null, emailAddress = null, district = null,
                subDistrict = null,  day, month, year, dob = null, registrationStatus = null;
        int idNum = 0, farmSize = 0, farmingArea = 0, contactNum = 0;
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, farmerIdNum);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                idNum = rs.getInt(1);
                farmerId = idNum;
                firstName = rs.getString(2);
                lastName = rs.getString(3);
                contactNum = rs.getInt(4);
                dob = rs.getString(5);
                gender = rs.getString(6);
                postalAddress = rs.getString(7);
                district = rs.getString(8);
                subDistrict = rs.getString(9);
                emailAddress = rs.getString(10);
                email = emailAddress;
                farmSize = rs.getInt(11);
                farmingArea = rs.getInt(12);
                registrationStatus = rs.getString(13);
            }

            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            contactNumField.setText(Integer.toString(contactNum));
            idNumField.setText(Integer.toString(idNum));
            globalId = idNum;
            assert dob != null;
            day = dob.substring(0,2);
            month = dob.substring(3,6);
            year = dob.substring(7);
            dayComboBox.setSelectedItem(day);
            monthComboBox.setSelectedItem(month);
            yearComboBox.setSelectedItem(year);
            genderComboBox.setSelectedItem(gender);
            postalAddressField.setText(postalAddress);
            districtComboBox.setSelectedItem(district);
            subDistrictComboBox.setSelectedItem(subDistrict);
            emailAddressField.setText(emailAddress);
            farmSizeField.setText(Integer.toString(farmSize));
            farmingAreaField.setText(Integer.toString(farmingArea));
            registrationStatusField.setText(registrationStatus);
            rs.close();
            pstmt.close();
            con.close();
            //id = idNum;

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setDefaultState() {
        firstNameField.setEnabled(false);
        lastNameField.setEnabled(false);
        contactNumField.setEnabled(false);
        idNumField.setEnabled(false);
        dayComboBox.setEnabled(false);
        monthComboBox.setEnabled(false);
        yearComboBox.setEnabled(false);
        genderComboBox.setEnabled(false);
        postalAddressField.setEnabled(false);
        districtComboBox.setEnabled(false);
        subDistrictComboBox.setEnabled(false);
        emailAddressField.setEnabled(false);
        registrationStatusLabel.setEnabled(false);
        registrationStatusField.setEnabled(false);
        verdictLabel.setEnabled(false);
        verdictComboBox.setEnabled(false);
        farmSizeField.setEnabled(false);
        farmingAreaField.setEnabled(false);
        saveBtn.setEnabled(false);
        saveBtn.setVisible(false);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(false);


    }
    public void setEditState() {
        firstNameField.setEnabled(true);
        lastNameField.setEnabled(true);
        contactNumField.setEnabled(true);
        idNumField.setEnabled(true);
        dayComboBox.setEnabled(true);
        monthComboBox.setEnabled(true);
        yearComboBox.setEnabled(true);
        genderComboBox.setEnabled(true);
        postalAddressField.setEnabled(true);
        districtComboBox.setEnabled(true);
        subDistrictComboBox.setEnabled(true);
        emailAddressField.setEnabled(true);
        registrationStatusField.setEnabled(false);
        registrationStatusLabel.setEnabled(true);
        verdictLabel.setEnabled(true);
        verdictComboBox.setEnabled(true);
        farmSizeField.setEnabled(true);
        farmingAreaField.setEnabled(true);
        saveBtn.setEnabled(true);
        saveBtn.setVisible(true);
        cancelBtn.setEnabled(true);
        cancelBtn.setVisible(true);
        //verdictComboBox.setSelectedItem(null);

    }

    public String[] subDistrictSelector(String s){
        String[] subList;
        switch (s){
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
        return subList;
    }

    private void updateFarmerInDatabase(int currentId, String currentEmail, String password,
                                        int idNum, String firstName, String lastName,
                                        int contactNum, String dob, String gender, String postalAddress,
                                        String district, String subDistrict, String emailAddress,
                                        int farmSize, int farmingArea)/*method to update a farmers info in the database*/ {

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "UPDATE farmers SET farmer_id=?, first_name=?, last_name=?, contact=?, date_of_birth=?, gender=?, postal_address=?, " +
                "district=?, sub_district=?, email_address=?, farm_size=?, farming_area=? " +
                "WHERE farmer_id=?";
        Connection con;
        PreparedStatement preparedStatement;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully...

            Statement stmt = con.createStatement();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, idNum);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setInt(4, contactNum);
            preparedStatement.setString(5, dob);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, postalAddress);
            preparedStatement.setString(8, district);
            preparedStatement.setString(9, subDistrict);
            preparedStatement.setString(10, emailAddress);
            preparedStatement.setInt(11, farmSize);
            preparedStatement.setInt(12, farmingArea);
            preparedStatement.setInt(13, currentId);


            //insert row into table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0) {
                updateUserInDatabase(currentEmail, firstName, lastName, emailAddress, password);
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

    private void updateUserInDatabase(String currentEmail, String firstName, String lastName,
                                      String emailAddress, String password)/*method to update user info in the database*/{
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String username = firstName + " " + lastName;


        try {
            Connection con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully...

            Statement stmt = con.createStatement();
            String sql = "UPDATE users SET username=?, email_address=?, password=? WHERE email_address=?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, emailAddress);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, currentEmail);
            preparedStatement.executeUpdate();



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


    private boolean idCheck(int idNum)/*method to check if an id number exists in the database that doesn't belong to the user*/{

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT farmer_id FROM farmers WHERE farmer_id = ?";
        int idCheck = 0;
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;

        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, idNum);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                idCheck = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        if(idNum == farmerId){
            return true;
        }
        else return idNum != idCheck;
    }
    private boolean emailCheck(String emailAddress)/*method to check if an email address exists in the database that doesn't belong to the user*/{

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT email_address FROM farmers WHERE email_address = ?", emailCheck = "";
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;

        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, emailAddress);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                emailCheck = rs.getString(1);
            }
            rs.close();
            pstmt.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        if(emailAddress.equals(email)){
            return true;
        }
        else return !emailAddress.equals(emailCheck);
    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == cancelBtn) {
            ViewFarmersFrame admin = new ViewFarmersFrame(adminSql, adminQuery);
            dispose();
            /*setDefaultState();
            setDefaultValues(idNumber);*/
        }
        if(e.getSource() == saveBtn){
            String firstName, lastName, postalAddress, gender, emailAddress, district, subDistrict,  password, confirmPassword,
                    day, month, year, dob, verdict, contact, fSize, fArea, idNumber, registrationStatus;
            int idNum, farmSize, farmingArea, contactNum;

            idNum = Integer.parseInt(idNumField.getText());
            firstName = firstNameField.getText();
            lastName = lastNameField.getText();
            postalAddress = postalAddressField.getText();
            gender = Objects.requireNonNull(genderComboBox.getSelectedItem()).toString();
            contactNum = Integer.parseInt(contactNumField.getText());
            emailAddress = emailAddressField.getText();
            district = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
            subDistrict = Objects.requireNonNull(subDistrictComboBox.getSelectedItem()).toString();
            password = Arrays.toString(passwordField.getPassword());
            farmSize = Integer.parseInt(farmSizeField.getText());
            farmingArea = Integer.parseInt(farmingAreaField.getText());
            day = Objects.requireNonNull(dayComboBox.getSelectedItem()).toString(); // the non null thing here might be an issue
            month = Objects.requireNonNull(monthComboBox.getSelectedItem()).toString();
            year = Objects.requireNonNull(yearComboBox.getSelectedItem()).toString();
            dob = day + "-" + month + "-" + year;
            verdict = Objects.requireNonNull(verdictComboBox.getSelectedItem()).toString();

            //number vars for testing if blank
            idNumber = idNumField.getText();
            contact = contactNumField.getText();
            fSize = farmSizeField.getText();
            fArea = farmingAreaField.getText();
            registrationStatus = registrationStatusField.getText();


            if (  !(idNumber.isBlank())  &&  !(firstName.isBlank())  &&  !(lastName.isBlank()) &&  !(postalAddress.isBlank())  &&  !(gender.isBlank())  &&  !(contact.isBlank())  &&
                    !(emailAddress.isBlank())  &&  !(dob.isBlank())  &&  !(district.isBlank())  &&  !(subDistrict.isBlank())  && !(password.isBlank()) && !(fSize.isBlank()) && !(fArea.isBlank()) ) {

                if(idCheck(idNum) && emailCheck(emailAddress)){
                    updateFarmerInDatabase(globalId, currentEmail, password, idNum, firstName, lastName, contactNum, dob, gender, postalAddress, district, subDistrict, emailAddress, farmSize, farmingArea);                        JOptionPane.showMessageDialog(this, "Update Completed", "Successfull", JOptionPane.INFORMATION_MESSAGE);
                        ViewFarmersFrame admin = new ViewFarmersFrame(adminSql, adminQuery);
                        dispose();
                    }
                    else if(!idCheck(idNum)) {
                        JOptionPane.showMessageDialog(this, "Update Failed, this ID number is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Update Failed, this email address is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }

            } else {
                JOptionPane.showMessageDialog(this, "Update Failed, fill in all required fields", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }




        }


    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        //subDistrictComboBox.removeAllItems();
        if(e.getSource() == districtComboBox) {
            subDistrictComboBox.removeAllItems();
            String districtSelected = Objects.requireNonNull(districtComboBox.getSelectedItem()).toString();
            String[] subList = subDistrictSelector(districtSelected);


            for(String s: subList){
                subDistrictComboBox.addItem(s);
            }
            //subDistrictComboBox.setEnabled(true);
        }

        if(e.getSource() == verdictComboBox){
            String verdict = Objects.requireNonNull(verdictComboBox.getSelectedItem()).toString();
            if(verdict.equals("Approve")){
                registrationStatusField.setText("Approved");
            }
            else if(verdict.equals("Reject")){
                registrationStatusField.setText("Rejected");
            }
            else {
                registrationStatusField.setText("Pending");
            }
        }

    }
}
