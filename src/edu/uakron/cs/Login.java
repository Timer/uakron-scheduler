package edu.uakron.cs;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

public class Login extends JFrame {
    public static void main(final String[] a) {
        SwingUtilities.invokeLater(() -> {
            if (!System.getProperty("os.name").startsWith("Mac")) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (final Exception ignored) {
                }
            }

            final Login g = new Login();

            g.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            g.pack();
            g.setLocationRelativeTo(g.getParent());
            g.setVisible(true);
        });
    }

    public Login() {
        setLayout(new BorderLayout());
        final JPanel header = new JPanel();
        header.setBackground(new Color(0, 45, 98));
        final JLabel headerText = new JLabel("Please enter your Zipline Login to get started");
        headerText.setForeground(new Color(235, 235, 235));
        header.add(headerText);
        add(header, BorderLayout.NORTH);

        final JPanel details = new JPanel();
        final JTextField uf;
        final JPasswordField pf;
        details.setLayout(new GridLayout(2, 2));
        details.add(new JLabel("Username:"));
        details.add(uf = new JTextField());
        details.add(new JLabel("Password:"));
        details.add(pf = new JPasswordField());
        details.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        add(details, BorderLayout.CENTER);

        final JPanel login = new JPanel();
        final JButton l;
        login.add(l = new JButton("Login"));
        add(login, BorderLayout.SOUTH);

        l.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                Login.this.setVisible(false);
                Login.this.dispose();
            });
            SwingUtilities.invokeLater(() -> {
                final Scheduler scheduler = new Scheduler(uf.getText(), new String(pf.getPassword()));
                scheduler.run();
            });
        });

        setTitle("Welcome");
    }
}
