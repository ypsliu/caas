/**
 * Created by Wang Shu Guang on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable","jquery.prompt","jquery.showLoading","jquery-ui"], function($) {
    var _uploader;
    function _init() {
        $("#resCreateError").hide();
         _initMatcherDefine();
        //  _initUploadify();
        _initFileUploader();
         _initUploadResult();
         $("#startUploadBtn").click(function(){
           // _uploadify();
          $("#upload_result").hide();
          $("#errormsg").empty();
          _uploadFile();
         });


    };
function _uploadFile(){
  //check matchtype
  var val=$('input:radio[name="matchtype"]:checked').val();
   if(val==null){
       $.alert("请先定义字段的匹配",function(){
           console.log("请先定义字段的匹配");
       });
       return false;
    }
   if(val=='matchtype_header'){
     var valid_header=true;
      $(".matchfield_header").each(function(){
        var value=$(this).val();
        if(value==""||value==null){
          $.alert("匹配字段填写不完整",function(){
              console.log("匹配字段填写不完整");
          });
          valid_header=false;
          return false;
        }
      });
      if(!valid_header){
        return false;
      }
   }
   if(val=='matchtype_order'){
     var valid_order=true;
     $(".matchfield_order").each(function(){
       var value=$(this).val();
       if(value==""||value==null){
         $.alert("匹配字段填写不完整",function(){
             console.log("匹配字段填写不完整");
         });
        valid_order=false;
         return false;
       }
     });
     if(!valid_order){
       return false;
     }
   }

   var filename=$("#csvfile").val();
   if(filename==null||filename==""){
     $.alert("请先选择文件",function(){
         console.log("请先选择文件");
     });
     return false;
   }

    _uploader.options.formData = _buildParameters();
    var files =_uploader.getFiles();
   if(files.length==0){
     $.alert("请重新选择文件",function(){
         console.log("请重新选择文件");
     });
     return false;
   }
    $("body").showLoading();
    _uploader.upload();
  }

  /**初始化file uploader*/
   function _initFileUploader(){
      var appCode=$("input[name='appCode']").val();
       require(["webuploader"], function(WebUploader) {
        _uploader = WebUploader.create({
         swf: '/images/Uploader.swf',
         server: '/api/v1/admin/app/'+appCode+'/userUpload',
         pick: '#picker_file',
         accept: {
             title: 'file',
             extensions: 'csv',
             mimeTypes:'.csv'
         }
       });
       _uploader.on('uploadComplete',function(file){
         console.log(file);
         $("body").hideLoading();
       });
       _uploader.on('uploadSuccess',function(file,data){
        _processUploadResult(data);
        //remove all file in queue after uploading
        var files =_uploader.getFiles();
        for(var i=0;i<files.length;i++){
          _uploader.removeFile(files[i],true);
        }
            _uploader.reset();
        var files =_uploader.getFiles();

         $("body").hideLoading();
      //  _uploader.reset();

       });

       _uploader.on('select',function(file){
         console.log('select');
       });

       _uploader.on('fileQueued',function(file){
         $("#csvfile").attr("value",file.name);
       });

      });



    }
//     function _uploadfile(){
// $("#uploadify").
//     }

   function _processUploadResult(data){
     $("#errormsg").empty();


     var issuccess=data.success;
     if(!issuccess ||issuccess=='false'){
        $("#errormsg").append("用户导入错误");
        return false;
     }
     $("#upload_result").show();
     var resultCode=data.resultCode;
     var failedArray =data.failed;
     var insertArray =data.inserted;
     var existedArray =data.existed;
     var updatedArray =data.updated;
     var invalidArray =data.invalid;

     var noOfInserted=data.noOfInserted;
     var noOfFailed=data.noOfFailed;
     var noOfUpdated=data.noOfUpdated;
     var noOfExisted=data.noOfExisted;
     var noOfInvalid=data.noOfInvalid;
        $("#noOfInsert").empty();
        $("#noOfFailed").empty();
        $("#noOfUpdated").empty();
        $("#noOfExisted").empty();
        $("#noOfInvalid").empty();

     $("#noOfInsert").append('导入成功-'+noOfInserted);
     $("#noOfFailed").append('导入失败-'+noOfFailed);
     $("#noOfUpdated").append('更新记录-'+noOfUpdated);
     $("#noOfExisted").append('重复记录-'+noOfExisted);
     $("#noOfInvalid").append('无效数据-'+noOfInvalid);

     $("#result_inserted").empty();
    $("#result_failed").empty();
    $("#result_updated").empty();
    $("#result_existed").empty();
    $("#result_invalid").empty();
    if(resultCode){
      var url="/api/v1/admin/userupload/"+resultCode+"/";

      $("#result_inserted").append($("<a href='"+url+"inserted"+"'>查看详细列表</a>"));
      $("#result_failed").append($("<a href='"+url+"failed"+"'>查看详细列表</a>"));
      $("#result_updated").append($("<a href='"+url+"updated"+"'>查看详细列表</a>"));
      $("#result_existed").append($("<a href='"+url+"existed"+"'>查看详细列表</a>"));
      $("#result_invalid").append($("<a href='"+url+"invalid"+"'>查看详细列表</a>"));
    }else{
      $("#result_inserted").append($("<div style='color:red'>获取结果发生错误</div>"));
     $("#result_failed").append($("<div style='color:red'>获取结果发生错误</div>"));
     $("#result_updated").append($("<div style='color:red'>获取结果发生错误</div>"));
     $("#result_existed").append($("<div style='color:red'>获取结果发生错误</div>"));
     $("#result_invalid").append($("<div style='color:red'>获取结果发生错误</div>"));
    }



    // if(insertArray){
    //   //  $("#noOfInsert").append('导入成功-'+noOfInserted);
    //     $("#result_inserted").empty();
    //     var $table=$("<table>");
    //     $table.addClass("aui aui-table-sortable");
    //     $table.css("font-size","8px");
    //     var $header=$('<thead><tr><th width="10%">编码</th><th width="20%">姓名</th><th width="30%">邮箱</th><th width="20%">手机号</th></tr></thead>');
    //     $table.append($header);
    //     var $body=$("<tbody>");
    //     for(var i=0;i<noOfInserted;i++){
    //     var $row=$("<tr>");
    //     $row.append($("<td>"+insertArray[i].originCode+"</td>"));
    //     $row.append($("<td>"+insertArray[i].name+"</td>"));
    //     $row.append($("<td>"+insertArray[i].email+"</td>"));
    //     $row.append($("<td>"+insertArray[i].mobile+"</td>"));
    //     $body.append($row);
    //     }
    //     $table.append($body);
    //     $("#result_success").append($table);
    // }
    // if(failedArray){
    //   $("#result_failed").empty();
    //   var $table=$("<table>");
    //   $table.addClass("aui aui-table-sortable");
    //   $table.css("font-size","8px");
    //   var $header=$('<thead><tr><th width="10%">编码</th><th width="20%">失败原因</th><th width="20%">姓名</th><th width="20%">邮箱</th><th width="10%">手机号</th></tr></thead>');
    //   $table.append($header);
    //   var $body=$("<tbody>");
    //   for(var i=0;i<noOfFailed;i++){
    //   var $row=$("<tr>");
    //   $row.append($("<td>"+failedArray[i].originCode+"</td>"));
    //   $row.append($("<td>"+failedArray[i].comment+"</td>"));
    //   $row.append($("<td>"+failedArray[i].name+"</td>"));
    //   $row.append($("<td>"+failedArray[i].email+"</td>"));
    //   $row.append($("<td>"+failedArray[i].mobile+"</td>"));
    //   $body.append($row);
    //   }

    //   $table.append($body);
    //   $("#result_failed").append($table);
    // }
   }

    function _uploadify(){
     $('#uploadify').uploadify('settings','formData',JSON.stringify(param));
     $('#uploadify').uploadify('upload','*');

    }
    function _buildParameters(){
            var matchtype= $('input[name="matchtype"]:checked').val();
            var param={};
            param.matchtype=matchtype;
            if(matchtype=='matchtype_header'){
               param.user_originCode=$("#column_code").val();
               param.user_name=$("#column_name").val();
               param.user_password=$("#column_password").val();
               param.user_email=$("#column_email").val();
               param.user_mobile=$("#column_mobile").val();

            }else if(matchtype=="matchtype_order"){
              param.user_originCode=$("#order_1").val();
              param.user_name=$("#order_2").val();
              param.user_password=$("#order_3").val();
              param.user_email=$("#order_4").val();
              param.user_mobile=$("#order_5").val();
            }
            return param;
    }
    function _initUploadify(){
      var appCode=$("input[name='appCode']").val();
      $("#uploadify").uploadify({
        'formData'      : {'someKey' : 'someValue', 'someOtherKey' : 1},
        'swf'           : '/images/uploadify.swf',
        'buttonText'    :  '请选择文件...',
        'uploader'      : '/api/v1/admin/app/'+appCode+'/userUpload',
        'auto'          : false,
        'fileObjName'   : 'file',
        'cancelImg'     : '/images/cancel.png',
        'fileTypeExts'  : '*.csv',
        'width'         : 120,
        'onSelect'      : function(file) {
             console.log("choose file:"+JSON.stringify(file));
         },
        'onComplete'    :function(event, queueID, fileObj, response, data){
          console.log("done");
        },
        'onError'       :function(event, queueID, fileObj){
            console.log("onError");
        },
        'onCancel'      :function(event, queueID, fileObj){
              console.log("onCancel ");
        },
        'onUploadSuccess':function(fileObj, data, response){
            console.log(JSON.stringify(data));
        },
        'onQueueComplete':function(){
            console.log("onQueueComplete");
        }
      });
    }


    function _initMatcherDefine(){
      //matching define
      $(".matchfield_header").attr("disabled",true);
      $(".matchfield_order").attr("disabled",true);

      $("#matchtype_header").click(function(){
      $(".matchfield_header").attr("value","");
      $(".matchfield_order").attr("value","");
      $("#column_code").attr("value","code");
      $("#column_name").attr("value","name");
      $("#column_password").attr("value","password");
      $("#column_email").attr("value","email");
      $("#column_mobile").attr("value","mobile");
      $(".matchfield_header").attr("disabled",true);
      $(".matchfield_order").attr("disabled",true);
      $(".matchfield_header").attr("disabled",false);
    });
    $("#matchtype_order").click(function(){
      $(".matchfield_header").attr("value","");
      $(".matchfield_order").attr("value","");
      $("#order_1").attr("value","1");
      $("#order_2").attr("value","2");
      $("#order_3").attr("value","3");
      $("#order_4").attr("value","4");
      $("#order_5").attr("value","5");

      $(".matchfield_header").attr("disabled",true);
      $(".matchfield_order").attr("disabled",true);
      $(".matchfield_order").attr("disabled",false);
    });


    }
    function _initUploadResult(){
      //upload result ui
      var icons = {
      header: "ui-icon-circle-arrow-e",
      activeHeader: "ui-icon-circle-arrow-s"
       };
      $( "#upload_result" ).accordion({
         heightStyle: "content",
         icons: icons
      });
     $( "#upload_result" ).hide();
    }
    function save(){
         $("#saveBtn").bind("click",function(){
         $('#file').uploadifyUpload();
        });
    }
      return {
        initialize : _init
    }
});
