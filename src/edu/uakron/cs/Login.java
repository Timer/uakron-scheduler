package edu.uakron.cs;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;

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
        header.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        header.setBackground(new Color(52, 152, 219));
        final JLabel headerText = new JLabel("Please enter your Zipline Login to get started");
        headerText.setFont(new Font("San Serif", NORMAL, 12));
        headerText.setForeground(Color.white);
        header.add(headerText);
        add(header, BorderLayout.NORTH);

        final JPanel details = new JPanel();
        details.setBackground(Color.white);
        final JTextField uf;
        final JPasswordField pf;
        details.setLayout(new GridLayout(2, 2, 5, 5));
        details.add(new JLabel("Username:"));
        details.add(uf = new JTextField());
        details.add(new JLabel("Password:"));
        details.add(pf = new JPasswordField());
        details.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        add(details, BorderLayout.CENTER);

        final JPanel login = new JPanel();
        login.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        login.setBackground(Color.white);
        final JButton l;
        login.add(l = new JButton("Login"));
        add(login, BorderLayout.SOUTH);

        pf.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                Login.this.setVisible(false);
                Login.this.dispose();
            });
            SwingUtilities.invokeLater(() -> {
                final Scheduler scheduler = new Scheduler(uf.getText(), new String(pf.getPassword()));
                scheduler.run();
            });
        });

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
