import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scheduler extends JFrame implements Runnable {
    private final String username, password;
    private final CustomTreeModel model1, model2;

    public static void main(final String[] a) {
        SwingUtilities.invokeLater(() -> {
            if (!System.getProperty("os.name").startsWith("Mac")) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (final Exception ignored) {
                }
            }

            final Scheduler g = new Scheduler(null, null);
            g.run();
        });
    }

    @Override
    public void run() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    public Scheduler(final String username, final String password) {
        this.username = username;
        this.password = password;

        setLayout(new GridBagLayout());
        final JPanel calendar = new JPanel();
        calendar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        for (int i = 0; i < 5; ++i) calendar.add(getWeekday());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(calendar, gbc);
        final JPanel information = new JPanel();
        information.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(information, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        information.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(SwingConstants.LEADING);
        label1.setText("Class Information");
        panel1.add(label1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        information.add(panel2, gbc);
        final JPanel classSelection = new JPanel();
        classSelection.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(classSelection, gbc);
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setText("Available Classes");
        classSelection.add(label2, BorderLayout.NORTH);
        final JTree treeSelect = new JTree(model1 = new CustomTreeModel());
        classSelection.add(treeify(treeSelect), BorderLayout.CENTER);
        final JPanel classSchedule = new JPanel();
        classSchedule.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(classSchedule, gbc);
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setText("Selected Classes");
        classSchedule.add(label3, BorderLayout.NORTH);
        final JTree treeSelected = new JTree(model2 = new CustomTreeModel());
        classSchedule.add(treeify(treeSelected), BorderLayout.CENTER);
        final JPanel exchangePanel = new JPanel();
        exchangePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(exchangePanel, gbc);
        final JButton buttonRemove = new JButton();
        buttonRemove.setText("<< Remove");
        gbc = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(buttonRemove, gbc);
        final JButton buttonAdd = new JButton();
        buttonAdd.setText("Add >>");
        gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(buttonAdd, gbc);
        final JTextField sectionField = new JTextField();
        gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(sectionField, gbc);
        final JButton lookupButton = new JButton();
        lookupButton.setText("Retrieve class[es]");
        gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(lookupButton, gbc);
        final JPanel exchangeSpacer = new JPanel();
        gbc = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(exchangeSpacer, gbc);

        setTitle("Scheduler");
    }

    private JPanel getWeekday() {
        final JPanel p = new JPanel();
        final Dimension d = new Dimension(100, 200);
        p.setSize(d);
        p.setMinimumSize(d);
        p.setPreferredSize(d);
        return p;
    }

    private JTree treeify(final JTree tree) {
        tree.setRootVisible(false);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return tree;
    }

    private class CustomTreeModel implements TreeModel {
        private final Object root = new Object();
        private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

        @Override
        public final Object getRoot() {
            return root;
        }

        @Override
        public Object getChild(final Object parent, final int index) {
            return null;
        }

        @Override
        public int getChildCount(final Object parent) {
            return 0;
        }

        @Override
        public boolean isLeaf(final Object node) {
            return false;
        }

        @Override
        public void valueForPathChanged(final TreePath path, final Object newValue) {
        }

        @Override
        public int getIndexOfChild(final Object parent, final Object child) {
            return 0;
        }

        @Override
        public final void addTreeModelListener(final TreeModelListener l) {
            treeModelListeners.add(l);
        }

        @Override
        public final void removeTreeModelListener(final TreeModelListener l) {
            treeModelListeners.remove(l);
        }

        public void refresh() {
            fireTreeStructureChanged();
        }

        private void fireTreeStructureChanged() {
            final TreeModelEvent e = new TreeModelEvent(this, new Object[]{root});
            for (final TreeModelListener tml : treeModelListeners) {
                tml.treeStructureChanged(e);
            }
        }
    }
}
