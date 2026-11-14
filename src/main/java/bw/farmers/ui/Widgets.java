package bw.farmers.ui;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/** Small building blocks shared by the screens. */
final class Widgets {

    private Widgets() {
    }

    /** A stat tile: tinted icon, a big number, a label and an optional footnote. */
    static JPanel statTile(String iconName, Color tone, Color toneSoft, String value, String label, String note) {
        JPanel tile = Ui.card(new GridBagLayout(), 18, 20);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        JLabel icon = new JLabel(Ui.icon(iconName, 18, tone));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(UIScale.scale(36), UIScale.scale(36)));
        c.insets = new Insets(0, 0, UIScale.scale(14), 0);
        tile.add(new Ui.Surface(new BorderLayout(), toneSoft, null, 10).with(icon), c);
        c.insets = new Insets(0, 0, 0, 0);
        JLabel v = Ui.heading(value, 30);
        v.setName("value");
        tile.add(v, c);
        c.insets = new Insets(UIScale.scale(2), 0, 0, 0);
        tile.add(Ui.text(label, Ui.semibold(13.5f), Ui.INK), c);
        if (note != null) {
            JLabel n = Ui.small(note);
            n.setName("note");
            tile.add(n, c);
        }
        return tile;
    }

    /** Updates a stat tile's number and, if given, its footnote. */
    static void setTile(JPanel tile, String value, String note) {
        for (java.awt.Component c : tile.getComponents()) {
            if ("value".equals(c.getName())) {
                ((JLabel) c).setText(value);
            } else if (note != null && "note".equals(c.getName())) {
                ((JLabel) c).setText(note);
            }
        }
    }

    /** A row of equal-width components with a gap between them. */
    static JPanel row(int gap, JComponent... items) {
        JPanel row = Ui.clear(new java.awt.GridLayout(1, items.length, UIScale.scale(gap), 0));
        for (JComponent i : items) {
            row.add(i);
        }
        return row;
    }

    /** A vertical stack that fills the viewport width and caps the content at {@code maxWidth}. */
    static final class Column extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        private final JPanel inner;

        Column(int maxWidth, int padV, int padH) {
            super(new GridBagLayout());
            setOpaque(false);
            inner = Ui.clear(new GridBagLayout());
            Ui.padding(this, padV, padH, padV, padH);
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1;
            c.weighty = 1;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(new MaxWidth(inner, UIScale.scale(maxWidth)), c);
        }

        private int rows;

        /** Adds a full-width block, {@code gapAbove} px below the previous one. */
        Column block(JComponent block, int gapAbove) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = rows++;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(rows == 1 ? 0 : UIScale.scale(gapAbove), 0, 0, 0);
            inner.add(block, c);
            return this;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return UIScale.scale(24);
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return r.height - UIScale.scale(48);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() != null && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /** Lays out one child at most {@code max} wide, centred. */
    private static final class MaxWidth extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int max;

        MaxWidth(JComponent child, int max) {
            super(null);
            this.max = max;
            setOpaque(false);
            add(child);
        }

        @Override
        public void doLayout() {
            Component child = getComponent(0);
            int w = Math.min(getWidth(), max);
            child.setBounds((getWidth() - w) / 2, 0, w, getHeight());
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = getComponent(0).getPreferredSize();
            return new Dimension(Math.min(d.width, max), d.height);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(0, getPreferredSize().height);
        }
    }

    /** A white rounded card whose children are clipped to its corners, for tables. */
    static final class ClipCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int arc;

        ClipCard(LayoutManager layout, int arc) {
            super(layout);
            this.arc = arc;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        private Shape shape() {
            float a = UIScale.scale((float) arc);
            return new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, a, a);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            g.setColor(Ui.SURFACE);
            g.fill(shape());
            g.dispose();
        }

        @Override
        protected void paintChildren(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.clip(shape());
            super.paintChildren(g);
            g.dispose();
            Graphics2D b = (Graphics2D) g0.create();
            Ui.antialias(b);
            b.setColor(Ui.BORDER);
            b.setStroke(new BasicStroke(1f));
            b.draw(shape());
            b.dispose();
        }
    }

    /** A short confirmation that slides up from the bottom of the window and fades away. */
    static void toast(Component anywhere, String message) {
        JRootPane root = SwingUtilities.getRootPane(anywhere);
        if (root == null) {
            return;
        }
        JLayeredPane layers = root.getLayeredPane();
        Toast t = new Toast(message);
        Dimension d = t.getPreferredSize();
        int x = (layers.getWidth() - d.width) / 2;
        int baseY = layers.getHeight() - d.height - UIScale.scale(28);
        t.setBounds(x, baseY + UIScale.scale(10), d.width, d.height);
        layers.add(t, JLayeredPane.POPUP_LAYER);
        long start = System.currentTimeMillis();
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            long ms = System.currentTimeMillis() - start;
            float in = Math.min(1f, ms / 180f);
            float eased = 1 - (1 - in) * (1 - in) * (1 - in);
            float out = ms > 2400 ? Math.max(0f, 1 - (ms - 2400) / 300f) : 1f;
            t.alpha = Math.min(eased, out);
            t.setLocation(x, baseY + Math.round(UIScale.scale(10) * (1 - eased)));
            layers.repaint(t.getBounds());
            if (ms > 2700) {
                timer.stop();
                layers.remove(t);
                layers.repaint();
            }
        });
        timer.start();
    }

    private static final class Toast extends JPanel {
        private static final long serialVersionUID = 1L;
        float alpha;

        Toast(String message) {
            super(new BorderLayout());
            setOpaque(false);
            JLabel l = Ui.text(message, Ui.semibold(14), Ui.CREAM);
            l.setIcon(Ui.icon("check-circle", 18, new Color(0x9FD3AE)));
            l.setIconTextGap(UIScale.scale(10));
            add(l);
            Ui.padding(this, 12, 18, 12, 20);
        }

        @Override
        public void paint(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g);
            g.dispose();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            float a = getHeight();
            g.setColor(new Color(0x1D2B22));
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), a, a));
            g.dispose();
        }
    }

    /** A thin horizontal rule with a centred caption, like "or". */
    static JComponent divider(String caption) {
        JPanel p = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        p.add(rule(), c);
        c.weightx = 0;
        c.insets = new Insets(0, UIScale.scale(12), 0, UIScale.scale(12));
        p.add(Ui.small(caption), c);
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(rule(), c);
        return p;
    }

    static JComponent rule() {
        JPanel r = new JPanel();
        r.setBackground(Ui.BORDER);
        r.setPreferredSize(new Dimension(1, 1));
        return r;
    }

    static Font numberFont(float px) {
        return Ui.display(px);
    }
}
