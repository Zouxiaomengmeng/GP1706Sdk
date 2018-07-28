package com.qianfeng.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Auther: lyd
 * @Date: 2018/7/25 09:29
 * @Description:日志产生的sdk
 */
public class LogSdk {
    //日志打印对象
    private static final Logger logger = Logger.getGlobal();
    //定义常量
    private static final String ver = "1.0";
    private static final String platformName = "java_server";//平台
    private static final String chargeSuccess = "e_cs";//成功事件
    private static final String chargeRefund = "e_cr";
    private static final String sdkName = "java_sdk";
    private static final String requestUrl = "http://192.168.216.111/index.html";//请求的路径，nginx所在的服务器


    /**
     * 支付成功事件，成功返回true，失败返回false
     * 尽量不要用第三方的包
     * @param mid  会员id
     * @param oid  订单id
     * @param flag  1:chargeSuccess   2:chargeRefund  默认使用1
     * @return
     */
    public static String chargeSuccess(String mid,String oid,String flag){
        //会员id和订单id都不能为空
        if(isEmpty(mid) || isEmpty(oid)){
            logger.log(Level.WARNING,"mid or oid is null." +
                    "but both is must not null.");
//            return false;
        }
        try{
            //umi oid 肯定不为空  http://192.168.216.111/index.html?en=1.0&pl=java_server
            Map<String,String> data = new HashMap<String,String>();
            //判断是订单支付成功事件还是退款成功事件
            if(isEmpty(flag) || flag.equals("1")){
                data.put("en",chargeSuccess);
            } else if(flag.equals("2")) {
                data.put("en",chargeRefund);
            }
            data.put("pl",platformName);
            data.put("sdk",sdkName);
            data.put("c_time",System.currentTimeMillis()+"");
            data.put("ver",ver);
            data.put("u_mid",mid);
            data.put("oid",oid);
            //构造最终请求的url，将这些数据发送出去
            String url = buildUrl(data);
            //将url添加到队列中
            //getInstance():共有的获取该类的实例的方法
            SendUrl.getInstance().addUrlToQueue(url);
            System.out.println(url);
            String json = "{\"code\":200,\"data\":{\"isSuccess\":true}}";
            return json;
        } catch (Exception e){
            throw  new RuntimeException("请求成功事件失败.");
        }
    }


    /**
     *
     * @param data
     * @return  192.168.216.1^A1532826780.536^A192.168.216.111^A
     * /index.html?en=e_pv&p_url=http%3A%2F%2Flocalhost%
     * 3A8080%2Fprojectout%2Fdemo.jsp&p_ref=http%3A%2F%2Flocalhost
     * %3A8080%2Fprojectout%2Fdemo.jsp&tt=%E6%B5%8B%E8%AF%95%E9%
     * A1%B5%E9%9D%A21&ver=1&pl=website&sdk=js&u_ud
     * =E6FA33DD-4381-428B-B8DA-A1F41BDF923E&u_mid=liyadong&u_sd=
     * CF69A5C7-7215-4914-89B1-CBEB94FAB4E3&c_time=1532740414696&l=
     * zh-CN&b_iev=Mozilla%2F5.0%20(Windows%20NT%2010.0%3B%20WOW64%
     * 3B%20Trident%2F7.0%3B%20.NET4.0C%3B%20.NET4.0E%3B%20.NET%
     * 20CLR%202.0.50727%3B%20.NET%20CLR%203.0.30729%3B%20.NET%20CLR%
     * 203.5.30729%3B%20InfoPath.3%3B%20rv%3A11.0)%20like%20Gecko&b_rst=1366*768
     */
    private static String buildUrl(Map<String, String> data) {
        //数据若为空就返回一个空，否则往下走
        if(data.isEmpty()){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        try {
            //添加基础数据
            sb.append(requestUrl).append("?");
            //循环data
            for (Map.Entry<String,String> en:data.entrySet()) {
                if(isNotEmpty(en.getKey())){
                    //若key不为空，给key拼接一个“=”，对value中的特殊符号进行编译，每一对key-value用“&”符号隔开
                    sb.append(en.getKey()).append("=").
                            append(URLEncoder.encode(en.getValue(),"UTF-8")).append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING,"value的编码异常");
        }
        return sb.toString().substring(0,sb.length()-1);
    }


    /**
     * 判断字符串是否为空，为空返回true，否则false
     * @param input
     * @return
     */
    public static boolean isEmpty(String input){
        //
        return input == null || input.trim().equals("") || input.trim().length() == 0 ? true : false;
    }

    public static boolean isNotEmpty(String input){

        return !isEmpty(input);
    }

    public static void main(String[] args) {
        System.out.println(LogSdk.isNotEmpty("aaaa"));
    }
}
