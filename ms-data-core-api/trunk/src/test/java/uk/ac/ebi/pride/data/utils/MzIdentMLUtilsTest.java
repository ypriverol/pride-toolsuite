package uk.ac.ebi.pride.data.utils;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MzIdentMLUtilsTest {

    private File mzIdentMLFile = null;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MzIdentMLUtils.class);

    @Before
    public void setUp() throws Exception {
        URL mzIdentMLFileURL = MzIdentMLUtilsTest.class.getClassLoader().getResource("harald-example.mzid");
        if (mzIdentMLFileURL == null) {
            throw new IllegalStateException("no file for input found!");
        }
        mzIdentMLFile = new File(mzIdentMLFileURL.toURI());

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testValidateMzIdentMLSchema() throws Exception {
        List<String> errors = MzIdentMLUtils.validateMzIdentMLSchema(mzIdentMLFile);
        for(String error: errors){
            logger.error(error);
        }

    }
}