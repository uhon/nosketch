hexagon = undefined;

var redrawHexagon = function(canvasSize) {
    console.log("draw hexagon on canvas size: " + canvasSize);
    var hexagonSize = canvasSize / 4;
    var mostRightCorner = new Point(canvasSize / 2 - hexagonSize, canvasSize / 2);
    console.log("draw hexagon with rightCorner: " + mostRightCorner);
    if(typeof(hexagon) !== "undefined") {
        hexagon.clear();
    }
    hexagon = new Path.RegularPolygon(mostRightCorner, 6, 20);
    hexagon.fillColor = '#e9e9ff';
    hexagon.lineWidth = 10;
    //hexagon.rotate(30);
    //hexagon.selected = true;
};

$(function() {
    // setup view
    paper.view.viewSize = [4000, 4000];
    $("#drawing").width(4000);
    $("#drawing").height(4000);
    var zoomFactor=1;

    var cWidth = $("#canvasContainer").width();
    var cHeight = $(window).height();
    console.log("containerWidth: " + cWidth + ", containerHeight: ", cHeight);

    var zWidth = cWidth / 4000;
    var zHeight = cHeight / 4000;

    if(zWidth < zHeight) {
        zoomFactor = zWidth;
    } else {
        zoomFactor = zHeight;
    }

    console.log("zoomFactor: " + zoomFactor);


    paper.view.zoom = zoomFactor;
    var newSize = Math.abs(4000 * zoomFactor);
    paper.view.viewSize = new Size(newSize, newSize);
    $("#drawing").width(newSize);
    $("#drawing").height(newSize);

    $("#drawing").show();

    redrawHexagon(4000);

    console.log(paper.view.size);



    window.onresize = function() {
        ////redrawHexagon(hexagon);
        ////console.log(view.viewSize);
        ////view.scale([ $(window).width(), $(window).height() ]);
        //console.log(view.viewSize);
        //console.log(hexagon);
        //paper.view.zoom = 2;
        //console.log(view.viewSize);
        //console.log(hexagon);
    };
});

var path;

var textItem = new PointText(new Point(20, 30));
textItem.fillColor = 'black';
textItem.content = 'Click and drag to draw a line.';

function onMouseDown(event) {
    // If we produced a path before, deselect it:
    if (path) {
        path.selected = false;
    }

    path = new Path();
    path.strokeColor = 'black';

    // Select the path, so we can see its segment points:
    path.fullySelected = true;
}

function onMouseDrag(event) {
    // Every drag event, add a point to the path at the current
    // position of the mouse:
    path.add(event.point);

    textItem.content = 'Segment count: ' + path.segments.length;
}

function onMouseUp(event) {
    var segmentCount = path.segments.length;

    // When the mouse is released, simplify it:
    path.simplify();
    console.log("path started at: " + path.getPointAt(0));
    console.log("path ended at: " + path.getPointAt(path.length - 1));

    // Select the path, so we can see its segments:
    path.selected = true;

    var newSegmentCount = path.segments.length;
    var difference = segmentCount - newSegmentCount;
    var percentage = 100 - Math.round(newSegmentCount / segmentCount * 100);
    textItem.content = difference + ' of the ' + segmentCount + ' segments were removed. Saving ' + percentage + '%';

}

