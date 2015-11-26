package jp.saka1029.land;

public class Point {
    
    public final double x, y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point multiply(double k) {
        return new Point(x * k, y * k);
    }
    
    public Point plus(Point p) {
        return new Point(x + p.x, y + p.y);
    }
    
    public Point minus(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    
    public double distance(Point p) {
        Point d = minus(p);
        return Math.sqrt(d.x * d.x + d.y * d.y);
    }

    public Point rotate(double a) {
        double sin = Math.sin(a);
        double cos = Math.cos(a);
        return new Point(cos * x + sin * y, -sin * x + cos * y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;
        Point o = (Point)obj;
        return o.x == x && o.y == y;
    }
    
    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

}
