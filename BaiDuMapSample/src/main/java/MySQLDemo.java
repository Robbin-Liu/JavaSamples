import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class City{
  public   String code;
  public  String cityName;
  public  int areaId;
}

public class MySQLDemo {

    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
//    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://127.0.0.1:3308/test";

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"; //com.mysql.jdbc.Driver;"com.mysql.cj.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://localhost:3308/test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_URL = "jdbc:mysql://123.57.236.194:3308/wjwapp?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false";
     //useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "wjwapp";
    static final String PASS = "7Mbzw4r%J*foy5A2pj";

    public static void main(String[] args) {

        List<Map<String,String>> cityList =  ExecelTool.getGBCityList();

        Connection conn = null;
        Statement stmt = null;
        ArrayList list = new ArrayList();
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT areaId, name, fullName FROM basic_area";
            ResultSet rs = stmt.executeQuery(sql);

            Statement ps=conn.createStatement(); //批次处理的Statement
            int num = 0;

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int areaId  = rs.getInt("areaId");
                String name = rs.getString("name");
                String fullName = rs.getString("fullName");

                // 输出数据
//                System.out.print("ID: " + id);
//                System.out.print(", name: " + name);
//                System.out.print(", fullNameL: " + fullName);
//                System.out.print("\n");


                //对比处理数据
                for (Map<String,String> map : cityList) {
                    //for (Map.Entry<String,String> entry : map.entrySet()) {
//                        String key = entry.getKey();
//                        String Value = entry.getValue();
                    String code  =map.get("code");
                    String cityName  =map.get("city");
                    if(name.equals(cityName)) {
                            System.out.print("ID: " + areaId);
                            System.out.print(", name: " + name);
                            System.out.print(", fullNameL: " + fullName);
                            System.out.print("\n");
                            System.out.println("---------匹配--------code:"+code+":"+cityName+",");

                            City city = new City();
                            city.areaId=areaId;
                            city.code = code;
                            city.cityName=cityName;

                            list.add(city);


                            num++;
                    }

                    //ps.addBatch("update basic_area set adcode='"+code+"',city='"+cityName+"' where areaId="+areaId);
                    //}
                }

            }
            System.out.println("---------匹配个数："+num);


            // 完成后关闭
            rs.close();
            stmt.close();
            ps.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }

        udpateCityData(list);
        System.out.println("Goodbye!");
    }

    public static void udpateCityData(List<City> list) {

        Connection conn = null;
        Statement stmt = null;
        Statement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT areaId, name, fullName FROM basic_area";
            ResultSet rs = stmt.executeQuery(sql);

            ps=conn.createStatement(); //批次处理的Statement

            //对比处理数据
            for (City city : list) {
                //for (Map.Entry<String,String> entry : map.entrySet()) {
//                        String key = entry.getKey();
//                        String Value = entry.getValue();
                String code  = city.code;
                String cityName  = city.cityName;
                int areaId= city.areaId;
                ps.addBatch("update basic_area set adcode='"+code+"',city='"+cityName+"' where areaId="+areaId);
                //}
            }


            ps.executeBatch();
            // 完成后关闭
            ps.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(ps!=null) ps.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye2!");
    }

}