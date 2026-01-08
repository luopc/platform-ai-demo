package com.luopc.platform.demo.ai.jdk.model;

public enum JavaVersion {
    JDK_8(8),
    JDK_11(11),
    JDK_17(17),
    JDK_21(21),
    JDK_25(25);

    private final int version;

    JavaVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
