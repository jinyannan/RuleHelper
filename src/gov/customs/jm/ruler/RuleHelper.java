package gov.customs.jm.ruler;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
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
	
	@Deprecated
	private static Object ExecuteExprRecursion(Integer rel_id, Integer expr_id,
			Object data) throws SQLException {

		if (isGo) {
			CachedRowSet exprData = getSingleRuleData(expr_id);
			exprData.next();
			Boolean resultBoolean = true;
			Boolean isExitBoolean = exprData.getBoolean("is_exit");
			Boolean isLoop = exprData.getBoolean("is_loop");
			Boolean isLeaf = exprData.getBoolean("is_leaf");
			String loopKey = exprData.getString("loop_key");
			String execKey = exprData.getString("exec_key");

			try {
				if (isLoop) {
					HashMap mapData = (HashMap) data;
					List loopList = (List) mapData.get(loopKey);
					for (int i = 0; i < (loopList.size()); i++) {
						mapData.put(execKey, (Object) loopList.get(i));
						CachedRowSet childExpr = GetChileExprData(rel_id);
						while (childExpr.next()) {
							Integer relID = childExpr.getInt("rel_id");
							Integer exprID = childExpr.getInt("child_expr_id");
							ExecuteExprRecursion(rel_id, exprID, data);
						}
					}

				} else if (isLeaf) {
					// resultBoolean = (Boolean) ExecuteExpr(exprData
					// .getString("expr_cond"));
					// if(isExitBoolean && !resultBoolean)
					// //如果该叶节点为Exit节点，当返回结果为false时，退出整个递归
					// isGo = false;
					System.out.println(exprData.getString("expr_desc"));
				} else {
					CachedRowSet childExpr = GetChileExprData(rel_id);
					while (childExpr.next()) {
						Integer relID = childExpr.getInt("rel_id");
						Integer exprID = childExpr.getInt("child_expr_id");
						System.out.println("rel_id = " + rel_id + ";expr_id = "
								+ expr_id);
						ExecuteExprRecursion(rel_id, exprID, data);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
		return null;
	}

	private static Integer GetRootRuleID(Integer rule_id) throws Exception {
		CachedRowSet rs = GetDataBySql("select * from expr_rel where rule_id = "
				+ rule_id + " and parent_rel_id is null");
		rs.first();
		return rs.getInt("rel_id");
	}

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

	private static CachedRowSet GetChileExprData(Integer parentRelID)
			throws SQLException {
		return GetDataBySql("select * from expr_rel where parent_rel_id = "
				+ parentRelID + " order by expr_order desc");
	}
	
	private static CachedRowSet GetExprDataByRelID(Integer relID)
			throws SQLException {
		Integer exprID;
		CachedRowSet relDataCachedRowSet = GetDataBySql("select * from expr_rel where parent_rel_id = " + relID);
		if (relDataCachedRowSet.next()) {
			exprID = relDataCachedRowSet.getInt("EXPR_ID");
			return getSingleRuleData(exprID);
		}else {
			return null;
		}
	}
	private static CachedRowSet GetSingleRelData(Integer relID)
			throws SQLException {
		return GetDataBySql("select * from expr_rel where rel_id = "
				+ relID);
	}
	
	private static CachedRowSet getSingleRuleData(Integer exprID)
			throws SQLException {
		return GetDataBySql("select * from expr_data where expr_id = " + exprID);
	}

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


	private static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	private static String colToMethodName(String s) {
		String[] strs = s.toLowerCase().split("_");
		String result = "";
		for (int i = 0; i < strs.length; i++) {
			result += toUpperCaseFirstOne(strs[i]);
		}
		return result;
	}
	
//	public static void printExprData(){
//		ExprData data = new ExprDataHome().findById(new BigDecimal(1));
//		System.out.println(data.getExprDesc());
//		
//	}
	private static boolean isEmptyString(String str)
    {
        return str == null || str.trim().length() == 0;
    }
	
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
	
	public static void ExecuteRuleByStack(Integer rootRuleID, Object data)
			throws Exception {
		//Integer rootRelID = GetRootRuleID(RootRelID);
		//Integer rootRelID = RootRelID;
		
		Stack<Integer[]> relDataStack = new Stack<Integer[]>();
		Integer relID;
		Integer parentRelID;
		Integer parentExprID;
		Integer exprID;
		Integer postExprID;
		Boolean isExit = null;
		Boolean isLoop = null;
		Boolean isLeaf = null;
		String loopKey = null;
		String execKey = null;
		String exprCond = null;
		String preExprCond = null;
		Boolean isResult = false;
		String resultDesc = "";
		String resultPos = "";
		String postExprCond;
		Boolean preResult = true;
		Boolean result = true;
		Boolean postResult = true;
		HashMap<Integer, Integer> loopCountMap = new HashMap<Integer, Integer>();
		//CachedRowSet firstLevelExpr = GetChileExprData(rootRelID);
		CachedRowSet rootLevelRelData = GetSingleRelData(rootRuleID);
		CachedRowSet childLevelExpr = null;
		CachedRowSet childExprData = null;
		CachedRowSet exprData = null;
		CachedRowSet postExprData = null;

		if (rootLevelRelData.next()) {
			Integer[] relDataArrayIntegers = new Integer[] {
					rootLevelRelData.getInt("rel_id"),
					rootLevelRelData.getInt("parent_rel_id"),
					rootLevelRelData.getInt("parent_expr_id"),
					rootLevelRelData.getInt("expr_id"),
					rootLevelRelData.getInt("post_expr_id") };
			relDataStack.push(relDataArrayIntegers);
			//rootLevelRelData.close();
		}else {
			System.out.println("该rel_id对应expr_id不存在！");
			return;
		}

		while (!relDataStack.empty()) {
			Integer[] relDataArr = relDataStack.peek();
			relID = relDataArr[0];
			parentRelID = relDataArr[1];
			parentExprID = relDataArr[2];
			exprID = relDataArr[3];
			postExprID = relDataArr[4];

			if (exprID != null && exprID != 0) {
				exprData = getSingleRuleData(exprID);
				exprData.first();
				preExprCond = exprData.getString("pre_expr_cond");
				exprCond = exprData.getString("expr_cond");
				isExit = exprData.getBoolean("is_exit");
				isLoop = exprData.getBoolean("is_loop");
				isLeaf = exprData.getBoolean("is_leaf");
				loopKey = exprData.getString("loop_key");
				execKey = exprData.getString("exec_key");
				isResult = exprData.getBoolean("is_result");
				resultDesc = exprData.getString("result_desc");
				resultPos = exprData.getString("result_pos");
				//exprData.close();
				if(!isEmptyString(preExprCond)){
					preResult = (Boolean) ExecuteExpr(preExprCond, data);
				}else {
					preResult = true;
				}
			}
			
			if (postExprID != null && postExprID != 0) {
				postExprData = getSingleRuleData(postExprID);
				postExprData.first();
				postExprCond = postExprData
						.getString("expr_cond");
				postExprData.close();
				if (!isEmptyString(postExprCond)) {
					postResult = (Boolean) ExecuteExpr(postExprCond, data);
				}else {
					postResult = true;
				}
			}else {
				postResult = true;
			}
			
			if (preResult && postResult) {
				if (isLeaf) {
					relDataStack.pop();
					if (isEmptyString(exprCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(exprCond, data);
					}
					if (result == null) {
						WriteLog(exprData.getString("expr_cond") + ";" + exprData.getString("expr_desc") + ";" + exprData.getString("result_desc") + ";result == null");
						result = false;
					}
					WriteLog(exprData.getString("expr_id") + ";" + exprData.getString("expr_desc") + ";");
					if (result) {
						if (isResult) {
							if (!isEmptyString(resultDesc)) {
								WriteLog("result_desc:" + GetTransResult(resultDesc, data));
							}
							if (!isEmptyString(resultPos)) {
								WriteLog("result_pos:" + GetTransResult(resultPos, data));
							}
						}
					}
					if (result && isExit) {
						return;
					}
				} else if (isLoop) {
					HashMap<String, Object> mapData = (HashMap<String, Object>) data;
					List loopData = (List) mapData.get(loopKey);
					if (isEmptyString(exprCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(exprCond, data);
					}
					Integer maxCount;
					Integer currentCount;
					maxCount = loopData.size();

					if (loopCountMap.get(relID) == null) {
						loopCountMap.put(exprID, 0);
						currentCount = 0;
					} else {
						currentCount = loopCountMap.get(relID);
					}

					if (result && currentCount < maxCount) {
						mapData.put(execKey,
								(Object) loopData.get(currentCount));
						childLevelExpr = GetChileExprData(relID);
						while (childLevelExpr.next()) {
							Integer[] relDataArrayIntegers = new Integer[] {
									childLevelExpr.getInt("rel_id"),
									childLevelExpr.getInt("parent_rel_id"),
									childLevelExpr.getInt("parent_expr_id"),
									childLevelExpr.getInt("expr_id"),
									childLevelExpr.getInt("post_expr_id") };
							relDataStack.push(relDataArrayIntegers);
						} 
						currentCount++;
						loopCountMap.put(relID, currentCount);
					} else {
						loopCountMap.remove(relID);
						relDataStack.pop();
					}
				} else {
					relDataStack.pop();	
					if (isEmptyString(exprCond)) {
						result = true;
					}else {
						result = (Boolean) ExecuteExpr(exprCond, data);
					}

					if (result) {
						childLevelExpr = GetChileExprData(relID);
						while (childLevelExpr.next()) {
							Integer[] relDataArrayIntegers = new Integer[] {
									childLevelExpr.getInt("rel_id"),
									childLevelExpr.getInt("parent_rel_id"),
									childLevelExpr.getInt("parent_expr_id"),
									childLevelExpr.getInt("expr_id"),
									childLevelExpr.getInt("post_expr_id") };
							relDataStack.push(relDataArrayIntegers);
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