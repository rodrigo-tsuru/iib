<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="html" />
	<xsl:template match="/">
		<html>
			<header>
			    <meta charset="utf-8" />
			    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
			    <meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
				<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css" />
				<script	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js" />
			</header>
			<body>
				<div class="container">
					<div class="page-header">
					<h1>JDBC Tester</h1>
					<p class="lead">JDBC Tester for IBM Integration Bus</p>
					</div>
					<div class="row">
						<div class="col-md-8">
						<h1>Resource URL</h1>
						<div class="col-md-6">
						http://localhost:7080/iib/admin/tools/jdbctester
						</div>
						</div>
					</div>
				</div>
				<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
				<script>$("#currUrl").text(window.location)</script>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
