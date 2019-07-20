var exports = module.exports = {};

exports.ok = function(res, msg) {
    end(200, res, msg);
}

exports.created = function(res, msg) {
    end(201, res, msg);
}

exports.notModified = function(res) {
    end(304, res);
}

exports.badRequest = function(res, msg) {
    end(400, res, msg);
}

exports.unauthorized = function(res, msg) {
    res.setHeader("Content-Length", 0);
    res.setHeader("WWW-Authenticate", "none");
    end(401, res, msg);
}

exports.forbidden = function(res, msg) {
    end(403, res, msg);
}

exports.notFound = function(res, msg) {
    end(404, res, msg);
}

exports.conflict = function(res, msg) {
    end(409, res, msg);
}

exports.locked = function(res, msg) {
    end(423, res, msg);
}

exports.error = function(res, msg) {
    end(500, res, msg);
}

function end(statusCode, res, msg) {
    res.statusCode = statusCode;
    if(msg) {
        res.send(msg);
    } else {
        res.end();
    }
}