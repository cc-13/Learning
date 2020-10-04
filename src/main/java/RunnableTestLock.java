import redis.clients.jedis.Jedis;

public class RunnableTestLock implements Runnable{

	public static BlockingQueueLogger logger = new BlockingQueueLogger(System.out);
		Jedis jedis;
		String key;
		public RunnableTestLock(String key) {
			this.key = key;
		}
		public void run() {
			Jedis jedis = new Jedis("localhost",32768);
			RedisWithReentrantLock lock = new RedisWithReentrantLock(jedis);
			try {
			while (!lock.lock(key)) {
				logger.log("Lock %s - failed",key);
				Thread.sleep(1000);
				} 
			logger.log("Lock %s - successful",key);
			Thread.sleep(1000);
			if (lock.lock(key)) {
				logger.log("Lock %s - successful",key);
			}else {
				logger.log("Lock %s - failed",key);
			}
			Thread.sleep(1000);
			lock.unlock(key);
			logger.log("Unlock %s",key);
			lock.unlock(key);
			logger.log("Unlock %s",key);
			
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}