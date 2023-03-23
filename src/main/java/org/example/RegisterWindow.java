package org.example;

import org.example.tools.StreamUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User: HP
 * Date: 2023/3/3
 * WELCOME!
 */
public class RegisterWindow {
    JFrame Jframe;
    int x;
    int y;

    public RegisterWindow() {
        Jframe = new JFrame();
        Jframe.setBackground(Color.lightGray);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        x = (int) ((screenSize.getWidth() - Jframe.getWidth()) / 2);
        y = (int) ((screenSize.getHeight() - Jframe.getHeight()) / 2);
        Jframe.setBounds(x - 200, y - 350, 500, 700);
        Jframe.setResizable(false);
//        JFrame.setLayout(new BorderLayout());
        Jframe.setLayout(null);
        Jframe.setAlwaysOnTop(true);
        Jframe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Jframe.setTitle("Register for new");
        Jframe.setVisible(true);
        Jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Jframe.dispose();
            }
        });
    }

    public void start() {
        //notice
        Container notice = new Container();
        notice.setLayout(new FlowLayout());
        notice.setBounds(10, 0, 500, 115);

        JLabel rule1 = new JLabel("                                Notice", JLabel.LEFT);
        notice.add(rule1);
        rule1.setPreferredSize(new Dimension(400, 17));

        JLabel rule2 = new JLabel("1. The length of username/password should be at least 6 and at most 20", JLabel.LEFT);
        notice.add(rule2);
        rule2.setPreferredSize(new Dimension(500, 17));

        JLabel rule3 = new JLabel("2. Username/password should only contain numbers, letters and '@#$%^&_'", JLabel.LEFT);
        notice.add(rule3);
        rule3.setPreferredSize(new Dimension(500, 17));

        JLabel rule4 = new JLabel("3. The items with '*' are not required", JLabel.LEFT);
        notice.add(rule4);
        rule4.setPreferredSize(new Dimension(500, 17));

        JLabel rule5 = new JLabel("4. If you prefer not to say your age, fill out -1", JLabel.LEFT);
        notice.add(rule5);
        rule5.setPreferredSize(new Dimension(500, 17));

        Jframe.add(notice);
        //information fill-up
        Container container = new Container();
        container.setBounds(0, 130, 460, 520);
        container.setVisible(true);//container center for information forms
        //username
        JLabel userName = new JLabel("username", JLabel.CENTER);
        userName.setBackground(Color.gray);
        userName.setBounds(0, 0, 100, 26);
        container.add(userName);

        JTextField userNameFill = new JTextField();
        userNameFill.setActionCommand("123");
        userNameFill.setBounds(100, 0, 300, 26);
        container.add(userNameFill);

        //password
        JLabel passWord = new JLabel("password", JLabel.CENTER);
        passWord.setBackground(Color.DARK_GRAY);
        passWord.setBounds(0, 30, 100, 26);
        container.add(passWord);

        JTextField passwordFill = new JPasswordField();
        passwordFill.setBounds(100, 30, 300, 26);
        container.add(passwordFill);
        //age
        JLabel age = new JLabel("age(-1~120)", JLabel.CENTER);
//        age.setBackground(Color.BLACK);
        age.setBounds(0, 60, 100, 26);
        container.add(age);

        JTextField ageFill = new JTextField();
        ageFill.setBounds(100, 60, 300, 26);
        container.add(ageFill);
        //sex
        JLabel sex = new JLabel("Sex", JLabel.CENTER);
//        sex.setBackground(Color.BLACK);
        sex.setBounds(0, 90, 100, 26);
        container.add(sex);

        CheckboxGroup sexGroup = new CheckboxGroup();
        Checkbox sex1 = new Checkbox("Male", sexGroup, false);
        sex1.setBounds(100, 90, 40, 26);
        Checkbox sex2 = new Checkbox("Female", sexGroup, false);
        sex2.setBounds(150, 90, 60, 26);
        Checkbox sex3 = new Checkbox("Other", sexGroup, false);
        sex3.setBounds(220, 90, 60, 26);
        Checkbox sex4 = new Checkbox("Prefer not to say", sexGroup, true);
        sex4.setBounds(280, 90, 140, 26);
        container.add(sex1);
        container.add(sex2);
        container.add(sex3);
        container.add(sex4);
        //e-mail
        JLabel mail = new JLabel("E-Mail", JLabel.CENTER);
        mail.setBackground(Color.BLACK);
        mail.setBounds(0, 120, 100, 26);
        container.add(mail);

        JTextField mailFill = new JTextField();
        mailFill.setBounds(100, 120, 300, 26);
        container.add(mailFill);
        //question
        JLabel question1 = new JLabel("Question1*", JLabel.CENTER);
        question1.setBounds(0, 150, 100, 26);
        container.add(question1);
        JLabel answer1 = new JLabel("Answer1*", JLabel.CENTER);
        answer1.setBounds(0, 180, 100, 26);
        container.add(answer1);
        JLabel question2 = new JLabel("Question2*", JLabel.CENTER);
        question2.setBounds(0, 210, 100, 26);
        container.add(question2);
        JLabel answer2 = new JLabel("Answer2*", JLabel.CENTER);
        answer2.setBounds(0, 240, 100, 26);
        container.add(answer2);

        JTextField question1Fill = new JTextField();
        question1Fill.setBounds(100, 150, 300, 26);
        container.add(question1Fill);
        JTextField question2Fill = new JTextField();
        question2Fill.setBounds(100, 210, 300, 26);
        container.add(question2Fill);
        JTextField answer1Fill = new JTextField();
        answer1Fill.setBounds(100, 180, 300, 26);
        container.add(answer1Fill);
        JTextField answer2Fill = new JTextField();
        answer2Fill.setBounds(100, 240, 300, 26);
        container.add(answer2Fill);

        //country
        JLabel country = new JLabel("Country*", JLabel.CENTER);
        country.setBackground(Color.BLACK);
        country.setBounds(0, 270, 100, 26);
        container.add(country);

        JTextField countryFill = new JTextField();
        countryFill.setBounds(100, 270, 300, 26);
        container.add(countryFill);

        //city
        JLabel city = new JLabel("City*", JLabel.CENTER);
        city.setBackground(Color.BLACK);
        city.setBounds(0, 300, 100, 26);
        container.add(city);

        JTextField cityFill = new JTextField();
        cityFill.setBounds(100, 300, 300, 26);
        container.add(cityFill);

        //introduction
        JLabel intro = new JLabel("Introduction*", JLabel.CENTER);
        intro.setBounds(0, 330, 100, 26);
        container.add(intro);

        JTextArea introFill = new JTextArea();
        introFill.setBounds(100, 330, 300, 130);
        container.add(introFill);

        //Button
        JButton clear = new JButton("CLEAR ALL");
        clear.setBounds(70, 470, 140, 30);
        container.add(clear);

        JButton submit = new JButton("REGISTER!");
        submit.setBounds(250, 470, 140, 30);
        container.add(submit);
        //
        clear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //clear all information
                userNameFill.setText("");
                passwordFill.setText("");
                ageFill.setText("");
                sex1.setState(false);
                sex2.setState(false);
                sex3.setState(false);
                sex4.setState(false);
                mailFill.setText("");
                question1Fill.setText("");
                question2Fill.setText("");
                answer2Fill.setText("");
                answer1Fill.setText("");
                countryFill.setText("");
                cityFill.setText("");
                introFill.setText("");
            }
        });
        submit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //check if the information is right
                String s1 = userNameFill.getText();
                String s2= passwordFill.getText();
                int age = Integer.parseInt(ageFill.getText());
                Checkbox sexCheckbox = sexGroup.getSelectedCheckbox();
                String sex = sexCheckbox.getLabel();
                String email = mailFill.getText();
                String country = countryFill.getText();
                String city = cityFill.getText();
                String intro = introFill.getText();
                if (!s1.matches("^[a-zA-Z0-9_@#$%^&]{6,20}") || !s2.matches("^[a-zA-Z0-9_@#$%^&]{6,20}")) {
                    //dialog
                    JDialog d1 = new JDialog(Jframe, "username/password invalid!", false);
                    //content
                    JButton b1 = new JButton("OK");
                    b1.setBounds(0, 0, 250, 100);
                    b1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            d1.dispose();
                        }
                    });
                    d1.add(b1);
                    //action
                    d1.setBounds(300, 350, 250, 100);
                    d1.setVisible(true);
                    d1.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d1.dispose();
                        }
                    });
                }else if(age < -1 || age > 121){
                    //dialog
                    JDialog d1 = new JDialog(Jframe, "age invalid!", false);
                    //content
                    JButton b1 = new JButton("OK");
                    b1.setBounds(0, 0, 250, 100);
                    b1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            d1.dispose();
                        }
                    });
                    d1.add(b1);
                    //action
                    d1.setBounds(300, 350, 250, 100);
                    d1.setVisible(true);
                    d1.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d1.dispose();
                        }
                    });
                }else if(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$")){
                    //dialog
                    JDialog d1 = new JDialog(Jframe, "email invalid!", false);
                    //content
                    JButton b1 = new JButton("Fine");
                    b1.setBounds(0, 0, 250, 100);
                    b1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            d1.dispose();
                        }
                    });
                    d1.add(b1);
                    //action
                    d1.setBounds(300, 350, 250, 100);
                    d1.setVisible(true);
                    d1.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d1.dispose();
                        }
                    });
                }
                else{
                    //valid information!
                    StreamUtils.User newUser = new StreamUtils.User(s1,s2,age,sex,email,country,city,intro);
                    //todo!!!! store the user into database
                    //dialog
                    JDialog d1 = new JDialog(Jframe, "Success!!!", false);
                    //content
                    JButton b1 = new JButton("Great!");
                    b1.setBounds(0, 0, 250, 100);
                    b1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            d1.dispose();
                            Jframe.setVisible(false);
                        }
                    });
                    d1.add(b1);
                    //action
                    d1.setBounds(300, 350, 250, 100);
                    d1.setVisible(true);
                    d1.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d1.dispose();
                        }
                    });
                }

            }
        });
        Jframe.add(container);
    }
}
