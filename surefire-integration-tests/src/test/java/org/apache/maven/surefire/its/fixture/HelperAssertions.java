package org.apache.maven.surefire.its.fixture;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

@SuppressWarnings( { "JavaDoc" } )
public class HelperAssertions
{
    /**
     * assert that the reports in the specified testDir have the right summary statistics
     */
    public static void assertTestSuiteResults( int total, int errors, int failures, int skipped, File testDir )
    {
        IntegrationTestSuiteResults suite = parseTestResults( new File[]{ testDir } );
        assertTestSuiteResults( total, errors, failures, skipped, suite );
    }

    /**
     * assert that the reports in the specified testDir have the right summary statistics
     */
    public static void assertIntegrationTestSuiteResults( int total, int errors, int failures, int skipped,
                                                          File testDir )
    {
        IntegrationTestSuiteResults suite = parseIntegrationTestResults( new File[]{ testDir } );
        assertTestSuiteResults( total, errors, failures, skipped, suite );
    }

    public static void assertTestSuiteResults( int total, int errors, int failures, int skipped,
                                               IntegrationTestSuiteResults actualSuite )
    {
        Assert.assertEquals( "wrong number of tests", total, actualSuite.getTotal() );
        Assert.assertEquals( "wrong number of errors", errors, actualSuite.getErrors() );
        Assert.assertEquals( "wrong number of failures", failures, actualSuite.getFailures() );
        Assert.assertEquals( "wrong number of skipped", skipped, actualSuite.getSkipped() );
    }

    public static IntegrationTestSuiteResults parseTestResults( File[] testDirs )
    {
        List<ReportTestSuite> reports = extractReports( testDirs );
        return parseReportList( reports );
    }

    public static IntegrationTestSuiteResults parseIntegrationTestResults( File[] testDirs )
    {
        List<ReportTestSuite> reports = extractITReports( testDirs );
        return parseReportList( reports );
    }

    /**
     * Converts a list of ReportTestSuites into an IntegrationTestSuiteResults object, suitable for summary assertions
     */
    public static IntegrationTestSuiteResults parseReportList( List<ReportTestSuite> reports )
    {
        Assert.assertTrue( "No reports!", reports.size() > 0 );
        int total = 0, errors = 0, failures = 0, skipped = 0;
        for ( ReportTestSuite report : reports )
        {
            total += report.getNumberOfTests();
            errors += report.getNumberOfErrors();
            failures += report.getNumberOfFailures();
            skipped += report.getNumberOfSkipped();
        }
        return new IntegrationTestSuiteResults( total, errors, failures, skipped );
    }

    public static List<ReportTestSuite> extractReports( File[] testDirs )
    {
        List<File> reportsDirs = new ArrayList<File>();
        for ( File testDir : testDirs )
        {
            File reportsDir = new File( testDir, "target/surefire-reports" );
            Assert.assertTrue( "Reports directory is missing: " + reportsDir.getAbsolutePath(), reportsDir.exists() );
            reportsDirs.add( reportsDir );
        }
        SurefireReportParser parser = new SurefireReportParser( reportsDirs );
        List<ReportTestSuite> reports;
        try
        {
            reports = parser.parseXMLReportFiles();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Couldn't parse XML reports", e );
        }
        return reports;
    }

    public static List<ReportTestSuite> extractITReports( File[] testDirs )
    {
        List<File> reportsDirs = new ArrayList<File>();
        for ( File testDir : testDirs )
        {
            File reportsDir = new File( testDir, "target/failsafe-reports" );
            Assert.assertTrue( "Reports directory is missing: " + reportsDir.getAbsolutePath(), reportsDir.exists() );
            reportsDirs.add( reportsDir );
        }
        SurefireReportParser parser = new SurefireReportParser( reportsDirs );
        List<ReportTestSuite> reports;
        try
        {
            reports = parser.parseXMLReportFiles();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Couldn't parse XML reports", e );
        }
        return reports;
    }
}
