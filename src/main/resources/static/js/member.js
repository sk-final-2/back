// ✅ 테이블 body 요소를 전역에서 가져오기
const memberTableBody = document.getElementById('member-table-body');

document.addEventListener('DOMContentLoaded', function() {
    const idHeader = document.getElementById('idHeader');
    const tableBody = document.getElementById('member-table-body');
    const sortIndicator = document.getElementById('sortIndicator');
    let sortOrder = 'asc';

    // ID 정렬
    idHeader.addEventListener('click', function() {
        const rows = Array.from(tableBody.querySelectorAll('tr'));
        rows.sort((a, b) => {
            const aId = parseInt(a.cells[0].textContent, 10);
            const bId = parseInt(b.cells[0].textContent, 10);
            return sortOrder === 'asc' ? aId - bId : bId - aId;
        });
        rows.forEach(row => tableBody.appendChild(row));

        if (sortOrder === 'asc') {
            sortOrder = 'desc';
            sortIndicator.textContent = '▼';
        } else {
            sortOrder = 'asc';
            sortIndicator.textContent = '▲';
        }
    });

    // ✅ 검색 & 필터 버튼
    const searchIdInput = document.getElementById('searchIdInput');
    const allUsersBtn = document.getElementById('allUsersBtn');
    const activeUsersBtn = document.getElementById('activeUsersBtn');
    const suspendedUsersBtn = document.getElementById('suspendedUsersBtn');

    const filterUsers = (status) => {
        allRows.forEach(row => {
            const statusCell = row.cells[3].textContent.trim();
            if (status === 'all') row.style.display = '';
            else if (status === 'active' && statusCell === '활성') row.style.display = '';
            else if (status === 'suspended' && statusCell === '정지됨') row.style.display = '';
            else row.style.display = 'none';
        });
    };

    allUsersBtn.addEventListener('click', () => filterUsers('all'));
    activeUsersBtn.addEventListener('click', () => filterUsers('active'));
    suspendedUsersBtn.addEventListener('click', () => filterUsers('suspended'));

    // ID 검색
    searchIdInput.addEventListener('input', () => {
        const idSearchTerm = searchIdInput.value.trim();
        allRows.forEach(row => {
            const userId = row.cells[0].textContent.trim();
            row.style.display = idSearchTerm === '' || userId.includes(idSearchTerm) ? '' : 'none';
        });
    });
});

function drawMemberTable(pageData) {
    const members = pageData.content;
    let html = '';
    if (members.length === 0) {
        html = '<tr><td colspan="7">일치하는 회원이 없습니다.</td></tr>';
    } else {
        members.forEach(member => {
            // ✅ 정지 해제일 정보를 처리하는 코드 추가
            const suspendedUntil = member.suspendedUntil;
            let formattedDate = '';
            if (member.suspended && suspendedUntil) {
                 const date = new Date(suspendedUntil);
                 formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
            }

            const statusText = member.suspended ? '정지됨' : '활성';
            const statusColor = member.suspended ? 'color: var(--destructive);' : 'color: var(--primary);';
            const suspendReason = member.suspended ? member.suspendedReason || '사유 없음' : 'N/A';
            const actionBtnHtml = member.suspended
                ? `<button class="action-btn btn-accent" onclick="handleMemberAction(${member.id}, 'unsuspend')">정지 해제</button>`
                : `<button class="action-btn btn-warning" onclick="handleMemberAction(${member.id}, 'suspend')">정지</button>`;

            html += `
                <tr>
                    <td>${member.id}</td>
                    <td>${member.email}</td>
                    <td>${member.name}</td>
                    <td style="${statusColor}">${statusText}
                        ${member.suspended && suspendedUntil ? `<div class="suspension-status">(${formattedDate})</div>` : ''}
                    </td>
                    <td>${suspendReason}</td>
                    <td>${member.createdAt.split('T')[0]}</td>
                    <td>
                        ${actionBtnHtml}
                        <button class="action-btn btn-warning" onclick="handleMemberAction(${member.id}, 'delete')">삭제</button>
                    </td>
                </tr>`;
        });
    }
    memberTableBody.innerHTML = html;

    allRows = Array.from(memberTableBody.querySelectorAll('tr'));
    currentPage = pageData.number;
    totalPages = pageData.totalPages;
    document.getElementById('pageInfo').textContent = `페이지 ${currentPage + 1} / ${totalPages}`;
    document.getElementById('prevBtn').disabled = currentPage === 0;
    document.getElementById('nextBtn').disabled = currentPage === totalPages - 1;
}

function fetchMembers(query = '', page = 0) {
    const pageToFetch = query ? 0 : page;
    const url = `/api/admin/members?page=${pageToFetch}&size=${pageSize}` + (query ? `&query=${query}` : '');
    fetch(url)
        .then(response => {
            if (!response.ok) return response.json().then(errorData => Promise.reject(errorData));
            return response.json();
        })
        .then(data => drawMemberTable(data.data))
        .catch(error => {
            console.error('Error fetching members:', error);
            alert('회원 목록을 불러오는 데 실패했습니다.');
        });
}

document.getElementById('searchButton').addEventListener('click', () => {
    fetchMembers(searchInput.value, 0);
});
document.getElementById('clearButton').addEventListener('click', () => {
    searchInput.value = '';
    fetchMembers('', 0);
});
document.getElementById('prevBtn').addEventListener('click', () => {
    if (currentPage > 0) fetchMembers(searchInput.value, currentPage - 1);
});
document.getElementById('nextBtn').addEventListener('click', () => {
    if (currentPage < totalPages - 1) fetchMembers(searchInput.value, currentPage + 1);
});

function handleMemberAction(memberId, actionType) {
    if (actionType === 'suspend') {
        currentMemberId = memberId;
        document.getElementById('suspendModal').style.display = 'block';
    } else if (actionType === 'unsuspend') {
        if (confirm('정말 이 회원을 정지 해제하시겠습니까?')) {
            fetch(`/api/admin/members/${memberId}/unsuspend`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        alert('정지 해제 처리가 완료되었습니다.');
                        fetchMembers(searchInput.value);
                    } else {
                        return response.json().then(errorData => {
                            alert('처리 실패: ' + (errorData.message || '알 수 없는 오류'));
                        });
                    }
                });
        }
    } else if (actionType === 'delete') {
        if (confirm('정말 이 회원을 영구 삭제하시겠습니까?')) {
            fetch(`/api/admin/members/${memberId}`, { method: 'DELETE' })
                .then(response => {
                    if (response.ok) {
                        alert('삭제 처리가 완료되었습니다.');
                        fetchMembers(searchInput.value);
                    } else {
                        return response.json().then(errorData => {
                            alert('처리 실패: ' + (errorData.message || '알 수 없는 오류'));
                        });
                    }
                });
        }
    }
}
