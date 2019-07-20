require.config({

    urlArgs : "version=1.4.0",

    baseUrl : "/js/lib",

    paths: {
        app : "../app",
        backbone : "backbone",
        underscore : "underscore",
        text : "text",
        "jquery.colorbox" : "jquery.colorbox"
    },

    shim: {
        "jquery.validator" : ["jquery.validation"],
        "jquery.verifiable" : ["jquery.validator"],
        "jquery.colorbox" : ["jquery"],
      	"backbone" : ["underscore", "jquery"],
      	"app/router": ["backbone"],
        "app/popup/router": ["backbone"]
    }
});
