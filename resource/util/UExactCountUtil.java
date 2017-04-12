package util;

import java.math.BigDecimal;

/**
 * 精确计算工具类
 * @author ys
 *
 */
public class UExactCountUtil {

	/**
	 * 存储被动数值
	 */
	private static BigDecimal a;
	
	/**
	 * 存储主动数值
	 */
	private static BigDecimal b;
	
	/**
	 * 存储计算结果
	 */
	private static Number result;
	
	/**
	 * 存储返回的类型（被动数值类型）
	 */
	private static Class<?> clazz = null;
	
	/**
	 * 初始化数据
	 * @param passive 被动数值
	 * @param initiative 主动数值
	 */
	private static void init(Number passive, Number initiative) {
		//passive转换为BigDecimal存储
		if(passive instanceof BigDecimal) {
			a = (BigDecimal)passive;
		} else {
			clazz = passive.getClass();//如果不是BigDecimal则记录类型
			a = new BigDecimal(passive.toString());
		}
		//initiative转换为BigDecimal存储
		if(initiative instanceof BigDecimal) {
			b = (BigDecimal)initiative;
		} else {
			b = new BigDecimal(initiative.toString());
		}
	}
	
	/**
	 * 结果转换
	 */
	private static void convert() {
		//如果返回类型不为null，则将结果转换为相应的类型
		if(clazz != null) {
			if(clazz == Integer.class) {
				result = ((BigDecimal)result).intValue();
			} else if(clazz == Float.class) {
				result = ((BigDecimal)result).floatValue();
			} else if(clazz == Double.class) {
				result = ((BigDecimal)result).doubleValue();
			}
		}
	}
	
	/**
	 * 执行加法运算
	 * @param passive 被加数
	 * @param initiative 加数
	 * @return 计算结果
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T add(T passive, Number initiative) {
		init(passive, initiative);
		result = a.add(b);
		convert();
		return (T)result;
	}
	
	/**
	 * 执行减法运算
	 * @param passive 被减数
	 * @param initiative 减数
	 * @param flag true正常执行，false调换主被动数值执行
	 * @return 计算结果
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T subtract(T passive, Number initiative, boolean flag) {
		init(passive, initiative);
		if(flag) {
			result = a.subtract(b);
		} else {
			result = b.subtract(a);
		}
		convert();
		return (T)result;
	}
	
	/**
	 * 执行乘法运算
	 * @param passive 被乘数
	 * @param initiative 乘数
	 * @return 计算结果
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T multiply(T passive, Number initiative) {
		init(passive, initiative);
		result = a.multiply(b);
		convert();
		return (T)result;
	}
	
	/**
	 * 执行除法运算
	 * @param passive 被除数
	 * @param initiative 除数
	 * @param flag flag true正常执行，false调换主被动数值执行
	 * @return 计算结果
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T divide(T passive, Number initiative, boolean flag) {
		init(passive, initiative);
		try {
			if(flag) {
				result = a.divide(b);
			} else {
				result = b.divide(a);
			}
		} catch (ArithmeticException e) {//除不尽时提示错误，并将结果设为0
			result = new BigDecimal(0);
			System.err.println("计算错误");
		}
		convert();
		return (T)result;
	}
}
