import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private static JTextField userField;
    private static JPasswordField passField;
    private static String username, password;

    public void buildLogin() {
        setTitle("Login");
        setSize(400, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /// Build objects
        userField = new JTextField();
        passField = new JPasswordField();
        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();
        Border border = centerPanel.getBorder();
        Border margin = new EmptyBorder(35, 35, 35, 35);
        JLabel headingLabel = new JLabel("Enter Your UAkron Login");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JButton btnLogin = new JButton("  Login  ");

        // set objects
        headingLabel.setForeground(new Color(235, 235, 235));
        northPanel.setBackground(new Color(0, 45, 98));
        centerPanel.setBorder(new CompoundBorder(border, margin));
        btnLogin.addActionListener(new Login());

        // add objects to panels
        northPanel.add(headingLabel);
        centerPanel.add(userLabel);
        centerPanel.add(userField);
        centerPanel.add(passLabel);
        centerPanel.add(passField);
        centerPanel.setLayout(new GridLayout(2, 2));
        southPanel.add(btnLogin);

        // add panels
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ignored) {
        }
        setVisible(true);
    }

    public String getUser() {
        return username;
    }

    public String getPass() {
        return password;
    }

    static class Login implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (new GUI().checkSpaces(userField.getText().trim())) {
                    username = userField.getText();
                    password = String.valueOf(passField.getPassword());
                    // run main after button click
                    final Main m = new Main();
                    m.run();
                }

            } catch (Exception validateError) {
                JOptionPane.showMessageDialog(null, "Error in Class:GUI Method:Login",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private boolean checkSpaces(String getUser) {
        String username;
        try {
            username = getUser;
            if (username.contains(" ")) {
                JOptionPane.showMessageDialog(null, "Invalid input form: USERNAME includes 'space'",
                        "Error", JOptionPane.ERROR_MESSAGE);
                userField.setText("");
                passField.setText("");
                return false;
            } else return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in Class:GUI Method:checkSpaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        GUI login = new GUI();
        login.buildLogin();
        //new Scheduler();
    }
}
