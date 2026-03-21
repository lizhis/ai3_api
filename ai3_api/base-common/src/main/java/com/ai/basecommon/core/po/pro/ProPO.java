package com.ai.basecommon.core.po.pro;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProPO {

  private Long id;
  private String title;
  private String image;
  private String detailImage;
  private Integer extraNumMin;
  private Integer extraNumMax;
  private String priceStr;
  private BigDecimal feeRate;
  private String remark;
  private String content;
  private Integer buyMaxNum;
  private Integer level;
  private Integer cateType;
  private Integer compTotal;
  private Integer compNum;
  private Integer isOpenAutoSub;
  private Integer autoSubMin;
  private Integer autoSubMax;
  private Integer autoSubStopNum;
  private Integer isShowHome;
  private Integer giveGoodsType;
  private Long giveShop;
  private String giveShopDesc;
  private BigDecimal giveAmount;
  private String giveDesc;
  private Integer giveQuotaAmount;
  private Integer giveQuotaIsActive;
  private Integer checkType;
  private Integer isAutoNext;
  private Integer status;
  private Integer isDel;
  private Integer sort;
  private Integer isTimerUp;
  private Long timerUpTime;
  private Long createTime;
  private Long updateTime;
  private Long releaseTime;

}
