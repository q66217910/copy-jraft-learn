package com.zd.jraft.common;

/**
 * 描述自身状态和输出状态
 */
public interface Describer {

    void describe(final Printer out);

    interface Printer {

        /**
         * Prints an object.
         *
         * @param x The <code>Object</code> to be printed
         * @return this printer
         */
        Printer print(final Object x);

        /**
         * Prints an Object and then terminates the line.
         *
         * @param x The <code>Object</code> to be printed.
         * @return this printer
         */
        Printer println(final Object x);
    }

}
