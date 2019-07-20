/**
 * 
 */
package cn.rongcapital.caas.itest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.caas.generator.IdGenerator;
import cn.rongcapital.caas.generator.IdGenerator.IdType;
import cn.rongcapital.caas.generator.impl.RedisIdGenerator;
import org.junit.Assert;

/**
 * @author zhaohai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RedisIdGeneratorITest {

    @Autowired
    private IdGenerator idGenerator;

    @Test
    public void generate() {
        String threadCountStr = System.getProperty("thread.count");
        int threadCount = 1;
        if (threadCountStr != null) {
            try {
                threadCount = Integer.parseInt(threadCountStr);
            } catch (NumberFormatException e) {
            }
        }
        String idCountStr = System.getProperty("id.count");
        long idCount = 1;
        if (idCountStr != null) {
            try {
                idCount = Long.parseLong(idCountStr);
            } catch (NumberFormatException e) {
            }
        }
        List<String> idList = Collections.synchronizedList(new ArrayList<String>());
        
        CountDownLatch countDownLatch = new CountDownLatch((int) idCount + 1);
        Client client = new Client(idCount, countDownLatch, idList);
        
        long milliStart = new Date().getTime();
        for (int i = 0; i < threadCount; i++) {
            new Thread(client).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Set<String> idSet = Collections.synchronizedSet(new HashSet<String>());
        for(String id : idList) {
            idSet.add(id);
        }
        
        Assert.assertEquals(idSet.size(), idList.size());
        
        long milliEnd = new Date().getTime();
        long spendMilliseconds = milliEnd - milliStart;
        BigDecimal distributeNumPerSecond = new BigDecimal(idCount).divide(
                new BigDecimal(spendMilliseconds).divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP),
                BigDecimal.ROUND_HALF_UP);
        System.out.format("Distribute %s IDs per seconds\n", distributeNumPerSecond.toString());
    }

    private class Client implements Runnable {
        private long max;
        private CountDownLatch latch;
        private List<String> idList;

        public Client(long max, CountDownLatch latch, List<String> idList) {
            this.max = max;
            this.latch = latch;
            this.idList = idList;
        }

        public void run() {
            try {
                long id = Long.parseLong(idGenerator.generate(IdType.USER));
                latch.countDown();
                long first = id;
                idList.add(String.valueOf(id));
                while (id < first + max) {
                    String idString = idGenerator.generate(IdType.USER);
                    idList.add(idString);
                    id = Long.parseLong(idString);
                    System.out.println(Thread.currentThread().getName() + " " + id);
                    latch.countDown();
                }
            } catch(Exception e) {
                
            }
        }
    }

    @Configuration
    @ImportResource("file:/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.xml")
    public static class RedisIdGeneratorITestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public IdGenerator idGenerator() {
            return new RedisIdGenerator();
        }
    }
}
