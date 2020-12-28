package cn.visolink.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.*;

/**
 * 代码生成器
 *
 */
public class MabatisPlusGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
    /**
     *     文件路径
     */
    private static String path = System.getProperty("user.dir")+"/cifimaster";
    /**
     *     table名字
     */
    private static String table = scanner("表名");
    public static void main(String[] args) {
        //1. 全局配置
        GlobalConfig config = new GlobalConfig();
        // 是否支持AR模式
        config.setActiveRecord(false)
                // 作者
                .setAuthor("autoJob")
                // 使用Swagger
                .setSwagger2(true)
                // 生成路径
                .setOutputDir(path + "/visolink-sales-api/src/main/java/D:\\北京微聚万家项目\\9.9rightmisson\\cifimaster\\visolink-sales-api\\src\\main\\java/cn/visolink/system/projectmanager")
                // 文件覆盖
                .setFileOverride(true)
                // 主键策略
                .setIdType(IdType.AUTO)
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setServiceName("%sService").setServiceImplName("%sServiceImpl")
                .setControllerName("%sController").setMapperName("%sDao").setXmlName("%sMapper")
                // 生成文件后 不打开文件夹
                .setOpen(false)
                // XML ResultMap
                .setBaseResultMap(true)
                // XML columList
                .setBaseColumnList(true);

        //2. 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        // 设置数据库类型
        dsConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.jdbc.Driver")
                .setUrl("jdbc:mysql://10.129.37.52:3306/xuke?useUnicode=true&useSSL=false&characterEncoding=utf8")
                .setUsername("xuke").setPassword("XUke#1234").setTypeConvert(new MySqlTypeConvert() {
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                if (fieldType.toLowerCase().contains("tinyint")) {
                    return DbColumnType.BOOLEAN;
                }
                //将数据库中datetime转换成date
                if (fieldType.toLowerCase().contains("datetime")) {
                    return DbColumnType.DATE;
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }
        });

        //3. 策略配置
        StrategyConfig stConfig = new StrategyConfig();
        stConfig.setColumnNaming(NamingStrategy.underline_to_camel)
                // 数据库表映射到实体的命名策略
                .setNaming(NamingStrategy.underline_to_camel)
                // 使用Lombok
                .setEntityLombokModel(true)
                // 数据库版本控制字段
                .setVersionFieldName("version")
                // 数据库逻辑删除字段
                .setLogicDeleteFieldName("status")
                .setTablePrefix(scanner("表前缀") + "_")

                .setRestControllerStyle(true)
//                .setSuperServiceClass("com.baomidou.mybatisplus.extension.service.IService")
                // 生成的表
                .setInclude(new String[]{table});

        //4. 包名策略配置
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("com.mybatis.plus.demo").setMapper("dao").setService("service").setModuleName(scanner("模块名"))
                .setServiceImpl("service.impl").setController("controller").setEntity("model");
        //5.自定义配置
        InjectionConfig cfg = new InjectionConfig() {
                //.vm模板中，通过${cfg.abc}获取属性
                @Override
                public void initMap() {
                    Map<String, Object> map = new HashMap<>();
//                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                    map.put("voPackage", "com.mybatis.plus.demo."+pkConfig.getModuleName()+".model");
                    this.setMap(map);
                }
        };
        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        //自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // mapper自定义输出文件名
                return path + "/visolink-sales-api/src/main/resources/mapper/"+pkConfig.getModuleName()+"/" + tableInfo.getEntityName() + "Mapper"
                        + StringPool.DOT_XML;
            }
        });

        templatePath = "/templates/entityVO.java.vm";
        //自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // VO自定义输出文件名
                return path + "/visolink-sales-api/src/main/java/com/mybatis/plus/demo/"+pkConfig.getModuleName()+"/model/vo/" + tableInfo.getEntityName() + "VO"
                        + StringPool.DOT_JAVA;
            }
        });

        templatePath = "/templates/form.java.vm";
        //自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // VO自定义输出文件名
                return path + "/visolink-sales-api/src/main/java/com/mybatis/plus/demo/"+pkConfig.getModuleName()+"/model/form/" + tableInfo.getEntityName() + "Form"
                        + StringPool.DOT_JAVA;
            }
        });

        // ------------form vo 使用默认模板请注释-end-----------
        cfg.setFileOutConfigList(focList);

        // 6 配置模板 自定义模板/在resources/templates 可以编辑
        TemplateConfig templateConfig = new TemplateConfig();
        // 关闭默认 xml 生成，调整生成 至 根目录
        templateConfig.setEntity("/templates/entity.java").setService("/templates/service.java")
                .setController("/templates/controller.java").setMapper("/templates/mapper.java")
                .setServiceImpl("/templates/serviceImpl.java").setXml(null);

        //7. 整合配置
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config).setDataSource(dsConfig).setStrategy(stConfig).setCfg(cfg)
                .setPackageInfo(pkConfig).setTemplate(templateConfig);

        //8. 执行
        ag.execute();
    }
}