package jp.saka1029.land;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;

public class SVG {

    public static final double SCALE = 100;
    public static final double MARGIN = 2.0;
    public static final String NL = String.format("%n");

    final StringBuilder text = new StringBuilder();
    final double width, height;
    final double minX, maxX, minY, maxY;
    
    public SVG(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        width = maxX - minX;
        height = maxY - minY;
    }

    int scale(double d) {
        return (int)(d * SCALE);
    }
    
    Point transform(Point p) {
        return new Point((p.x - minX + MARGIN) * SCALE, (height - p.y - minY + MARGIN) * SCALE);
    }

    private void printf(String format, Object... args) {
        text.append(String.format(format, args));
    }

    public void box() {
        printf("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\"%n",
            scale(MARGIN), scale(MARGIN), scale(width), scale(height));
        printf(" stroke-dasharray=\"20 2\""
            + " stroke-width=\"2\""
            + " stroke=\"black\""
            + " fill=\"none\" />%n");
    }

    public void polygonDash(Collection<Point> points) {
        polygon(points, "fill=\"none\" stroke-dasharray=\"20 2\"");
    }

    public void polygon(Collection<Point> points) {
        polygon(points, "fill=\"#d0f0d0\"");
    }

    public void polygon(Collection<Point> points, String opt) {
        printf("<polygon points=\"");
        for (Point e : points) {
            Point p = transform(e);
            printf("%.0f %.0f ", p.x, p.y);
        }
        printf("\"%n");
        printf(" stroke=\"black\" stroke-width=\"2\"");
        if (opt != null)
            printf(" %s", opt);
        printf("/>%n");
    }
    
    public void text(Point at, String text) {
        Point p = transform(at);
        printf("<text x=\"%.0f\" y=\"%.0f\"", p.x, p.y);
        printf(" font-size=\"60\"");
        printf(" text-anchor=\"middle\" dominant-baseline=\"middle\"");
        printf(">%s</text>%n", text);
    }

    public void text(Point start, Point end, String text) {
        Point center = transform(start.plus(end).multiply(0.5));
        Point diff = end.minus(start);
        double alpha = diff.x == 0.0 ? -90 : -Math.atan(diff.y / diff.x) * 180 / Math.PI;
        printf("<text x=\"%.0f\" y=\"%.0f\"", center.x, center.y);
        printf(" font-size=\"60\"");
        printf(" text-anchor=\"middle\" dominant-baseline=\"middle\"");
        printf(" transform=\"rotate(%f %.0f %.0f)\"", alpha, center.x, center.y);
        printf(">%s</text>%n", text);
    }

    public void writeTo(File file) throws IOException {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            out.printf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>%n");
            out.printf("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">%n",
                scale(width + MARGIN * 2), scale(height + MARGIN * 2));
            out.print(text.toString());
            out.printf("</svg>%n");
        }
    }
}
