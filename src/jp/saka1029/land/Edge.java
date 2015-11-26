package jp.saka1029.land;

public class Edge {

    public final String left, right;
    public final double distance;
 
    public Edge(String left, String right, double distance) {
        this.left = left;
        this.right = right;
        this.distance = distance;
    }
 
    @Override
    public String toString() {
        return String.format("%s--%s->%s", left, distance, right);
    }
}
