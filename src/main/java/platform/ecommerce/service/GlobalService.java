package platform.ecommerce.service;

import platform.ecommerce.dto.MemberSaveRequestDto;

public interface GlobalService {

    Long join(MemberSaveRequestDto memberSaveRequestDto);
}
