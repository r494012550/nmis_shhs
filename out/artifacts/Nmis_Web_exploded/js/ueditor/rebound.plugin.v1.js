var pluginHandle={
		me:this,
		component_name_text : "textinputComponent",
		component_name_textarea : "textareaComponent",
		component_name_select : "selectComponent",
		component_name_number : "numberinputComponent",
		component_name_clinicalcode : "clinicalcode",
		component_name_barcode : "barcodeComponent",
		component_name_qrcode : "qrcodeComponent",
		component_name_datetime : "datetimeComponent",
		component_name_checkbox : "checkboxComponent",
		component_name_radio : "radioComponent",
		component_name_snapshot : "snapshot",
		component_name_finding : "finding",
		component_name_srsection : "srsection",
		component_name_anatomychart : "anatomychart",
		editdom:new Object(),
		
		insertTextComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		
		insertTextareaComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		
		insertSelectComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		
		insertNumberComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		insertDatetimeComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		insertCheckboxComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		insertRadioComponet : function(editor,html){
			var obj=$(html);
			obj.attr("id",uuid());
			obj.attr("objuid",uuid());
			html=$(obj).prop("outerHTML");
			editor.execCommand('insertHtml', html);
		},
		insertSection : function(editor,uid,html,is_qc,clone,title,displayname,catalog,header){
			var jobj=$("<article id='"+uuid()+"' name='srsection' gtype='srsection' title='"+title+"' displayname='"+displayname+"' sectionuid='"+uid+"' qc='"+(is_qc||'')+"' clone='"
						+(clone||'')+"' is_catalog='"+(catalog||'')+"' is_header='"+(header||'')+"' style='border: 1px solid #FFFFFF; padding: 1px;'>"
						+html+"</article>");
			
			if(clone==1){
				jobj.attr("class","easyui-tooltip");
				jobj.attr("title","可右键复制删除");
				jobj.attr("position","right");
			}
			html=this.newComponentId(jobj);
			editor.execCommand('insertHtml', html);
		},
		insertCustomComponent : function(editor,data,objuidflag){
			var html=data.html;
			var unitstr="";
			if(data.type==0&&data.unit){
				var iuid=data.uid+new Date().format('yyyyMMddhhmmssS');
				unitstr="<input type='button' for='"+iuid+"' uid='"+data.uid+"' style='border:0px;background: #FFFFFF;' value='"+data.unit+"'/>&nbsp;";
				var o=$(html);
				o.attr("iuid",iuid);
				//console.log(o[0].outerHTML)
				html=o[0].outerHTML;
				o.remove();
			}
			if(objuidflag){
				console.log(html)
				var obj=$(html);
				obj.attr("objuid",uuid());
				html=$(obj).prop("outerHTML");
				console.log(html);
			}
			editor.execCommand('insertHtml',html+unitstr);
		},

		insertClinicalCodeComponent : function(editor,data){
			var html=this.getClinicalCodeComponentHtml(data);
			editor.execCommand('insertHtml', html);
		},
		
		getClinicalCodeComponentHtml : function(data){
			return '<span><input type="button" name="clinicalcode" gtype="clinicalcode" code="'+data.code+'" scheme="'+data.scheme+'" gvalue="'+
						data.meaning+'" value="['+data.meaning+']" data-clipboard-text="['+data.meaning+']" style="border:0px;background: #FFFFFF;"/></span>';//disabled="disabled"
		},
		
		insertAnatomyChartComponent : function(editor,data){
			var html='<embed src="image/anatomychart/'+data.name+'" width="250" height="250" type="image/svg+xml" pluginspage="http://www.adobe.com/svg/viewer/install/"/>';
			editor.execCommand('insertHtml', html);
		},
		
		insertFindingComponent : function(editor,data){
			var html="";
			console.log(data);
			if(data.name=="snapshot"&&data.belongreport=="all"){
				
//				html='<hr style="border:0px;"/><article name="snapshot" gtype="snapshot" style="border:2px solid #EEEEEE;padding:0px;">'+
//				'<input type="button" name="snapshot" gtype="snapshot" value="{snapshot}" disabled="disabled" style="border:0px;background: #EFEFEF;width:100%"/></article><br/>';
				html='<input type="button" name="snapshot" gtype="snapshot" value="{snapshot}" disabled="disabled" style="border:0px;background: #EFEFEF;width:100%"/><p><br/></p>';
			}
			else{
//				html='<hr style="border:0px;"/><article name="finding" gvalue="'+data.name+'" report="'+
//				data.belongreport+'" gtype="finding" style="border:2px solid #EEEEEE;padding:0px;"><input type="button" name="finding" gtype="finding" report="'+
//				data.belongreport+'" gvalue="'+data.name+'" value="{'+data.name+'}" disabled="disabled" style="border:0px;background: #EFEFEF;width:100%"/></article><br/>';
				
				html='<input type="button" name="finding" gtype="finding" report="'+
				data.belongreport+'" gvalue="'+data.name+'" value="{'+data.name+'}" disabled="disabled" style="border:0px;background: #EFEFEF;width:100%"/>';
			}
			editor.execCommand('insertHtml', html);
		},
		createElement:function (type, name){     
		    var element = null;     
		    try {        
		    	element = document.createElement(type);   
		        element.setAttribute("name",name);    
		    } catch (e) {}   
		    if(element==null) {     
		        element = document.createElement(type);     
		        element.name = name;     
		    } 
		    return element;     
		},
		createFindingComponent:function(input){
			var el=this.createElement("article","finding");
			$(el).attr("gtype",$(input).attr("gtype"));
			$(el).attr("gvalue",$(input).attr("gvalue"));
			$(el).attr("report",$(input).attr("report"));
			$(el).css({"border":"1px solid #FFFFFF","padding":"1px"});
			
			//console.log(el.outerHTML);
			return el;
		},
		createSnapshotComponent:function(input){
			var el=this.createElement("article","snapshot");
			$(el).attr("gtype",$(input).attr("gtype"));
			$(el).css({"border":"1px solid #FFFFFF","padding":"1px"});
			
			//console.log(el.outerHTML);
			return el;
		},
		formatComponent:function(content){
			content=content.replace(/ghidden/g,"hidden='hidden'");
			content=content.replace(/\|\|/g,"<br hidden/>");
			content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>");

			var contentj=$("<div>"+content+"</div>");
			this.convertCkAndRdAndSummary(contentj);//转换复选框和单选框和提取文本框
			content=contentj.html();
			contentj.remove();
			return content;
		},
		addTooltipContentForSection:function(content){//为复制章节添加提示信息，保存、提交、初审、审核报告后调用该方法
			var contentj=$("<div>"+content+"</div>");
			contentj.find("article[name='srsection'][clone='1']").each(function(index,obj){
				$(obj).removeClass().addClass('easyui-tooltip').attr("title",'可右键复制删除');
			});
			content=contentj.html();
			contentj.remove();
			return content;
		},
		addListenerOnSRContainer:function (container,selector){
			if(!selector){
				selector="article[name='srsection'][clone='1'],[name='snapshot']";
			}
			
			container.find(selector).mouseenter(function(e){//mousemove,click   [name='snapshot']
				var el = e.currentTarget || e.target || e.srcElement;
				//console.log(el);
				if(/article/ig.test( el.tagName )){
					$(el).css({"border":"1px solid #EBECED","padding":"1px"});
					pluginHandle.editdom=el;
				}
			}).mouseleave(function(e){
				 var el =  e.currentTarget || e.target || e.srcElement;
				 if(/article/ig.test( el.tagName )){
					 //console.log('mouseleave');
					 $(el).css({"border":"1px solid #FFFFFF","padding":"1px"});
					 //delete pluginHandle.editdom;
				 }
			}).bind('contextmenu',function(e){
		        e.preventDefault();
		        console.log(container.attr('reportid'))
		        if($('#orderStatus_'+container.attr('reportid')).val()!=myCache.ReportStatus.FinalResults){
			        var el =  e.currentTarget || e.target || e.srcElement;
					if(/article/ig.test( el.tagName )){
						
						if($(el).attr('name')=='srsection'&&$(el).attr('clone')=='1'){
							$('#mm_article').empty();
					        $('#mm_article').menu('appendItem', {
					        	text: '复制',
					        	onclick: function(){pluginHandle.cloneSrSection(el,container);}
					        });
					        $('#mm_article').menu('appendItem', {
					        	text: '删除',
					        	onclick: function(){pluginHandle.delSrSection(el,container);}
					        });
					        $('#mm_article').menu('show', {
					            left: e.pageX,
					            top: e.pageY
					        });
						} else if($(el).attr('name')=='snapshot'){
					        $('#mm_article').empty();
					        $('#mm_article').menu('appendItem', {
					        	text: $.i18n.prop('edit'),
					        	onclick: function(){openEditImageSnapshotDialog();}
					        });
					        $('#mm_article').menu('appendItem', {
					        	text: $.i18n.prop('clear'),
					        	onclick: function(){clearFindingContent();}
					        });
					        $('#mm_article').menu('appendItem', {
					        	text: $.i18n.prop('delete'),
					        	onclick: function(){deleteFindingFromReport();}
					        });
					        $('#mm_article').menu('show', {
					            left: e.pageX,
					            top: e.pageY
					        });
						}
					}
		        }
		    });
		},
		cloneSrSection:function(e,container){
			console.log(e)
			var me=this;
			var clone=$(e).clone(true);
			var cloneid=uuid();
			clone.attr("id",cloneid);
			clone.removeClass().addClass('easyui-tooltip');
			clone.attr("title",'可右键复制删除');
			$.getJSON(window.localStorage.ctx+"/srreport/getSectionByUid?uid="+$(e).attr("sectionuid"), function(json){
				var section=validationData(json);
				
				console.log(section)
				
				var sectioncontent=section&&section.sectioncontent?section.sectioncontent:'';
//				if(sectioncontent){
					clone.html(sectioncontent);
//				}
				me.convertCkAndRdAndSummary(clone);//转换复选框和单选框和提取文本框
				var content=me.newComponentId(clone);
				
				$(e).after(content);
				$.parser.parse($('#'+cloneid).parent());
				reg_formula_dom(container,'#'+cloneid);//注册表达式，已使其生效
				pluginHandle.addListenerOnSRContainer(container,'#'+cloneid);
			});

		},
		delSrSection:function(e,container){
			$(e).remove();
		},
		convertCkAndRdAndSummary: function(container){
			container.find("input.checkbox_Component,input.radiobutton_Component,textarea[issummary='true']").each(function(index,obj){
				if($(obj).attr('name')==pluginHandle.component_name_checkbox){
					if($(obj).attr('options')){
						var options=JSON.parse($(obj).attr('options'));
						var wrap_options=$(obj).attr('wrap_options');//选项之间换行
						var wrap="";
						if(wrap_options=="wrap_options"){
							wrap="<br/>";
						}
						//console.log(options)
						var checksstr="";
						var name_id=uuid();
						for(var i=0,len=options.length;i< len;i++){
							//console.log(options[i])
							var owrap=i<(len-1)?wrap:"";
							var id=uuid();
							var checkedstr=options[i].defaultoption=='checked'?"checked='checked'":"";
							checksstr+=($("<input type='checkbox' name='checkboxComponent_"+name_id+"' gname='checkboxComponent' id='"+id+"' uid='"+$(obj).attr('uid')+"' value='"+options[i].value+"' gvalue='"+options[i].value+"' gvaluecode='"+
									options[i].code+"' code='"+$(obj).attr('code')+"' onchange='checkbox_onchange_handle(checked,this)' "+
									checkedstr+" operation='"+$(obj).attr('operation')+"' sectionuid='"+options[i].sectionuid+"' displayname='"+(options[i].displayname||'')
									+"' style='width:18px;height:18px;' "+(i<(len-1)?"_separator=','":"")+(owrap?" iswrap='1'":"")+" mutex='"+(options[i].mutex||'0')+"' _color='"+(options[i].color||'')
									+"'/>").attr('summary',$(obj).attr('summary')).prop("outerHTML")+"<label class='checkbox_label' for='"+id+"'>&nbsp;"+options[i].value+"&nbsp;</label>"+owrap);
						}
						$(obj).replaceWith(checksstr);
					}
				} else if($(obj).attr('name')==pluginHandle.component_name_radio){
					if($(obj).attr('options')){
						var options=JSON.parse($(obj).attr('options'));
						var wrap_options=$(obj).attr('wrap_options');//选项之间换行
						var wrap="";
						if(wrap_options=="wrap_options"){
							wrap="<br/>";
						}
						//console.log(options)
						var checksstr="";
						var name_id=uuid();
						for(var i=0,len=options.length;i< len;i++){
							//console.log(options[i])
							var owrap=i<(len-1)?wrap:"";
							var id=uuid();
							var checkedstr=options[i].defaultoption=='checked'?"checked='checked'":"";
							checksstr+=($("<input type='radio' name='radioComponent_"+name_id+"' gname='radioComponent' id='"+id+"' uid='"+$(obj).attr('uid')+"' value='"+options[i].value+"' gvalue='"+options[i].value+"' gvaluecode='"+
									options[i].code+"' code='"+$(obj).attr('code')+"' onchange='radio_onchange_handle(checked,this)' "+
									checkedstr+" operation='"+$(obj).attr('operation')+"' sectionuid='"+options[i].sectionuid+"' displayname='"+(options[i].displayname||'') +"' _color='"+(options[i].color||'')
									+"' style='width:18px;height:18px;' "+(owrap?" iswrap='1'":"")+"/>"
									).attr('summary',$(obj).attr('summary')).prop("outerHTML")+"<label class='checkbox_label' for='"+id+"'>&nbsp;"+options[i].value+"&nbsp;</label>"+owrap);
						}
						$(obj).replaceWith("<span objuid='"+uuid()+"'>"+checksstr+"</span>");
					}
				} else {
					//已经生成提取工具栏，则跳过
					if($(obj).next().hasClass('summary_textarea_toolbar')){
						return true;
					}
					var width=$(obj).width(),height=$(obj).height();
					$(obj).width(width-23);
					$(obj).wrap("<span class='summary_textarea_span' style='width:"+width+"px;height:"+height+"px'></span>");
					$(obj).after("<span class='summary_textarea_toolbar' style='width:23px;height:60px;'>" +
							"<a href='javascript:;' class='textbox-icon icon-tip2' title='提取' icon-index='0' tabindex='-1' style='width: 22px; height: 28px;' onclick='extract_Summary();'></a>" +
							"<a href='javascript:;' class='textbox-icon icon-clear' title='清空' icon-index='1' tabindex='-1' style='width: 22px; height: 28px;' onclick='clear_Summary();'></a></span>");
				}
			});
		},
		newComponentId:function(container){
			var arr=new Array(0);
			container.find('embed').each(function(index,obj){
				var locationcom_id=$(obj).attr('locationcom_id');
				if(locationcom_id){
					container.find('#'+locationcom_id).attr('islocationselect','true');
				}
			});
			
			container.find("input[type!=button],select,textarea,label").each(function(index,obj){
				if(obj.getAttribute('name')==pluginHandle.component_name_number){//数字输入框的表达式
					var id=$(obj).attr('id'),newid=uuid();
					var tmp = new Object();
					tmp.id=id;
					tmp.newid=newid;
					arr.push(tmp);
					$(obj).attr('id',newid);
				} else if("LABEL"==obj.tagName){//处理单选框和复选框的for属性，设置为处理单选框和复选框新的id
					var id=$(obj).prev("input[type='checkbox'],input[type='radio']").attr("id");
					if(id){
						$(obj).attr('for',id);
					}
				} if('SELECT'==obj.tagName){
					var newid=uuid();
					if($(obj).attr('islocationselect')=='true'){
						container.find("embed[locationcom_id='"+$(obj).attr('id')+"']:first").attr('locationcom_id',newid);
					}
					$(obj).attr('id',newid);
				} else {
					$(obj).attr('id',uuid());
				}
				$(obj).attr("objuid",uuid());
//				if('SELECT'==obj.tagName){
//					$(obj).removeAttr('class').addClass('easyui-combobox');
//					$(obj).attr("gvalue","");
//					$(obj).attr("gvaluecode","");
//				}
//				else{
//					$(obj).attr("value","");
//				}
			});
			//clone.find('.textbox.combo').remove()//.replaceWith("");
			
			var content=container.prop("outerHTML");
			//因为所有组件的id重新生成，需要将表达式中的id替换为新的id
			for(var j=0,len=arr.length;j<len;j++){
				content=content.replace(new RegExp("{"+arr[j].id+"}","g"),"{"+arr[j].newid+"}");
			}
			return content;
		}
		
}