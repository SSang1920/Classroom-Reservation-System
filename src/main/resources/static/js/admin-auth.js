/**
 * 관리자 페이지 접근 제어 스크립트
 * 페이지가 로드될 때 localStorage의 JWT 토큰을 확인하여, 토큰이 없거나 관리자가 아닐 경우
 * 각각 로그인 페이지 또는 메인 페이지로 리디렉션
 */
document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('accessToken');

    // 토큰이 없을 경우
    if (!token) {
        window.location.href = '/login';
        return;
    }

    try {
        // 토큰에서 역할 추출
        const payload = parseJwt(token);
        const userRole = payload.role;

        // 관리자가 아닐 경우
        if (userRole !== 'ROLE_ADMIN') {
            window.location.href = '/';
        }
    } catch (e) {
        // 토큰이 유효하지 않은 경우 (변조, 만료)
        console.error("관리자 접근 제어 중 토큰 파싱 오류: ", e);
        alert("세션이 유효하지 않습니다. 다시 로그인해주세요.");
        logout();
    }
});



