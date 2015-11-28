# IllegalShapedLand

相続税の計算における不整形地から想定整形地を求めます

## 概要

土地を相続する場合、土地の評価額を算出する必要があります。
周辺の道路の路線価が決まっている場合はそれを元に計算します。

国税庁のホームページ
 [土地家屋の評価](https://www.nta.go.jp/taxanswer/sozoku/4602.htm)にその計算方法が掲載されています。

ただし、角地の場合は側方の路線価を考慮する必要があります。
（[地区の異なる2以上の路線に接する宅地の評価](https://www.nta.go.jp/taxanswer/hyoka/4605.htm)）

また、土地の形状が長方形でない場合は[不整形地の評価](https://www.nta.go.jp/shiraberu/zeiho-kaishaku/tsutatsu/kihon/sisan/hyoka/02/03.htm)
を考慮する必要があります。



## 相続税における土地の評価

## 不整形地から想定整形地を求める

以下のような土地の図面があったとします。

![地積測量図の例](data/figure.png)

```java
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
```

```
面積=159.213309㎡
想定整形地 17.173888m×12.686205m=217.871465㎡
```

出力されるSVGは以下のようになります。

![図面](data/sample.png)
        


