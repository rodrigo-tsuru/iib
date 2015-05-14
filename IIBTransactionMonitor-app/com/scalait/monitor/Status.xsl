<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" />
<xsl:template match="/">
	<html>
		<header>
		<style>
table
{
font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;
width:100%;
border-collapse:collapse;
}
td, th 
{
font-size:10px;
border:1px solid #98bf21;
padding:3px 7px 2px 7px;
}
th 
{
font-size:10px;
text-align:left;
padding-top:5px;
padding-bottom:4px;
background-color:#A7C942;
color:#fff;
}
tr.alt td 
{
color:#000;
background-color:#EAF2D3;
}
</style>
		</header>
		<body>
			<h1>Check-list</h1>
			<table border="1">
					<xsl:for-each select="/status/brokers"></xsl:for-each>
			</table>
		</body>
	</html>
</xsl:template> 

</xsl:stylesheet>
