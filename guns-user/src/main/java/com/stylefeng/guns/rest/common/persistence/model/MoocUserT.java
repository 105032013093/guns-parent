package com.stylefeng.guns.rest.common.persistence.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author dengsijie
 * @since 2019-12-14
 */
@Data
@TableName("mooc_user_t")
public class MoocUserT extends Model<MoocUserT> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键编号
     */
    @TableId(value = "UUID", type = IdType.AUTO)
    private Integer uuid;
    /**
     * 用户账号
     */
    @TableField("user_name")
    private String userName;
    /**
     * 用户密码
     */
    @TableField("user_pwd")
    private String userPwd;
    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;
    /**
     * 用户性别 0-男，1-女
     */
    @TableField("user_sex")
    private Integer userSex;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    @TableField("user_phone")
    private String userPhone;
    /**
     * 用户住址
     */
    private String address;
    /**
     * 头像URL
     */
    @TableField("head_url")
    private String headUrl;
    /**
     * 个人介绍
     */
    private String biography;
    /**
     * 生活状态 0-单身，1-热恋中，2-已婚，3-为人父母
     */
    @TableField("life_state")
    private Integer lifeState;
    /**
     * 创建时间
     */
    @TableField("begin_time")
    private Date beginTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }


}
