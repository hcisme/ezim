package org.chc.ezim.service;

import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.dto.UserContactDto;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.UserContactSearchResultVO;

import java.util.List;


/**
 * 联系人 业务接口
 */
public interface UserContactService {

    /**
     * 根据条件查询列表
     */
    List<UserContact> findListByParam(UserContactDto param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(UserContactDto param);

    /**
     * 分页查询
     */
    PaginationResultVO<UserContact> findListByPage(UserContactDto param);

    /**
     * 新增
     */
    Integer add(UserContact bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<UserContact> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<UserContact> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(UserContact bean, UserContactDto param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(UserContactDto param);

    /**
     * 根据UserIdAndContactId查询对象
     */
    UserContact getUserContactByUserIdAndContactId(String userId, String contactId);


    /**
     * 根据UserIdAndContactId修改
     */
    Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId);


    /**
     * 根据UserIdAndContactId删除
     */
    Integer deleteUserContactByUserIdAndContactId(String userId, String contactId);

    /**
     * 搜索联系人
     */
    UserContactSearchResultVO searchContact(String userId, String contactId);

    /**
     * 删除联系人
     */
    void removeContactUser(String userId, String contactId, UserContactStatusEnum statusEnum);

    /**
     * 添加好友
     */
    void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo);

    /**
     * 用户注册时 添加机器人为好友
     */
    void addContact4Robot(String userId);
}