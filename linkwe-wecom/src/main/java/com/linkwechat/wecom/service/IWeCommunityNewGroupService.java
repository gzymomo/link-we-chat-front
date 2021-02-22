package com.linkwechat.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkwechat.wecom.domain.WeCommunityNewGroup;
import com.linkwechat.wecom.domain.dto.WeCommunityNewGroupDto;
import com.linkwechat.wecom.domain.vo.WeCommunityNewGroupVo;

import java.util.List;

/**
 * 社群运营 新客自动拉群
 *
 * @author kewen
 * @date 2021-02-19
 */
public interface IWeCommunityNewGroupService extends IService<WeCommunityNewGroup> {

    /**
     * 添加新客自动拉群信息
     *
     * @param communityNewGroupDto 信息
     * @return 结果
     */
    int add(WeCommunityNewGroupDto communityNewGroupDto);

    /**
     * 查询新客自动拉群列表
     *
     * @param communityNewGroup 搜索信息
     * @return {@link WeCommunityNewGroup}s
     */
    List<WeCommunityNewGroupVo> selectWeCommunityNewGroupList(WeCommunityNewGroup communityNewGroup);

    /**
     * 获取新客自动拉群详细信息
     * @param newGroupId 主键id
     * @return {@link WeCommunityNewGroupVo} 自动拉群信息
     */
    WeCommunityNewGroupVo selectWeCommunityNewGroupById(Long newGroupId);

    /**
     * 修改新客自动拉群
     * @param communityNewGroupDto 信息
     * @return 结果
     */
    int updateWeCommunityNewGroup(WeCommunityNewGroupDto communityNewGroupDto);

    /**
     * 删除新客自动拉群
     *
     * @param idList id列表
     * @return
     */
   int batchRemoveWeCommunityNewGroupIds(List<String> idList);
}
