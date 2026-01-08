package com.luopc.platform.demo.ai.jdk.service;

import com.luopc.platform.demo.ai.jdk.model.JavaVersion;
import com.luopc.platform.demo.ai.jdk.model.JavaVersionInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class JavaVersionService {

    private static final String API_URL = "https://api.azul.com/metadata/v1/zulu/packages?availability_type=ca&java_package_type=jdk&archive_type=tar.gz&arch=x86&hw_bitness=64";
    private final RestClient restClient;

    public JavaVersionService() {
        this.restClient = RestClient.create();
    }

    public String getDownloadUrl(JavaVersion jdkVersion) {
        if (jdkVersion == null) return null;
        List<JavaVersionInfo> javaVersionInfos = getJavaVersions(jdkVersion);
        if (!CollectionUtils.isEmpty(javaVersionInfos)) {
            return javaVersionInfos.getFirst().getDownloadUrl();
        } else {
            return null;
        }
    }

    public List<JavaVersionInfo> getJavaVersions(JavaVersion jdkVersion) {
        if (jdkVersion == null) return null;
        String url = String.format("%s&distro_version=%s", API_URL, jdkVersion.getVersion());
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<JavaVersionInfo>>() {
                });
    }

    public static void main(String[] args) {
        JavaVersionService service = new JavaVersionService();
        List<JavaVersionInfo> javaVersionInfos = service.getJavaVersions(JavaVersion.JDK_25);
        System.out.println("JDK Version：" + javaVersionInfos);
        System.out.println("Download URL：" + service.getDownloadUrl(JavaVersion.JDK_25));
    }

}
