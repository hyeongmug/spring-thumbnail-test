package com.example.thumbnail;

import com.example.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ThumbnailApplicationTests {

    String savePath = "./files";
    String fileName = "pngimage.png";

    @Test
    void makeThumbnail() throws IOException {
        (new FileService()).makeThumbnail(savePath,fileName, 300, 200, "THUMB");
    }
}
