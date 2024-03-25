package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

