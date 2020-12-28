package cn.visolink.utils;

import cn.hutool.json.XML;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;

/**
 * @author sjl
 * @Created date 2019/11/11 10:16 下午
 */
@Singleton
public class XmlUtil {

    public static Document parseXmlString(String xmlStr){

        try{
            InputSource is = new InputSource(new StringReader(xmlStr));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,Object> getXmlBodyContext(String bodyXml){

        cn.hutool.json.JSONObject xmlJSONObj = XML.toJSONObject(bodyXml);
        String result = xmlJSONObj.getStr("DATA");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data",result);
        return resultMap ;
    }

    }
    
