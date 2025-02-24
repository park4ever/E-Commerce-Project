package platform.ecommerce.dto.member;

import lombok.Builder;
import lombok.Data;
import platform.ecommerce.entity.Address;

@Data
public class MemberDetailsDto {

    private Long memberId;
    private String username;
    private String phoneNumber;
    private String street;
    private String city;
    private String zipcode;
    private String additionalInfo;

    @Builder
    public MemberDetailsDto(Long memberId, String username, String phoneNumber, Address address) {
        this.memberId = memberId;
        this.username = username;
        this.phoneNumber = phoneNumber;

        if (address != null) {
            this.street = address.getStreet();
            this.city = address.getCity();
            this.zipcode = address.getZipcode();
            this.additionalInfo = address.getAdditionalInfo();
        }
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s",
                city != null ? city : "",
                street != null ? street : "",
                zipcode != null ? zipcode : "").trim();
    }

    public Address getAddress() {
        return new Address(this.city, this.street, this.zipcode,
                this.additionalInfo != null ? this.additionalInfo : "");
    }
}
