package lab2;

import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer extends Thread {

	private SynchronizedQueue<BitSet[]> queue;
	private Double total = 0.0;
	private BitSet[] cart;
	private static ReentrantLock lock = new ReentrantLock();
	private static ReentrantLock productLock = new ReentrantLock();
	public Consumer(SynchronizedQueue<BitSet[]> queue) {
		this.queue = queue;
	}
	public void run() {
		System.out.println("C: " + Lab2.workPending);
		while(Lab2.workPending || !queue.isEmpty()) {
			total = 0.0;
			try {
				cart = queue.remove();
			} catch (InterruptedException e) {}
			String cartOfItems = "";
			
			for(int i=0; i<cart.length; i++) {
				productLock.lock();
				String price = Lab2.products.get(cart[i]);
				productLock.unlock();
				if(price != null) {
					total += Double.parseDouble(price);
				}
				
				String foodName = " ";
				for(int z=0; z<cart[i].length(); z+=9) {
					BitSet bits = cart[i].get(z, z+9);
					if(Lab2.barcodes.containsKey(bits)) {
						String food = Lab2.barcodes.get(bits);
						foodName += food;
					}
					bits.clear();
				}
				if(price == null) cartOfItems += foodName + " = $0" + "\n";
				else cartOfItems += foodName + " = $" + price + "\n";
			}
			lock.lock();
			System.out.println("Cart: " + "\n" + cartOfItems);
			total = (int)(total*100)/100.0;
			System.out.println("Total = $" + total);
			System.out.println();
			lock.unlock();
			
		}
	}
}

