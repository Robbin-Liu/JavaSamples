import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Main {

    public static void doGetTestOne(String lat_lng) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&language=zh-CN&location="
                + lat_lng + "&output=json&pois=1&ak=T5EUXV5ntlbvwvh2hHAOvql4CRk3c5Dl");

        /*
         * 添加请求头信息
         */
        // 浏览器表示
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        // 传输的类型
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");

        httpGet.addHeader("Host","www.pp.com");
        httpGet.addHeader("Referer", "http://www.pp.com/xxxAction.html");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String reverseGeocode(String lng,String lat){
        String location=lng+","+lat;
        //String url="http://api.map.baidu.com/reverse_geocoding/v3/?ak=OTlpF4n8haTK4VH5dQ25tVMjGGAUAFd9&output=json&coordtype=wgs84ll&location="+location;
        String url="http://api.map.baidu.com/reverse_geocoding/v3/?ak=Sf8xjUHUgytm7BIeQPnbluaMBUQHUHCc&output=json&coordtype=wgs84ll&location="+location;

        System.out.println(url);
        String res=doGet(url);
        String addresslocation= JSON.parseObject(res).getJSONObject("result").getString("formatted_address");
        System.out.println(addresslocation);
        return addresslocation;
    }

    public static String doGet(String url){
        //创建一个Http客户端
        CloseableHttpClient httpClient= HttpClientBuilder.create().build();
        //创建一个get请求
        HttpGet httpGet=new HttpGet(url);
        //响应模型
        CloseableHttpResponse response=null;
        try {
            //由客户端发送get请求
            response=httpClient.execute(httpGet);
            //从响应模型中获取响应实体
            HttpEntity responseEntity=response.getEntity();
            if (responseEntity!=null){
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(httpClient!=null){
                    httpClient.close();
                }
                if (response!=null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
        /**
         * -逆地理编码—百度接口根据经纬度解析地址
         *
         * @param lat_lng
         * @return
         * @throws IOException
         */
    public static Map<String, String> geocoder(String lat_lng) throws IOException {
        URL url = new URL("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&language=zh-CN&location="
                + lat_lng + "&output=json&pois=1&ak=T5EUXV5ntlbvwvh2hHAOvql4CRk3c5Dl");
        URLConnection connection = url.openConnection();
        /**
         * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
         * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
         */
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
        out.flush();
        out.close();
        // 一旦发送成功，用以下方法就可以得到服务器的回应：
        String res;
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
        StringBuilder sb = new StringBuilder("");
        while ((res = in.readLine()) != null) {
            sb.append(res.trim());
        }
        String str = sb.toString();
        Map<String, String> map = new HashMap<String, String>();
        if (str != null && str != "") {
            int addss = str.indexOf("country\":");
            int added = str.indexOf("\",\"country_code");
            if (addss > 0 && added > 0) {
                String country = str.substring(addss + 10, added);
                System.out.println("国家：" + country);
                map.put("country", country);
            }
            int addss1 = str.indexOf("province\":");
            int added1 = str.indexOf("\",\"city");
            if (addss1 > 0 && added1 > 0) {
                String province = str.substring(addss1 + 11, added1);
                System.out.println("州市：" + province);
                map.put("province", province);
            }
            int addss2 = str.indexOf("city\":");
            int added2 = str.indexOf("\",\"city_level");
            if (addss2 > 0 && added2 > 0) {
                String city = str.substring(addss2 + 7, added2);
                System.out.println("城市：" + city);
                map.put("city", city);
            }
            return map;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {


//        String lng="31.225696563611";
//        String lat="121.49884033194";
        String lng="39.92069";
        String lat="116.49913";
        reverseGeocode(lng,lat);

        System.out.println("-----hello-18--");

        //Map map = doGetTestOne("48.845289,2.392104");
        //System.out.println(map);

        //doGetTestOne("48.845289,2.392104");

    }

    }
