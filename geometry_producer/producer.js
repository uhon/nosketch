var loadSvg = require("load-svg");
var parsePath = require("extract-svg-path").parse;
var svgMesh3d = require("svg-mesh-3d");
const THREE = require("three");
var createGeom = require("three-simplicial-complex")(THREE);
var writeFile = require('write');
var jsdom = require("jsdom").jsdom;
const fs = require('fs');
const measureTime = require('measure-time');

const jsonfile = require('jsonfile')


const sourceFolder = './../jvm/public/shapes';
// const sourceFolder = './../jvm/public/font-awesome/black/svg';
const targetFolder = './../jvm/public/shapes/vertices';


function parse(svgInput, filename) {
	const getSVGElapsed = measureTime();
	var svgPath = parsePath(svgInput);
	console.log("time to svgInput", getSVGElapsed());

	const getElapsedMesh3d = measureTime();
	var complex = svgMesh3d(svgPath, {
		delaunay: false,
		scale: 1,
		simplify: 50
	});
	console.log("time to mesh3d", getElapsedMesh3d());

	const getGeoElapsed = measureTime();
	var geo = createGeom(complex);
	console.log("time to geo", getGeoElapsed());

	var verticesJson = JSON.stringify(geo);

	// console.log(verticesJson);

	jsonfile.writeFileSync(targetFolder + "/" + filename + ".vert", { vertices: verticesJson });
}

fs.readdir(sourceFolder, (err, files) => {
	files.forEach(file => {

		if(file.endsWith(".svg")) {


		var svg = fs.readFile(sourceFolder + "/" + file, 'utf8', function (err, data) {
			if (err) {
				return console.log(err);
			}
			console.log("filename: " + file.slice(0, -4));
			parse(data, file.slice(0, -4));
		})
	}
})
});
