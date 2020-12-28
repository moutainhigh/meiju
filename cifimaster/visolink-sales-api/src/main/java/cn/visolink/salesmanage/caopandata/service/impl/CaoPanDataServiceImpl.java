package cn.visolink.salesmanage.caopandata.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.caopandata.dao.CaoPanDataMapper;
import cn.visolink.salesmanage.caopandata.dao.MingYuanCostDataMapper;
import cn.visolink.salesmanage.caopandata.service.CaoPanDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2019/11/9 11:06 上午
 */
//@Transactional(propagation = Propagation.REQUIRED)
@Service
public class CaoPanDataServiceImpl implements CaoPanDataService {

    @Autowired
    private CaoPanDataMapper caoPanDataMapper;
    @Autowired
    private MingYuanCostDataMapper mingYuanCostDataMapper;

    @Resource(name = "jdbcTemplateNOS")
    private JdbcTemplate jdbcTemplateNOS;

    @Resource(name = "jdbcTemplatemingyuancost")
    private JdbcTemplate jdbcTemplatemingyuancost;

    //每天凌晨一点同步数据
   // @Scheduled(cron="0 0 1 * * ?")
    //@Transactional
    @Override
    public ResultBody getSigningData() {
        Map<Object, Object> resulMap = new HashMap<>();
        String getDataSql="SELECT * FROM tradedataviewforsman";
        //获取操盘数据库签约信息数据（第三方系统数据）
        Integer initedNumber=null;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        List<Map<String, Object>> signData  =jdbcTemplateNOS.queryForList(getDataSql);
        if(signData!=null&&signData.size()>0){
            List<List<Map<String, Object>>> list = getList(signData);
            initedNumber= caoPanDataMapper.emptySinggingData();
            for (List<Map<String, Object>> maps : list) {
                Integer count = caoPanDataMapper.initedSignData(maps);
                integer+=count;
            }
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        //初始化数据前清空本系统（营销管控系统数据）
        String message1="本次初始化共清除"+initedNumber+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resulMap.put("message1",message1);
        resulMap.put("message2",message2);
        resulMap.put("message3",message3);


        caoPanDataMapper.mergeData();
        resulMap.put("message4","数据合并成功");

        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(resulMap);
        resultBody.setMessages("初始化签约信息数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    @Override
    public ResultBody getNosSigningAdd(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -6);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        mon3=mon3+"-01";
        if(!(params==null || params.size()==0 ||params.get("startTime")==null || params.get("startTime").equals(""))){
            mon3= params.get("startTime")+"";
        }
        String getDataSql="SELECT * FROM tradedataviewforsman where check_time>='"+mon3+"'";
        //获取操盘数据库签约信息数据（第三方系统数据）
        List<Map<String, Object>> signData = jdbcTemplateNOS.queryForList(getDataSql);
        if(signData!=null && signData.size()>0){
            List<List<Map<String, Object>>> list = getList(signData);
            //删除数据
            caoPanDataMapper.deleteCaoPanByDate(mon3);
            for (List<Map<String, Object>> maps : list) {
                caoPanDataMapper.initedSignData(maps);
            }
            caoPanDataMapper.updateCaoPanInfo(mon3);
        }
        return null;
    }

    @Override
    public ResultBody initCostData(){
        ResultBody<Object> resultBody = new ResultBody<>();
        String sqlData="SELECT  *FROM  tender.VIEW_CONTRACTINFO ";
        List<Map<String, Object>> costList = jdbcTemplatemingyuancost.queryForList(sqlData);
        Integer deleteInteger=0;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        if(costList!=null&&costList.size()>0){
                List<List<Map<String, Object>>> list = getList(costList);
                deleteInteger= mingYuanCostDataMapper.emptyMingYuanCost();
                for (List<Map<String, Object>> maps : list) {
                    Integer count = mingYuanCostDataMapper.initedCostData(maps);
                    integer+=count;
                }

        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        Map<Object, Object> resultMap = new HashMap<>();
        //初始化数据前清空本系统（营销管控系统数据）
        String message1="本次初始化共清除"+deleteInteger+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resultMap.put("message1",message1);
        resultMap.put("message2",message2);
        resultMap.put("message3",message3);

        return resultBody;
    }

    //批量方法
    private List<List<Map<String, Object>>> getList(List reqMap) {
        //list 为全量集合
        int batchCount = 1000; //每批插入数目
        int batchLastIndex = batchCount;
        List<List<Map<String, Object>>> shareList = new ArrayList<>();
        if (reqMap != null) {
            for (int index = 0; index < reqMap.size(); ) {
                if (batchLastIndex >= reqMap.size()) {
                    batchLastIndex = reqMap.size();
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    break;
                } else {
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    index = batchLastIndex;// 设置下一批下标
                    batchLastIndex = index + (batchCount - 1);
                }
            }
        }
        return shareList;
    }
}
