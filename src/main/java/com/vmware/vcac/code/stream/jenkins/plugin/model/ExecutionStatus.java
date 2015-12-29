package com.vmware.vcac.code.stream.jenkins.plugin.model;

import java.io.Serializable;

/**
 * Created by rsaraf on 6/18/2015.
 */
public enum ExecutionStatus implements Serializable {

    STARTED,
    COMPLETED,
    FAILED,
    NOT_STARTED,
    IN_PROGRESS,
    PAUSED,
    DELETED,
    SKIPPED,
    CANCELED;

    public String value() {
        return name();
    }

    public static ExecutionStatus fromValue(String v) {
        return valueOf(v);
    }

}