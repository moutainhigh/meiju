package cn.visolink.salesmanage.fileexport.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "表四月计划部分对象")
public class MonthFour {

    private String  guid;
    private String creator;
    private String create_time;
    private Integer is_delete_flag;
    private String editor;
    private String update_time;

    private String months;
    private String monthly_plan_id;
    private String version;
    private String monthly_plan_index_id;
    private String compiler_account_id;
    private String big_card;
    private Integer come_client_quantity;
    private Double small_card;
    private Double come_client_cost;
    private Double cost;
    private Double turnover_rate;
    private Double rate;
    private Double transaction_cost;
    private Integer subscription_number;
    private String subscription_funds;
    private Double sign_number_set;
    private Double sign_funds;
    private String risk_point;
    private String countermeasures;
    private String policy_use;
    private String core_action;
    private String project_id;
    private String channel_id;
    private Double is_effective;
    private Double marketing_promotion_cost;
    private Double total_sign_funds
;   private Integer total_sign_set
;  private Integer plan_subscription_set
;  private Double plan_subscription_funds;
    private  Integer  week_serial_number;

    private  Integer  id;
    private  String   howWeek;
    private  Double  sign_target;
    private  Integer  visit_quantity;
    private  Integer  subscription_number_set;
    private  String    start_time;
    private  String   end_time;
    private  Integer  day_num;
}
