package com.spicysoft.jwig.renderer;

import java.util.HashMap;

import org.apache.commons.logging.Log;

import com.spicysoft.jwig.Filter;
import com.spicysoft.jwig.Range;
import com.spicysoft.jwig.sablecc.node.AAbbreviationArrayLiteralElement;
import com.spicysoft.jwig.sablecc.node.AAddAddExpression;
import com.spicysoft.jwig.sablecc.node.AAndAndExpression;
import com.spicysoft.jwig.sablecc.node.AArrayLiteralElements;
import com.spicysoft.jwig.sablecc.node.AConcatAddExpression;
import com.spicysoft.jwig.sablecc.node.ADecimalIntegerLiteral;
import com.spicysoft.jwig.sablecc.node.ADivMulExpression;
import com.spicysoft.jwig.sablecc.node.ADotAccess;
import com.spicysoft.jwig.sablecc.node.ADoubleLiteral;
import com.spicysoft.jwig.sablecc.node.AEmptyArrayLiteral;
import com.spicysoft.jwig.sablecc.node.AEqEqExpression;
import com.spicysoft.jwig.sablecc.node.AExpression;
import com.spicysoft.jwig.sablecc.node.AFalseBooleanLiteral;
import com.spicysoft.jwig.sablecc.node.AFilterArgs;
import com.spicysoft.jwig.sablecc.node.AGeCmpExpression;
import com.spicysoft.jwig.sablecc.node.AGtCmpExpression;
import com.spicysoft.jwig.sablecc.node.AHexIntegerLiteral;
import com.spicysoft.jwig.sablecc.node.AInCmpExpression;
import com.spicysoft.jwig.sablecc.node.AKeyValueForStartStatement;
import com.spicysoft.jwig.sablecc.node.ALeCmpExpression;
import com.spicysoft.jwig.sablecc.node.ALtCmpExpression;
import com.spicysoft.jwig.sablecc.node.AMinusUnaryExpression;
import com.spicysoft.jwig.sablecc.node.AModMulExpression;
import com.spicysoft.jwig.sablecc.node.AMulMulExpression;
import com.spicysoft.jwig.sablecc.node.ANeEqExpression;
import com.spicysoft.jwig.sablecc.node.ANoargsFilterInvoke;
import com.spicysoft.jwig.sablecc.node.ANoneLiteral;
import com.spicysoft.jwig.sablecc.node.ANormalForStartStatement;
import com.spicysoft.jwig.sablecc.node.ANotNotExpression;
import com.spicysoft.jwig.sablecc.node.AOctalIntegerLiteral;
import com.spicysoft.jwig.sablecc.node.AOrOrExpression;
import com.spicysoft.jwig.sablecc.node.APowPowExpression;
import com.spicysoft.jwig.sablecc.node.AQuotientMulExpression;
import com.spicysoft.jwig.sablecc.node.ARangeRangeExpression;
import com.spicysoft.jwig.sablecc.node.ARegularArrayLiteral;
import com.spicysoft.jwig.sablecc.node.AStringLiteral;
import com.spicysoft.jwig.sablecc.node.ASubAddExpression;
import com.spicysoft.jwig.sablecc.node.ASubscriptAccess;
import com.spicysoft.jwig.sablecc.node.ATenaryTenaryExpression;
import com.spicysoft.jwig.sablecc.node.ATrueBooleanLiteral;
import com.spicysoft.jwig.sablecc.node.AVarPrimary;
import com.spicysoft.jwig.sablecc.node.AWithargsFilterInvoke;

public abstract class ExpressionEvaluationWalker extends AbstractWalker {

	protected final RenderContext context;

	public ExpressionEvaluationWalker(final Log L,final RenderContext context) {
		super(L);
		this.context = context;
	}

	@Override public void outANoargsFilterInvoke(ANoargsFilterInvoke node)
	{
		final Filter filter = context.environment().getRegisteredFilter(node.getIdent().getText());
		final Object piped = context.renderStack().pop();
		final Object result = filter.invoke(context.environment(), piped,null);
		context.renderStack().push(result);

		super.outANoargsFilterInvoke(node);
	}

	@Override public void outAWithargsFilterInvoke(AWithargsFilterInvoke node)
	{
		final int count = ((AFilterArgs)node.getFilterArgs()).getAdditionalFilterArgs().size() + 1;
		final Object[] params = new Object[count];
		for (int n = 0;n < count; n++) {
			params[count - n -1] = context.renderStack().pop();
		}
		final Object piped = context.renderStack().pop();
		final Filter filter = context.environment().getRegisteredFilter(node.getIdent().getText());
		final Object result = filter.invoke(context.environment(), piped, params);
		context.renderStack().push(result);

		super.outAWithargsFilterInvoke(node);
	}

	@Override public void inARegularArrayLiteral(ARegularArrayLiteral node)
	{
		super.inARegularArrayLiteral(node);
		context.renderStack().mark();
	}

	@Override public void outARegularArrayLiteral(ARegularArrayLiteral node)
	{
		final MixedMap map = new MixedMap();
		final int length = ((AArrayLiteralElements)node.getArrayLiteralElements()).
			getAdditionalArrayLiteralElement().size() + 1;
		for (int n = 0;n < length;n++) {
			final Object value = context.renderStack().pop();
			final Object key   = context.renderStack().pop();
			map.internalAdd(key, value);
		}
		context.renderStack().checkMark(0);
		context.renderStack().push(map);
	    super.outARegularArrayLiteral(node);
	}

	@Override public void outAAbbreviationArrayLiteralElement(AAbbreviationArrayLiteralElement node)
	{
		final Object value = context.renderStack().pop();
		context.renderStack().push(value);
		context.renderStack().push(value);
		super.outAAbbreviationArrayLiteralElement(node);
	}

	@Override public void outAEmptyArrayLiteral(AEmptyArrayLiteral node)
	{
		context.renderStack().push(new HashMap<Object,Object>());
	    super.outAEmptyArrayLiteral(node);
	}

	@Override
	public void outASubscriptAccess(ASubscriptAccess node) {
		final Object key  = context.renderStack().pop();
		final Object that = context.renderStack().pop();
		context.renderStack().push(context.variableAccessor().accessBySubscript(that, key));
	    super.outASubscriptAccess(node);
	}

	@Override
	public void outADotAccess(ADotAccess node) {
		final String key = node.getIdent().getText();
		final Object that = context.renderStack().pop();
		context.renderStack().push(context.variableAccessor().accessByDot(that, key));
	    super.outADotAccess(node);
	}

	@Override
	public void outAVarPrimary(AVarPrimary node) {
		final String key  = node.getIdent().getText();
		final Object that = context.variables();
		context.renderStack().push(context.variableAccessor().accessByDot(that, key));
	    super.outAVarPrimary(node);
	}

	@Override
	public void outAMinusUnaryExpression(AMinusUnaryExpression node) {

		final Number number = (Number)context.renderStack().pop();
		context.renderStack().push(Op.minus(number));
		super.outAMinusUnaryExpression(node);
	}

	@Override
	public void outAPowPowExpression(APowPowExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.pow(left,right));
		super.outAPowPowExpression(node);
	}

	@Override
	public void outAMulMulExpression(AMulMulExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.mul(left,right));
	    super.outAMulMulExpression(node);
	}

	@Override
	public void outADivMulExpression(ADivMulExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.div(left,right));
	    super.outADivMulExpression(node);
	}

	@Override
	public void outAModMulExpression(AModMulExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.mod(left,right));
	    super.outAModMulExpression(node);
	}

	@Override
	public void outAQuotientMulExpression(AQuotientMulExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.quotient(left,right));
	    super.outAQuotientMulExpression(node);
	}

	@Override
	public void outAAddAddExpression(AAddAddExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.add(left,right));
	    super.outAAddAddExpression(node);
	}

	@Override
	public void outASubAddExpression(ASubAddExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.sub(left,right));
	    super.outASubAddExpression(node);
	}

	@Override
	public void outAConcatAddExpression(AConcatAddExpression node) {
		final String right = context.renderStack().pop().toString();
		final String left  = context.renderStack().pop().toString();
		context.renderStack().push(left + right);
	    super.outAConcatAddExpression(node);
	}

	@Override
	public void outALtCmpExpression(ALtCmpExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.lt(left,right));
	    super.outALtCmpExpression(node);
	}

	@Override
	public void outAGtCmpExpression(AGtCmpExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.gt(left,right));
	    super.outAGtCmpExpression(node);
	}

	@Override
	public void outALeCmpExpression(ALeCmpExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.le(left,right));
	    super.outALeCmpExpression(node);
	}

	@Override
	public void outAGeCmpExpression(AGeCmpExpression node) {
		final Number right = (Number)context.renderStack().pop();
		final Number left  = (Number)context.renderStack().pop();
		context.renderStack().push(Op.ge(left,right));
	    super.outAGeCmpExpression(node);
	}

	@Override public void outAInCmpExpression(AInCmpExpression node)
	{
		final Object container = context.renderStack().pop();
		final Object find = context.renderStack().pop();
		context.renderStack().push(Op.in(find,container));
	    super.outAInCmpExpression(node);
	}

	@Override
	public void outAEqEqExpression(AEqEqExpression node) {
		final Object right = context.renderStack().pop();
		final Object left  = context.renderStack().pop();
		context.renderStack().push(Op.eq(left,right));
	    super.outAEqEqExpression(node);
	}

	@Override
	public void outANeEqExpression(ANeEqExpression node) {
		final Object right = context.renderStack().pop();
		final Object left  = context.renderStack().pop();
		context.renderStack().push(Op.ne(left,right));
	    super.outANeEqExpression(node);
	}

	@Override public void outANotNotExpression(ANotNotExpression node)
	{
		final Object expression = context.renderStack().pop();
		context.renderStack().push(Op.not((Boolean)expression));
		super.outANotNotExpression(node);
	}

	@Override
	public void outAAndAndExpression(AAndAndExpression node) {
		final Boolean right = (Boolean)context.renderStack().pop();
		final Boolean left  = (Boolean)context.renderStack().pop();
		context.renderStack().push(Op.and(left,right));
	    super.outAAndAndExpression(node);
	}

	@Override
	public void outAOrOrExpression(AOrOrExpression node) {
		final Boolean right = (Boolean)context.renderStack().pop();
		final Boolean left  = (Boolean)context.renderStack().pop();
		context.renderStack().push(Op.or(left,right));
	    super.outAOrOrExpression(node);
	}

	@Override
	public void caseATenaryTenaryExpression(ATenaryTenaryExpression node) {
	    inATenaryTenaryExpression(node);

	    node.getCondition().apply(this);
	    boolean result = Op.evaluate(context.renderStack().pop());
	    if (result) {
	    	node.getThen().apply(this);
	    } else {
	    	node.getElse().apply(this);
	    }

	    outATenaryTenaryExpression(node);
	}

	@Override
	public void outARangeRangeExpression(ARangeRangeExpression node) {
		final Object to   = context.renderStack().pop();
		final Object from = context.renderStack().pop();
		context.renderStack().push(Range.newRange(from,to));
		super.outARangeRangeExpression(node);
	}

	@Override
	public void caseAExpression(AExpression node) {
	    inAExpression(node);
	    this.context.renderStack().mark();
	    node.getSubExpression().apply(this);
	    this.context.renderStack().checkMark(1);
	    outAExpression(node);
	}

	@Override
	public void outAStringLiteral(AStringLiteral node) {
		final String token = node.getStringLiteral().getText();
		context.renderStack().push(token.substring(1,token.length()-1));
		super.outAStringLiteral(node);
	}

	@Override public void outADoubleLiteral(ADoubleLiteral node)
	{
		final double value = Double.valueOf(node.getFloatingPointLiteral().getText());
		context.renderStack().push(value);
		super.outADoubleLiteral(node);
	}

	@Override
	public void outANoneLiteral(ANoneLiteral node)
	{
		context.renderStack().push(Null.INSTANCE);
		super.outANoneLiteral(node);
	}

	@Override
	public void outAFalseBooleanLiteral(AFalseBooleanLiteral node) {
		context.renderStack().push(Boolean.FALSE);
		super.outAFalseBooleanLiteral(node);
	}

	@Override
	public void outATrueBooleanLiteral(ATrueBooleanLiteral node) {
		context.renderStack().push(Boolean.TRUE);
		super.outATrueBooleanLiteral(node);
	}

	@Override
	public void caseADecimalIntegerLiteral(ADecimalIntegerLiteral node) {
	    inADecimalIntegerLiteral(node);

	    context.renderStack().push(Integer.parseInt(node.getDecimalIntegerLiteral().getText()));

	    outADecimalIntegerLiteral(node);
	}

	@Override
	public void caseAHexIntegerLiteral(AHexIntegerLiteral node) {
	    inAHexIntegerLiteral(node);

	    context.renderStack().push(Integer.parseInt(node.getHexIntegerLiteral().getText().substring(2),16));

	    outAHexIntegerLiteral(node);
	}

	@Override
	public void caseAOctalIntegerLiteral(AOctalIntegerLiteral node) {
	    inAOctalIntegerLiteral(node);

	    context.renderStack().push(Integer.parseInt(node.getOctalIntegerLiteral().getText().substring(1),8));

	    outAOctalIntegerLiteral(node);
	}

    @Override public void caseANormalForStartStatement(ANormalForStartStatement node)
    {
    	inANormalForStartStatement(node);
        outANormalForStartStatement(node);
    }

    @Override public void caseAKeyValueForStartStatement(AKeyValueForStartStatement node)
    {
    	inAKeyValueForStartStatement(node);
    	outAKeyValueForStartStatement(node);
    }
}