import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javax.swing.UIManager.put;

public class TestOkhttp {
    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Test
    public void print() throws IOException {
        TestOkhttp testOkhttp = new TestOkhttp();
        String response = testOkhttp.run("https://api.douban.com/v2/movie/top250?start=0&count=10");
        System.out.println(response);
    }

    @Test
    public void test1() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", "孙笑川");
        map.put("age", 25);
        map.put("height", 1.70);
        map.put("major", new String[]{"理发", "挖掘机"});
        map.put("hasGirlFriend", false);
        map.put("car", null);
        map.put("house", null);

        //null作为value时，转换成json后不会保存
        JSONObject json1 = new JSONObject(map);
        System.out.println(json1.toString());

    }

    //javaBean
    public class UserInfo {

        private Boolean female;
        private String[] hobbies;
        private Double discount;
        private Integer age;
        private Map<String, Integer> features;

        public Boolean getFemale() {
            return female;
        }

        public void setFemale(Boolean female) {
            this.female = female;
        }

        public String[] getHobbies() {
            return hobbies;
        }

        public void setHobbies(String[] hobbies) {
            this.hobbies = hobbies;
        }

        public Double getDiscount() {
            return discount;
        }

        public void setDiscount(Double discount) {
            this.discount = discount;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Map<String, Integer> getFeatures() {
            return features;
        }

        public void setFeatures(Map<String, Integer> features) {
            this.features = features;
        }

    }

    //使用javaBean初始化json
    @Test
    public void test2() {
        UserInfo userInfo = new UserInfo();
        userInfo.setFemale(true);
        userInfo.setHobbies(new String[]{"yoga", "swimming"});
        userInfo.setDiscount(9.5);
        userInfo.setAge(26);
        userInfo.setFeatures(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 1L;

            {
                put("height", 175);
                put("weight", 70);
            }
        });
        JSONObject jsonObj = (JSONObject) JSON.toJSON(userInfo);
        System.out.println(jsonObj);
    }

    @Test
    public void test3() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("female", true);
        jsonObj.put("hobbies", Arrays.asList(new String[]{"yoga", "swimming"}));
        jsonObj.put("discount", 9.5);
        jsonObj.put("age", "26");
        jsonObj.put("features", new HashMap<String, Integer>() {
            private static final long serialVersionUID = 1L;

            {
                put("height", 175);
                put("weight", 70);
            }
        });
        System.out.println(jsonObj);
    }

    @Test
    public void test12() {
        String jsonString = "['white','卢本伟','芦苇','卢姥爷']";

        JSONArray jsonArray = JSON.parseArray(jsonString);//FastJson的方法
        System.out.println(jsonArray);
        for (Object object : jsonArray) {
            System.out.println(object);
        }
    }

    @Test
    public void test_1() {
        JSONArray jsonArray = new JSONArray();
        //1.add(value)方法
        jsonArray.add("孙悟空");
        //2.add(index value)方法
        jsonArray.add(1, "{'变化':72,'武器',金箍棒}");

        System.out.println(jsonArray);
    }

    @Test
    public void test_2() {
        String[] names = {"baidu", "bing"};
        String[] urls = new String[2];
        for (int i = 0; i < 2; i++) {
            urls[i] = "www." + names[i] + ".com";
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < 2; i++) {
            JSONObject node = new JSONObject();
            node.put("url", urls[i]);
            jsonArray.add(i, node);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ops", jsonArray);
        String jsonString = JSONObject.toJSONString(jsonObject);
        System.out.println(jsonString);
    }

    @Test
    public void test_3() throws Exception {
        String[] Addrs = {
                "建设路东段二中门口",
                "关岭县坡贡镇街上15号",
                "黑林铺前街"
        };
        String[] locations = new String[Addrs.length];
        locations = httpURLConectionGET(Addrs);
        System.out.println(locations);
    }

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static String[] httpURLConectionGET(String[] addresses) throws Exception {
        if (addresses.length == 0) {//当addresses为空时
            return null;
        }
        String P_url = "https://restapi.amap.com/v3/batch?key=e5ece6b78c3ba0e03ecd31b27db19968";
        String[] locations = null;
        okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS);

        String[] urls = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            urls[i] = "/v3/place/around?offset=10&page=1&key=e5ece6b78c3ba0e03ecd31b27db19968&address="
                    + addresses[i];
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < addresses.length; i++) {
            JSONObject node = new JSONObject();
            node.put("url", urls[i]);
            jsonArray.add(i, node);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ops", jsonArray);
        String jsonString = JSONObject.toJSONString(jsonObject);
        System.out.println(jsonString);
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                        , jsonString);
        Request request = new Request.Builder().url(P_url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);

        Response response = call.execute();
        JSONArray ja=JSON.parseArray(response.body().string());//把JSON字符串转换为JSONArray
        System.out.println("大JSONArray......");
        for (Object o:ja) {
            System.out.println(o);
            JSONObject jo = (JSONObject) o;
            JSONObject jo1 = jo.getJSONObject("body");
            JSONArray ja1 = jo1.getJSONArray("pois");
            System.out.println("小JSONArray......");
            int i = 0;
            for (Object o1:ja1) {
                System.out.println(o1);
                JSONObject jo2 = (JSONObject) o1;
                locations[i] = jo2.getString("location");
                i++;
            }
        }
        return locations;
    }
}
