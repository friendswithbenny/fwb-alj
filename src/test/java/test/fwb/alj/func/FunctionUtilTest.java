package test.fwb.alj.func;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.fwb.alj.col.ListIndex.ListIndices.FixedListIndices;
import org.fwb.alj.func.FunctionUtil.CallCallable;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

public class FunctionUtilTest {
	static final Function<Callable<Integer>, Integer> CALL_INT = new CallCallable<Integer>();
	
	@Test
	public void testCallCallable() {
		CountingCallable cc = new CountingCallable();
		List<Integer> l = Lists.transform(
				Collections.nCopies(5, cc),
				CALL_INT);
		assertEquals(0, cc.i);
		assertEquals(new FixedListIndices(5), l);
		assertEquals(5, cc.i);
		assertEquals(Arrays.asList(5, 6, 7, 8, 9), l);
		assertEquals(10, cc.i);
	}
	
	static class CountingCallable implements Callable<Integer> {
		private int i = 0;
		@Override
		synchronized
		public Integer call() throws Exception {
			return i ++;
		}
	}
}
