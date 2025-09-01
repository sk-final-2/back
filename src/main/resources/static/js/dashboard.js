//dashboard.js


    function fetchDashboardData() {
        fetch('/api/admin/stats/member-status')
            .then(res => res.json())
            .then(data => {
                const stats = data.data;
                document.getElementById('totalMembers').textContent = (stats.active + stats.suspended).toLocaleString();
                document.getElementById('activeMembers').textContent = stats.active.toLocaleString();
                drawStatusPieChart(stats);
            });

        // ✅ 총 면접 시도 횟수 API 호출 추가
            fetch('/api/admin/stats/total')
                .then(res => res.json())
                .then(data => {
                    const totalCount = data.data && data.data.count ? data.data.count : 0;
                    document.getElementById('totalInterviews').textContent = totalCount.toLocaleString();
                });


        const todayDate = new Date().toISOString().split('T')[0];
        fetch(`/api/admin/stats/daily-users?startDate=${todayDate}&endDate=${todayDate}`)
            .then(res => res.json())
            .then(data => {
                const count = data.data && data.data.length > 0 ? data.data[0].count : 0;
                document.getElementById('todayNewMembers').textContent = count.toLocaleString();
            });


        const today = new Date().toISOString().split('T')[0];
        const sevenDaysAgo = new Date(Date.now() - 7*24*60*60*1000).toISOString().split('T')[0];
        fetch(`/api/admin/stats/daily-users?startDate=${sevenDaysAgo}&endDate=${today}`)
            .then(res => res.json())
            .then(data => {
                drawDailyChart(data.data);
            });
    }

    function drawStatusPieChart(stats) {
        const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary').trim();
        const destructiveColor = getComputedStyle(document.documentElement).getPropertyValue('--destructive').trim();
        if (statusPieChart) statusPieChart.destroy();
        statusPieChart = new Chart(document.getElementById('statusPieChart'), {
            type: 'doughnut',
            data: {
                labels: ['활성', '정지됨'],
                datasets: [{ data: [stats.active, stats.suspended], backgroundColor: [primaryColor, destructiveColor], hoverOffset: 4 }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom', labels: { color: 'var(--foreground)' } },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(2) + '%';
                                return `${context.label}: ${percentage}`;
                            }
                        }
                    }
                }
            }
        });
    }

    function drawDailyChart(stats) {
        const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary').trim();
        if (dailyChart) dailyChart.destroy();
        const labels = stats.map(item => item.date);
        const counts = stats.map(item => item.count);
        dailyChart = new Chart(document.getElementById('dailyChart'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '일별 가입자 수',
                    data: counts,
                    backgroundColor: primaryColor,
                    borderColor: primaryColor,
                    borderWidth: 1
                }]
            },
            options: { scales: { y: { beginAtZero: true } } }
        });
    }
