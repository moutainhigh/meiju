package cn.visolink.salesmanage.fileexport.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "表三对象")
public class ListThree implements Serializable {
  private String  guid;
    private String creator;
    private String  create_time;

    private String is_delete_flag;
    private String editor;
    private String update_time;

    private String months;
    private String  monthly_plan_id;
    private String version;
    private String monthly_plan_index_id;
    private String  compiler_account_id;
    private String  matter;
    private String  action;
    private Integer  subscription_number;
    private Double  subscription_amount;
    private Double  first_three_months_average_monthly_turnover;
    private Integer  come_client_quantity;
    private Double  first_three_months_monthly_average_coming_proportion;
    private Double  coming_proportion;
    private Double  first_three_months_monthly_average_turnover_rate;
    private Double  turnover_rate;
    private Double  contract_amount;
    private Double  contract_cost_rate;
    private Double  right_responsibility_amount;
    private Double  right_responsibility_cost_rate;
    private Double  first_three_months_average_monthly_transaction_cost;
    private Double  transaction_cost;
    private Double  first_three_months_monthly_average_coming_cost;
    private Double  coming_cost;
    private Integer  first_three_months_average_monthly_sets;
    private Integer  first_three_months_monthly_average_monthly_coming_amount;
    private String  project_id;
    private String  channel_id;
    private Integer  is_effective;
}
