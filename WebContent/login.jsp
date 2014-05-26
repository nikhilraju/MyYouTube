<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login to MyYoutube</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script type="text/javascript">
	$(function() {
		var name = $("#name"), password = $("#password"), allFields = $([])
				.add(name).add(password), tips = $(".validateTips");

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
		$("#login-form")
				.dialog(
						{
							autoOpen : true,
							height : 300,
							width : 350,
							modal : true,
							dialogClass : "no-close",
							buttons : {
								"Login" : function() {
									var bValid = true;
									allFields.removeClass("ui-state-error");

									bValid = bValid
											&& checkLength(name, "username", 3,
													16);
									bValid = bValid
											&& checkLength(password,
													"password", 5, 16);

									bValid = bValid
											&& checkRegexp(name,
													/^[a-z]([0-9a-z_])+$/i,
													"Username may consist of a-z, 0-9, underscores, begin with a letter.");
									// From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
									bValid = bValid
											&& checkRegexp(password,
													/^([0-9a-zA-Z])+$/,
													"Password field only allow : a-z 0-9");

									if (bValid) {
										$.post("Login", {'name': $("#name").val(), 'password': $("#password").val()}, function(data) {
											data = data.replace(
													/(\r\n|\n|\r)/gm, "");
											if(data == "failure")
												$(".result").html("Please try again!");
											else
												window.location.href=data;
											});
									return true;
									}
									
								},
							},
						});
	});
</script>
</head>
<body>
	<div id="login-form" title="Login to MyYouTube">
		<p class="validateTips">All form fields are required.</p>

		<form action="Login" id="Login" method="post">
			<fieldset>
				<label for="name">Name</label> <input type="text" name="name"
					id="name" class="text ui-widget-content ui-corner-all" /> <label
					for="password">Password</label> <input type="password"
					name="password" id="password" value="">
				<div class="result"></div>
			</fieldset>
		</form>
	</div>
</body>
</html>