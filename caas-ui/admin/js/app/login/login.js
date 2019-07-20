define([ "md5", "jquery", "jquery.utility", "jquery.validator","jquery.prompt" ], function(md5, $) {
	function _init() {

		$(".admintype").on("click",function(){
      var val=$(this).text();
			 $("#admintypeval").text(val);
			 if(val=="管理员"){
				 $("#isSuperAdmin").attr("value",false);
			  }

			 if(val=="超级管理员"){
				$("#isSuperAdmin").attr("value",true);
		  	}

		});
		$("#login").on("click", function() {
			$("#loginError").hide();
			if($("form").validate()) {
				//check isSuperAdmin, must selected.
				var isSuperAdmin= $("#isSuperAdmin").val();
				if(isSuperAdmin==""){
					$.alert("请选择管理员类型",function(){
						console.log("Pls chose the admin type");
					});
					return false;
				}
				var form = $("form[name='login']");
				var data = form.serializeObject();
				//ipa user password no need to hash
       var enableipa=$("#enableipa").val();
				  if(!enableipa || (enableipa && !$("#domainUser").prop("checked"))){
				  		data.password = hex_md5(data.password);
				  }
					if($("#domainUser").prop("checked")){
					data.domainUser=true;
				}
					else{
						data.domainUser=false;
					}
				$.ajax({
					url : form.attr("action"),
					type : form.attr("method"),
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(result, textStatus, xhr) {

						if(result && !result.success){
								$("#loginError").show();
								return false;
						}
						require([ "app/components/nav/nav-view" ], function(navView) {
							navView.reset();
							navView.refresh(function() {
								var currentUrl = window.location.href;
								if(currentUrl.match(new RegExp("#home$")) || currentUrl.match(new RegExp("#$")) || currentUrl.indexOf("#") < 0) {
									Backbone.history.loadUrl(Backbone.history.fragment);
								} else {
									window.location.href = "#";
								}
							});
						});


					},
					error : function(xhr, textStatus, errorThrown) {
						if(xhr.status === 401) {
							$("#loginError").show();
						}
						if(xhr.status === 400) {
							$("#loginError").show();
						}
						if(xhr.status === 404) {
							$("#loginError").show();
						}
					}
				});
			}
		});

_checkIPA();

	}

	function _checkIPA(){

		$.ajax({
					url : '/ipa/config',
					type : 'GET',
					contentType : "application/json",
					success : function(result, textStatus, xhr) {

				  $("#enableipa").attr("value",result.enabled);
					if(result.enabled){
             $("#domain-panel").show();
					}else{
						$("#domain-panel").hide();
					}
					},
					error : function(xhr, textStatus, errorThrown) {
						if(xhr.status === 401) {
							$("#loginError").show();
						}
					}
				});
	}
	return {
		initialize : _init
	}
});
