package com.cooklog.repository;

import com.cooklog.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Modifying
    @Query(value = "update board set readcnt = readcnt+1 where id = :id", nativeQuery = true)
    void updateReadCnt(Long id);

    //마지막 board id가 없을 경우(첫 요청일 경우)
    Page<Board> findAll(Pageable pageable);

    //두번째 요청일 경우
    @Query(value = "select b from Board b where b.id < :id order by b.createdAt DESC")
    Page<Board> findAll(Long id, Pageable pageable);

}
