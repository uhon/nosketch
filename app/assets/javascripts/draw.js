var wWidth = 500;
var wHeight = 500;
var cSize = 500;
var defaultPlaygroundSize = 500;
var globalZoomFactor = 1;
var currentStrokeWidth = 5;
var currentPath;
var hexagon;
var innerCircle;
var innerCircleRadius;
// Containing Paths
var shapes = [];



var resizeWindow = function() {
    console.log("resize window...");
    //$("#drawing").width(defaultPlaygroundSize);
    //$("#drawing").height(defaultPlaygroundSize);

    wWidth = $(window).width();
    wHeight = $(window).height() -40; // minus the padding that is applied. a possible headers should also be taken in place
    console.log("windowWidth: " + wWidth + ", windowHeight: ", wHeight);
    console.log("oldZoomFactor: " + globalZoomFactor);

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
    console.log("newZoomFactor: " + newZoomFactor);

    currentStrokeWidth *= newZoomFactor / globalZoomFactor;

    $("#canvasContainer").height(cSize);
    $("#canvasContainer").width(cSize + 200);
    $("#drawing").width(cSize);
    $("#drawing").height(cSize);

    view.viewSize = new Size(cSize, cSize);

    rescaleShapes(newZoomFactor / globalZoomFactor);
    redrawHexagon();
    redrawInnerCircle();
    globalZoomFactor = newZoomFactor;
};

var rescaleShapes = function(zoomFactor) {
    var exportedJson = "";
    $.each(shapes, function() {
        this.scale(zoomFactor);
        this.position *= zoomFactor;
        this.strokeWidth *= zoomFactor;
        exportedJson += this.exportJSON();
    });
    console.log(exportedJson);
};

var redrawHexagon = function() {
    var mostRightCorner = new paper.Point(cSize / 2, cSize / 2);
    console.log("draw hexagon with rightCorner: " + mostRightCorner);
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
    console.log("redraw innerCircle with radius: " + innerCircleRadius + ", center: " + center);
    innerCircle = new Path.Circle(center, innerCircleRadius);
    innerCircle.fillColor = '#e9e9aa';
};

$(function() {
    console.log("view size: " + view.size);
    console.log("view viewSize: " + view.viewSize);
    console.log("view center: " + view.center);
    resizeWindow();
    console.log("view size: " + view.size);
    console.log("view viewSize: " + view.viewSize);
    console.log("view center: " + view.center);



    $(window).afterResize(resizeWindow);
});


function onMouseDown(event) {
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
    // Every drag event, add a point to the path at the current
    // position of the mouse:
    var x1 = event.point.x;
    var y1 = event.point.y;
    var x0 = paper.view.center.x;
    var y0 = paper.view.center.y;
    var distanceToCenter = Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
    var r = innerCircleRadius;
    //console.log("x1: " + x1 + " " + "y1: " + y1 + " " + "x0: " + x0 + " " + "y0: " + y0);
    //console.log("distanceToCenter: " + distanceToCenter);
    if(distanceToCenter < r) {
        currentPath.add(event.point);
    }

}

function onMouseUp(event) {
    var segmentCount = currentPath.segments.length;

    // When the mouse is released, simplify it:
    currentPath.simplify();

    console.log("path started at: " + currentPath.getPointAt(0));
    console.log("path ended at: " + currentPath.getPointAt(currentPath.length - 1));

    // Select the path, so we can see its segments:
    currentPath.selected = true;

    shapes.push(currentPath);
}

