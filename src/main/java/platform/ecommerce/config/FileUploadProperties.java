package platform.ecommerce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    private String path;        //실제 저장되는 파일 경로

    private String urlPrefix;   //URL 요청 prefix (ex : /images/)

    private String maxSize;     //최대 허용 파일 크기 (문자열로 처리해두고 나중에 검증용으로 사용)
}
