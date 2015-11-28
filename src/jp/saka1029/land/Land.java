package jp.saka1029.land;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Land {
    
    class Region {
        
        final String[] names;
        final double[] distances;
        
        Region(Triangle triangle) {
            this.names = Arrays.copyOf(triangle.points, 3);
            this.distances = new double[3];
            for (int i = 0; i < 3; ++i) {
                String left = names[i];
                String right = i < 2 ? names[i + 1] : names[0];
                Double distance = Land.this.distances.get(left + right);
                if (distance == null)
                    throw new IllegalArgumentException(
                        String.format("%sにおける点%sと点%sの距離が定義されていません", triangle, left, right));
                this.distances[i] = distance;
            }
        }
        
        Point third(double xl, Point a, double al, Point b, double bl) {
            double alpha = Math.acos((al * al + xl * xl - bl * bl) / (2 * al * xl));
            Point xx = b.minus(a).multiply(al / xl);
            return a.plus(xx.rotate(alpha));
        }

        Point third(Map<String, Point> points, String name, double xl, Point a, double al, Point b, double bl) {
            Point p = third(xl, a, al, b, bl);
            points.put(name, p);
            return p;
        }

        Point third(Map<String, Point> points) {
            Point p0 = points.get(names[0]);
            Point p1 = points.get(names[1]);
            Point p2 = points.get(names[2]);
            Point x;
            if (p0 != null && p1 != null)
                x = third(points, names[2], distances[0], p0, distances[2], p1, distances[1]);
            else if (p0 != null && p2 != null)
                x = third(points, names[1], distances[2], p2, distances[1], p0, distances[0]);
            else if (p2 != null && p1 != null)
                x = third(points, names[0], distances[1], p1, distances[0], p2, distances[2]);
            else
                x = null;
            return x;
        }
        
        double area() {
            double s = (distances[0] + distances[1] + distances[2]) / 2.0;
            return Math.sqrt(s * (s - distances[0]) * (s - distances[1]) * (s - distances[2]));
        }

        @Override
        public String toString() {
            return String.format("%s--%s->%s--%s->%s--%s->%s",
                names[0], distances[0],
                names[1], distances[1],
                names[2], distances[2],
                names[0]);
        }
    }

    public final String startBase, endBase;
    public final List<Edge> edges;
    public final List<Triangle> triangles;
    public final Map<String, Double> distances;
    public final Map<String, Point> points;
    public final double minX, maxX, minY, maxY;
    public final double width, height;
    public final double area, legalShapedArea;
    
    /**
     * 
     * @param startBase 左下隅に配置する頂点の名前を指定します。
     * @param endBase   右下隅に配置する頂点の名前を指定します。
     * @param edges     辺の両端の頂点の名前と辺の長さを指定します。
     * @param triangles 三角形の頂点の名前を指定します。
     *                  頂点は右回りに指定する必要があります。
     */
    public Land(String startBase, String endBase, List<Edge> edges, List<Triangle> triangles) {
        if (startBase == null)
            throw new IllegalArgumentException("startBase");
        if (endBase == null)
            throw new IllegalArgumentException("endBase");
        if (edges == null || edges.isEmpty())
            throw new IllegalArgumentException("edges");
        if (triangles == null || triangles.isEmpty())
            throw new IllegalArgumentException("triangles");
        Map<String, Double> distances = new HashMap<>();
        for (Edge e : edges) {
            distances.put(e.left + e.right, e.distance);
            distances.put(e.right + e.left, e.distance);
        }
        if (!distances.containsKey(startBase + endBase))
            throw new IllegalArgumentException("%sと%sの距離がedgesに定義されていません");
        this.distances = Collections.unmodifiableMap(distances);
        this.startBase = startBase;
        this.endBase = endBase;
        this.edges = Collections.unmodifiableList(edges);
        this.triangles = Collections.unmodifiableList(triangles);
        List<Region> regions = new ArrayList<>();
        for (Triangle e : triangles)
            regions.add(new Region(e));
        System.out.println(regions);
        double area = 0;
        for (Region r : regions)
            area += r.area();
        this.area = area;
        System.out.println("total area=" + area);
        Map<String, Point> _points = new TreeMap<>();
        this.points = Collections.unmodifiableMap(_points);
        _points.put(startBase, new Point(0, 0));
        _points.put(endBase, new Point(distances.get(startBase + endBase), 0));
        while (true) {
            int size = regions.size();
            for (Iterator<Region> i = regions.iterator(); i.hasNext(); ) {
                Region a = i.next();
                Point p = a.third(_points);
                if (p != null)
                    i.remove();
            }
            if (regions.size() == size)
                break;
        }
        if (!regions.isEmpty())
            throw new RuntimeException("計算できません。計算済の点=" + points + ", 未計算の領域=" + regions);
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Point e : _points.values()) {
            minX = Math.min(minX, e.x);
            maxX = Math.max(maxX, e.x);
            minY = Math.min(minY, e.y);
            maxY = Math.max(maxY, e.y);
        }
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.width = maxX - minX;
        this.height = maxY - minY;
        this.legalShapedArea = width * height;
    }
    
    public void writeSVG(File file) throws IOException {
        SVG svg = new SVG(minX, maxX, minY, maxY);
        Point topLeft = new Point(minX, minY);
        Point bottomLeft = new Point(minX, maxY);
        Point topRight = new Point(maxX, minY);
        Point bottomRight = new Point(maxX, maxY);
        svg.polygonDash(Arrays.asList(topLeft, topRight, bottomRight, bottomLeft));
        for (Triangle e : triangles) {
            Point[] ps = new Point[] {points.get(e.points[0]), points.get(e.points[1]), points.get(e.points[2])};
            svg.polygon(Arrays.asList(ps));
        }
        // 辺の長さ
        for (Edge e : edges) {
            String n0 = e.left;
            String n1 = e.right;
            Point p0 = points.get(n0);
            Point p1 = points.get(n1);
            double d = distances.get(n0 + n1);
//            svg.text(p0.plus(p1).multiply(0.5), String.format("%.2f", d));
            svg.text(p0, p1, String.format("%.2f", d));
        }
        // 頂点の名前
        for (Entry<String, Point> e : points.entrySet())
            svg.text(e.getValue(), e.getKey()); 
        // 想定整形地
        double x = maxX - minX;
        double y = maxY - minY;
        svg.text(new Point(minX, minY), new Point(maxX, minY), String.format("%.2f", x));
        svg.text(new Point(minX, minY), new Point(minX, maxY), String.format("%.2f", y));
        svg.writeTo(file);
    }
    
}
