package gov.customs.jm.ruler;

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
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.rowset.CachedRowSet;
import org.antlr.ext.ConditionExpression.Expression;
import org.antlr.ext.ConditionExpression.Visitor.IGetValue;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import gov.customs.jm.data.*;
import ExpressionHelper.EntryHelper;


public class RuleHelper {
	
	private static Boolean isGo = true;
	
	/**
	 * 判断是否合法日期
	 * @param str
	 * @return
	 */
	private static boolean isValidDate(String str) {
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
	 * @param parentRuleID
	 * @return
	 */
	private static List<RuleRelData> GetChileExprDataByHB(BigDecimal parentRuleID){
		SessionFactory sessionFactory = null;
		try {
			sessionFactory= new Configuration().configure()
					.buildSessionFactory();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		Session s = sessionFactory.openSession();
		Query queryRuleRelData = s.createSQLQuery("select * from RULE_REL_DATA where PARENT_RULE_ID = " + parentRuleID).addEntity(RuleRelData.class);
		List<RuleRelData> listRuleRelData = queryRuleRelData.list();
		return listRuleRelData;
	}
	
	/**
	 * 使用hibernate，根据rule_id，查找rule_rel_data中相应记录。
	 * @param ruleID
	 * @return
	 */
	private static RuleRelData GetSingleRelDataByHB(BigDecimal ruleID){
		RuleRelData rrd = new RuleRelData();
		RuleRelDataId rrdid = new RuleRelDataId();
		rrdid.setRuleId(ruleID);
		rrd.setId(rrdid);
		return new RuleRelDataHome().findByExample(rrd).get(0);
	}
	
	/**
	 * 根据ruleid，查找rule_data中相应记录
	 * @param ruleID
	 * @return
	 */
	private static RuleData getSingleRuleDataByHB(BigDecimal ruleID){
		return (new RuleDataHome().findById(ruleID));
	}
	
	/**
	 * 根据表达式和传入的数据，运算表达式
	 * @param exprCond
	 * @param data
	 * @return
	 */
	private static Object ExecuteExpr(String exprCond, Object data, Object local) {
		return new Expression().ExecuteExpression(exprCond, data, local);
	}

	/**
	 *  首字母大写 
	 * @param s
	 * @return
	 */
	private static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	/**
	 * 将列名转换为方法名
	 * @param s
	 * @return
	 */
	private static String colToMethodName(String s) {
		String[] strs = s.toLowerCase().split("_");
		String result = "";
		for (int i = 0; i < strs.length; i++) {
			result += toUpperCaseFirstOne(strs[i]);
		}
		return result;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	private static boolean isEmptyString(String str)
    {
        return str == null || str.trim().length() == 0;
    }

	/**
	 * 写日志
	 * @param str
	 * @throws IOException
	 */
	private static void WriteLog(String str) throws IOException {
		FileWriter writer = null;

		File file = new File("//Users//yannan//Documents//antlr-new//expr_result.log");
		if (!file.exists()) {
			file.createNewFile();
		}
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(
					"//Users//yannan//Documents//antlr-new//expr_result.log", true);
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

	/**
	 * 将结果中的表达式部分转换为实际数据返回
	 * @param beforTransResult
	 * @param data
	 * @return
	 */
	private static String GetTransResult(String beforTransResult, Object data, Object local) {
		String regex = "(?<=\\{)[^{}]+(?=\\})";
		//beforeResult = "报关单号为{$PRI_LIST.G_NO},G_NO为{$PRI_HEAD.G_TYPE}数据出现错误";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(beforTransResult);
		String afterTransResult = "";
		int location = 0;
		String result = "";
		while (matcher.find()) {
			String temp = beforTransResult.substring(matcher.start(), matcher.end());
			result = String.valueOf(ExecuteExpr(temp, data, local));
			afterTransResult += beforTransResult.substring(location, matcher.start()) + result;
			location = matcher.end();
		}
		afterTransResult += beforTransResult.substring(location, beforTransResult.length());
		//System.out.println(afterTransResult);
		return afterTransResult;
	}
	
	/**
	 * 根据传入的节点值拆分逻辑树，计算分别代表的规则
	 * @param rootRuleID
	 * @param data
	 * @throws Exception
	 */
	public static void ExecuteRuleByStack(BigDecimal rootRuleID, Object data, Object local)
			throws Exception {
		//Integer rootRelID = GetRootRuleID(RootRelID);
		//Integer rootRelID = RootRelID;
		
		Stack<RuleRelData> relDataStack = new Stack<RuleRelData>();

		BigDecimal parentRuleID;
		BigDecimal ruleID;
		String postRuleDesc;
		
		String ruleDesc  = "";
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
		String ruleType = ""; //0:leaf node 1:branch node 2:loop node
		String loopKey = "";
		String loopCond = "";
		String execKey = "";
		String ruleStatus = null;
		Date effectiveDate = null;
		BigDecimal version = null;
		String serviceDomain = "";
		String belongSys = "";
		String category = "";
		String createPerson = "";
		String scene = "";
		String customeCode = "";
		String note = "";
		String businessCode = "";
		String positionDesc = "";

//		String preExprCond = null;
//		Boolean isResult = false;
//		String resultDesc = "";
//		String resultPos = "";
//		String postExprCond;
//		
		Boolean preResult = true;
		Boolean result = true;
		Boolean postResult = true;
		
		/**
		 * loopCountMap:记录循环节点当前的记录集下表
		 * loopTempMap:保存循环节点过滤后的记录
		 */
		HashMap<BigDecimal, Integer> loopCountMap = new HashMap<BigDecimal, Integer>();
		HashMap<BigDecimal, List<?>> loopTempMap = new HashMap<BigDecimal, List<?>>();
		//CachedRowSet firstLevelExpr = GetChileExprData(rootRelID);
		//CachedRowSet rootLevelRelData = GetSingleRelData(rootRuleID);
		List<RuleRelData> childLevelRule = null;
		CachedRowSet childExprData = null;
		CachedRowSet ruleData = null;
		CachedRowSet postRuleData = null;
		
		RuleData rData = new RuleData();
		RuleRelData rrData = new RuleRelData();
		
		RuleRelData rootLevelRelDataList = GetSingleRelDataByHB(rootRuleID);
		relDataStack.push(rootLevelRelDataList);

		while (!relDataStack.empty()) {
			rrData = relDataStack.peek();
			
			parentRuleID = rrData.getId().getParentRuleId();
			ruleID = rrData.getId().getRuleId();
			postRuleDesc = rrData.getPostRuleDesc();

			if (ruleID != null && ruleID != BigDecimal.ZERO) {
				//ruleData = getSingleRuleData(ruleID);
				//ruleData.first();
				rData = getSingleRuleDataByHB(ruleID);
				
				ruleDesc  = rData.getRuleDesc();
				preRuleCond = rData.getPreRuleCond();
				ruleCond = rData.getRuleCond();
				postRuleCond = rData.getPostRuleCond();
				isExit = rData.getIsExit();
				isLog = rData.getIsLog();
				logDesc = rData.getLogDesc();
				isEstimate = rData.getIsEstimate();
				isShare = rData.getIsShere();
				isFeedBack = rData.getIsFeedback();
				feedBackDesc = rData.getFeedbackDesc();
				ruleType = rData.getRuleType();
				loopKey = rData.getLoopKey();
				loopCond = rData.getLoopCond();
				execKey = rData.getExecKey();
				ruleStatus = rData.getRuleStatus();
				effectiveDate = (Date) rData.getEffectiveDate();
				version = rData.getVersion();
				serviceDomain = rData.getServiceDomain();
				belongSys = rData.getBelongSys();
				category = rData.getCategory();
				createPerson = rData.getCreatePerson();
				scene = rData.getScene();
				customeCode = rData.getCustomsCode();
				note = rData.getNote();
				businessCode = rData.getBusinessCode();
				positionDesc = rData.getPositionDesc();

				if(!isEmptyString(preRuleCond)){
					preResult = (Boolean) ExecuteExpr(preRuleCond, data, local);
				}else {
					preResult = true;
				}
			}

			if (!isEmptyString(postRuleDesc)) {
				postResult = (Boolean) ExecuteExpr(postRuleDesc, data, local);
			}else {
				postResult = true;
			}
			
			if (preResult && postResult) {
				//叶节点 ruletype == 0
				if (ruleType.equals("0")) {
					relDataStack.pop();
					if (isEmptyString(ruleCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(ruleCond, data, local);
					}
					if (result == null) {
						WriteLog(ruleCond + ";" + ruleDesc + ";" + logDesc + ";result == null");
						result = false;
					}
					WriteLog(ruleID + ";" + ruleCond + ";");
					//当result 为true，并且islog为true，记录日志
					if (result) {
						if (isLog) {
							if (!isEmptyString(logDesc)) {
								WriteLog("Log Desc" + GetTransResult(logDesc, data, local));
							}
							if (!isEmptyString(positionDesc)) {
								WriteLog("result_pos:" + GetTransResult(positionDesc, data, local));
							}
						}
					}
					if (result && isExit) {
						return;
					}
				} else if (ruleType.equals("2")) {
					HashMap<String, Object> mapData = (HashMap<String, Object>) data;
					
					//List loopData = (List) mapData.get(loopKey);
					/**
					 * 如果缓存中存在当前循环节点的结果集，则从缓存中获得
					 */
					List<?> loopData;
					if (loopTempMap.containsKey(ruleID)) {
						loopData = loopTempMap.get(ruleID);
					}else {
						loopData = (List<?>)ExecuteExpr(loopKey, data, local);
					}
					if (isEmptyString(ruleCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(ruleCond, data, local);
					}
					Integer maxCount;
					Integer currentCount;
					maxCount = loopData.size();

					if (loopCountMap.get(ruleID) == null) {
						loopCountMap.put(ruleID, 0);
						currentCount = 0;
					} else {
						currentCount = loopCountMap.get(ruleID);
					}

					if (result && currentCount < maxCount) {
						mapData.put(execKey,
								(Object) loopData.get(currentCount));
						childLevelRule = GetChileExprDataByHB(parentRuleID);
						for (int i = 0; i < childLevelRule.size(); i++) {
							relDataStack.push(childLevelRule.get(i));
						}
						currentCount++;
						loopCountMap.put(parentRuleID, currentCount);
					} else {
						loopCountMap.remove(ruleID);
						loopTempMap.remove(ruleID);
						relDataStack.pop();
					}
				} else {
					//枝节点
					relDataStack.pop();	
					if (isEmptyString(ruleCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(ruleCond, data, local);
					}

					if (result) {
						childLevelRule = GetChileExprDataByHB(parentRuleID);
						for (int i = 0; i < childLevelRule.size(); i++) {
							relDataStack.push(childLevelRule.get(i));
						}
					}
				}
			} else {
				relDataStack.pop();
			}
		}
		return;
	}

	
}