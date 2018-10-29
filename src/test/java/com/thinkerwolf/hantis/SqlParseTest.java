package com.thinkerwolf.hantis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * 如何分段获取标签的内容
 * 
 * @author wukai
 *
 */
public class SqlParseTest {

	@Test
	public void test() throws ParserConfigurationException, SAXException, IOException {
		String path = "sqls/BlogSqls.xml";
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		Document doc = db.parse(is);
		Element element = (Element) doc.getElementsByTagName("sqls").item(0);
		System.out.println("sqls : " + element.getNodeName());

		Map<String, Object> paramterMap = new HashMap<>();
		// paramterMap.put("id", 3);

		NodeList nodeList = element.getElementsByTagName("select");
		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			handleSelect((Element) nodeList.item(i), paramterMap);
		}
	}

	private void handleSelect(Element el, Object parameter) {
		NodeList nodeList = el.getChildNodes();
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				sb.append(node.getTextContent());
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sb.append(handleIf((Element) node, parameter));
			}
			// System.out.println(nodeList.item(i).getTextContent());
		}
		System.out.println(sb);
	}

	private String handleIf(Element el, Object parameter) {
		String context = el.getTextContent();
		// String[] ss = context.split("#{ }");
		String test = el.getAttribute("test");
		try {
			boolean b = (boolean) Ognl.getValue(test, parameter);
			if (b) {
				return context;
			}
			return "";
		} catch (OgnlException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Test
	public void sqlRegexTest() throws OgnlException {
		String s = "where id = #{ id } and title = #{ title } and content = #{content}";
		Pattern p = Pattern.compile("#\\s*\\{\\s*[^#\\{\\}]*\\s*\\}");
		Pattern p1 = Pattern.compile("[^#\\s\\{\\}]+");
		Matcher m = p.matcher(s);
		Map<String, Object> paramterMap = new HashMap<>();
		paramterMap.put("id", 3);
		paramterMap.put("title", "Oracle");
		paramterMap.put("content", "Pettern");
		while (m.find()) {
			m.start();
			m.end();
			String ss = m.group();
			Matcher ssMatcher = p1.matcher(ss);
			ssMatcher.find();
			// System.out.println("start#" + m.start() + ", end#" + m.end() +
			// "..." + ssMatcher.group());
			System.out.println(
					"start#" + m.start() + ", end#" + m.end() + "..." + Ognl.getValue(ssMatcher.group(), paramterMap));
		}
	}

}
