package cn.visolink.system.company.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.18
 */
@Mapper
public interface CompanyMapper {

    List<Map> getAllList(Map map);
    Integer getAllListCount(Map map);

    int insertCompany(Map map);

    List<Map> getAllProject();

    List<Map> getAssInforData(Map paramMap);

    int updateCompanyById(Map paramMap);

    int deleteCompanyById(String id);
}
