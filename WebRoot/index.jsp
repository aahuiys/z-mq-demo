<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="Common.js"></script>
	<script type="text/javascript">
		window.onload = function() {
			gel("send").onclick = function() {
				var xmlHttp = createXmlHttp();
				var url = "<%=basePath%>sendMessage";
				xmlHttp.open("POST", url, true);
				//POST方式为请求报文头中添加Content-Type来设置参数的编码组织方式
				xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				xmlHttp.onreadystatechange = function() {
					if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
						var res = xmlHttp.responseText;
						gel("showinfo").innerHTML = res + "<br />" + gel("showinfo").innerHTML;
					}
				};
				var count = gel("id").value;
				var id = Math.floor(Math.random() * count) + 1;
				xmlHttp.send("id=" + id);
				gel("sendinfo").innerHTML = "[QUEUE]Send No." + id + " message." + "<br />" + gel("sendinfo").innerHTML;
			};
		};
		
		function clearInfo() {
			gel("sendinfo").innerHTML = "";
			gel("showinfo").innerHTML = "";
		}
		
		function getRequest(action) {
			if (action == "STATUS" && !autoRefresh) return;
			if (action == "STOP" && !confirm("Do you want stop?")) return;
			var xmlHttp = createXmlHttp();
			var url = "<%=basePath%>sendMessage?action=" + action;
			xmlHttp.open("GET", url, true);
			//GET方式设置浏览器不从缓存获取数据
			xmlHttp.setRequestHeader("If-Modified-Since", 0);
			xmlHttp.onreadystatechange = function() {
				if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
					var res = xmlHttp.responseText;
					gel("showinfo").innerHTML = res + "<br />" + gel("showinfo").innerHTML;
				}
			};
			xmlHttp.send(null);
			if (action == "GET") {
				gel("sendinfo").innerHTML = "Send a request. " + "<br />" + gel("sendinfo").innerHTML;
				autoRefresh = true;
			} else if (action == "STATUS") {
				gel("sendinfo").innerHTML = "Get the current result." + "<br />" + gel("sendinfo").innerHTML;
			} else {
				gel("sendinfo").innerHTML = "Stop all test thread! " + "<br />" + gel("sendinfo").innerHTML;
				autoRefresh = false;
			}
		}
		
		var autoRefresh = false;
		
		function switchRefresh() {
			autoRefresh = !autoRefresh;
		}
		
		setInterval("getRequest('STATUS')", 1000);
	</script>
  </head>
  
  <body align="center">
  	<div>
  		<input type="button" value="CLEAR" onclick="clearInfo()" /><br />
  		Single Test: 
  		<input type="text" id="id" value="1000" />
  		<input type="button" id="send" value="POST" /><br />
  		Long-term Test: 
  		<input type="button" value="GET" onclick="getRequest('GET')" />
<%--  		<input type="button" value="STATUS" onclick="getRequest('STATUS')" />--%>
  		<input type="button" value="SWITCH" onclick="switchRefresh()" />
  		<input type="button" value="STOP" onclick="getRequest('STOP')" />
  	</div>
  	<div style="margin: auto;">
  		<div id="sendinfo" style="float: left; width: 25%; padding-left: 25%;"></div>
  		<div id="showinfo" style=" width: 25%; padding-left: 50%;"></div>
  	</div>
  </body>
</html>
