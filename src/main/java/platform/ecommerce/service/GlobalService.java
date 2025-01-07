package platform.ecommerce.service;

import platform.ecommerce.dto.member.MemberSaveRequestDto;

public interface GlobalService {
    Long join(MemberSaveRequestDto memberSaveRequestDto);

    boolean emailExists(String email);
}
