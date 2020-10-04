import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;


public class RedisWithReentrantLock {

	ThreadLocal<HashMap<String, Integer>> locks = new ThreadLocal<HashMap<String, Integer>>();
	Jedis jedis = null;
	public RedisWithReentrantLock(Jedis jedis) {
		this.jedis = jedis;
		locks.set(new HashMap<String, Integer>());
	}
	private boolean _lock(String key) {
		return jedis.setnx(key, "") != 0;
	}
	private void _unlock(String key) {
		jedis.del(key);
	}
	
	public boolean lock(String key) {
		Integer count = locks.get().get(key);
		if (count != null) {
			count++;
			locks.get().put(key, count);
			return true;
		}else {
			if (_lock(key)) {
				locks.get().put(key, new Integer(1));
				return true;
			}else {
				return false;
			}
		}
	}
	public void unlock(String key) {
		Integer count = locks.get().get(key);
		if (count != null) {
			count--;
			if (count == 0) {
				locks.get().remove(key);
				_unlock(key);
			}else {
				locks.get().put(key, count);
			}
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 0;i<5;i++) {
			executor.execute(new RunnableTestLock("MyKey"));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
