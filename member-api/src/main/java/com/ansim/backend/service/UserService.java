package com.ansim.backend.service;

import com.ansim.backend.entity.Usr;
import com.ansim.backend.repository.UsrRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UsrRepository usrRepository;

    public UserService(UsrRepository usrRepository) {
        this.usrRepository = usrRepository;
    }

    public List<Usr> searchByName(String name) {
        return usrRepository.findByMemberNameContaining(name);
    }

    public Usr searchByLoginId(String loginId) {
        return usrRepository.findByLoginId(loginId).orElse(null);
    }
}
