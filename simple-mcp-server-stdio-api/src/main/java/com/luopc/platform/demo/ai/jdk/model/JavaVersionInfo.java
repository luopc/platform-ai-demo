package com.luopc.platform.demo.ai.jdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JavaVersionInfo {

    @JsonProperty("availability_type")
    private String availabilityType;

    @JsonProperty("distro_version")
    private List<Integer> distroVersion;

    @JsonProperty("download_url")
    private String downloadUrl;

    @JsonProperty("java_version")
    private List<Integer> javaVersion;

    private Boolean latest;

    private String name;

    @JsonProperty("openjdk_build_number")
    private Integer openjdkBuildNumber;

    @JsonProperty("package_uuid")
    private String packageUuid;

    private String product;

    public JavaVersionInfo() {
    }

    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    public List<Integer> getDistroVersion() {
        return distroVersion;
    }

    public void setDistroVersion(List<Integer> distroVersion) {
        this.distroVersion = distroVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<Integer> getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(List<Integer> javaVersion) {
        this.javaVersion = javaVersion;
    }

    public Boolean getLatest() {
        return latest;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpenjdkBuildNumber() {
        return openjdkBuildNumber;
    }

    public void setOpenjdkBuildNumber(Integer openjdkBuildNumber) {
        this.openjdkBuildNumber = openjdkBuildNumber;
    }

    public String getPackageUuid() {
        return packageUuid;
    }

    public void setPackageUuid(String packageUuid) {
        this.packageUuid = packageUuid;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}

