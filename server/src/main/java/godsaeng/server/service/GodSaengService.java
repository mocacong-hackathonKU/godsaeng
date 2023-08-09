package godsaeng.server.service;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.repository.GodSaengRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;

    @Transactional
    public GodSaengSaveResponse save(GodSaengSaveRequest request) {
        GodSaeng godSaeng = new GodSaeng(request.getTitle(), request.getDescription(), request.getWeeks());
        return new GodSaengSaveResponse(godSaengRepository.save(godSaeng).getId());
    }
}
