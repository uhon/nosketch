 module.exports = {
   "loadSvg": require("load-svg"),
   "parsePath": require("extract-svg-path").parse,
   "svgMesh3d": require("svg-mesh-3d"),
   "createGeom":  require("three-simplicial-complex")(THREE),
     "jsdom":  require("jsdom").jsdom
 };

