import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class Scheduler extends JFrame {
    public static void main(String args[]) {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (final Exception ignored) {
            }
        }
        // remove folder icon in tree
        Icon empty = new TreeIcon();
        UIManager.put("Tree.closedIcon", empty);
        UIManager.put("Tree.openIcon", empty);
        UIManager.put("Tree.collapsedIcon", empty);
        UIManager.put("Tree.expandedIcon", empty);
        UIManager.put("Tree.leafIcon", empty);
        Scheduler s = new Scheduler();

        s.setLocationRelativeTo(null);
        s.setVisible(true);
    }

    public Scheduler() {
        buildScheduler();
    }

    private void buildScheduler() {
        //build objects
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

        //frame setup
        setTitle("Akron Scheduler");
        setPreferredSize(new java.awt.Dimension(800, 730));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //set container layouts
        centerPanel.setLayout(new java.awt.GridLayout(1, 3));
        descPanel.setLayout(new java.awt.GridLayout(3, 3));

        // set default trees
        clearTree(availTree);
        clearTree(schedTree);
        createTree(availTree);


        //calendar layout
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

        //available setup
        availLabel.setFont(new Font("Tahoma", 0, 18));
        availLabel.setHorizontalAlignment(SwingConstants.CENTER);
        availScroll.setViewportView(availTree);

        // available layout
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


        //Course number setup
        courseNumField.setHorizontalAlignment(JTextField.CENTER);
        courseNumLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //IOPanel layout
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
                                .addComponent(remBtn, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(142, Short.MAX_VALUE))
        );


        //Schedule setup
        schedLabel.setFont(new Font("Tahoma", 0, 18));
        schedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        schedScroll.setViewportView(schedTree);

        //Schedule layout
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


        // lineBreak layout
        GroupLayout LBLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(LBLayout);
        LBLayout.setHorizontalGroup(
                LBLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(calendarPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(LBLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(descPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(LBLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(LBLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lineBreak))
                                .addContainerGap())
        );
        LBLayout.setVerticalGroup(
                LBLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(LBLayout.createSequentialGroup()
                                .addComponent(calendarPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lineBreak, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(descPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
        );

        //add everything
        centerPanel.add(availPanel);
        centerPanel.add(IOPanel);
        centerPanel.add(schedPanel);
        descPanel.add(descLabel);
        descPanel.add(roomLabel);
        descPanel.add(daysLabel);
        descPanel.add(subLabel);
        descPanel.add(profLabel);
        descPanel.add(openLabel);
        descPanel.add(courseLabel);
        descPanel.add(studLabel);
        descPanel.add(unitsLabel);
        pack();

        //todo: handle events
        courseNumField.addActionListener(e -> System.out.println("courseNumField handle"));
        runBtn.addActionListener(e -> System.out.println("runBtn handle"));
        addBtn.addActionListener(e -> System.out.println("addBtn handle"));
        remBtn.addActionListener(e -> System.out.println("remBtn handle"));
    }

    private void clearTree(JTree tree) {
        DefaultMutableTreeNode defaultTree = new javax.swing.tree.DefaultMutableTreeNode("");
        tree.setModel(new DefaultTreeModel(defaultTree));
        tree.setRootVisible(false);

    }

    private void createTree(JTree tree) {

        tree.setRootVisible( false );
        DefaultMutableTreeNode defaultTree = new DefaultMutableTreeNode("");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(defaultTree);
        tree.setModel( defaultTreeModel );
        DefaultMutableTreeNode parent;
        DefaultMutableTreeNode child;

        parent = (DefaultMutableTreeNode) defaultTreeModel.getRoot();
        child = new DefaultMutableTreeNode( "node 1" );
        addToTree(defaultTreeModel, parent, child);

        parent = child;
        child = new DefaultMutableTreeNode( "node 2" );
        addToTree(defaultTreeModel, parent, child);

        parent = (DefaultMutableTreeNode) defaultTreeModel.getRoot();
        child = new DefaultMutableTreeNode( "node 3" );
        addToTree(defaultTreeModel, parent, child);

        parent = child;
        child = new DefaultMutableTreeNode( "node 4" );
        addToTree( defaultTreeModel, parent, child);
    }

    private void addToTree(DefaultTreeModel treeModel, DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        treeModel.insertNodeInto(child, parent, parent.getChildCount());
        if (parent == treeModel.getRoot()) {
            treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
        }
    }

}

class TreeIcon implements Icon {
    private int SIZE = 0;
    public TreeIcon() {}
    public int getIconWidth() { return SIZE; }
    public int getIconHeight() { return SIZE; }
    public void paintIcon(Component c, Graphics g, int x, int y) {}
}
