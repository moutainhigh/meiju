package cn.visolink.salesmanage.flieUtils.fileController;

import cn.visolink.salesmanage.flieUtils.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/download")
public class DownloadFile {

    @Autowired
    private FileService fileService;

    //文件下载相关代码
    @RequestMapping("/file/{id}")
    public String download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        if (id != null) {
            Map params = new HashMap();
            params.put("id", id);
            Map fileInfo = fileService.getPath(params);
            String rename = fileInfo.get("FileNameOld").toString();

            String path = fileInfo.get("SaveUrl") + "";
            String showName = fileInfo.get("ShowName") + "";
            String filePath = null;
            if (path != null) {
                filePath = path.substring(0, path.lastIndexOf(File.separator));
            }
            //设置文件路径
            String realPath = this.getClass().getResource("/").getPath() + filePath + File.separator;
            System.out.print(realPath);
            String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
            // realPath = "D://aim//";
            File file = new File(realPath, fileName);
            if (file.exists()) {

                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(rename, "utf-8"));// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }

                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }
}
