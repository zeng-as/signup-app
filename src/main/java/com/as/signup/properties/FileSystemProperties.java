package com.as.signup.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file-system")
@Data
public class FileSystemProperties {

    private String uploadPath;

    private String outerPrefix;
}
