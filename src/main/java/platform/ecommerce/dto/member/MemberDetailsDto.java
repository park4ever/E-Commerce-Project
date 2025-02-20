package platform.ecommerce.dto.member;

import lombok.Builder;
import lombok.Data;
import platform.ecommerce.entity.Address;

@Data
public class MemberDetailsDto {

    private String username;
    private String phoneNumber;
    private String street;
    private String city;
    private String zipcode;
    private String additionalInfo;

    @Builder
    public MemberDetailsDto(String username, String phoneNumber, Address address) {
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
}
