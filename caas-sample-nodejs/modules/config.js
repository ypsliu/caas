//var env = process.env.NODE_ENV || "app1";

var config = {
    app1 : {
        caas: {
            host : "caas-user",
            port : 80,
            urlPrefix : "/api/v1",
            appKey : "fe77618e57494ff291e64b778f70a9d6",
            appSecret : "18c6557b1c3743199b062ad4a4896ff5"
        },
        server : {
            host : "caas-sample-1",
            port : 3008
        }
    },
    app2 : {
        caas: {
            host : "caas-user",
            port : 80,
            urlPrefix : "/api/v1",
            appKey : "2b32de779cc04d818cc1461e8921e941",
            appSecret : "a359940c1d154366b162df47cb2b4929"
        },
        server : {
            host : "caas-sample-2",
            port : 3009
        }
    }
}

var exports = module.exports = function(env) {
    if(!env) {
        env = process.env.NODE_ENV || "app1";
    }
    return config[env];
};