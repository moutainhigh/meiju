package cn.visolink.salesmanage.nonprojectpur.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.nonprojectpur.mapper.NonProjectPurMapper;
import cn.visolink.salesmanage.nonprojectpur.service.NonProjectPurService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 非工程采购数据处理实现
 * </p>
 *
 * @author bql
 * @since 2020-11-26
 */

@Service
public class NonProjectPurServiceImpl implements NonProjectPurService {

    private final NonProjectPurMapper nonProjectPurMapper;

    @Value("${nonProjectPur.url}")
    private String nonProjectPur;

    @Value("${nonProjectPur.userId}")
    private String nonProjectPurUserId;

    @Value("${nonProjectPur.password}")
    private String nonProjectPurPassword;

    public NonProjectPurServiceImpl(NonProjectPurMapper nonProjectPurMapper) {
        this.nonProjectPurMapper = nonProjectPurMapper;
    }


    /**
     * 初始化非工程采购数据
     *
     * */
    @Override
    public void initNonProjectPur(){
        try {
            /* esbInfo参数 */
            Map esbInfoTime = new HashMap<>();
            esbInfoTime.put("instId", "47f1a9db3f434426baf8993b5df07e86");
            esbInfoTime.put("requestTime", "1577182054138");

            /* 分页参数 */
            Map queryInfo = new HashMap<>();
            queryInfo.put("pageSize", "20");
            queryInfo.put("pageNumber", "1");

            /* 条件参数 */
            Map requestInfoTime = new HashMap<>();
            requestInfoTime.put("startTime", "2017-01-01 11:43:45");
            requestInfoTime.put("endTime", DateUtil.format(new Date(), "yyyy-mm-dd hh:mm:ss"));
            requestInfoTime.put("source", 0);

            Map createTimeAndMap = new HashMap();
            createTimeAndMap.put("esbInfo", esbInfoTime);
            createTimeAndMap.put("queryInfo", queryInfo);
            createTimeAndMap.put("requestInfo", requestInfoTime);
            nonProjectPurMapper.deleteNonProjectPur();

            int num = 1;
            for(int i = 0; i<num ; i++){
                queryInfo.put("pageNumber", i+1);
                System.out.println(JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)));
                JSONObject createTimeriskresult = HttpRequestUtil.httpPost(nonProjectPur, nonProjectPurUserId, nonProjectPurPassword, JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)), false);  //发送数据
                Map aa = JSONObject.toJavaObject(createTimeriskresult, Map.class);
                Map resultInfo = (Map) aa.get("queryInfo");
                num = Integer.parseInt(resultInfo.get("totalPage")+"");
                System.out.println("一共"+resultInfo.get("totalRecord")+"条数据");
                List<Map<String, Object>> projectInfo = (List<Map<String, Object>>) aa.get("resultInfo");
                projectInfo.remove(null);
                for(Map<String, Object> map :projectInfo){
                    if(map != null){
                        for (Map.Entry<String,Object> entry: map.entrySet()){
                            String value =String.valueOf(entry.getValue());
                            if("null".equalsIgnoreCase(value)||"".equals(value)){
                                map.put(entry.getKey(),"");
                            }

                        }
                        if(map.get("categorys")!=null){
                            map.put("categorys",JSONObject.toJavaObject((JSON) map.get("categorys"), String.class));
                        }
                        if(map.get("banks")!=null){
                            map.put("banks",JSONObject.toJavaObject((JSON) map.get("banks"), String.class));
                        }
                        if(map.get("regionOrgn")!=null){
                            map.put("regionOrgn",JSONObject.toJavaObject((JSON) map.get("regionOrgn"), String.class));
                        }
                    }else{

                    }
                }
                if (projectInfo.size() > 0) {
                    nonProjectPurMapper.initNonProjectPur(projectInfo);
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-11_1006, "数据库查询失败！");
        }
    }

    /**
     * 明源对接数据接口，修改付款单状态
     *
     * @param request request
     * @param dateList 参数
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map updateNonProjectPur(HttpServletRequest request, List<Map<String, Object>> dateList) {
        try {
            dateList.remove(null);
            for (Map<String, Object> map : dateList) {
                if (map != null) {
                    nonProjectPurMapper.deleteNonProjectPurByCode(map.get("businessCode")+"");
                    if (map.get("categorys") != null) {
                        map.put("categorys", JSON.toJSONString(map.get("categorys")));
                    }
                    if (map.get("banks") != null) {
                        map.put("banks", JSON.toJSONString(map.get("banks")));
                    }
                    if (map.get("regionOrgn") != null) {
                        map.put("regionOrgn", JSON.toJSONString(map.get("regionOrgn")));
                    }
                }
            }
            if (dateList.size() > 0) {
                nonProjectPurMapper.initNonProjectPur(dateList);
            }
            return ResultUtil.getSuccessMap("数据更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-11_1006, "数据异常！");
        }
    }
}
