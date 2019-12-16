package com.stylefeng.guns.rest.common.persistence.model;

import com.baomidou.mybatisplus.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2017-08-23
 */
@Data
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

	private Long id;
	private String userName;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}


}
