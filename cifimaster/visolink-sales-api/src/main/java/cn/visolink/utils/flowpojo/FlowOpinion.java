package cn.visolink.utils.flowpojo;

import lombok.Data;

import java.util.List;

@Data
public class FlowOpinion {

    private boolean state;

    private String message;

    private List<FlowOpinionRes> opinions;
}
