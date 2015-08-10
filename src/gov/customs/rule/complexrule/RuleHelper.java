package gov.customs.rule.complexrule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.CachedRowSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import gov.customs.rule.data.*;
import gov.customs.rule.expression.proxy.*;
import gov.customs.rule.expression.proxy.ExpressionHelperProxy.LogType;

public class RuleHelper {

	private static final org.apache.logging.log4j.Logger ruleLogger = LogManager
			.getLogger("gov.customs.rule.expression.complexrule");

	private Boolean isGo = true;

	/**
	 * 判断是否合法日期
	 * 
	 * @param str
	 * @return
	 */
	private boolean isValidDate(String str) {
		boolean convertSuccess = false;
		SimpleDateFormat[] formats = { new SimpleDateFormat("yyyy-MM-dd"),
				new SimpleDateFormat("yyyy/MM/dd") };
		for (SimpleDateFormat simpleDateFormat : formats) {
			try {
				simpleDateFormat.setLenient(false);
				simpleDateFormat.parse(str);
				convertSuccess = true;
				break;
			} catch (ParseException | NullPointerException e1) {
			}
		}

		return convertSuccess;
	}

	/**
	 * 根据父节点返回子节点list
	 * 
	 * @param parentRuleID
	 * @return
	 */
	private List<RuleRelData> getChileRuleRelDataByHB(BigDecimal parentRuleID) {
		String sql = "select * from RULE_REL_DATA where PARENT_RULE_ID = "
				+ parentRuleID + " order by rule_order desc";
		Query queryRuleRelData = getSessionFactory().createSQLQuery(sql)
				.addEntity(RuleRelData.class);
		List<RuleRelData> listRuleRelData = queryRuleRelData.list();
		if (listRuleRelData.size() > 0) {
			return listRuleRelData;
		} else {
			return null;
		}
	}

	/**
	 * 使用hibernate，根据rule_id，查找rule_rel_data中相应记录。
	 * 
	 * @param ruleID
	 * @return
	 */
	private RuleRelData getSingleRelDataByHB(BigDecimal ruleId) {
		String sql = "select * from RULE_REL_DATA where rule_id = " + ruleId;
		Query query = getSessionFactory().createSQLQuery(sql).addEntity(
				RuleRelData.class);
		List<RuleRelData> list = query.list();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 根据ruleid，查找rule_data中相应记录
	 * 
	 * @param ruleID
	 * @return
	 */
	private RuleData getSingleRuleDataByHB(BigDecimal ruleID) {

		String sql = "select * from RULE_DATA where rule_id = " + ruleID;
		Query query = getSessionFactory().createSQLQuery(sql).addEntity(
				RuleData.class);
		List<RuleData> list = query.list();
		if (list.size() == 1) {
			return (RuleData) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	private Session getSessionFactory() {
		SessionFactory sessionFactory = null;
		try {
			sessionFactory = new Configuration().configure()
					.buildSessionFactory();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return sessionFactory.openSession();
	}

	/**
	 * 根据表达式和传入的数据，运算表达式
	 * 
	 * @param exprCond
	 * @param data
	 * @param local
	 * @return
	 */
	private Object executeExpr(String exprCond, Object data, Object local) {
		return new ExpressionHelperProxy().ExecuteExpression(exprCond, data,
				local);
	}

	private Object executeExpr(RuleLogData logData, Object data, Object local) {
		return new ExpressionHelperProxy().ExecuteExpression(logData, data,
				local);
	}

	/**
	 * 计算表达式，并根据标示，决定是否写日志和反馈
	 * 
	 * @param ruleData
	 * @param data
	 * @param local
	 * @param feedback
	 * @return
	 */
	@Deprecated
	private Object executeExpr(RuleData ruleData, Object data, Object local,
			ArrayList<RuleFeedback> feedback) throws Exception {
		String ruleCond = ruleData.getRuleCond();
		Boolean isLog = Boolean.valueOf(ruleData.getIsLog());

		Boolean isEstimate = Boolean.valueOf(ruleData.getIsEstimate());
		Boolean result = (Boolean) executeExpr(ruleCond, data, local);

		BigDecimal ruleId = ruleData.getRuleId();
		Boolean isFeedBack = Boolean.valueOf(ruleData.getIsFeedback());
		String businessCode = ruleData.getBusinessCode();
		String hitDesc = GetTransResult(ruleData.getHitDesc(), data, local);
		String positionDesc = GetTransResult(ruleData.getPositionDesc(), data,
				local);

		RuleFeedback feed = new RuleFeedback(ruleId, isFeedBack, businessCode,
				hitDesc, positionDesc);
		if (result) {
			if (isLog) {
				WriteLog(ruleCond);
			}
			if (isFeedBack) {
				feedback.add(feed);
			}
		}
		return result;

	}

	/**
	 * 根据表达式和传入的数据，运算表达式
	 * 
	 * @param exprCond
	 * @param data
	 * @return
	 */
	private Object executeExpr(String exprCond, Object data) {
		return executeExpr(exprCond, data, new HashMap<String, Object>());
	}

	/**
	 * 首字母大写
	 * 
	 * @param s
	 * @return
	 */
	private String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	/**
	 * 将列名转换为方法名
	 * 
	 * @param s
	 * @return
	 */
	private String colToMethodName(String s) {
		String[] strs = s.toLowerCase().split("_");
		String result = "";
		for (int i = 0; i < strs.length; i++) {
			result += toUpperCaseFirstOne(strs[i]);
		}
		return result;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	private boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 写日志
	 * 
	 * @param str
	 * @throws IOException
	 */
	private void WriteLog(String str) throws IOException {
		FileWriter writer = null;

		File file = new File(
				"//Users//yannan//Documents//antlr-new//expr_result.log");
		if (!file.exists()) {
			file.createNewFile();
		}
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(
					"//Users//yannan//Documents//antlr-new//expr_result.log",
					true);
			writer.write(str + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// private String GetTransResult(String beforTransResult, Object data) {
	// return GetTransResult(beforTransResult, data, null);
	// }

	/**
	 * 将结果中的表达式部分转换为实际数据返回
	 * 
	 * @param beforTransResult
	 * @param data
	 * @return
	 */
	private String GetTransResult(String beforTransResult, Object data,
			Object local) {
		if (beforTransResult == null || beforTransResult.trim().equals("")) {
			return "";
		}
		String regex = "(?<=\\{)[^{}]+(?=\\})";
		// beforeResult = "报关单号为{$PRI_LIST.G_NO},G_NO为{$PRI_HEAD.G_TYPE}数据出现错误";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(beforTransResult);
		String afterTransResult = "";
		int location = 0;
		String result = "";

		while (matcher.find()) {
			String temp = beforTransResult.substring(matcher.start(),
					matcher.end());
			result = String.valueOf(executeExpr(temp, data, local));
			afterTransResult += beforTransResult.substring(location,
					matcher.start())
					+ result;
			location = matcher.end();
		}
		afterTransResult += beforTransResult.substring(location,
				beforTransResult.length());
		return afterTransResult;
	}

	/**
	 * 根据传入的节点值拆分逻辑树，计算分别代表的规则
	 * 
	 * @param rootRuleID
	 * @param data
	 * @throws Exception
	 */
	@SuppressWarnings({ "unused" })
	public ArrayList<RuleFeedback> executeComplexRule(BigDecimal rootRuleID,
			Object data) throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		long beginTime = new java.util.Date().getTime();

		Stack<RuleRelData> relDataStack = new Stack<RuleRelData>();

		BigDecimal parentRuleID;
		BigDecimal ruleID;
		String postRuleDesc;
		String ruleName;
		String ruleDesc = "";
		String preRuleCond = "";
		String ruleCond = "";
		String postRuleCond = "";
		Boolean isExit = null;
		Boolean isLog = null;
		String logDesc = "";
		Boolean isEstimate = null;
		Boolean isShare = null;
		Boolean isFeedBack = null;
		String feedBackDesc = "";
		String ruleType = ""; // 0:leaf node 1:branch node 2:loop node
		String loopKey = "";
		String loopCond = "";
		String execKey = "";
		String ruleStatus = null;
		Date effectiveDate = null;
		BigDecimal version = null;
		BigDecimal serviceDomain = null;
		BigDecimal belongSys = null;
		String category = "";
		String createPerson = "";
		String scene = "";
		String customeCode = "";
		String note = "";
		String businessCode = "";
		String positionDesc = "";

		Boolean preResult = true;
		Boolean result = true;
		RuleFeedback feedback = null;
		ArrayList<RuleFeedback> feedbackList = new ArrayList<RuleFeedback>();
		Boolean postResult = true;

		/**
		 * loopCountMap:记录循环节点当前的记录集下表 loopTempMap:保存循环节点过滤后的记录
		 * localmap:循环中拆解的对象临时保存在这里
		 */
		HashMap<BigDecimal, Integer> loopCountMap = new HashMap<BigDecimal, Integer>();
		HashMap<BigDecimal, List<?>> loopTempMap = new HashMap<BigDecimal, List<?>>();
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		// HashMap<String, Object> hmLocal = new HashMap<String, Object>();
		Object local = (Object) localMap;
		List<RuleRelData> childLevelRule = null;

		RuleData ruleData = new RuleData();
		RuleRelData ruleRelData = new RuleRelData();

		RuleRelData rootLevelRelDataList = getSingleRelDataByHB(rootRuleID);
		if (rootLevelRelDataList != null) {
			relDataStack.push(rootLevelRelDataList);
		} else {
			return null;
		}

		while (relDataStack != null && !relDataStack.empty()) {
			ruleRelData = relDataStack.peek();

			parentRuleID = ruleRelData.getParentRuleId();
			ruleID = ruleRelData.getRuleId();
			postRuleDesc = ruleRelData.getPostRuleCond();

			if (ruleID == BigDecimal.valueOf(301)) {
				System.out.println("hehe");
			}

			if (ruleID != null && ruleID != BigDecimal.ZERO) {
				ruleData = getSingleRuleDataByHB(ruleID);

				ruleName = ruleData.getRuleName();
				ruleDesc = ruleData.getRuleCond();
				preRuleCond = ruleData.getPreRuleCond();
				ruleCond = ruleData.getRuleCond();
				postRuleCond = ruleData.getPostRuleAction();
				isExit = ruleData.getIsExit() == null ? false : ruleData
						.getIsExit();
				isLog = ruleData.getIsLog();
				logDesc = ruleData.getLogDesc();
				isEstimate = ruleData.getIsEstimate() == null ? false
						: ruleData.getIsEstimate();
				isShare = ruleData.getIsShare();
				isFeedBack = ruleData.getIsFeedback();
				// feedBackDesc = rData.getFeedbackD();
				ruleType = ruleData.getRuleType();
				loopKey = ruleData.getLoopCond();
				// loopCond = rData.getLoopCond();
				execKey = ruleData.getExecKey();
				ruleStatus = ruleData.getRuleStatus();
				effectiveDate = (Date) ruleData.getEffectiveDate();
				version = ruleData.getRuleVersion();
				serviceDomain = ruleData.getServiceDomain();
				belongSys = ruleData.getOwnerSys();
				category = ruleData.getCategory();
				createPerson = ruleData.getCreatePerson();
				scene = ruleData.getScene();
				customeCode = ruleData.getCustomsCode();
				note = ruleData.getNote();
				businessCode = ruleData.getBusinessCode();
				positionDesc = ruleData.getPositionDesc();

				RuleLogData logData = new RuleLogData();
				logData.setRuleId(ruleID);
				logData.setRuleName(ruleName);
				logData.setRuleCond(ruleCond);
				logData.setRuleType(ruleType);
				logData.setIsEstimate(isEstimate);
				logData.setIsLog(isLog);

			}

			// 如果规则状态不为1，则跳过
			if (!ruleStatus.equals("1")) {
				relDataStack.pop();
				continue;
			}

			if (!isEmptyString(postRuleDesc)) {
				postResult = (Boolean) executeExpr(postRuleDesc, data, local);
			} else {
				postResult = true;
			}
			// FIXME
			// System.out.println("ruleid==" + ruleID);
			if (ruleID.equals(BigDecimal.valueOf(370))) {
				System.out.println("hehe");
			}

			if (postResult) {
				// 叶节点 ruletype == 0
				// FIXME
				// System.out.println("次序：" + ruleID);
				if (ruleType.equals("0")) {
					relDataStack.pop();
					if (isEmptyString(ruleCond)) {
						result = true;
						feedback = getFeedbackByRuleData(ruleData, data, local);
					} else {
						feedback = executeSingleRule(ruleData, data, local);
						result = feedback == null ? false : true;
					}
					if (result) {
						if (!isEstimate) {
							feedbackList.add(feedback);
						}
						if (isExit && !isEstimate) {
							return feedbackList;
						}
					}

				} else if (ruleType.equals("2")) {

					/**
					 * 如果缓存中存在当前循环节点的结果集，则从缓存中获得，loopkey为filter返回的arraylist
					 */
					List<?> loopData;
					if (loopTempMap.containsKey(ruleID)) {
						loopData = loopTempMap.get(ruleID);
					} else {
						loopData = (List<?>) executeExpr(loopKey, data, local);
						loopTempMap.put(ruleID, loopData);
					}
					if (isEmptyString(ruleCond)) {
						result = true;
					} else {
						feedback = executeSingleRule(ruleData, data, local);
						result = feedback == null ? false : true;
						// result = (Boolean) executeExpr(ruleCond, data,
						// local);
					}
					Integer maxCount;
					Integer currentCount;
					//FIXME:
					maxCount = loopData.size();

					if (loopCountMap.get(ruleID) == null) {
						loopCountMap.put(ruleID, 0);
						currentCount = 0;
					} else {
						currentCount = loopCountMap.get(ruleID);
					}

					if (result && currentCount < maxCount) {
						localMap.put(execKey,
								(Object) loopData.get(currentCount));
						childLevelRule = getChileRuleRelDataByHB(ruleID);
						for (int i = 0; i < childLevelRule.size(); i++) {
							relDataStack.push(childLevelRule.get(i));
						}
						currentCount++;
						loopCountMap.put(ruleID, currentCount);
					} else {
						loopCountMap.remove(ruleID);
						loopTempMap.remove(ruleID);
						localMap.remove(execKey);
						relDataStack.pop();
					}
				} else {
					// 枝节点
					relDataStack.pop();
					if (isEmptyString(ruleCond)) {
						result = true;
						// feedback = getFeedbackByRuleData(ruleData, data,
						// local);
					} else {
						feedback = executeSingleRule(ruleData, data, local);
						result = feedback == null ? false : true;
					}

					if (result) {
						childLevelRule = getChileRuleRelDataByHB(ruleID);
						for (int i = 0; i < childLevelRule.size(); i++) {
							relDataStack.push(childLevelRule.get(i));
						}
					}
				}
			} else {
				relDataStack.pop();
			}
		}

		long endTime = new java.util.Date().getTime();
		String message = String.format("%d;%s;%s;%d", rootRuleID.intValue(),
				format.format(beginTime), format.format(endTime), endTime
						- beginTime);
		printLog(message, LogType.Trace);

		return feedbackList;
	}

	/**
	 * 打印日志
	 * 
	 * @param message
	 * @param logType
	 */
	public void printLog(String message, LogType logType) {
		ThreadContext.put("ThreadID",
				String.valueOf(Thread.currentThread().getId()));
		switch (logType) {
		case Error:
			ruleLogger.error(message);
			break;
		case Trace:
			ruleLogger.trace(message);
			break;
		default:
			break;
		}
	}

	public RuleFeedback executeSingleRule(BigDecimal ruleId, Object data) {
		return executeSingleRule(ruleId, data,
				(Object) new HashMap<String, Object>());
	}

	/**
	 * 执行单条规则
	 * 
	 * @param ruleId
	 * @param data
	 * @return
	 */
	public RuleFeedback executeSingleRule(BigDecimal ruleId, Object data,
			Object local) {
		RuleData ruleData = getSingleRuleDataByHB(ruleId);
		return executeSingleRule(ruleData, data, local);
	}

	/**
	 * 执行单挑规则
	 * 
	 * @param ruleData
	 * @param data
	 * @return
	 */
	public RuleFeedback executeSingleRule(RuleData ruleData, Object data,
			Object local) {
		String ruleStatus = ruleData.getRuleStatus();
		java.util.Date effectiveDate = ruleData.getEffectiveDate();
		BigDecimal ruleId = ruleData.getRuleId();
		String ruleName = ruleData.getRuleName();
		String ruleType = ruleData.getRuleType();
		String preRuleCond = ruleData.getPreRuleCond();
		String ruleCond = ruleData.getRuleCond();
		Boolean isFeedBack = ruleData.getIsFeedback();
		Boolean isEstimate = ruleData.getIsEstimate();
		Boolean isLog = ruleData.getIsLog();
		String businessCode = ruleData.getBusinessCode();
		String hitDesc = ruleData.getHitDesc();
		String positionDesc = ruleData.getPositionDesc();
		Boolean result = false;

		RuleLogData logData = new RuleLogData();
		logData.setRuleId(ruleId);
		logData.setRuleName(ruleName);
		logData.setRuleCond(ruleCond);
		logData.setRuleType(ruleType);
		logData.setIsEstimate(isEstimate);
		logData.setIsLog(isLog);

		// 只有状态为1的时候执行
		if (ruleStatus.equals("1")) {
			// 必须大于生效日期
			if (effectiveDate == null
					|| effectiveDate.before(new java.util.Date())) {
				// 前置条件满足或者null
				if (preRuleCond == null || preRuleCond.trim().equals("")
						|| (boolean) executeExpr(preRuleCond, data, local)) {
					if (ruleCond == null || ruleCond.trim().equals("")) {
						result = true;
					} else {
						result = (Boolean) executeExpr(logData, data, local);
					}
				}
			}
		}
		// FIXME:此处增加对于计算失败的输出，正式版本一定要去掉
		// if (result == null) {
		// System.out.println("结果错误：" + ruleId);
		// }
		if (result) {
			return getFeedbackByRuleData(ruleData, data, local);
		} else {
			// System.out.println("rule false：" + ruleData.getRuleId());
			return null;
		}
	}

	public RuleFeedback getFeedbackByRuleData(RuleData ruleData, Object data,
			Object local) {

		BigDecimal ruleId;
		Boolean isFeedBack;
		String businessCode;
		String hitDesc;
		String positionDesc;

		ruleId = ruleData.getRuleId();
		isFeedBack = ruleData.getIsFeedback();
		businessCode = ruleData.getBusinessCode();
		hitDesc = GetTransResult(ruleData.getHitDesc(), data, local);
		positionDesc = GetTransResult(ruleData.getPositionDesc(), data, local);

		RuleFeedback feedback = new RuleFeedback(ruleId, isFeedBack,
				businessCode, hitDesc, positionDesc);

		return feedback;
	}

	public boolean getRuleRelData() {
		String sql = "select * from RULE_REL_DATA";
		Query query = getSessionFactory().createSQLQuery(sql).addEntity(
				RuleRelData.class);
		List<RuleRelData> list = query.list();
		if (list.size() > 1) {
			return true;
		} else {
			return false;
		}
	}

}