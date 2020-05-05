<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--private Integer vizualizari;-->
    <!--private String linkUtilizator;-->
    <!--private LocalDateTime publicatLa;-->


    <xsl:template match="/html">
        <xsl:variable name="id" select="//div[@id='offerbottombar']/ul/li[3]//strong/text()"/>
        <xsl:variable name="titlu" select="//div[@class='offer-titlebox']/h1/text()"/>

        <xsl:variable name="locatie" select="//address/p/text()"/>
        <xsl:variable name="judet" select="//div[@id='cityFieldGray']/span[2]/text()"/>

        <xsl:variable name="pret" select="//div[@class='pricelabel'][1]/strong[1]/text()"/>

        <xsl:variable name="categorii"
                      select="//table[@id='breadcrumbTop']/tbody/tr/td[@class='middle']/ul/li/a/span/text()"/>

        <xsl:variable name="compartimentare"
                      select="//ul[@class='offer-details']//span[@class='offer-details__name']/text()[contains(.,'Compartimentare')]/../../strong/text()"/>
        <xsl:variable name="suprafata"
                      select="//ul[@class='offer-details']//span[@class='offer-details__name']/text()[contains(.,'Suprafata')]/../../strong/text()"/>
        <xsl:variable name="anConstructie"
                      select="//ul[@class='offer-details']//span[@class='offer-details__name']/text()[contains(.,'An')]/../../strong/text()"/>
        <xsl:variable name="etaj"
                      select="//ul[@class='offer-details']//span[@class='offer-details__name']/text()[contains(.,'Etaj')]/../../strong/text()"/>

        <xsl:variable name="oferitDe"
                      select="//ul[@class='offer-details']//span[@class='offer-details__name']/text()[contains(.,'Oferit')]/../../strong/text()"/>

        <xsl:variable name="vizualizari" select="//div[@id='offerbottombar']/ul/li[2]//strong/text()"/>
        <xsl:variable name="publicatLa" select="//div[@id='offerbottombar']/ul/li[1]//strong/text()"/>


        <!--Multiple parts should be combined in json-->
        <xsl:variable name="descriereParts" select="//div[@id='textContent']/text()"/>
        <xsl:variable name="poze" select="//ul[@id='bigGallery']/li/a/@href"/>


        {
        "id":<xsl:value-of select="normalize-space($id)"/>,
        "titlu":"<xsl:value-of select="normalize-space(translate($titlu, '\&quot;', ''))"/>",
        "locatie":"<xsl:value-of select="normalize-space(translate($locatie,'\&quot;', ''))"/>",
        "pret":<xsl:value-of select="normalize-space(translate(translate($pret,'€', ''),' ',''))"/>,
        "compartimentare":"<xsl:value-of select="normalize-space($compartimentare)"/>",
        <xsl:if test="$suprafata">"suprafata":<xsl:value-of select="normalize-space(translate($suprafata,'m²', ''))"/>,</xsl:if>
        "anConstructie":"<xsl:value-of select="normalize-space($anConstructie)"/>",
        "etaj":"<xsl:value-of select="normalize-space($etaj)"/>",
        "oferitDe":"<xsl:value-of select="normalize-space($oferitDe)"/>",
        "vizualizari":<xsl:value-of select="normalize-space($vizualizari)"/>,
        "publicatLa":"<xsl:value-of select="normalize-space($publicatLa)"/>",
        "descriere":"<xsl:for-each select="$descriereParts"><xsl:value-of select="normalize-space(.)"/></xsl:for-each>"
        "poze":[<xsl:for-each select="$poze">"<xsl:value-of select="normalize-space(.)"/>",</xsl:for-each>""]
    </xsl:template>
</xsl:stylesheet>
