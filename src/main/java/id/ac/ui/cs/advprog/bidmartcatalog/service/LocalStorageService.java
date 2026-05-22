package id.ac.ui.cs.advprog.bidmartcatalog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final String uploadDir = "uploads/";

    @Override
    public String store(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(dir.getAbsolutePath(), fileName);
        Files.write(path, file.getBytes());
        return "/uploads/" + fileName;
    }
}
