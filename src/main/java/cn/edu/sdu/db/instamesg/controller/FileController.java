package cn.edu.sdu.db.instamesg.controller;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    private static final String webdavServer = "http://124.70.165.173:11451/instamesg/";

    public synchronized String uploadAvatar(MultipartFile file, String username) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath("avatar/")
                .setSaveFilename(username + ".png")
                .setOriginalFilename(username + ".png")
                .upload();
        return fileInfo == null ? "error" : webdavServer + "avatar/" + fileInfo.getOriginalFilename();
    }

    @PostMapping("/upload")
    public synchronized String upload(@RequestParam MultipartFile file) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath("avatar/")
                .setSaveFilename(file.getOriginalFilename())
                .setOriginalFilename(file.getOriginalFilename())
                .upload();
        return fileInfo == null ? "上传失败！" : webdavServer + fileInfo.getUrl();
    }
}
