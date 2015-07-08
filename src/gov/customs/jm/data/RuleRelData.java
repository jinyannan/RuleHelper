package gov.customs.jm.data;

// Generated Jul 7, 2015 1:51:03 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

/**
 * RuleRelData generated by hbm2java
 */
public class RuleRelData implements java.io.Serializable {

	private BigDecimal relId;
	private BigDecimal parentRuleId;
	private BigDecimal ruleId;
	private String postRuleCond;
	private BigDecimal ruleOrder;
	private String entityKeys;

	public RuleRelData() {
	}

	public RuleRelData(BigDecimal relId) {
		this.relId = relId;
	}

	public RuleRelData(BigDecimal relId, BigDecimal parentRuleId,
			BigDecimal ruleId, String postRuleCond, BigDecimal ruleOrder,
			String entityKeys) {
		this.relId = relId;
		this.parentRuleId = parentRuleId;
		this.ruleId = ruleId;
		this.postRuleCond = postRuleCond;
		this.ruleOrder = ruleOrder;
		this.entityKeys = entityKeys;
	}

	public BigDecimal getRelId() {
		return this.relId;
	}

	public void setRelId(BigDecimal relId) {
		this.relId = relId;
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

	public String getPostRuleCond() {
		return this.postRuleCond;
	}

	public void setPostRuleCond(String postRuleCond) {
		this.postRuleCond = postRuleCond;
	}

	public BigDecimal getRuleOrder() {
		return this.ruleOrder;
	}

	public void setRuleOrder(BigDecimal ruleOrder) {
		this.ruleOrder = ruleOrder;
	}

	public String getEntityKeys() {
		return this.entityKeys;
	}

	public void setEntityKeys(String entityKeys) {
		this.entityKeys = entityKeys;
	}

}
