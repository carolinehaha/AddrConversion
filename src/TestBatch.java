import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestBatch {
    public static OkHttpClient okHttpClient;
    public final static int CONNECT_TIMEOUT = 60;//设置连接超时时间
    public final static int READ_TIMEOUT = 120;//设置读取超时时间
    public final static int WRITE_TIMEOUT = 60;//设置写入超时时间
    @Test
    public void httpURLConectionGET() {
        String[] addresses = {"建设路东段二中门口","关岭县坡贡镇街上15号","黑林铺前街","九堡镇九堡家苑二区三排9号","县城迎宾大道北关医院对面"
                ,"(AB)清远清新禾云新一派美宜佳                      ","大库","","佛山市南海区九江镇璜玑大道璜玑市场内"};
        String s="";
        String locations = "";
        for(int i=0;i<addresses.length-1;i++){
            if(addresses[i].equals("")){
                s += "0,0"+"|";
            }
            else
                s += addresses[i]+"|";
        }
        if(addresses[addresses.length-1].equals("")) s+="0,0";
        else
            s+=addresses[addresses.length-1];
        String url = "https://restapi.amap.com/v3/geocode/geo?key=e5ece6b78c3ba0e03ecd31b27db19968&address=" +s+"&batch=true";
        String location = "";
        System.out.println(url);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
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
                JSONArray addressArr = JSON.parseArray(a.get("geocodes").toString());//取得JSON数组
                for (int i = 0; i < addressArr.size() ; i++) {
                    System.out.println(addressArr.get(i));
                    JSONObject c = JSON.parseObject(addressArr.get(i).toString());
                    location = c.get("location").toString();
                    System.out.println("经纬度:" + location);
                    locations+=location+"|";
                }
                locations=locations.substring(0,locations.length()-1);
                System.out.println(locations);
            }else{
                throw new IOException("连接失败：" + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
