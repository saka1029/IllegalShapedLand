package jp.saka1029.land;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestLand {

    @Test
    public void testMyLand() throws IOException {
        List<Edge> edges = Arrays.asList(
            new Edge("a", "b", 10.17),
            new Edge("a", "e", 9.57),
            new Edge("b", "c", 13.99),
            new Edge("b", "d", 14.05),
            new Edge("b", "e", 14.27),
            new Edge("d", "c", 0.32),
            new Edge("e", "d", 3.00)
        );
        List<Triangle> triangles = Arrays.asList(
            new Triangle("a", "b", "e"),
            new Triangle("b", "c", "d"),
            new Triangle("b", "d", "e")
        );
        Land landAE = new Land("a", "e", edges, triangles);
        System.out.printf("a-e面:");
        System.out.printf("面積=%f㎡%n", landAE.area);
        System.out.printf("想定整形地 %fm×%fm=%f㎡%n", landAE.width, landAE.height, landAE.legalShapedArea);
        landAE.writeSVG(new File("data/landAE.svg"));
        Land landED = new Land("e", "d", edges, triangles);
        System.out.printf("e-d面:");
        System.out.printf("面積=%f㎡%n", landED.area);
        System.out.printf("想定整形地 %fm×%fm=%f㎡%n", landED.width, landED.height, landED.legalShapedArea);
        landED.writeSVG(new File("data/landED.svg"));
    }

    @Test
    public void testLand() throws IOException {
        List<Edge> edges = Arrays.asList(
            new Edge("a", "b", 6.30),
            new Edge("a", "e", 8.55),
            new Edge("b", "e", 7.30),
            new Edge("c", "b", 4.89),
            new Edge("c", "d", 4.90),
            new Edge("c", "e", 7.15),
            new Edge("e", "d", 8.68)
        );
        List<Triangle> triangles = Arrays.asList(
            new Triangle("a", "b", "e"),
            new Triangle("b", "c", "e"),
            new Triangle("c", "d", "e")
        );
        Land land = new Land("a", "e", edges, triangles);
        System.out.printf("面積=%f㎡%n", land.area);
        System.out.printf("想定整形地 %fm×%fm=%f㎡%n", land.width, land.height, land.legalShapedArea);
        land.writeSVG(new File("data/land.svg"));
    }


    @Test
    public void testSample() throws IOException {
        List<Edge> edges = Arrays.asList(
            new Edge("a", "b", 17.17),
            new Edge("b", "c",  8.01),
            new Edge("c", "d", 13.40),
            new Edge("d", "a", 13.50),
            new Edge("a", "c", 18.95)
        );
        List<Triangle> triangles = Arrays.asList(
            new Triangle("a", "d", "c"),
            new Triangle("a", "c", "b")
        );
        Land land = new Land("a", "b", edges, triangles);
        System.out.printf("面積=%f㎡%n", land.area);
        System.out.printf("想定整形地 %fm×%fm=%f㎡%n", land.width, land.height, land.legalShapedArea);
        land.writeSVG(new File("data/sample.svg"));
        Land land2 = new Land("d", "a", edges, triangles);
        System.out.printf("面積=%f㎡%n", land2.area);
        System.out.printf("想定整形地 %fm×%fm=%f㎡%n", land2.width, land2.height, land2.legalShapedArea);
        land2.writeSVG(new File("data/sample2.svg"));
    }

    double area(double a, double b, double c) {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Test
    public void testSampleTest() {
        System.out.println(area(13.50, 13.40, 18.95));
        System.out.println(area(13.50, 13.40, 18.95) * 2 / 18.95);
        System.out.println(area(17.17,  8.01, 18.95));
        System.out.println(area(17.17,  8.01, 18.95) * 2 / 18.95);
        System.out.println(18.95 * (9.54 + 7.26) / 2);
    }

}
