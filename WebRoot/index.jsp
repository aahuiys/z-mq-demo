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
				var count = gel("id").value;
<%--				for (var i = 0; i < count; i++) {--%>
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
					var id = Math.floor(Math.random() * count) + 1;
					xmlHttp.send("id=" + id);
					gel("sendinfo").innerHTML = "[QUEUE]Send No." + id + " message." + "<br />" + gel("sendinfo").innerHTML;
<%--				}--%>
			};
		};
		
		function clearInfo() {
			gel("sendinfo").innerHTML = "";
			gel("showinfo").innerHTML = "";
		}
		
		function getRequest() {
			var count = gel("id").value;
			var xmlHttp = createXmlHttp();
			var url = "<%=basePath%>sendMessage?count=" + count;
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
			gel("sendinfo").innerHTML = "Apply " + count + " message, please wait." + "<br />" + gel("sendinfo").innerHTML;
		}
	</script>
  </head>
  
  <body align="center">
  	<div>
  		<input type="text" id="id" value="1000" />
  		<input type="button" id="send" value="POST" />
  		<input type="button" value="GET" onclick="getRequest()" />
  		<input type="button" value="CLEAR" onclick="clearInfo()" />
  	</div>
  	<div style="margin: auto;">
  		<div id="sendinfo" style="float: left; width: 25%; padding-left: 25%;"></div>
  		<div id="showinfo" style=" width: 25%; padding-left: 50%;"></div>
  	</div>
  </body>
</html>
