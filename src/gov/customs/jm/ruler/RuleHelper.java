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

import gov.customs.jm.data.*;
//import Data.BhlHead;
//import Data.BhlList;
//import Data.EmsAlcWork;
//import Data.ExprData;
//import Data.ExprDataHome;
//import Data.PreBhlHead;
//import Data.PreBhlList;
import ExpressionHelper.EntryHelper;
//import org.glassfish.tyrus.core.PrimitiveDecoders.BooleanDecoder;

//import Data.BhlHead;
//import Data.BhlList;
//import Data.EmsAlcWork;
//import Data.ExprData;
//import Data.ExprDataHome;
//import Data.PreBhlHead;
//import Data.PreBhlList;
//import ExpressionHelper.EntryHelper;

public class RuleHelper {
	private static Boolean isGo = true;
	
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
	
	public static Connection getConnection() throws Exception {
		return getOracleConnection();
	}

	private static Connection getOracleConnection() {
		String dbUrl = "jdbc:oracle:thin:@192.168.1.16:1521:h2010";
		String theUser = "allsys";
		String thePw = "allsysnew";
		Connection c = null;
		Statement conn;
		ResultSet rs = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			c = DriverManager.getConnection(dbUrl, theUser, thePw);
			// conn = c.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	private static CachedRowSet getExprRelAllData(BigInteger rule_id)
			throws SQLException {
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CachedRowSet crs = null;
		try {
			con = getConnection();
			statement = con.createStatement();
			String conditonSql = "select * from expr_rel where rule_id = "
					+ rule_id;
			resultSet = statement.executeQuery(conditonSql);
			crs = new com.sun.rowset.CachedRowSetImpl();
			crs.populate(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (statement != null) {
				statement.close();
			}
			if (con != null)
				con.close();
		}
		return crs;
	}

	private static Object runExpr(BigInteger exprID) {
		// 此处根据表达式id计算表达式
		return (Object) true;
	}


	private static Integer GetRootRuleID(Integer rule_id) throws Exception {
		CachedRowSet rs = GetDataBySql("select * from expr_rel where rule_id = "
				+ rule_id + " and parent_rel_id is null");
		rs.first();
		return rs.getInt("rel_id");
	}

	@Deprecated
	private static CachedRowSet GetDataBySql(String sql) throws SQLException {
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CachedRowSet crs = null;
		try {
			con = getConnection();
			statement = con.createStatement();
			String conditonSql = sql;
			resultSet = statement.executeQuery(conditonSql);
			crs = new com.sun.rowset.CachedRowSetImpl();
			crs.populate(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (statement != null) {
				statement.close();
			}
			if (con != null)
				con.close();
		}
		return crs;
	}

	/**
	 * 根据父节点返回子节点list
	 * @param parentRuleID
	 * @return
	 */
	private static List<RuleRelData> GetChileExprDataByHB(BigDecimal parentRuleID){
		RuleRelData rrd = new RuleRelData();
		RuleRelDataId rrdid = new RuleRelDataId();
		rrdid.setParentRuleId(parentRuleID);
		rrd.setId(rrdid);
		return new RuleRelDataHome().findByExample(rrd); 
	}
	
//	/*
//	 * 根据
//	 */
//	@Deprecated
//	private static CachedRowSet GetExprDataByRelID(Integer relID)
//			throws SQLException {
//		Integer exprID;
//		CachedRowSet relDataCachedRowSet = GetDataBySql("select * from expr_rel where parent_rel_id = " + relID);
//		if (relDataCachedRowSet.next()) {
//			exprID = relDataCachedRowSet.getInt("EXPR_ID");
//			return getSingleRuleData(exprID);
//		}else {
//			return null;
//		}
//	}
	
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
	private static Object ExecuteExpr(String exprCond, Object data) {
		String result = "";
		Object m = null;
//		Expression expObj = new Expression(exprCond,
//				(IGetValue) new EntryHelper());
//		try {
//			expObj.Compile();
//			if (data == null) {
//				data = (Object) ExecRuleHelper.getTestData();
//			}
//			m = expObj.Calculate(data);
//			if (m == null) {
//				result = "error";
//			} else {
//				result = m.toString();
//			}
//		} catch (Exception e) {
//			result = "error";
//		}
		return m;
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
	private static String GetTransResult(String beforTransResult, Object data) {
		String regex = "(?<=\\{)[^{}]+(?=\\})";
		//beforeResult = "报关单号为{$PRI_LIST.G_NO},G_NO为{$PRI_HEAD.G_TYPE}数据出现错误";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(beforTransResult);
		String afterTransResult = "";
		int location = 0;
		String result = "";
		while (matcher.find()) {
			String temp = beforTransResult.substring(matcher.start(), matcher.end());
			result = String.valueOf(ExecuteExpr(temp, data));
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
	public static void ExecuteRuleByStack(BigDecimal rootRuleID, Object data)
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
		
		Boolean isLoop = null;
		Boolean isLeaf = null;
//		String preExprCond = null;
//		Boolean isResult = false;
//		String resultDesc = "";
//		String resultPos = "";
//		String postExprCond;
//		
		Boolean preResult = true;
		Boolean result = true;
		Boolean postResult = true;
		
		HashMap<BigDecimal, Integer> loopCountMap = new HashMap<BigDecimal, Integer>();
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

//		if (rootLevelRelData.next()) {
//			Integer[] relDataArrayIntegers = new Integer[] {
//					rootLevelRelData.getInt("parent_rule_id"),
//					rootLevelRelData.getInt("rule_id"),
//					rootLevelRelData.getString("post_rule_desc") };
//			relDataStack.push(relDataArrayIntegers);
//			//rootLevelRelData.close();
//		}else {
//			System.out.println("该rel_id对应expr_id不存在！");
//			return;
//		}

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
					preResult = (Boolean) ExecuteExpr(preRuleCond, data);
				}else {
					preResult = true;
				}
			}

			if (!isEmptyString(postRuleDesc)) {
				postResult = (Boolean) ExecuteExpr(postRuleDesc, data);
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
						result = (Boolean) ExecuteExpr(ruleCond, data);
					}
					if (result == null) {
						WriteLog(ruleData.getString("expr_cond") + ";" + ruleData.getString("expr_desc") + ";" + ruleData.getString("result_desc") + ";result == null");
						result = false;
					}
					WriteLog(ruleData.getString("expr_id") + ";" + ruleData.getString("expr_desc") + ";");
					//当result 为true，并且islog为true，记录日志
					if (result) {
						if (isLog) {
							if (!isEmptyString(logDesc)) {
								WriteLog("Log Desc" + GetTransResult(logDesc, data));
							}
							if (!isEmptyString(positionDesc)) {
								WriteLog("result_pos:" + GetTransResult(positionDesc, data));
							}
						}
					}
					if (result && isExit) {
						return;
					}
				} else if (ruleType.equals("2")) {
					HashMap<String, Object> mapData = (HashMap<String, Object>) data;
					List loopData = (List) mapData.get(loopKey);
					if (isEmptyString(ruleCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(ruleCond, data);
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
//						while (childLevelExpr.next()) {
//							Integer[] relDataArrayIntegers = new Integer[] {
//									childLevelExpr.getInt("rel_id"),
//									childLevelExpr.getInt("parent_rel_id"),
//									childLevelExpr.getInt("parent_expr_id"),
//									childLevelExpr.getInt("expr_id"),
//									childLevelExpr.getInt("post_expr_id") };
//							relDataStack.push(relDataArrayIntegers);
//						} 
						
						for (int i = 0; i < childLevelRule.size(); i++) {
							relDataStack.push(childLevelRule.get(i));
						}
						currentCount++;
						loopCountMap.put(parentRuleID, currentCount);
					} else {
						loopCountMap.remove(parentRuleID);
						relDataStack.pop();
					}
				} else {
					//枝节点
					relDataStack.pop();	
					if (isEmptyString(ruleCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(ruleCond, data);
					}

					if (result) {
//						childLevelExpr = GetChileExprData(relID);
//						while (childLevelExpr.next()) {
//							Integer[] relDataArrayIntegers = new Integer[] {
//									childLevelExpr.getInt("rel_id"),
//									childLevelExpr.getInt("parent_rel_id"),
//									childLevelExpr.getInt("parent_expr_id"),
//									childLevelExpr.getInt("expr_id"),
//									childLevelExpr.getInt("post_expr_id") };
//							relDataStack.push(relDataArrayIntegers);
//						}
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