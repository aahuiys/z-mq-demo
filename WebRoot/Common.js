function createXmlHttp() {//创建xhr对象兼容方法
	var xhobj = false;
	try {
		xhobj = new ActiveXObject("MSXML2.XMLHTTP");//ie msxml3.0+
	} catch(e1) {
		try {
			xhobj = new ActiveXObject("Microsoft.XMLHTTP");//ie msxml2.6
		} catch(e2) {
			xhobj = false;
		}
	}
	if(!xhobj && typeof XMLHttpRequest != 'undefined') {//Firefox,Opera8.0+,Safari,ie10
		xhobj = new XMLHttpRequest();
	}
	return xhobj;
}

function gel(id) {
	return document.getElementById(id);
}