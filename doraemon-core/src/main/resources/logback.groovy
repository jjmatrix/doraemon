import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

def logFilePath = "/apps/logs/core";

def logPattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%level] [%thread] [%logger{50}] >>> %msg%n";

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
        charset = Charset.forName("UTF-8")
    }
}
appender("FILE", RollingFileAppender) {
    file = "${logFilePath}/core.log";
    append = true;

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logFilePath}/core.log.%d{yyyy-MM-dd}";
        maxHistory = 7;
    }
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
        charset = Charset.forName("UTF-8")
    }
}

appender("FILE_ASYNC", AsyncAppender) {
    appenderRef('FILE');
    queueSize = 512;
    includeCallerData = true;
}

root(DEBUG, ["CONSOLE"]);
logger("mybatis_dao", DEBUG, ["FILE"], true);
logger("org.jmatrix", DEBUG, ["CONSOLE", "FILE"], true);
logger("org.springframework", INFO, ["FILE"], true);
logger("org.apache", WARN, ["FILE"], true);
