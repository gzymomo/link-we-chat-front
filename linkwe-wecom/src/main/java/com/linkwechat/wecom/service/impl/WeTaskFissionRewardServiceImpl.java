package com.linkwechat.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkwechat.common.exception.wecom.WeComException;
import com.linkwechat.common.utils.DateUtils;
import com.linkwechat.wecom.domain.WeCustomer;
import com.linkwechat.wecom.domain.WeTaskFission;
import com.linkwechat.wecom.domain.WeTaskFissionRecord;
import com.linkwechat.wecom.domain.WeTaskFissionReward;
import com.linkwechat.wecom.domain.vo.WeTaskFissionRewardVo;
import com.linkwechat.wecom.mapper.WeTaskFissionRewardMapper;
import com.linkwechat.wecom.service.IWeCustomerService;
import com.linkwechat.wecom.service.IWeTaskFissionRecordService;
import com.linkwechat.wecom.service.IWeTaskFissionRewardService;
import com.linkwechat.wecom.service.IWeTaskFissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 任务裂变奖励Service业务层处理
 *
 * @author leejoker
 * @date 2021-01-20
 */
@Service
public class WeTaskFissionRewardServiceImpl implements IWeTaskFissionRewardService {
    @Autowired
    private WeTaskFissionRewardMapper weTaskFissionRewardMapper;
    @Autowired
    private IWeCustomerService weCustomerService;
    @Autowired
    private IWeTaskFissionRecordService weTaskFissionRecordService;
    @Autowired
    private IWeTaskFissionService weTaskFissionService;

    /**
     * 查询任务裂变奖励
     *
     * @param id 任务裂变奖励ID
     * @return 任务裂变奖励
     */
    @Override
    public WeTaskFissionReward selectWeTaskFissionRewardById(Long id) {
        return weTaskFissionRewardMapper.selectWeTaskFissionRewardById(id);
    }

    /**
     * 查询任务裂变奖励列表
     *
     * @param weTaskFissionReward 任务裂变奖励
     * @return 任务裂变奖励
     */
    @Override
    public List<WeTaskFissionReward> selectWeTaskFissionRewardList(WeTaskFissionReward weTaskFissionReward) {
        return weTaskFissionRewardMapper.selectWeTaskFissionRewardList(weTaskFissionReward);
    }

    /**
     * 新增任务裂变奖励
     *
     * @param weTaskFissionReward 任务裂变奖励
     * @return 结果
     */
    @Override
    public int insertWeTaskFissionReward(WeTaskFissionReward weTaskFissionReward) {
        weTaskFissionReward.setCreateTime(DateUtils.getNowDate());
        return weTaskFissionRewardMapper.insertWeTaskFissionReward(weTaskFissionReward);
    }

    /**
     * 修改任务裂变奖励
     *
     * @param weTaskFissionReward 任务裂变奖励
     * @return 结果
     */
    @Override
    public int updateWeTaskFissionReward(WeTaskFissionReward weTaskFissionReward) {
        return weTaskFissionRewardMapper.updateWeTaskFissionReward(weTaskFissionReward);
    }

    /**
     * 批量删除任务裂变奖励
     *
     * @param ids 需要删除的任务裂变奖励ID
     * @return 结果
     */
    @Override
    public int deleteWeTaskFissionRewardByIds(Long[] ids) {
        return weTaskFissionRewardMapper.deleteWeTaskFissionRewardByIds(ids);
    }

    /**
     * 删除任务裂变奖励信息
     *
     * @param id 任务裂变奖励ID
     * @return 结果
     */
    @Override
    public int deleteWeTaskFissionRewardById(Long id) {
        return weTaskFissionRewardMapper.deleteWeTaskFissionRewardById(id);
    }

    @Override
    public WeTaskFissionRewardVo getRewardByFissionId(String fissionId, String eid) {
        WeTaskFissionRewardVo weTaskFissionRewardVo = new WeTaskFissionRewardVo();
        WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getExternalUserid, eid));
        String externalUseriId = Optional.ofNullable(weCustomer).map(WeCustomer::getExternalUserid)
                .orElseThrow(() -> new WeComException("用户信息不存在"));

        WeTaskFissionRecord record = weTaskFissionRecordService.selectWeTaskFissionRecordByIdAndCustomerId(Long.valueOf(fissionId), externalUseriId);
        Date completeTime = Optional.ofNullable(record).map(WeTaskFissionRecord::getCompleteTime)
                .orElseThrow(() -> new WeComException("任务信息不存在"));

        WeTaskFission weTaskFission = weTaskFissionService.selectWeTaskFissionById(Long.valueOf(fissionId));
        Optional.ofNullable(weTaskFission).ifPresent(fission ->{
            weTaskFissionRewardVo.setRewardRule(fission.getRewardRule());
            weTaskFissionRewardVo.setRewardImageUrl(fission.getRewardImageUrl());
            weTaskFissionRewardVo.setRewardUrl(fission.getRewardUrl());
        });

        if(completeTime != null){
            WeTaskFissionReward fissionReward = weTaskFissionRewardMapper.selectOne(new LambdaQueryWrapper<WeTaskFissionReward>()
                    .eq(WeTaskFissionReward::getTaskFissionId, fissionId)
                    .eq(WeTaskFissionReward::getRewardUserId, externalUseriId));
            weTaskFissionRewardVo.setWeTaskFissionReward(fissionReward);

        }

        return weTaskFissionRewardVo;
    }
}
