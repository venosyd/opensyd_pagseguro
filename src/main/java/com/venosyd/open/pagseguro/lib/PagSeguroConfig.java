package com.venosyd.open.pagseguro.lib;

import com.venosyd.open.commons.util.ConfigReader;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public class PagSeguroConfig extends ConfigReader {

    /** */
    public PagSeguroConfig() {
        super("pagseguro");
    }

    /** */
    public PagSeguroConfig(String file) {
        super("pagseguro-" + file);
    }
}