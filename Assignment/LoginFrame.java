package Assignment;

//Importing all necessary Packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static java.lang.System.exit;


//Creating LoginFrame class
public class LoginFrame extends JFrame implements ActionListener {

    /*Instance variables*/
    boolean isAdmin = false;
    Container container = getContentPane();

    JLabel titleLabel = new JLabel("Login");
    JLabel userNameLabel = new JLabel("Username/Email");//
    JLabel passwordLabel = new JLabel("Password");//
    JButton forgotPasswordButton = new JButton("Forgot Password?");
    JButton createAccountButton = new JButton("Create Account");
    JTextField userNameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginBtn = new JButton("Login");
    JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
    TextField hintField = new TextField("hint");
    JButton exitBtn = new JButton("Exit");

    JButton adminLoginBtn = new JButton("Admin Login");
    JButton userLoginBtn = new JButton("User Login");

    public LoginFrame(){
        setTitle("Login Form");
        setBounds(408,84,550,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        userLoginBtn.setEnabled(false);

    }
    public void setLayoutManager()/*method to set layout manager*/ {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize() /*method to set bonds of components*/{
        int a = 50, b = 200, c, d;
        //Setting location and Size of each component using setBounds() method.
        titleLabel.setBounds(200, 50, 300, 70);
        userNameLabel.setBounds(a, 150, 100, 30);
        userNameField.setBounds(b, 150, 200, 30);
        passwordLabel.setBounds(a, 200, 100, 30);
        passwordField.setBounds(b, 200, 200, 30);
        showPasswordCheckBox.setBounds(b, 230, 150, 30);
        createAccountButton.setBounds(200, 370, 150, 30);
        loginBtn.setBounds(200, 300, 150, 30);
        adminLoginBtn.setBounds(370, 500, 120, 30);
        userLoginBtn.setBounds(230, 500, 120, 30);
        exitBtn.setBounds(50, 500, 100, 30);



    }
    public void addComponentsToContainer()/*method to add components to the container*/ {
        //Adding each component to the Container
        container.add(titleLabel);
        container.add(hintField);
        container.add(userNameLabel);
        container.add(userNameField);
        container.add(passwordLabel);
        container.add(passwordField);
        container.add(showPasswordCheckBox);
        container.add(createAccountButton);
        container.add(loginBtn);
        container.add(adminLoginBtn);
        container.add(userLoginBtn);
        container.add(exitBtn);

    }
    public void addActionEvent()/*method to add ActionListener or ItemListener to components*/ {
        //adding Action listener to components
        loginBtn.addActionListener(this);
        forgotPasswordButton.addActionListener(this);
        createAccountButton.addActionListener(this);
        showPasswordCheckBox.addActionListener(this);
        adminLoginBtn.addActionListener(this);
        userLoginBtn.addActionListener(this);
        exitBtn.addActionListener(this);
    }


    //Overriding actionPerformed() method
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String emailAddress, username, password;
            emailAddress = userNameField.getText();
            username = userNameField.getText();
            password = Arrays.toString(passwordField.getPassword());

            if (!(emailAddress.isBlank()) && !(password.isBlank()) && !(username.isBlank())) {

                if (userCheck(emailAddress, username, password, isAdmin)) {
                    JOptionPane.showMessageDialog(this, "Login Completed", "Successful", JOptionPane.INFORMATION_MESSAGE);
                    if(isAdmin){
                        AdminLandingFrame admin = new AdminLandingFrame();
                        dispose();
                    }
                    else {
                        ProfileFrame profile = new ProfileFrame(emailAddress);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Login Failed, Incorrect Email/Username or Password", "Try Again!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed, fill in all required fields", "Try Again!", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == createAccountButton) {
            RegistrationFrame registration = new RegistrationFrame();
            dispose();
        }
        if (e.getSource() == adminLoginBtn) {
            isAdmin = true;
            adminLoginBtn.setEnabled(false);
            userLoginBtn.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Login as Admin", "Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == userLoginBtn) {
            isAdmin = false;
            adminLoginBtn.setEnabled(true);
            userLoginBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Login as User", "Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == showPasswordCheckBox) {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }


        }
        if (e.getSource() == exitBtn){
            dispose();
            exit(0);
        }
    }

    private boolean userCheck(String emailAddress, String username, String password, boolean isAdmin)/*method to check if user exists in the database*/{

        final String url = "jdbc:mysql://localhost/farmersbw?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        final String userName = "root";
        final String passwordDB = "";
        String sql = "SELECT username, email_address, password, is_admin FROM users WHERE (email_address=? OR username=?) AND password=? AND is_admin=?";
        String userCheck = null,emailCheck = null, passwordCheck = null;
        boolean isAdminCheck = false;
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;

        try {
            con = DriverManager.getConnection(url, userName, passwordDB);
            // Connected to database successfully

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, emailAddress);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setBoolean(4, isAdmin);

            rs = pstmt.executeQuery();
            while(rs.next()) {
                userCheck = rs.getString(1);
                emailCheck = rs.getString(2);
                passwordCheck = rs.getString(3);
                isAdminCheck = rs.getBoolean(4);

            }
            rs.close();
            pstmt.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return (emailAddress.equals(emailCheck) || username.equals(userCheck)) && password.equals(passwordCheck) && isAdmin == isAdminCheck;
    }
}
