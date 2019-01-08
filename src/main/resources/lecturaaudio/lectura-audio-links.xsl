<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/html">
        <xsl:variable name="nextPage" select="//a[@class='blog-pager-older-link']/@href"/>
        {
        "nextPage":"<xsl:value-of select="$nextPage"/>",
         "items":[
        <xsl:for-each select="//div[contains(@itemprop,'blogPost')]">
            <xsl:variable name="title" select="normalize-space(.//h3[contains(@class,'post-title')]/a/text())"/>
            <xsl:variable name="link" select=".//h3[contains(@class,'post-title')]/a/@href"/>
            {
            "autor":"<xsl:value-of select="normalize-space(substring-before($title,'-'))"/>",
            "titlu":"<xsl:value-of select="normalize-space(substring-after($title,'-'))"/>",
            "link":"<xsl:value-of select="$link"/>"
            },
        </xsl:for-each>
            ]
        }
    </xsl:template>
</xsl:stylesheet>
