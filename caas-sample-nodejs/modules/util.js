var exports = module.exports = {};

exports.encrypt = function(text) {
    try {
        var cipher = require("crypto").createCipher("aes-256-cbc", "123456")
        var crypted = cipher.update(text, "utf8", "hex")
        crypted += cipher.final("hex");
        return crypted;
    } catch (err) {
        console.error(err);
        return null;
    }
}

exports.decrypt = function(text) {
    try {
        var decipher = require("crypto").createDecipher("aes-256-cbc", "123456")
        var dec = decipher.update(text, "hex", "utf8")
        dec += decipher.final("utf8");
        return dec;
    } catch (err) {
        console.error(err);
        return null;
    }
}

exports.hash = function(text) {
    return require("crypto").createHash("md5").update(text).digest("hex");
}