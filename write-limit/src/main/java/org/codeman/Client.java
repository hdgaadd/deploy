package org.codeman;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hdgaadd
 * Created on 2022/09/14
 *
 * question: 针对大量数据写入Redis, 造成的: (1.Redis有写入限制, 数据写入可能失败 2.大量写入造成Redis瞬时流量高峰)
 * solution: 1. qps过高则进行写入休眠 2.每1000条数据提交有一次Redis, 而不是一条一次
 */
public class Client {

    private static final int totalQps;

    private static final String filePath;

    private static final Properties properties;

    private static int requiredQps;

    static {
        properties = CommonParaUtil.paraUtil();
        totalQps = Integer.parseInt(properties.getProperty("qps"));
        filePath = properties.getProperty("hdfs.input.path");
    }

    public static void main(String[] args) throws IOException {
        SparkSession spark = SparkSession.builder()
                .appName("Spark-to-redis")
                .master("local")
                .getOrCreate();
        JavaRDD<String> lines = spark.read().textFile(filePath).javaRDD();

        JavaRDD<Row> rows = lines.map((Function<String, Row>) line -> {
            String[] str = line.split(" ");
            return RowFactory.create(str[0], str[1]);
        });
        int partition = rows.getNumPartitions();

        requiredQps = totalQps / partition;

        rows.foreachPartition(row -> {
            Jedis jedis = RedisUtil.getInstance(properties.getProperty("redis.ip"), Integer.parseInt(properties.getProperty("redis.port")), properties.getProperty("redis.pwd"));
            Pipeline pipeline = jedis.pipelined();
            AtomicLong atomicLong = new AtomicLong();
            long start = System.currentTimeMillis();

            row.forEachRemaining(v -> {
                        atomicLong.incrementAndGet();
                        qpsControll(start, atomicLong);

                        // 每1000条提交一次
                        System.out.println(v.getString(0) + " , " +  v.getString(1));
                        pipeline.sadd(v.getString(0), v.getString(1));
                        if (atomicLong.get() % 3 == 0) {
                            pipeline.sync();
                        }
                    }
            );

            pipeline.close();
            jedis.close();
        });
        spark.stop();
    }

    /**
     * 写入控制
     *
     * @param start
     * @param count
     */
    private static void qpsControll(long start, AtomicLong count) {
        // 当前qps
        long actualQps = 1000 * count.get() / (System.currentTimeMillis() - start);
        System.out.println("current qps is " + actualQps);

        if (actualQps > (long) requiredQps) {
            System.out.println("============STOP============");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }


}

