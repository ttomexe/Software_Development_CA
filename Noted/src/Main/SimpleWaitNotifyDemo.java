package Main;
import java.util.Date;


class Waiter implements Runnable {

	Object lock;
	
	public Waiter(Object lock) {
		this.lock = lock;
	}

	@Override
	public void run() {
		synchronized (lock) {
			try {
				System.out.println("Waiter is waiting for the notifier at " + new Date());
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Waiter is done waiting at " + new Date());
	}

}

class Notifier implements Runnable {

	Object lock;

	public Notifier(Object lock) {
		this.lock = lock;
	}

	@Override
	public void run() {
		System.out.println("Notifier is sleeping for 3 seconds at " + new Date());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		synchronized (lock) {
			System.out.println("Notifier is notifying waiting thread to wake up at " + new Date());
			lock.notify();
		}

	}

}

public class SimpleWaitNotifyDemo {

	public static void main(String[] args) {

		Object lock = new Object();

		//Waiter thread
		Waiter waiter = new Waiter(lock);
		Thread waiterThread = new Thread(waiter);
		waiterThread.start();

		//Notifier thread
		Notifier notifier = new Notifier(lock);
		Thread notifierThread = new Thread(notifier);
		notifierThread.start();

	}

}