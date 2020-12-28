package cn.visolink.firstplan.message.dao;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/1 3:21 下午
 */
public interface NodeMessageSendDao {
     List<Map> getSendData(String date);
}
