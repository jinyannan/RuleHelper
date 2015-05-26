package gov.customs.jm.data;

// Generated May 26, 2015 2:43:33 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * RuleData generated by hbm2java
 */
public class RuleData implements java.io.Serializable {

	private BigDecimal ruleId;
	private BigDecimal version;
	private String ruleDesc;
	private String preRuleCond;
	private String ruleCond;
	private String postRuleCond;
	private Boolean isExit;
	private Boolean isLog;
	private String logDesc;
	private Boolean isEstimate;
	private Boolean isShere;
	private Boolean isFeedback;
	private String feedbackDesc;
	private String ruleType;
	private String loopKey;
	private String loopCond;
	private String execKey;
	private String ruleStatus;
	private Date effectiveDate;
	private String serviceDomain;
	private String belongSys;
	private String category;
	private String createPerson;
	private String scene;
	private String customsCode;
	private String note;
	private String businessCode;
	private String positionDesc;

	public RuleData() {
	}

	public RuleData(BigDecimal ruleId) {
		this.ruleId = ruleId;
	}

	public RuleData(BigDecimal ruleId, String ruleDesc, String preRuleCond,
			String ruleCond, String postRuleCond, Boolean isExit,
			Boolean isLog, String logDesc, Boolean isEstimate, Boolean isShere,
			Boolean isFeedback, String feedbackDesc, String ruleType,
			String loopKey, String loopCond, String execKey, String ruleStatus,
			Date effectiveDate, String serviceDomain, String belongSys,
			String category, String createPerson, String scene,
			String customsCode, String note, String businessCode,
			String positionDesc) {
		this.ruleId = ruleId;
		this.ruleDesc = ruleDesc;
		this.preRuleCond = preRuleCond;
		this.ruleCond = ruleCond;
		this.postRuleCond = postRuleCond;
		this.isExit = isExit;
		this.isLog = isLog;
		this.logDesc = logDesc;
		this.isEstimate = isEstimate;
		this.isShere = isShere;
		this.isFeedback = isFeedback;
		this.feedbackDesc = feedbackDesc;
		this.ruleType = ruleType;
		this.loopKey = loopKey;
		this.loopCond = loopCond;
		this.execKey = execKey;
		this.ruleStatus = ruleStatus;
		this.effectiveDate = effectiveDate;
		this.serviceDomain = serviceDomain;
		this.belongSys = belongSys;
		this.category = category;
		this.createPerson = createPerson;
		this.scene = scene;
		this.customsCode = customsCode;
		this.note = note;
		this.businessCode = businessCode;
		this.positionDesc = positionDesc;
	}

	public BigDecimal getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(BigDecimal ruleId) {
		this.ruleId = ruleId;
	}

	public BigDecimal getVersion() {
		return this.version;
	}

	public void setVersion(BigDecimal version) {
		this.version = version;
	}

	public String getRuleDesc() {
		return this.ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public String getPreRuleCond() {
		return this.preRuleCond;
	}

	public void setPreRuleCond(String preRuleCond) {
		this.preRuleCond = preRuleCond;
	}

	public String getRuleCond() {
		return this.ruleCond;
	}

	public void setRuleCond(String ruleCond) {
		this.ruleCond = ruleCond;
	}

	public String getPostRuleCond() {
		return this.postRuleCond;
	}

	public void setPostRuleCond(String postRuleCond) {
		this.postRuleCond = postRuleCond;
	}

	public Boolean getIsExit() {
		return this.isExit;
	}

	public void setIsExit(Boolean isExit) {
		this.isExit = isExit;
	}

	public Boolean getIsLog() {
		return this.isLog;
	}

	public void setIsLog(Boolean isLog) {
		this.isLog = isLog;
	}

	public String getLogDesc() {
		return this.logDesc;
	}

	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}

	public Boolean getIsEstimate() {
		return this.isEstimate;
	}

	public void setIsEstimate(Boolean isEstimate) {
		this.isEstimate = isEstimate;
	}

	public Boolean getIsShere() {
		return this.isShere;
	}

	public void setIsShere(Boolean isShere) {
		this.isShere = isShere;
	}

	public Boolean getIsFeedback() {
		return this.isFeedback;
	}

	public void setIsFeedback(Boolean isFeedback) {
		this.isFeedback = isFeedback;
	}

	public String getFeedbackDesc() {
		return this.feedbackDesc;
	}

	public void setFeedbackDesc(String feedbackDesc) {
		this.feedbackDesc = feedbackDesc;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getLoopKey() {
		return this.loopKey;
	}

	public void setLoopKey(String loopKey) {
		this.loopKey = loopKey;
	}

	public String getLoopCond() {
		return this.loopCond;
	}

	public void setLoopCond(String loopCond) {
		this.loopCond = loopCond;
	}

	public String getExecKey() {
		return this.execKey;
	}

	public void setExecKey(String execKey) {
		this.execKey = execKey;
	}

	public String getRuleStatus() {
		return this.ruleStatus;
	}

	public void setRuleStatus(String ruleStatus) {
		this.ruleStatus = ruleStatus;
	}

	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getServiceDomain() {
		return this.serviceDomain;
	}

	public void setServiceDomain(String serviceDomain) {
		this.serviceDomain = serviceDomain;
	}

	public String getBelongSys() {
		return this.belongSys;
	}

	public void setBelongSys(String belongSys) {
		this.belongSys = belongSys;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreatePerson() {
		return this.createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public String getScene() {
		return this.scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getCustomsCode() {
		return this.customsCode;
	}

	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getBusinessCode() {
		return this.businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getPositionDesc() {
		return this.positionDesc;
	}

	public void setPositionDesc(String positionDesc) {
		this.positionDesc = positionDesc;
	}

}
