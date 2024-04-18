package com.cooklog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.repository.ImageRepository;
import com.cooklog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private ImageServiceImpl imageService;

    private List<Image> createImageList() {

        Image image = Image.builder().name("images/test.png").order(1).build();
        Image image2 = Image.builder().name("images/test2.png").order(2).build();
        Image image3 = Image.builder().name("images/test3.png").order(3).build();

        List<Image> imageList = Arrays.asList(image, image2, image3);

        return imageList;
    }

    private List<MultipartFile> createNewImageList() {
        MockMultipartFile multipartFile1 = new MockMultipartFile("images", "newFile.jpg", "image/png", "test file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", "newFile2.jpg", "image/png", "test file2".getBytes(StandardCharsets.UTF_8));
        List<MultipartFile> images = Arrays.asList(multipartFile1, multipartFile2);

        return images;
    }

    @Test
    @DisplayName("새로운 이미지가 없고 기존 이미지가 수정 됐을 때 수정 테스트")
    void UpdateFIleList_withNoNewImage() {
        //given
        Board board = mock(Board.class);
        //1번,2번,3번 사진에서 1번,3번으로 수정되었다.
        List<String> originalFiles = Arrays.asList("images/test.png", "images/test3.png");

        List<Image> imageList = createImageList();
        when(imageRepository.findAllByBoard_IdOrderByOrder(any())).thenReturn(Optional.of(imageList));

        //when
        imageService.updateFileList(board, originalFiles, null);

        //then
        for (Image image : imageList) {
            if (!originalFiles.contains(image.getName())) {
                //originalFile에 해당 안되는 파일이 1개 지워졌는지 확인
                verify(imageRepository, times(1)).delete(image);
            } else {
                verify(imageRepository, never()).delete(image); // 기존 이미지는 삭제되지 않아야 함
            }
        }

        //순서 바뀌었는 지 확인
        assertThat(imageList.get(0).getOrder()).isEqualTo(1);
        assertThat(imageList.get(2).getOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("새로운 이미지만 수정 됐을 때 수정 테스트")
    void UpdateFIleList_withNewImage() {
        //given
        Board board = mock(Board.class);
        //1번,2번,3번 사진에서 1번,3번으로 수정되었다.
        List<String> originalFiles = Arrays.asList("images/test.png", "images/test2.png", "images/test3.png");
        //새로 업로드 될 파일
        List<MultipartFile> newImageList = createNewImageList();

        List<Image> imageList = createImageList();
        when(imageRepository.findAllByBoard_IdOrderByOrder(any())).thenReturn(Optional.of(imageList));

        //when
        imageService.updateFileList(board, originalFiles, newImageList);

        //then

        //기존 이미지 하나도 안지워졌는지 확인
        verify(imageRepository, never()).delete(any());

        //ArgumentCaptor는 인자 값을 받아올 수 있다.
        ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);

        //새로운 파일이 두개 저장 됐다.
        verify(imageRepository, times(2)).save(captor.capture());

        //반환되는 값을 검증에 사용한다.
        List<Image> saveImages = captor.getAllValues();

        //순서 바뀌었는 지 확인
        assertThat(imageList.get(0).getOrder()).isEqualTo(1);
        assertThat(imageList.get(1).getOrder()).isEqualTo(2);
        assertThat(imageList.get(2).getOrder()).isEqualTo(3);
        assertThat(saveImages.get(0).getOrder()).isEqualTo(4);
        assertThat(saveImages.get(1).getOrder()).isEqualTo(5);
    }

    @Test
    @DisplayName("새로운 이미지 추가되고 기존 이미지가 수정 됐을 때 수정 테스트")
    void UpdateFIleList_withNewImageAndOriginalImage() {
        //given
        Board board = mock(Board.class);
        //1번,2번,3번 사진에서 1번,3번으로 수정되었다.
        List<String> originalFiles = Arrays.asList("images/test.png", "images/test3.png");
        //새로 업로드 될 파일
        List<MultipartFile> newImageList = createNewImageList();

        List<Image> imageList = createImageList();
        when(imageRepository.findAllByBoard_IdOrderByOrder(any())).thenReturn(Optional.of(imageList));

        //when
        imageService.updateFileList(board, originalFiles, newImageList);

        //then
        for (Image image : imageList) {
            if (!originalFiles.contains(image.getName())) {
                //originalFile에 해당 안되는 파일이 1개 지워졌는지 확인
                verify(imageRepository, times(1)).delete(image);
            } else {
                verify(imageRepository, never()).delete(image); // 기존 이미지는 삭제되지 않아야 함
            }
        }

        //ArgumentCaptor는 인자 값을 받아올 수 있다.
        ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);
        //새로운 파일이 두개 저장 됐다.
        verify(imageRepository, times(2)).save(captor.capture());

        //반환되는 값을 검증에 사용한다.
        List<Image> saveImages = captor.getAllValues();

        //순서 바뀌었는 지 확인
        assertThat(imageList.get(0).getOrder()).isEqualTo(1);
        assertThat(imageList.get(2).getOrder()).isEqualTo(2);
        assertThat(saveImages.get(0).getOrder()).isEqualTo(3);
        assertThat(saveImages.get(1).getOrder()).isEqualTo(4);
    }

    @Test
    @DisplayName("새로운 이미지 추가되고 기존 이미지가 모두 삭제됐을 때 수정 테스트")
    void UpdateFIleList_withNewImageAndNoOriginalImage() {
        //given
        Board board = mock(Board.class);
        //새로 업로드 될 파일
        List<MultipartFile> newImageList = createNewImageList();

        List<Image> imageList = createImageList();
        when(imageRepository.findAllByBoard_IdOrderByOrder(any())).thenReturn(Optional.of(imageList));

        //when
        imageService.updateFileList(board, null, newImageList);

        //then

        //originalFile 전부 없앰(3개)
        verify(imageRepository,times(3)).delete(any());

        //ArgumentCaptor는 인자 값을 받아올 수 있다.
        ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);
        //새로운 파일이 두개 저장 됐다.
        verify(imageRepository, times(2)).save(captor.capture());

        //반환되는 값을 검증에 사용한다.
        List<Image> saveImages = captor.getAllValues();

        //순서 바뀌었는 지 확인
        assertThat(saveImages.get(0).getOrder()).isEqualTo(1);
        assertThat(saveImages.get(1).getOrder()).isEqualTo(2);
    }

}