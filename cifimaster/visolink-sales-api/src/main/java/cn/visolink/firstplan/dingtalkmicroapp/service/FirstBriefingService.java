package cn.visolink.firstplan.dingtalkmicroapp.service;
import cn.visolink.exception.ResultBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/8/27 10:49 上午
 */
public interface FirstBriefingService {

    public ResultBody getFirstBriefingList(Map map, HttpServletRequest request);

    public ResultBody getFirstBriefingInfo(Map map);

    /**
     * 生成首开播报消息
     * @return
     */
    public ResultBody firstBriefingMessage();

    /**
     * 流程比对推送
     */
    public void flowComparisonPush();
}
