package lab1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Lab1 {


	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		XMLReader xml = new XMLReader();
		HashMap<BitSet, String> barcodes = createHashMap("src/lab1/Barcodes3of9.xml", xml, "Symbol", "Binary", "Character");
		HashMap<BitSet, String> products = createHashMap("src/lab1/BCProducts.xml", xml, "Product", "Barcode", "Price");
		
		Document cartfile = xml.ReadXML("src/lab1/Carts.xml");
		NodeList allCartsList = xml.GetNodes(cartfile, "Cart");
		BitSet bits3 = null;
	//	PrintStream fp = new PrintStream("src/lab1/Carts.out");
		//System.setOut(fp);
		
		for(int s=0; s<allCartsList.getLength(); s++) {
			Element cart = (Element)xml.GetNode(allCartsList, s);
			NodeList itemsInCart = cart.getElementsByTagName("item");
			System.out.println("Cart " + s);
			Double total = 0.0;
			for(int i=0; i<itemsInCart.getLength(); i++) {
				String item = itemsInCart.item(i).getChildNodes().item(0).getNodeValue().trim();
				bits3 = toBitSet(item);
				if(products.get(bits3) != null) {
					total += Double.parseDouble(products.get(bits3));
				}
				String foodName = " ";
				for(int z=0; z<bits3.length(); z+=9) {
					BitSet bits = bits3.get(z, z+9);
					if(barcodes.containsKey(bits)) {
						String food = barcodes.get(bits);
						foodName += food;
					}
					bits.clear();
				}
				
				if(products.get(bits3) == null) System.out.println(foodName + " = $0");
				else System.out.println(foodName + " = $" + products.get(bits3));
				
			}
			
			
			total = (int)(total*100)/100.0;
			System.out.println("Total = $" + total);
		}

	}



	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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
	/*
	public static void printMap(HashMap mp) {
		Iterator it = mp.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());

			//it.remove(); // avoids a ConcurrentModificationException
		}
	}
	*/
}

