//详情信息
function details(){
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		 sendAjax({
			url : "/worklist/viewDetails",
			contentType : "application/x-www-form-urlencoded; charset=UTF-8",
			dataType : 'json',
			data : {
				patientid : row.patientid
			},
			success : function(data) {
				$('#details').dialog({
					title : '详情信息',
					border:'thin',
					width : 1000,
					height : 500,
					closed : false,
					cache : false,
					href : '/worklist/detailsView',
					modal : true,
					onLoad:function(){
						document.getElementById("_patientid").value=data[0].patientid;
						document.getElementById('_patientname').value=data[0].patientname;
						document.getElementById('_py').value=data[0].py;
						document.getElementById('_sexdisplay').value=data[0].sexdisplay;
						document.getElementById('_telephone').value=data[0].telephone;
						document.getElementById('_modality_type').value=data[0].modality_type;
						document.getElementById('_age').value=data[0].age;
						document.getElementById('_bedno').value=data[0].bedno;
						document.getElementById('_address').value=data[0].address;
						document.getElementById('_birthdate').value=data[0].birthdate;
						document.getElementById('_cardno').value=data[0].cardno;
						document.getElementById('_inno').value=data[0].inno;
						document.getElementById('_outno').value=data[0].outno;
						document.getElementById('_wardno').value=data[0].wardno;
						document.getElementById('_studyitems').value=data[0].studyitems;
						document.getElementById('_studyid').value=row.studyorderstudyid;
						document.getElementById('_patientsource').value=data[0].patientsource;
						document.getElementById('_priority').value=data[0].priority;
						document.getElementById('_modality').value=data[0].modality;
						document.getElementById('_appdept').value=data[0].appdept;
						document.getElementById('_appdoctor').value=data[0].appdoctor;
						document.getElementById('_modality').value=data[0].modality;
						document.getElementById('_remark').value=data[0].remark;
					}
				});
				$('#details').dialog('refresh','/worklist/detailsView');
			}
			});
		
		
	
	}else{
		$.messager.show({
			title : '提醒',
			msg : "请选择检查！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}