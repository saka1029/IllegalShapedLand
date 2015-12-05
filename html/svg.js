SVG = function(width, height) {
    this.namespace = "http://www.w3.org/2000/svg";
    this.element = document.createElementNS(this.namespace, "svg");
    this.set(this.element, {
        "xmlns": this.namespace,
        "xmlns:xlink": "http://www.w3.org/1999/xlink",
        "width": width,
        "height": height,
        "viewBox": "0 0 " + width + " " + height
    });
}

SVG.prototype = {
    set: function(element, attributes) {
        for (var key in attributes)
            element.setAttribute(key, attributes[key]);
    },
    
    draw : function(name, attributes, text) {
        var shape = document.createElementNS(this.namespace, name);
        this.set(shape, attributes);
        if (text !== undefined)
            shape.appendChild(document.createTextNode(text));
        this.element.appendChild(shape);
    }
}