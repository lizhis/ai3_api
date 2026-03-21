package com.ai.servicebase.handler;

import com.ai.basecommon.core.vo.base.AgreementVO;
import com.ai.basecommon.enums.AgreementTypeEnum;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.AgreementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public class AgreementHandler {


    @Autowired
    private AgreementMapper agreementMapper;


    @ReadOnly
    public AgreementVO userAgreement() throws Exception{
        return agreementMapper.findByType(AgreementTypeEnum.USER_AGREEMENT.getCode());
    }

    @ReadOnly
    public AgreementVO privacyAgreement() throws Exception{
        return agreementMapper.findByType(AgreementTypeEnum.PRIVACY_AGREEMENT.getCode());
    }

    @ReadOnly
    public AgreementVO appPermissions() throws Exception{
        return agreementMapper.findByType(AgreementTypeEnum.APP_PERMISSIONS.getCode());
    }

    @ReadOnly
    public AgreementVO carAgreement() throws Exception{
        return agreementMapper.findByType(AgreementTypeEnum.CAR_AGREEMENT.getCode());
    }

    @ReadOnly
    public AgreementVO seasonAgreement() throws Exception{
        return agreementMapper.findByType(AgreementTypeEnum.SEASON_PLUS.getCode());
    }




}
