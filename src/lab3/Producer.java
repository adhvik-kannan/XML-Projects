package lab3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.*;

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
	private SynchronizedQueue<BitSet[]> queue;
	private ReentrantLock lock;
	public Producer(SynchronizedQueue<BitSet[]> queue, XMLReader xml) {
		this.queue = queue;
		this.xml = xml;
	}
	public void run() {
		Lab3.workPending = true;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/lab3/Carts.csv"));

			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("[^0-9]");
				BitSet[] items = new BitSet[(words.length)];
				for(int i=0; i<words.length; i++) {
					String item = words[i];
					items[i] = Lab3.toBitSet(item);	
				}
				try {
					queue.add(items);
				} catch (InterruptedException e) {}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Lab3.workPending = false;

	}
}