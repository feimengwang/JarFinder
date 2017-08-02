package cn.true123.jarfinder;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FindJar implements Runnable {
	static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(100000000);
	private String searchText = "SyncTokenTag";

	private JarFinder.CallBack callBack;
	private String path;

	private Vector<String> vector = new Vector<String>();

	public void setCallBack(JarFinder.CallBack callBack) {
		this.callBack = callBack;
	}

	public void setParam(String path, String searchText) {
		this.path = path;
		this.searchText = searchText;
	}

	public void execute() {
		CountDownLatch lcd = new CountDownLatch(10);
		findJar(new File(path));
		ExecutorService es = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			es.execute(new FindClassRunnable(lcd));
		}
		try {
			lcd.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		es.shutdown();
		vector.addElement("æ£€ç´¢ç»“æ�Ÿã€‚");
		if (callBack != null) {
			callBack.callBack(vector);
			callBack.finish();
		}
	}

	public static void main(String[] args) {
		CountDownLatch lcd = new CountDownLatch(10);
		FindJar tttt = new FindJar();
		tttt.findJar(new File("C:\\workspace\\master_current"));
		ExecutorService es = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 10; i++) {
			es.execute(tttt.new FindClassRunnable(lcd));
		}
		try {
			lcd.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		es.shutdown();
	}

	public void findJar(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				findJar(f);
			}
		} else {
			if (file.getName().toLowerCase().endsWith("jar")) {
				queue.offer(file.getAbsolutePath());

			}
		}
	}

	public void test(String file) {
		JarFile jar = null;
		try {
			jar = new JarFile(file);
			Enumeration<JarEntry> entities = jar.entries();
			while (entities.hasMoreElements()) {
				JarEntry en = entities.nextElement();
				if (!en.isDirectory() && en.getName() != null && en.getName().endsWith("class")) {
					String className = en.getName().substring(en.getName().lastIndexOf("/") + 1);
					System.out.println(className);
					if (className != null)
						className = className.trim();
					if ((searchText + ".class").equalsIgnoreCase(className)) {
						System.out.println(en.getName());
						System.out.println("find class in jar:" + file);
						vector.addElement("class:" + en.getName() + " in jar:" + file);
						if (callBack != null)
							callBack.callBack(vector);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (jar != null)
					jar.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	class FindClassRunnable implements Runnable {
		CountDownLatch cdl;

		public FindClassRunnable(CountDownLatch cdl) {
			this.cdl = cdl;
		}

		@Override
		public void run() {
			while (queue.size() > 0) {
				try {
					String file = queue.poll(2, TimeUnit.SECONDS);
					test(file);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			cdl.countDown();
		}

	}

	@Override
	public void run() {
		execute();
	}
}
