package com.venosyd.open.pagseguro.lib.entities;

import java.util.Map;

import com.venosyd.open.entities.infra.SerializableEntity;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public class Transaction extends SerializableEntity {

    private String type;

    private String code;

    private Map<String, Object> metadata;

    public Transaction() {
        setCollection_key("Transaction");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

}
