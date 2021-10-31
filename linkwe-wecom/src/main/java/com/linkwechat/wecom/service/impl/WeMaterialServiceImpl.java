package com.linkwechat.wecom.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.http.ForestResponse;
import com.linkwechat.common.config.CosConfig;
import com.linkwechat.common.config.RuoYiConfig;
import com.linkwechat.common.config.ServerConfig;
import com.linkwechat.common.constant.WeConstans;
import com.linkwechat.common.enums.WeErrorCodeEnum;
import com.linkwechat.common.exception.wecom.WeComException;
import com.linkwechat.common.utils.SnowFlakeUtil;
import com.linkwechat.common.utils.StringUtils;
import com.linkwechat.common.utils.file.FileUploadUtils;
import com.linkwechat.common.utils.file.FileUtils;
import com.linkwechat.wecom.client.WeMediaClient;
import com.linkwechat.wecom.domain.WeMaterial;
import com.linkwechat.wecom.domain.dto.WeMediaDto;
import com.linkwechat.wecom.domain.dto.WeResultDto;
import com.linkwechat.wecom.domain.vo.WeMaterialFileVO;
import com.linkwechat.wecom.mapper.WeMaterialMapper;
import com.linkwechat.wecom.service.IWeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 素材service
 *
 * @author KEWEN
 * @date 2020-10-08
 */
@Slf4j
@Service
public class WeMaterialServiceImpl implements IWeMaterialService {

    @Autowired
    private WeMaterialMapper weMaterialMapper;

    @Autowired
    private WeMediaClient weMediaClient;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public WeMaterialFileVO uploadWeMaterialFile(MultipartFile file, String type) {
        if (null == file) {
            throw new WeComException("文件为空！");
        }
        try {


//            /**
//             * 当前上传腾讯云
//             */
//            String s = FileUploadUtils.upload2Cos(file, cosConfig);
//            //上传文件到文件服务器
//            // 上传文件路径
//            String filePath = RuoYiConfig.getUploadPath();
//            String fileName = "";
//            if (com.linkwechat.common.enums.MediaType .FILE.getType().equals(type)) {
//                fileName = FileUploadUtils.uploadFile(file);
//            } else {
//                // 上传并返回新文件名称
//                fileName = FileUploadUtils.upload(filePath, file);
//            }
//            String url =  fileName;
//            //上传临时素材
            Optional<com.linkwechat.common.enums.MediaType > mediaType = com.linkwechat.common.enums.MediaType .of(type);
            if (!mediaType.isPresent()) {
                throw new WeComException("媒体类型出错！");
            }
            //构造返回结果
//            return WeMaterialFileVO.builder().materialUrl(url).materialName(file.getOriginalFilename()).build();
            return WeMaterialFileVO.builder().materialUrl(ruoYiConfig.getFile().getCos().getCosImgUrlPrefix()).materialName(FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos())).build();

//            return WeMaterialFileVO.builder().materialUrl(ruoYiConfig.getFile().getCos().getCosImgUrlPrefix()+"/common/findImage?fileName=/").materialName(FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos())).build();
        } catch (Exception e) {
            throw new WeComException(e.getMessage());
        }

    }

    @Override
    public int insertWeMaterial(WeMaterial weMaterial) {
        weMaterial.setCreateTime(DateUtil.date());
        weMaterial.setId(SnowFlakeUtil.nextId());
        return weMaterialMapper.insertWeMaterial(weMaterial);
    }

    @Override
    public int deleteWeMaterialById(Long id) {
        return weMaterialMapper.deleteWeMaterialById(id);
    }

    @Override
    public void deleteWeMaterialByIds(Long[] ids) {
         weMaterialMapper.deleteWeMaterialByIds(ids);
    }

    @Override
    public int updateWeMaterial(WeMaterial weMaterial) {
        weMaterial.setCreateTime(DateUtil.date());
        return weMaterialMapper.updateWeMaterial(weMaterial);
    }

    @Override
    public WeMaterial findWeMaterialById(Long id) {
        return weMaterialMapper.findWeMaterialById(id);
    }

    @Override
    public List<WeMaterial> findWeMaterials(String categoryId, String search, String mediaType) {
        return weMaterialMapper.findWeMaterials(categoryId, search, mediaType);
    }


    @Override
    public void resetCategory(String categoryId, String materials) {
        List<String> materialList = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(materials, WeConstans.COMMA));
        if (CollectionUtil.isNotEmpty(materialList)) {
            materialList.forEach(s -> {
                weMaterialMapper.resetCategory(categoryId, s);
            });
        }
    }

    @Override
    public WeMediaDto uploadTemporaryMaterial(String url, String type, String name) {
        HttpURLConnection conn = null;
        try{
            URL materialUrl = new URL(url);
            conn = (HttpURLConnection) materialUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            InputStream inputStream = conn.getInputStream();
            return weMediaClient.upload(inputStream, name, type);
        }catch (Exception e){
            e.printStackTrace();
            log.error("上传临时文件失败......url:{},type:{},name:{},ex:{},st:{}",url,type,name,e.getMessage(),e.getStackTrace());
        }finally {
            if (conn !=null){
                conn = null;
            }
        }
        return null;
    }

    @Override
    public WeMediaDto uploadImg(MultipartFile file) {
        return weMediaClient.uploadimg(file);
    }

    @Override
    public WeMediaDto getMediaToResponse(String mediaId) {
        WeMediaDto weMediaDto = new WeMediaDto();
        weMediaDto.setErrcode(0);
        ForestResponse forestResponse = weMediaClient.getMediaToResponse(mediaId);
        if (forestResponse != null) {
            try {
                String fixedName =new String(forestResponse.getFilename().getBytes("ISO8859-1"),"UTF-8");
                fixedName = fixedName.replace("\"", "");
                weMediaDto.setFileName(fixedName);
                if(ruoYiConfig.getFile().isStartCosUpload()) {
                    String fileFullName = FileUploadUtils.upload2Cos(forestResponse.getInputStream(), FileUtil.getSuffix(fixedName), ruoYiConfig.getFile().getCos());
                    String imgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
                    weMediaDto.setUrl(imgUrlPrefix + fileFullName);
                }else {
                    String filePath = FileUploadUtils.upload(forestResponse.getInputStream(), fixedName);
                    weMediaDto.setUrl(filePath);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                weMediaDto.setErrcode(-1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new WeComException("文件下载异常");
            }
        }
        return weMediaDto;
    }

}
