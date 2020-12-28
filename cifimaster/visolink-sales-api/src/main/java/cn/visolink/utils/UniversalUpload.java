package cn.visolink.utils;

import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件上传通用工具类
 * @author Administrator
 *
 */
public
class UniversalUpload {
    public static List<Map> uploadFile(HttpServletRequest request){

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        List<Map> list=new ArrayList<Map>();
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request))
        {
            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
            Iterator iter=multiRequest.getFileNames();
            String bizType=request.getParameter("BizType");
            String UserID=request.getParameter("UserID");
            while(iter.hasNext())
            {
                //返回结果
                Map<String,Object> returnMap = new HashMap<>();
                MultipartFile file=multiRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                    String uuid= UUID.randomUUID().toString();
                    String fileName=file.getOriginalFilename();

                    String expName=fileName.substring(fileName.lastIndexOf(".")+1);


                    String path=request.getSession().getServletContext().getRealPath("/")+"Uploads"
                            +File.separator+bizType+ File.separator;
                    File saveFile=new File(path);
                    if(!saveFile.exists()){
                        saveFile.mkdirs();
                    }
	                    /*String contextPath=request.getServletContext().getContextPath()+"Uploads"+
	                    		File.separator+File.separator+bizType+File.separator+File.separator+uuid+"\\."+expName;*/
                    String contextPath="Uploads"+
                            File.separator+File.separator+bizType+File.separator+File.separator+uuid+"."+expName;

                    //上传
                    try {
                        file.transferTo(new File(saveFile,uuid+"."+expName));

                        returnMap.put("SaveFileName", uuid);//保存到磁盘的文件名
                        returnMap.put("ExpName", expName);//扩展名
                        returnMap.put("OriginalFileName", fileName);//原始文件名
                        returnMap.put("Path", contextPath);//保存到磁盘路径
                        returnMap.put("FileSize", file.getSize());//文件大小
                        returnMap.put("FileType", file.getContentType());//文件类型
                        returnMap.put("UserID", StringUtil.isEmpty(UserID)?"":UserID);//创建人
                        list.add(returnMap);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;

    }

    /**
     * 批量删除文件
     * @param request
     * @param paths   文件路径集合
     */

    public static void delFiles(HttpServletRequest request,List<String> paths){

        String basPath=request.getSession().getServletContext().getRealPath("/");

        for (String path : paths) {
            File file=new File(basPath+path);
            if(file.exists()){
                file.delete();
            }
        }
    }
}
