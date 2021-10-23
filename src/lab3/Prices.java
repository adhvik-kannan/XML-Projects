package lab3;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lab1.XMLReader;

public class Prices {

	public static HashMap<BitSet, String> products;
	public static ArrayList<BitSet> productBarcodes;
	public static ArrayList<String> priceList;
	public ServerSocket server;
	public static Prices prices;
	public BitSet product;
	public String price;

	public void priceDataBase() throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {
		server = new ServerSocket(1234);
		Socket socket = server.accept();
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		productBarcodes = Lab3.createArrayListOfBitSets("src/lab1/BCProducts.xml", new XMLReader(), "Product", "Barcode", "Price");
		priceList = Lab3.createArrayListOfCharacters("src/lab1/BCProducts.xml", new XMLReader(), "Product", "Barcode", "Price");
		try {
			while((product = (BitSet) in.readObject())!=null) {
				if(product!=null){

					if(productBarcodes.contains(product)) price = priceList.get(productBarcodes.indexOf(product));
					else price = "0.0";
				}
				out.writeObject(price);
				out.flush();
			}
		}
			catch(EOFException e) {}
		
	
		out.close();
		in.close();
		socket.close();
		System.out.println("Prices socket closed");
		server.close();
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {
		prices = new Prices();
		prices.priceDataBase();
	}	

}
