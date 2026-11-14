package bw.farmers.ui;

import bw.farmers.model.Status;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Hand-drawn charts: no chart library, so they match the rest of the app exactly. */
final class Charts {

    private static final Status[] ORDER = {Status.APPROVED, Status.PENDING, Status.DECLINED};
    private static final Color TRACK = new Color(0xF1EADD);

    private Charts() {
    }

    private static float s(float v) {
        return UIScale.scale(v);
    }

    /** Horizontal stacked bars: farmers per district, split by status. */
    static final class DistrictBars extends JComponent {
        private static final long serialVersionUID = 1L;
        private final transient List<Map.Entry<String, int[]>> rows = new ArrayList<>();

        DistrictBars() {
            setFont(Ui.font(Font.PLAIN, 13));
        }

        /** {@code counts} maps district to {approved, pending, declined}. */
        void setData(Map<String, int[]> counts) {
            rows.clear();
            counts.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(sum(b.getValue()), sum(a.getValue())))
                    .forEach(rows::add);
            revalidate();
            repaint();
        }

        private static int sum(int[] v) {
            return v[0] + v[1] + v[2];
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Math.round(s(420)), Math.round(s(34) + rows.size() * s(30)));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            float labelW = s(108);
            float valueW = s(40);
            float barX = labelW;
            float barW = getWidth() - labelW - valueW;
            int max = rows.stream().mapToInt(e -> sum(e.getValue())).max().orElse(1);

            // legend
            float lx = 0;
            g.setFont(Ui.font(Font.PLAIN, 12.5f));
            FontMetrics lfm = g.getFontMetrics();
            for (Status st : ORDER) {
                g.setColor(Ui.statusSwatch(st));
                g.fill(new Ellipse2D.Float(lx, s(6), s(9), s(9)));
                g.setColor(Ui.MUTED);
                g.drawString(st.label(), lx + s(14), s(4) + lfm.getAscent());
                lx += s(14) + lfm.stringWidth(st.label()) + s(18);
            }

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            float y = s(34);
            float bh = s(12);
            for (Map.Entry<String, int[]> e : rows) {
                float cy = y + s(15);
                g.setColor(Ui.INK);
                g.drawString(e.getKey(), 0, cy + fm.getAscent() / 2f - s(2));
                RoundRectangle2D track = new RoundRectangle2D.Float(barX, cy - bh / 2, barW, bh, bh, bh);
                g.setColor(TRACK);
                g.fill(track);
                java.awt.Shape old = g.getClip();
                float total = sum(e.getValue());
                float filled = barW * total / max;
                g.clip(new RoundRectangle2D.Float(barX, cy - bh / 2, filled, bh, bh, bh));
                float x = barX;
                for (int i = 0; i < 3; i++) {
                    float segW = filled * e.getValue()[i] / Math.max(1f, total);
                    g.setColor(Ui.statusSwatch(ORDER[i]));
                    g.fill(new java.awt.geom.Rectangle2D.Float(x, cy - bh / 2, segW + 0.5f, bh));
                    x += segW;
                }
                g.setClip(old);
                g.setColor(Ui.INK);
                g.setFont(Ui.semibold(13));
                String v = String.valueOf((int) total);
                g.drawString(v, getWidth() - g.getFontMetrics().stringWidth(v), cy + fm.getAscent() / 2f - s(2));
                g.setFont(getFont());
                y += s(30);
            }
            g.dispose();
        }
    }

    /** A ring split by status, with the total in the middle. */
    static final class StatusDonut extends JComponent {
        private static final long serialVersionUID = 1L;
        private final int[] counts = new int[3];

        void setData(int approved, int pending, int declined) {
            counts[0] = approved;
            counts[1] = pending;
            counts[2] = declined;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Math.round(s(240)), Math.round(s(268)));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            int total = counts[0] + counts[1] + counts[2];
            float d = s(150);
            float th = s(20);
            float x = (getWidth() - d) / 2f;
            float y = s(4);
            g.setStroke(new BasicStroke(th, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g.setColor(TRACK);
            g.draw(new Ellipse2D.Float(x + th / 2, y + th / 2, d - th, d - th));
            if (total > 0) {
                double start = 90;
                double gap = 2.2;
                for (int i = 0; i < 3; i++) {
                    if (counts[i] == 0) {
                        continue;
                    }
                    double extent = 360.0 * counts[i] / total;
                    g.setColor(Ui.statusSwatch(ORDER[i]));
                    double drawn = extent >= 360 ? 360 : Math.max(0.5, extent - gap);
                    g.draw(new Arc2D.Float(x + th / 2, y + th / 2, d - th, d - th, (float) start,
                            (float) -drawn, Arc2D.OPEN));
                    start -= extent;
                }
            }
            g.setColor(Ui.INK);
            g.setFont(Ui.display(30));
            FontMetrics fm = g.getFontMetrics();
            String t = String.valueOf(total);
            g.drawString(t, x + (d - fm.stringWidth(t)) / 2, y + d / 2 + s(4));
            g.setFont(Ui.font(Font.PLAIN, 12.5f));
            FontMetrics sfm = g.getFontMetrics();
            g.setColor(Ui.MUTED);
            String sub = total == 1 ? "farmer" : "farmers";
            g.drawString(sub, x + (d - sfm.stringWidth(sub)) / 2, y + d / 2 + s(22));

            // legend
            float ly = y + d + s(24);
            for (int i = 0; i < 3; i++) {
                float row = ly + i * s(26);
                g.setColor(Ui.statusSwatch(ORDER[i]));
                g.fill(new Ellipse2D.Float(s(8), row - s(9), s(10), s(10)));
                g.setColor(Ui.INK);
                g.setFont(Ui.font(Font.PLAIN, 13.5f));
                g.drawString(ORDER[i].label(), s(26), row);
                String v = counts[i] + (total > 0 ? String.format(Locale.ROOT, "  %d%%",
                        Math.round(100.0 * counts[i] / total)) : "");
                g.setFont(Ui.semibold(13.5f));
                g.drawString(v, getWidth() - s(8) - g.getFontMetrics().stringWidth(v), row);
            }
            g.dispose();
        }
    }

    /** Submitted, under review, decision: where a registration is in its journey. */
    static final class Stepper extends JComponent {
        private static final long serialVersionUID = 1L;
        private Status status = Status.PENDING;

        void setStatus(Status s) {
            status = s;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Math.round(s(360)), Math.round(s(66)));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            String[] labels = {"Submitted", "Under review",
                    status == Status.PENDING ? "Decision" : status.label()};
            // 0 = to do, 1 = current, 2 = done, 3 = declined
            int[] state = switch (status) {
                case PENDING -> new int[]{2, 1, 0};
                case APPROVED -> new int[]{2, 2, 2};
                case DECLINED -> new int[]{2, 2, 3};
            };
            float r = s(14);
            float pad = s(46);
            float span = (getWidth() - 2 * pad) / 2f;
            float cy = r + s(2);
            for (int i = 0; i < 2; i++) {
                float x0 = pad + span * i + r + s(4);
                float x1 = pad + span * (i + 1) - r - s(4);
                g.setColor(state[i + 1] >= 2 ? Ui.FOREST : Ui.BORDER);
                g.setStroke(new BasicStroke(s(2.5f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Float(x0, cy, x1, cy));
            }
            for (int i = 0; i < 3; i++) {
                float cx = pad + span * i;
                Ellipse2D circle = new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r);
                switch (state[i]) {
                    case 2 -> {
                        g.setColor(Ui.FOREST);
                        g.fill(circle);
                        Ui.icon("check", 15, Color.WHITE).paintIcon(this, g, Math.round(cx - s(7.5f)),
                                Math.round(cy - s(7.5f)));
                    }
                    case 3 -> {
                        g.setColor(Ui.CLAY);
                        g.fill(circle);
                        Ui.icon("x", 15, Color.WHITE).paintIcon(this, g, Math.round(cx - s(7.5f)),
                                Math.round(cy - s(7.5f)));
                    }
                    case 1 -> {
                        g.setColor(Ui.GOLD_SOFT);
                        g.fill(circle);
                        g.setColor(Ui.GOLD);
                        g.setStroke(new BasicStroke(s(2.5f)));
                        g.draw(new Ellipse2D.Float(cx - r + s(1.25f), cy - r + s(1.25f), 2 * r - s(2.5f),
                                2 * r - s(2.5f)));
                        float dd = s(9);
                        g.fill(new Ellipse2D.Float(cx - dd / 2, cy - dd / 2, dd, dd));
                    }
                    default -> {
                        g.setColor(Ui.SURFACE);
                        g.fill(circle);
                        g.setColor(Ui.BORDER);
                        g.setStroke(new BasicStroke(s(2)));
                        g.draw(new Ellipse2D.Float(cx - r + s(1), cy - r + s(1), 2 * r - s(2), 2 * r - s(2)));
                    }
                }
                g.setFont(state[i] == 0 ? Ui.font(Font.PLAIN, 13) : Ui.semibold(13));
                g.setColor(state[i] == 0 ? Ui.MUTED : state[i] == 3 ? Ui.CLAY : Ui.INK);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(labels[i], cx - fm.stringWidth(labels[i]) / 2f, cy + r + s(10) + fm.getAscent());
            }
            g.dispose();
        }
    }

    /** A slim progress bar, for "how much of the farm is used". */
    static final class UsageBar extends JComponent {
        private static final long serialVersionUID = 1L;
        private double fraction;

        void setFraction(double f) {
            fraction = Math.max(0, Math.min(1, f));
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Math.round(s(120)), Math.round(s(8)));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            float h = getHeight();
            g.setColor(TRACK);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            g.setColor(Ui.GOLD);
            g.fill(new RoundRectangle2D.Float(0, 0, (float) (getWidth() * fraction), h, h, h));
            g.dispose();
        }
    }

    /** Counts farmers per district and status, in the shape {@link DistrictBars} wants. */
    static Map<String, int[]> byDistrict(List<bw.farmers.model.Farmer> farmers) {
        Map<String, int[]> m = new LinkedHashMap<>();
        for (bw.farmers.model.Farmer f : farmers) {
            int[] v = m.computeIfAbsent(f.district(), k -> new int[3]);
            v[switch (f.status()) {
                case APPROVED -> 0;
                case PENDING -> 1;
                case DECLINED -> 2;
            }]++;
        }
        return m;
    }
}
