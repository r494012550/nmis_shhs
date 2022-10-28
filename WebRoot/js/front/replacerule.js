/**
 * 
 */


var replaceRule = {
	expressions: /(@not|@in)/,
	value_exp : '@{value}',
	displayname_exp : '@{displayname}',
	replace : function(obj,summary_attr){
		//console.log(obj);

		var ret="";
		var value="";
		if('SELECT'==obj.tagName){
			value=$(obj).attr('gvalue');
		} else if('INPUT'==obj.tagName&&($(obj).attr('gname')==pluginHandle.component_name_radio||$(obj).attr('gname')==pluginHandle.component_name_checkbox)){
			if($(obj).attr('checked')=='checked'){
				value=$(obj).val();
			} else if($(obj).attr('gname')==pluginHandle.component_name_radio){//单选按钮value为空，直接返回''
				return '';
			}
		} else{
			value=$(obj).val();
		}
		var summary=$(obj).attr(summary_attr);
		if(summary){
			var evalexp=summary.match(/@{{.+}}/);
			if(evalexp){//@{{javascript}}，提取文本中添加javascript
				evalexp=evalexp[0].replace(new RegExp(this.value_exp,"gm"),value);
				try  {
					var evalexp=evalexp.substr(3,evalexp.length-5);
					console.log(evalexp)
					evalexp=eval(evalexp);
				}catch(exception) {
					console.log(exception)
				}
				ret=summary.replace(/@{{.+}}/,evalexp);
			}
			else if($.trim(value)!=""){
				if('SELECT'==obj.tagName||('INPUT'==obj.tagName&&($(obj).attr('gname')==pluginHandle.component_name_radio||$(obj).attr('gname')==pluginHandle.component_name_checkbox))){
					var exp=this.getExp(summary);
					if(exp){
						if(exp.search(/@not/)>=0){
//								console.log(this.getOpt(exp))
//								console.log(value)
							if(this.getOpt(exp).split(",").indexOf(value)<0){
								ret=summary.substring(exp.length,summary.length).replace(this.value_exp,value);
							}
						}
						else if(exp.search(/@in/)>=0){
							if(this.getOpt(exp).split(",").indexOf(value)>=0){
								ret=summary.substring(exp.length,summary.length).replace(this.value_exp,value);
							}
						}
					} else{
						
						if(summary.match(/@{value}/)){
							summary=summary.replace(this.value_exp,value);
						} else if(summary.match(/@{displayname}/)){
							var disname= this.getOptionDisplayname(obj);
							summary=disname?summary.replace(this.displayname_exp,disname):'';
						}
						ret=summary;
					}
				}
				else{
					var exp=this.getExp(summary);
					ret=summary.substring(exp.length,summary.length).replace(this.value_exp,value);
				}
			
			}
			
		}
		return ret;
	},
	getExp : function(summary){
		//console.log(this.expressions.test(summary));
		var ret="";
		if(this.expressions.test(summary)){
			var ex=summary.match(/^@(not|in)\[.+\]/);
			//console.log(ex);
			if(ex&&ex.length>0){
				ret=ex[0];
			}
//			console.log(ret);
		}
		return ret;
	},
	getOpt : function(exp){
		var ret="";
		if(exp.indexOf("[")>=0&&exp.indexOf("]")>=0){
			ret=exp.substring(exp.indexOf("[")+1,exp.indexOf("]"));
		}
		return ret;
	},
	getOptionDisplayname : function (obj){
		var ret="";
		if('SELECT'==obj.tagName){
			var selectdom=$(obj)[0].options[$(obj).attr('selected_index')];
			if(selectdom){
				ret=selectdom.getAttribute('displayname')||'';
			}
		} else if('INPUT'==obj.tagName&&($(obj).attr('gname')==pluginHandle.component_name_radio||$(obj).attr('gname')==pluginHandle.component_name_checkbox)){
			ret=obj.getAttribute('displayname')||'';
		}
		return $.trim(ret);
	}

}