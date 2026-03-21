package com.ai.basead.mapstruct;

import com.ai.basecommon.core.dto.ad.AdKwaiDTO;
import com.ai.basecommon.core.dto.ad.AdOceanengineDTO;
import com.ai.basecommon.core.dto.ad.AdTencentDTO;
import com.ai.basecommon.core.param.ad.AdKwaiParam;
import com.ai.basecommon.core.param.ad.AdOceanengineParam;
import com.ai.basecommon.core.param.ad.AdTencentParam;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MapMapper {



    AdOceanengineDTO adOceanengineParamToDTO(AdOceanengineParam adOceanengineParam);


    AdTencentDTO adTencentParamToDTO(AdTencentParam adTencentParam);

    AdKwaiDTO adKwaiParamToDTO(AdKwaiParam adKwaiParam);


}
