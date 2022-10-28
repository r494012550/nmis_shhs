// This file is required by the index.html file and will
// be executed in the renderer process for that window.
// No Node.js APIs are available in this process because
// `nodeIntegration` is turned off. Use `preload.js` to
// selectively enable features needed in the rendering
// process.
//console.log(window.electron.readCBImage())
//const element = document.getElementById('image')
//element.src=window.electron.readCBImage()

//document.getElementById("d1").innerHTML="<img src='"+window.electron.readCBImage()+"'>";


function electron_enable(){
	console.log(window.electron_echo)
	return window.electron_echo!=null;
}

function electron_plaza_loaddata(accno,username,pwd,href){
	if(href){
		href=href.replace(/ /g,'%20');
	}
	window.electron_exec_plaza_loaddata.exec(accno,username,pwd,href);
}

function electron_via_open(accno,href){
	if(href){
		href=href.replace(/ /g,'%20');
	}
	window.electron_exec_openvia.exec(accno,href);
}

function electron_print(href){
	if(href){
		href=href.replace(/ /g,'%20');
		href=href.replace(/&/g,'"&"');
	}
	window.electron_exec_print.exec(href);
}

function electron_scan(href){
	if(href){
		href=href.replace(/ /g,'%20');
		href=href.replace(/&/g,'"&"');
	}
	window.electron_exec_scan.exec(href);
}

function electron_openDevTools(){
	return window.electron_exec_openDevTools.open();
}