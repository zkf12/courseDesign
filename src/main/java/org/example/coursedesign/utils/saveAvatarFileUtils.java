package org.example.coursedesign.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class saveAvatarFileUtils {
    /**
     * 上传图片文件到本地目录
     * @param file 上传的文件
     * @param uploadDir 上传目录 (如: "uploads/avatars/")
     * @param allowedExtensions 允许的文件扩展名 (如: new String[]{"jpg", "jpeg", "png"})
     * @param maxSize 最大文件大小 (字节)
     * @param dir 上传目录中的保存文件夹
     * @return 返回文件访问路径
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 文件验证失败异常
     */
    public static String uploadImage(MultipartFile file, String uploadDir,
                                     String[] allowedExtensions, long maxSize, String dir)
            throws IOException, IllegalArgumentException {

        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 2. 验证文件大小
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        // 3. 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new IllegalArgumentException("只支持 " + String.join(", ", allowedExtensions) + " 格式的文件");
        }

        // 4. 创建上传目录(如果不存在)
        Path uploadPath = Paths.get(uploadDir + dir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        System.out.println(uploadPath);

        // 5. 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // 6. 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        System.out.println(filePath);
        file.transferTo(filePath.toFile());

        // 7. 返回文件访问路径 (可根据实际需求调整)
        return dir + newFilename;
    }

    /**
     * 删除本地图片文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteImage(String filePath) {
        try {
            // 确保路径格式正确
            Path path = Paths.get(filePath.replace("\\", "/")); // 将反斜杠替换为正斜杠
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
}
