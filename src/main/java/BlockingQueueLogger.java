
import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class BlockingQueueLogger {
	BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
	PrintStream os = null;
	volatile boolean isInterrupted = false;
	
	public BlockingQueueLogger(PrintStream os) {
		this.os = os;
		start();
	}
	public void start() 
	{
		ThreadFactory factory = new ThreadFactory() {

			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		};
		Executor exec = Executors.newSingleThreadExecutor(factory);
		exec.execute(new Runnable() {

			public void run() {
				try {
					while (!isInterrupted) {
						String msg = queue.take();
						os.println(msg);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
			}
			
		});
	}
	public void log(String msg) 
	{
		String date = new Date().toString();
		String threadName = Thread.currentThread().getName();
		String outMsg = String.format("[%s]-[%s]-%s", date,threadName,msg);
		try {
			queue.put(outMsg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void log(String format, Object ...args) 
	{ 
		String msg = String.format(format,args);
		log(msg);
	}
}
