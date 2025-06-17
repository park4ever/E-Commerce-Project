package platform.ecommerce.service.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.config.FileUploadProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileUploadProperties properties;

    @Override
    public String store(MultipartFile file, String subDirectory) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);
            String fileName = UUID.randomUUID() + "." + extension;

            Path targetDir = Paths.get(properties.getPath(), subDirectory);
            Files.createDirectories(targetDir); //하위 디렉토리 없으면 생성

            Path targetPath = targetDir.resolve(fileName);
            file.transferTo(targetPath.toFile());

            // ex) /images/item/uuid.jpg
            return properties.getUrlPrefix() + subDirectory + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패 : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String relativePath) {
        try {
            Path filePath = Paths.get(properties.getPath()).resolve(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패 : " + e.getMessage(), e);
        }
    }

    private String getExtension(String fileName) {
        int dotIdx = fileName.lastIndexOf('.');
        return (dotIdx > 0) ? fileName.substring(dotIdx + 1) : "bin";
    }
}
