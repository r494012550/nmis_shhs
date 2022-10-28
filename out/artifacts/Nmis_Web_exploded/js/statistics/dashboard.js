/**
 *
 */

var myChart_totalNumOfSchOrReg,//近7天预约登记数
	myChart_totalNumOfExam,//近7天检查数
	myChart_totalNumOfReport,//近7天报告数
	myChart_eachEquipRevernue,//每台设备当天的收入
	myChart_modalityRevernue30Days,//近30天按设备类型统计的收入
	myChart_patientSourceRevernue30Days,//近30天按患者来源统计的收入
	myChart_eachEquipRevernue30Days,//近30天每台设备的收入
	myChart_positiveRate,//阴阳率
	myChart_onlineTime;//
var dashboard_start,//Dashboard开始时间
	dashboard_end,//Dashboard结束时间
	dashboard_institutionid;//Dashboard机构id
/*
 * theme 主题，可选'dark'
 * */
function dashboardOnload(theme){
	if(!theme){
		theme=$('#chart_theme').val();
	}
	summaryData();
//	theme='dark';
	totalNumOfSchReg_nearly7Days(null,null,theme);//可选'dark'
	totalNumOfExam_nearly7Days(null,null,theme);//可选'dark'
	totalNumOfReport_nearly7Days(null,null,theme);//可选'dark'
	eachEquipmentRevernue(null,null,theme);//可选'dark'
	modalityRevernue_30Days(null,null,theme);//可选'dark'
	patientSourceRevernue_30Days(null,null,theme);//可选'dark'
	eachEquipmentRevernue_30Days(null,null,theme);//可选'dark'
	positiveRate(null,null,theme);//可选'dark'
	onlineTime(null,null,theme);//
}

function changeInstitution_db(newValue){
	// console.log(newValue)
	dashboard_institutionid=newValue;
	dashboardOnload('');
}

function selectTime(){
	localStorage.setItem("enable_db_start_key", false);
	localStorage.setItem("enable_db_end_key", true);
	showDateRangeDialog(summaryData);
}

function summaryData(start,end){
	if(end==null){
		end=new Date();
	}
	$('#time_span').text(end.format('yyyy-MM-dd'));
	end.setDate(end.getDate()+1);
	start=new Date(end);
	start.setDate(start.getDate()-1);
	getJSON(window.localStorage.ctx+'/statistics/summarizeDataOfToday',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid: dashboard_institutionid
		},
		function(json){
//			console.log(json)
			$('#appoint_count').html(json.data.appoint_count);
			$('#reg_count').html(json.data.reg_count);
			$('#exam_count').html(json.data.exam_count);
			$('#report_count').html(json.data.report_count);
			$('#preaudit_count').html(json.data.preaudit_count);
			$('#audit_count').html(json.data.audit_count);
		}
	);
}

function tabsOnResize(width, height){
	// console.log(width)
	if(myChart_totalNumOfSchOrReg!=null){
		myChart_totalNumOfSchOrReg.resize();
	}
	if(myChart_totalNumOfExam!=null){
		myChart_totalNumOfExam.resize();
	}
	if(myChart_totalNumOfReport!=null){
		myChart_totalNumOfReport.resize();
	}
	if(myChart_eachEquipRevernue!=null){
		myChart_eachEquipRevernue.resize();
	}
	if(myChart_eachEquipRevernue30Days!=null){
		myChart_eachEquipRevernue30Days.resize();
	}
	if(myChart_positiveRate!=null){
		myChart_positiveRate.resize();
	}
	if(myChart_modalityRevernue30Days!=null){
		myChart_modalityRevernue30Days.resize();
	}
	if(myChart_patientSourceRevernue30Days!=null){
		myChart_patientSourceRevernue30Days.resize();
	}
}

function showDateRangeDialog(fun){
	dashboard_start=null;
	dashboard_end=null;
	$('#common_dialog').dialog(
		{
			title : '选择时间',
			width : 550,height : 380,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
			border: 'thin',
			href : window.localStorage.ctx+'/view/statistics/dateRange.html',
			modal : true,
			buttons:[{
				text: $.i18n.prop('ok'),
				width:80,
				handler:function(){
					// console.log(dashboard_start);
					if(dashboard_end==null&&$('#db_end').calendar('options').current!=null){
						dashboard_end=$('#db_end').calendar('options').current;
					}
					// console.log(dashboard_end);
					// console.log(fun)
					if(fun!=null){
						fun(dashboard_start,dashboard_end);
					}
					$('#common_dialog').dialog('close');
				}
			},{
				text: $.i18n.prop('cancel'),
				width:60,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
}

function totalNumOfSchReg_nearly7Days (start,end,theme){
	var container=document.getElementById("container_totalNumOfSchOrReg");
	if(container==null){
		return;
	}
	if(myChart_totalNumOfSchOrReg==null){
		myChart_totalNumOfSchOrReg = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	start=new Date(end);
	start.setDate(start.getDate()-7);

	getJSON(window.localStorage.ctx+'/statistics/totalNumOfSchOrReg',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
//			console.log(json)
			if(json.code==0){

				var option = {
					title: {
						text: '近7天预约登记数',
						padding: [10, 5, 5, 5],
						textStyle: {
							fontSize: 16
						}
					},
					grid: {
						top: 40,
						bottom: 35
					},
					tooltip: {
						trigger: 'axis'
					},
					legend: {
						data: ['预约', '登记'],
						right:'10%'
					},
					toolbox: {
						orient: 'vertical',
						showTitle: false,
						tooltip: {
							show: true,
							formatter: function (param) {
								return '<div>' + param.title + '</div>';
							},
						},
						feature: {
							myTool_DateRange: {
								show: true,
								title: '选择时间',
								icon: 'image://themes/icons/calendar.png',
								onclick: function (){
									localStorage.setItem("enable_db_start_key", false);
									localStorage.setItem("enable_db_end_key", true);
									showDateRangeDialog(totalNumOfSchReg_nearly7Days);
								}
							},
							magicType: {
								type: ["line", "bar"]
							},
							restore: {
								show: true,
								title: '还原'
							},
							saveAsImage: {
								show: true,
								title: '保存为图片'
							}
						}
					},
					xAxis: {
						type: 'category',
						data: json.data.category//['4-14', '4.15', '4.16', '4.17', '4.18', '4.19', '4.20']
					},
					yAxis: {
						type: 'value'
					},
					series: [
						{
							name: '预约',
							data: json.data.data_sch,//[820, 932, 901, 934, 1290, 1330, 1320],
							type: 'line',
							smooth: true
						},
						{
							name: '登记',
							data: json.data.data_reg,//[860, 982, 941, 634, 1090, 1530, 1620],
							type: 'line',
							smooth: true
						}
					]
				};
				myChart_totalNumOfSchOrReg.setOption(option);
			}
		}
	);
}

function totalNumOfExam_nearly7Days (start,end,theme){
	var  container= document.getElementById("container_totalNumOfExam");
	if(container==null){
		return;
	}
	if(myChart_totalNumOfExam==null){
		myChart_totalNumOfExam = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/totalNumOfExam',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){

			var option = {
				title: {
					text: '近7天检查数',
					left: 'center',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 40,
					bottom: 35
				},
				tooltip: {
					trigger: 'axis'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						}
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", false);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(totalNumOfExam_nearly7Days);
							}
						},
						magicType: {
							type: ["line", "bar"]
						},
						restore: {
							show: true,
							title: '还原'
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				xAxis: {
					type: 'category',
					data: json.data.category//['4.14', '4.15', '4.16', '4.17', '4.18', '4.19', '4.20']
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						data: json.data.data,//[120, 200, 150, 80, 70, 110, 130],
						type: 'bar',
						label: {
							show: true
						}
					}
				]
			};
			myChart_totalNumOfExam.setOption(option);
		}
	);
}

function totalNumOfReport_nearly7Days (start,end,theme){
	var  container= document.getElementById("container_totalNumOfReport");
	if(container==null){
		return;
	}
	if(myChart_totalNumOfReport==null){
		myChart_totalNumOfReport = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/totalNumOfReport',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '近7天报告数',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 40,
					bottom: 35
				},
				tooltip: {
					trigger: 'axis'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", false);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(totalNumOfReport_nearly7Days);
							}
						},
						magicType: {
							type: ["line", "bar"]
						},
						restore: {
							show: true,
							title: '还原'
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				legend: {
					data: ['报告', '初审', '审核'],
					right:'10%'
				},
				xAxis: {
					type: 'category',
					data: json.data.category//['4.14', '4.15', '4.16', '4.17', '4.18', '4.19', '4.20']
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						name: '报告',
						data: json.data.data_report,//[820, 932, 901, 934, 1290, 1330, 1320],
						type: 'line',
						smooth: true
					},
					{
						name: '初审',
						data: json.data.data_preaudit,//[620, 732, 701, 1034, 1340, 1030, 1220],
						type: 'line',
						smooth: true
					},
					{
						name: '审核',
						data: json.data.data_audit,//[720, 832, 601, 1234, 1240, 1230, 1620],
						type: 'line',
						smooth: true
					}
				]
			};
			myChart_totalNumOfReport.setOption(option);
		}
	);
}

function eachEquipmentRevernue(start,end,theme){
	var container= document.getElementById("container_eachEquipRevernue");
	if(container==null){
		return;
	}
	if(myChart_eachEquipRevernue==null){
		myChart_eachEquipRevernue = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	start=new Date(end);
	end.setDate(end.getDate()+1);
	getJSON(window.localStorage.ctx+'/statistics/eachEquipmentRevernue',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '当天设备收入',
					subtext: json.data.startdate+' 总共：'+json.data.total,
					left: 'left',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 40,
					bottom: 20
				},
				tooltip: {
					trigger: 'item'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", false);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(eachEquipmentRevernue);
							}
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				series: [
					{
						name: '收入',
						type: 'pie',
						radius: '50%',
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						},
						data: json.data.datas
//				      		[
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子西门子西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子西门子西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//					        { value: 10048, name: '西门子CT1' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 10080, name: '西门西门子西门子子MR2' },
//					        { value: 484, name: '西门子CT2' },
//				      	]
					}
				]
			};
			// console.log(option)
			myChart_eachEquipRevernue.setOption(option);
			myChart_eachEquipRevernue.on('click', function(params) {
				// console.log(params)
			});
		}
	);
}


function modalityRevernue_30Days(start,end,theme){
	var container= document.getElementById("container_modalityRevernue30Days");
	if(container==null){
		return;
	}
	if(myChart_modalityRevernue30Days==null){
		myChart_modalityRevernue30Days = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	if(start==null){
		start=new Date(end);
		start.setDate(start.getDate()-30);
	}

	getJSON(window.localStorage.ctx+'/statistics/modalityRevernue',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '近30天收入（设备类型）',
					subtext: json.data.startdate+' - '+json.data.enddate+' 总共：'+json.data.total,
					left: 'center',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 40,
					bottom: 20
				},
				tooltip: {
					trigger: 'item'
				},
				legend: {
					top: '20%',
					orient: 'vertical',
					left: 'right'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", true);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(modalityRevernue_30Days);
							}
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				series: [
					{
						name: '收入',
						type: 'pie',
						radius: ['25%', '50%'],
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						},
						data: json.data.datas
//				      		[
//					        { value: 1048, name: '西门子CT1' },
//					        { value: 735, name: '西门子MR1' },
//					        { value: 580, name: '西门子MR2' },
//					        { value: 484, name: '西门子CT2' },
//				      	]
					}
				]
			};
			myChart_modalityRevernue30Days.setOption(option);
			myChart_modalityRevernue30Days.on('click', function(params) {
				// console.log(params)
				end.setDate(end.getDate()-1);
				eachEquipmentRevernue_30Days(start,end,'',params.name,null,null);
			});
		}
	);
}

function patientSourceRevernue_30Days(start,end,theme){
	var container= document.getElementById("container_patientSourceRevernue30Days");
	if(container==null){
		return;
	}
	if(myChart_patientSourceRevernue30Days==null){
		myChart_patientSourceRevernue30Days = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	if(start==null){
		start=new Date(end);
		start.setDate(start.getDate()-30);
	}

	getJSON(window.localStorage.ctx+'/statistics/patientSourceRevernue',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '近30天收入（患者来源）',
					subtext: json.data.startdate+' - '+json.data.enddate+' 总共：'+json.data.total,
					left: 'center',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 40,
					bottom: 20
				},
				tooltip: {
					trigger: 'item'
				},
				legend: {
					top: '20%',
					orient: 'vertical',
					left: 'right'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", true);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(patientSourceRevernue_30Days);
							}
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				series: [
					{
						name: '收入',
						type: 'pie',
						radius: ['25%', '50%'],
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						},
						data: json.data.datas
					}
				]
			};
			myChart_patientSourceRevernue30Days.setOption(option);
			myChart_patientSourceRevernue30Days.on('click', function(params) {
				// console.log(params)
				end.setDate(end.getDate()-1);
				eachEquipmentRevernue_30Days(start,end,'',null,params.data.code,params.name);
			});
		}
	);
}

function eachEquipmentRevernue_30Days(start,end,theme,modality,patientsource,patientsource_display){

	var  container= document.getElementById("container_eachEquipRevernue30Days");
	if(container==null){
		return;
	}
	if(myChart_eachEquipRevernue30Days==null){
		myChart_eachEquipRevernue30Days = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	if(start==null){
		start=new Date(end);
		start.setDate(start.getDate()-30);
	}
	getJSON(window.localStorage.ctx+'/statistics/eachEquipmentRevernue30Days',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid,
			modality: modality||'',
			patientsource: patientsource||''
		},
		function(json){
//			console.log(json)
			var option = {
				title: {
					text: '近30天收入',
					subtext: modality||patientsource_display||'',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 50,
					bottom: 30
				},
				tooltip: {
					trigger: 'axis'
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", true);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(eachEquipmentRevernue_30Days);
							}
						},
						restore: {
							show: true,
							title: '还原'
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				legend: {
					data: json.data.legend,//['西门子CT1', '西门子CT2', '西门子MR1', '西门子MR2', '西门子MR3', '西门子MR4'],
					right:'5%',
					selected:json.data.legend_selected,
					selector: [
						{
							type: 'all',
							title: '全选'
						},
						{
							type: 'inverse',
							title: '反选'
						}
					]
				},
				xAxis: {
					type: 'category',
					data: json.data.category//['4.08', '4.09', '4.10', '4.11', '4.12', '4.13', '4.14', '4.15', '4.16', '4.17', '4.18', '4.19', '4.20']
				},
				yAxis: {
					type: 'value'
				},
				series: json.data.series
//			  		[
//				    {
//				    	name: '西门子CT1',
//				    	data: [620, 232, 901, 934, 1290, 1330, 820, 932, 901, 934, 1290, 1330, 1320],
//				      	type: 'line',
//				      	smooth: true
//				    }
//			  	]
			};
			myChart_eachEquipRevernue30Days.setOption(option);
		}
	);
}

function positiveRate(start,end,theme){

	var  container= document.getElementById("container_positiveRate");
	if(container==null){
		return;
	}
	if(myChart_positiveRate==null){
		myChart_positiveRate = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	if(start==null){
		start=new Date(end);
		start.setDate(start.getDate()-7);
	}

	getJSON(window.localStorage.ctx+'/statistics/positiveRate',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			// console.log(json)
//			var option = {
//				title: {
//					text: '阴阳率-阳性率',
//					subtext: start.format('yyyy-MM-dd')+' - '+end.format('yyyy-MM-dd'),
//					padding: [10, 5, 5, 5],
//					textStyle: {
//						fontSize: 16
//					}
//				},
//				grid: {
//					top: 55,
//					bottom: 30
//				},
//				tooltip: {
//			    	trigger: 'axis',
//			    	axisPointer: {
//			      		type: 'shadow'
//			    	}
//			  	},
//			  	toolbox: {
//					orient: 'vertical',
//					showTitle: false,
//					tooltip: {
//				        show: true,
//				        formatter: function (param) {
//			                return '<div>' + param.title + '</div>';
//			            },
//				    },
//			        feature: {
//			        	myTool_DateRange: {
//			                show: true,
//			                title: '选择时间',
//			                icon: 'image://themes/icons/calendar.png',
//			                onclick: function (){
//			                	localStorage.setItem("enable_db_start_key", true);
//			                	localStorage.setItem("enable_db_end_key", true);
//			                	showDateRangeDialog(positiveRate);
//			                }
//			            },
//			            saveAsImage: {
//			            	show: true,
//			            	title: '保存为图片'
//			            }
//			        }
//			    },
//			  	legend: {},
//			  	yAxis: {
//			    	type: 'value',
//			    	axisLabel: {
//			            formatter: '{value} %'
//			        }
//			  	},
//			  	xAxis: {
//			    	type: 'category',
//			    	name: '科室',
//			    	data: json.data.category//['创伤骨科门诊', '创伤骨科专家门诊', '儿内二科门诊', '肝胆胰腺外科门诊', '感染科病房', '关节骨科病房','急诊科']
//			    	
//			  	},
//			  	series: json.data.series
////			  		[
////				  	{
////				    	name: 'CT',
////				    	type: 'bar',
////				      	data: [50, 64, 75, 12, 33, 80, 50]
////				  	},
//
//			};

			var option = {
				title: {
					text: '阴阳性',
					subtext: start.format('yyyy-MM-dd')+' - '+end.format('yyyy-MM-dd'),
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'shadow'
					}
				},
				toolbox: {
					orient: 'vertical',
					showTitle: false,
					tooltip: {
						show: true,
						formatter: function (param) {
							return '<div>' + param.title + '</div>';
						},
					},
					feature: {
						myTool_DateRange: {
							show: true,
							title: '选择时间',
							icon: 'image://themes/icons/calendar.png',
							onclick: function (){
								localStorage.setItem("enable_db_start_key", true);
								localStorage.setItem("enable_db_end_key", true);
								showDateRangeDialog(positiveRate);
							}
						},
						saveAsImage: {
							show: true,
							title: '保存为图片'
						}
					}
				},
				legend: {},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '3%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					data: json.data.category,//['科室科室科室科室科室1', '科室1', '科室1', '科室1', '科科室科室科室科室室1', '科室1', '科室1','科室1', '科科室科室科室室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1','科室1', '科室1', '科室1', '科室1', '科室1', '科室1', '科室1'],
					axisLabel: { interval: 0, rotate: 60 },
					max: 50
				},
				yAxis: {
					type: 'value'
				},
				series: json.data.series
//				[
//				    {
//				      name: '阳性数',
//				      type: 'bar',
//				      stack: 'total',
//				      emphasis: {
//				        focus: 'series'
//				      },
//				      data: [320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320,320, 302, 301, 334, 390, 330, 320]
//				    },
//				    {
//				      name: '阴性数',
//				      type: 'bar',
//				      stack: 'total',
//				      emphasis: {
//				        focus: 'series'
//				      },
//				      data: [120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210]
//				    }
//
//				]
			};
			myChart_positiveRate.setOption(option);
		}
	);
}

function onlineTime(start,end,theme){

	var  container= document.getElementById("container_online_time");
	if(container==null){
		return;
	}
	if(myChart_onlineTime==null){
		myChart_onlineTime = echarts.init(container,theme);
	}
	if(end==null){
		end=new Date();
	}
	end.setDate(end.getDate()+1);
	if(start==null){
		start=new Date(end);
		start.setDate(start.getDate()-1);
	}
	getJSON(window.localStorage.ctx+'/statistics/onlineTime',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			// console.log("在线时长")
			// console.log(json)
			var option = {
				title: {
					text: '在线时长(分钟)',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 16
					}
				},
				grid: {
					top: 30,
					bottom: 30
				},
				xAxis: {
					max: 'dataMax'
				},
				yAxis: {
					type: 'category',
					data: json.data.category,
					inverse: true,
					animationDuration: 300,
					animationDurationUpdate: 300
					//,
					//max: 20 // only the largest 3 bars will be displayed
				},
				series: [
					{
						realtimeSort: true,
						type: 'bar',
						data: json.data.data,
						label: {
							show: true,
							position: 'right',
							valueAnimation: true
						}
					}
				],
				legend: {
					show: true
				},
				animationDuration: 0,
				animationDurationUpdate: 3000,
				animationEasing: 'linear',
				animationEasingUpdate: 'linear'
			};
			myChart_onlineTime.setOption(option);
			setInterval(function () {
				pollingOnlineTime(dashboard_institutionid);
			}, 30000);//轮询间隔30s
		}
	);
}

function pollingOnlineTime(dashboard_institutionid) {
	var end=new Date();
	end.setDate(end.getDate()+1);
	var start=new Date(end);
	start.setDate(start.getDate()-1);
	// console.log('Polling Online Time：'+new Date());
	getJSON(window.localStorage.ctx+'/statistics/onlineTime',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:dashboard_institutionid
		},
		function(json){
			myChart_onlineTime.setOption({
				series: [
					{
						type: 'bar',
						data: json.data.data
					}
				]
			});
		}
	);
}


