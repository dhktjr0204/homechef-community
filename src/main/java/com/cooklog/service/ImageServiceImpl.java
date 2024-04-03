package com.cooklog.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.User;
import com.cooklog.repository.ImageRepository;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;


    @Override
    public List<String> fileListWrite(List<MultipartFile> files, Board board) {
        List<String> fileNameList = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {

            String fileName = saveS3(files.get(i));

            saveImageInDB(board, fileName, i + 1);

        }

        return fileNameList;
    }

    @Override
    public List<String> fileListLoad(List<String> fileNames) {
        List<String> urlList = new ArrayList<>();

        for (String imageName : fileNames) {

            String urlText = null;
            try {
                urlText = loadS3(imageName);
            } catch (FileNotFoundException e) {
                urlText="";
            }

            urlList.add(urlText);
        }

        return urlList;
    }

    @Override
    public String fileWrite(MultipartFile file, Long userId) {
        return saveS3(file);
    }

    @Override
    public String fileLoad(String fileName) {
        try {
            return loadS3(fileName);
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    @Override
    public void updateFileList(Board board, List<String> originalFiles, List<MultipartFile> newFiles){

        List<Image> images = imageRepository.findAllByBoard_IdOrderByOrder(board.getId())
                .orElseThrow(BoardNotFoundException::new);

        //새로 전송된 사진들이 시작할 순서
        int orderIndex = 0;

        //만약 기존 이미지가 모두 지워졌다면 DB에 저장된 이미지들 모두 삭제
        if(originalFiles==null){
            for(Image image:images){
                imageRepository.delete(image);
                deleteS3(image.getName());
            }
        }else{
            // 이미 저장된 사진들의 순서를 바꿔준다.
            orderIndex = originalFiles.size();

            for (Image image : images) {
                int index = originalFiles.indexOf(image.getName());
                //만약 originalFiles에 없으면 사용자가 삭제한 사진이기 때문에 삭제
                if (index == -1) {
                    imageRepository.delete(image);
                    deleteS3(image.getName());
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

    //s3 사진 로드 로직
    private String loadS3(String fileName) throws FileNotFoundException {

        if (isNotExist(fileName)) {
            throw new FileNotFoundException();
        }

        URL url = amazonS3.getUrl(bucket, fileName);

        String urlText = "" + url;

        return urlText;
    }

    //s3 저장 로직
    private String saveS3(MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String directory = "images/";

        String fileName = directory + uuid + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileName;
    }

    //s3 삭제 로직
    @Override
    public void deleteS3(String fileName) {
        try {
            amazonS3.deleteObject(bucket, fileName);
            System.out.println("삭제 완료");
        }catch (AmazonServiceException e){
            System.out.println("Amazon 서비스 예외 발생");
            System.out.println(e.getErrorMessage());
        }
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
