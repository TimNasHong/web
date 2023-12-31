package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        try {
            String originFilename=file.getOriginalFilename();
            String extension=originFilename.substring(originFilename.lastIndexOf("."));
            String fileUrl=UUID.randomUUID().toString()+extension;
            String fileObject=aliOssUtil.upload(file.getBytes(),fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
