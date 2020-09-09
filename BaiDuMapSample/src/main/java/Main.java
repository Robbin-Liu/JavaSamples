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

    /**
     * -逆地理编码—百度接口根据经纬度解析地址
     *
     * @param lng 经度
     * @param lat 纬度
     * @return
     * @throws IOException
     */
    public static String reverseGeocode(String lng,String lat){
        String location=lng+","+lat;
        //String url="http://api.map.baidu.com/reverse_geocoding/v3/?ak=OTlpF4n8haTK4VH5dQ25tVMjGGAUAFd9&output=json&coordtype=wgs84ll&location="+location;
        String url="http://api.map.baidu.com/reverse_geocoding/v3/?ak=Sf8xjUHUgytm7BIeQPnbluaMBUQHUHCc&output=json&coordtype=wgs84ll&location="+location;

        System.out.println(url);
        String res=doGet(url);

        String province= JSON.parseObject(res).getJSONObject("result").getJSONObject("addressComponent").getString("province");
        System.out.println("省:"+province);

        String city= JSON.parseObject(res).getJSONObject("result").getJSONObject("addressComponent").getString("city");
        System.out.println("市:"+city);

        String city_level= JSON.parseObject(res).getJSONObject("result").getJSONObject("addressComponent").getString("city_level");
        System.out.println("city_level:"+city_level);

        String district= JSON.parseObject(res).getJSONObject("result").getJSONObject("addressComponent").getString("district");
        System.out.println("区:"+district);


        String adcode= JSON.parseObject(res).getJSONObject("result").getJSONObject("addressComponent").getString("adcode");
        System.out.println("区编码:"+adcode);

        String address= JSON.parseObject(res).getJSONObject("result").getString("formatted_address");
        System.out.println("街道地址:"+address);
        return address;
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

    public static void main(String[] args) throws Exception {


        System.out.println("-----start---");

//        String lng="31.225696563611";
//        String lat="121.49884033194";
        String lng="39.92069";
        String lat="116.49913";
        reverseGeocode(lng,lat);

        System.out.println("-----end---");

    }

    }
