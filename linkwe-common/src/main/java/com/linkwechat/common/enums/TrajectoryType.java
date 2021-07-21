package com.linkwechat.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * 客户轨迹枚举
 */
@Getter
public enum TrajectoryType {


    TRAJECTORY_TYPE_XXDT(1,"信息动态"),
    TRAJECTORY_TYPE_SJDT(2,"社交动态"),

    TRAJECTORY_TYPE_HDGZ(3,"活动规则"),

    TRAJECTORY_TYPE_DBDT(4,"待办动态");

    private String name;

    private Integer type;


    TrajectoryType( Integer type,String name) {
        this.name = name;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
