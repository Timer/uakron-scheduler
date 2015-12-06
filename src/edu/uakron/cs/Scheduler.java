package edu.uakron.cs;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Scheduler extends JFrame implements Runnable {
    private final LinkedList<Map.Entry<String, List<ClassOffering>>> chooseList, choseList;
    private final ZiplineDriver driver;
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

        driver.addListener(offerings -> {
            final Map<String, List<ClassOffering>> agg = new LinkedHashMap<>();
            for (final ClassOffering o : offerings) {
                final List<ClassOffering> l;
                final String k = o.subject + ':' + o.course;
                if (agg.containsKey(k)) l = agg.get(k);
                else agg.put(k, l = new LinkedList<>());
                l.add(o);
            }
            //TODO: filter out already selected stuff
            chooseList.clear();
            chooseList.addAll(agg.entrySet().stream().collect(Collectors.toList()));
            chooseList.sort((o1, o2) -> o1.getKey().compareTo(o2.getKey()));
            model1.refresh();
        });
    }

    public Scheduler(final String username, final String password) {
        chooseList = new LinkedList<>();
        choseList = new LinkedList<>();
        driver = new ZiplineDriver(username, password);
        new Thread(driver).start();
        this.username = username;
        this.password = password;

        setLayout(new GridBagLayout());
        final JPanel calendar = new JPanel();
        calendar.setLayout(new GridLayout(1, ClassOffering.Days.values().length - 1, 5, 5));
        for (int i = 0; i < ClassOffering.Days.values().length - 1; ++i) calendar.add(getWeekday(i));
        calendar.revalidate();
        calendar.setSize(calendar.getPreferredSize());
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
        final JTree treeSelect = new JTree();
        treeSelect.setModel(model1 = new CustomTreeModel(treeSelect, chooseList));
        classSelection.add(treeify(treeSelect), BorderLayout.CENTER);
        final JPanel classSchedule = new JPanel();
        classSchedule.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(classSchedule, gbc);
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setText("Selected Classes");
        classSchedule.add(label3, BorderLayout.NORTH);
        final JTree treeSelected = new JTree();
        treeSelected.setModel(model2 = new CustomTreeModel(treeSelected, true, choseList));
        classSchedule.add(treeify(treeSelected), BorderLayout.CENTER);
        final JPanel exchangePanel = new JPanel();
        exchangePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(exchangePanel, gbc);
        final JButton buttonRemove = new JButton();
        buttonRemove.setText("<< Remove");
        buttonRemove.addActionListener(this::remove);
        gbc = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(buttonRemove, gbc);
        final JButton buttonAdd = new JButton();
        buttonAdd.setText("Add >>");
        buttonAdd.addActionListener(this::add);
        gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(buttonAdd, gbc);
        final JTextField sectionField = new JTextField();
        gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(sectionField, gbc);
        final JButton lookupButton = new JButton();
        lookupButton.setText("Retrieve class[es]");
        lookupButton.addActionListener((event) -> {
            final String section = sectionField.getText();
            sectionField.setText("");
            driver.offer(section);
        });
        gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(lookupButton, gbc);
        final JPanel exchangeSpacer = new JPanel();
        gbc = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
        exchangePanel.add(exchangeSpacer, gbc);

        setTitle("Scheduler");

        revalidate();
    }

    private Pair<LocalTime, LocalTime> getRange() {
        final Pair<LocalTime, LocalTime> p = new Pair<>();
        final List<ClassOffering> ol = new LinkedList<>();
        final List<List<ClassOffering>> l = choseList.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        l.forEach(ol::addAll);
        for (final ClassOffering o : ol) {
            for (final ClassOffering.DayPair pair : o.days) {
                try {
                    if (p.v1 == null) p.v1 = pair.start;
                    else if (pair.start.isBefore(p.v1)) p.v1 = pair.start;
                    if (p.v2 == null) p.v2 = pair.end;
                    else if (pair.end.isAfter(p.v2)) p.v2 = pair.end;
                } catch (final NullPointerException ignored) {
                }
            }
        }
        return p;
    }

    private JComponent getWeekday(final int index) {
        final JComponent p = new JComponent() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                final ClassOffering.Days day = ClassOffering.Days.values()[index];
                final Rectangle r = g.getClipBounds();
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, r.width - 1, r.height - 1);
                final List<ClassOffering> ol = new LinkedList<>();
                final List<List<ClassOffering>> l = choseList.stream().map(Map.Entry::getValue).collect(Collectors.toList());
                l.forEach(ol::addAll);
                final List<ClassOffering> od = ol.stream().filter(
                        e -> e.days.stream().map(e2 -> e2.day).collect(Collectors.toList()).contains(day)
                ).collect(Collectors.toList());

                final Pair<LocalTime, LocalTime> p = getRange();
                if (p.v1 == null || p.v2 == null) return;
                g.setFont(new Font("Arial", NORMAL, 10));
                final FontMetrics met = g.getFontMetrics();
                final long tm = ChronoUnit.MINUTES.between(p.v1, p.v2);
                for (final ClassOffering o : od) {
                    ClassOffering.DayPair pair = null;
                    for (final ClassOffering.DayPair dp : o.days) if (dp.day == day) pair = dp;
                    if (pair == null || pair.start == null || pair.end == null) continue;
                    final long sm = ChronoUnit.MINUTES.between(p.v1, pair.start), em = ChronoUnit.MINUTES.between(p.v1, pair.end);
                    final double p_start = sm / (double) tm, p_end = em / (double) tm;
                    final int d_start = (int) Math.round(p_start * (r.height - 2)), d_end = (int) Math.round(p_end * (r.height - 2));
                    g.drawRect(5, d_start, r.width - 10, d_end - d_start);
                    g.drawString(o.desc, (r.width - met.stringWidth(o.desc)) / 2, (d_start + d_end) / 2);
                }
            }
        };
        final Dimension d = new Dimension(100, 200);
        p.setPreferredSize(d);
        return p;
    }

    private JComponent treeify(final JTree tree) {
        tree.setRootVisible(false);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        final JScrollPane p = new JScrollPane(tree);
        p.setPreferredSize(new Dimension(400, 300));
        return p;
    }

    private void add(final ActionEvent e) {
        final Object o = model1.tree.getLastSelectedPathComponent();
        if (!(o instanceof OfferEncapsulator)) return;
        final OfferEncapsulator oe = (OfferEncapsulator) o;
        final TreePath p = model1.tree.getSelectionPath();
        final ClassEncapsulator ce = (ClassEncapsulator) p.getParentPath().getLastPathComponent();
        add(ce.entry, oe.offering);
    }

    private void remove(final ActionEvent e) {
        final Object o = model2.tree.getLastSelectedPathComponent();
        if (!(o instanceof OfferEncapsulator)) return;
        final OfferEncapsulator oe = (OfferEncapsulator) o;
        final TreePath p = model2.tree.getSelectionPath();
        final ClassEncapsulator ce = (ClassEncapsulator) p.getParentPath().getLastPathComponent();
        remove(ce.entry, oe.offering);
    }

    private void add(final Map.Entry<String, List<ClassOffering>> e, final ClassOffering o) {
        if (e.getValue().remove(o)) {
            final List<Map.Entry<String, List<ClassOffering>>> l = choseList.stream().filter(v -> v.getKey().equals(e.getKey())).collect(Collectors.toList());
            if (l.isEmpty()) choseList.add(new AbstractMap.SimpleEntry<>(e.getKey(), new ArrayList<ClassOffering>(1) {{
                add(o);
            }}));
            else {
                l.get(0).getValue().add(o);
            }
            model1.refresh();
            model2.refresh();
            repaint();
        }
    }

    private void remove(final Map.Entry<String, List<ClassOffering>> e, final ClassOffering o) {
        if (e.getValue().remove(o)) {
            if (e.getValue().isEmpty()) choseList.remove(e);
            final List<Map.Entry<String, List<ClassOffering>>> l = chooseList.stream().filter(v -> v.getKey().equals(e.getKey())).collect(Collectors.toList());
            if (l.isEmpty()) return;
            l.get(0).getValue().add(o);
            model1.refresh();
            model2.refresh();
            repaint();
        }
    }

    private class CustomTreeModel implements TreeModel {
        private final JTree tree;
        private final boolean expand;
        private final Object root = new Object();
        private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

        private final LinkedList<Map.Entry<String, List<ClassOffering>>> list;

        public CustomTreeModel(final JTree tree, final LinkedList<Map.Entry<String, List<ClassOffering>>> list) {
            this(tree, false, list);
        }

        public CustomTreeModel(final JTree tree, final boolean expand, final LinkedList<Map.Entry<String, List<ClassOffering>>> list) {
            this.tree = tree;
            this.expand = expand;
            this.list = list;
        }

        @Override
        public final Object getRoot() {
            return root;
        }

        @Override
        public Object getChild(final Object parent, final int index) {
            if (index >= getChildCount(parent)) return null;
            if (parent == root) {
                return new ClassEncapsulator(list.get(index));
            } else if (parent instanceof ClassEncapsulator) {
                final List<ClassOffering> o = ((ClassEncapsulator) parent).entry.getValue();
                return new OfferEncapsulator(o.get(index));
            }
            return null;
        }

        @Override
        public int getChildCount(final Object parent) {
            if (parent == root) {
                return list.size();
            } else if (parent instanceof ClassEncapsulator) {
                final List<ClassOffering> o = ((ClassEncapsulator) parent).entry.getValue();
                return o.size();
            }
            return 0;
        }

        @Override
        public boolean isLeaf(final Object node) {
            if (node == root) return getChildCount(root) < 1;
            if (node instanceof ClassEncapsulator) {
                final List<ClassOffering> o = ((ClassEncapsulator) node).entry.getValue();
                return o.isEmpty();
            }
            return true;
        }

        @Override
        public void valueForPathChanged(final TreePath path, final Object newValue) {
        }

        @Override
        public int getIndexOfChild(final Object parent, final Object child) {
            final int c = getChildCount(parent);
            for (int i = 0; i < c; ++i) if (child.equals(getChild(parent, c))) return i;
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
            if (expand) for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
        }

        private void fireTreeStructureChanged() {
            final TreeModelEvent e = new TreeModelEvent(this, new Object[]{root});
            for (final TreeModelListener tml : treeModelListeners) {
                tml.treeStructureChanged(e);
            }
        }
    }

    private final class OfferEncapsulator {
        public final ClassOffering offering;

        public OfferEncapsulator(final ClassOffering offering) {
            this.offering = offering;
        }

        private String toStr(final List<ClassOffering.DayPair> pairs) {
            String s = "";
            for (final ClassOffering.DayPair p : pairs) {
                s += p.day.key + " ";
            }
            final ClassOffering.DayPair p = pairs.size() < 1 ? null : pairs.get(0);
            if (p == null || p.start == null || p.end == null) s = s.trim();
            else {
                s += p.start.format(DateTimeFormatter.ofPattern("h:mm a"));
                s += " - ";
                s += p.end.format(DateTimeFormatter.ofPattern("h:mm a"));
            }
            return s;
        }

        @Override
        public String toString() {
            return toStr(offering.days) + " " + offering.professor;
        }
    }

    private final class ClassEncapsulator {
        public final Map.Entry<String, List<ClassOffering>> entry;

        public ClassEncapsulator(final Map.Entry<String, List<ClassOffering>> entry) {
            this.entry = entry;
        }

        @Override
        public String toString() {
            final List<ClassOffering> l = entry.getValue();
            if (l.isEmpty()) return entry.getKey();
            return entry.getKey() + " " + l.get(0).desc;
        }
    }

    private class Pair<V1, V2> {
        private V1 v1;
        private V2 v2;
    }
}
