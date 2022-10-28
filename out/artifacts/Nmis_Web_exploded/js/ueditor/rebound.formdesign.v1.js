UE.plugins['numberinput'] = function () {
    var me = this,thePlugins = 'numberinput';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.numberbox'),
				width : 500,height : 620,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/number.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
//				onClose:function(){
//					
//				}
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					
    					console.log(popup.anchorEl.getAttribute('iuid'))
    					
    					if(popup.anchorEl.getAttribute('iuid')){
    						var nextlabel=$(popup.anchorEl).next("button[for='"+popup.anchorEl.getAttribute('iuid')+"']");
    						if(nextlabel){
    							nextlabel.remove();
    						}
    					}
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    					
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&"number"==el.getAttribute("type")) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.numberbox')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('numberinput_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('numberinput');
        	//createInserText();
        }
    });
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.insertnumberbox'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -750px -101px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
0
);

UE.plugins['article'] = function () {
    var me = this,thePlugins = 'article';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
//        	var dialog=$('#common_dialog').dialog({
//				title : $.i18n.prop('admin.combobox'),
//				width : 600,height : 680,border: 'thin',
//				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
//				href : this.options.UEDITOR_HOME_URL +'formdesign/select.jsp',
//				buttons:[{
//					text: $.i18n.prop('save'),
//					className:'edui-okbutton',
//					width:80,
//					handler:function(){onok(me);}
//				},{
//					text: $.i18n.prop('cancel'),
//					className:'edui-cancelbutton',
//					width:80,
//					handler:function(){oncancel();$('#common_dialog').dialog('close');}
//				}]//,
//				//onLoad:onload_init
//        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /article/ig.test( el.tagName )&&el.getAttribute("name")=='srsection') {
            var html = popup.formatHtml(
                '<nobr>章节: <span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
                $(el).css({"border":"1px solid #EFEFEF","padding":"1px"});
            } else {
                popup.hide();
                
            }
        }
    });
    me.addListener( 'mouseout', function( t, evt ) {
    	evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /article/ig.test( el.tagName )&&el.getAttribute("name")=='srsection') {
            $(el).css({"border":"1px solid #FFFFFF","padding":"1px"});
		 }
	});
};

UE.plugins['select'] = function () {
    var me = this,thePlugins = 'select';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.combobox'),
				width : 680,height : 750,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/select.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /select/ig.test( el.tagName )) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.combobox')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('select_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('select');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.insertcombobox'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -724px -101px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
1
);

UE.plugins['textinput'] = function () {
    var me = this,thePlugins = 'textinput';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.textbox'),
				width : 400,height : 430,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/text.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&"text"==el.getAttribute("type")) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.textbox')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};

	
UE.registerUI('textinput_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('textinput');
        }
    });
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.inserttextbox'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -772px -101px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
2
);

UE.plugins['textarea'] = function () {
    var me = this,thePlugins = 'textarea';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.textarea'),
				width : 450,height : 510,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/textarea.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /textarea/ig.test( el.tagName ) ) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.textarea')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('textarea_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('textarea');
        }
    });
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.inserttextarea'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -794px -101px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
3
);

UE.plugins['datetime'] = function () {
    var me = this,thePlugins = 'datetime';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.datetime'),
				width : 380,height : 430,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/datetime.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&("datetime"==el.getAttribute("datetype")||"date"==el.getAttribute("datetype"))) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.datetime')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('datetime_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('datetime');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.insertdatetime'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -141px -20px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
4
);

UE.plugins['checkbox'] = function () {
    var me = this,thePlugins = 'checkbox';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : '复选框',
				width : 680,height : 740,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/checkbox.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&"checkboxComponent"==el.getAttribute("name")) {
            var html = popup.formatHtml(
                '<nobr>复选框: <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('checkbox_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('checkbox');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: '插入复选框',
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -767px -77px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
5
);

UE.plugins['radio'] = function () {
    var me = this,thePlugins = 'radio';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : '单选框',
				width : 680,height : 740,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/radio.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&"radioComponent"==el.getAttribute("name")) {
            var html = popup.formatHtml(
                '<nobr>单选框: <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('radio_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('radio');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: '插入单选框',
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -747px -77px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
6
);


UE.plugins['barcode'] = function () {
    var me = this,thePlugins = 'barcode';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : $.i18n.prop('admin.barcode'),
				width : 380,height : 420,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/barcode.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /img/ig.test( el.tagName )&&/barcodeComponent/ig.test(el.getAttribute("name"))) {
            var html = popup.formatHtml(
                '<nobr>'+$.i18n.prop('admin.barcode')+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};
	
UE.registerUI('barcode_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('barcode');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: $.i18n.prop('admin.insertbarcode'),
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -815px -101px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
7
);

UE.plugins['formulabtn'] = function () {
    var me = this,thePlugins = 'formulabtn';
    me.commands[thePlugins] = {
        execCommand:function () {

        }
    };
    me.addListener( 'click', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /input/ig.test( el.tagName )&&"button"==el.getAttribute("type")&&"formula"==el.getAttribute("formula")) {
        	var range=me.selection.getRange();
        	range.selectNodeContents(el);
        	range.select(true);
        	//$('#'+el.getAttribute('tmpid')).css('background-color','red');
        }
    });
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /input/ig.test( el.tagName )&&"button"==el.getAttribute("type")&&"formula"==el.getAttribute("formula")) {
        	$('#'+el.getAttribute('tmpid')).css('background-color','red');
        }
    });
    me.addListener( 'mouseout', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /input/ig.test( el.tagName )&&"button"==el.getAttribute("type")&&"formula"==el.getAttribute("formula")) {
        	$('#'+el.getAttribute('tmpid')).css('background-color','green');
        }
    });
};
//UE.plugins["justdelcomp"] = function () {
//    var me = this,thePlugins = "justdelcomp";
//    me.commands[thePlugins] = {
//        execCommand:function () {
//        	console.log("justdelcomp");
//        }
//    };
//    var popup = new baidu.editor.ui.Popup( {
//        editor:this,
//        content: '',
//        className: 'edui-bubble',
//        _edittext: function () {
//              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
//              me.execCommand(thePlugins);
//              this.hide();
//        },
//        _delete:function(){
//        	
//        	$.messager.confirm({
//    			title : '确认',
//    			ok : '是',
//    			cancel : '否',
//    			border:'thin',
//    			msg : '是否删除选中的组件?',
//    			fn : function(r) {
//    				if (r) {
//    					
//    					console.log(popup.anchorEl.getAttribute('iuid'))
//    					
//    					if(popup.anchorEl.getAttribute('iuid')){
//    						var nextlabel=$(popup.anchorEl).next("button[for='"+popup.anchorEl.getAttribute('iuid')+"']");
//    						if(nextlabel){
//    							nextlabel.remove();
//    						}
//    					}
//    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
//    					
//    				}
//    			}
//    		});
//
//            this.hide();
//        }
//    } );
//    popup.render();
//    
//    me.addListener( 'mouseover', function( t, evt ) {
//        evt = evt || window.event;
//        var el = evt.target || evt.srcElement;
////        console.log(el.tagName+"--"+el.getAttribute('name'))
//        if ( /article/ig.test( el.tagName )) {
//        	var value=el.getAttribute('gvalue');
//            var html = popup.formatHtml(
//                '<nobr>病症所见-'+(value?value:el.getAttribute('name'))+': <span onclick=$$._delete() class="edui-clickable">删除</span></nobr>' );
//            if ( html ) {
//                popup.getDom( 'content' ).innerHTML = html;
//                popup.anchorEl = el;
//                popup.showAnchor( popup.anchorEl );
//            } else {
//                popup.hide();
//            }
//        }
//    });
//    
//};


UE.plugins['qrcode'] = function () {
    var me = this,thePlugins = 'qrcode';
    me.commands[thePlugins] = {
        execCommand:function () {
        	
        	var dialog=$('#common_dialog').dialog({
				title : '二维码',
				width : 380,height : 420,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/qrcode.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});

            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        if ( /img/ig.test( el.tagName )&&/qrcodeComponent/ig.test(el.getAttribute("name"))) {
            var html = popup.formatHtml(
                '<nobr>'+'二维码'+': <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};

UE.registerUI('qrcode_btn',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
        execCommand:function(){
        	editor.execCommand('qrcode');
//        	createInserSelect();
//            alert('execCommand:' + uiName)
        }
    });
//alert('ddd');
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title: '二维码',
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -840px -100px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //当点到编辑内容上时，按钮要做的状态反射
    editor.addListener('selectionchange', function () {
        var state = editor.queryCommandState(uiName);
        if (state == -1) {
            btn.setDisabled(true);
            btn.setChecked(false);
        } else {
            btn.setDisabled(false);
            btn.setChecked(state);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
,
8
);

UE.plugins['anatomychart_svg'] = function () {
    var me = this,thePlugins = 'anatomychart_svg';
    me.commands[thePlugins] = {
        execCommand:function () {
        	var dialog=$('#common_dialog').dialog({
				title : '解剖结构示意图',
				width : 440,height : 470,border: 'thin',
				closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : this.options.UEDITOR_HOME_URL +'formdesign/anatomychart.jsp',
				buttons:[{
					text: $.i18n.prop('save'),
					className:'edui-okbutton',
					width:80,
					handler:function(){onok(me);}
				},{
					text: $.i18n.prop('cancel'),
					className:'edui-cancelbutton',
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}]//,
				//onLoad:onload_init
        	});
        }
    };
    var popup = new baidu.editor.ui.Popup( {
        editor:this,
        content: '',
        className: 'edui-bubble',
        _edittext: function () {
              baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
              me.execCommand(thePlugins);
              this.hide();
        },
        _delete:function(){
        	$.messager.confirm({
    			title : $.i18n.prop('confirm'),
    			border:'thin',
    			msg : $.i18n.prop('admin.confirmdeletecomponent'),
    			fn : function(r) {
    				if (r) {
    					baidu.editor.dom.domUtils.remove(popup.anchorEl,false);
    				}
    			}
    		});
            this.hide();
        }
    } );
    popup.render();
    me.addListener( 'mouseover', function( t, evt ) {
        evt = evt || window.event;
        var el = evt.target || evt.srcElement;
        //console.log(nodeType)
        if ( /embed/ig.test( el.tagName )) {
            var html = popup.formatHtml(
                '<nobr>解剖结构示意图: <span onclick=$$._edittext() class="edui-clickable">'+$.i18n.prop('edit')+'</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">'+$.i18n.prop('delete')+'</span></nobr>' );
            if ( html ) {
                popup.getDom( 'content' ).innerHTML = html;
                popup.anchorEl = el;
                popup.showAnchor( popup.anchorEl );
            } else {
                popup.hide();
            }
        }
    });
};


