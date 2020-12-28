package cn.visolink.salesmanage.fileimport.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImportService {

     Map monthlyPlanImport(MultipartFile file, String months, int type);

    void listThreePlanImport(MultipartFile file,   String months,String projectId);
}
