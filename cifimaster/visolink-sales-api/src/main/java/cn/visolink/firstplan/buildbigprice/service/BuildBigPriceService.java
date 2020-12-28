package cn.visolink.firstplan.buildbigprice.service;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/16 4:45 下午
 */

public interface BuildBigPriceService {


    //导出EXCEL模版
    ResultBody exportExcelTemplate(HttpServletRequest request, HttpServletResponse response , Map map);

    VisolinkResultBody getProjectStages(Map map);

    VisolinkResultBody importExcelTemplate(MultipartFile multipartFile,Map map);
    VisolinkResultBody updateBigPriceIsSave(Map map);

    /*移除Map中值为空的键值对*/
     Map filterMap(Map map);

     VisolinkResultBody viewBigBuildData(Map map,HttpServletRequest request);
     ResultBody exportBuildBigPriceData(HttpServletRequest request,HttpServletResponse response);
}
