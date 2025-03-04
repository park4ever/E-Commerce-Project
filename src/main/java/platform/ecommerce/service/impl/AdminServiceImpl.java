package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.dto.admin.AdminReviewDto;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

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
        Sort sort = Sort.unsorted(); // 기본 정렬 없음
        if ("createdDate".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "createdDate");
        } else if ("lastModifiedDate".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "lastModifiedDate");
        }

        List<Member> members = StringUtils.hasText(searchKeyword)
                ? memberRepository.searchActiveMembers(searchKeyword, searchKeyword, sort)
                : memberRepository.findAllActiveMembers(sort);

        return members.stream()
                .map(member -> new AdminMemberDto(
                        member.getId(),
                        member.getEmail(),
                        member.getUsername(),
                        member.getPhoneNumber(),
                        member.getDateOfBirth(),
                        member.getRole(),
                        member.getCreatedDate(),
                        member.getLastModifiedDate()
                ))
                .collect(Collectors.toList());
    }

    //회원 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminMemberDto getMemberById(Long memberId) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        return new AdminMemberDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getPhoneNumber(),
                member.getDateOfBirth(),
                member.getRole(),
                member.getCreatedDate(),
                member.getLastModifiedDate()
        );
    }

    //회원 정보 수정
    @Override
    public void updateMember(Long memberId, AdminMemberDto updatedMemberDto) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        member.updateMemberByAdmin(
                updatedMemberDto.getUsername(),
                updatedMemberDto.getPhoneNumber(),
                updatedMemberDto.getRole()
        );
    }

    //회원 삭제
    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        member.deactivate();
        memberRepository.save(member); //명시적 저장
    }

    @Override
    public List<AdminItemDto> getAllItems(String searchKeyword, String sortBy) {
        return null;
    }

    @Override
    public AdminItemDto getItemById(Long itemId) {
        return null;
    }

    @Override
    public void updateItem(Long itemId, AdminItemDto updatedItemDto) {

    }

    @Override
    public void deleteItem(Long itemId) {

    }

    @Override
    public List<AdminOrderDto> getAllOrders(String searchKeyword, String sortBy) {
        return null;
    }

    @Override
    public AdminOrderDto getOrderById(Long orderId) {
        return null;
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

    }

    @Override
    public void cancelOrder(Long orderId) {

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
}
