package cn.visolink.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

/**
 * 封装分页对象工具类
 *
 * @author yangjie
 * @since 2020-05-14
 */
public class PagingParamUtil {
    /**
     * 默认页码：1
     */
    private static final int PAGE = 1;
    /**
     * 默认每页大小：10
     */
    private static final int SIZE = 10;

    /**
     * 页码参数处理
     *
     * @param currentPage 页码
     * @return return
     */
    private static Integer currentPageHandle(Integer currentPage) {
        return currentPage == null ? PAGE : currentPage;
    }

    /**
     * 每页大小参数处理
     *
     * @param pageSize 每页大小
     * @return return
     */
    private static Integer pageSizeHandle(Integer pageSize) {
        return pageSize == null ? SIZE : pageSize;
    }

    /**
     * 处理分页参数，返回 Page
     *
     * @param map map
     * @return Page
     */
    public static Page<Map> getPage(Map<String, String> map) {
        Integer currentPage = PagingParamUtil.currentPageHandle(Integer.parseInt(map.get("currentPage")));
        Integer pageSize = PagingParamUtil.pageSizeHandle(Integer.parseInt(map.get("pageSize")));
        return new Page<>(currentPage, pageSize);
    }
}
