package cn.visolink.utils.flowpojo;

import lombok.Data;

/**
 *
 //    "id": "3591003", //附件 ID
 //            "name": "会议纪要-简版.docx", //附件名称
 //            "size": 54342, //附件大小（单位：Byte)
 //            "ext": "docx", //附件类型
 //            "number": 0, //附件排序号
 //            "previewUrl":"string", //附件预览地址
 //            "downloadUrl":"string" //附件下载地址
 */
@Data
public class FlowOpinionResFile {
    private String id;
    private String name;
    private Long size;
    private String ext;
    private int number;
    private String previewUrl;
    private String downloadUrl;

}
