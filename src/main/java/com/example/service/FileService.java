package com.example.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class FileService {
    public void makeThumbnail(String savePath, String saveName, int width, int height, String suffix) throws IOException {
        File newFile = new File(savePath + File.separator + saveName);

        int orientation = 1; // 회전정보, 1. 0도, 3. 180도, 6. 270도, 8. 90도 회전한 정보

        Metadata metadata; // 이미지 메타 데이터 객체
        Directory directory;  // 이미지의 Exif 데이터를 읽기 위한 객체

        try {
            metadata = ImageMetadataReader.readMetadata(newFile);
            directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null) {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
            }

        } catch (Exception e) {
            orientation = 1;
        }

        // 저장된 원본파일로부터 BufferedImage 객체를 생성합니다.
        BufferedImage srcImg = ImageIO.read(newFile);

        // 회전 시킨다.
        switch (orientation) {
            case 6:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_90, null);
                break;
            case 3:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_180, null);
                break;
            case 8:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_270, null);
                break;
            default:
                break;
        }

        // 썸네일의 너비와 높이 입니다.
        int dw = width, dh = height;
        // 원본 이미지의 너비와 높이 입니다.
        int ow = srcImg.getWidth();
        int oh = srcImg.getHeight();
        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow;
        int nh = (ow * dh) / dw;
        // 계산된 높이가 원본보다 높다면 crop이 안되므로
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if (nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }
        // 계산된 크기로 원본이미지를 가운데에서 crop 합니다.
        BufferedImage cropImg = Scalr.crop(srcImg, (ow - nw) / 2, (oh - nh) / 2, nw, nh);
        // crop된 이미지로 썸네일을 생성합니다.
        BufferedImage destImg = Scalr.resize(cropImg, dw, dh);
        if (suffix == null) {
            suffix = "thumb";
        }
        String thumbName = saveName.substring(0, saveName.lastIndexOf("."));
        String fileExt = saveName.substring(saveName.lastIndexOf(".") + 1).toLowerCase();
        File thumbFile = new File(savePath + File.separator + thumbName + "_" + suffix + '.' + fileExt);

        // 저장
        ImageIO.write(destImg, fileExt, thumbFile);
    }
}
