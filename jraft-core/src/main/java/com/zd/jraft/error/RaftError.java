package com.zd.jraft.error;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum RaftError {

    /**
     * 未知错误
     */
    UNKNOWN(-1),

    /**
     * Success
     */
    SUCCESS(0),

    /**
     * 任务停止
     */
    ESTOP(1001),

    /**
     * 内部异常
     */
    EINTERNAL(1004),

    /**
     * 许可
     */
    EPERM(1008),

    /**
     * 服务器繁忙
     */
    EBUSY(1009),

    /**
     * IO error
     */
    EIO(1014),

    /**
     * shutdown
     */
    EN_ODE_SHUTDOWN(10006);

    private final int value;

    private static final Map<Integer, RaftError> RAFT_ERROR_MAP =
            Arrays.stream(RaftError.values())
                    .collect(Collectors
                            .toMap(RaftError::getNumber, Function.identity(), (a, b) -> a));


    public static RaftError forNumber(final int value) {
        return RAFT_ERROR_MAP.getOrDefault(value, UNKNOWN);
    }

    public static String describeCode(final int code) {
        RaftError e = forNumber(code);
        return e != null ? e.name() : "<Unknown:" + code + ">";
    }

    public final int getNumber() {
        return this.value;
    }

    RaftError(final int value) {
        this.value = value;
    }

}
