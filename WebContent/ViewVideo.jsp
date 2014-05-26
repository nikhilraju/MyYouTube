<%@page import="video.util.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="video.util.*"%>
<%@ page import="java.util.*"%>
<%
	if ("html5".equalsIgnoreCase(request.getParameter("vidPlayer"))) {
%>
<!DOCTYPE html>
<%
	} else {
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>MyYouTube</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script type='text/javascript'
	src='https://d3jqa6xvp7udni.cloudfront.net/jwplayer.js'></script>
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
		String rtmpUrl = "rtmp://s2pd3pnzg0hx8z.cloudfront.net/cfx/st/";
		String httpUrl = "https://d3jqa6xvp7udni.cloudfront.net/";
		String vidPlayer = request.getParameter("videoPlayer");
		int videoId = Integer.parseInt(request.getParameter("videoid"));
		// 		int userId = Integer.parseInt((String) session
		// 				.getAttribute("userid"));
		ConnectDB d = new ConnectDB();
		Video v = d.getVideo(videoId);
		String userid1 = request.getParameter("userid");
		int userid = 1;
		if (!"".equalsIgnoreCase(userid1) && userid1 != null)
			userid = Integer.parseInt(userid1);
		if ("html5".equalsIgnoreCase(vidPlayer)) {
			httpUrl += v.getVname();
		} else {
			rtmpUrl += v.getVname();
		}
	%>
	<script type="text/javascript">
$(function() {
	$("#rate")
	.button()
	.click(function() {
	      $.ajax({
	             type: "POST",
	             url: "RatingServlet",
	             data: {'videoid': <%=v.getVideoid()%>, 'userid':<%=userid%>, 'rating': $("#ratingVal").val()},
	             success: function(msg) {
	                $("#ratingId").html(msg);
	             }
	          });
	});
	$("#changePlayer")
	.button()
	.click(function(){
		<%if(!"html5".equalsIgnoreCase(vidPlayer)){%>
			var url = "/MyYouTube/ViewVideo.jsp?videoid="+<%=v.getVideoid()%>+"&userid="+<%=userid%>+"&videoPlayer=html5";
		<%} else{%>
		var url = "/MyYouTube/ViewVideo.jsp?videoid="+<%=v.getVideoid()%>+"&userid="+<%=userid%>+"&videoPlayer=jwplayer";		
		<%}%>
		window.location.href = url;
	})
});
</script>
	<table width="900" border="0" align="center" cellpadding="0"
		cellspacing="0">
		<tr>
			<td width="300" height="80" class="logo">My YouTube</td>
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
		<!-- 		<tr> -->
		<!-- 			<td rowspan="2" class="filler"></td> -->
		<!-- 			<td class="featured-heading">Feature Video</td> -->
		<!-- 		</tr> -->
		<!-- 		<tr> -->
		<!-- 			<td class="featured-video"><iframe width="580" height="265" -->
		<!-- 					src="//www.youtube.com/embed/4T7bYJq5i2M" frameborder="0" -->
		<!-- 					allowfullscreen></iframe></td> -->
		<!-- 		</tr> -->
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td class="categories-heading">Categories</td>
			<td class="recent-heading"><p>Featured Videos</p>
			<button id="changePlayer" onclick="changePlayer(<%=videoId%>, <%=userid%>)">
			<% if("html5".equalsIgnoreCase(vidPlayer)) {%>
			Change Player to JWPlayer
			<%}else{%>
			Change Player to HTML5
			<%} %>
			</button>
			</td>
		</tr>
		<tr>
			<td><div id="category">
					<ul>
						<li><a href="/MyYouTube/index.jsp?userid=<%=userid%>">General</a></li>
					</ul>
				</div></td>
			<td valign="top"><table width="90%" border="0" align="center"
					cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<%
								if ("html5".equalsIgnoreCase(vidPlayer)) {
							%> <video width="580" height="480" controls>
								<source src="<%=httpUrl%>" type="<%=v.getExtension()%>"></source>
								Your Browser doesn't support HTML5
							</video>
							<%
								} else {
							%>
							<div id='mediaplayer'></div> <script type="text/javascript">
						jwplayer('mediaplayer').setup({
							file : "<%=rtmpUrl%>",
									width : "580",
									height : "480"
								});
							</script> <%
 	}
 %>
						</td>
					</tr>
					<tr>
						<td id="ratingId" class="rating">Current Rating :<%=v.getRating()%>/5
						</td>
					</tr>
					<tr>
						<td>Rate this video : <select id="ratingVal">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
						</select>
							<button id="rate">Submit your Rating</button></td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td colspan="2" class="copyright">&copy; Copyright 2013 aj2568
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
