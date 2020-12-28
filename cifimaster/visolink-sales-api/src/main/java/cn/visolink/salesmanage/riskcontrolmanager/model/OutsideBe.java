package cn.visolink.salesmanage.riskcontrolmanager.model;

import lombok.Data;

@Data
public class OutsideBe {


    private String area_id;     // 区域ID
    private String area_name;     // 区域名
    private String project_id;     // 项目ID

    private String project_code;     // 项目CODE
    private String trader;     // 操盘手
    private String project_name;     // 项目名
    private Integer channel_volume_total;     // 渠道成交量合计

    private Double channel_volume_per;     // 渠道成交量占比

    private Integer deal_total;     //  成交量合计
    private Integer nocard;     // 未刷证

    private String belonging_time;     // 业务归属时间
    private String channel;     // 渠道
    private Double agency_volume_per;     // 中介成交占比
    private Double agochannel_volume_per;     // 自渠成交占比
    private Double own_volume_per;     // 私营媒介成交占比
    private String opening_time;     //  启用时间


    private Integer brush_card_total;    // 刷证合计
    private Integer risk_risk_total;    // 风险合计

    private Double risk_rate;    // 系统提示风险占比
    private Double risk_check_rate;    // 系统提示风险人工复核为正常占比
    private Double risk_nocheck_rate;    // 人工未复核占比


    private Double unknown_brush_rate;        // 系统提示未知占比

    private Double channel_nocard_rate;    // 渠道未刷证占比

    private Double deal_nocard_rate;    // 成交未刷证占比

    private Integer risk_check_normal;    // 系统提示风险正常
    private Integer risk_check_fly_alone;    // 系统提示风险飞单
    private Integer risk_nocheck;     // 系统提示风险未复核
    private Integer normal_normal_total;     // 正常正常合计

    private Integer normal_check_normal;    // 系统提示正常的正常
    private Integer normal_check_fly_alone;    // 系统提示正常飞单
    private Integer normal_nocheck;    // 系统提示正常未复核
    private Integer brush_card_nosnap;     // 已刷证无抓拍


    private Integer unknown_total;     //  系统提示未知合计
    private Integer no_report_time;      //  未导入报备时间
    private Integer no_frist_snap;     //  无首次抓拍时间
    private Integer no_frist_danger;     //  无首次抓拍时间 复合为风险
    private Integer no_frist_nature;     //  无首次抓拍时间 复合为正常
    private Integer no_frist_unkown;     //  无首次抓拍时间 未知
    private Integer channel_nocard_total;     //  渠道未刷证合计
    private Integer deal_nocard_total;    //  成交未刷证合计
}
