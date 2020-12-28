package cn.visolink.firstplan.dataAccess.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 数据接口
 *
 * @author 彭
 *    接口对接,
 *    @return
 *
 * @date 2019-9-20
 */
@Mapper
public  interface DataAccessDao {

    /**
     * 新增
     * @return
     */
    public void insertPanoramaProject(Map params);
    /**
     * 修改
     * @return
     */
    public void updatePanoramaProject(Map params);

    public Map getPanoramaProjectById(Map params);

    /**
     * 写入报备跟来访人
     * @param list
     * @return
     */
    public int insertReport(List list);

    /**
     * 修改小卡大卡认购
     * @param list
     * @return
     */
    public int updateReport(List list);


    /**
     *
     * @param list
     * @return
     */
    public int insertCard(List list);

    /**
     * 删除
     * @param params
     * @return
     */
    int delGuestStorageByDate(Map params);

    int delGuestStorageByProject(Map params);

    int delGuestStorageAll();

    /**
     * 下个节点填报提醒
     * @return
     */
    Map selectNodeReport(Map params);

    /**
     * 获取岗位人
     * @return
     */
    List sendUserName(Map params);


    /**
     * 查询当前需要提醒的节点
     */
    public List selectPlanNodeSendNode();

    /**
     * 修改节点推送状态
     */
    public void updatePlanNodeSendStatusById(String plan_node_id);


    /**
     * 获取钉钉开关状态
     * */
    int getDingPushStatus();


    /**
     * 获取重启是否需要退登
     * */
    int getLoginOutStatus();

    /**
     * 获取是否同步签约计划
     * */
    int getSignAll();

    List<Map> comcomguest(Map map);

}
