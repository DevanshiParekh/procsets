// API Paths
var api = {};
api.query1 = "/facesix/services/demo.txt";
api.query2 = "/facesix/services/queries.txt";
api.query3 = "/facesix/services/columns.txt";
api.query4 = "/facesix/services/verticals.txt";
api.query5 = "http://demo.progsets.io/progsets/rest/psql/exe?return.as=verticals";
api.visualquery= "http://demo.progsets.io/progsets/rest/psql/exe";
api.base = "/facesix/rest/fsql/ssql?";
api.auth = "Basic admin:nimda";

function millisToMinutesAndSeconds(millis) {
    var minutes = Math.floor(millis / 60000);
    var seconds = ((millis % 60000) / 1000).toFixed(0);
    return minutes + ":" + (seconds < 10 ? '0' : '') + seconds;
}

// Global params
var global = {};
global.timeout = 1000*10;
global.timeoutinmutes = millisToMinutesAndSeconds(global.timeout);
