/**
 * 인증이 필요한 API 요청을 처리하는 클라이언트 함수
 * AccessToken 만료되었을 때 자동으로 재발급 시도하고, 기존 요청 재시도
 */
async function fetchWithAuth(url, options = {}) {
    // localStorage에서 토큰 가져오기
    let accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        window.location.href = '/login';
        throw new Error('No access token found');
    }

    // 기본 헤더 설정
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        'Authorization': `Bearer ${accessToken}`
    };

    // 첫 번째 API 요청 시도
    let response = await fetch(url, {...options, headers });

    // 엑세스 토큰이 만료(401) 시 재발급 시도
    if (response.status === 401) {
        console.warn("Access Token이 만료되어 재발급을 시도합니다.");
        try {
            const newAccessToken = await reissueToken();

            // 재발급 성공 시, 새 토큰으로 헤더를 업데이트하고 원래 요청 재시도
            headers['Authorization'] = `Bearer ${newAccessToken}`;
            console.log("새 토큰으로 API 요청을 재시도합니다.");
            response = await fetch(url, {...options, headers });

        } catch (reissueError) {
            console.error("토큰 재발급 실패:", reissueError.message);
            throw reissueError;
        }
    }

    // 권한 없음(403) 에러 처리
    if (response.status === 403) {
        console.error("접근 권한이 없습니다. (403 Forbidden)");
        alert("해당 기능에 접근할 권한이 없습니다.");
        throw new Error('Forbidden');
    }

    return response;
}

/**
 * RefreshToken을 사용해서 AccessToken, RefreshToken 재발급 요청
 */
async function reissueToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
        alert("세션 정보(RefreshToken)가 없습니다. 다시 로그인해주세요.");
        logout();
        throw new Error("재발급에 필요한 Refresh Token이 없습니다.");
    }

    try {
        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        });

        if (!response.ok) {
            // 서버가 재발급을 거부한 경우
            const errorData = await response.json();
            throw new Error(errorData.message || '서버에서 토큰 재발급을 거부했습니다.');
        }

        const result = await response.json();
        const newTokens = result.data;

        // localStorage에 새로운 토큰 저장
        localStorage.setItem('accessToken', newTokens.accessToken);
        if (newTokens.refreshToken) {
            localStorage.setItem('refreshToken', newTokens.refreshToken);
        }

        console.log("새로운 Access Token이 성공적으로 발급되었습니다.");
        return newTokens.accessToken;

    } catch (error) {
        console.error("reissueToken 함수 내부에서 오류 발생:", error.message);
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        logout();
        throw error;
    }
}