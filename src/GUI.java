import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    public static void main(final String[] a) {
        SwingUtilities.invokeLater(() -> {
            if (!System.getProperty("os.name").startsWith("Mac")) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (final Exception ignored) {
                }
            }

            final GUI g = new GUI();

            g.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            g.pack();
            g.setLocationRelativeTo(g.getParent());
            g.setVisible(true);
        });
    }

    public GUI() {
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
            final Thread t = new Thread(() -> {
                final Main m = new Main(uf.getText(), new String(pf.getPassword()));
                m.run();
            });
            t.setDaemon(false);
            t.start();
            SwingUtilities.invokeLater(() -> {
                GUI.this.setVisible(false);
                GUI.this.dispose();
            });
        });

        setTitle("Welcome");
    }
}
