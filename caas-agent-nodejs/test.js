var crypto = require("crypto");
var agent = new require("./agent")("caas-user", 80, "/api/v1", "fe77618e57494ff291e64b778f70a9d6", "18c6557b1c3743199b062ad4a4896ff5");

agent.login("testuser10", crypto.createHash("md5").update("1").digest("hex"), null, null, function(err0, result0) {
	console.log("Login:");
	console.dir(result0);

	agent.auth(result0.auth_code, function(err, result) {
		console.log("Auth");
		console.dir(result);

		require("async").parallel({
			userInfo : function(notice) {
	            agent.getUserInfo(result.access_token, function(err1, result1) {
	            	console.log("Get user info:");
					console.dir(result1);
					notice();
				});
	        },
	        getAuthCode : function(notice) {
	            agent.getAuthCode(result.access_token, function(err1, result1) {
	            	console.log("Get auth_code:");
					console.dir(result1);
					notice();
				});
	        },
	        checkAuth : function(notice) {
	            agent.checkAuth(result.access_token, "/test-resource-1", "查询", function(err1, result1) {
	            	console.log("Check auth:");
					console.dir(result1);
					notice();
				});
	        },
	        batchCheckAuth : function(notice) {
	        	agent.batchCheckAuth(result.access_token, ["/test-resource-1", "/test-resource-2", "/test-resource-3"], "查询", function(err1, result1) {
					console.log("Batch check auth:");
					console.dir(result1);
					notice();
				});
	        },
	        getRolesBySubject : function(notice) {
	        	var subjectCode = "2";
	        	agent.getRolesBySubject(result.access_token, subjectCode, function(err1, result1) {
	        		console.log("get roles by subject, subjectCode is:" + subjectCode);
					console.dir(result1);
					notice();
	        	});
	        },
	        base64Vcode : function(notice) {
	        	agent.base64Vcode(null, function(err1, result1) {
					console.log("Base64 vcode:");
					console.dir(result1);
					notice();
				});
	        },
	        validateUserName : function(notice) {
	        	agent.validateUserName("testuser14", null, function(err1, result1) {
					console.log("Validate user name:");
					console.dir(result1);
					notice();
				});
	        },
	        validateEmail : function(notice) {
	        	agent.validateEmail("test@test.com", null, function(err1, result1) {
					console.log("Validate email:");
					console.dir(result1);
					notice();
				});
	        },
	        validateMobile : function(notice) {
	        	agent.validateMobile("13411111110", null, function(err1, result1) {
					console.log("Validate mobile:");
					console.dir(result1);
					notice();
				});
	        },
	        validateVcode : function(notice) {
	        	agent.validateVcode("xxxx", null, function(err1, result1) {
					console.log("Validate vcode:");
					console.dir(result1);
					notice();
				});
	        }
	    }, function() {
	        agent.refreshToken(result.refresh_token, function(err2, result2) {
				console.log("Refresh token:");
				console.dir(result2);
				agent.resetPassword("testuser10", crypto.createHash("md5").update("1").digest("hex"), function(err3, result3) {
					console.log("Reset password:");
					console.dir(result3);

					agent.login("testuser10", crypto.createHash("md5").update("1").digest("hex"), null, null, function(err4, result4) {
						console.log("Login:");
						console.dir(result4);
						agent.auth(result4.auth_code, function(err5, result5) {
							console.log("Auth:");
							console.dir(result5);
							agent.logout(result5.access_token, function(err6, result6) {
								console.log("Logout:");
								console.dir(result6);			
							});
						});
					});
				});
			});
	    });
	});
});






