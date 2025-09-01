    // stats.js

    document.addEventListener('DOMContentLoaded', () => {
        // ✅ 차트 전역 변수
        let dailyChartFull, monthlyChartFull, dailyInterviewChart, monthlyInterviewChart;

        // ✅ 함수를 전역으로 노출
        window.updateChartsFull = updateChartsFull;

        // ✅ 전체 통계 차트 로드 함수
        function updateChartsFull() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            fetchAndDrawChartsFull(startDate, endDate);
        }

        // ✅ 기간별 데이터 로드 및 차트 그리기
        function fetchAndDrawChartsFull(startDate, endDate) {
            const dailyUrl = `/api/admin/stats/daily-users?startDate=${startDate}&endDate=${endDate}`;
            const monthlyUrl = `/api/admin/stats/monthly-users?startDate=${startDate}&endDate=${endDate}`;

            // ✅ 변경된 URL로 수정
            const monthlyInterviewUrl = `/api/admin/stats/monthly-interview-attempts?startDate=${startDate}&endDate=${endDate}`;
            const dailyInterviewUrl = `/api/admin/stats/daily-interview-attempts?startDate=${startDate}&endDate=${endDate}`;

            const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary').trim();
            const accentColor = getComputedStyle(document.documentElement).getPropertyValue('--accent').trim();

            // ✅ 일별 가입자 수
            fetch(dailyUrl).then(res => res.json()).then(data => {
                if (dailyChartFull) dailyChartFull.destroy();
                const stats = data.data || [];
                const labels = stats.map(item => item.date);
                const counts = stats.map(item => item.count);
                dailyChartFull = new Chart(document.getElementById('dailyChart-full'), {
                    type: 'bar',
                    data: { labels: labels, datasets: [{ label: '일별 가입자 수', data: counts, backgroundColor: primaryColor, borderColor: primaryColor, borderWidth: 1 }] },
                    options: { scales: { y: { beginAtZero: true } } }
                });
            });

            // ✅ 월별 가입자 수
            fetch(monthlyUrl).then(res => res.json()).then(data => {
                if (monthlyChartFull) monthlyChartFull.destroy();
                const stats = data.data || [];
                const labels = stats.map(item => item.date);
                const counts = stats.map(item => item.count);
                monthlyChartFull = new Chart(document.getElementById('monthlyChart-full'), {
                    type: 'bar',
                    data: { labels: labels, datasets: [{ label: '월별 가입자 수', data: counts, backgroundColor: accentColor, borderColor: accentColor, borderWidth: 1 }] },
                    options: { scales: { y: { beginAtZero: true } } }
                });
            });

            // ✅ 일별 면접 시도 횟수 - 꺾은선 그래프로 변경
            fetch(dailyInterviewUrl).then(res => res.json()).then(data => {
                if (dailyInterviewChart) dailyInterviewChart.destroy();
                const stats = data.data || []; // ✅ 데이터가 없으면 빈 배열로 초기화

                // ✅ 배열 인덱스 대신 DTO 속성(date, count)으로 변경
                const labels = stats.map(item => item.date);
                const counts = stats.map(item => item.count);

                dailyInterviewChart = new Chart(document.getElementById('dailyInterviewChart'), {
                    type: 'line', // ✅ 차트 종류를 'line'으로 변경
                    data: { labels: labels, datasets: [{
                        label: '일별 면접 시도 횟수',
                        data: counts,
                        backgroundColor: primaryColor,
                        borderColor: primaryColor,
                        borderWidth: 2,
                        tension: 0.4, // ✅ 부드러운 곡선 효과 추가
                        fill: true // ✅ 영역 채우기
                    }] },
                    options: { scales: { y: { beginAtZero: true } } }
                });
            });

            // ✅ 월별 면접 시도 횟수 - 꺾은선 그래프로 변경
            fetch(monthlyInterviewUrl).then(res => res.json()).then(data => {
                if (monthlyInterviewChart) monthlyInterviewChart.destroy();
                const stats = data.data || [];
                const labels = stats.map(item => item.date);
                const counts = stats.map(item => item.count);
                monthlyInterviewChart = new Chart(document.getElementById('monthlyInterviewChart'), {
                    type: 'line', // ✅ 차트 종류를 'line'으로 변경
                    data: { labels: labels, datasets: [{
                        label: '월별 면접 시도 횟수',
                        data: counts,
                        backgroundColor: accentColor,
                        borderColor: accentColor,
                        borderWidth: 2,
                        tension: 0.4, // ✅ 부드러운 곡선 효과 추가
                        fill: true // ✅ 영역 채우기
                    }] },
                    options: { scales: { y: { beginAtZero: true } } }
                });
            });
        }
    });