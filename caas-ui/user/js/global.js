require.config({

    urlArgs : "version=1.4.0",

    baseUrl : "/js/lib",

    paths: {
        app : "../app",
        md5 : "md5",
        "jquery.colorbox": "jquery.colorbox",
    },

    shim: {
    	"jquery.validator" : ["jquery", "jquery.validation"],
    	"jquery.colorbox" : ["jquery"],
    	"jquery.prompt" : ["jquery", "jquery.colorbox"]
    }
});