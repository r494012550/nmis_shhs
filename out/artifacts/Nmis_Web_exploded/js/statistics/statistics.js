
$(function() {
	$.extend($.fn.combobox.methods, {
        addClearBtn: function(jq, iconCls){
            return jq.each(function(){
                var t = $(this);
                
                var opts = t.textbox('options');
                opts.icons = opts.icons || [];
                opts.icons.unshift({
                    iconCls: iconCls,
                    handler: function(e){
                        $(e.data.target).textbox('clear').textbox('textbox').focus();
                        //$(this).css('visibility','hidden');
                    }
                });
                t.textbox();
               /*  if (!t.textbox('getText')){
                    t.textbox('getIcon',0).css('visibility','hidden');
                } */
                
                //console.log(t.textbox('textbox'))
                /* t.combobox({onChange:function(){
                    var icon = t.textbox('getIcon',0);
                    
                    console.log($(this).val())
                    
                    if ($(this).val()){
                        icon.css('visibility','visible');
                    } else {
                        icon.css('visibility','hidden');
                    }
                }});  */
                
            });
        }
    });
	
});


function showStatistics(row){
//$("#reportFrame").attr("src", '/statistics/showStatistics?tempname='+row.filename+'&'+datetype+'from='+from+'&'+datetype+'to='+to);
//$('#main_statistics').panel('refresh',window.localStorage.ctx+'/statistics/showStatistics?tempname='+row.filename+'&'+datetype+'from='+from+'&'+datetype+'to='+to);
// 	if($('#statistics_tabs').tabs('exists',1)){
// 		$('#statistics_tabs').tabs('select',1);
// 	}
	$('#statistics_tabs').tabs('select', '统计');
	$('#centerarea').layout('panel','north').panel('refresh',window.localStorage.ctx+'/statistics/showStatisticsConditions?id='+row.id);
	$('#main_statistics').html("");
	$("#reportFrame").attr("src", "");
	$('#statisticalreport_id').val(row.id);
}

function generateStatisticalReport(id){
	$('#progress_dlg').dialog('open');
	$('#generatestatisticsform').form('submit', {
		url: window.localStorage.ctx+"/statistics/showStatisticalReport?id="+id,
		onSubmit: function(){
//			if($('#nodename').textbox('getText')==''){
//				return false;
//			}
		},
		success: function(data){
			//console.log(data)
			$('#main_statistics').html(data);
			$('#progress_dlg').dialog('close');
//			var res=validationDataAll(data);
//			
//			if(res.code == 0){
//				
//			} else {
//				$.messager.show({
//		            title:'失败',
//		            msg:"添加节点失败！请重试!",
//		            timeout:3000,
//		            border:'thin',
//		            showType:'slide'
//		        });
//			}
		}
	});
}

var newreport=true;

function showHtml(title,index){
	if(index==1&&newreport){
		$('#progress_dlg').dialog('open');
		$('#generatestatisticsform').form('submit', {
			url: window.localStorage.ctx+"/statistics/showStatisticalReport?id="+$('#statisticalreport_id').val(),
//			onSubmit: function(){
//			},
			success: function(data){
				//console.log(data)
				$('#main_statistics').html(data);
				$('#progress_dlg').dialog('close');
				newreport=false;
			}
		});
	}
}

function generateStatisticalReport_pdf(id){
	$('#report_tabs').tabs('select',0);
	$('#progress_dlg').dialog('open');
	$("#reportFrame").attr("src", window.localStorage.ctx+'/statistics/showStatisticalReport_Pdf?id='+id+'&'+$( "#generatestatisticsform" ).serialize());
	$('#progress_dlg').dialog('close');
	newreport=true;
}

function initConditions(){
	var date=new Date();
	$('#date_from').datebox('setValue',date);
	$('#date_to').datebox('setValue',date);
	
	$('#sta_group').combobox('addClearBtn', 'icon-clear');
	$('#sta_modality').combobox('addClearBtn', 'icon-clear');
	setHeight();
}

function setHeight(){
    var c = $('#centerarea');
    var p = c.layout('panel','north');    // get the center panel
    var oldHeight = p.panel('panel').outerHeight();
    p.panel('resize', {height:'auto'});
    var newHeight = p.panel('panel').outerHeight();
    c.layout('resize',{
        height: (c.height() + newHeight - oldHeight)
    });
}

function showFirstCondition(data){
	if(data.rows.length>0){
		showStatistics(data.rows[0]);
		$('#statisticsReport_dl').datalist('selectRow',0);
	}
}

function exportStatisticalReport(id,type){

	$('#generatestatisticsform').form({
	    url:window.localStorage.ctx+"/statistics/exportStatisticalReport?id="+id+"&type="+type
	});
	$('#generatestatisticsform').submit();
}