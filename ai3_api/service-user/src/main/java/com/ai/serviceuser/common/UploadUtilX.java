package com.ai.serviceuser.common;


import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.SysConfUploadPO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.SysConfUploadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Description
 * @Author 
 */
@Component
public class UploadUtilX {

    @Autowired
    private SysConfUploadMapper sysConfUploadMapper;

    @Autowired
    private RedisUtilX redisUtilX;


    /**
     * 上传文件
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public String file(MultipartFile multipartFile) throws Exception{
        if(null == multipartFile){
            BaseException.error(StatusCodeEnum.SYSTEM_ERROR);
        }
        SysConfUploadPO confPO = this.getConf();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.ERROR);
        }
        this.verify(multipartFile,confPO);


        File file = transferToFile(multipartFile);

        HashMap<String,String> map = new HashMap<>();
        map.put("bucket",confPO.getBucket());

        String result = HttpUtil.upload(confPO.getUrl(),map,file);
        if(StringUtil.isEmpty(result)){
            BaseException.error("文件服务器异常");
        }
        return confPO.getDomain() + "/" + result;
    }



    /**
     * 上传文件
     * @param filePath
     * @return
     * @throws Exception
     */
    public String file(String filePath) throws Exception {
        if(StringUtil.isEmpty(filePath)){
            BaseException.error(StatusCodeEnum.SYSTEM_ERROR);
        }
        SysConfUploadPO confPO = this.getConf();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.ERROR);
        }
        File file = new File(filePath);
        this.verify(file,confPO);

        HashMap<String,String> map = new HashMap<>();
        map.put("bucket",confPO.getBucket());
        String result = HttpUtil.upload(confPO.getUrl(),map,file);
        if(StringUtil.isEmpty(result)){
            BaseException.error("文件服务器异常");
        }
        return confPO.getDomain() + "/" + result;
    }






    private File transferToFile(MultipartFile multipartFile) {
        //选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取文件后缀
            String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));
            file = File.createTempFile(originalFilename, prefix);    //创建临时文件
            multipartFile.transferTo(file);
            //删除
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 获取文件后缀名 不带.
     * @param fileName
     * @return
     * @throws Exception
     */
    private String getSuffix(String fileName) throws Exception{
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 文件校验
     * @param file
     * @throws Exception
     */
    private void verify(File file,SysConfUploadPO confUploadPO) throws Exception{
        if(null == file){
            BaseException.error(StatusCodeEnum.SYSTEM_ERROR);
        }
        if(!file.isFile()){
            BaseException.error(StatusCodeEnum.OSS_FILE_NO_EXIST);
        }
        if(null == confUploadPO || StringUtil.isEmpty(confUploadPO.getSuffix())){
            BaseException.error(StatusCodeEnum.OSS_FILE_SUFFIX_ERROR);
        }
        String name = file.getName();
        String extension = this.getSuffix(name);
        boolean s = Arrays.asList(confUploadPO.getSuffix().toLowerCase().split(",")).contains(extension);
        if(!s){
            BaseException.error(StatusCodeEnum.OSS_FILE_SUFFIX_ERROR);
        }

        Long fileSize = file.length();
        if(fileSize.compareTo((long) (confUploadPO.getSize() * 1048576)) > 0){
            BaseException.error(StatusCodeEnum.OSS_FILE_SIZE_ERROR);
        }
    }


    /**
     * 文件校验
     * @param file
     * @throws Exception
     */
    private void verify(MultipartFile file,SysConfUploadPO confUploadPO) throws Exception{

        String filename = file.getOriginalFilename();
        String extension = this.getSuffix(filename);

        if(null == confUploadPO || StringUtil.isEmpty(confUploadPO.getSuffix())){
            BaseException.error(StatusCodeEnum.OSS_FILE_SUFFIX_ERROR);
        }

        boolean s = Arrays.asList(confUploadPO.getSuffix().toLowerCase().split(",")).contains(extension);
        if(!s){
            BaseException.error(StatusCodeEnum.OSS_FILE_SUFFIX_ERROR);
        }

        Long fileSize = file.getSize();
        if(fileSize.compareTo((long) (confUploadPO.getSize() * 1048576)) > 0){
            BaseException.error(StatusCodeEnum.OSS_FILE_SIZE_ERROR);
        }
    }


    private SysConfUploadPO getConf() throws Exception {
        SysConfUploadPO confPO = redisUtilX.getObj(RedisKey.conf_upload,SysConfUploadPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfUploadMapper.find();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.OSS_CONF_NO);
        }
        redisUtilX.setObj(RedisKey.conf_upload,confPO,600);
        return confPO;
    }





}
