package Assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AdminLandingFrame extends JFrame implements ActionListener, ItemListener {

    Container container = getContentPane();

    JLabel titleLabel = new JLabel("Menu");
    JButton viewFarmersBtn = new JButton("View Farmers");
    JButton printFarmersBtn = new JButton("Print Farmers");
    JButton logoutBtn = new JButton("Logout");


    public AdminLandingFrame(){
        setTitle("Landing Page");
        setBounds(433,84,500,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Calling setLayoutManger() method inside the constructor.
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();

    }
    public void setLayoutManager() {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }
    public void setLocationAndSize() {

        //Setting location and Size of each component using setBounds() method.
        titleLabel.setBounds(200, 50, 300, 70);
        viewFarmersBtn.setBounds(100, 150, 300, 70);
        printFarmersBtn.setBounds(100, 250, 300, 70);
        logoutBtn.setBounds(350, 500, 100, 30);

    }
    public void addComponentsToContainer() {
        //Adding each component to the Container
        container.add(titleLabel);
        container.add(viewFarmersBtn);
        container.add(printFarmersBtn);
        container.add(logoutBtn);

    }
    public void addActionEvent() /*method to add an ItemListener or ActionListener to components*/{
        //adding Action listener to components
        viewFarmersBtn.addActionListener(this);
        printFarmersBtn.addActionListener(this);
        logoutBtn.addActionListener(this);
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == logoutBtn){
            LoginFrame frame = new LoginFrame();
            dispose();
        }
        if(e.getSource() == viewFarmersBtn){
            ViewFarmersFrame frame = new ViewFarmersFrame();
            dispose();
        }
        if(e.getSource() == printFarmersBtn){
            PrintFarmerFrame frame = new PrintFarmerFrame();
            dispose();
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
