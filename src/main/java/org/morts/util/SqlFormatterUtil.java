package org.morts.util;

import java.util.Optional;

public final class SqlFormatterUtil {

    public static String formatString(String sqlString) {

        if (sqlString == null) {
            return null;
        } else {
            return String.format("'%s'", sqlString);
        }
    }
}
