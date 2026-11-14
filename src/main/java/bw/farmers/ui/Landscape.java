package bw.farmers.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * A painted Botswana farm at golden hour: sorghum rows running to the horizon, acacias and a
 * baobab on the skyline, a borehole windmill and a few cattle. Drawn with Java2D, so it is
 * sharp at any size and has no image licence to worry about. Children are laid out on top.
 */
final class Landscape extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color SKY_TOP = new Color(0x163C30);
    private static final Color SKY_MID = new Color(0xB8743A);
    private static final Color SKY_LOW = new Color(0xF4D18A);
    private static final Color SUN = new Color(0xFFF4D2);
    private static final Color MESA = new Color(0xA0703F);
    private static final Color SILHOUETTE = new Color(0x1E261C);
    private static final Color FIELD_FAR = new Color(0x8C9548);
    private static final Color FIELD_GOLD = new Color(0xC99A3A);
    private static final Color FIELD_GREEN = new Color(0x3E6B35);
    private static final Color FIELD_NEAR = new Color(0x1F3D27);

    /** 0 = no shading; 1 = strong dark gradient at the bottom, for white text. */
    private final float shade;
    /** Horizontal position of the sun and vanishing point, 0..1. */
    private final float focus;

    Landscape(LayoutManager layout, float shade, float focus) {
        super(layout);
        this.shade = shade;
        this.focus = focus;
        setOpaque(true);
        setBackground(FIELD_NEAR);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        paintScene(g, getWidth(), getHeight(), shade, focus);
        g.dispose();
    }

    static void paintScene(Graphics2D g, int w, int h, float shade, float focus) {
        Ui.antialias(g);
        Random rnd = new Random(7);
        float horizon = h * 0.60f;
        float sx = w * focus;
        // the scene's unit of size: follows the shorter side, but never too small for wide banners
        float u = Math.max(Math.min(w, h), w * 0.45f) / 100f;

        // sky
        g.setPaint(new LinearGradientPaint(0, 0, 0, horizon, new float[]{0f, 0.55f, 1f},
                new Color[]{SKY_TOP, SKY_MID, SKY_LOW}));
        g.fill(new Rectangle2D.Float(0, 0, w, horizon + 6 * u));

        // sun glow and disc
        float sy = horizon - 7 * u;
        float glow = 55 * u;
        g.setPaint(new RadialGradientPaint(new Point2D.Float(sx, sy), glow, new float[]{0f, 0.25f, 1f},
                new Color[]{new Color(255, 240, 200, 210), new Color(255, 222, 150, 90), new Color(255, 210, 130, 0)},
                MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Float(0, 0, w, horizon + 6 * u));
        float sr = 8.5f * u;
        g.setColor(SUN);
        g.fill(new Ellipse2D.Float(sx - sr, sy - sr, 2 * sr, 2 * sr));

        // birds
        g.setColor(new Color(22, 40, 32, 150));
        g.setStroke(new BasicStroke(Math.max(1f, 0.35f * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float[][] birds = {{0.20f, 0.20f, 1.4f}, {0.25f, 0.16f, 1.0f}, {0.29f, 0.22f, 0.8f}, {0.58f, 0.13f, 0.9f}};
        for (float[] b : birds) {
            bird(g, w * b[0], h * b[1], b[2] * 1.6f * u);
        }

        // distant mesas, hazy
        g.setColor(new Color(MESA.getRed(), MESA.getGreen(), MESA.getBlue(), 120));
        g.fill(mesa(w * 0.02f, w * 0.30f, horizon, 4.2f * u, w * 0.04f));
        g.fill(mesa(w * 0.78f, w * 1.02f, horizon, 3.2f * u, w * 0.05f));
        g.setColor(new Color(MESA.getRed(), MESA.getGreen(), MESA.getBlue(), 80));
        g.fill(mesa(w * 0.40f, w * 0.62f, horizon, 2.0f * u, w * 0.05f));

        // skyline trees
        g.setColor(SILHOUETTE);
        acacia(g, w * 0.10f, horizon + 0.6f * u, 9.5f * u);
        acacia(g, w * 0.23f, horizon + 0.3f * u, 5.0f * u);
        acacia(g, w * 0.90f, horizon + 0.5f * u, 7.0f * u);
        baobab(g, w * 0.53f, horizon + 0.5f * u, 9.0f * u);

        // fields, back to front
        float vx = sx;
        Shape far = band(w, h, horizon, horizon + 0.8f * u, 0.4f * u, 0f);
        fill(g, far, FIELD_FAR);

        Shape gold = band(w, h, horizon + 4.5f * u, horizon + 3.0f * u, 1.2f * u, 1.3f);
        g.setPaint(new GradientPaint(0, horizon, FIELD_GOLD.brighter(), 0, horizon + 16 * u, FIELD_GOLD));
        g.fill(gold);
        rows(g, gold, vx, horizon, w, h, 40, new Color(110, 70, 20, 70), 0.35f * u);

        // windmill on the gold field, with its little reservoir
        windmill(g, w * 0.34f, horizon + 7.5f * u, 22 * u);

        // cattle grazing
        g.setColor(new Color(0x2A2219));
        cow(g, w * 0.47f, horizon + 9.5f * u, 1.45f * u, false);
        cow(g, w * 0.52f, horizon + 10.4f * u, 1.55f * u, true);
        cow(g, w * 0.63f, horizon + 8.6f * u, 1.25f * u, true);

        Shape green = band(w, h, horizon + 15f * u, horizon + 12f * u, 2.2f * u, 2.1f);
        g.setPaint(new GradientPaint(0, horizon + 12 * u, FIELD_GREEN.brighter(), 0, h, FIELD_GREEN.darker()));
        g.fill(green);
        rows(g, green, vx, horizon, w, h, 46, new Color(10, 30, 12, 80), 0.5f * u);
        rows(g, green, vx + 0.6f * u, horizon, w, h, 46, new Color(160, 190, 90, 45), 0.25f * u);

        Shape near = band(w, h, horizon + 27f * u, horizon + 31f * u, 2.5f * u, 0.7f);
        fill(g, near, FIELD_NEAR);
        tufts(g, rnd, w, h, horizon + 30f * u, u);

        // shading for text legibility
        if (shade > 0) {
            g.setPaint(new GradientPaint(0, h * 0.40f, new Color(10, 22, 16, 0), 0, h,
                    new Color(10, 22, 16, Math.round(235 * shade))));
            g.fill(new Rectangle2D.Float(0, h * 0.40f, w, h * 0.6f + 1));
            g.setPaint(new GradientPaint(0, 0, new Color(10, 22, 16, Math.round(110 * shade)), 0, h * 0.25f,
                    new Color(10, 22, 16, 0)));
            g.fill(new Rectangle2D.Float(0, 0, w, h * 0.25f));
        }
    }

    private static void fill(Graphics2D g, Shape s, Color c) {
        g.setColor(c);
        g.fill(s);
    }

    /** A field band from {@code yl} at the left edge to {@code yr} at the right, with a gentle wave. */
    private static Shape band(int w, int h, float yl, float yr, float wave, float phase) {
        Path2D p = new Path2D.Float();
        int steps = 48;
        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            float x = t * w;
            float y = yl + (yr - yl) * t + (float) Math.sin(t * Math.PI * 2.2 + phase) * wave;
            if (i == 0) {
                p.moveTo(x, y);
            } else {
                p.lineTo(x, y);
            }
        }
        p.lineTo(w, h);
        p.lineTo(0, h);
        p.closePath();
        return p;
    }

    /** Crop rows converging on the vanishing point, clipped to the band. */
    private static void rows(Graphics2D g, Shape band, float vx, float vy, int w, int h, int count, Color c,
                             float width) {
        Shape old = g.getClip();
        g.clip(band);
        // fade the rows out towards the horizon, where they would bunch into a dark smudge
        g.setPaint(new GradientPaint(0, vy, new Color(c.getRed(), c.getGreen(), c.getBlue(), 0), 0,
                vy + (h - vy) * 0.55f, c));
        g.setStroke(new BasicStroke(Math.max(0.8f, width)));
        float spread = w * 4.5f;
        for (int i = -count; i <= count; i++) {
            float bx = vx + spread * i / count;
            g.draw(new Line2D.Float(vx, vy, bx, h * 1.6f));
        }
        g.setClip(old);
    }

    private static Shape mesa(float x0, float x1, float base, float height, float slope) {
        Path2D p = new Path2D.Float();
        p.moveTo(x0, base);
        p.curveTo(x0 + slope * 0.5f, base, x0 + slope * 0.6f, base - height, x0 + slope, base - height);
        p.lineTo(x1 - slope, base - height * 0.92f);
        p.curveTo(x1 - slope * 0.6f, base - height * 0.92f, x1 - slope * 0.5f, base, x1, base);
        p.closePath();
        return p;
    }

    private static void bird(Graphics2D g, float x, float y, float s) {
        Path2D p = new Path2D.Float();
        p.moveTo(x - s, y - s * 0.2f);
        p.quadTo(x - s * 0.45f, y - s * 0.7f, x, y);
        p.quadTo(x + s * 0.45f, y - s * 0.7f, x + s, y - s * 0.2f);
        g.draw(p);
    }

    /** An umbrella thorn acacia: forked trunk under a wide, flat crown. */
    private static void acacia(Graphics2D g, float x, float base, float s) {
        Area tree = new Area();
        Path2D trunk = new Path2D.Float();
        float t = s * 0.07f;
        trunk.moveTo(x - t, base);
        trunk.lineTo(x - t * 0.6f, base - s * 0.45f);
        trunk.lineTo(x - s * 0.42f, base - s * 0.86f);
        trunk.lineTo(x - s * 0.36f, base - s * 0.88f);
        trunk.lineTo(x - t * 0.1f, base - s * 0.55f);
        trunk.lineTo(x + s * 0.30f, base - s * 0.90f);
        trunk.lineTo(x + s * 0.36f, base - s * 0.87f);
        trunk.lineTo(x + t * 0.9f, base - s * 0.45f);
        trunk.lineTo(x + t, base);
        trunk.closePath();
        tree.add(new Area(trunk));
        float cy = base - s * 0.98f;
        float[][] puffs = {{-0.62f, 0.02f, 0.62f}, {-0.25f, -0.05f, 0.7f}, {0.15f, -0.04f, 0.72f},
                {0.52f, 0.01f, 0.6f}, {-0.05f, 0.03f, 1.15f}};
        for (float[] p : puffs) {
            float cw = s * p[2];
            tree.add(new Area(new Ellipse2D.Float(x + s * p[0] - cw / 2, cy + s * p[1] - s * 0.13f, cw, s * 0.22f)));
        }
        g.fill(tree);
    }

    /** A baobab: a fat trunk with stubby root-like branches. */
    private static void baobab(Graphics2D g, float x, float base, float s) {
        Area tree = new Area();
        Path2D trunk = new Path2D.Float();
        trunk.moveTo(x - s * 0.24f, base);
        trunk.curveTo(x - s * 0.20f, base - s * 0.3f, x - s * 0.15f, base - s * 0.55f, x - s * 0.13f, base - s * 0.62f);
        trunk.lineTo(x + s * 0.13f, base - s * 0.62f);
        trunk.curveTo(x + s * 0.15f, base - s * 0.55f, x + s * 0.20f, base - s * 0.3f, x + s * 0.24f, base);
        trunk.closePath();
        tree.add(new Area(trunk));
        BasicStroke branch = new BasicStroke(s * 0.075f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        float top = base - s * 0.6f;
        float[][] arms = {{-0.11f, -0.46f, -0.14f}, {-0.07f, -0.28f, -0.30f}, {0.0f, 0.02f, -0.34f},
                {0.07f, 0.27f, -0.28f}, {0.11f, 0.47f, -0.12f}};
        for (float[] a : arms) {
            Path2D arm = new Path2D.Float();
            arm.moveTo(x + s * a[0], top);
            arm.quadTo(x + s * (a[0] + a[1]) / 2, top + s * a[2] * 0.4f, x + s * a[1], top + s * a[2]);
            tree.add(new Area(branch.createStrokedShape(arm)));
            // a sparse tuft of leaves at each tip
            float r = s * 0.09f;
            tree.add(new Area(new Ellipse2D.Float(x + s * a[1] - r * 1.4f, top + s * a[2] - r * 0.7f, r * 2.8f,
                    r * 1.2f)));
        }
        g.fill(tree);
    }

    /** A borehole windmill: lattice tower, multi-blade wheel and tail vane, with a round reservoir. */
    private static void windmill(Graphics2D g, float x, float base, float s) {
        Color ink = new Color(0x1B231A);
        g.setColor(ink);
        float top = base - s;
        float half = s * 0.17f;
        float lw = Math.max(1f, s * 0.018f);
        g.setStroke(new BasicStroke(lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(x - half, base, x - s * 0.03f, top + s * 0.08f));
        g.draw(new Line2D.Float(x + half, base, x + s * 0.03f, top + s * 0.08f));
        int sections = 5;
        g.setStroke(new BasicStroke(lw * 0.6f));
        for (int i = 0; i < sections; i++) {
            float y0 = base - (s * 0.92f) * i / sections;
            float y1 = base - (s * 0.92f) * (i + 1) / sections;
            float w0 = half - (half - s * 0.03f) * i / sections;
            float w1 = half - (half - s * 0.03f) * (i + 1) / sections;
            g.draw(new Line2D.Float(x - w0, y0, x + w1, y1));
            g.draw(new Line2D.Float(x + w0, y0, x - w1, y1));
            g.draw(new Line2D.Float(x - w1, y1, x + w1, y1));
        }
        // wheel
        float cx = x;
        float cy = top + s * 0.02f;
        float r = s * 0.22f;
        Area wheel = new Area();
        int blades = 16;
        for (int i = 0; i < blades; i++) {
            double a0 = Math.PI * 2 * i / blades;
            double a1 = a0 + Math.PI * 2 / blades * 0.62;
            Path2D blade = new Path2D.Float();
            blade.moveTo(cx + Math.cos(a0) * r * 0.32, cy + Math.sin(a0) * r * 0.32);
            blade.lineTo(cx + Math.cos(a0) * r, cy + Math.sin(a0) * r);
            blade.lineTo(cx + Math.cos(a1) * r, cy + Math.sin(a1) * r);
            blade.lineTo(cx + Math.cos(a1) * r * 0.32, cy + Math.sin(a1) * r * 0.32);
            blade.closePath();
            wheel.add(new Area(blade));
        }
        float hub = r * 0.18f;
        wheel.add(new Area(new Ellipse2D.Float(cx - hub, cy - hub, 2 * hub, 2 * hub)));
        g.fill(wheel);
        // tail vane
        g.setStroke(new BasicStroke(lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(cx, cy, cx + s * 0.38f, cy + s * 0.01f));
        Path2D vane = new Path2D.Float();
        vane.moveTo(cx + s * 0.30f, cy - s * 0.07f);
        vane.lineTo(cx + s * 0.44f, cy - s * 0.10f);
        vane.lineTo(cx + s * 0.44f, cy + s * 0.10f);
        vane.lineTo(cx + s * 0.30f, cy + s * 0.07f);
        vane.closePath();
        g.fill(vane);
        // reservoir
        float rw = s * 0.46f;
        float rh = s * 0.08f;
        g.fill(new Rectangle2D.Float(x + s * 0.24f, base - rh, rw, rh));
        g.setColor(new Color(0x8FB3B0));
        g.fill(new Rectangle2D.Float(x + s * 0.26f, base - rh, rw - s * 0.04f, rh * 0.25f));
        g.setColor(ink);
    }

    /** A grazing cow, seen side on. */
    private static void cow(Graphics2D g, float x, float base, float s, boolean facingLeft) {
        Area cow = new Area();
        float dir = facingLeft ? -1 : 1;
        cow.add(new Area(new java.awt.geom.RoundRectangle2D.Float(x - 2.2f * s, base - 3.4f * s, 4.4f * s, 2.0f * s,
                1.6f * s, 1.6f * s)));
        // legs
        for (float lx : new float[]{-1.8f, -1.1f, 1.0f, 1.7f}) {
            cow.add(new Area(new Rectangle2D.Float(x + lx * s - 0.22f * s, base - 1.8f * s, 0.44f * s, 1.8f * s)));
        }
        // neck and lowered head
        Path2D head = new Path2D.Float();
        head.moveTo(x + dir * 1.8f * s, base - 3.3f * s);
        head.lineTo(x + dir * 3.0f * s, base - 2.0f * s);
        head.lineTo(x + dir * 3.6f * s, base - 1.0f * s);
        head.lineTo(x + dir * 2.9f * s, base - 0.8f * s);
        head.lineTo(x + dir * 1.7f * s, base - 2.0f * s);
        head.closePath();
        cow.add(new Area(head));
        // horns
        Path2D horn = new Path2D.Float();
        horn.moveTo(x + dir * 2.7f * s, base - 2.1f * s);
        horn.quadTo(x + dir * 2.6f * s, base - 2.9f * s, x + dir * 3.2f * s, base - 2.9f * s);
        cow.add(new Area(new BasicStroke(0.22f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                .createStrokedShape(horn)));
        // tail
        Path2D tail = new Path2D.Float();
        tail.moveTo(x - dir * 2.1f * s, base - 3.1f * s);
        tail.quadTo(x - dir * 2.6f * s, base - 2.4f * s, x - dir * 2.5f * s, base - 1.4f * s);
        cow.add(new Area(new BasicStroke(0.18f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                .createStrokedShape(tail)));
        g.fill(cow);
    }

    /** Grass tufts in the foreground. */
    private static void tufts(Graphics2D g, Random rnd, int w, int h, float top, float u) {
        g.setColor(new Color(0x14291B));
        g.setStroke(new BasicStroke(Math.max(1f, 0.35f * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int count = Math.max(18, (int) (w / (6 * u)));
        for (int i = 0; i < count; i++) {
            float x = rnd.nextFloat() * w;
            float y = top + rnd.nextFloat() * (h - top) * 0.9f;
            float s = (1.6f + rnd.nextFloat() * 2.2f) * u * (0.6f + (y - top) / Math.max(1, h - top));
            int blades = 4 + rnd.nextInt(4);
            for (int b = 0; b < blades; b++) {
                double ang = -Math.PI / 2 + (b - blades / 2.0) * 0.28 + (rnd.nextFloat() - 0.5) * 0.2;
                GeneralPath p = new GeneralPath();
                p.moveTo(x, y);
                p.quadTo(x + Math.cos(ang) * s * 0.4, y + Math.sin(ang) * s * 0.6,
                        x + Math.cos(ang) * s, y + Math.sin(ang) * s);
                g.draw(p);
            }
        }
    }
}
