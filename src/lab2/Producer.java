package lab2;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lab1.Lab1;
import lab1.XMLReader;

public class Producer extends Thread {

	private XMLReader xml;
	private Document cartfile;
	SynchronizedQueue<BitSet[]> queue;
	private ReentrantLock lock;
	
	public Producer(SynchronizedQueue<BitSet[]> queue, XMLReader xml) {
		this.queue = queue;
		this.xml = xml;
	}
	public void run() {
		Lab2.workPending = true;
		System.out.println("P: " + Lab2.workPending);
		try {
			cartfile = xml.ReadXML("src/lab1/Carts.xml");
		} catch (ParserConfigurationException | SAXException | IOException e) {}
		NodeList allCartsList = xml.GetNodes(cartfile, "Cart");
		for(int s=0; s<allCartsList.getLength(); s++) {
			Element cart = (Element)xml.GetNode(allCartsList, s);
			NodeList itemsInCart = cart.getElementsByTagName("item");
			BitSet[] items = new BitSet[itemsInCart.getLength()];
			
			for(int i=0; i<itemsInCart.getLength(); i++) {
				String item = itemsInCart.item(i).getChildNodes().item(0).getNodeValue().trim();
				items[i] = Lab1.toBitSet(item);
			}
			//System.out.println("Added cart " + s);
			try {
				queue.add(items);
			} catch (InterruptedException e) {}
			
		}
		System.out.println("P: " + Lab2.workPending);
		Lab2.workPending = false;
		
	}
}