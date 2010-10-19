package com.spicysoft.jwig.renderer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.JwigRuntimeException;
import com.spicysoft.jwig.Variables;

public class VariableAccessor
{
	private final static Log L = LogFactory.getLog(VariableAccessor.class);
    private final static Object ERROR = new Object();
	private static enum ACCESS_TYPE {
		BY_VARIABLES,BY_GETTER,BY_METHOD,BY_MAP,BY_FIELD,BY_SETTER,NO_MATCH,
		BY_LIST_INDEX,BY_ARRAY_INDEX,BY_ITERATE
	}
    private final WalkingContext context;

    public VariableAccessor(final WalkingContext context)
    {
    	this.context = context;
    }

    @SuppressWarnings("unchecked")
    public void setByDot(final Object that,final String key,final Object value)
    {
        if (that == null) {
            throw new IllegalArgumentException("Null Argument: that");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null Argument: key");
        }
        if (value == null) {
        	throw new IllegalArgumentException("Null Argument: value");
        }
        if (key.length() == 0) {
            throw new JwigRuntimeException(context,"'key' shouldn't be empty, but is: ''");
        }

        final ACCESS_TYPE cause;
        FIND: {
	        if (that instanceof Variables) {
	        	((Variables)that).put(key, value);
	            cause = ACCESS_TYPE.BY_VARIABLES;
	            break FIND;
	        }
        	if (that instanceof Map<?,?>) {
	            ((Map<Object,Object>)that).put(key,value);
	            cause = ACCESS_TYPE.BY_MAP;
	            break FIND;
	        }

	        final Class<?> klass = that.getClass();

	        final Field field = getField(klass, key);
	        if (field != null) {
	            if (setFieldValue(that,field,value)) {
	            	cause = ACCESS_TYPE.BY_FIELD;
	            	break FIND;
	            }
	        }

	        final Method getter = getMethod(klass,nameOfSetter(key),value.getClass());
	        if (getter != null) {
                final Object tmp = invokeMethod(that,getter,value);
                if (tmp != ERROR) {
                	cause = ACCESS_TYPE.BY_SETTER;
                	break FIND;
                }
	        }
	        cause = ACCESS_TYPE.NO_MATCH;
	        break FIND;
        }

        if (L.isTraceEnabled()) {
            L.trace("cause:" + cause + ",key:'" + key + "',value:'" + value + "',that:'" + that + "'");
        }
        if (cause == ACCESS_TYPE.NO_MATCH) {
        	throw new JwigRuntimeException(context,"Can't set value:'%s', because key:'%s' doesn't exist in that:'%s'",value,key,that);
        }
    }

    @SuppressWarnings("unchecked")
    public void setBySubscript(final Object that,final String key,final Object value)
    {
        if (that == null) {
            throw new IllegalArgumentException("Null Argument: that");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null Argument: key");
        }
        if (value == null) {
        	throw new IllegalArgumentException("Null Argument: value");
        }
        if (key.length() == 0) {
            throw new JwigRuntimeException(context,"'key' shouldn't be empty, but is: ''");
        }

        final ACCESS_TYPE cause;
        if (that instanceof Map<?,?>) {
            ((Map<Object,Object>)that).put(key,value);
            cause = ACCESS_TYPE.BY_MAP;
        } else if (that instanceof Variables) {
            ((Variables)that).put(key,value);
            cause = ACCESS_TYPE.BY_VARIABLES;
        } else {
            cause = ACCESS_TYPE.NO_MATCH;
        }
        if (L.isTraceEnabled()) {
            L.trace("value:'" + value + "',cause:" + cause + ",that:'" + that + "',key:'" + key + "'");
        }
        if (cause == ACCESS_TYPE.NO_MATCH) {
        	throw new JwigRuntimeException(context,"Can't set value:'%s', because key:'%s' doesn't exist in that:'%s'",value,key,that);
        }
    }

    /**
     * Implementation
     *
     * For convenience sake foo.bar does the following things on the PHP layer:
     * check if foo is an array and bar a valid element;
     * if not, and if foo is an object, check that bar is a valid property;
     * if not, and if foo is an object, check that bar is a valid method
     *  (even if bar is the constructor - use __construct() instead);
     *
     * if not, and if foo is an object, check that getBar is a valid method;
     * if not, return a null value.
     *
     * @SEE http://www.twig-project.org/book/02-Twig-for-Template-Designers
     * @TODO *WARNNG* 'Constructor-auto-call' is not implemented now.
     */
    public Object accessByDot(final Object that,final String key)
    {
        if (that == null) {
            throw new JwigRuntimeException(context,"Null Argument: that");
        }
        if (key == null) {
            throw new JwigRuntimeException(context,"Null Argument: key");
        }
        if (key.length() == 0) {
            throw new JwigRuntimeException(context,"Empty Argument: key");
        }

        final Object value;
        final ACCESS_TYPE cause;
        FIND: {
	        if (that instanceof Variables && ((Variables)that).contains(key,Object.class)) {
	            value = ((Variables)that).get(key);
	            cause = ACCESS_TYPE.BY_VARIABLES;
	            break FIND;
	        }

	        if (that instanceof Map<?,?> && ((Map<?,?>)that).containsKey(key)) {
	            value = ((Map<?,?>)that).get(key);
	            cause = ACCESS_TYPE.BY_MAP;
	            break FIND;
	        }

	        final Class<?> klass = that.getClass();

	        final Field field = getField(klass, key);
	        if (field != null) {
	            final Object tmp = getFieldValue(that,field);
	            if (tmp != ERROR) {
	            	value = tmp;
	            	cause = ACCESS_TYPE.BY_FIELD;
	            	break FIND;
	            }
	        }

	        final Method method = getMethod(klass, key);
	        if (method != null) {
                final Object tmp = invokeMethod(that,method);
                if (tmp != ERROR) {
                	value = tmp;
                	cause = ACCESS_TYPE.BY_METHOD;
                	break FIND;
                }
	        }

	        final Method getter = getMethod(klass,nameOfGetter(key));
	        if (getter != null) {
                final Object tmp = invokeMethod(that,getter);
                if (tmp != ERROR) {
                	value = tmp;
                	cause = ACCESS_TYPE.BY_GETTER;
                	break FIND;
                }
	        }
	        value = Null.INSTANCE;
	        cause = ACCESS_TYPE.NO_MATCH;
	        break FIND;
        }

        if (L.isTraceEnabled()) {
            L.trace("value:'" + value + "',cause:" + cause + ",key:'" + key + "',that:'" + that + "'");
        }
        if (value == null) {
        	throw new IllegalStateException("Null Returned: cause:" + cause + ",key:'" + key + "',that:'" + that + "'");
        }
        return value;
    }

    /**
     * foo['bar'] on the other hand works mostly the same with the a small difference in the order:
     * check if foo is an array and bar a valid element;
     * if not, return a null value.
     * Using the alternative syntax is also useful to dynamically get attributes from arrays:
     */
    @SuppressWarnings("unchecked")
	public Object accessBySubscript(final Object that,final Object key_)
    {
        if (that == null) {
            throw new JwigRuntimeException(context,"Null Argument: that");
        }
        if (key_ == null) {
            throw new JwigRuntimeException(context,"Null Argument: key");
        }
        final String key = key_.toString();
        if (key.length() == 0) {
            throw new JwigRuntimeException(context,"Empty Argument: key");
        }
        final Object value;
        final ACCESS_TYPE cause;
        if (key_ instanceof Number) {
        	final int ikey = ((Number)key_).intValue();
        	if (that instanceof List<?>) {
        		value = ((List<Object>)that).get(ikey);
        		cause = ACCESS_TYPE.BY_LIST_INDEX;
        	} else if (that instanceof Object[]) {
        		value = ((Object[])that)[ikey];
        		cause = ACCESS_TYPE.BY_ARRAY_INDEX;
        	} else if (that instanceof Iterable) {
        		Iterator<Object> i = ((Iterable<Object>)that).iterator();
        		for (int n = 1;n < ikey;n++) {
        			i.next();
        		}
        		value = i.next();
        		cause = ACCESS_TYPE.BY_ITERATE;
        	} else {
	            value = Null.INSTANCE;
	            cause = ACCESS_TYPE.NO_MATCH;
        	}
            if (L.isTraceEnabled()) {
                L.trace("value:'" + value + "',cause:" + cause + ",that:'" + that + "',key:'" + key + "'" + key_.getClass());
            }
            if (value == null) {
            	throw new IllegalStateException("Null Returned: cause:" + cause + ",key:'" + key + "',that:'" + that + "'");
            }
            return value;
        } else {
        	if (that instanceof Variables && ((Variables)that).contains(key,Object.class)) {
	            value = ((Variables)that).get(key);
	            cause = ACCESS_TYPE.BY_VARIABLES;
	    	} else if (that instanceof Map<?,?> && ((Map<?,?>)that).containsKey(key)) {
	            value = ((Map<?,?>)that).get(key);
	            cause = ACCESS_TYPE.BY_MAP;
	        } else {
	            value = Null.INSTANCE;
	            cause = ACCESS_TYPE.NO_MATCH;
	        }
            if (L.isTraceEnabled()) {
                L.trace("value:'" + value + "',cause:" + cause + ",that:'" + that + "',key:'" + key + "'" + key_.getClass());
            }
            if (value == null) {
            	throw new IllegalStateException("Null Returned: cause:" + cause + ",key:'" + key + "',that:'" + that + "'");
            }
            return value;
        }
    }

    /**
     * [VariableName] -> [GetterName]
     * a -> getA
     * A -> getA
     * height -> getHeight
     */
    public String nameOfGetter(final String variableName)
    {
    	return nameOfXetter("get",variableName);
    }

    public String nameOfSetter(final String variableName)
    {
    	return nameOfXetter("set",variableName);
    }

    public String nameOfXetter(final String prefix,final String variableName)
    {
        if (variableName == null) {
            throw new JwigRuntimeException(context,"Null Argument: variableName");
        }
        if (variableName.length() == 0) {
            throw new JwigRuntimeException(context,"Empty Argument: variableName");
        }

        final String nameOfGetter = prefix
            + Character.toUpperCase(variableName.charAt(0))
            + (variableName.length() == 1 ? "" : variableName.substring(1));

        if (L.isTraceEnabled()) {
          L.trace("nameOfGetter:'" + nameOfGetter + "'<-variableName:'" + variableName + "'");
        }
        return nameOfGetter;
    }

    private static boolean setFieldValue(final Object that,final Field field,final Object value)
    {
    	try {
    		field.set(that,value);
    		return true;
    	} catch(final Exception e) {
    		L.error("that:'" + that +"',field:'" + field + "'",e);
    		return false;
    	}
    }

    private static Object getFieldValue(final Object that,final Field field)
    {
    	try {
    		return field.get(that);
    	} catch(final Exception e) {
    		L.error("that:'" + that +"',field:'" + field + "'",e);
    		return ERROR;
    	}
    }

    private static Object invokeMethod(final Object that,final Method method,final Object ... args)
    {
    	try {
    		return method.invoke(that,args);
    	} catch(final Exception e) {
    		L.error("that:'" + that +"',method:'" + method + "'",e);
    		return ERROR;
    	}
    }

	private static Method getMethod(final Class<?> klass, final String key,final Class<?> ... args) {
		assert(klass != null);
		assert(key != null);
		try {
		    return  klass.getMethod(key,args);
    	} catch(final NoSuchMethodException e) {
    		if (L.isTraceEnabled()) {
    			L.trace("klass:'" + klass.toString() +"',key:'" + key + "'");
    		}
    		return null;
		} catch(final Exception e) {
			L.error("klass:'" + klass.toString() +"',key:'" + key + "'",e);
		    return null;
		}
	}

	private static Field getField(final Class<?> klass, final String key) {
		assert(klass != null);
		assert(key != null);
		try {
		    return klass.getField(key);
    	} catch(final NoSuchFieldException e) {
    		if (L.isTraceEnabled()) {
    			L.trace("klass:'" + klass.toString() +"',key:'" + key + "'");
    		}
    		return null;
		} catch(final Exception e) {
			L.error("klass:'" + klass.toString() +"',key:'" + key + "'",e);
		    return null;
		}
	}

}
