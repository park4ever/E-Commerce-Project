package platform.ecommerce.dto.member;

import lombok.Builder;
import lombok.Data;
import platform.ecommerce.entity.Address;

@Data
public class MemberDetailsDto {

    private String username;
    private String phoneNumber;
    private Address address;

    @Builder
    public MemberDetailsDto(String username, String phoneNumber, Address address) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
