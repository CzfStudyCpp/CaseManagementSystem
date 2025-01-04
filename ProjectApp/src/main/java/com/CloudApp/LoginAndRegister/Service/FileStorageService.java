package com.CloudApp.LoginAndRegister.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {

    @Value("${app.upload.directory}") // 从配置文件读取目标目录
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        // 检查并创建目录
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // 自动创建目录
        }

        // 保存文件,支持重复文件
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + File.separator + fileName;
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        return filePath;
    }
}
