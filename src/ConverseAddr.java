
import java.io.IOException;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;


public class ConverseAddr {

    public static OkHttpClient okHttpClient;
    public final static int CONNECT_TIMEOUT = 60;//设置连接超时时间
    public final static int READ_TIMEOUT = 120;//设置读取超时时间
    public final static int WRITE_TIMEOUT = 60;//设置写入超时时间

    /**
     * 高德地图通过地址获取详细地址以及经纬度
     */
    public static String httpURLConectionGET(String address) {
        if(address.equals("")){
            return "0,0";
        }
        String url = "https://restapi.amap.com/v3/geocode/geo?key=e5ece6b78c3ba0e03ecd31b27db19968&address=" + address+"&batch=true";
        String location = null;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                JSONObject a = null;//把JSON字符串转换为JSONObject
                a = JSON.parseObject(response.body().string());
                //System.out.println(a.get("geocodes"));
                if(a.get("geocodes") == null){
                    return "0,0";
                }
                JSONArray addressArr = JSON.parseArray(a.get("geocodes").toString());//取得JSON数组
                //System.out.println(addressArr.get(0));
                if(addressArr.size()==0){
                    return "0,0";
                }
                JSONObject c = JSON.parseObject(addressArr.get(0).toString());
                location = c.get("location").toString();
                //System.out.println("经纬度:" + location);
            }else{
                throw new IOException("连接失败：" + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }


    static int N = 20;//开启N个线程
    protected static BlockingQueue<String> myQueue = new LinkedBlockingDeque<>(N);
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(N + 2);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        P_Thread pThread = new P_Thread();
        pThread.start();
        C_Thread[] cThreads = new C_Thread[N];
        for (int i = 0; i < N; i++) {
            cThreads[i] = new C_Thread();
            cThreads[i].start();
        }
        cyclicBarrier.await();
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        System.exit(0);
    }
}
