package org.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

import org.Setting.Network_setting.Network_Setting;
import org.client.tools.User;

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
        Jframe.setBounds(x - 200, y - 370, 500, 720);
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
        /*
        An old version of notice, no longer used
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

        backgroundPanel.add(notice);
        */

        // Add the animated background
        AnimatedBackgroundPanel2 backgroundPanel = new AnimatedBackgroundPanel2();
        backgroundPanel.setLayout(null);
        Jframe.setContentPane(backgroundPanel);

        // Notice
        JTextArea notice = new JTextArea();
        notice.setEditable(false);
        notice.setBounds(10, 0, 480, 115);
        notice.setLineWrap(true);
        notice.setWrapStyleWord(true);
        notice.setText("""
                Notice:
                1. The length of username/password should be at least 6 and at most 20.
                2. Username/password should only contain numbers, letters and '@#$%^&_'.
                3. The items with '*' are not required.
                4. If you prefer not to say your age, fill out -1.
                5. Question and answer are used to recover your password. But the function is not available now. Stay tuned.
                """);
        Font font = new Font("Times New Roman", Font.PLAIN, 13);
        notice.setFont(font);

        backgroundPanel.add(notice);

        //information fill-up
        Container container = new Container();
        container.setBounds(20, 130, 460, 540);
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

        // Confirm password
        JLabel confirmPassword = new JLabel("confirm passwd", JLabel.CENTER);
        confirmPassword.setBackground(Color.DARK_GRAY);
        confirmPassword.setBounds(0, 60, 100, 26);
        container.add(confirmPassword);

        JTextField confirmPasswordFill = new JPasswordField();
        confirmPasswordFill.setBounds(100, 60, 300, 26);
        container.add(confirmPasswordFill);

        //age
        JLabel age = new JLabel("age(-1~120)", JLabel.CENTER);
        age.setBounds(0, 90, 100, 26);
        container.add(age);

        JTextField ageFill = new JTextField();
        ageFill.setBounds(100, 90, 300, 26);
        container.add(ageFill);
        //sex
        JLabel sex = new JLabel("Sex", JLabel.CENTER);
        sex.setBounds(0, 120, 100, 26);
        container.add(sex);

        CheckboxGroup sexGroup = new CheckboxGroup();
        Checkbox sex1 = new Checkbox("Male", sexGroup, false);
        sex1.setBounds(100, 120, 40, 26);
        Checkbox sex2 = new Checkbox("Female", sexGroup, false);
        sex2.setBounds(150, 120, 60, 26);
        Checkbox sex3 = new Checkbox("Other", sexGroup, false);
        sex3.setBounds(220, 120, 60, 26);
        Checkbox sex4 = new Checkbox("Prefer not to say", sexGroup, true);
        sex4.setBounds(280, 120, 140, 26);
        container.add(sex1);
        container.add(sex2);
        container.add(sex3);
        container.add(sex4);
        //e-mail
        JLabel mail = new JLabel("E-Mail", JLabel.CENTER);
        mail.setBackground(Color.BLACK);
        mail.setBounds(0, 150, 100, 26);
        container.add(mail);

        JTextField mailFill = new JTextField();
        mailFill.setBounds(100, 150, 300, 26);
        container.add(mailFill);
        //question
        JLabel question1 = new JLabel("Question1*", JLabel.CENTER);
        question1.setBounds(0, 180, 100, 26);
        container.add(question1);
        JLabel answer1 = new JLabel("Answer1*", JLabel.CENTER);
        answer1.setBounds(0, 210, 100, 26);
        container.add(answer1);
        JLabel question2 = new JLabel("Question2*", JLabel.CENTER);
        question2.setBounds(0, 240, 100, 26);
        container.add(question2);
        JLabel answer2 = new JLabel("Answer2*", JLabel.CENTER);
        answer2.setBounds(0, 270, 100, 26);
        container.add(answer2);

        JTextField question1Fill = new JTextField();
        question1Fill.setBounds(100, 180, 300, 26);
        container.add(question1Fill);
        JTextField question2Fill = new JTextField();
        question2Fill.setBounds(100, 240, 300, 26);
        container.add(question2Fill);
        JTextField answer1Fill = new JTextField();
        answer1Fill.setBounds(100, 210, 300, 26);
        container.add(answer1Fill);
        JTextField answer2Fill = new JTextField();
        answer2Fill.setBounds(100, 270, 300, 26);
        container.add(answer2Fill);

        //country
        JLabel country = new JLabel("Country*", JLabel.CENTER);
        country.setBackground(Color.BLACK);
        country.setBounds(0, 300, 100, 26);
        container.add(country);

        JTextField countryFill = new JTextField();
        countryFill.setBounds(100, 300, 300, 26);
        container.add(countryFill);

        //city
        JLabel city = new JLabel("City*", JLabel.CENTER);
        city.setBackground(Color.BLACK);
        city.setBounds(0, 330, 100, 26);
        container.add(city);

        JTextField cityFill = new JTextField();
        cityFill.setBounds(100, 330, 300, 26);
        container.add(cityFill);

        //introduction
        JLabel intro = new JLabel("Introduction*", JLabel.CENTER);
        intro.setBounds(0, 360, 100, 26);
        container.add(intro);

        JTextArea introFill = new JTextArea();
        introFill.setBounds(100, 360, 300, 130);
        introFill.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        container.add(introFill);

        //Button
        JButton clear = new JButton("CLEAR ALL");
        clear.setBounds(70, 500, 140, 30);
        container.add(clear);

        JButton submit = new JButton("REGISTER!");
        submit.setBounds(250, 500, 140, 30);
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
                String s2 = passwordFill.getText();
                String ageString = ageFill.getText();
                if(ageString.equals("")){
                    System.out.println("Please enter your age!");
                    ErrorDialog ed = new ErrorDialog(Jframe, "Please enter your age!");
                    ed.setVisible(true);
                    return;
                }
                int age = Integer.parseInt(ageString);
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
                    JButton b1 = new JButton("Fine");
                    b1.setBounds(0, 0, 251, 100);
                    b1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            d1.dispose();
                        }
                    });
                    d1.add(b1);
                    //action
                    d1.setBounds(300, 350, 251, 100);
                    d1.setVisible(true);
                    d1.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d1.dispose();
                        }
                    });
                } else if (age < -1 || age > 121) {
                    //dialog
                    JDialog d1 = new JDialog(Jframe, "age invalid!", false);
                    //content
                    JButton b1 = new JButton("OK");
                    b1.setBounds(0, 0, 251, 100);
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
                } else if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$")) {
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
                } else {
                    //valid information!
                    User newUser = new User(s1, s2, age, sex, email, country, city, intro);
                    Connection connection;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Network_Setting ns = new Network_Setting();
                        System.out.println("Network Setting: " + ns);
                        connection = DriverManager.getConnection(ns.getPersonalized_setting(), ns.getPersonalized_username(), ns.getPersonalized_password());
                        String sql = "select * from users where username = ? or email = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setObject(1, s1);
                        statement.setObject(2, email);
                        ResultSet result = statement.executeQuery();
                        if (result.next()) {
                            //fail
                            ErrorDialog ed = new ErrorDialog(Jframe, "This username or email has been used!");
                            ed.setVisible(true);
                        } else {
                            // Valid!
                            sql = "insert into users (username, password, age, sex, email, country, city, introduction) " +
                                    "values(?,?,?,?,?,?,?,?)";
                            PreparedStatement statement1 = connection.prepareStatement(sql);
                            try {
                                statement1.setObject(1, s1);
                                statement1.setObject(2, s2);
                                statement1.setObject(3, age);
                                statement1.setObject(4, sex);
                                statement1.setObject(5, email);
                                statement1.setObject(6, country);
                                statement1.setObject(7, city);
                                statement1.setObject(8, intro);
                            } catch (SQLException ee) {
                                ee.printStackTrace();
                            }

                            int resultUpdate = statement1.executeUpdate();
                            System.out.println(resultUpdate);
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
                    } catch (SQLException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        notice.setOpaque(false);
        userNameFill.setOpaque(false);
        passwordFill.setOpaque(false);
        confirmPasswordFill.setOpaque(false);
        ageFill.setOpaque(false);
        mailFill.setOpaque(false);
        question1Fill.setOpaque(false);
        question2Fill.setOpaque(false);
        answer1Fill.setOpaque(false);
        answer2Fill.setOpaque(false);
        countryFill.setOpaque(false);
        cityFill.setOpaque(false);
        introFill.setOpaque(false);
        backgroundPanel.add(container);
    }

    public static class ErrorDialog extends JDialog {
        public ErrorDialog(JFrame parent, String message) {
            super(parent, "Error", true);
            setBounds(parent.getX() + 200, parent.getY() + 200, 300, 150);
            setLayout(new BorderLayout());

            JLabel errorMessage = new JLabel(message, SwingConstants.CENTER);
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> dispose());

            add(errorMessage, BorderLayout.CENTER);
            add(okButton, BorderLayout.SOUTH);
        }
    }

    static class AnimatedBackgroundPanel2 extends JPanel {
        private static final int BALL_COUNT = 40;
        private static final int MAX_SPEED = 4;

        private java.util.List<Ball> balls = new ArrayList<>();

        public AnimatedBackgroundPanel2() {
            for (int i = 0; i < BALL_COUNT; i++) {
                balls.add(new Ball(getRandomNumber(0, getWidth()), getRandomNumber(0, getHeight())));
            }

            Timer timer = new Timer(20, e -> moveBallsAndUpdateUI());
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Ball ball : balls) {
                ball.paint(g);
            }
        }

        private void moveBallsAndUpdateUI() {
            for (Ball ball : balls) {
                ball.move();
                ball.checkBounds(getWidth(), getHeight());
            }
            repaint();
        }

        private int getRandomNumber(int min, int max) {
            return (int) (Math.random() * (max - min + 1) + min);
        }

        class Ball {
            int x;
            int y;
            int speedX;
            int speedY;
            int size;
            Color ballColor;

            public Ball(int x, int y) {
                this.x = x;
                this.y = y;
                this.speedX = getRandomNumber(-MAX_SPEED, MAX_SPEED);
                this.speedY = getRandomNumber(-MAX_SPEED, MAX_SPEED);
                this.size = getRandomNumber(10, 20);
                this.ballColor = new Color(getRandomNumber(0, 255), getRandomNumber(0, 255), getRandomNumber(0, 255), 128);
            }

            public void move() {
                x += speedX;
                y += speedY;
            }

            public void checkBounds(int width, int height) {
                if (x < 0 || x + size > width) {
                    speedX = -speedX;
                }

                if (y < 0 || y + size > height) {
                    speedY = -speedY;
                }
            }

            public void paint(Graphics g) {
                g.setColor(ballColor);
                g.fillOval(x, y, size, size);
            }
        }
    }
}
