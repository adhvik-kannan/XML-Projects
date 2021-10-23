package lab2;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lab1.Lab1;
import lab1.XMLReader;

public class Lab2 {

	public static HashMap<BitSet, String> barcodes;
	public static HashMap<BitSet, String> products;
	public static boolean workPending = true;
	public static int numThreads = 10;


	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		XMLReader xml = new XMLReader();

		barcodes = createHashMap("src/lab1/Barcodes3of9.xml", xml, "Symbol", "Binary", "Character");
		products = createHashMap("src/lab1/BCProducts.xml", xml, "Product", "Barcode", "Price");
		SynchronizedQueue<BitSet[]> queue = new SynchronizedQueue<BitSet[]>(10);
		for(int j=0; j<1; j++) {
			new Producer(queue, xml).start();
		}
		for(int i=0; i<numThreads; i++) {
			new Consumer(queue).start();
		}

	}

	public static String extract(Node np, String tagname) {
		if(np.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element)np;
			NodeList b = element.getElementsByTagName(tagname);
			Element bi = (Element)b.item(0);
			if(bi == null) return null;
			if(bi.getChildNodes().item(0) != null) {
				String bval = bi.getChildNodes().item(0).getNodeValue().trim();

				return bval;
			}
			return null;



		}
		else return null;
	}

	public static BitSet toBitSet(String bitString) {
		BitSet bits = new BitSet(bitString.length());
		for(int i=0; i<bitString.length(); i++) if(bitString.charAt(i) == '1') bits.set(i);
		return bits;
	}
	public static HashMap<BitSet, String> createHashMap(String filePath, XMLReader xml, String selement, String tagName1, String tagName2) throws ParserConfigurationException, SAXException, IOException {
		BitSet bits = null;
		HashMap<BitSet, String> hashMap = new HashMap<BitSet, String>();
		Document file = xml.ReadXML(filePath);
		NodeList nodeList = xml.GetNodes(file, selement);
		for(int s=0; s<nodeList.getLength(); s++) {
			Node node = xml.GetNode(nodeList, s);
			String binary = extract(node, tagName1);
			if(binary == null) continue;
			bits = toBitSet(binary);
			String character = extract(node, tagName2);
			if(hashMap.containsKey(bits)) continue;
			hashMap.put(bits, character);
		}
		return hashMap;
	}
}
