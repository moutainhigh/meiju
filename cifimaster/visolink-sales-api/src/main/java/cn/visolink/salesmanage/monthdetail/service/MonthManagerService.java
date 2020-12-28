package cn.visolink.salesmanage.monthdetail.service;

import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MonthManagerService {


    /**
     * 通过项目ID和月份来查询表二（月度计划明细）信息
     * @param
     * @return
     */
    public Map<String,Object> mouthPlanSelect(String userId,String projectId, String months, Integer isEffective);

    /**
     * 通过项目ID和月份来查询表二信息,若没有数据则初始化，被controller调用（月度计划明细）
     * @param
     * @return
     */
    public Map<String,Object> allMouthPlanSelect(String userId,String projectId, String months, Integer isEffective);


        /**
         * 通过项目ID和月份来初始化（月度计划明细）
         * @param
         * @return
         */
    public Integer initialMouthPlan(String projectId, String months);

    /**
     * 通过项目ID和月份来跟新表二信息（月度计划明细）
     * @param map
     * @return
     */
    public Integer mouthPlanUpdate(Map<String, Object> map);

    /**
     * 通过项目ID和月份来设置表二（月度计划明细）是否上报状态
     * @param
     * @return
     */
    public Integer mouthPlanEffective(String projectId, String months, Integer isEffective);


 

    /**
     * 通过项目ID和月份来设置表 四（月度计划明细）里面的风险
     * @param map
     * @return
     */
    public Integer mouthPlanUpdateRisk(Map<String, Object> map);


    /**
     * 初始化招揽客人的渠道费用明细并表示到前端去
     *
     * @return
     */
    public List<Map> initialiseChannelDetail(String projectId, String months,Integer State);



    /**
     * 通过月份和项目ID来查找渠道费用明细
     *
     * @return
     */
    public List<Map> selectMouthChannelDetail(String projectId, String months, Integer isEffective);

    /**
     * 通过月份和项目ID来查找渠道费用明细,若有值则直接表示到前端，若没有则初始化到前端
     *
     * @return
     */
    public List<Map> allChannelDetailSelect(String projectId, String months, Integer isEffective);

    /**
     * 通过项目ID和月份跟新渠道费用明细
     *
     * @return
     */
    public Integer  updateChannelDetail(List<Map> listmap);


    /**
     * 通过项目ID和月份来设置是否上报可用状态
     *
     * @return
     */
    public String channelDetailTestEffective(String userId,String projectId, String months, Integer isEffective);


    /**
     * 通过项目ID和月份来查找周计划
     *
     * @return
     */
    public List<Map> selectWeeklyPlan(String projectId, String months, Integer isEffective);

    /**
     * 通过项目ID和月份来初始化周计划
     *
     * @return
     */
    public void initialWeeklyPlan(String projectId, String months);

    /**
     * 通过项目ID和月份来跟新周计划
     *
     * @return
     */
    public Integer updateWeeklyPlan(List<Map> listmap);

    /**
     * 通过项目ID和月份来决定周计划上报状态
     *
     * @return
     */
    public String weeklyPlanIsEffective(String projectId, String months, Integer isEffective);
    /**
     * 通过项目ID和月份来查找或初始化周计划，被controller调用
     *
     * @return
     */
    public List<Map> allWeeklyPlanSelect(String projectId, String months, Integer isEffective);
    /*
     * 查找表三下面柱状图的数据，每月matter金额和成交率
     **/
    public List<Map> columnSelect(String projectId, String months,Integer isEffective);
    /**
     * 通过项目ID和月份来查找周计划
     *求上一个月的，在表四中和本月做对比
     * @return
     */
    public List<Map> frontselectWeeklyPlan(String projectId, String months,Integer isEffective);

    /**
     * 附件的上传
     *
     * @return
     */
    public  List<Map> UploadAttach(MultipartFile file, HttpServletRequest request) throws IOException;


        /**
         * 附件的查找
         *
         * @return
         */
    public List<Map> selectAttach(String projectId);

    /**
     * 附件的删除
     *
     * @return
     */
    public Integer deleteAttach(String fileID, Integer IsDel);
    /**
     * 测试表二三四是否可以上报
     *
     * @return
     */
    public String weeklyPlanTestEffective(String userId,String projectId, String months,Integer isEffective);
    /**
     * 查找项目名
     *
     * @return
     */
    public String  selectProjectName(String projectId);

    }
