package scrapping.framework.xml;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.NamespaceBinder;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class NamespaceFixer extends DefaultFilter {

    public static final String SYNTHESIZED_NAMESPACE_PREFX = "http://cyberneko.org/html/ns/synthesized/";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final HTMLEventInfo SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();

    protected NamespaceContext fNamespaceContext;
    protected int fSynthesizedNamespaceCount;
    
    private static final String PREFIX = "xmlns";
    private static final String URI = NamespaceBinder.XHTML_1_0_URI;
    private static final String ATYPE = "CDATA";

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs)
        throws XNIException {
        fNamespaceContext = nscontext;
        fSynthesizedNamespaceCount = 0;
        super.startDocument(locator, encoding, nscontext, augs);
    }


    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        handleStartElement(element, attrs);
        super.startElement(element, attrs, augs);
    } 

    public void endElement(QName element, Augmentations augs) throws XNIException {
    	
    	if (element.prefix != null && element.uri == null) {
    		element.uri = fNamespaceContext.getURI(element.prefix);
        }
       
        super.endElement(element, augs);
    }

    protected void handleStartElement(QName element, XMLAttributes attrs) {
    	
    	QName fQName = new QName();
    	
         int attrCount = attrs != null ? attrs.getLength() : 0;
         for (int i = attrCount-1; i >= 0; i--) {
             attrs.getName(i, fQName);
             attrs.setName(i, fQName);
             
                 if (!fQName.rawname.equals("xmlns") &&
                     !fQName.rawname.startsWith("xmlns:")) {
                     if (fQName.prefix != null && fQName.uri == null) {
                         //synthesizeBinding(attrs, fQName.prefix);
                    	 synthesizeBinding(attrs, fQName);
                     }
                 }
         }

    	if (element.prefix != null && element.uri == null) {
            synthesizeBinding(attrs, element);
        }

    }

    //protected void synthesizeBinding(XMLAttributes attrs, String ns) {
    protected void synthesizeBinding(XMLAttributes attrs, QName fQName) {
        
        String localpart = fQName.prefix;
        String qname = PREFIX + ':' + localpart;
        String avalue = SYNTHESIZED_NAMESPACE_PREFX+fSynthesizedNamespaceCount++;
        
        fQName.setValues(PREFIX, localpart, qname, URI);
        attrs.addAttribute(fQName, ATYPE, avalue);

        XercesBridge.getInstance().NamespaceContext_declarePrefix(fNamespaceContext, localpart, avalue);
    } 

}
