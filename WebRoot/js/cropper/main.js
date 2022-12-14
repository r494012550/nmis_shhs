//$(function(){

	//'use strict';

  	function CropAvatar($element,uploadurl,aspectRatio,callback) {
  		//console.log(uploadurl)
  		this.$container = $element;
  		this.$uploadurl=uploadurl;
  		this.$aspectRatio=aspectRatio;
  		this.$avatarView = this.$container.find('.avatar-view');
  		this.$avatar = this.$avatarView.find('img');
  		this.$avatarModal = this.$container.find('#avatar-modal');
  		this.$loading = this.$container.find('.loading');
  		this.$closebtn=this.$container.find('#closebtn');
  		this.$donebtn=this.$container.find('#donebtn');
  		this.$avatarForm = this.$avatarModal.find('.avatar-form');
  		this.$avatarUpload = this.$avatarForm.find('.avatar-upload');
  		this.$avatarSrc = this.$avatarForm.find('.avatar-src');
  		this.$avatarData = this.$avatarForm.find('.avatar-data');
//  		this.$avatarInput = this.$avatarForm.find('#avatarInput');
  		this.$avatarInput = this.$avatarForm.find("[type='file']");
  		
  		this.$avatarSave = this.$avatarForm.find('.avatar-save');
  		this.$avatarBtns = this.$avatarForm.find('.avatar-btns');
  		this.$avatarWrapper = this.$avatarModal.find('.avatar-wrapper');
  		this.$avatarPreview = this.$avatarModal.find('.avatar-preview');
  		this.$callback=callback;
  		this.init();
	}

  CropAvatar.prototype = {
    constructor: CropAvatar,

    support: {
      fileList: !!$('<input type="file">').prop('files'),
      blobURLs: !!window.URL && URL.createObjectURL,
      formData: !!window.FormData
    },

    init: function () {
      this.support.datauri = this.support.fileList && this.support.blobURLs;

      if (!this.support.formData) {
        this.initIframe();
      }

      this.initTooltip();
      this.initModal();
      this.addListener();
    },

    addListener: function () {
      this.$avatarView.on('click', $.proxy(this.click, this));
      this.$avatarInput.on('change', $.proxy(this.change, this));//;
      this.$avatarForm.on('submit', $.proxy(this.submit, this));
      this.$avatarBtns.bind('click', $.proxy(this.rotate, this));
      this.$closebtn.bind('click', $.proxy(this.close, this));
      this.$donebtn.bind('click', $.proxy(this.submit, this));
    },

    initTooltip: function () {
    	if(this.$avatarModal.attr("class")&&this.$avatarModal.attr("class").indexOf('easyui-dialog')>=0){
    		this.$avatarView.tooltip({
    			placement: 'bottom'
    		});
    	}
    },

    initModal: function () {
//      this.$avatarModal.modal({
//        show: false
//      });
    },

    initPreview: function () {
      var url = this.$avatar.attr('src');
      this.$avatarPreview.empty().html('<img src="' + url + '">');
    },

    initIframe: function () {
      var target = 'upload-iframe-' + (new Date()).getTime(),
          $iframe = $('<iframe>').attr({
            name: target,
            src: ''
          }),
          _this = this;

      // Ready ifrmae
      $iframe.one('load', function () {

        // respond response
        $iframe.on('load', function () {
          var data;

          try {
            data = $(this).contents().find('body').text();
          } catch (e) {
//            console.log(e.message);
          }

          if (data) {
            try {
              data = $.parseJSON(data);
            } catch (e) {
//              console.log(e.message);
            }

            _this.submitDone(data);
          } else {
            _this.submitFail('Image upload failed!');
          }

          _this.submitEnd();

        });
      });

      this.$iframe = $iframe;
      this.$avatarForm.attr('target', target).after($iframe.hide());
    },

    click: function () {
    	if(this.$avatarModal.attr("class")&&this.$avatarModal.attr("class").indexOf('easyui-dialog')>=0){
    		this.$avatarModal.dialog('open');
    	}
    	else{
			layer.open({
  			  type: 1,
  			  title:'????????????',
  			  resize:false,
  			  maxWidth:740,
  			  content: this.$avatarModal 
  			});
    	}
    	this.initPreview();
    },
    
    close:function(){
    	if(this.$avatarModal.attr("class")&&this.$avatarModal.attr("class").indexOf('easyui-dialog')>=0){
    		this.$avatarModal.dialog('close');
    	}
    	else{
    		layer.close(layer.index);
    	}
    },

    change: function () {
    	
      var files,
          file;

      if (this.support.datauri) {
        files = this.$avatarInput.prop('files');

        if (files.length > 0) {
          file = files[0];

          if (this.isImageFile(file)) {
            if (this.url) {
              URL.revokeObjectURL(this.url); // Revoke the old one
            }

            this.url = URL.createObjectURL(file);
            this.startCropper();
          }
        }
      } else {
        file = this.$avatarInput.val();

        if (this.isImageFile(file)) {
          this.syncUpload();
        }
      }
    },

    submit: function () {
      if (!this.$avatarSrc.val() && !this.$avatarInput.val()) {
    	  
    	  $.messager.show({
  			title : '??????',
  			msg : "????????????????????????",
  			timeout : 1000,
  			border:'thin',
  			showType : 'slide'
  		});
    	  
        return false;
      }

      if (this.support.formData) {
        this.ajaxUpload();
        return false;
      }
    },

    rotate: function (e) {
    	var data;
    	if (this.active) {
    		data = $(e.target).data();
    		console.log(data)
    		if(!data.method){
    			data=$(e.target).parent().parent().data();
    		}
    		this.$img.cropper(data.method, data.option);
    	}
    },

    isImageFile: function (file) {
      if (file.type) {
        return /^image\/\w+$/.test(file.type);
      } else {
        return /\.(jpg|jpeg|png|gif)$/.test(file);
      }
    },

    startCropper: function () {
      var _this = this;

      if (this.active) {
        this.$img.cropper('replace', this.url);
      } else {
        this.$img = $('<img src="' + this.url + '">');
        this.$avatarWrapper.empty().html(this.$img);
        this.$img.cropper({
          aspectRatio: this.$aspectRatio,
          preview: '.avatar-preview',//this.$avatarPreview.selector,
          strict: false,
          crop: function (data) {
            var json = [
                  '{"x":' + data.x,
                  '"y":' + data.y,
                  '"height":' + data.height,
                  '"width":' + data.width,
                  '"rotate":' + data.rotate + '}'
                ].join();

            _this.$avatarData.val(json);
          }
        });

        this.active = true;
      }
    },

    stopCropper: function () {
      if (this.active) {
        this.$img.cropper('destroy');
        this.$img.remove();
        this.active = false;
      }
    },

    ajaxUpload: function () {
	    var dataURL = this.$img.cropper("getCroppedCanvas");
	    var	data=dataURL.toDataURL("image/jpeg", 0.5);
	    var url = this.$uploadurl;//window.localStorage.ctx+"/profile/updateSign",
	//          data = new FormData(this.$avatarForm[0]),
	    _this = this;
	      
	      $.ajax(url, {
	        type: 'post',
	        data: {imgBase64:data.toString()},
	        dataType: 'json',
	//        processData: false,
	//        contentType: false,
	
	        beforeSend: function () {
	          _this.submitStart();
	        },
	
	        success: function (data) {
	          _this.submitDone(data);
	        },
	
	        error: function (XMLHttpRequest, textStatus, errorThrown) {
	          _this.submitFail(textStatus || errorThrown);
	        },
	
	        complete: function () {
	          _this.submitEnd();
	        }
	      });
    },

    syncUpload: function () {
      this.$avatarSave.click();
    },

    submitStart: function () {
      this.$loading.fadeIn();
    },

    submitDone: function (data) {
    	var json =validationDataAll(data);
    	if(json.code==0){
    		this.$closebtn.click();
    		this.$avatar.attr('src',json.data);
    		if(this.$callback){
    			this.$callback();
    		}
    	}
    	else{
    		$.messager.show({
    			title : '??????',
    			msg : "?????????????????????????????????????????????????????????????????????????????????",
    			timeout : 1000,
    			border:'thin',
    			showType : 'slide'
    		});
    	}
    	
//      console.log(data);

//      if ($.isPlainObject(data) && data.state === 200) {
//        if (data.result) {
//          this.url = data.result;
//
//          if (this.support.datauri || this.uploaded) {
//            this.uploaded = false;
//            this.cropDone();
//          } else {
//            this.uploaded = true;
//            this.$avatarSrc.val(this.url);
//            this.startCropper();
//          }
//
//          this.$avatarInput.val('');
//        } else if (data.message) {
//          this.alert(data.message);
//        }
//      } else {
//        this.alert('Failed to response');
//      }
    },

    submitFail: function (msg) {
      this.alert(msg);
    },

    submitEnd: function () {
      this.$loading.fadeOut();
    },

    cropDone: function () {
      this.$avatarForm.get(0).reset();
      this.$avatar.attr('src', this.url);
      this.stopCropper();
      this.$avatarModal.modal('hide');
    },

    alert: function (msg) {
      var $alert = [
            '<div class="alert alert-danger avater-alert">',
              '<button type="button" class="close" data-dismiss="alert">&times;</button>',
              msg,
            '</div>'
          ].join('');

      this.$avatarUpload.after($alert);
    }
  };
  
  //var es_cropAvatar=new CropAvatar($('#crop-avatar'));
//});


