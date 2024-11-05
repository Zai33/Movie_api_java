package com.kyawgyi.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {

//        get name of the file
        String fileName = file.getOriginalFilename();

//        to get file path
        String filePath = path + File.separator + fileName;

//        file object or create folder for file
        File newFile = new File(path);
        if (!newFile.exists()) {
            newFile.mkdir();
        }

//        copy the file or upload the file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath));


        return fileName;
    }

    @Override
    public InputStream getFile(String path, String filename) throws FileNotFoundException {

        String filePath = path + File.separator + filename;

        return new FileInputStream(filePath);
    }
}
