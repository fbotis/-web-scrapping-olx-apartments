<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/html">
        <xsl:variable name="nextPage" select="//a[@data-cy='page-link-next']/span/text()"/>
        { "items":[

        <xsl:for-each select="//div[@class='offer-wrapper']">
            <xsl:variable name="id" select=".//table[@data-id]/@data-id"/>
            <xsl:variable name="link" select=".//td[@class='title-cell']//a/@href"/>
            <xsl:variable name="publicatLa"  select=".//p[@class='lheight16']/small[2]"/>
            <xsl:variable name="pret"  select=".//p[@class='price']/strong/text()"/>
            {
            "id":<xsl:value-of select="$id"/>,
            "link":"<xsl:value-of select="$link"/>",
            <xsl:if test="not(contains($pret,'Schimb'))">
                "pret":"<xsl:value-of select="normalize-space(translate($pret, ' €', ''))"/>",
            </xsl:if>

            "publicatLa":"<xsl:value-of select="normalize-space(translate($publicatLa, '\&quot;', ''))"/>"
            },
        </xsl:for-each>
        ],
        "nextPage":"<xsl:value-of select="$nextPage"/>"
        }


    </xsl:template>
</xsl:stylesheet>
