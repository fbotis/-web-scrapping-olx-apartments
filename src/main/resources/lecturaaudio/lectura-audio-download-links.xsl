<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/html">
        {"downloadLinks":[
        <xsl:for-each select="//a[contains(@href,'.rar')]/@href">
            "<xsl:value-of select="normalize-space(.)"/>",
        </xsl:for-each>
        ]
        }
    </xsl:template>
</xsl:stylesheet>
