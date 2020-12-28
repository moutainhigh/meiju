package cn.visolink.salesmanage.fileupload.controller;

import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.fileupload.service.UploadService;
import cn.visolink.utils.StringUtil;
import cn.visolink.logs.aop.log.Log;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@Api(tags = "上传")
@RequestMapping("/Upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @Log("上传")
    @CessBody
    @ApiOperation(value = "上传")
    @PostMapping(value = "/Common.action")
    public  List<Map> Common(HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<Map>  result =new ArrayList<>();

        List<Map> list=new ArrayList<>();

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
                            + File.separator+bizType+File.separator;
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
        result = list;
        return result;
    }


    /**
     * 上传附件
     */
    @Log("上传附件")
    @ApiOperation(value = "上传附件")
    @PostMapping(value = "/uploadFile")
    public ResultBody uploadFile(@RequestParam("file")MultipartFile multipartFile,@RequestParam("filePath")String filePath,@RequestParam("bizID")String bizID){
        return uploadService.uploadFile(multipartFile,filePath,bizID);
    }
    /**
     * 上传附件-2
     */
    @Log("上传附件-2")
    @ApiOperation(value = "上传附件-2")
    @PostMapping(value = "/uploadFile_2")
    public ResultBody uploadFile(@RequestParam("file")MultipartFile multipartFile,@RequestParam("filePath")String filePath,@RequestParam("bizID")String bizID,@RequestParam("orderIndex")String orderIndex){
        return uploadService.uploadFile_2(multipartFile,filePath,bizID,orderIndex);
    }

    /**
     * 上传附件
     */
    @Log("删除附件")
    @ApiOperation(value = "删除附件")
    @PostMapping(value = "/delFile")
    public ResultBody delFile(@RequestBody Map params){
        return uploadService.delFile(params);
    }
}