package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftRepository;
import gssato.wins_pool.dto.DraftRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftServiceTest {

    @Autowired
    DraftService draftService;

    @MockBean
    UserService userService;

    @MockBean
    YearService yearService;

    @MockBean
    DraftPickService draftPickService;

    @MockBean
    DraftRepository repository;


    DraftRequestDTO makeRequestDTO() {
        DraftRequestDTO requestDTO = new DraftRequestDTO();
        requestDTO.setUserEmail("test@test.com");
        requestDTO.setYear(2023);
        requestDTO.setPickNumber(1);

        return requestDTO;
    }

}