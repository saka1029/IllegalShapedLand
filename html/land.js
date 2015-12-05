Point = function(x, y) {
    this.x = x;
    this.y = y;
}

Point.prototype = {
    plus : function(p) {
        return new Point(this.x + p.x, this.y + p.y);
    },

    minus : function(p) {
        return new Point(this.x - p.x, this.y - p.y);
    },

    multiply : function(k) {
        return new Point(this.x * k, this.y * k);
    },

    distance : function(p) {
        var d = this.minus(p);
        return Math.sqrt(d.x * d.x + d.y * d.y);
    },

    rotate : function(a) {
        var sin = Math.sin(a);
        var cos = Math.cos(a);
        return new Point(cos * this.x + sin * this.y, -sin * this.x + cos * this.y);
    },

    toString : function() {
        return "(" + this.x + ", " + this.y + ")";
    },
};

Triangle = function(a, b, c) {
    if (!a) throw "頂点の名前1が定義されていません";
    if (!b) throw "頂点の名前2が定義されていません";
    if (!c) throw "頂点の名前3が定義されていません";
    if (a == b) throw "頂点の名前1(" + a + ")と2(" + b + ")が同じです";
    if (b == c) throw "頂点の名前2(" + b + ")と3(" + c + ")が同じです";
    if (c == a) throw "頂点の名前1(" + c + ")と3(" + a + ")が同じです";
    this.points = [a, b, c];
}

Triangle.prototype = {
    toString: function() {
        return "三角形(" + this.points + ")";
    }
};

Edge = function(left, right, distance) {
    if (!left) throw "頂点の名前1が定義されていません";
    if (!right) throw "頂点の名前2が定義されていません";
    if (!distance) throw "辺の長さが定義されていません";
    if (left == right) throw "頂点の名前1(" + left + ")と2(" + right + ")が同じです";
    this.left = left;
    this.right = right;
    this.distance = distance;
}

Edge.prototype = {
    toString : function() {
        return "辺(" + this.left + ", " + this.right + ", " + this.distance + ")";
    }
};

Region = function(triangle, distances) {
    this.names = triangle.points;
    this.distances = [];
    for (var i = 0; i < 3; ++i) {
        var left = this.names[i];
        var right = i < 2 ? this.names[i + 1] : this.names[0];
        this.distances[i] = distances[left + right];
    }
}

Region.prototype = {
    third2: function(xl, a, al, b, bl) {
        var alpha = Math.acos((al * al + xl * xl - bl * bl) / (2 * al * xl));
        var xx = b.minus(a).multiply(al / xl);
        return a.plus(xx.rotate(alpha));
    },

    third1: function(points, name, xl, a, al, b, bl) {
        var p = this.third2(xl, a, al, b, bl);
        points[name] = p;
        return p;
    },

    third: function(points) {
        var p0 = points[this.names[0]];
        var p1 = points[this.names[1]];
        var p2 = points[this.names[2]];
        var x;
        if (p0 && p1)
            x = this.third1(points, this.names[2], this.distances[0], p0, this.distances[2], p1, this.distances[1]);
        else if (p0 && p2)
            x = this.third1(points, this.names[1], this.distances[2], p2, this.distances[1], p0, this.distances[0]);
        else if (p2 && p1)
            x = this.third1(points, this.names[0], this.distances[1], p1, this.distances[0], p2, this.distances[2]);
        else
            x = null;
        return x;
    },
    
    area: function() {
        var s = (this.distances[0] + this.distances[1] + this.distances[2]) / 2.0;
        var t = s * (s - this.distances[0]) * (s - this.distances[1]) * (s - this.distances[2]);
        if (t < 0)
            throw this.toString() + "は三角形ではありません";
        return Math.sqrt(t);
    },

    toString: function() {
        return "領域(" + this.names[0] + "--" + this.distances[0] + "->"
            + this.names[1] + "--" + this.distances[1] + "->"
            + this.names[2] + "--" + this.distances[2] + "->"
            + this.names[0] + ")";
    }
};

Land = function(startBase, endBase, edges, triangles, /* optional(25) */scale, /* optional(2) */ margin) {
    if (!startBase) throw "基準となる点1の名前が定義されていません";
    if (!endBase) throw "基準となる点2の名前が定義されていません";
    if (startBase == endBase) throw "基準となる点1(" + startBase + ")と点2(" + endBase + ")の名前が同じです";
    this.distances = new Object();
    for (var i in edges) {
        var e = edges[i];
        this.distances[e.left + e.right] = e.distance;
        this.distances[e.right + e.left] = e.distance;
    }
    if (!this.distances[startBase + endBase])
        throw startBase + "と" + endBase + "の距離が定義されていません";
    this.startBase = startBase;
    this.endBase = endBase;
    this.edges = edges;
    this.triangles = triangles;
    try {
        this.calculatePoints();
    } catch (e) {
        if (e == "全体を囲むことができません") {
            var temp = this.startBase;
            this.startBase = this.endBase;
            this.endBase = temp;
            this.calculatePoints();
        } else
            throw e;
    }
    this.minX = Number.MAX_VALUE;
    this.maxX = Number.MIN_VALUE;
    this.minY = Number.MAX_VALUE;
    this.maxY = Number.MIN_VALUE;
    for (var k in this.points) {
        e = this.points[k];
        this.minX = Math.min(this.minX, e.x);
        this.maxX = Math.max(this.maxX, e.x);
        this.minY = Math.min(this.minY, e.y);
        this.maxY = Math.max(this.maxY, e.y);
    }
    this.width = this.maxX - this.minX;
    this.height = this.maxY - this.minY;
    this.legalShapedArea = this.width * this.height;
    this.SCALE = scale === undefined ? 25 : scale;
    this.MARGIN = margin === undefined ? 2 :margin;
}

Land.prototype = {
    scale: function(x) {
        return x * this.SCALE;
    },
    
    transform: function(p) {
        return new Point(
            p.x - this.minX + this.MARGIN,
            this.height - p.y - this.minY + this.MARGIN).multiply(this.SCALE);
    },

    pointsString: function() {
        var r = "";
        for (var i = 0; i < arguments.length; ++i)
            r += arguments[i].x + " " + arguments[i].y + " ";
        return r;
    },

    calculatePoints: function() {
        this.regions = [];
        for (var i in this.triangles) {
            var t = this.triangles[i];
            this.regions.push(new Region(t, this.distances));
        }
        this.area = 0;
        for (var i in this.regions) {
            var r = this.regions[i];
            this.area += r.area();
        }
        this.points = new Object();
        this.points[this.startBase] = new Point(0, 0);
        this.points[this.endBase] = new Point(this.distances[this.startBase + this.endBase], 0);
        while (true) {
            var size = this.regions.length;
            for (var i = 0; i < this.regions.length; ) {
                var r = this.regions[i];
                var p = r.third(this.points);
                if (p) {
                    if (p.y < 0)
                        throw "全体を囲むことができません";
                    this.regions.splice(i, 1);
                } else
                    ++i;
            }
            if (this.regions.length == size)
                break;
        }
        if (this.regions.length != 0)
            throw "計算できません。計算済の点=" + this.points + ", 未計算の領域=" + this.regions;
    },
    
    svg: function() {
        var svg = new SVG(this.scale(this.width + this.MARGIN * 2), this.scale(this.height + this.MARGIN * 2));
        var p = this.transform(new Point(this.minX, this.maxY));
        // 想定整形地
        svg.draw("rect", {
            x: p.x,
            y: p.y,
            width: this.scale(this.width),
            height: this.scale(this.height),
            fill: "none",
            stroke: "black",
            "stroke-width": 2,
            "stroke-dasharray": "20 2",
        });
        // 三角形
        for (var i in this.triangles) {
            var t = this.triangles[i];
            svg.draw("polygon", {
                points: this.pointsString(
                    this.transform(this.points[t.points[0]]), 
                    this.transform(this.points[t.points[1]]), 
                    this.transform(this.points[t.points[2]])),
                fill: "#d0f0d0",
                stroke: "black",
                "stroke-width": 2,
            });
        }
        // 辺の長さ
        for (var i in this.edges) {
            var e = this.edges[i];
            var n0 = e.left;
            var n1 = e.right;
            var p0 = this.points[n0];
            var p1 = this.points[n1];
            var d = this.distances[n0 + n1];
            var center = this.transform(p0.plus(p1).multiply(0.5));
            var diff = p1.minus(p0);
            var alpha = diff.x == 0.0 ? -90 : -Math.atan(diff.y / diff.x) * 180 / Math.PI;
            svg.draw("text", {
                x: center.x,
                y: center.y,
                transform: "rotate(" + alpha + " " + center.x + " " + center.y + ")",
                "font-size": 20,
//              "font-weight": "bold",
                "text-anchor": "middle",
                "dominant-baseline": "text-after-edge"
            }, d.toFixed(2));
        }
        // 頂点の名前
        for (var key in this.points) {
            var p = this.transform(this.points[key]);
            svg.draw("text", {
                x: p.x,
                y: p.y,
                "font-size": 40,
                fill: "red",
                "font-weight": "bold",
                "text-anchor": "middle",
                "dominant-baseline": "middle"
            }, key);
        }
        // 想定整形地 幅
        var p = this.transform(new Point(this.minX, this.minY).plus(new Point(this.maxX, this.minY)).multiply(0.5));
        svg.draw("text", {
            x: p.x,
            y: p.y + 20,
            "font-size": 20,
            "font-weight": "bold",
            "text-anchor": "middle",
            "dominant-baseline": "text-before-edge"
        }, this.width.toFixed(2));
        // 想定整形地 高さ
        var q = this.transform(new Point(this.minX, this.minY).plus(new Point(this.minX, this.maxY)).multiply(0.5));
        svg.draw("text", {
            x: q.x - 20,
            y: q.y,
            "font-size": 20,
            "font-weight": "bold",
            "text-anchor": "middle",
            "dominant-baseline": "text-after-edge",
            transform: "rotate(-90 " + (q.x - 20) + " " + q.y + ")",
        }, this.height.toFixed(2));
        return svg.element;
    }
};
