package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> findAll();

    Teacher findById(Long id);
}
