package com.wesabe.grendel;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.security.Security;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/*Test.class")
public class TestSuite {

    @BeforeClass
    public static void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

}