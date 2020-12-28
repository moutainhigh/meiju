package cn.visolink.common;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.core.dbhelper.api.FrameServiceApi;
import org.apache.wicket.core.dbhelper.sql.BizSQLParser;
import org.apache.wicket.core.dbhelper.sql.SQLObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class DataRequestService {
	
	@Autowired
	public FrameServiceApi frameServiceApi;


	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 返回原始frameServiceApi.getJsonByJsonData方法返回的原始数据
	 * @param map
	 * @return
	 */
	public Map dataRequestoriginal(Map map){
		
		Gson gson =new Gson();
		
		String paramJson=gson.toJson(map);
		
		String resultJson=frameServiceApi.getJsonByJsonData(paramJson);
		
		Map resultMap=gson.fromJson(resultJson, Map.class);
		
		return resultMap;
	}
	
	
	/**
	 * 返回原始frameServiceApi.getJsonByJsonData方法返回的数据中的data部分
	 * @param map
	 * @return
	 */
	public Map dataRequestForData(Map map){
		
		Gson gson =new Gson();
		
		String paramJson=gson.toJson(map);
		
		String resultJson=frameServiceApi.getJsonByJsonData(paramJson);
		
		Map resultMap=gson.fromJson(resultJson, LinkedHashMap.class);
		
		return (Map) resultMap.get("data");
	}

	/**
	 * 新增、修改操作
	 *
	 * @param sqlId 执行语句
	 * @param dataMap 传入参数
	 * @return
	 */
	public int updateByJdbcTemplate(String sqlId,Map dataMap) {
		BizSQLParser bizSQLParser = new BizSQLParser();
		SQLObject so = bizSQLParser.getSQLObject(sqlId);

		String sql = so.getSql().trim();

		Pattern pattern = Pattern.compile("\\{([^>]+?)}");

		Matcher matcher = pattern.matcher(sql);
		List<String> paramList =new ArrayList<>();
		while(matcher.find()) {
			String content = matcher.group(1);
			paramList.add(content);
		}
		pattern = Pattern.compile("'\\{([^>]+?)}'");

		sql = sql.replaceAll(pattern.toString(),"?");
		List<Object> paramsObject = new ArrayList<>();

		for (String s : paramList) {
			if(null!=dataMap.get(s)){
				paramsObject.add(dataMap.get(s));
			}else{
				paramsObject.add(null);
			}
		}
		log.info("请求参数"+ JSON.toJSONString(dataMap));
		log.info("执行SQL参数value"+ JSON.toJSONString(paramsObject)+"执行SQLKey"+ JSON.toJSONString(paramList));
		return jdbcTemplate.update(sql,paramsObject.toArray());
	}


	/**
	 * 查询
	 * @param sqlId 查询SQL
	 * @param dataMap 参数列表
	 * @return
	 */
	public List queryForList(String sqlId, Map<String, String> dataMap) {
		BizSQLParser bizSQLParser = new BizSQLParser();
		SQLObject so = bizSQLParser.getSQLObject(sqlId);

		String sql = so.getSql().trim();

		Pattern pattern = Pattern.compile("\\{([^>]+?)}");

		Matcher matcher = pattern.matcher(sql);
		List<String> paramList =new ArrayList<>();
		while(matcher.find()) {
			String content = matcher.group(1);
			paramList.add(content);
		}
		pattern = Pattern.compile("'\\{([^>]+?)}'");

		sql = sql.replaceAll(pattern.toString(),"?");
		List<Object> paramsObject = new ArrayList<>();


		if(sql.indexOf("{")>0){
			pattern = Pattern.compile("\\{([^>]+?)}");

			sql = sql.replaceAll(pattern.toString(), "?");
		}

		for (String s : paramList) {
			if(null!=dataMap.get(s)){
				paramsObject.add(dataMap.get(s));
			}else{
				paramsObject.add("");
			}
		}
		log.info("请求参数"+ JSON.toJSONString(dataMap));
		log.info("执行SQL参数value"+ JSON.toJSONString(paramsObject)+"执行SQLKey"+ JSON.toJSONString(paramList));
		return jdbcTemplate.queryForList(sql,paramsObject.toArray());
	}



	/**
	 * 查询
	 * @param sqlId 查询SQL
	 * @param dataMap 参数列表
	 * @return
	 */
	public Map queryForMap(String sqlId, Map dataMap) {
		BizSQLParser bizSQLParser = new BizSQLParser();
		SQLObject so = bizSQLParser.getSQLObject(sqlId);

		String sql = so.getSql().trim();

		Pattern pattern = Pattern.compile("\\{([^>]+?)}");

		Matcher matcher = pattern.matcher(sql);
		List<String> paramList =new ArrayList<>();
		while(matcher.find()) {
			String content = matcher.group(1);
			paramList.add(content);
		}
		pattern = Pattern.compile("'\\{([^>]+?)}'");

		sql = sql.replaceAll(pattern.toString(),"?");
		List<Object> paramsObject = new ArrayList<>();

		for (String s : paramList) {
			if(null!=dataMap.get(s)){
				paramsObject.add(dataMap.get(s));
			}else{
				paramsObject.add("");
			}
		}
		log.info("请求参数"+ JSON.toJSONString(dataMap));
		log.info("执行SQL参数value"+ JSON.toJSONString(paramsObject)+"执行SQLKey"+ JSON.toJSONString(paramList)+"执行SQL"+sql);
//		return jdbcTemplate.queryForMap(sql,paramsObject.toArray());查询结果为空会报错，奇怪的方法
		List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql, paramsObject.toArray());
		if(null!=mapList && mapList.size()>0){
			return mapList.get(0);
		}
		return new HashMap();
	}

	/**
	 * 查询
	 * @param sqlId 查询SQL
	 * @param dataMap 参数列表
	 * @return
	 */
	public <T> T queryForObject(String sqlId, Map dataMap,Class<T> tClass) {
		BizSQLParser bizSQLParser = new BizSQLParser();
		SQLObject so = bizSQLParser.getSQLObject(sqlId);

		String sql = so.getSql().trim();

		Pattern pattern = Pattern.compile("\\{([^>]+?)}");

		Matcher matcher = pattern.matcher(sql);
		List<String> paramList =new ArrayList<>();
		while(matcher.find()) {
			String content = matcher.group(1);
			paramList.add(content);
		}
		pattern = Pattern.compile("'\\{([^>]+?)}'");

		sql = sql.replaceAll(pattern.toString(),"?");
		List<Object> paramsObject = new ArrayList<>();

		for (String s : paramList) {
			if(null!=dataMap.get(s)){
				paramsObject.add(dataMap.get(s));
			}else{
				paramsObject.add("");
			}
		}
		log.info("请求参数"+ JSON.toJSONString(dataMap));
		log.info("执行SQL参数value"+ JSON.toJSONString(paramsObject)+"执行SQLKey"+ JSON.toJSONString(paramList));
		return jdbcTemplate.queryForObject(sql,paramsObject.toArray(),tClass);
	}
}
