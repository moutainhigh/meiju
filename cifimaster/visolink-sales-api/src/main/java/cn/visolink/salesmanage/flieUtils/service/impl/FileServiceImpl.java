package cn.visolink.salesmanage.flieUtils.service.impl;

import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.salesmanage.flieUtils.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class FileServiceImpl  implements FileService {

    @Autowired
    private FileDao fileDao;

    @Override
    public Map getPath(Map params) {
        return fileDao.getPath(params);
    }
}
