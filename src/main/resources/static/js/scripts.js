// 검색창 토글 기능
document.getElementById('searchToggle').addEventListener('click', function(event) {
    event.preventDefault();  // 링크 기본 동작 방지 (페이지 이동 방지)

    var searchBox = document.getElementById('searchBox');

    // 검색창이 보이면 숨기고, 숨겨져 있으면 보이게 함
    if (searchBox.style.display === 'none' || searchBox.style.display === '') {
        searchBox.style.display = 'flex';
    } else {
        searchBox.style.display = 'none';
    }
});

// 검색 버튼 클릭 시 검색어 처리 기능
document.getElementById('searchButton').addEventListener('click', function() {
    var searchQuery = document.getElementById('searchInput').value;  // 입력된 검색어 가져오기
    if (searchQuery) {
        // 검색 처리 로직, 예: 검색 페이지로 이동하거나 AJAX 요청
        alert('검색어: ' + searchQuery);  // 실제 구현 시 검색 로직으로 대체
    } else {
        alert('검색어를 입력해주세요.');
    }
});

// 외부 클릭 시 검색창 비활성화
document.addEventListener('click', function(event) {
    var searchBox = document.getElementById('searchBox');
    var searchToggle = document.getElementById('searchToggle');

    // 클릭한 곳이 검색창 또는 검색 토글 버튼이 아닌 경우에만 닫기
    if (!searchBox.contains(event.target) && event.target !== searchToggle) {
        searchBox.style.display = 'none';
    }
});

// 슬라이드 자동 변경 기능
document.addEventListener('DOMContentLoaded', function() {
    const carouselElement = document.querySelector('#mainCarousel');
    if (carouselElement) {
        const carousel = new bootstrap.Carousel(carouselElement, {
            interval: 5000, // 슬라이드 간격 (5초)
            ride: 'carousel'
        });
    }
});
