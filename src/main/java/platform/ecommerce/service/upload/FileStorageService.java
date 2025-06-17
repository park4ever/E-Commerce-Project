package platform.ecommerce.service.upload;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file, String subDirectory);  //파일 저장 후 URL 반환

    void delete(String relativePath);                       //저장된 파일 삭제
}
