package scrapping.framework.xml;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.NamespaceBinder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XHTMLUtils {

	public static final Pattern CHARSET_PATTERN =
			Pattern.compile("(\\S+);\\W*charset\\W*=([\\w\\-]+)", Pattern.CASE_INSENSITIVE);

	public static class XHTMLConfiguration extends HTMLConfiguration {

		public XHTMLConfiguration(String encoding) {
			super();

			setProperty(NAMES_ELEMS, "lower");
			setProperty(NAMES_ATTRS, "lower");
			XMLDocumentFilter[] filters = { new NamespaceFixer() };
			setProperty("http://cyberneko.org/html/properties/filters", filters);
			if (null != encoding)
				setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);
		}
	}

	public static HTMLConfiguration getXHTMLConfiguration(String encoding) {
		return new XHTMLConfiguration(encoding);
	}

	public static final String HTML_PREFIX = "html";

	public static class XHTMLNamespaceContext implements NamespaceContext {

		private final Map<String, String> namespaces = new HashMap<String, String>();

		public XHTMLNamespaceContext() {
			namespaces.put(HTML_PREFIX, NamespaceBinder.XHTML_1_0_URI);
		}

		public Iterator<String> getPrefixes(String namespaceURI) {
			return Collections.unmodifiableMap(namespaces).keySet().iterator();
		}

		public String getPrefix(String namespaceURI) {
			for (Map.Entry<String, String> entry : namespaces.entrySet()) {
				if (entry.getValue().equals(namespaceURI))
					return entry.getKey();
			}

			return null;
		}

		public String getNamespaceURI(String prefix) {
			return namespaces.get(prefix);
		}
	}

	private static final XHTMLNamespaceContext XHTML_NS_CONTEXT = new XHTMLNamespaceContext();

	public static XPath getXPathForXHTML() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(XHTML_NS_CONTEXT);

		return xpath;
	}

	public static Document getBodyAsDocument(HttpResponse response) throws IOException {
		String encoding = getCharacterEncoding(response.getEntity());
		DOMParser parser = new DOMParser(new XHTMLConfiguration(encoding));
		InputSource inputSource = new InputSource(response.getEntity().getContent());

		try {
			parser.parse(inputSource);
			return parser.getDocument();
		} catch (SAXException ex) {
			throw new IOException("XHTML formatting has failed.", ex);
		}
	}

	public static String getCharacterEncoding(HttpEntity entity) {
		String encoding = null;

		if (null != entity.getContentEncoding())
			encoding = entity.getContentEncoding().getValue();

		if ((null == encoding) && (null != entity.getContentType()))
			encoding = getCharacterEncoding(entity.getContentType().getValue());

		return encoding;
	}

	public static String getCharacterEncoding(String contentType) {
		String encoding = null;
		if (null != contentType) {
			try {
				Matcher matcher = CHARSET_PATTERN.matcher(contentType.toLowerCase().trim());
				if (matcher.find()) {
					String tmpEncoding = matcher.group(2);
					if (!"null".equalsIgnoreCase(tmpEncoding))
						encoding = tmpEncoding.toUpperCase();
				}
			} catch (Exception ex) {
			}
		}

		return encoding;
	}

	public static String getRootPath(URL url) {
		StringBuilder buff = new StringBuilder(url.toExternalForm().length());
		if (null != url.getProtocol())
			buff.append(url.getProtocol()).append("://");

		buff.append(url.getHost());
		if (url.getPort() != -1)
			buff.append(":").append(url.getPort());

		return buff.toString();
	}

	public static Document parse(InputStream is, String encoding, boolean fromJSON)
			throws IOException, SAXException {
		Reader reader = null;
		if (fromJSON) {
			InputStream streamNoBOM = TextUtils.skipBOM(is);
			BufferedReader in = new BufferedReader(new InputStreamReader(streamNoBOM, encoding));

			String line = null;
			StringBuilder buff = new StringBuilder(2048);
			while ((line = in.readLine()) != null)
				buff.append(line);

			try {
				Object json = null;
				String jsonStr = buff.toString();
				try {
					//some portals return comments as json array
					json = new JSONObject(jsonStr);
				} catch (JSONException je) {
					json = new JSONArray(jsonStr);
				}
				reader = new StringReader(org.json.XML.toString(json));
			} catch (Exception ex) {
				throw new IOException("Cannot read JSON input!", ex);
			}
		} else{
			reader = new InputStreamReader(is, encoding);
		}

		InputSource source = new InputSource(reader);

		DOMParser parser = new DOMParser(XHTMLUtils.getXHTMLConfiguration(encoding));
		parser.parse(source);

		return parser.getDocument();
	}

	public static Document parseXml(String xml, String encoding) throws IOException {
		Reader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		DOMParser parser = new DOMParser(XHTMLUtils.getXHTMLConfiguration(encoding));
		try {
			parser.parse(source);
		} catch (SAXException ex) {
			throw new IOException(ex.getMessage(), ex);
		}

		return parser.getDocument();
	}

	public static String toString(Node node) throws IOException {
		return toString(node, null);
	}

	public static String toString(Node node, String method, String encoding) throws IOException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(OutputKeys.METHOD, method);
		properties.put(OutputKeys.ENCODING, encoding);
		properties.put(OutputKeys.INDENT, "yes");

		return toString(node, properties);
	}

	public static String toString(Node node, Map<String, String> properties) throws IOException {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			if (null != properties)
				for (Map.Entry<String, String> entry : properties.entrySet())
					transformer.setOutputProperty(entry.getKey(), entry.getValue());
			transformer.transform(source, result);

			return stringWriter.getBuffer().toString();
		} catch (Exception ex) {
			throw new IOException("Document processing error.", ex);
		}
	}
}
