package com.zsh.teach_oa_danmu.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Danmu {

    private String text;//弹幕内容

    private String color;//字体的颜色

    private int size;//字体的大小

    private int position;//设置在视频的位置 0-滚动  1-顶部  2-底部

    private  int time;//出现在视频的哪个时间点


}
