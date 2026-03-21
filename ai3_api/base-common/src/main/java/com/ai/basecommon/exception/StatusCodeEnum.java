package com.ai.basecommon.exception;

import java.io.Serializable;

public enum StatusCodeEnum implements Serializable {

    SUCCESS(0, "成功"),
    ERROR(-1, "失败"),

    SERVICE_ERROR(900, "服务异常，请稍后再试"),
    REJECT(901, "网络异常"),//客户端封禁

    BALANCE_NOT_ENOUGH(1001,"余额不足"),

    SIGN_ERROR(9999, "认证失败"),
    SYSTEM_ERROR(10000, "系统异常，请稍后再试"),
    AUTH_ERROR(10001, "认证失败，请重新登录"),
    NO_AUTH(10002, "暂无权限"),
    REQUEST_LIMIT(10003, "请求过于频繁"),
    REQUEST_TOO_MANY(10004, "操作频繁，请稍后再试"),
    PARAM_ERROR(10005, "参数错误"),
    CONFIG_ERROR(10007, "配置有误，请联系管理员"),
    REQUEST_ERROR(10010, "非法请求"),


    EXIST(10011, "数据已存在"),
    NO_EXIST(10012, "数据不存在"),
    DO_REPEAT(10013, "重复提交"),
    NUM_NOT_ENOUGH(10014, "数量不足"),
    PASSWORD_ERROR(10015, "密码错误"),
    PASSWORD_PAY_ERROR(10016, "支付密码错误"),
    USER_NO_EXIST(10017, "用户不存在"),
    USER_FREEZE(10020, "账号已冻结"),
    PLEASE_AUTH(10021, "请先进行实名认证"),
    FREEZE(10025, "已封禁"),
    CAN_NOT_USE(10026, "你无法使用此功能"),
    PLEASE_RESTART_APP(10028, "网络异常，请重启APP"),
    BLACKLIST(10088, "网络异常，请重启APP"),



    //OSS
    OSS_CONF_NO(10031, "未配置OSS,无法进行上传操作"),
    OSS_FILE_NO_EXIST(10032, "文件不存在,无法上传"),
    OSS_FILE_SUFFIX_ERROR(10033, "不被允许的文件"),
    OSS_FILE_SIZE_ERROR(10034, "文件大小不符合规则"),

    //验证码
    CAPTCHA_CHANNEL_NO_EXIST(10101, "验证码通道不存在"),
    CAPTCHA_SEND_FAIL(10102, "验证码发送失败"),
    CAPTCHA_ERROR(10103, "验证码错误"),
    CAPTCHA_EXPIRE(10104, "验证码已失效"),
    CAPTCHA_VERIFY_FAIL(10105, "验证码校验失败"),
    CAPTCHA_TYPE_ERROR(10106, "验证码类型错误"),
    CAPTCHA_TICKET_FAIL(10107, "验证码发送券校验失败"),
    CAPTCHA_NEW_VERIFY_FAIL(10108, "新验证码校验失败"),

    //正则
    REG_TEL_RULE(10201, "手机号格式错误"),
    REG_EMAIL_RULE(10202, "邮箱格式错误"),

    //注册
    EMAIL_EXIST(11003, "该邮箱已注册"),
    EMAIL_ERROR(11004, "邮箱错误"),
    TEL_EXIST(11005, "该手机号已注册"),
    PASS_RULE_ERROR(11007,"密码不符合规则"),
    PASS_AGAIN_NO_SAME(11008,"两次密码不一致"),
    INVITE_TEL_ERROR(11011,"邀请码错误"),

    //登陆
    LOGIN_FAIL(11031,"登陆失败"),
    LOGIN_ACCOUNT_NO_EXIST(11032, "账号不存在"),
    LOGIN_ACCOUNT_FREEZE(11033, "账号已冻结,无法登陆"),
    LOGIN_PASSWORD_ERROR(11034, "密码错误"),
    LOGIN_PASSWORD_ERROR_(11035,"密码错误，您还有{}次机会。"),
    LOGIN_ACTION_FREEZE(11036,"用户行为被限制，请2小时后重试"),
    LOGIN_ACCOUNT_ERROR_(11037,"账号错误，您还有{}次机会。"),

    //修改资料
    EDIT_DATA_OLD_PASSWORD_ERR(11101,"旧密码错误"),
    AUTH_STATUS_YES(11111,"已经认证过了"),
    AUTH_EXIST(11113,"该身份证已绑定过了"),
    AUTH_PLEASE(11115,"请先进行实名认证"),
    AUTH_FAIL(11116,"实名认证不通过"),

    //银行卡
    BANK_CARD_EXIST(11212,"已绑定过银行卡"),

    //支付宝
    BANK_ALIPAY_ERROR(11220,"不正确的支付宝账号"),
    BANK_ALIPAY_EXIST(11221,"已绑定过支付宝账号"),

    //签到
    SIGN_ALREADY(11300,"今天已签到过了"),

    //车主认证
    CAR_OWNER_AUTH_WAIT(11400,"认证资料正在审核中，请勿重复提交"),
    CAR_OWNER_CAR_NUMBER(11401,"车牌号错误"),
    CAR_OWNER_CAR_NUMBER_EXIST(11402,"车牌号已认证"),
    CAR_OWNER_AUTH_HAS_SUCCESS(11403,"您已认证成功"),
    CAR_OWNER_AUTH_NO_SUCCESS(11404,"未认证车主"),
    CAR_OWNER_RECEIVE_ALREADY(11406,"已领取过了"),

    //生活缴费
    LIFE_PAY_HOUSE_NUMBER_EXIST(11476,"户号已被绑定"),

    //季卡会员
    SEASON_NO(11503,"您不是季卡会员"),
    SEASON_PROOF_TODAY_EXIST(11506,"今日已上传"),
    SEASON_GIFT_MONTH_EXIST(11511,"本月已领取"),




    //项目
    PRO_STATUS_ERROR(22113,"项目不存在或已下架"),
    PRO_LIMIT_20_MAX_NUM(22115,"单次投放不要超过20份"),
    PRO_LIMIT_LEVEL_(22116,"该项目投放要求为VIP{}"),
    PRO_BUY_MAX_NUM(22117,"超过限投数量"),
    PRO_SELL_OUT(22118,"项目已售罄"),
    PRO_SELL_NOT_ENOUGH(22118,"项目可购买数量不足"),
    PRO_SKU_ERROR(22120,"规格错误"),


    //云币森林
    FOREST_TREE_NO_EXIST(30005,"云币树不存在"),
    FOREST_WATER_LIMIT(30010,"今日浇水次数已达上限"),
    FOREST_FERTILIZE_LIMIT(30011,"今日施肥次数已达上限"),


    //步步生金
    STEP_GOLD_CLOCK_TIME_ERROR(30303,"不在打卡时间"),
    STEP_GOLD_CLOCK_EXIST(30304,"你已打过卡了"),
    STEP_GOLD_WATER_TIME_ERROR(30333,"不在喝水时间"),
    STEP_GOLD_WATER_FULL(30335,"今天已经喝水8次了"),
    STEP_GOLD_WATER_SPACE_ERROR(30336,"距离上次喝水不足1小时"),


    //商品
    SHOP_NO_EXIST(33105,"商品不存在"),
    SHOP_STATUS_ERROR(33107,"商品状态异常"),
    SHOP_ADDR_PLEASE(33108,"请选择收货地址"),
    SHOP_ADDR_ERROR(33109,"收货地址不可用"),
    SHOP_ENERGY_NOT_ENOUGH(33110,"云币不足"),
    SHOP_BALANCE_NOT_ENOUGH(33111,"余额不足"),

    BLESSING_SHOP_PRICE_NOT_ENOUGH(33115,"福卡数量不足"),



    //礼品
    GIFT_CODE_EMPTY(33151,"请输入福利码"),
    GIFT_CODE_ERROR(33152,"错误的福利码"),
    GIFT_CODE_CANNOT(33154,"不能使用的福利码"),
    GIFT_CODE_MAX_NUM_FULL(33155,"福利码已不可用"),
    GIFT_CODE_DAY_NUM_FULL(33156,"福利码今日兑换次数已满"),
    GIFT_CODE_ALREADY_USE(33158,"使用过的福利码"),
    GIFT_NO_EXIST(33163,"礼品不存在或已下架"),


    //订单
    SHOP_ORDER_NO_EXIST(33207,"订单不存在"),
    SHOP_ORDER_STATUS_ERROR(33208,"订单状态异常"),


    //冲提
    RECHARGE_CHANNEL_NOT(41102,"充值渠道已关闭"),
    RECHARGE_CHANNEL_ERROR(41103,"充值渠道错误"),
    RECHARGE_AMOUNT_MIN(41106,"充值金额过低"),
    RECHARGE_AMOUNT_MAX(41107,"充值金额过高"),
    WITHDRAW_AMOUNT_MIN(41136,"提现金额过低"),
    WITHDRAW_PLEASE_BANK_CARD(41137,"请先绑定银行卡号"),
    WITHDRAW_PLEASE_ALIPAY(41138,"请先绑定支付宝"),
    WITHDRAW_NEED_LEASE_ONE(41139,"未投放项目，存在信用卡套现行为，请完成投放后提现"),
    WITHDRAW_AMOUNT_ERROR(41140,"提现金额错误"),
    WITHDRAW_QUOTA_CLOSE(41150,"小额提现已关闭"),
    WITHDRAW_QUOTA_NUM_NO(41153,"小额提现次数为0"),
    WITHDRAW_QUOTA_NO(41154,"无效的小额提现券"),



    //AI聊天
    AI_CHAT_CONTENT_LENGTH(50005,"内容过长，请进行删减"),
    AI_CHAT_VISITOR_LIMIT(50016,"当前次数已满，请登录后继续使用"),
    AI_CHAT_USER_LIMIT(50017,"当前次数已满，可升级PLUS会员后继续使用"),
    AI_CHAT_VIP_LIMIT(50018,"当前次数已满，请明天再使用"),


    //客服
    CUSTOMER_OPEN_CHAT_EXIST(51004,"请勿重复开启会话"),
    CUSTOMER_CHAT_EXIST(51006,"暂无会话"),
    CUSTOMER_CHAT_OVER(51007,"您的会话已结束"),







    ;




    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述
     */
    private String msg;

    StatusCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static StatusCodeEnum getByCode(int code) {
        StatusCodeEnum[] values = StatusCodeEnum.values();
        for (StatusCodeEnum StatusCodeEnum : values) {
            if (StatusCodeEnum.code == code) {
                return StatusCodeEnum;
            }
        }
        return null;
    }
}