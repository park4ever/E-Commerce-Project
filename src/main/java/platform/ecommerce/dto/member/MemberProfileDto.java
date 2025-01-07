package platform.ecommerce.dto.member;

import lombok.Builder;
import lombok.Data;
import platform.ecommerce.entity.Address;

import java.time.LocalDate;

@Data
public class MemberProfileDto {

    private String email;
    private String username;
    private String phoneNumber;
    private Address address;
    private LocalDate dateOfBirth;
    private String defaultShippingAddress;

    @Builder

    public MemberProfileDto(String email, String username, String phoneNumber, Address address, LocalDate dateOfBirth, String defaultShippingAddress) {
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.defaultShippingAddress = defaultShippingAddress;
    }
}
