// load-tests/interview-by-id.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ===== 고정 설정(수정 필요 시 여기만 수정) =====
const BASE_URL = 'http://localhost:8080';
const PATH     = '/api/interview-results';
const ID       = '30'; // ← 단일 ID
const TOKEN    = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjQGMuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc1NzQ5MTExMywiZXhwIjoxNzU3NDkyMDEzfQ.Vgxuw4BhAZ4bWK92wxIB95nftdjLAhFuvy2_HNb2aKo';
const WARMUP   = true; // 시작 전에 1회 워밍업 호출 여부
// ===========================================

// 실행 옵션(요청/초 고정: 비교에 유리)
export const options = {
  scenarios: {
    steady: {
      executor: 'constant-arrival-rate',
      rate: 20,            // 초당 20요청
      timeUnit: '1s',
      duration: '45s',     // 총 45초
      preAllocatedVUs: 20,
      maxVUs: 100,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    'http_req_duration{ep:getInterviewById}': ['p(95)<500'],
  },
};

const headers = {
  Accept: 'application/json',
  ...(TOKEN ? { Authorization: TOKEN.startsWith('Bearer ') ? TOKEN : `Bearer ${TOKEN}` } : {}),
};

export function setup() {
  if (WARMUP) {
    const url = `${BASE_URL}${PATH}/${ID}`;
    http.get(url, { headers, tags: { ep: 'getInterviewById', cache: 'warmup' } });
    sleep(0.2);
  }
}

export default function () {
  const url = `${BASE_URL}${PATH}/${ID}`;
  const res = http.get(url, { headers, tags: { ep: 'getInterviewById' } });

  if (res.status !== 200) {
    console.log(`❗ status=${res.status} url=${url}`);
    // console.log(res.body); // 필요하면 바디 확인
  }

  check(res, {
    '200 OK': r => r.status === 200,
    'is JSON': r => String(r.headers['Content-Type'] || '').includes('application/json'),
  });

  sleep(0.1); // 서버 보호용 대기
}
