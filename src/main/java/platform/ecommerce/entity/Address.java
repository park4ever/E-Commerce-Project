package platform.ecommerce.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String city;
    private String street;
    private String zipcode;

    private String additionalInfo; //추가 정보. 예 : 아파트 동호수 등

    @Override
    public String toString() {
        return city + " " + street + " " + zipcode;
    }

    public String fullAddress() {
        return String.format("%s, %s, %s, %s",
                zipcode, city, street, additionalInfo);
    }

    public static Address fromFullAddress(String fullAddress) {
        Address address = new Address();
        //주소를 분리하는 로직 구현
        String[] parts = fullAddress.split(",\\s*");
        if (parts.length >= 3) {
            address.setZipcode(parts[0]);
            address.setCity(parts[1]);
            address.setStreet(parts[2]);
            if (parts.length > 3) {
                address.setAdditionalInfo(parts[3]);
            }
        }
        return address;
    }
}
