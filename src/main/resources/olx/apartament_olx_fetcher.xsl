<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--private Integer vizualizari;-->
    <!--private String linkUtilizator;-->
    <!--private LocalDateTime publicatLa;-->


    <xsl:template match="/html">
        <xsl:variable name="titlu" select="//*[@id='offerdescription']/div[2]/h1/text()"/>
        <xsl:variable name="locatie" select="//*[@id='offerdescription']/div[2]/div[1]/a/strong/text()"/>
        <xsl:variable name="detaliiAnunt" select="//*[@id='offerdescription']/div[2]/div[1]"/>
        <xsl:variable name="oferitDe"
                      select="//th/text()[contains(.,'Oferit de')]/../../td/strong/a/text()"/>
        <xsl:variable name="compartimentare"
                      select="//th/text()[contains(.,'Compartimentare')]/../../td/strong/a/text()"/>
        <xsl:variable name="suprafata"
                      select="//th/text()[contains(.,'Suprafata utila')]/../../td/strong/text()"/>
        <xsl:variable name="dataConstructie"
                      select="//th/text()[contains(.,'An constructie')]/../../td/strong/a/text()"/>
        <xsl:variable name="etaj"
                      select="//th/text()[contains(.,'Etaj')]/../../td/strong/a/text()"/>
        <xsl:variable name="descriere" select="//*[@id='textContent']/p/text()"/>
        <xsl:variable name="pret" select="//*[@id='offeractions']/div[1]/strong/text()"/>
        <xsl:variable name="vizualizari"
                      select="//*[@id='offerbottombar']/div[3]/strong/text()"/>
        <xsl:variable name="linkUtilizator"
                      select="//*[@id='offeractions']/div[4]/div[2]/h4/a/@href"/>
        <xsl:variable name="camere"
                      select="//*[@id='breadcrumbTop']/tbody/tr/td[2]/ul/li[last()]/a/span/text()"/>
        {
        "titlu":"<xsl:value-of select="normalize-space(translate($titlu, '\&quot;', ''))"/>",
        "locatie":"<xsl:value-of select="normalize-space(translate($locatie,'\&quot;', ''))"/>",
        "detaliiAnunt":"<xsl:value-of select="normalize-space($detaliiAnunt)"/>",
        "oferitDe":"<xsl:value-of select="normalize-space($oferitDe)"/>",
        "compartimentare":"<xsl:value-of select="normalize-space($compartimentare)"/>",
        <xsl:if test="$suprafata">
            "suprafata":<xsl:value-of select="normalize-space(translate($suprafata,'m²', ''))"/>,
        </xsl:if>
        "etaj":"<xsl:value-of select="normalize-space($etaj)"/>",
        "linkUtilizator":"<xsl:value-of select="normalize-space($linkUtilizator)"/>",
        "descriere":"<xsl:value-of select="normalize-space(translate($descriere,'\&quot;', ''))"/>",
        "camere":<xsl:value-of select="replace($camere,'[^0-9]', '')"/>,
        "pret":<xsl:value-of select="normalize-space(translate(translate($pret,'€', ''),' ',''))"/>,
        "vizualizari":<xsl:value-of select="normalize-space($vizualizari)"/>,
        "dataConstructie":"<xsl:value-of select="normalize-space($dataConstructie)"/>"
        }
    </xsl:template>
</xsl:stylesheet>
