package test.fwb.enums;

import org.fwb.alj.en.EnumUtil;
import org.fwb.alj.en.EnumUtil.EnumName;
import org.fwb.alj.en.EnumUtil.EnumValueList;
import org.fwb.alj.en.EnumUtil.EnumValues;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

public class EnumUtilTest {
	@Test
	public void testENUM_NAME() {
		assertEquals(
				Arrays.asList("a1", "b2"),
				Lists.transform(
						Arrays.asList(EnumA.a1, EnumB.b2),
						EnumUtil.ENUM_NAME));
	}
	
	@Test
	public void testEnumName() {
		assertEquals(
				Arrays.asList("a1", "a2", "a3"),
				Lists.transform(
						Arrays.asList(EnumA.values()),
						new EnumName<EnumA>()));
	}
	
	@Test
	public void testENUM_VALUES() {
		assertEquals(
				Arrays.asList(
						new HashSet<Enum<?>>(Arrays.asList(EnumB.values())),
						new HashSet<Enum<?>>(Arrays.asList(EnumA.values()))),
				Lists.transform(
						Arrays.asList(EnumB.class, EnumA.class),
						EnumUtil.ENUM_VALUES));
	}
	
	@Test
	public void testEnumValues() {
		assertEquals(
				Arrays.asList(
						new HashSet<Enum<?>>(Arrays.asList(EnumB.values())),
						new HashSet<Enum<?>>(Arrays.asList(EnumB.values()))),
				Lists.transform(
						Arrays.asList(EnumB.class, EnumB.class),
						new EnumValues<EnumB>()));
	}
	
	@Test
	public void testENUM_VALUE_LIST() {
		assertEquals(
				Arrays.asList(
						Arrays.asList(EnumB.values()),
						Arrays.asList(EnumA.values())),
				Lists.transform(
						Arrays.asList(EnumB.class, EnumA.class),
						EnumUtil.ENUM_VALUE_LIST));
	}
	
	@Test
	public void testEnumValueList() {
		assertEquals(
				Arrays.asList(
						Arrays.asList(EnumB.values()),
						Arrays.asList(EnumB.values())),
				Lists.transform(
						Arrays.asList(EnumB.class, EnumB.class),
						new EnumValueList<EnumB>()));
	}
	
	enum EnumA { a1, a2, a3 }
	enum EnumB { b3, b2, b1 }
}
