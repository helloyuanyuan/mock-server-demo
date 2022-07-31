package com.solera.global.qa.mockservertest.utils.junit.logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import com.solera.global.qa.mockservertest.utils.junit.extension.TimingExtension;

@Tag("timed")
@ExtendWith(TimingExtension.class)
interface TimeExecutionLogger {
}
