
import java.io.IOException;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;


public class ConverseAddr {

    static int N = 10;//开启N个线程
    protected static BlockingQueue<String> readQueue = new ArrayBlockingQueue<>(N);
    protected static BlockingQueue<String> writeQueue = new ArrayBlockingQueue<>(N);
//    static CyclicBarrier cyclicBarrier = new CyclicBarrier(N + 2);

    public static void main(String[] args) throws Exception {
//        long start = System.currentTimeMillis();
        C_Thread cThread = new C_Thread();
        cThread.start();
        ConverseThread[] converseThread= new ConverseThread[N];
        for (int i = 0; i < N; i++) {
            converseThread[i] = new ConverseThread();
            converseThread[i].start();
        }
        P_Thread pThread = new P_Thread();
        pThread.start();
//        cyclicBarrier.await();
//        long end = System.currentTimeMillis();
//        System.out.println("耗时：" + (end - start) + "ms");
    }
}
