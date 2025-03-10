package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.admin.*;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    //회원 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public List<AdminMemberDto> getAllMembers(String searchKeyword, String sortBy) {
        Sort sort = getSortForMembers(sortBy);

        List<Member> members = StringUtils.hasText(searchKeyword)
                ? memberRepository.searchActiveMembers(searchKeyword, searchKeyword, sort)
                : memberRepository.findAllActiveMembers(sort);

        return members.stream().map(this::convertToAdminMemberDto).collect(toList());
    }

    //회원 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminMemberDto getMemberById(Long memberId) {
        return convertToAdminMemberDto(findEntityById(memberRepository, memberId, "회원"));
    }

    //회원 정보 수정
    @Override
    public void updateMember(Long memberId, AdminMemberDto updatedMemberDto) {
        Member member = findEntityById(memberRepository, memberId, "회원");

        member.updateMemberByAdmin(
                updatedMemberDto.getUsername(),
                updatedMemberDto.getPhoneNumber(),
                updatedMemberDto.getRole()
        );
    }

    //회원 삭제
    @Override
    public void deleteMember(Long memberId) {
        Member member = findEntityById(memberRepository, memberId, "회원");
        member.deactivate();
        memberRepository.save(member); //명시적 저장
    }

    //상품 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public List<AdminItemDto> getAllItems(String searchKeyword, String sortBy) {
        Sort sort = getSortForItem(sortBy);
        List<Item> items = itemRepository.searchItems(searchKeyword, sort);

        return items.stream().map(this::convertToAdminItemDto).collect(toList());
    }

    //상품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminItemDto getItemById(Long itemId) {
        return convertToAdminItemDto(findEntityById(itemRepository, itemId, "상품"));
    }

    //상품 정보 수정
    @Override
    public void updateItem(Long itemId, AdminItemDto updatedItemDto) {
        Item item = findEntityById(itemRepository, itemId, "상품");

        item.updateItemDetails(
                updatedItemDto.getItemName(),
                updatedItemDto.getDescription(),
                updatedItemDto.getPrice(),
                updatedItemDto.getStockQuantity()
        );
    }

    //상품 활성/비활성화
    @Override
    public void toggleItemAvailability(Long itemId, boolean isAvailable) {
        Item item = findEntityById(itemRepository, itemId, "상품");

        if (isAvailable) {
            item.activate(); //활성화
        } else {
            item.deactivate(); //비활성화
        }

        itemRepository.save(item);
    }

    //상품 삭제
    @Override
    public void deleteItem(Long itemId) {
        itemRepository.delete(findEntityById(itemRepository, itemId, "상품"));
    }

    //주문 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderDto> getAllOrders(String searchKeyword, Pageable pageable) {
        Page<Order> orders = orderRepository.searchOrders(searchKeyword, pageable);

        return orders.map(this::convertToAdminOrderDto);
    }

    //주문 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminOrderDto getOrderById(Long orderId) {
        return convertToAdminOrderDto(findEntityById(orderRepository, orderId, "주문"));
    }

    //주문 상태 변경
    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        findEntityById(orderRepository, orderId, "주문").updateStatus(newStatus);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = findEntityById(orderRepository, orderId, "주문");

        if (!order.isCancelable()) {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        order.cancel();
    }

    @Override
    public void updateOrderItem(Long orderItemId, int newQuantity) {

    }

    @Override
    public void deleteOrderItem(Long orderItemId) {

    }

    @Override
    public List<AdminReviewDto> getAllReviews(String searchKeyword, String sortBy) {
        return null;
    }

    @Override
    public AdminReviewDto getReviewById(Long reviewId) {
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }

    /**
     * 공통 메서드
     */

    //ID를 기반으로 엔티티 찾기(제네릭)
    private <T> T findEntityById(JpaRepository<T, Long> repository, Long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName + " ID [ " + id + " ]를 찾을 수 없습니다."));
    }

    //AdminMemberDto 변환
    private AdminMemberDto convertToAdminMemberDto(Member member) {
        return AdminMemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .dateOfBirth(member.getDateOfBirth())
                .role(member.getRole())
                .createdDate(member.getCreatedDate())
                .lastModifiedDate(member.getLastModifiedDate())
                .build();
    }

    //AdminItemDto 변환
    private AdminItemDto convertToAdminItemDto(Item item) {
        return AdminItemDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .imageUrl(item.getImageUrl())
                .createdDate(item.getCreatedDate())
                .lastModifiedDate(item.getLastModifiedDate())
                .totalSales(itemRepository.getTotalSalesByItemId(item.getId()))
                .isAvailable(item.isAvailable())
                .build();
    }

    //AdminOrderDto 변환
    private AdminOrderDto convertToAdminOrderDto(Order order) {
        List<AdminOrderItemDto> orderItems = order.getOrderItems().stream()
                .map(orderItem -> new AdminOrderItemDto(
                        orderItem.getItem().getId(),
                        orderItem.getItem().getItemName(),
                        orderItem.getOrderPrice(),
                        orderItem.getCount(),
                        orderItem.getItem().getImageUrl()
                ))
                .collect(toList());

        return AdminOrderDto.builder()
                .id(order.getId())
                .memberEmail(order.getMember().getEmail())
                .memberName(order.getMember().getUsername())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress().toString())
                .lastModifiedDate(order.getLastModifiedDate())
                .orderItems(orderItems)
                .totalAmount(order.getTotalPrice())
                .isPaid(order.isPaid())
                .isCancelable(order.isCancelable())
                .build();
    }

    //회원 정렬 메서드
    private Sort getSortForMembers(String sortBy) {
        return "createdDate".equals(sortBy) ? Sort.by(ASC, "createdDate") : Sort.by(ASC, "lastModifiedDate");
    }

    //상품 정렬 메서드
    private Sort getSortForItem(String sortBy) {
        return switch (sortBy) {
            case "price" -> Sort.by(ASC, "price");
            case "priceDesc" -> Sort.by(DESC, "price");
            default -> Sort.by(DESC, "createdDate");
        };
    }
}