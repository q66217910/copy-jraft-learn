package com.zd.jraft.storage;

/**
 * 输出打印
 */
public interface Describer {

    void describe(final Printer out);

    interface Printer {

        /**
         * 打印
         */
        Printer print(final Object x);

        Printer println(final Object x);
    }

}
