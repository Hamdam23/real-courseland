package com.courseland.lesson;

import com.courseland.file.FileServiceClient;
import com.courseland.lesson.dtos.LessonRequestDTO;
import com.courseland.lesson.dtos.LessonResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository userRepository;
    private final LessonMapper lessonMapper;
    private final FileServiceClient fileServiceClient;

    @Override
    public LessonResponseDTO createLesson(LessonRequestDTO lessonRequestDTO) {
        Lesson lesson = userRepository.save(lessonMapper.toEntity(lessonRequestDTO));

        return lessonMapper.toResponseDTO(lesson);
    }

    @Override
    public LessonResponseDTO updateLesson(Long id, LessonRequestDTO lessonRequestDTO) {
        Lesson user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("invalid id"));
        lessonMapper.partialUpdate(user, lessonRequestDTO);

        LessonResponseDTO response = lessonMapper.toResponseDTO(user);
        response.setStudyMaterials(fileServiceClient.getFilesFromIds(lessonRequestDTO.getStudyMaterials()).getBody());
        response.setRelatedResources(fileServiceClient.getFilesFromIds(lessonRequestDTO.getRelatedResources()).getBody());

        return response;
    }

    @Override
    public List<LessonResponseDTO> getAllLessons() {
        return userRepository.findAll()
                .stream().map(lessonMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponseDTO getLesson(Long id) {
        Lesson user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("invalid id"));

        return lessonMapper.toResponseDTO(user);
    }

    @Override
    public void deleteLesson(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("invalid id"));
        userRepository.deleteById(id);
    }
}