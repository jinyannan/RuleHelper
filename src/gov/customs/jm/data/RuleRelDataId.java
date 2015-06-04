package gov.customs.jm.data;

// Generated Jun 4, 2015 4:42:51 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

/**
 * RuleRelDataId generated by hbm2java
 */
public class RuleRelDataId implements java.io.Serializable {

	private BigDecimal parentRuleId;
	private BigDecimal ruleId;

	public RuleRelDataId() {
	}

	public RuleRelDataId(BigDecimal parentRuleId, BigDecimal ruleId) {
		this.parentRuleId = parentRuleId;
		this.ruleId = ruleId;
	}

	public BigDecimal getParentRuleId() {
		return this.parentRuleId;
	}

	public void setParentRuleId(BigDecimal parentRuleId) {
		this.parentRuleId = parentRuleId;
	}

	public BigDecimal getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(BigDecimal ruleId) {
		this.ruleId = ruleId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RuleRelDataId))
			return false;
		RuleRelDataId castOther = (RuleRelDataId) other;

		return ((this.getParentRuleId() == castOther.getParentRuleId()) || (this
				.getParentRuleId() != null
				&& castOther.getParentRuleId() != null && this
				.getParentRuleId().equals(castOther.getParentRuleId())))
				&& ((this.getRuleId() == castOther.getRuleId()) || (this
						.getRuleId() != null && castOther.getRuleId() != null && this
						.getRuleId().equals(castOther.getRuleId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getParentRuleId() == null ? 0 : this.getParentRuleId()
						.hashCode());
		result = 37 * result
				+ (getRuleId() == null ? 0 : this.getRuleId().hashCode());
		return result;
	}

}
