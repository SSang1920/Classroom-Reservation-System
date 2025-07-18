// JWT 토큰을 파싱하여 payload 객체를 반환하는 유틸리티 함수
function parseJwt(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      return JSON.parse(jsonPayload);

    } catch (e) {
      throw new Error("Invalid token");
    }
}

// 로그아웃을 처리하는 공통 함수: 서버에 로그아웃을 요청하고 로컬 스토리지에서 토큰을 제거
function logout() {
    const token = localStorage.getItem('accessToken');

    if (token) {
        fetch('/api/auth/logout', {
          method: 'POST',
          headers: { 'Authorization': `Bearer ${token}` }
        });
    }

    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken")

    alert('로그아웃되었습니다.');
    window.location.href = "/";
}