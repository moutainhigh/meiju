package cn.visolink.salesmanage.fileupload.service;

import cn.visolink.exception.ResultBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UploadService {

    public ResultBody uploadFile(@RequestParam("file") MultipartFile multipartFile,String filePath,String  bizID);
    public ResultBody uploadFile_2(@RequestParam("file") MultipartFile multipartFile,String filePath,String  bizID,String orderIndex);


    public ResultBody delFile(Map params);


    public int delFileByBizId(String id);

    public List getFileLists(String id);


    public int updateFileBizId(Map params);

}
