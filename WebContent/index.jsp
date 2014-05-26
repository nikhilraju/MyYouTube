<%@page import="video.util.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="video.util.*"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Hotel Management System</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link href="template.css" rel="stylesheet" type="text/css" />
<style type="text/css">
<!--
.rating {
	font-family: Tahoma, Geneva, sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #333;
	background-image: url(images/rating-icon.gif);
	padding-right: 10px;
	background-repeat: no-repeat;
}

body {
	font-size: 62.5%;
}

label,input {
	display: block;
}

input.text {
	margin-bottom: 12px;
	width: 95%;
	padding: .4em;
}

fieldset {
	padding: 0;
	border: 0;
	margin-top: 25px;
}

h1 {
	font-size: 1.2em;
	margin: .6em 0;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
}

.no-close .ui-dialog-titlebar-close {
	display: none;
}
-->
</style>
<script type="text/javascript">
	function deleteVideo(videoId, userid){
		$.post("DeleteVideo", {'videoid': videoId, 'userid': userid}, function(data){
			window.location.href = data;
		});
	}
	$(function() {
		var title = $("#title"), description = $("#description"), file = $("#file"), allFields = $(
				[]).add(title).add(description), tips = $(".validateTips");

		function updateTips(t) {
			tips.text(t).addClass("ui-state-highlight");
			setTimeout(function() {
				tips.removeClass("ui-state-highlight", 1500);
			}, 500);
		}

		function checkLength(o, n, min, max) {
			if (o.val().length > max || o.val().length < min) {
				o.addClass("ui-state-error");
				updateTips("Length of " + n + " must be between " + min
						+ " and " + max + ".");
				return false;
			} else {
				return true;
			}
		}

		function checkRegexp(o, regexp, n) {
			if (!(regexp.test(o.val()))) {
				o.addClass("ui-state-error");
				updateTips(n);
				return false;
			} else {
				return true;
			}
		}

		$("#dialog-form").dialog(
				{
					autoOpen : false,
					height : 300,
					width : 350,
					modal : true,
					buttons : {
						"Upload Video" : function() {
							var bValid = true;
							allFields.removeClass("ui-state-error");

							bValid = bValid
									&& checkLength(title, "title", 6, 16);
							bValid = bValid
									&& checkLength(description, "description",
											10, 80);
							var ext = $('#file').val().split('.').pop().toLowerCase();
							if($.inArray(ext, ['mp4','avi']) == -1) {
							    alert('Invalid extension!');
							    file.addClass("ui-state-error");
							    bValid = false;
							}							
							if (bValid) {
								$("#upload-form").submit();
							}
						},
						Cancel : function() {
							$(this).dialog("close");
						}
					},
					close : function() {
						allFields.val("").removeClass("ui-state-error");
					}
				});

		$("#upload-video").click(function() {
			$("#dialog-form").dialog("open");
			return false;
		});
	});
</script>
</head>
<body>
	<%
		ArrayList<Video> vl = new ArrayList<Video>();
		String myVideos = request.getParameter("myvideos");
		if ("".equalsIgnoreCase(myVideos) || myVideos == null)
			myVideos = (String) request.getAttribute("myvideos");
		String userId = request.getParameter("userid");
		int userid = -1;
		if (null != userId && !"".equalsIgnoreCase(userId)) {
			userid = Integer.parseInt(userId);
		}
		if ("true".equalsIgnoreCase(myVideos)) {
			ConnectDB d = new ConnectDB();
			vl = d.getVideoList(userid);
		} else {
			ConnectDB d = new ConnectDB();
			vl = d.getVideoList(-1);
		}
	%>
	<table width="900" border="0" align="center" cellpadding="0"
		cellspacing="0">
		<tr>
			<td width="300" height="80" class="logo">Hotel Management System</td>
			<td width="600"><div id="menu">
					<ul>
						<li class="menu-selected"><a
							href="/MyYouTube/index.jsp?userid=<%=userid%>">Home</a></li>
						<li><a href="#" id="upload-video">Upload Video</a></li>
						<li><a
							href="/MyYouTube/index.jsp?myvideos=true&userid=<%=userid%>">My
								Videos</a></li>
						<li><a href="/MyYouTube/index.jsp?userid=<%=userid%>">All
								Videos</a></li>
					</ul>
				</div></td>
		</tr>
		
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td class="categories-heading">Categories</td>
			<td class="recent-heading"><p>Recent Videos</p></td>
		</tr>
		<tr>
			<td><div id="category">
					<ul>
						<li><a href="/MyYouTube/index.jsp?userid=<%=userid%>">General</a></li>
					</ul>
				</div></td>
			<td valign="top"><table width="90%" border="0" align="center"
					cellpadding="0" cellspacing="0">
					<%
						for (Video v : vl) {
					%>
					<tr>
						<!-- <td rowspan="3" class="video-icon">&nbsp;</td> -->
						<td class="video-title"><a
							href="ViewVideo.jsp?videoid=<%=v.getVideoid()%>&userid=<%=userid%>"><%=v.getTitle()%></a></td>
						<td class="rating">Rating : <%=v.getRating()%>/5
						</td>
					</tr>
					<tr>
						<td class="video-description"><%=v.getDescription()%></td>
						<%
							if ("true".equalsIgnoreCase(myVideos)) {
						%>
						<td><button id="delete"
								onclick="deleteVideo(<%=v.getVideoid()%>, <%=userid%>)">Delete
								this video</button></td>
						<%
							}
						%>

					</tr>
					<%
						}
					%>
				</table></td>
		</tr>
		<tr>
			<td colspan="2" class="copyright">&copy; Copyright 2013 ac3647
				nr2483</td>
		</tr>
	</table>

	<div id="dialog-form" title="Upload a video">
		<p class="validateTips">All form fields are required.</p>

		<form id="upload-form" action="FileUploadServlet" method="post"
			enctype="multipart/form-data">
			<fieldset>
				<label for="name">Enter your Video title</label> <input type="text"
					name="title" id="title"
					class="text ui-widget-content ui-corner-all" /> <label
					for="description">Enter your Video Description</label> <input
					type="text" name="description" id="description" value=""
					class="text ui-widget-content ui-corner-all" /> <input
					type="hidden" name="userid" value="<%=userid%>"></input><label
					for="video">Select your Video file</label> <input type="file"
					name="file" id="file" class="text ui-widget-content ui-corner-all" />
			</fieldset>
		</form>
	</div>
</body>
</html>
