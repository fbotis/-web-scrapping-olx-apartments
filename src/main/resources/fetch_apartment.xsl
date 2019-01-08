<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/html">
        <xsl:variable name="title" select="//*[@id='offerdescription']/div[2]/h1/text()"/>
        <xsl:variable name="location" select="//*[@id='offerdescription']/div[2]/div[1]/a/strong/text()"/>
        <xsl:variable name="adAddingDetails" select="//*[@id='offerdescription']/div[2]/div[1]/em"/>


        <xsl:variable name="offeredBy"
                      select="//th/text()[contains(.,'Oferit de')]/../../td/strong/a/text()"/>
        <xsl:variable name="compartimentare"
                      select="//th/text()[contains(.,'Compartimentare')]/../../td/strong/a/text()"/>
        <xsl:variable name="suprafata"
                      select="//th/text()[contains(.,'Suprafata utila')]/../../td/strong/text()"/>
        <xsl:variable name="dataconstructie"
                      select="//th/text()[contains(.,'An constructie')]/../../td/strong/a/text()"/>
        <xsl:variable name="etaj"
                      select="//th/text()[contains(.,'Etaj')]/../../td/strong/a/text()"/>


        <xsl:variable name="descriere" select="//*[@id='textContent']/p/text()"/>
        <xsl:variable name="price" select="//*[@id='offeractions']/div[1]/strong/text()"/>
        {
        "title":"<xsl:value-of select="normalize-space(translate($title, '\&quot;', ''))"/>",
        "location":"<xsl:value-of select="normalize-space(translate($location,'\&quot;', ''))"/>",
        "adAddingDetails":"<xsl:value-of select="normalize-space($adAddingDetails)"/>",
        "offeredBy":"<xsl:value-of select="normalize-space($offeredBy)"/>",
        "compartimentare":"<xsl:value-of select="normalize-space($compartimentare)"/>",
        "suprafata":"<xsl:value-of select="normalize-space($suprafata)"/>",
        "etaj":"<xsl:value-of select="normalize-space($etaj)"/>",
        "descriere":"<xsl:value-of select="normalize-space(translate($descriere,'\&quot;', ''))"/>",
        "price":"<xsl:value-of select="normalize-space($price)"/>",
        "dataconstructie":"<xsl:value-of select="normalize-space($dataconstructie)"/>"
        }
    </xsl:template>
</xsl:stylesheet>
