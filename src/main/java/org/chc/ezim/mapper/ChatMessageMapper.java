package org.chc.ezim.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 聊天消息表 数据库操作接口
 */
public interface ChatMessageMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据Id更新
	 */
	 Integer updateById(@org.springframework.data.repository.query.Param("bean") T t, @Param("id") Long id);


	/**
	 * 根据Id删除
	 */
	 Integer deleteById(@Param("id") Long id);


	/**
	 * 根据Id获取对象
	 */
	 T selectById(@Param("id") Long id);


}
