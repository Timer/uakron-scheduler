import javax.swing.*;
import java.awt.*;

public class Scheduler extends JFrame {

    public Scheduler() {
        int width = getWidth();
        int height = getHeight();
        String test = "class list...";

        setTitle("UAkron Scheduler");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Build objects
        JPanel calendarPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();
        JPanel centerIO = new JPanel();

        JTextArea calendarTemp = new JTextArea();
        JTextArea descTemp = new JTextArea();
        JTextArea availList = new JTextArea();
        JTextArea addedList = new JTextArea();

        //JTextField subjectNum = new JTextField(); //user specifies subject (i.e. 3460)
        JButton btnAdd = new JButton("  Add >>  ");
        JButton btnRemove = new JButton("<< Remove");

        // set objects
        calendarTemp.append("calendar placeholder");
        descTemp.append("desc placeholder");
        calendarPanel.add(calendarTemp);
        availList.append(test);
        addedList.append(test);

        // add objects to panels
        centerIO.add(btnAdd);
        centerIO.add(btnRemove);
        centerIO.setLayout(new BoxLayout(centerIO, BoxLayout.Y_AXIS));
        centerPanel.add(availList);
        centerPanel.add(centerIO);
        centerPanel.add(addedList);
        centerPanel.setLayout(new GridLayout(1, 3));
        southPanel.add(descTemp);

        // add panels
        add(calendarPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ignored) {
        }
        setVisible(true);
    }


}
