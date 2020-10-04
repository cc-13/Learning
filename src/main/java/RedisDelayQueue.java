import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.asm.Type;

import redis.clients.jedis.Jedis;

public class RedisDelayQueue<T> {
	public static BlockingQueueLogger logger = new BlockingQueueLogger(System.out);
	static class TaskItem<T>{
		public String id;
		public T task;
		public String toString() {
			return String.format("id=%s,task=%s", id,task.toString());
		}
	}
	private TypeReference taskType = new TypeReference<TaskItem<T>>() {};
	private Jedis jedis;
	private String key;
	private long delayInMS = 0;
	private TaskHandler handler;
	
	public RedisDelayQueue(Jedis jedis, String key,long delayInMS)
	{
		this.jedis = jedis;
		this.key = key;
		this.delayInMS = delayInMS;
		this.handler = (object) -> {
			TaskItem<T> task = (TaskItem<T>) object;
			logger.log(task.task.toString());
			return 0;
		};
	}
	public void setHandler(TaskHandler handler) {
		this.handler = handler;
	}
	public void delay(T msg) {
		TaskItem<T> item = new TaskItem<T>();
		item.id = UUID.randomUUID().toString();
		item.task = msg;
		long targetScore = System.currentTimeMillis() + delayInMS;
		jedis.zadd(key, targetScore, JSON.toJSONString(item));
	}
	
	public void pool() {
		while (!Thread.currentThread().isInterrupted()) {
			long currentTimeInMS = System.currentTimeMillis() + delayInMS;
			Set<String> objects = jedis.zrangeByScore(key, 0, currentTimeInMS, 0, 1);
			if (objects == null || objects.isEmpty()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
				continue;
			}
			String value = objects.iterator().next();
			if (jedis.zrem(key, value) > 0) {
				TaskItem<T> task = (TaskItem<T>) JSON.parseObject(objects.iterator().next(), taskType);
				this.handler.handle(task);
			}
		}

	}

	public static void main(String[] args) {
		
		ExecutorService exec = Executors.newCachedThreadPool();
		
		// 2 producers
		for (int i = 0;i<3;i++) {
			final int value = i;
			exec.execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Jedis jedis = new Jedis("localhost",32768);
					RedisDelayQueue<String> queue = new RedisDelayQueue<String>(jedis,"TestDelayQueue",5000);
					queue.delay("This is a messge from Thread " + Thread.currentThread().getName());
				}
				
			});
		}
		// 2 consumers
		for (int i = 0;i<2;i++) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Jedis jedis = new Jedis("localhost",32768);
					RedisDelayQueue<String> queue = new RedisDelayQueue<String>(jedis,"TestDelayQueue",5000);
					queue.pool();
				}
			});
		}
		try {
			Thread.sleep(15*1000);
			exec.shutdownNow();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
