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
			<h1>Relatório de erros no barramento SOA</h1>
			<table border="1">
			<tr>
				<th>Horario</th>
				<th>Integration Node (Broker)</th>
				<th>Id Transacao</th>
				<th>Integration Server (Execution Group)</th>
				<th>Message Flow</th>
				<th>Node</th>
				<th>Mensagem</th>
				<th>Exceção</th>
			</tr>
			<xsl:for-each select="/Erros/Erro">
				<tr>
					<td><xsl:value-of select="EVENTTIME" /></td>
					<td><xsl:value-of select="INTEGRATIONNODE" /></td>
					<td><xsl:value-of select="LOCALTRANSACTIONID" /></td>
					<td><xsl:value-of select="INTEGRATIONSERVER" /></td>
					<td><xsl:value-of select="MESSAGEFLOW" /></td>
					<td><xsl:value-of select="NODELABEL" /></td>
					<td><xsl:value-of select="BITSTREAM" /></td>
					<td><table border="1"><xsl:call-template name="printStackTrace" ><xsl:with-param name="nodeSet" select="EXCEPTIONLIST/*[last()]" /></xsl:call-template></table></td>
				</tr>
			</xsl:for-each>
			</table>
		</body>
	</html>
</xsl:template> 
<xsl:template name="printStackTrace">
	<xsl:param name="nodeSet" />
	<xsl:if test="count($nodeSet/Number) > 0">
		<xsl:call-template name="printStackTrace" ><xsl:with-param name="nodeSet" select="$nodeSet/*[last()]" /></xsl:call-template>
			<tr>
			<td><xsl:value-of select="local-name($nodeSet)" /></td>
			<td><xsl:value-of select="$nodeSet/Number" /></td>
			<td><xsl:value-of select="$nodeSet/Text" /></td>
			</tr>
		
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
