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

public class ProfileFrame extends JFrame implements ActionListener, ItemListener {

    /*Instance variables*/
    public String email, currentEmail;
    private int farmerId;
    Container container = getContentPane();
    //String firstName, lastName, postalAddress, gender, emailAddress, district, subDistrict,  password, confirmPassword, day, month, year, dob, username, contact, fSize, fArea, idNumber;
    //int idNum, farmSize, farmingArea, contactNum;
    JLabel titleLabel = new JLabel("Profile");
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
    JLabel passwordLabel = new JLabel("Password");
    JLabel confirmPasswordLabel = new JLabel("Confirm Password");
    JTextField firstNameField = new JTextField();//
    JTextField lastNameField = new JTextField();//

    JTextField contactNumField = new JTextField();//
    JTextField idNumField = new JTextField();//
    JTextField postalAddressField = new JTextField();//
    JTextField farmSizeField = new JTextField();
    JTextField farmingAreaField = new JTextField();
    JTextField emailAddressField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JPasswordField confirmPasswordField = new JPasswordField();
    JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
    JCheckBox showConfirmPasswordCheckBox = new JCheckBox("Show Password");
    JButton editBtn = new JButton("Edit");
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

    JComboBox<String> dayComboBox = new JComboBox<>(days);
    JComboBox<String> monthComboBox = new JComboBox<>(months);
    JComboBox<String> yearComboBox = new JComboBox<>(years);
    JComboBox<String> districtComboBox = new JComboBox<>(districts);
    JComboBox<String> subDistrictComboBox = new JComboBox<>(subDisDef);

    JComboBox<String> genderComboBox = new JComboBox<>(sex);
    JButton logoutBtn = new JButton("Logout");
    JLabel registrationStatusLabel = new JLabel("Registration Status");
    JTextField registrationStatusField = new JTextField();
    int a = 50, b = 200, c = 450, d = 600, l1 = 100, l2 = 200, y1 = 50, y2 = 150, y3 = 200, y4 = 250, y5 = 300, y6 = 350, y7 = 400, y8 = 450, y9 = 500;
    public int globalId;
    //Creating constructor of RegistrationFrame() class
    public ProfileFrame() {

        setTitle("Profile Page");
        setBounds(10,10,900,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        setDefaultState();
        setDefaultValues("godofbantr@gmail.com1");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public ProfileFrame(String emailAddress) {
        email = emailAddress;
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
        setDefaultState();
        setDefaultValues(email);
        //setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void setLayoutManager()/*method to set the layout manager*/ {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize() /*method to set the bounds of components*/{
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
        passwordLabel.setBounds(c,y7,l1,30);
        confirmPasswordLabel.setBounds(c,y8,l1,30);
        farmSizeLabel.setBounds(a, y9, l1, 30);
        farmingAreaLabel.setBounds(c, y9, l1, 30);
        registrationStatusLabel.setBounds(450, 550, 150, 30);
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
        passwordField.setBounds(d,y7,l2,30);
        confirmPasswordField.setBounds(d,y8,l2,30);
        farmSizeField.setBounds(b,y9, l2, 30);
        farmingAreaField.setBounds(d,y9, l2, 30);
        registrationStatusField.setBounds(d,550, l2, 30);
        showPasswordCheckBox.setBounds(d,430,l2,20);
        showConfirmPasswordCheckBox.setBounds(d,480,l2,20);
        editBtn.setBounds(b+200,600,150,30);
        saveBtn.setBounds(d+50,600,150,30);
        cancelBtn.setBounds(b,600,150,30);
        logoutBtn.setBounds(b,600,150,30);


    }
    public void addComponentsToContainer() /*method to add components to the container*/{
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
        container.add(passwordLabel);
        container.add(confirmPasswordLabel);
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
        container.add(passwordField);
        container.add(confirmPasswordField);
        container.add(showPasswordCheckBox);
        container.add(showConfirmPasswordCheckBox);
        container.add(farmSizeField);
        container.add(farmingAreaField);
        container.add(editBtn);
        container.add(saveBtn);
        container.add(cancelBtn);
        container.add(logoutBtn);
        container.add(registrationStatusLabel);
        container.add(registrationStatusField);
    }
    public void addActionEvent() /*method to set ActionListener or ItemListener on given objects*/{
        //adding Action listener to components
        editBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        logoutBtn.addActionListener(this);
        //genderComboBox.addActionListener(this);
        showPasswordCheckBox.addActionListener(this);
        showConfirmPasswordCheckBox.addActionListener(this);
        //districtComboBox.addActionListener(this);
        districtComboBox.addItemListener(this);
        //subDistrictComboBox.addActionListener(this);
    }

    public void setDefaultValues(String email)/*method to retreive user data from the database to display on profile*/ {
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT * FROM farmers WHERE email_address = ?";
        String firstName = null, lastName = null, postalAddress = null, gender = null, emailAddress = null, district = null, subDistrict = null, day, month,
                year, dob = null, registrationStatus = null;
        int idNum = 0, farmSize = 0, farmingArea = 0, contactNum = 0;
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
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
            currentEmail = emailAddress;
            passwordField.setText(setPassword(email));
            confirmPasswordField.setText(setPassword(email));
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

    public String setPassword(String email)/*method to retrieve user password from the database to display on profile*/ {
        String password = null;
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT password FROM users WHERE email_address = ?";
        String firstName = null, lastName = null, postalAddress = null, gender = null, emailAddress = null, district = null, subDistrict = null, day, month,
                year, dob = null, registrationStatus = null;
        int idNum = 0, farmSize = 0, farmingArea = 0, contactNum = 0;
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;


        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                password = rs.getString(1);
            }


            rs.close();
            pstmt.close();
            con.close();
            //id = idNum;

        }catch(Exception e){
            e.printStackTrace();
        }
        assert password != null;
        password = password.replaceAll("\\[", "");
        password = password.replaceAll("]", "");
        password = password.replaceAll(",", "");
        password = password.replaceAll("\\s", "");
        return password;
    }
    //method to set
    public void setDefaultState()/*method to set the default state of the profile elements*/ {
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
        passwordField.setEnabled(false);
        passwordField.setEchoChar('*');
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setEnabled(false);
        confirmPasswordField.setEchoChar('*');
        confirmPasswordField.setVisible(false);
        showPasswordCheckBox.setEnabled(false);
        showConfirmPasswordCheckBox.setEnabled(false);
        showPasswordCheckBox.setVisible(false);
        showConfirmPasswordCheckBox.setVisible(false);
        farmSizeField.setEnabled(false);
        farmingAreaField.setEnabled(false);
        registrationStatusField.setEnabled(false);
        saveBtn.setEnabled(false);
        saveBtn.setVisible(false);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(false);
        editBtn.setVisible(true);
        editBtn.setEnabled(true);
        logoutBtn.setVisible(true);
        logoutBtn.setEnabled(true);


    }
    public void setEditState()/*method o set the state of the profile elements when the edit button is pressed*/ {
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
        passwordField.setEnabled(true);
        passwordField.setEchoChar('*');
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setEnabled(true);
        confirmPasswordField.setEchoChar('*');
        confirmPasswordField.setVisible(true);
        showPasswordCheckBox.setEnabled(true);
        showConfirmPasswordCheckBox.setEnabled(true);
        showPasswordCheckBox.setVisible(true);
        showConfirmPasswordCheckBox.setVisible(true);
        farmSizeField.setEnabled(true);
        farmingAreaField.setEnabled(true);
        registrationStatusField.setEnabled(false);
        saveBtn.setEnabled(true);
        saveBtn.setVisible(true);
        cancelBtn.setEnabled(true);
        cancelBtn.setVisible(true);
        editBtn.setEnabled(false);
        editBtn.setVisible(false);
        logoutBtn.setEnabled(false);
        logoutBtn.setVisible(false);


    }

    public String[] subDistrictSelector(String s)/*method to enforce a dependancy of the subdistrict on the district selected*/{
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
                                        int idNum, String firstName, String lastName, int contactNum,
                                        String dob, String gender, String postalAddress, String district,
                                        String subDistrict, String emailAddress, int farmSize,
                                        int farmingArea)/*method to update farmer details in the database*/ {

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
                    JOptionPane.showMessageDialog(this, " Farmer Update Failed, Try again later!", "Try Again!", JOptionPane.ERROR_MESSAGE);
                }

                stmt.close();
                con.close();


            }catch(Exception e){
                e.printStackTrace();
            }

        }

    private boolean idCheck(int idNum)/*method to check if a given id number already exists in the database and is not the farmer id number*/{

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
    private boolean emailCheck(String emailAddress)/*method to check if a given email address already exists in the database and is not the farmer id number*/{

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

    private void updateUserInDatabase(String currentEmail, String firstName, String lastName, String emailAddress, String password)/*method to update user details in the database*/{
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
            if(addedRows >= 0) {
                JOptionPane.showMessageDialog(this, "User Update Completed", "Successful", JOptionPane.INFORMATION_MESSAGE);
                ProfileFrame profile = new ProfileFrame(emailAddress);
                dispose();
            }
            else{
                JOptionPane.showMessageDialog(this, "User Update Failed, Try again later!", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == editBtn) {
            farmerId = Integer.parseInt(idNumField.getText());
            setEditState();

        }
        if(e.getSource() == cancelBtn) {
            setDefaultState();
            setDefaultValues(email);
        }
        if(e.getSource() == saveBtn){
            String firstName, lastName, postalAddress, gender, emailAddress, district, subDistrict,  password, confirmPassword, day, month, year, dob, contact, fSize, fArea, idNumber;
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
            confirmPassword = Arrays.toString(confirmPasswordField.getPassword());
            farmSize = Integer.parseInt(farmSizeField.getText());
            farmingArea = Integer.parseInt(farmingAreaField.getText());
            day = Objects.requireNonNull(dayComboBox.getSelectedItem()).toString(); // the non null thing here might be an issue
            month = Objects.requireNonNull(monthComboBox.getSelectedItem()).toString();
            year = Objects.requireNonNull(yearComboBox.getSelectedItem()).toString();
            dob = day + "-" + month + "-" + year;
            //number vars for testing if blank
            idNumber = idNumField.getText();
            contact = contactNumField.getText();
            fSize = farmSizeField.getText();
            fArea = farmingAreaField.getText();


            if (  !(idNumber.isBlank())  &&  !(firstName.isBlank())  &&  !(lastName.isBlank()) &&  !(postalAddress.isBlank())  &&  !(gender.isBlank())  &&  !(contact.isBlank())  &&
                    !(emailAddress.isBlank())  &&  !(dob.isBlank())  &&  !(district.isBlank())  &&  !(subDistrict.isBlank())  && !(password.isBlank()) && !(fSize.isBlank()) && !(fArea.isBlank()) ) {

                if(password.equals(confirmPassword)) {
                    if(idCheck(idNum) && emailCheck(emailAddress)){
                        updateFarmerInDatabase(globalId, currentEmail, password, idNum, firstName, lastName, contactNum, dob, gender, postalAddress, district, subDistrict, emailAddress, farmSize, farmingArea);
                        //JOptionPane.showMessageDialog(this, "Update Completed", "Successfull", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(!idCheck(idNum)) {
                        JOptionPane.showMessageDialog(this, "Update Failed, this ID number is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Update Failed, this email address is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this, "Confirm Password does not match" , "Try Again!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Update Failed, fill in all required fields", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }




        }
        if(e.getSource() == logoutBtn){
            LoginFrame frame = new LoginFrame();
            dispose();
        }
        if (e.getSource() == showPasswordCheckBox) {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            }
            else{
                passwordField.setEchoChar('*');
            }
        }
        if (e.getSource() == showConfirmPasswordCheckBox) {
            if (showConfirmPasswordCheckBox.isSelected()) {
                confirmPasswordField.setEchoChar((char) 0);
            }
            else{
                confirmPasswordField.setEchoChar('*');
            }
        }
        /*if(e.getSource() == districtComboBox) {
            subDistrictComboBox.removeAllItems();
            String districtSelected = (String) districtComboBox.getSelectedItem();
            String[] subList = subDistrictSelector(districtSelected);

            for(String s: subList){
                subDistrictComboBox.addItem(s);
            }
        }*/

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        //subDistrictComboBox.removeAllItems();
        if(e.getSource() == districtComboBox) {
            subDistrictComboBox.removeAllItems();
            String districtSelected = (String) districtComboBox.getSelectedItem();
            String[] subList = subDistrictSelector(districtSelected);

            for(String s: subList){
                subDistrictComboBox.addItem(s);
            }
        }
    }
}