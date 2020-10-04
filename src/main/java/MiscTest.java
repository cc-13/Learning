import java.util.Date;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;

public class MiscTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost",32768);
		//jedis.set("foo", "bar");
		//String value = jedis.get("foo");
		//System.out.println(value);
		//jedis.rpush("mylist", "a","b");
		//System.out.println(jedis.lpop("mylist"));
		//System.out.println(jedis.lpop("mylist"));
		//User user = new User(35,"Ivy",new Date());
		//String value = jedis.get("CurrentUser");
		//User user = JSON.parseObject(value, User.class);
		//System.out.println(JSON.toJSONString(user));
		//jedis.set("CurrentUser", JSON.toJSONString(user));
		for (int i = 0;i<10;i++) {
			jedis.pfadd("codehole","user"+i);
			
		}
		long total=jedis.pfcount("codehole");
		System.out.printf("%d %d",100000,total);
		jedis.close();
		
	}

}
