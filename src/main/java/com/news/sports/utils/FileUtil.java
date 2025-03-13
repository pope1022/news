package com.news.sports.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {
    
    public static String upload(MultipartFile file, String uploadPath) throws IOException {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新的文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        
        String realPath = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
        File targetDir = new File(realPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        // 创建目标文件
        File targetFile = new File(realPath + fileName);
        file.transferTo(targetFile);

        return fileName;
    }
    
    public static void delete(String fileName, String uploadPath) {
        File file = new File(uploadPath + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }
} 