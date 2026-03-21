package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProVO {

  private Long id;
  private String title;
  private String image;
  private String detailImage;
  private Integer extraNumMin;
  private Integer extraNumMax;
  private BigDecimal feeRate;
  private String remark;
  private String content;
  private Integer level;
  private Integer cateType;
  private Integer isAutoNext;
  private Integer buyMaxNum;
  private Integer buyMinNum = 1;
  private Integer compTotal;
  private Integer compNum;
  private Integer giveGoodsType;
  private Long giveShop;
  private String giveShopDesc;
  private BigDecimal giveAmount;
  private String giveDesc;
  private Integer giveQuotaAmount;
  private Integer giveQuotaIsActive;
  private Integer checkType;
  private List<ProSkuVO> skuList;

}
