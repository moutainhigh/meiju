package cn.visolink.utils;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用http发送方法
 * @author wanghs
 */
public class HttpUtils
{
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
        	String urlNameString = url;
        	if(StringUtils.isNotBlank(param))
        		urlNameString = url + "?" + param;
            log.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {

        	String urlNameString = url;
        	if(StringUtils.isNotBlank(param))
        		urlNameString = url + "?" + param;
            log.info("sendPost - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

    public static String sendSSLPost(String url, String param)
    {
        StringBuilder result = new StringBuilder();
    	String urlNameString = url;
    	if(StringUtils.isNotBlank(param))
    		urlNameString = url + "?" + param;
        try
        {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null)
            {
                if (ret != null && !ret.trim().equals(""))
                {
                    result.append(new String(ret.getBytes("ISO-8859-1"), "utf-8"));
                }
            }
            log.info("recv - {}", result);
            conn.disconnect();
            br.close();
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
        }
        return result.toString();
    }

    public static String sendPostByJson(String url, String message) {
		Map<String, String> dMap = new HashMap<String, String>();
		dMap.put("Content-Type", "application/json;charset=utf-8");
		return sendPost(url, message, "UTF-8", dMap);
	}

	public static String sendPost(String url, String message, Map<String, String> dMap) {
		return sendPost(url,message, "UTF-8", dMap);
	}

	public static String sendPost(String url, String message, String charset, Map<String, String> dMap) {
		return sendPost(url,message, "UTF-8", dMap, false);
	}

	public static String sendPost(String url, String message, String charset, Map<String, String> dMap, boolean lineFlag) {
		String ret = "";
		HttpURLConnection huc = null;
		OutputStream out = null;
		InputStream is = null;
		try{
			URL urls = new URL(url);
			byte[] req = message.getBytes(charset);
			huc = openURL(urls, dMap, charset);
			huc.setRequestProperty("Content-Length", String.valueOf(req.length));
			out = huc.getOutputStream();
			out.write(req);
			out.flush();
			out.close();
			is = huc.getInputStream();
			ret = io2String(is, charset, lineFlag);
			log.info("响应:"+ret);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(huc != null)
				huc.disconnect();
		}
		return ret;
	}


	/**
	 * 请求设置
	 */
	public static HttpURLConnection openURL(URL url, Map<String, String> dMap, String charset) {
		HttpURLConnection huc = null;
		try {
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("POST");
			for(String key : dMap.keySet()){
				huc.setRequestProperty(key, dMap.get(key));
			}
			huc.setConnectTimeout(NumberUtils.toInt(dMap.get("connectTimeout"),30) * 1000);
			huc.setReadTimeout(NumberUtils.toInt(dMap.get("readTime"),60) * 1000);
			huc.setDoOutput(true);
			huc.setDoInput(true);
			huc.setRequestProperty("Accept-Charset", charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return huc;
	}

	public static String io2String(InputStream inputStream){
		return io2String(inputStream, "utf-8", false);
	}

	public static String io2String(InputStream inputStream, boolean lineFlag){
		return io2String(inputStream, "utf-8", lineFlag);
	}

	public static String io2String(InputStream inputStream, String charset, boolean lineFlag){
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, charset));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(lineFlag){
					if(buffer.length() == 0)
						buffer.append(line);
					else buffer.append("\r\n"+line);
				}else
					buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer.toString();
	}

    private static class TrustAnyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }


    /**
     * 获取url无参数段
     * @param url
     * @return
     */
    public static String getUrlBase(String url){
        if(url.indexOf("?") != 0){
            String[] arrSplit = null;
            arrSplit = url.split("[?]");
            return arrSplit[0];
        }else{
            return url;
        }
    }

    public static String getUrlParam(String url){
        if(url.indexOf("?") >= 0){
            String[] arrSplit = null;
            arrSplit = url.split("[?]");
            return arrSplit[1];
        }else{
            return "";
        }
    }

    /**
     * 将url参数转换成map
     *
     * @param param
     *            aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParamMap(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s= StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

}
