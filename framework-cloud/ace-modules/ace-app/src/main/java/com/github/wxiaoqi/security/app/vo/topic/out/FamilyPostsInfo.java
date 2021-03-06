package com.github.wxiaoqi.security.app.vo.topic.out;

import com.github.wxiaoqi.security.api.vo.img.ImgInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qs
 * @date 2019/8/9
 */
@Data
public class FamilyPostsInfo implements Serializable {


    @ApiModelProperty(value = "家里人帖子id")
    private String id;
    private String userId;
    @ApiModelProperty(value = "发帖人")
    private String userName;
    @ApiModelProperty(value = "发布时间")
    private String createTime;
    @ApiModelProperty(value = "发帖人头像")
    private String profilePhoto;
    @ApiModelProperty(value = "类型(1-图片,2-视频)")
    private String imageType;
    @ApiModelProperty(value = "帖子图片/视频")
    private String postImage;
    @ApiModelProperty(value = "视频图片")
    private String videoImage;

    @ApiModelProperty(value = "帖子内容")
    private String content;
    @ApiModelProperty(value = "点赞数")
    private int upNum;
    @ApiModelProperty(value = "评论数")
    private int commentNum;
    @ApiModelProperty(value = "是否点赞(0-未赞,1-已点赞)")
    private String isUp;
    @ApiModelProperty(value = "是否置顶(0-未置顶，1-已置顶)")
    private String isTop;
    @ApiModelProperty(value = "显示类型(0=隐藏，1=显示)")
    private String showType;
    @ApiModelProperty(value = "身份类型(0-不是游客,1-游客,2-运营人员)")
    private String identityType;


    @ApiModelProperty(value = "帖子图片集合")
    private List<ImgInfo> postImageList;

    public List<ImgInfo> getPostImageList() {
        List<ImgInfo> list = new ArrayList<>();
        if(StringUtils.isNotEmpty(postImage)){
            String[] imArrayIds = new String[]{postImage};
            if(postImage.indexOf(",")!= -1){
                imArrayIds = postImage.split(",");
            }
            for (String url:imArrayIds){
                ImgInfo info = new ImgInfo();
                info.setUrl(url);
                list.add(info);
            }
        }
        return list;
    }





}
