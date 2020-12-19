package com.qiguliuxing.dts.core.type;

/**
 * 佣金类型的枚举类
 * 
 * @author CHENBO
 * @QQ 623659388
 * @since 1.0.0
 */
public enum BrokerageTypeEnum {

	SYS_APPLY((byte) 0, "系统结算自动申请"), USER_APPLY((byte) 1, "用户手工申请"), APPLY_FINISH((byte) 2, "审批通过或完成"), APPLY_FAIL((byte) 2, "审批不通过");

	private Byte type;
	private String desc;

	private BrokerageTypeEnum(Byte type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public static BrokerageTypeEnum getInstance(Byte type2) {
		if (type2 != null) {
			for (BrokerageTypeEnum tmp : BrokerageTypeEnum.values()) {
				if (tmp.type.intValue() == type2.intValue()) {
					return tmp;
				}
			}
		}
		return null;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
