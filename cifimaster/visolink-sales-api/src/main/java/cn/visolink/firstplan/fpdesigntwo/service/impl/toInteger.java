package cn.visolink.firstplan.fpdesigntwo.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

/*将汉字转为数字*/
@Service
public class toInteger {

    private  Integer result = 0;
    // HashMap
    private  Map<String, Integer> unitMap = new java.util.HashMap<String, Integer>();
    private  Map<String, Integer> numMap = new java.util.HashMap<String, Integer>();

    // 字符串分离
    private  String stryi = new String();
    private  String stryiwan = new String();
    private  String stryione = new String();
    private  String strwan = new String();
    private  String strone = new String();

    public  void ChangeChnString(String chnStr) {
        // unit
        unitMap.put("十", 10);
        unitMap.put("百", 100);
        unitMap.put("千", 1000);
        unitMap.put("万", 10000);

        // num
        numMap.put("零", 0);
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);

        // 去零
        for (int i = 0; i < chnStr.length(); i++) {
            if ('零' == (chnStr.charAt(i))) {
                chnStr = chnStr.substring(0, i) + chnStr.substring(i + 1);
            }
        }
        // 分切成三部分
        int index = 0;
        boolean yiflag = true;
        boolean yiwanflag = true; //亿字节中存在万
        boolean wanflag = true;
        for (int i = 0; i < chnStr.length(); i++) {
            if ('亿' == (chnStr.charAt(i))) {
                // 存在亿前面也有小节的情况
                stryi = chnStr.substring(0, i);
                if (chnStr.indexOf('亿' + "") > chnStr.indexOf('万' + "")) {
                    stryi = chnStr.substring(0, i);
                    for (int j = 0; j < stryi.length(); j++) {
                        if ('万' == (stryi.charAt(j))) {
                            yiwanflag = false;
                            stryiwan = stryi.substring(0, j);
                            stryione = stryi.substring(j + 1);
                        }
                    }
                }
                if(yiwanflag){//亿字节中没有万，直接赋值
                    stryione = stryi;
                }
                index = i + 1;
                yiflag = false;// 分节完毕
                strone = chnStr.substring(i + 1);

            }
            if ('万' == (chnStr.charAt(i)) && chnStr.indexOf('亿' + "") < chnStr.indexOf('万' + "")) {
                strwan = chnStr.substring(index, i);
                strone = chnStr.substring(i + 1);
                wanflag = false;// 分节完毕
            }
        }
        if (yiflag && wanflag) {// 没有处理
            strone = chnStr;
        }
    }

    // 字符串转换为数字
    public  Integer chnStrToNum(String str) {
        Integer strreuslt = 0;
        Integer value1 = 0;
        Integer value2 = 0;
        Integer value3 = 0;
        if (str.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < str.length(); i++) {
            char bit = str.charAt(i);
            // 数字
            if (numMap.containsKey(bit + "")) {
                value1 = numMap.get(bit + "");
                if (i == str.length() - 1) {
                    strreuslt += value1;
                }

            }
            // 单位
            else if (unitMap.containsKey(bit + "")) {
                value2 = unitMap.get(bit + "");
                if (value1 == 0 && value2 == 10) {
                    value3 = 1 * value2;
                } else {
                    value3 = value1 * value2;
                    // 清零避免重复读取
                    value1 = 0;
                }
                strreuslt += value3;
            }
        }
        return strreuslt;
    }

    // 组合数字
    public  Integer ComputeResult(String chnStr) {
        chnStr=chnStr.substring(1, chnStr.length() - 1);
        ChangeChnString(chnStr);
        Integer stryiwanresult = chnStrToNum(stryiwan);
        Integer stryioneresult = chnStrToNum(stryione);
        Integer strwanresult = chnStrToNum(strwan);
        Integer stroneresult = chnStrToNum(strone);
        result = (stryiwanresult + stryioneresult) * 100000000 + strwanresult * 10000 + stroneresult;
        // 重置
        stryi = "";
        strwan = "";
        strone = "";
        return result;
    }

}
