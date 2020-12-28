package cn.visolink.system.company.service;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.18
 */
public interface CompanyService {

    List<Map> getAllList(Map map);
    Integer getAllListCount(Map map);

    int insertCompany(Map map);

    List<Map> getAllProject();

    PageInfo getAssInforData(Map paramMap);

    int updateCompanyById(Map paramMap);

    int deleteCompanyById(String id);
}
