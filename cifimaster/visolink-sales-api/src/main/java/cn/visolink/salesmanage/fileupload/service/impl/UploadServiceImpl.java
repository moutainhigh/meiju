package cn.visolink.salesmanage.fileupload.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.fileupload.service.UploadService;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.*;

@Service
public class UploadServiceImpl implements UploadService {


    @Autowired
    private FileDao fileDao;

    @Value(("${uploadPath}"))
    private String uploadPath;
    @Value(("${relepath}"))
    private  String relepath;

    @Override
    public ResultBody uploadFile(MultipartFile multipartFile,String filePath,String bizID) {
        //记录上传信息
        ResultBody resultBody = new ResultBody<>();
        HashMap<Object, Object> resultMap = new HashMap<>();
        try {
            if (!multipartFile.isEmpty()) {
                //获取文件名称
                String originalFilename = multipartFile.getOriginalFilename();
                //获取文件后缀名称
                String hzName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //获取文件前缀名
                String beforeFileName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
                //获取文件类型
                String contentType = multipartFile.getContentType();
                //获取文件大小
                long fileSize = multipartFile.getSize();
                //重新命名文件
                String fileTime = DateUtil.format(new Date(), "yyyyMMddHHmmss");
                String newFileName = fileTime+"-"+ UUID.randomUUID().toString()+"."+hzName;
                File saveFile = new File(uploadPath+"/"+filePath);
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }
                multipartFile.transferTo(new File(saveFile,newFileName));

                String id = UUID.randomUUID().toString();
                HashMap<Object, Object> paramMap = new HashMap<>();
                paramMap.put("bizID",bizID);
                paramMap.put("id", id);
                paramMap.put("fileName", originalFilename);
                resultMap.put("name",originalFilename);
                paramMap.put("fileHz", hzName);
                String showName= URLEncoder.encode(originalFilename, "UTF-8");
                paramMap.put("fileUrl", relepath+"/"+filePath+"/"+newFileName+"?n="+showName);
                paramMap.put("fileType", contentType);
                paramMap.put("fileSize", fileSize);
                paramMap.put("showName",beforeFileName);
                fileDao.insertFile(paramMap);
                resultMap.put("url", relepath+"/"+filePath+"/"+newFileName+"?n="+showName);
                resultMap.put("id", id);
            }
            resultBody.setMessages("文件上传成功!");
            resultBody.setData(resultMap);
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("文件上传失败，请重新上传!");
            resultBody.setCode(1);
            return resultBody;
        }
    }

    @Override
    public ResultBody uploadFile_2(MultipartFile multipartFile, String filePath, String bizID,String orderIndex) {
        //记录上传信息
        ResultBody resultBody = new ResultBody<>();
        HashMap<Object, Object> resultMap = new HashMap<>();
        try {
            if (!multipartFile.isEmpty()) {
                //获取文件名称
                String originalFilename = multipartFile.getOriginalFilename();
                //获取文件后缀名称
                String hzName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //获取文件前缀名
                String beforeFileName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
                //获取文件类型
                String contentType = multipartFile.getContentType();
                //获取文件大小
                long fileSize = multipartFile.getSize();
                //重新命名文件
                String fileTime = DateUtil.format(new Date(), "yyyyMMddHHmmss");
                String newFileName = fileTime+"-"+ UUID.randomUUID().toString()+"."+hzName;
               // String s="/Users/WorkSapce/文件/旭辉集团";
                //File saveFile = new File(s);
                //创建文件的路径（文件夹）
               File saveFile = new File(uploadPath+"/"+filePath);
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }
                File fileSave = new File(uploadPath + "/" + filePath, newFileName);

                multipartFile.transferTo(fileSave);

                String id = UUID.randomUUID().toString();
                HashMap<Object, Object> paramMap = new HashMap<>();
                paramMap.put("bizID",bizID);
                paramMap.put("id", id);
                paramMap.put("fileName", originalFilename);
                resultMap.put("name",originalFilename);
                paramMap.put("fileHz", hzName);
                String showName= URLEncoder.encode(originalFilename, "UTF-8");
                paramMap.put("fileUrl", relepath+"/"+filePath+"/"+newFileName+"?n="+showName);
                paramMap.put("fileType", contentType);
                paramMap.put("fileSize", fileSize);
                paramMap.put("showName",beforeFileName);
                paramMap.put("orderIndex",orderIndex);
                fileDao.insertFile(paramMap);
                resultMap.put("url", relepath+"/"+filePath+"/"+newFileName+"?n="+showName);
                resultMap.put("id", id);
                resultMap.put("orderIndex",orderIndex);
                resultBody.setMessages("文件上传成功!");
                resultBody.setData(resultMap);
                return resultBody;
            }else {
                return ResultBody.error(400,"您上传的文件内容为空!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("文件上传失败，请重新上传!");
            resultBody.setCode(1);
            return resultBody;
        }
    }

    @Override
    public ResultBody delFile(Map params) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            Integer integer = fileDao.delFile(params.get("id") + "");
            if(integer<=0){
                resultBody.setMessages("删除失败，找不到此文件!");
                resultBody.setCode(1);
                return resultBody;
            }
            resultBody.setMessages("删除附件成功!");
            return resultBody;
        }catch (Exception e){
            resultBody.setMessages("删除附件失败!");
            resultBody.setCode(1);
            return resultBody;
        }
    }

    @Override
    public int delFileByBizId(String id) {
        return fileDao.delFileByBizId(id);
    }


    @Override
    public List getFileLists(String id) {
        return fileDao.getFileLists(id);
    }

    @Override
    public int updateFileBizId(Map params) {
        return fileDao.updateFileById(params);
    }


}
