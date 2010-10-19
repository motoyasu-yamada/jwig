package com.spicysoft.jwig.renderer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Op
{
	private static final Log L = LogFactory.getLog(Op.class);

	public final static Number minus(Number number) {
		if (number instanceof Integer) {
			return Integer.valueOf(-number.intValue());
		}
		if (number instanceof Double) {
			return Double.valueOf(-number.doubleValue());
		}
		throw new IllegalArgumentException(
				"number:'number' should be an instance of Integer or Double.");
	}

	public final static Number add(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return Integer.valueOf(left.intValue() + right.intValue());
		} else {
			return Double.valueOf(left.doubleValue() + right.doubleValue());
		}
	}

	public final static Number sub(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return Integer.valueOf(left.intValue() - right.intValue());
		} else {
			return Double.valueOf(left.doubleValue() - right.doubleValue());
		}
	}

	public final static Number mul(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return Integer.valueOf(left.intValue() * right.intValue());
		} else {
			return Double.valueOf(left.doubleValue() * right.doubleValue());
		}
	}

	public final static Number div(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return ((double)left.intValue()) / right.intValue();
		} else {
			return Double.valueOf(left.doubleValue() / right.doubleValue());
		}
	}

	public final static Number mod(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return Integer.valueOf(left.intValue() % right.intValue());
		} else {
			return Double.valueOf(left.doubleValue() % right.doubleValue());
		}
	}

	public final static Integer quotient(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return left.intValue() / right.intValue();
		} else {
			return (int)(left.doubleValue() / right.doubleValue());
		}
	}

	public final static Number pow(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return Integer.valueOf((int)java.lang.Math.pow(left.intValue(),right.intValue()));
		} else {
			return Double.valueOf((int)java.lang.Math.pow(left.doubleValue(),right.doubleValue()));
		}
	}

	public final static boolean lt(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return left.intValue() < right.intValue();
		} else {
			return left.doubleValue() < right.doubleValue();
		}
	}

	public final static boolean gt(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return left.intValue() > right.intValue();
		} else {
			return left.doubleValue() > right.doubleValue();
		}
	}

	public final static boolean le(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return left.intValue() <= right.intValue();
		} else {
			return left.doubleValue() <= right.doubleValue();
		}
	}

	public final static boolean ge(Number left, Number right)
	{
		if (asInteger(left,right)) {
			return left.intValue() >= right.intValue();
		} else {
			return left.doubleValue() >= right.doubleValue();
		}
	}

	@SuppressWarnings("unchecked")
	public final static boolean in(Object find,Object container)
	{
		if (L.isTraceEnabled()) {
			L.trace(String.format("Op.in %s(%s) in %s(%s)",find,find.getClass(),container,container.getClass()));
		}
		if (container instanceof Iterable<?>) {
			for (final Object i : ((Iterable<?>)container)) {
				if (eq(find,i)) {
					return true;
				}

			}
			return false;
		}
		if (container instanceof Map<?,?>) {
			return ((Map<Object,Object>)container).containsKey(find);
		}
		if (container instanceof String && find instanceof String) {
			return ((String)container).indexOf((String)find) != -1;
		}
		return false;
	}

	public final static boolean between(Number min, Number value,Number max)
	{
		if (asInteger(min,value,max)) {
			return min.intValue() <= value.intValue() && value.intValue() <= max.intValue();
		} else {
			return min.doubleValue() <= value.doubleValue() && value.doubleValue() <= max.doubleValue();
		}
	}

	public final static boolean eq(final Object left,final Object right)
	{
		if (left == null && right == null) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		if (left instanceof String && right instanceof String) {
			return left.equals(right);
		}
		if (left instanceof Number && right instanceof Number) {
			if (asInteger((Number)left,(Number)right)) {
				return ((Number)left).intValue()    == ((Number)right).intValue();
			} else {
				return ((Number)left).doubleValue() == ((Number)right).doubleValue();
			}
		}
		return left.equals(right);
	}

	public final static boolean not(final Boolean left)
	{
		if (left == null) {
			throw new IllegalArgumentException("Null Argument: left");
		}
		return ! left;
	}

	public final static boolean and(final Boolean left,final Boolean right)
	{
		if (left == null) {
			throw new IllegalArgumentException("Null Argument: left");
		}
		if (right == null) {
			throw new IllegalArgumentException("Null Argument: right");
		}
		return left && right;
	}

	public final static boolean or(final Boolean left,final Boolean right)
	{
		if (left == null) {
			throw new IllegalArgumentException("Null Argument: left");
		}
		if (right == null) {
			throw new IllegalArgumentException("Null Argument: right");
		}
		return left || right;
	}

	public final static boolean ne(final Object left,final Object right)
	{
		return !eq(left,right);
	}

	public final static boolean evaluate(final Object object)
	{
		if (object == null) {
			return false;
		}
		if (object instanceof Boolean) {
			return (Boolean)object;
		}
		if (object instanceof Number) {
			return ((Number)object).intValue() != 0;
		}
		if (object instanceof String) {
			return !((String)object).isEmpty();
		}
		return true;
	}

	private final static boolean asInteger(Number ... args)
	{
		for (Number n : args) {
			if(n instanceof Double || n instanceof Float || n instanceof BigDecimal) {
				return false;
			}
			if (n instanceof Integer || n instanceof Long ||
				n instanceof Short   || n instanceof Byte || n instanceof BigInteger) {
				continue;
			}
			throw new IllegalArgumentException("n:'" + n + "' should be an instance of Number.");
		}
		return true;
	}
}
