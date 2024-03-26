package com.cooklog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    @Override
    public List<String> fileListWrite(List<MultipartFile> files, Board board) throws IOException {
        List<String> fileNameList = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {

            String fileName = saveS3(files.get(i));

            saveImageInDB(board, fileName, i + 1);

        }

        return fileNameList;
    }

    @Override
    public List<String> fileListLoad(String fileNames) throws FileNotFoundException {
        List<String> urlList = new ArrayList<>();

        String[] imageNames = fileNames.split(",");

        for (String imageName : imageNames) {

            if (isNotExist(imageName)) {
                throw new FileNotFoundException();
            }

            URL url = amazonS3.getUrl(bucket, imageName);

            String urlText = "" + url;
            urlList.add(urlText);
        }

        return urlList;
    }

    @Transactional
    @Override
    public void updateFileList(Board board, List<String> originalFiles, List<MultipartFile> newFiles) throws IOException {

        List<Image> images = imageRepository.findAllByBoard_IdOrderByOrder(board.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 board id가 없습니다."));

        //새로 전송된 사진들이 시작할 순서
        int orderIndex = 0;

        // 이미 저장된 사진들의 순서를 바꿔준다.
        if (originalFiles != null) {

            orderIndex = originalFiles.size();

            for (Image image : images) {
                int index = originalFiles.indexOf(image.getName());
                //만약 originalFiles에 없으면 사용자가 삭제한 사진이기 때문에 삭제
                if (index == -1) {
                    imageRepository.delete(image);
                } else {
                    //사진이 포함되어있다면 순서 업데이트
                    image.update(index + 1);
                }
            }
        }

        //새로운 파일 s3에 저장
        if (newFiles != null) {
            for (int i = 0; i < newFiles.size(); i++) {

                String fileName = saveS3(newFiles.get(i));

                saveImageInDB(board, fileName, orderIndex + i + 1);

            }
        }
    }

    @Override
    public List<String> getFileNameList(String images) {

        return Arrays.asList(images.split(","));

    }

    //s3 저장 로직
    private String saveS3(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();

        String directory = "images/";

        String fileName = directory + uuid + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);

        return fileName;
    }


    private boolean isNotExist(String fileName) {

        return !amazonS3.doesObjectExist(bucket, fileName);
    }

    private void saveImageInDB(Board board, String fileName, int order) {
        Image image = Image.builder()
                .board(board)
                .name(fileName)
                .order(order).build();

        imageRepository.save(image);
    }
}
