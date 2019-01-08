<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/html">
        { "items":[

        <xsl:for-each select="//div[contains(@class, ' post ')]">
            <xsl:variable name="link" select=".//a[contains(@class,'post-link')]/@href"/>
            <xsl:variable name="price" select=".//span[contains(@class,'post-price')]/text()"/>
            <xsl:variable name="title" select=".//div[contains(@class,'caption')]/p/text()"/>
            <xsl:variable name="zona"
                          select=".//div[contains(@class,'caption')]/table//td/text()[contains(.,'Zona')]/../../td[2]/text()"/>
            <xsl:variable name="camere"
                          select=".//div[contains(@class,'caption')]/table//td/text()[contains(.,'Camere')]/../../td[2]/text()"/>
            <xsl:variable name="suprafata"
                          select=".//div[contains(@class,'caption')]/table//td/text()[contains(.,'Suprafață')]/../../td[2]/text()"/>
            <xsl:variable name="oferit"
                          select=".//div[contains(@class,'caption')]/table//td/text()[contains(.,'Oferit de')]/../../td[2]/text()"/>
            <xsl:variable name="publicat"
                          select=".//div[contains(@class,'caption')]/table//td/text()[contains(.,'Publicat')]/../../td[2]/text()"/>

            {
            "link":"<xsl:value-of select="$link"/>",
            "pret":"<xsl:value-of select="normalize-space(translate($price, '\&quot;', ''))"/>",
            "titlu":"<xsl:value-of select="normalize-space(translate($title, '\&quot;', ''))"/>",
            "zona":"<xsl:value-of select="normalize-space(translate($zona, '\&quot;', ''))"/>",
            "camere":"<xsl:value-of select="normalize-space(translate($camere, '\&quot;', ''))"/>",
            "suprafata":"<xsl:value-of select="normalize-space(translate($suprafata, '\&quot;', ''))"/>",
            "oferit":"<xsl:value-of select="normalize-space(translate($oferit, '\&quot;', ''))"/>",
            "publicat":"<xsl:value-of select="normalize-space(translate($publicat, '\&quot;', ''))"/>"
            },
        </xsl:for-each>
        ]}
    </xsl:template>
</xsl:stylesheet>
