package jp.saka1029.land;

import java.util.Arrays;

public class Triangle {
    
    public final String[] points;
    
    public Triangle(String a, String b, String c) {
        if (a == null)
            throw new IllegalArgumentException("a");
        if (b == null)
            throw new IllegalArgumentException("b");
        if (c == null)
            throw new IllegalArgumentException("c");
        if (a.equals(b))
            throw new IllegalArgumentException("a and b duplicated");
        if (b.equals(c))
            throw new IllegalArgumentException("b and c duplicated");
        if (c.equals(a))
            throw new IllegalArgumentException("c and a duplicated");
        this.points = new String[] {a, b, c};
    }
    
    @Override
    public String toString() {
        return String.format("Triangle%s", Arrays.toString(points));
    }
}