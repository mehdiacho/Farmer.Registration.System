package bw.farmers.ui;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** The FarmersBW mark: a sprout rising from a ploughed field under a low sun. */
public final class Logo {

    private Logo() {
    }

    /** Paints the mark in a {@code s}-by-{@code s} square at (x, y). */
    public static void paint(Graphics2D g0, float x, float y, float s) {
        Graphics2D g = (Graphics2D) g0.create();
        Ui.antialias(g);
        g.translate(x, y);
        Shape tile = new RoundRectangle2D.Float(0, 0, s, s, s * 0.46f, s * 0.46f);
        g.setPaint(new GradientPaint(0, 0, new Color(0x2B7A4D), s, s, new Color(0x123726)));
        g.fill(tile);
        g.clip(tile);

        // sun
        g.setColor(new Color(0xF2B33D));
        float r = s * 0.15f;
        g.fill(new Ellipse2D.Float(s * 0.68f - r, s * 0.32f - r, 2 * r, 2 * r));

        // field: a rounded hill with furrows
        g.setColor(new Color(0x3F9460));
        g.fill(new Ellipse2D.Float(-s * 0.3f, s * 0.70f, s * 1.6f, s * 0.8f));
        g.setColor(new Color(255, 248, 234, 90));
        g.setStroke(new BasicStroke(s * 0.035f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i <= 2; i++) {
            float k = i * 0.11f;
            g.draw(new java.awt.geom.Arc2D.Float(-s * 0.3f + s * k, s * 0.70f + s * k, s * 1.6f - 2 * s * k,
                    s * 0.8f, 20, 140, java.awt.geom.Arc2D.OPEN));
        }

        // sprout
        Color cream = new Color(0xFFF8EA);
        g.setColor(cream);
        g.setStroke(new BasicStroke(s * 0.075f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D stem = new Path2D.Float();
        stem.moveTo(s * 0.44f, s * 0.80f);
        stem.quadTo(s * 0.43f, s * 0.62f, s * 0.47f, s * 0.48f);
        g.draw(stem);
        g.fill(leaf(s * 0.46f, s * 0.56f, s * 0.15f, s * 0.36f, s * 0.36f, true));
        g.fill(leaf(s * 0.47f, s * 0.49f, s * 0.80f, s * 0.24f, s * 0.42f, false));
        g.dispose();
    }

    private static Shape leaf(float x0, float y0, float x1, float y1, float bulge, boolean flip) {
        float mx = (x0 + x1) / 2;
        float my = (y0 + y1) / 2;
        float dx = x1 - x0;
        float dy = y1 - y0;
        float len = (float) Math.hypot(dx, dy);
        float nx = -dy / len * bulge * 0.5f * (flip ? -1 : 1);
        float ny = dx / len * bulge * 0.5f * (flip ? -1 : 1);
        Path2D p = new Path2D.Float();
        p.moveTo(x0, y0);
        p.quadTo(mx + nx, my + ny, x1, y1);
        p.quadTo(mx - nx * 0.35f, my - ny * 0.35f, x0, y0);
        p.closePath();
        return p;
    }

    public static BufferedImage image(int px) {
        BufferedImage img = new BufferedImage(px, px, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        paint(g, 0, 0, px);
        g.dispose();
        return img;
    }

    /** Window icons at the sizes Windows, macOS and Linux ask for. */
    public static List<Image> windowIcons() {
        List<Image> list = new ArrayList<>();
        for (int px : new int[]{16, 20, 24, 32, 40, 48, 64, 128, 256}) {
            list.add(image(px));
        }
        return list;
    }

    /** The mark as a Swing icon, {@code px} unscaled pixels square. */
    public static Icon icon(int px) {
        int size = UIScale.scale(px);
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                paint((Graphics2D) g, x, y, size);
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}
