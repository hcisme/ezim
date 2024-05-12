package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.model.UserBeauty;
import org.chc.ezim.entity.dto.UserBeautyDto;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserBeautyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 靓号表 Controller
 */
@RestController("userBeautyController")
@RequestMapping("/userBeauty")
public class UserBeautyController extends ABaseController {

    @Resource
    private UserBeautyService userBeautyService;

    /**
     * 根据条件分页查询
     */
    @GetMapping("/loadDataList")
    public ResponseVO loadDataList(UserBeautyDto query) {
        return getSuccessResponseVO(userBeautyService.findListByPage(query));
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public ResponseVO add(@RequestBody UserBeauty bean) {
        userBeautyService.add(bean);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量新增
     */
    @PostMapping("/addBatch")
    public ResponseVO addBatch(@RequestBody List<UserBeauty> listBean) {
        userBeautyService.addBatch(listBean);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量新增/修改
     */
    @PutMapping("/addOrUpdateBatch")
    public ResponseVO addOrUpdateBatch(@RequestBody List<UserBeauty> listBean) {
        userBeautyService.addBatch(listBean);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Id查询对象
     */
    @GetMapping("/getUserBeautyById")
    public ResponseVO getUserBeautyById(Integer id) {
        return getSuccessResponseVO(userBeautyService.getUserBeautyById(id));
    }

    /**
     * 根据Id修改对象
     */
    @PutMapping("/updateUserBeautyById")
    public ResponseVO updateUserBeautyById(@RequestBody UserBeauty bean, Integer id) {
        userBeautyService.updateUserBeautyById(bean, id);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Id删除
     */
    @DeleteMapping("/deleteUserBeautyById")
    public ResponseVO deleteUserBeautyById(Integer id) {
        userBeautyService.deleteUserBeautyById(id);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据UserId查询对象
     */
    @GetMapping("/getUserBeautyByUserId")
    public ResponseVO getUserBeautyByUserId(String userId) {
        return getSuccessResponseVO(userBeautyService.getUserBeautyByUserId(userId));
    }

    /**
     * 根据UserId修改对象
     */
    @PutMapping("/updateUserBeautyByUserId")
    public ResponseVO updateUserBeautyByUserId(@RequestBody UserBeauty bean, String userId) {
        userBeautyService.updateUserBeautyByUserId(bean, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据UserId删除
     */
    @DeleteMapping("/deleteUserBeautyByUserId")
    public ResponseVO deleteUserBeautyByUserId(String userId) {
        userBeautyService.deleteUserBeautyByUserId(userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Email查询对象
     */
    @GetMapping("/getUserBeautyByEmail")
    public ResponseVO getUserBeautyByEmail(String email) {
        return getSuccessResponseVO(userBeautyService.getUserBeautyByEmail(email));
    }

    /**
     * 根据Email修改对象
     */
    @PutMapping("/updateUserBeautyByEmail")
    public ResponseVO updateUserBeautyByEmail(@RequestBody UserBeauty bean, String email) {
        userBeautyService.updateUserBeautyByEmail(bean, email);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Email删除
     */
    @DeleteMapping("/deleteUserBeautyByEmail")
    public ResponseVO deleteUserBeautyByEmail(String email) {
        userBeautyService.deleteUserBeautyByEmail(email);
        return getSuccessResponseVO(null);
    }
}