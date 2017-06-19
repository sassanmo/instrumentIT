package instrumentIT;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.novatec.instrumentit.parser.ios.SwiftKeywords;
import com.novatec.instrumentit.parser.ios.SwiftParser;
import com.novatec.instrumentit.parser.Class;
import com.novatec.instrumentit.parser.Method;

public class SwiftParserTest {
	
	private SwiftParser swiftParser;
	private String sourceStringIs;

	@Before
	public void setUp() throws Exception {
		this.swiftParser = new SwiftParser();
		File file = new File(SwiftParserTest.class.getResource("TestFile.swift").getPath());
		this.sourceStringIs = Files.toString(file, Charsets.UTF_8);
	}

	@After
	public void tearDown() throws Exception {
		this.sourceStringIs = null;
	}
	
	@Test
	public void parseMethodsTest() {
		sourceStringIs = swiftParser.deleteComments(sourceStringIs);
		List<Class> classes = swiftParser.parseClasses(sourceStringIs);
		List<Method> methods = swiftParser.parseMethods(sourceStringIs, classes);
		Assert.assertEquals("Should find one method", 1, methods.size());
		Assert.assertEquals("Method signarure should be [func bla() ]", "bla() ", methods.get(0).getSignature());
		Assert.assertEquals("Class name is TestFile", "TestFile", methods.get(0).getMethodHolder().getName());
	}

	@Test
	public void parseClassTest() {
		sourceStringIs = swiftParser.deleteComments(sourceStringIs);
		List<Class> classes = swiftParser.parseClasses(sourceStringIs);
		Assert.assertEquals("Should find one class", 1, classes.size());
		Assert.assertEquals("Class name is TestFile", "TestFile", classes.get(0).getName());
		Assert.assertEquals("No children", 0, classes.get(0).getChildren().size());
	}
	
	@Test
	public void deleteCommentsTest() {
		sourceStringIs = swiftParser.deleteComments(sourceStringIs);
		Assert.assertFalse("String does not contain // Blocks", sourceStringIs.contains(SwiftKeywords.COMMENT_1));
		Assert.assertFalse("String does not contain /// Blocks", sourceStringIs.contains(SwiftKeywords.COMMENT_DOC_1));
		Assert.assertFalse("String does not contain /** Blocks", sourceStringIs.contains(SwiftKeywords.COMMENT_DOC_N_BEGIN));
		Assert.assertFalse("String does not contain */ Blocks", sourceStringIs.contains(SwiftKeywords.COMMENT_DOC_N_END));
		Assert.assertFalse("String does not contain /* Blocks", sourceStringIs.contains(SwiftKeywords.COMMENT_N_BEGIN));
	}

}
