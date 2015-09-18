var wWidth = 1000;
var wHeight = 1000;
var cSize = 1000;
var defaultPlaygroundSize = 500;
var globalZoomFactor = 1;
var currentStrokeWidth = 5;
var currentPath;
var hexagon;
var innerCircle;
var innerCircleRadius;
var connectors = [];
// Containing Paths
var shapes = [];
var dragActive = false;
tool.minDistance = 1;



var resizeWindow = function() {
    // console.log("resize window...");
    //$("#drawing").width(defaultPlaygroundSize);
    //$("#drawing").height(defaultPlaygroundSize);

    wWidth = $(window).width();
    wHeight = $(window).height() -40; // minus the padding that is applied. a possible headers should also be taken in place
    // console.log("windowWidth: " + wWidth + ", windowHeight: ", wHeight);
    // console.log("oldZoomFactor: " + globalZoomFactor);

    var zWidth = wWidth / defaultPlaygroundSize;
    var zHeight = wHeight / defaultPlaygroundSize;
    cSize = 0;
    var newZoomFactor = 1;
    if(zWidth < zHeight) {
        cSize = wWidth;
        newZoomFactor = zWidth;
    } else {
        cSize = wHeight;
        newZoomFactor = zHeight;
    }
    // console.log("newZoomFactor: " + newZoomFactor);

    $("#canvasContainer").height(cSize);
    $("#canvasContainer").width(cSize + 200);
    $("#drawing").width(cSize);
    $("#drawing").height(cSize);

    view.viewSize = new Size(cSize, cSize);

    rescaleShapes(newZoomFactor / globalZoomFactor);
    redrawHexagon();
    redrawInnerCircle();
    redrawConnectors();
    globalZoomFactor = newZoomFactor;
};

var rescaleShapes = function(scaleFactor) {
    var exportedJson = "";
    $.each(shapes, function() {
        this.scale(scaleFactor);
        this.position *= scaleFactor;
        this.strokeWidth *= scaleFactor;
        exportedJson += this.exportJSON();
    });
    // console.log(exportedJson);
};

var redrawHexagon = function() {
    var mostRightCorner = new paper.Point(cSize / 2, cSize / 2);
    // console.log("draw hexagon with rightCorner: " + mostRightCorner);
    if(typeof(hexagon) !== "undefined") {
        hexagon.clear();
    }
    hexagon = new Path.RegularPolygon(mostRightCorner, 6, cSize / 2);
    hexagon.fillColor = '#e9e9ff';
};

var redrawInnerCircle = function() {
    if(typeof(innerCircle) !== "undefined") {
        innerCircle.clear();
    }
    innerCircleRadius = (1/2) * Math.sqrt(3) * cSize / 2;
    var center = new Point(cSize / 2);
    // console.log("redraw innerCircle with radius: " + innerCircleRadius + ", center: " + center);
    innerCircle = new Path.Circle(center, innerCircleRadius);
    innerCircle.fillColor = '#e9e9aa';
};

var redrawConnectors = function() {
    var vector = new Point(0) + new Point(innerCircleRadius, 0);
    // console.log("initial vector: " + vector);
    for(var i = 0; i < 6; i++) {
        var center = new Point(cSize / 2) + vector;
        // console.log("draw connector at center: " + center);
        var connector = new Path.Circle(center, innerCircleRadius / 30);
        //connector = connector.center = vector;
        connector.fillColor = "#336699";
        vector.angle += 60;
        connectors.push(connector);
    }
};

$(function() {
    // console.log("view size: " + view.size);
    // console.log("view viewSize: " + view.viewSize);
    // console.log("view center: " + view.center);
    resizeWindow();
    // console.log("view size: " + view.size);
    // console.log("view viewSize: " + view.viewSize);
    // console.log("view center: " + view.center);



    $(window).afterResize(resizeWindow);
});


function onMouseDown(event) {
    dragActive = true;
    // If we produced a path before, deselect it:
    if (typeof(currentPath) !== "undefined") {
        currentPath.selected = false;
    }

    currentPath = new Path();
    currentPath.strokeColor = 'black';
    currentPath.strokeWidth = currentStrokeWidth;

    // Select the path, so we can see its segment points:
    currentPath.fullySelected = true;
}

function onMouseDrag(event) {
    if(dragActive) {
        // Every drag event, add a point to the path at the current
        // position of the mouse:
        var x1 = event.point.x;
        var y1 = event.point.y;
        var x0 = paper.view.center.x;
        var y0 = paper.view.center.y;
        var distanceToCenter = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        if (distanceToCenter < innerCircleRadius) {
            currentPath.add(event.point);

            $.each(connectors, function () {
                if (this.hitTest(event.point) !== null) {
                    finishShape();
                }
            });
        }
    }
}

function onMouseUp(event) {
    finishShape();
}

function finishShape() {
    dragActive = false;
    // When the mouse is released, simplify it:
    currentPath.simplify();

    // console.log("path started at: " + currentPath.getPointAt(0));
    // console.log("path ended at: " + currentPath.getPointAt(currentPath.length - 1));

    // Select the path, so we can see its segments:
    currentPath.selected = true;

    shapes.push(currentPath);
}

