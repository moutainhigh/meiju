package cn.visolink.salesmanage.commissionpolicy.service.impl;

import cn.visolink.salesmanage.commissionpolicy.dao.CmPolicyApplyMapper;
import cn.visolink.salesmanage.commissionpolicy.service.CmPolicyApplyService;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.HEAD;
import java.util.*;

@Service
public class CmPolicyApplyServiceImpl implements CmPolicyApplyService {


    @Autowired
    private CmPolicyApplyMapper cmPolicyApplyMapper;

    @Autowired
    private PackageanddiscountDao packageanddiscountDao;

    /**
     * 获取佣金政策
     *
     * @param map 参数
     * @return 政策列表
     */

    @Override
    public PageInfo getPolicy(Map map) {
        PageHelper.startPage(Integer.parseInt(map.get("pageNum") + ""), Integer.parseInt(map.get("pageSize") + ""));
        List<Map> list = cmPolicyApplyMapper.getPolicy(map);
        return new PageInfo(list);
    }


    /**
     * 获取佣金政策明细
     *
     * @param policyId 政策ID
     * @return 政策详情
     */
    @Override
    public Map getPolicyDetail(String policyId, String policyType) {
        Map policyMap = new HashMap();
        if ("0".equals(policyType) || Integer.parseInt(policyType) == 0) {
            policyMap = cmPolicyApplyMapper.getBrokerPolicyDetail(policyId);

        } else {
            policyMap = cmPolicyApplyMapper.getAgencyPolicyDetail(policyId);
        }
        List<Map> list = cmPolicyApplyMapper.getPolicyFile(policyId);
        String flowId = cmPolicyApplyMapper.getFlowId(policyId);
        List<Map> orgList = cmPolicyApplyMapper.getBizOrgList(policyId);
        if (policyMap != null) {
            if (list.size() > 0) {
                policyMap.put("fileList", list);

            } else {
                policyMap.put("fileList", new ArrayList<>(0));
            }
            if (orgList.size() > 0) {
                policyMap.put("org_list", orgList);
            } else {
                policyMap.put("org_list", new ArrayList<>(0));
            }
            policyMap.put("flow_id", flowId);
        }

        return policyMap;
    }


    @Override
    public List<Map> getProjectStage(String projectId) {
        return cmPolicyApplyMapper.getProjectStage(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map deletePolicy(String policyId) {
        //删除主表
        cmPolicyApplyMapper.deletePolicy(policyId);
        //删除子表
        // cmPolicyApplyMapper.deletePolicyDetail(policyId);
        //删除附件
        cmPolicyApplyMapper.deleteFile(policyId);
        //删除flow表
        cmPolicyApplyMapper.deleteFlow(policyId);
        return null;
    }

    @Override
    public Map updatePolicyStatus(String policyId, String state) {
        //修改主表
        cmPolicyApplyMapper.updatePolicyStatus(policyId, state);
        return null;
    }

    /**
     * 全民营销政策申请
     *
     * @param map 参数
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int brokerPolicyApply(Map map) {
        //首先插入主表
        cmPolicyApplyMapper.saveCmPolicy(map);
        //其次插入全民经纪人政策表
        cmPolicyApplyMapper.saveBrokerPolicy(map);
        //最后插入flow表
        String jsonMap = JSONObject.toJSONString(map);


//查询事业部
        Map flowMap = new HashMap();
        String business_unit_id = "";
        if (map.get("project_id") != null && !"2".equals(map.get("org_level")) && !"3".equals(map.get("org_level")) && !"".equals(map.get("project_id"))) {
            Map buinessData = packageanddiscountDao.getBuinessData(map.get("project_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        } else if ("2".equals(map.get("org_level"))) {
            business_unit_id = map.get("org_id") + "";
        } else if ("3".equals(map.get("org_level"))) {
            Map buinessData = packageanddiscountDao.getBuinessDataByOrgId(map.get("org_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        }

        //,
        String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000","10030000","10170000","10120000"};
       //
        String orgNameData[] = {"上海", "皖赣", "西南", "苏南", "浙江", "山东","华北","西北","广桂"};
        List<String> asList = Arrays.asList(buinsData);
        int indexOf = asList.indexOf(business_unit_id);
        if (indexOf == -1) {
            flowMap.put("orgName", "事业部");
        } else {
            flowMap.put("orgName", orgNameData[indexOf]);
        }

        if (map.get("org_level") != null) {
            if ("2".equals(map.get("org_level"))) {
                flowMap.put("comcommon", "{\"isArea\":\"1\"}");
            } else if ("3".equals(map.get("org_level"))) {
                flowMap.put("comcommon", "{\"isArea\":\"2\"}");
            } else {
                flowMap.put("comcommon", "{\"isArea\":\"0\"}");
            }
        }
        flowMap.put("flow_json", jsonMap);
        flowMap.put("project_id", map.get("project_id"));
        flowMap.put("stage_id", map.get("project_id"));
        flowMap.put("flow_type", "commission");
        flowMap.put("json_id", map.get("policyId"));
        //  flowMap.put("base_id",map.get("stage_id"));
        flowMap.put("flow_code", "Pintube_NatMarkeUsePolicy");
        flowMap.put("creator", map.get("creator"));
        //   flowMap.put("orgName",map.get("creator"));
        flowMap.put("TITLE", map.get("policy_name"));
        flowMap.put("post_name", map.get("creator"));
       /* List OrgList = new ArrayList();
        cmPolicyApplyMapper.saveOrgList(map.get("policyId").toString(),OrgList);*/
        cmPolicyApplyMapper.saveBrokerPolicyFlow(flowMap);
        //根据附件ID修改附件表 把政策ID添加进去
        if (map.get("fileList") != "" && map.get("fileList") != null) {
            String fileStr = map.get("fileList") + "";
            fileStr = fileStr.substring(1, fileStr.length() - 1);
            String[] arr = fileStr.split(",");
            for (String s : arr) {
                s = s.replace(" ", "");
                cmPolicyApplyMapper.updateFileBizId(s, map.get("policyId") + "");
            }
        }

        return 0;
    }

    /**
     * 中介政策申请
     *
     * @param map 参数
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int agencyPolicyApply(Map map) {
        //首先插入主表
        cmPolicyApplyMapper.saveCmPolicy(map);
        //其次插入全民经纪人政策表
        cmPolicyApplyMapper.saveAgencyPolicy(map);
        //最后插入flow表
        String jsonMap = JSONObject.toJSONString(map);
        Map flowMap = new HashMap();
        String business_unit_id = "";
        if (map.get("project_id") != null && !"2".equals(map.get("org_level")) && !"3".equals(map.get("org_level")) && !"".equals(map.get("project_id"))) {
            Map buinessData = packageanddiscountDao.getBuinessData(map.get("project_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        } else if ("2".equals(map.get("org_level"))) {
            business_unit_id = map.get("org_id") + "";
        } else if ("3".equals(map.get("org_level"))) {
            Map buinessData = packageanddiscountDao.getBuinessDataByOrgId(map.get("org_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        }
        //
        String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000","10030000","10170000","10120000"};
        //
        String orgNameData[] = {"上海", "皖赣", "西南", "苏南", "浙江", "山东","华北","西北","广桂"};
        List<String> asList = Arrays.asList(buinsData);
        int indexOf = asList.indexOf(business_unit_id);
        if (indexOf == -1) {
            flowMap.put("orgName", "事业部");
        } else {
            flowMap.put("orgName", orgNameData[indexOf]);
        }
        flowMap.put("flow_json", jsonMap);
        flowMap.put("project_id", map.get("project_id"));
        flowMap.put("stage_id", map.get("project_id"));
        flowMap.put("flow_type", "commission");
        flowMap.put("json_id", map.get("policyId"));
        //  flowMap.put("base_id",map.get("stage_id"));
        flowMap.put("flow_code", "Pintube_InterUsePolicy");
        flowMap.put("creator", map.get("creator"));
        //   flowMap.put("orgName",map.get("creator"));
        flowMap.put("TITLE", map.get("policy_name"));
        //flowMap.put("comcommon","{\"start_type\":\"'"+map.get("start_type")+"'\"}");
        if (map.get("org_level") != null) {
            if ("2".equals(map.get("org_level"))) {
                flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"1\"}");
            } else if ("3".equals(map.get("org_level"))) {
                flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"2\"}");
            } else {
                flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"0\"}");
            }
        }
        String comcommon = flowMap.get("comcommon") + "";
        comcommon = comcommon.replace("'", "");
        flowMap.put("comcommon", comcommon);
        flowMap.put("post_name", map.get("creator"));
        cmPolicyApplyMapper.saveBrokerPolicyFlow(flowMap);
        //把组织全部存进中间表
     /*   List OrgList = new ArrayList();
        cmPolicyApplyMapper.saveOrgList(map.get("policyId").toString(),OrgList);*/
        //根据附件ID修改附件表 把政策ID添加进去
        if (map.get("fileList") != "" && map.get("fileList") != null) {
            String fileStr = map.get("fileList") + "";
            fileStr = fileStr.substring(1, fileStr.length() - 1);
            String[] arr = fileStr.split(",");
            for (String s : arr) {
                s = s.replace(" ", "");
                cmPolicyApplyMapper.updateFileBizId(s, map.get("policyId") + "");
            }
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePolic(Map map) {
        //首先修改主表
        cmPolicyApplyMapper.updatePolicy(map);

        if (map.get("policy_type") == "1" || Integer.parseInt(map.get("policy_type") + "") == 1) {
            cmPolicyApplyMapper.updateAgencyrPolicy(map);
        } else {
            cmPolicyApplyMapper.updateBrokerPolicy(map);
        }

        //其次修改全民经纪人政策表
        //最后修改flow表
        String jsonMap = JSONObject.toJSONString(map);
        Map flowMap = new HashMap();
        String business_unit_id = "";
        if (map.get("project_id") != null && !"2".equals(map.get("org_level")) && !"3".equals(map.get("org_level")) && !"".equals(map.get("project_id"))) {
            Map buinessData = packageanddiscountDao.getBuinessData(map.get("project_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        } else if ("2".equals(map.get("org_level"))) {
            business_unit_id = map.get("org_id") + "";
        } else if ("3".equals(map.get("org_level"))) {
            Map buinessData = packageanddiscountDao.getBuinessDataByOrgId(map.get("org_id") + "");
            business_unit_id = buinessData.get("business_unit_id") + "";
        }

        //
        String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000","10030000","10170000","10120000"};
        //
        String orgNameData[] = {"上海", "皖赣", "西南", "苏南", "浙江", "山东","华北","西北","广桂"};
        List<String> asList = Arrays.asList(buinsData);
        int indexOf = asList.indexOf(business_unit_id);
        if (indexOf == -1) {
            flowMap.put("orgName", "事业部");
        } else {
            flowMap.put("orgName", orgNameData[indexOf]);
        }

        flowMap.put("flow_json", jsonMap);
        flowMap.put("policyId", map.get("policyId"));
        flowMap.put("project_id", map.get("project_id"));
        flowMap.put("stage_id", map.get("project_id"));
        flowMap.put("TITLE", map.get("policy_name"));
        if (map.get("start_type") != null) {
            if (map.get("org_level") != null) {
                if ("2".equals(map.get("org_level"))) {
                    flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"1\"}");
                } else if ("3".equals(map.get("org_level"))) {
                    flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"2\"}");
                } else {
                    flowMap.put("comcommon", "{\"start_type\":\"" + map.get("start_type") + "\",\"isArea\":\"0\"}");
                }
            }
            // flowMap.put("comcommon","{\"start_type\":\"'"+map.get("start_type")+"'\"}");
            String comcommon = flowMap.get("comcommon") + "";
            comcommon = comcommon.replace("'", "");
            flowMap.put("comcommon", comcommon);
        }
        cmPolicyApplyMapper.updateFlow(flowMap);
        cmPolicyApplyMapper.updateFileStatus(map.get("policyId") + "");
        if (map.get("fileList") != "" && map.get("fileList") != null) {
            String fileStr = map.get("fileList") + "";
            fileStr = fileStr.substring(1, fileStr.length() - 1);
            String[] arr = fileStr.split(",");
            for (String s : arr) {
                s = s.replace(" ", "");
                cmPolicyApplyMapper.updateFileBizId(s, map.get("policyId") + "");
            }
        }
        return 0;
    }


}
