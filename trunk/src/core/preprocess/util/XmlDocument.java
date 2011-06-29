package core.preprocess.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.util.Constant;

/**
 * 
 * @author lambda
 * 
 */
public class XmlDocument {
	private Document document;
	private String[] labels;
	private String title;
	private String[] titleFeatures;
	private String content;
	private String[] contentFeatures;

	/**
	 * 
	 * @param outputDir
	 * @param filename
	 * @param labels
	 * @param title
	 * @param content
	 * @throws Exception
	 */
	public static void createDocument(File outputDir, String filename, String[] labels, String title, String content) throws Exception {
		Document doc;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		}
		catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
			throw e;
		}

		Element rootNode = doc.createElement("document");
		doc.appendChild(rootNode);

		Element labelsNode = doc.createElement("labels");
		for (int i = 0; i < labels.length; i++) {
			Element labelNode = doc.createElement("label");
			labelNode.appendChild(doc.createTextNode(labels[i]));
			labelsNode.appendChild(labelNode);
		}
		rootNode.appendChild(labelsNode);

		Element titleNode = doc.createElement("title");
		titleNode.appendChild(doc.createTextNode(title));
		rootNode.appendChild(titleNode);

		Element contentNode = doc.createElement("content");
		contentNode.appendChild(doc.createTextNode(content));
		rootNode.appendChild(contentNode);
		TransformerFactory tf = TransformerFactory.newInstance();

		try {
			Transformer transformer = tf.newTransformer();
			// transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(outputDir, filename)));
			StreamResult result = new StreamResult(pw);

			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (TransformerException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void parseDocument(File file) throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.document = db.parse(file);

			NodeList rootNode = document.getChildNodes().item(0).getChildNodes();
			NodeList labelsNode = rootNode.item(1).getChildNodes();
			Node titleNode = rootNode.item(3);
			Node contentNode = rootNode.item(5);

			// System.out.println(labelsNode.getLength());
			this.labels = new String[labelsNode.getLength() / 2];
			if (this.labels.length == 0) {
				this.labels = new String[1];
				this.labels[0] = Constant.EMPTY_LABEL;
			}
			else {
				for (int i = 1; i < labelsNode.getLength(); i += 2) {
					this.labels[i / 2] = labelsNode.item(i).getTextContent();
					// System.out.println(labelsNode.item(i).getNodeName() + ": " + labels[i / 2]);
				}
			}

			this.title = titleNode.getTextContent();
			if (this.title.length() == 0) {
				this.titleFeatures = new String[0];
			}
			else this.titleFeatures = this.title.trim().split(Constant.WORD_SEPARATING_PATTERN);
			//System.out.println(this.title.length() + ": " + this.title);
			//System.out.println(this.titleFeatures.length + ", " + this.titleFeatures[0].equals(""));
			//System.out.println(titleNode.getNodeName() + ": " + title);

			this.content = contentNode.getTextContent();
			if (this.content.length() == 0) {
				this.contentFeatures = new String[0];
			}
			else this.contentFeatures = this.content.trim().split(Constant.WORD_SEPARATING_PATTERN);
			//System.out.println(contentNode.getNodeName() + ": " + content);
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (SAXException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public String getContent() {
		return this.content;
	}

	public String getTitle() {
		return this.title;
	}

	public String[] getLabels() {
		return this.labels;
	}

	public String[] getTitleFeatures() {
		return this.titleFeatures;
	}

	public String[] getContentFeatures() {
		return this.contentFeatures;
	}
}