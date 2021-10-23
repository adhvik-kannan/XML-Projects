package lab3;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer extends Thread {

	private SynchronizedQueue<BitSet[]> queue;
	private Double total = 0.0;
	private BitSet[] cart;
	private static ReentrantLock lock = new ReentrantLock();
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	private static ReentrantLock productLock = new ReentrantLock();
	private String price;

	public Consumer(SynchronizedQueue<BitSet[]> queue) {
		this.queue = queue;
	}
	public void run() {

		try {
			socket = new Socket("localhost", 1234);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e1) {} catch (IOException e1) {}



		while(Lab3.workPending || !queue.isEmpty()) {

			total = 0.0;
			try {
				cart = queue.remove();

			} catch (InterruptedException e) {}
			String cartOfItems = "";

			for(int i=0; i<cart.length; i++) {
				productLock.lock();
				try {
					out.writeObject(cart[i]);
					out.flush();
				} catch (IOException e1) {}

				productLock.unlock();
				try {
					price = (String) in.readObject();

				} catch (NumberFormatException | IOException e) {} catch (ClassNotFoundException e) {}
				if(price != null) {
					total += Double.parseDouble(price);
				}
				String foodName = " ";
				for(int z=0; z<cart[i].length(); z+=9) {
					BitSet bits = cart[i].get(z, z+9);
					if(Lab3.barcodes.containsKey(bits)) {
						String food = Lab3.barcodes.get(bits);
						foodName += food;
					}
					bits.clear();
				}
				if(price == null) cartOfItems += foodName + " = $0" + "\n";
				else cartOfItems += foodName + " = $" + price + "\n";
			}
			lock.lock();
			if(cartOfItems!="") {
				System.out.println("Cart: " + "\n" + cartOfItems);
				total = (int)(total*100)/100.0;
				System.out.println("Total = $" + total);
				System.out.println();
			}
			lock.unlock();

		}

		try {
			out.close();
		} catch (IOException e2) {}
		try {
			in.close();
		} catch (IOException e1) {} catch(NullPointerException e3) {}
		try {
			socket.close();
			System.out.println("Consumer Socket closed");
		} catch (IOException e) {}


	}

}

