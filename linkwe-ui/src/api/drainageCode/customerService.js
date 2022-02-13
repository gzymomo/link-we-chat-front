import request from '@/utils/request'
const service = window.CONFIG.services.kf
// 客服列表
export function getList (params) {
  return request({
    url: service + '/wecom/kf/list',
    params
  })
}

// 新增客服
export function addService (data) {
  return request({
    url: service + '/wecom/kf/add',
    method: 'post',
    data: data
  })
}

// 客服编辑
export function editService (data) {
  return request({
    url: service + '/wecom/kf/edit/account',
    method: 'post',
    data: data
  })
}

// 删除客服
export function remove (id) {
  return request({
    url: service + '/wecom/kf/delete/' + id,
    method: 'delete'
  })
}

// 客服详情
export function getDetail (id) {
  return request({
    url: service + '/wecom/kf/get/' + id
  })
}

// 批量下载场景二维码
export function downloadBatch (ids) {
  return request({
    url: service + "/wecom/kf/scenes/batch/download",
    params: {
      ids: ids
    },
    responseType: 'blob'
  })
}

// 场景列表
export function getSceneList (params) {
  return request({
    url: service + '/wecom/kf/scenes/list',
    params
  })
}

// 新增场景
export function addScene (data) {
  return request({
    url: service + '/wecom/kf/scenes/add',
    data: data,
    method: 'post'
  })
}

// 编辑场景
export function editScene (data) {
  return request({
    url: service + '/wecom/kf/scenes/edit',
    data: data,
    method: 'put'
  })
}

// 删除场景
export function deleteScene (ids) {
  console.log(33333)
  return request({
    url: service + '/wecom/kf/scenes/delete/' + ids,
    method: 'delete'
  })
}

// 咨询记录列表
export function getResultList (params) {
  return request({
    url: service + '/wecom/kf/record/list',
    params
  })
}

// 咨询记录导出
export function exportResult (params) {
  return request({
    url: service + '/wecom/kf/record/export',
    params: params
  })
}