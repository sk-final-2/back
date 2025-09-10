// ==============================
// 전역 변수
// ==============================
let dailyChart, statusPieChart, dailyChartFull, monthlyChartFull;
let currentPage = 0;
const pageSize = 10;
let totalPages = 1;
let currentMemberId = null;
let allRows = [];

// ==============================
// 로그아웃 버튼
// ==============================
const logoutButton = document.getElementById('logoutBtn');
if (logoutButton) {
    logoutButton.addEventListener('click', function() {
        window.location.href = '/login';
    });
}

// ==============================
// 함수 정의
// ==============================

// 대시보드 데이터 가져오기
function fetchDashboardData() {
    fetch('/api/admin/stats/total')
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                console.log('총 면접 시도:', data.data.count);
                // 여기에 차트 업데이트 코드 넣으면 됨
            } else {
                alert('대시보드 데이터 로드 실패');
            }
        })
        .catch(err => console.error('대시보드 API 오류:', err));
}

// 회원 목록 가져오기
function fetchMembers(searchKeyword = '', page = 0) {
    fetch(`/api/admin/members?search=${encodeURIComponent(searchKeyword)}&page=${page}`)
        .then(res => res.json())
        .then(data => {
            const tableBody = document.getElementById('memberTableBody');
            tableBody.innerHTML = ''; // 테이블 비우기

            if (data.success && data.data.content) {
                data.data.content.forEach(member => {
                    const suspendedUntil = member.suspendedUntil;
                    let formattedDate = '';
                    if (suspendedUntil) {
                         const date = new Date(suspendedUntil);
                         formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
                    }

                    const statusText = member.suspended ? '정지됨' : '정상';
                    const statusColor = member.suspended ? 'color: red;' : 'color: green;';

                    const rowHtml = `
                        <tr>
                            <td>${member.id}</td>
                            <td>${member.email}</td>
                            <td>${member.name}</td>
                            <td>
                                <span style="${statusColor}">${statusText}</span>
                                ${member.suspended ? `<div class="suspension-status">(${formattedDate})</div>` : ''}
                            </td>
                            <td>${member.suspendedReason || ''}</td>
                            <td>${new Date(member.createdAt).toISOString().split('T')[0]}</td>
                            <td>
                                <button class="action-btn btn-danger" onclick="handleMemberAction(${member.id}, 'suspend')">정지</button>
                            </td>
                        </tr>
                    `;
                    tableBody.innerHTML += rowHtml;
                });
            } else {
                tableBody.innerHTML = '<tr><td colspan="7">회원 목록을 불러올 수 없습니다.</td></tr>';
            }
        })
        .catch(err => console.error('회원 API 오류:', err));
}

// 통계 차트 전체 업데이트
function updateChartsFull() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    fetch(`/api/admin/stats/interviews?period=daily&startDate=${startDate}&endDate=${endDate}`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                console.log('일별 통계:', data.data);
                // 차트 그리기 로직 추가
            } else {
                alert('통계 데이터 로드 실패');
            }
        })
        .catch(err => console.error('통계 API 오류:', err));
}

// ==============================
// 뷰 전환 로직
// ==============================
const navButtons = document.querySelectorAll('.nav-button');
const contentSections = document.querySelectorAll('.content-section');
navButtons.forEach(button => {
    button.addEventListener('click', () => {
        navButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        const targetId = button.dataset.target;
        contentSections.forEach(section => section.style.display = 'none');
        document.getElementById(targetId).style.display = 'block';

        if (targetId === 'dashboard-section') {
            fetchDashboardData();
        } else if (targetId === 'member-section') {
            fetchMembers('', 0);
        } else if (targetId === 'stats-section') {
            const today = new Date().toISOString().split('T')[0];
            const thirtyDaysAgo = new Date(Date.now() - 30*24*60*60*1000).toISOString().split('T')[0];
            document.getElementById('startDate').value = thirtyDaysAgo;
            document.getElementById('endDate').value = today;
            updateChartsFull();
        }
    });
});

// ==============================
// 초기 로드
// ==============================
document.addEventListener('DOMContentLoaded', () => {
    fetchDashboardData();

    // 모달 관련 이벤트
    const suspendDropdown = document.getElementById('suspendReasonDropdown');
    const suspendInput = document.getElementById('suspendReasonInput');
    const confirmBtn = document.getElementById('confirmSuspendBtn');
    const cancelBtn = document.getElementById('cancelSuspendBtn');
    const suspendModal = document.getElementById('suspendModal');
    const searchInput = document.getElementById('searchInput');

    if (suspendDropdown) {
        suspendDropdown.addEventListener('change', () => {
            if (suspendDropdown.value === '기타') {
                suspendInput.style.display = 'block';
                suspendInput.focus();
            } else {
                suspendInput.style.display = 'none';
            }
        });
    }

    if (confirmBtn) {
            confirmBtn.addEventListener('click', () => {
                let reason = suspendDropdown.value;
                if (reason === '기타') {
                    reason = suspendInput.value.trim();
                    if (!reason) {
                        alert('기타 사유를 입력해주세요.');
                        return;
                    }
                } else if (!reason) {
                    alert('정지 사유를 선택해주세요.');
                    return;
                }

                // ✅ 정지 해제일 입력 필드에서 값을 가져옵니다.
                const suspendedUntilDate = document.getElementById('suspendUntilDateInput').value;

                // ✅ 날짜를 선택하지 않았을 경우 경고창을 띄웁니다.
                if (!suspendedUntilDate) {
                    alert('정지 해제일을 선택해주세요.');
                    return;
                }

                fetch(`/api/admin/members/${currentMemberId}/suspend`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        reason: reason,
                        // ✅ 정지 해제일 데이터를 요청 본문에 추가합니다.
                        suspendedUntil: suspendedUntilDate
                    })
                })
                .then(response => {
                    if (response.ok) {
                        alert('회원 정지 처리가 완료되었습니다.');
                        suspendModal.style.display = 'none';

                        // 정지 처리 후 회원 목록을 새로고침
                        fetchMembers(searchInput.value);
                    } else {
                        return response.json().then(errorData => {
                            if (response.status === 403 && errorData.code === "AUTH007") {
                                if (errorData.data && errorData.data.suspendedUntil) {
                                    const until = new Date(errorData.data.suspendedUntil);
                                    const formatted = `${until.getFullYear()}년 ${until.getMonth() + 1}월 ${until.getDate()}일`;
                                    alert(`해당 계정은 ${formatted} 까지 정지된 계정입니다.`);
                                } else {
                                    alert(errorData.message);
                                }
                            } else {
                                alert('처리 실패: ' + (errorData.message || '알 수 없는 오류'));
                            }
                        });
                    }
                })
                .catch(error => {
                    console.error('API 호출 중 오류:', error);
                    alert('API 호출 중 오류가 발생했습니다.');
                });
            });
        }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            suspendModal.style.display = 'none';
            suspendDropdown.value = '';
            suspendInput.value = '';
            suspendInput.style.display = 'none';
        });
    }
});
