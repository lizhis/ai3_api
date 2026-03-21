package com.ai.basecommon.constants;

/**
 * @Description
 * @Author  
 *
 */
public class RedisKey {

    //全局配置
    public final static String sys_config = "sys_config";
    public final static String app_version_last_ = "app_version_last_"; //最新app版本号+平台

    //第三方接口
    public final static String conf_api = "conf_api";

    //提现配置
    public final static String conf_withdraw = "conf_withdraw";

    //设备号-密钥
    public final static String device_id_to_secret_ = "device_id_to_secret_";


    //设备是否在线
    public final static String device_is_online_ = "device_is_online_";

    //上传配置
    public final static String conf_upload = "conf_upload";

    //短信配置
    public final static String conf_sms = "conf_sms";

    //充值 支付宝配置
    public final static String conf_recharge_alipay = "conf_recharge_alipay";

    //充值 财源配置
    public final static String conf_caiyuan = "conf_caiyuan";

    //充值 长卿配置
    public final static String conf_changqing = "conf_changqing";

    //充值 智汇配置
    public final static String conf_zhihui = "conf_zhihui";

    //充值 华达配置
    public final static String conf_huada = "conf_huada";

    //充值 华达2配置
    public final static String conf_huada2 = "conf_huada2";

    //充值 麒麟配置
    public final static String conf_qilin = "conf_qilin";

    //合同配置
    public final static String conf_contract = "conf_contract";


    //账单
    public final static String bill_amount_request_user = "bill_amount_request_user_"; //用户余额账单请求数据


    //用户信息
    public final static String user_po_cache_ = "user_po_cache_";
    public final static String user_season_cache_ = "user_season_cache_";//季卡信息
    public final static String user_is_season_cache_ = "user_is_season_cache_";//是否是季卡

    //用户冻结
    public final static String user_freeze_ = "user_freeze_";
    public final static String ip_freeze_ = "ip_freeze_";
    public final static String deviceId_freeze_ = "deviceId_freeze_";


    //广告
    public final static String ad_tencent_id_ = "ad_tencent_id_";
    public final static String ad_tencent2_id_ = "ad_tencent2_id_";
    public final static String ad_tencent3_id_ = "ad_tencent3_id_";
    public final static String ad_tencent4_id_ = "ad_tencent4_id_";
    public final static String ad_tencent_active_id_ = "ad_tencent_active_id_";
    public final static String ad_tencent2_active_id_ = "ad_tencent2_active_id_";
    public final static String ad_tencent3_active_id_ = "ad_tencent3_active_id_";
    public final static String ad_tencent4_active_id_ = "ad_tencent4_active_id_";
    public final static String ad_tencent_remain_id_ = "ad_tencent_remain_id_";
    public final static String ad_tencent2_remain_id_ = "ad_tencent2_remain_id_";
    public final static String ad_tencent3_remain_id_ = "ad_tencent3_remain_id_";
    public final static String ad_tencent4_remain_id_ = "ad_tencent4_remain_id_";
    public final static String ad_uc_id_ = "ad_uc_id_";
    public final static String ad_oceanengine_id_ = "ad_oceanengine_id_";
    public final static String ad_oceanengine_active_id_ = "ad_oceanengine_active_id_";//巨量引擎激活的ID
    public final static String ad_oceanengine_remain_id_ = "ad_oceanengine_remain_id_";
    public final static String ad_baidu_id_ = "ad_baidu_id_";
    public final static String ad_baidu_active_id_ = "ad_baidu_active_id_";//百度激活的ID
    public final static String ad_kwai_id_ = "ad_kwai_id_";
    public final static String ad_kwai_active_id_ = "ad_kwai_active_id_";//快手激活的ID
    public final static String ad_bianxianmao_id_ = "ad_bianxianmao_id_";
    public final static String ad_xingtu_id_ = "ad_xingtu_id_";


    //新广告
    public final static String ads_deviceid_channel_ = "ads_deviceid_channel_"; //设备来源
    public final static String ads_deviceid_callbackurl_oceanengine_ = "ads_deviceid_callbackurl_oceanengine_"; //设备的callback





    //用户登陆错误次数
    public final static String user_login_account_error_deviceId_ = "user_login_account_error_";
    public final static String user_login_pass_error_userId_ = "user_login_pass_error_";


    public final static String task_all_key = "task_all_key";

    //ai对话记录
    public final static String ai_chat_msg_list_ = "ai_chat_msg_list_";


    //福卡配置
    public final static String conf_blessing = "conf_blessing";
    public final static String conf_blessing_card = "conf_blessing_card";


    //客户端封禁
    public final static String client_reject_list = "client_reject_list";


    //森林
    public final static String forest_tree_level_list = "forest_tree_level_list";

    //在线客服
    public final static String agent_online = "agent_online";

    //客户会话待分配记号
    public final static String customer_chat_wait_assign = "customer_chat_wait_assign";

    //设备分配的客服
    public final static String device_id_to_agent_ = "device_id_to_agent_";

    //设备打开的会话
    public final static String device_id_to_chat_ = "device_id_to_chat_";

    //客服信息
    public final static String agent_info_ = "agent_info_";

    //用户订阅客服
    public final static String customer_service_subscribe_ = "customer_service_subscribe_";

    //用户正在输入
    public final static String customer_service_typing_ = "customer_service_typing_";

    //客服订阅客服
    public final static String agent_service_subscribe_ = "agent_service_subscribe_";


    //banner
    public final static String banner_list = "banner_list";
    public final static String activity_list = "activity_list";
    public final static String newbie_channel_list = "newbie_channel_list";

    //shop
    public final static String shop_all = "shop_all";
    public final static String shop_detail_by_id_ = "shop_detail_by_id_";


    //黑名单
    public final static String blacklist_tel_set = "blacklist_tel_set";
    public final static String blacklist_device_set = "blacklist_device_set";
    public final static String blacklist_ip_set = "blacklist_ip_set";





}
