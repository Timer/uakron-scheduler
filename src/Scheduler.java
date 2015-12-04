import javax.swing.*;
import java.awt.*;

public class Scheduler extends JFrame {
    public static void main(String args[]) {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (final Exception ignored) {
            }
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Scheduler().setVisible(true);
            }
        });
    }

    public Scheduler() {
        buildScheduler();
    }

    private void buildScheduler() {

        //top calendar
        JPanel calendarPanel = new JPanel();
        // center container
        JPanel centerPanel = new JPanel();
        //available classes
        JPanel availPanel = new JPanel();
        JLabel availLabel = new JLabel("Available");
        JScrollPane availScroll = new JScrollPane();
        JTree availTree = new JTree();
        //user controls
        JPanel IOPanel = new JPanel();
        JLabel courseNumLabel = new JLabel("Course Number:");
        JTextField courseNumField = new JTextField();
        JButton runBtn = new JButton("Run");
        JButton addBtn = new JButton("Add >>");
        JButton remBtn = new JButton("<< Remove");
        //scheduled classes
        JPanel schedPanel = new JPanel();
        JLabel schedLabel = new JLabel("Scheduled");
        JScrollPane schedScroll = new JScrollPane();
        JTree schedTree = new JTree();
        //bottom description
        JSeparator lineBreak = new JSeparator();
        JPanel descPanel = new JPanel();
        JLabel descLabel = new JLabel("Description");
        JLabel roomLabel = new JLabel("Room");
        JLabel daysLabel = new JLabel("Days");
        JLabel subLabel = new JLabel("Subject");
        JLabel profLabel = new JLabel("Professor");
        JLabel openLabel = new JLabel("Open");
        JLabel courseLabel = new JLabel("Course");
        JLabel studLabel = new JLabel("Students");
        JLabel unitsLabel = new JLabel("Units");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Akron Scheduler");
        setPreferredSize(new java.awt.Dimension(800, 730));

        GroupLayout calendarPanelLayout = new GroupLayout(calendarPanel);
        calendarPanel.setLayout(calendarPanelLayout);
        calendarPanelLayout.setHorizontalGroup(
                calendarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        calendarPanelLayout.setVerticalGroup(
                calendarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 264, Short.MAX_VALUE)
        );

        centerPanel.setLayout(new java.awt.GridLayout(1, 5));
        descPanel.setLayout(new java.awt.GridLayout(3, 3));
        descPanel.add(descLabel);
        descPanel.add(roomLabel);
        descPanel.add(daysLabel);
        descPanel.add(subLabel);
        descPanel.add(profLabel);
        descPanel.add(openLabel);
        descPanel.add(courseLabel);
        descPanel.add(studLabel);
        descPanel.add(unitsLabel);

        availLabel.setFont(new Font("Tahoma", 0, 18));
        availLabel.setHorizontalAlignment(SwingConstants.CENTER);

        availScroll.setViewportView(availTree);

        GroupLayout availPanelLayout = new GroupLayout(availPanel);
        availPanel.setLayout(availPanelLayout);
        availPanelLayout.setHorizontalGroup(
                availPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(availScroll)
                        .addGroup(GroupLayout.Alignment.TRAILING, availPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(availLabel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                .addContainerGap())
        );
        availPanelLayout.setVerticalGroup(
                availPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(availPanelLayout.createSequentialGroup()
                                .addComponent(availLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(availScroll, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );

        centerPanel.add(availPanel);


        //todo handle
        runBtn.addActionListener(e1 -> System.out.println("runBtn handle"));
        addBtn.addActionListener(e -> System.out.println("addBtn handle"));
        remBtn.addActionListener(e -> System.out.println("remBtn handle"));

        courseNumField.setHorizontalAlignment(JTextField.CENTER);
        courseNumField.addActionListener(e -> System.out.println("courseNumField handle"));

        courseNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        courseNumLabel.setText("Course Number: ");

        GroupLayout IOPanelLayout = new GroupLayout(IOPanel);
        IOPanel.setLayout(IOPanelLayout);
        IOPanelLayout.setHorizontalGroup(
                IOPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(IOPanelLayout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addGroup(IOPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(addBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(remBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, IOPanelLayout.createSequentialGroup()
                                .addContainerGap(34, Short.MAX_VALUE)
                                .addGroup(IOPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(courseNumLabel)
                                        .addGroup(IOPanelLayout.createSequentialGroup()
                                                .addComponent(courseNumField, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(runBtn, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)))
                                .addGap(35, 35, 35))
        );
        IOPanelLayout.setVerticalGroup(
                IOPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(IOPanelLayout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(courseNumLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(IOPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(courseNumField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(runBtn, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                .addGap(48, 48, 48)
                                .addComponent(addBtn, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(remBtn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(142, Short.MAX_VALUE))
        );

        centerPanel.add(IOPanel);

        schedLabel.setFont(new Font("Tahoma", 0, 18));
        schedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        schedScroll.setViewportView(schedTree);

        GroupLayout schedPanelLayout = new GroupLayout(schedPanel);
        schedPanel.setLayout(schedPanelLayout);
        schedPanelLayout.setHorizontalGroup(
                schedPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(schedScroll)
                        .addGroup(GroupLayout.Alignment.TRAILING, schedPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(schedLabel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                .addContainerGap())
        );
        schedPanelLayout.setVerticalGroup(
                schedPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(schedPanelLayout.createSequentialGroup()
                                .addComponent(schedLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(schedScroll, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );

        centerPanel.add(schedPanel);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(calendarPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(descPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lineBreak))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(calendarPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lineBreak, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(descPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
        );

        pack();
    }
}
