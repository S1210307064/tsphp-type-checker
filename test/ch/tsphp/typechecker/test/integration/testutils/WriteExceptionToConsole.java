/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.exceptions.TSPHPException;

public class WriteExceptionToConsole implements IErrorLogger
{
    @Override
    public void log(TSPHPException exception) {
        System.out.println(exception.getMessage());
    }
}
