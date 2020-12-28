package cn.visolink.system.company.service.impl;

import cn.visolink.system.company.dao.CompanyMapper;
import cn.visolink.system.company.service.CompanyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.18
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public List<Map> getAllList(Map map) {
        return companyMapper.getAllList(map);
    }   @Override
    public Integer getAllListCount(Map map) {
        return companyMapper.getAllListCount(map);
    }

    @Override
    public int insertCompany(Map map) {
        return companyMapper.insertCompany(map);
    }

    @Override
    public List<Map> getAllProject() {
        return companyMapper.getAllProject();
    }

    @Override
    public PageInfo getAssInforData(Map paramMap) {
        // form -> do 转换
        int pageIndex = Integer.parseInt(paramMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        PageHelper.startPage(pageIndex, pageSize);
        List list = companyMapper.getAssInforData(paramMap);
        PageInfo<Object> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }

    @Override
    public int updateCompanyById(Map map) {
        return companyMapper.updateCompanyById(map);
    }
    @Override
    public int deleteCompanyById(String id){
        return companyMapper.deleteCompanyById(id);
    }
}
