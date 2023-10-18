package com.project.ipms.service;

import org.junit.jupiter.api.Test;

public class ImageServiceImplTest {


    @Test
    public void testImageCompare1(){
        ImageServiceImpl imageService = new ImageServiceImpl();
        boolean value = imageService.compare(im1, img2);
    }

    @Test
    public void testImageCompare2(){
        ImageServiceImpl imageService = new ImageServiceImpl();
        boolean value = imageService.compare(im1, img2);
    }
}
