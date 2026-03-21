package com.ai.servicebusiness.handler;

import com.ai.basecommon.core.param.pro.ProParam;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.pro.ProSkuVO;
import com.ai.basecommon.core.vo.pro.ProVO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.servicebusiness.commom.UserUtilX;
import com.ai.servicebusiness.config.db.ReadOnly;
import com.ai.servicebusiness.mapper.ProMapper;
import com.ai.servicebusiness.mapper.ProOrderMapper;
import com.ai.servicebusiness.mapper.ProSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class ProHandler {

    @Autowired
    private ProMapper proMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private ProOrderMapper proOrderMapper;

    @Autowired
    private ProSkuMapper proSkuMapper;

    @ReadOnly
    public BaseVO select(ProParam param)  throws Exception{
        if(null == param){
            param = new ProParam();
        }

        Long userId = userUtilX.getUserIdNotError();
        if(null == userId || userId.compareTo(1L) < 0){
            return BaseVO.ok(new ArrayList<>());
        }

        //查询这个用户是否完成新手任务
        boolean r = userUtilX.isNewbieStatusOK(userId);
        if(!r){
            return BaseVO.ok(new ArrayList<>());
        }

        UserPO userPO = userUtilX.getCacheUserPO(userId);
        if(null == userPO){
            return BaseVO.ok(new ArrayList<>());
        }
        param.setLevel(userPO.getLevel());

        List<ProVO> list = proMapper.select(param);
        if(null == list || list.isEmpty()){
            return BaseVO.ok(new ArrayList<>());
        }

        Iterator<ProVO> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProVO element = iterator.next();
            List<ProSkuVO> skuVOList = proSkuMapper.selectVOByProId(element.getId());
            if(null == skuVOList || skuVOList.isEmpty()){
                iterator.remove();
            }
            else{
                element.setSkuList(skuVOList);
            }
        }

        return BaseVO.ok(list);
    }


    @ReadOnly
    public BaseVO selectHome() throws Exception{

        Long userId = userUtilX.getUserIdNotError();
        if(null == userId || userId.compareTo(1L) < 0){
            return BaseVO.ok(new ArrayList<>());
        }

        boolean r = userUtilX.isNewbieStatusOK(userId);
        if(!r){
            return BaseVO.ok(new ArrayList<>());
        }

        UserPO userPO = userUtilX.getCacheUserPO(userId);
        if(null == userPO){
            return BaseVO.ok(new ArrayList<>());
        }

        List<ProVO> list = proMapper.selectHome(userPO.getLevel());
        if(null == list || list.isEmpty()){
            return BaseVO.ok(new ArrayList<>());
        }

        Iterator<ProVO> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProVO element = iterator.next();
            List<ProSkuVO> skuVOList = proSkuMapper.selectVOByProId(element.getId());
            if(null == skuVOList || skuVOList.isEmpty()){
                iterator.remove();
            }
            else{
                element.setSkuList(skuVOList);
            }
        }
        return BaseVO.ok(list);
    }


    @ReadOnly
    public ProVO detail(Long id) throws Exception{
        if(null == id){
            return null;
        }
        Long userId = userUtilX.getUserId();
        if(null == userId){
            return null;
        }
        UserPO userPO = userUtilX.getCacheUserPO(userId);
        if(null == userPO){
            return null;
        }
        if(!StatusEnum.YES.getCode().equals(userPO.getNewbieStatus())){
            LogUtil.log("该用户："+userId+" 没有完成新手任务 无法查看项目详情：" + id);
            return null;
        }

        ProVO vo = proMapper.findVOById(id);
        if(null == vo){
            return null;
        }

        if(vo.getLevel() > userPO.getLevel()){
            LogUtil.log("该用户："+userId+"等级不够 无法查看项目详情：" + id);
            return null;
        }

        vo.setBuyMaxNum(Integer.min(vo.getCompNum(),vo.getBuyMaxNum()));

        //限购逻辑
        if(null == vo.getBuyMaxNum() || 0 == vo.getBuyMaxNum()){
            vo.setBuyMaxNum(0);
            vo.setBuyMinNum(0);
        }
        if(vo.getBuyMaxNum() > 0){
            int n = proOrderMapper.countByUserIdAndProId(userId,vo.getId());
            if(vo.getBuyMaxNum() > n){
                vo.setBuyMinNum(1);
                vo.setBuyMaxNum(vo.getBuyMaxNum() - n);
            }
            else{
                vo.setBuyMinNum(0);
                vo.setBuyMaxNum(0);
            }
        }

        List<ProSkuVO> skuVOList = proSkuMapper.selectVOByProId(id);
        if(null == skuVOList){
            skuVOList = new ArrayList<>();
        }
        vo.setSkuList(skuVOList);

        return vo;
    }





}
