package Assignment;

//Importing all necessary Packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Objects;

//Creating RegistrationFrame class
public class RegistrationFrame extends JFrame implements ActionListener, ItemListener {


    Container container = getContentPane();


    JLabel titleLabel = new JLabel("Registration");
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
    JButton resetBtn = new JButton("Clear All");
    JButton registerBtn = new JButton("Register");

    private final String[] days
            = { "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
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
            = { "Central", "Chobe", "Ghanzi", "Kgalagadi", "Kgatleng", "Kweneng", "North-East", "North-West/Ngamiland", "South-East", "Southern"};
    public String[] subDistricts;

    private final String[] sex = {"Male", "Female"};

    JComboBox<String> dayComboBox = new JComboBox<>(days);
    JComboBox<String> monthComboBox = new JComboBox<>(months);
    JComboBox<String> yearComboBox = new JComboBox<>(years);
    JComboBox<String> districtComboBox = new JComboBox<>(districts);
    JComboBox<String> subDistrictComboBox = new JComboBox<>();
    JComboBox<String> genderComboBox = new JComboBox<>(sex);
    JButton canceBtn = new JButton("Cancel");

    int a = 50, b = 200, c = 450, d = 600, l1 = 100, l2 = 200, y1 = 50, y2 = 150, y3 = 200, y4 = 250, y5 = 300, y6 = 350, y7 = 400, y8 = 450, y9 = 500;


    //Creating constructor of RegistrationFrame() class
    public RegistrationFrame() {


        setTitle("Registration Form");
        setBounds(233,9,900,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);

        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        setDefaultState();
        addActionEvent();
        //setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void setDefaultState(){
        districtComboBox.setSelectedItem(null);
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
        passwordLabel.setBounds(c,y7,l1,30);
        confirmPasswordLabel.setBounds(c,y8,l1,30);
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
        passwordField.setBounds(d,y7,l2,30);
        confirmPasswordField.setBounds(d,y8,l2,30);
        farmSizeField.setBounds(b,y9, l2, 30);
        farmingAreaField.setBounds(d,y9, l2, 30);
        showPasswordCheckBox.setBounds(d,430,l2,20);
        showConfirmPasswordCheckBox.setBounds(d,480,l2,20);
        registerBtn.setBounds(b+200,580,150,30);
        resetBtn.setBounds(b+200,620,150,30);
        canceBtn.setBounds(b+200,660,150,30);


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
        container.add(registerBtn);
        container.add(resetBtn);
        container.add(canceBtn);
    }
    public void addActionEvent() {
        //adding Action listener to components
        resetBtn.addActionListener(this);
        registerBtn.addActionListener(this);
        genderComboBox.addActionListener(this);
        showPasswordCheckBox.addActionListener(this);
        showConfirmPasswordCheckBox.addActionListener(this);
        districtComboBox.addItemListener(this);
        canceBtn.addActionListener(this);

    }

    //Overriding actionPerformed() method
    @Override
    public void actionPerformed(ActionEvent e) {
        //Coding Part of register button
        if (e.getSource() == registerBtn) {
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

            //username = firstName + " " + lastName;

            if (  !(idNumber.isBlank())  &&  !(firstName.isBlank())  &&  !(lastName.isBlank()) &&  !(postalAddress.isBlank())  &&  !(gender.isBlank())  &&  !(contact.isBlank())  &&
                    !(emailAddress.isBlank())  &&  !(dob.isBlank())  &&  !(district.isBlank())  &&  !(subDistrict.isBlank())  && !(password.isBlank()) && !(fSize.isBlank()) && !(fArea.isBlank()) ) {

                if(password.equals(confirmPassword)) {
                    if(idCheck(idNum) && emailCheck(emailAddress)){
                        farmer = addFarmerToDatabase(idNum, firstName, lastName, contactNum, dob, gender, postalAddress, district, subDistrict, emailAddress, farmSize, farmingArea);
                        user = addUserToDatabase(firstName, lastName, emailAddress, password);
                        JOptionPane.showMessageDialog(this, "Registration Completed", "Successful", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        LoginFrame login = new LoginFrame();
                    }
                    else if(!idCheck(idNum)) {
                        JOptionPane.showMessageDialog(this, "Registration Failed, this ID number is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Registration Failed, this email address is already registered to an account", "Try Again!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this, "Confirm Password does not match" , "Try Again!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed, fill in all required fields", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }






        }
        //Coding Part of RESET button
        if (e.getSource() == resetBtn) {
            firstNameField.setText("");
            lastNameField.setText("");
            contactNumField.setText("");
            idNumField.setText("");
            postalAddressField.setText("");
            emailAddressField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            farmSizeField.setText("");
            farmingAreaField.setText("");
            dayComboBox.setSelectedItem(null);
            monthComboBox.setSelectedItem(null);
            yearComboBox.setSelectedItem(null);
            genderComboBox.setSelectedItem(null);
            districtComboBox.setSelectedItem(null);
            subDistrictComboBox.setSelectedItem(null);
        }
        //Coding Part of showPassword JCheckBox
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
        if (e.getSource() == canceBtn){
            LoginFrame frame = new LoginFrame();
            dispose();
        }


    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        subDistrictComboBox.removeAllItems();
        if(e.getSource() == districtComboBox) {

            String district = (String) districtComboBox.getSelectedItem();
            String[] subList = subDistrictSelector(district);

            for(String s: subList){
                subDistrictComboBox.addItem(s);
            }
        }
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
    public Farmer farmer;
    private Farmer addFarmerToDatabase(int idNum, String firstName, String lastName, int contactNum, String dob, String gender, String postalAddress, String district, String subDistrict, String emailAddress, int farmSize, int farmingArea) {
        Farmer farmer = null;
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "INSERT INTO farmers (farmer_id, first_name, last_name, contact, date_of_birth, gender, postal_address, district, sub_district, email_address, farm_size, farming_area)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        Connection con;
        PreparedStatement preparedStatement;
        //ResultSet rs;

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


            //insert row into table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0) {
                farmer = new Farmer();
                farmer.setIdNum(idNum);
                farmer.setFirstName(firstName);
                farmer.setLastName(lastName);
                farmer.setContactNum(contactNum);
                farmer.setDob(dob);
                farmer.setGender(gender);
                farmer.setPostalAddress(postalAddress);
                farmer.setDistrict(district);
                farmer.setSubDistrict(subDistrict);
                farmer.setEmailAddress(emailAddress);
                farmer.setFarmSize(farmSize);
                farmer.setFarmingArea(farmingArea);



            }

            stmt.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }

        return farmer;
    }

    public User user;
    private User addUserToDatabase(String firstName, String lastName, String emailAddress, String password){
        User user = null;
        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String username = firstName + " " + lastName;


        try {
            Connection con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully...

            Statement stmt = con.createStatement();
            String sql = "INSERT INTO users(username, email_address, password)" +
                    "VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, emailAddress);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();



            //insert row into table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0) {
                user = new User();
                user.setUserName(userName);
                user.setEmailAddress(emailAddress);
                user.setPassword(password);



            }

            stmt.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }

        return user;
    }
    private boolean idCheck(int idNum){

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
        return idNum != idCheck;
    }
    private boolean emailCheck(String emailAddress){

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
        return !emailAddress.equals(emailCheck);
    }


}
