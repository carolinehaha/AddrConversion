import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.util.StringUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//地址转换线程
public class ConverseThread extends Thread{

    public static OkHttpClient okHttpClient;
    public final static int CONNECT_TIMEOUT = 60;//设置连接超时时间
    public final static int READ_TIMEOUT = 120;//设置读取超时时间
    public final static int WRITE_TIMEOUT = 60;//设置写入超时时间
    AtomicInteger sum = new AtomicInteger(0);
    AtomicInteger count = new AtomicInteger(0);
    String[] addresses = new String[10];
    String[] addr_ids = new String[10];
    String locations;

    @Override
    public void run() {
        String str;
        while(true){
            try {

                //从readQueue把地址读出来
                str = ConverseAddr.readQueue.poll(1,TimeUnit.SECONDS);
                if (str == null) {//当队列中没有元素时
//                    ConverseAddr.cyclicBarrier.await();
                    break;
                }
                Addr_Converse(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName()+"转换"+sum.get()*10+"条数据。");
    }

    public synchronized void Addr_Converse(String str) throws InterruptedException {
        String addr_id = getAddressId(str);
        String addr = getAddress(str);
        if(count.get() < 9){
            addr_ids[count.get()] = addr_id +"|";
            addresses[count.get()] = addr;
            count.incrementAndGet();
        }else if(count.get() == 9){
            addr_ids[count.get()]= addr_id;
            addresses[count.get()]= addr;
            count.incrementAndGet();
        }
        else{
            locations = getLocation(addresses);
            writeAddress(addr_ids,addresses,locations);
            count.getAndSet(0);
            sum.incrementAndGet();
        }
    }
    public synchronized String getAddress (String string){
        int dotIndex1 = string.indexOf(",");
        int dotIndex2 = string.indexOf(",", dotIndex1 + 1);
        String address = string.substring(dotIndex1 + 2, dotIndex2 - 1).trim();
        return address;
    }
    public synchronized String getAddressId(String string){
        int dotIndex1 = string.indexOf(",");
        String addr_id = string.substring(1, dotIndex1 - 1);
        return addr_id;
    }
    public synchronized String getLocation(String[] addresses)  {
        String locations = httpURLConectionGET(addresses);
        return locations;
    }
    public synchronized void writeAddress(String[] addr_ids,String[] addresses,String locations)throws InterruptedException{
        String[] locations1 = StringUtils.splitString(locations,"|");
        for (int i = 0; i <addresses.length ; i++) {
            String addr_id = addr_ids[i];
            if(addr_id.endsWith("|")) addr_id = addr_id.substring(0,addr_id.length()-1);
            String address = addresses[i];
            String location = locations1[i];
            if(location.equals("")||location.equals("[]")) location="0,0";
            String s = "\"" + addr_id + "\",\"" + address + "\",\"" + location + "\"";
            ConverseAddr.writeQueue.offer(s,1,TimeUnit.SECONDS);
        }
    }

    /**
     * 高德地图通过地址获取详细地址以及经纬度
     */
    public String httpURLConectionGET(String address) {
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

    public synchronized String httpURLConectionGET(String[] addresses) {
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
        //System.out.println(url);
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
                    //System.out.println(addressArr.get(i));
                    JSONObject c = JSON.parseObject(addressArr.get(i).toString());
                    location = c.get("location").toString();
                    //System.out.println("经纬度:" + location);
                    locations+=location+"|";
                }
                locations=locations.substring(0,locations.length()-1);
                //System.out.println(locations);
            }else{
                throw new IOException("连接失败：" + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locations;
    }
}
