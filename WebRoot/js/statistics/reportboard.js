
var myChart_noresult_report_modality, // 各个设备类型未写的报告
	myChart_report_modality, // 设备分类
	myChart_report_patientsource, // 来源分类
	myChart_noaudit_report_modality, // 各个设备类型未审的报告
	myChart_imagequality, // 图像评级
	myChart_reportquality, // 报告质量
	myChart_diagnosis_coincidence, // 诊断符合
	myChart_pos_or_neg, // 阴阳性
	datatime, // 具体某天时间
  	reportboard_institutionid; // 机构id
/**
 * theme 主题，可选'dark'
 */
function reportboardOnload(datatime, theme){
	if(!theme){
		theme=$('#chart_theme').val();
	}
	if (datatime==null || !(datatime instanceof Date)) {
		datatime = new Date();
	}
	reportData(datatime); // 当天各个设备类型的报告数量和审核数量
	containerReportModality(datatime, theme); // 设备分类
	containerReportPatientsource(datatime, theme); // 来源分类
	noresult_report_modality(datatime, theme); // 各个设备类型未写的报告
	noaudit_report_modality(datatime, theme); // 各个设备类型未审的报告
	imagequality(datatime, null, theme); // 图像评级
	reportquality(datatime, null, theme); // 报告质量
	diagnosisCoincidence(datatime, null, theme); // 诊断符合
	posOrNeg(datatime, null, theme); // 阴阳性
}

/**
 *  当天各个设备类型的报告数量
 */
function reportData(time) {
	if (time==null) {
		time = new Date();
	}
	$('#report_time_span').text(time.format('yyyy-MM-dd'));
	getJSON(window.localStorage.ctx+'/statistics/reportDataOfToday',
		{
			start: time.format('yyyy-MM-dd'),
			end: time.format('yyyy-MM-dd'),
			institutionid: reportboard_institutionid
		},
		function(json){
			$('#rt_count').html(json.data.report_count);
			$('#rt_noresult_count').html(json.data.report_noresult_count);
			$('#rt_no_audit_count').html(json.data.report_no_audit_count);
			$('#rt_audit_count').html(json.data.report_audit_count);
		}
	);
}

/**
 *  更改机构
 */
function report_changeInstitution_db(newValue){
	reportboard_institutionid=newValue;
	reportboardOnload();
}

/**
 *  选择具体的一天
 */
function selectDatatime() {
	$('#common_dialog').dialog(
			{
				title : '选择时间',
				width : 350,height : 350,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
				border: 'thin',
				href : window.localStorage.ctx+'/view/statistics/datetime.html',
				modal : true,
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){
						reportboardOnload(datatime);
						$('#common_dialog').dialog('close');
					}
				},{
					text: $.i18n.prop('cancel'),
					width:60,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
	});
}

/**
 *  设备分类
 */
function containerReportModality(time, theme) {
	var container= document.getElementById("container_report_modality");
	if(container==null){
		return;
	}
	if(time==null){
		time=new Date();
	}
	if (myChart_report_modality==null) {
		myChart_report_modality = echarts.init(container,theme);
	}
	
	getJSON(window.localStorage.ctx+'/statistics/reportModality',
		{
			start: time.format('yyyy-MM-dd'),
			end: time.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var option = {
				tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
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
				    type: 'value'
				},
				yAxis: {
				    type: 'category',
				    name: time.format('yyyy-MM-dd'),
				    data: ['其他', 'DR', 'MR', 'CT']
				},
				series: [
				    {
				      name: '未写报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_noresult
				    },
				    {
				      name: '初步报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_pre_report
				    },
				    {
				      name: '未审报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_no_audit
				    },
				    {
				      name: '已审报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_audit
				    }
				]
			};
			myChart_report_modality.setOption(option);
		}
	);
}

/**
 *  来源分类
 */
function containerReportPatientsource(time, theme) {
	var container= document.getElementById("container_report_patientsource");
	if(container==null){
		return;
	}
	if(time==null){
		time=new Date();
	}
	if (myChart_report_patientsource==null) {
		myChart_report_patientsource = echarts.init(container,theme);
	}
	
	getJSON(window.localStorage.ctx+'/statistics/reportPatientsource',
		{
			start: time.format('yyyy-MM-dd'),
			end: time.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var option = {
				tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
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
				    type: 'value'
				},
				yAxis: {
				    type: 'category',
				    name: time.format('yyyy-MM-dd'),
				    data: ['体检', '急诊', '住院', '门诊']
				},
				series: [
				    {
				      name: '未写报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_noresult
				    },
				    {
				      name: '初步报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_pre_report
				    },
				    {
				      name: '未审报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_no_audit
				    },
				    {
				      name: '已审报告',
				      type: 'bar',
				      stack: 'total',
				      label: {
				        show: true
				      },
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.data_audit
				    }
				]
			};
			myChart_report_patientsource.setOption(option);
		}
	);
}

/**
 * 各个设备类型未写的报告
 */
function noresult_report_modality(time, theme){
	var container= document.getElementById("container_noresult_report_modality");
	if(container==null){
		return;
	}
	if(time==null){
		time=new Date();
	}
	if (myChart_noresult_report_modality==null) {
		myChart_noresult_report_modality = echarts.init(container,theme);
	}
	
	getJSON(window.localStorage.ctx+'/statistics/noresultReportModality',
		{
			start: time.format('yyyy-MM-dd'),
			end: time.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '各个设备未写报告情况',
					subtext: json.data.startdate,
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
			            saveAsImage: {
			            	show: true,
			            	title: '保存为图片'
			            }
			        }
			    },
			  	series: [
			    	{
			      		name: '未写报告',
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
			myChart_noresult_report_modality.setOption(option);
		}
	);
}

/**
 *  未审报告
 */
function noaudit_report_modality(time, theme){
	var container= document.getElementById("container_noaudit_report_modality");
	if(container==null){
		return;
	}
	if(time==null){
		time=new Date();
	}
	if (myChart_noaudit_report_modality==null) {
		myChart_noaudit_report_modality = echarts.init(container,theme);
	}
	
	getJSON(window.localStorage.ctx+'/statistics/noauditReportModality',
		{
			start: time.format('yyyy-MM-dd'),
			end: time.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var option = {
				title: {
					text: '各个设备未审报告情况',
					subtext: json.data.startdate,
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
			            saveAsImage: {
			            	show: true,
			            	title: '保存为图片'
			            }
			        }
			    },
			  	series: [
			    	{
			      		name: '未审报告',
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
			myChart_noaudit_report_modality.setOption(option);
		}
	);
}

/**
 *  图像评级
 */
function imagequality(start, end, theme){
	var container= document.getElementById("container_imagequality");
	if(container==null){
		return;
	}
	if(end==null){
		end=new Date();
	}
	if (myChart_imagequality==null) {
		myChart_imagequality = echarts.init(container,theme);
	}
	end.setDate(end.getDate()+1);
	var start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/imagequality',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var app = {};
			const posList = [
	             'left',
	             'right',
	             'top',
	             'bottom',
	             'inside',
	             'insideTop',
	             'insideLeft',
	             'insideRight',
	             'insideBottom',
	             'insideTopLeft',
	             'insideTopRight',
	             'insideBottomLeft',
	             'insideBottomRight'
	           ];
	           app.configParameters = {
	             rotate: {
	               min: -90,
	               max: 90
	             },
	             align: {
	               options: {
	                 left: 'left',
	                 center: 'center',
	                 right: 'right'
	               }
	             },
	             verticalAlign: {
	               options: {
	                 top: 'top',
	                 middle: 'middle',
	                 bottom: 'bottom'
	               }
	             },
	             position: {
	               options: posList.reduce(function (map, pos) {
	                 map[pos] = pos;
	                 return map;
	               }, {})
	             },
	             distance: {
	               min: 0,
	               max: 100
	             }
	           };
	           app.config = {
	             rotate: 90,
	             align: 'left',
	             verticalAlign: 'middle',
	             position: 'insideBottom',
	             distance: 15,
	             onChange: function () {
	               const labelOption = {
	                 rotate: app.config.rotate,
	                 align: app.config.align,
	                 verticalAlign: app.config.verticalAlign,
	                 position: app.config.position,
	                 distance: app.config.distance
	               };
	               myChart.setOption({
	                 series: [
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   }
	                 ]
	               });
	             }
	           };
	           const labelOption = {
	             show: true,
	             position: app.config.position,
	             distance: app.config.distance,
	             align: app.config.align,
	             verticalAlign: app.config.verticalAlign,
	             rotate: app.config.rotate,
	             formatter: '{c}  {name|{a}}',
	             fontSize: 16,
	             rich: {
	               name: {}
	             }
	           };
			var option = {
			  tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }
				  },
				  legend: {
				    data: json.data.syscode
				  },
				  toolbox: {
				    show: true,
				    orient: 'vertical',
				    left: 'right',
				    top: 'center',
				    feature: {
			        	myTool_DateRange: {
			                show: true,
			                title: '选择时间',
			                icon: 'image://themes/icons/calendar.png',
			                onclick: function (){
			                	localStorage.setItem("enable_db_start_key", true);
			                	localStorage.setItem("enable_db_end_key", true);
			                	showDateRangeDialog(imagequality);
			                }
			            },
				        mark: { show: true },
				        dataView: { show: true, readOnly: false },
				        magicType: { show: true, type: ['line', 'bar', 'stack'] },
				        restore: { show: true, title: '还原' },
				        saveAsImage: { show: true, title: '保存为图片' }
				    }
				  },
				  xAxis: [
				    {
				      type: 'category',
				      axisTick: { show: false },
				      data: json.data.category
				    }
				  ],
				  yAxis: [
				    {
				      type: 'value'
				    }
				  ],
				  series: [
				    {
				      name: json.data.syscode[0],
				      type: 'bar',
				      barGap: 0,
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[0]]
				    },
				    {
				      name: json.data.syscode[1],
				      type: 'bar',
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[1]]
				    },
				    {
					      name: json.data.syscode[2],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[2]]
					    },
				    {
					      name: json.data.syscode[3],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[3]]
					}
				  ]
			};
			myChart_imagequality.setOption(option);
		}
	);
}

/**
 *  报告质量
 */
function reportquality(start, end, theme){
	var container= document.getElementById("container_reportquality");
	if(container==null){
		return;
	}
	if(end==null){
		end=new Date();
	}
	if (myChart_reportquality==null) {
		myChart_reportquality = echarts.init(container,theme);
	}
	end.setDate(end.getDate()+1);
	var start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/reportquality',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var app = {};
			const posList = [
	             'left',
	             'right',
	             'top',
	             'bottom',
	             'inside',
	             'insideTop',
	             'insideLeft',
	             'insideRight',
	             'insideBottom',
	             'insideTopLeft',
	             'insideTopRight',
	             'insideBottomLeft',
	             'insideBottomRight'
	           ];
	           app.configParameters = {
	             rotate: {
	               min: -90,
	               max: 90
	             },
	             align: {
	               options: {
	                 left: 'left',
	                 center: 'center',
	                 right: 'right'
	               }
	             },
	             verticalAlign: {
	               options: {
	                 top: 'top',
	                 middle: 'middle',
	                 bottom: 'bottom'
	               }
	             },
	             position: {
	               options: posList.reduce(function (map, pos) {
	                 map[pos] = pos;
	                 return map;
	               }, {})
	             },
	             distance: {
	               min: 0,
	               max: 100
	             }
	           };
	           app.config = {
	             rotate: 90,
	             align: 'left',
	             verticalAlign: 'middle',
	             position: 'insideBottom',
	             distance: 15,
	             onChange: function () {
	               const labelOption = {
	                 rotate: app.config.rotate,
	                 align: app.config.align,
	                 verticalAlign: app.config.verticalAlign,
	                 position: app.config.position,
	                 distance: app.config.distance
	               };
	               myChart.setOption({
	                 series: [
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   }
	                 ]
	               });
	             }
	           };
	           const labelOption = {
	             show: true,
	             position: app.config.position,
	             distance: app.config.distance,
	             align: app.config.align,
	             verticalAlign: app.config.verticalAlign,
	             rotate: app.config.rotate,
	             formatter: '{c}  {name|{a}}',
	             fontSize: 16,
	             rich: {
	               name: {}
	             }
	           };
			var option = {
			  tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }
				  },
				  legend: {
				    data: json.data.syscode
				  },
				  toolbox: {
				    show: true,
				    orient: 'vertical',
				    left: 'right',
				    top: 'center',
				    feature: {
			        	myTool_DateRange: {
			                show: true,
			                title: '选择时间',
			                icon: 'image://themes/icons/calendar.png',
			                onclick: function (){
			                	localStorage.setItem("enable_db_start_key", true);
			                	localStorage.setItem("enable_db_end_key", true);
			                	showDateRangeDialog(reportquality);
			                }
			            },
				        mark: { show: true },
				        dataView: { show: true, readOnly: false },
				        magicType: { show: true, type: ['line', 'bar', 'stack'] },
				        restore: { show: true, title: '还原' },
				        saveAsImage: { show: true, title: '保存为图片' }
				    }
				  },
				  xAxis: [
				    {
				      type: 'category',
				      axisTick: { show: false },
				      data: json.data.category
				    }
				  ],
				  yAxis: [
				    {
				      type: 'value'
				    }
				  ],
				  series: [
				    {
				      name: json.data.syscode[0],
				      type: 'bar',
				      barGap: 0,
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[0]]
				    },
				    {
				      name: json.data.syscode[1],
				      type: 'bar',
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[1]]
				    },
				    {
					      name: json.data.syscode[2],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[2]]
					    },
				    {
					      name: json.data.syscode[3],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[3]]
					}
				  ]
			};
			myChart_reportquality.setOption(option);
		}
	);
}

/**
 *  诊断符合
 */
function diagnosisCoincidence(start, end, theme){
	var container= document.getElementById("container_diagnosis_coincidence");
	if(container==null){
		return;
	}
	if(end==null){
		end=new Date();
	}
	if (myChart_diagnosis_coincidence==null) {
		myChart_diagnosis_coincidence = echarts.init(container,theme);
	}
	end.setDate(end.getDate()+1);
	var start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/diagnosisCoincidence',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var app = {};
			const posList = [
	             'left',
	             'right',
	             'top',
	             'bottom',
	             'inside',
	             'insideTop',
	             'insideLeft',
	             'insideRight',
	             'insideBottom',
	             'insideTopLeft',
	             'insideTopRight',
	             'insideBottomLeft',
	             'insideBottomRight'
	           ];
	           app.configParameters = {
	             rotate: {
	               min: -90,
	               max: 90
	             },
	             align: {
	               options: {
	                 left: 'left',
	                 center: 'center',
	                 right: 'right'
	               }
	             },
	             verticalAlign: {
	               options: {
	                 top: 'top',
	                 middle: 'middle',
	                 bottom: 'bottom'
	               }
	             },
	             position: {
	               options: posList.reduce(function (map, pos) {
	                 map[pos] = pos;
	                 return map;
	               }, {})
	             },
	             distance: {
	               min: 0,
	               max: 100
	             }
	           };
	           app.config = {
	             rotate: 90,
	             align: 'left',
	             verticalAlign: 'middle',
	             position: 'insideBottom',
	             distance: 15,
	             onChange: function () {
	               const labelOption = {
	                 rotate: app.config.rotate,
	                 align: app.config.align,
	                 verticalAlign: app.config.verticalAlign,
	                 position: app.config.position,
	                 distance: app.config.distance
	               };
	               myChart.setOption({
	                 series: [
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   }
	                 ]
	               });
	             }
	           };
	           const labelOption = {
	             show: true,
	             position: app.config.position,
	             distance: app.config.distance,
	             align: app.config.align,
	             verticalAlign: app.config.verticalAlign,
	             rotate: app.config.rotate,
	             formatter: '{c}  {name|{a}}',
	             fontSize: 16,
	             rich: {
	               name: {}
	             }
	           };
			var option = {
			  tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }
				  },
				  legend: {
				    data: json.data.syscode
				  },
				  toolbox: {
				    show: true,
				    orient: 'vertical',
				    left: 'right',
				    top: 'center',
				    feature: {
			        	myTool_DateRange: {
			                show: true,
			                title: '选择时间',
			                icon: 'image://themes/icons/calendar.png',
			                onclick: function (){
			                	localStorage.setItem("enable_db_start_key", true);
			                	localStorage.setItem("enable_db_end_key", true);
			                	showDateRangeDialog(diagnosisCoincidence);
			                }
			            },
				        mark: { show: true },
				        dataView: { show: true, readOnly: false },
				        magicType: { show: true, type: ['line', 'bar', 'stack'] },
				        restore: { show: true, title: '还原' },
				        saveAsImage: { show: true, title: '保存为图片' }
				    }
				  },
				  xAxis: [
				    {
				      type: 'category',
				      axisTick: { show: false },
				      data: json.data.category
				    }
				  ],
				  yAxis: [
				    {
				      type: 'value'
				    }
				  ],
				  series: [
				    {
				      name: json.data.syscode[0],
				      type: 'bar',
				      barGap: 0,
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[0]]
				    },
				    {
				      name: json.data.syscode[1],
				      type: 'bar',
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[1]]
				    },
				    {
					      name: json.data.syscode[2],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[2]]
					    },
				    {
					      name: json.data.syscode[3],
					      type: 'bar',
					      label: labelOption,
					      emphasis: {
					        focus: 'series'
					      },
					      data: json.data.map[json.data.syscode[3]]
					}
				  ]
			};
			myChart_diagnosis_coincidence.setOption(option);
		}
	);
}

/**
 *  阴阳性
 */
function posOrNeg(start, end, theme){
	var container= document.getElementById("container_pos_or_neg");
	if(container==null){
		return;
	}
	if(end==null){
		end=new Date();
	}
	if (myChart_pos_or_neg == null) {
		myChart_pos_or_neg = echarts.init(container,theme);
	}
	end.setDate(end.getDate()+1);
	var start=new Date(end);
	start.setDate(start.getDate()-7);
	getJSON(window.localStorage.ctx+'/statistics/posOrNeg',
		{
			start: start.format('yyyy-MM-dd'),
			end: end.format('yyyy-MM-dd'),
			institutionid:reportboard_institutionid
		},
		function(json){
			var app = {};
			const posList = [
	             'left',
	             'right',
	             'top',
	             'bottom',
	             'inside',
	             'insideTop',
	             'insideLeft',
	             'insideRight',
	             'insideBottom',
	             'insideTopLeft',
	             'insideTopRight',
	             'insideBottomLeft',
	             'insideBottomRight'
	           ];
	           app.configParameters = {
	             rotate: {
	               min: -90,
	               max: 90
	             },
	             align: {
	               options: {
	                 left: 'left',
	                 center: 'center',
	                 right: 'right'
	               }
	             },
	             verticalAlign: {
	               options: {
	                 top: 'top',
	                 middle: 'middle',
	                 bottom: 'bottom'
	               }
	             },
	             position: {
	               options: posList.reduce(function (map, pos) {
	                 map[pos] = pos;
	                 return map;
	               }, {})
	             },
	             distance: {
	               min: 0,
	               max: 100
	             }
	           };
	           app.config = {
	             rotate: 90,
	             align: 'left',
	             verticalAlign: 'middle',
	             position: 'insideBottom',
	             distance: 15,
	             onChange: function () {
	               const labelOption = {
	                 rotate: app.config.rotate,
	                 align: app.config.align,
	                 verticalAlign: app.config.verticalAlign,
	                 position: app.config.position,
	                 distance: app.config.distance
	               };
	               myChart.setOption({
	                 series: [
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   },
	                   {
	                     label: labelOption
	                   }
	                 ]
	               });
	             }
	           };
	           const labelOption = {
	             show: true,
	             position: app.config.position,
	             distance: app.config.distance,
	             align: app.config.align,
	             verticalAlign: app.config.verticalAlign,
	             rotate: app.config.rotate,
	             formatter: '{c}  {name|{a}}',
	             fontSize: 16,
	             rich: {
	               name: {}
	             }
	           };
			var option = {
			  tooltip: {
				    trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }
				  },
				  legend: {
				    data: json.data.syscode
				  },
				  toolbox: {
				    show: true,
				    orient: 'vertical',
				    left: 'right',
				    top: 'center',
				    feature: {
			        	myTool_DateRange: {
			                show: true,
			                title: '选择时间',
			                icon: 'image://themes/icons/calendar.png',
			                onclick: function (){
			                	localStorage.setItem("enable_db_start_key", true);
			                	localStorage.setItem("enable_db_end_key", true);
			                	showDateRangeDialog(posOrNeg);
			                }
			            },
				        mark: { show: true },
				        dataView: { show: true, readOnly: false },
				        magicType: { show: true, type: ['line', 'bar', 'stack'] },
				        restore: { show: true, title: '还原' },
				        saveAsImage: { show: true, title: '保存为图片' }
				    }
				  },
				  xAxis: [
				    {
				      type: 'category',
				      axisTick: { show: false },
				      data: json.data.category
				    }
				  ],
				  yAxis: [
				    {
				      type: 'value'
				    }
				  ],
				  series: [
				    {
				      name: json.data.syscode[0],
				      type: 'bar',
				      barGap: 0,
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[0]]
				    },
				    {
				      name: json.data.syscode[1],
				      type: 'bar',
				      label: labelOption,
				      emphasis: {
				        focus: 'series'
				      },
				      data: json.data.map[json.data.syscode[1]]
				    }
				  ]
			};
			myChart_pos_or_neg.setOption(option);
		}
	);
}

