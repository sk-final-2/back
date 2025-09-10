// load-tests/interview-results.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ── 실행 옵션(요청/초 고정: 비교에 유리)
export const options = {
  scenarios: {
    steady: {
      executor: 'constant-arrival-rate',
      rate: __ENV.RATE ? parseInt(__ENV.RATE) : 20, // rps (필요시 -e RATE=30)
      timeUnit: '1s',
      duration: __ENV.DURATION || '45s',            // -e DURATION=1m 로 조정 가능
      preAllocatedVUs: __ENV.VUS ? parseInt(__ENV.VUS) : 20,
      maxVUs: __ENV.MAX_VUS ? parseInt(__ENV.MAX_VUS) : 100,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    'http_req_duration{ep:interviewResults}': ['p(95)<500'],
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const PATH = __ENV.PATH || '/api/interview-results';
const TOKEN = __ENV.TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjQGMuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc1NzQ4MDc3NiwiZXhwIjoxNzU3NDgxNjc2fQ.Vmw3kdxrgoXjsCW_kUkbnnw0myz2skqOeZPYBZavuqg';

const headers = {
  ...(TOKEN ? { Authorization: TOKEN.startsWith('Bearer ') ? TOKEN : `Bearer ${TOKEN}` } : {}),
  Accept: 'application/json',
};

export function setup() {
  if (__ENV.WARMUP === 'true') {
    // 캐시 워밍업 1회
    http.get(`${BASE}${PATH}`, { headers, tags: { ep: 'interviewResults', cache: 'warmup' } });
    sleep(1);
  }
}

export default function () {
  const res = http.get(`${BASE}${PATH}`, { headers, tags: { ep: 'interviewResults' } });
  check(res, { '200 OK': r => r.status === 200 });
  if (res.status !== 200) {
    console.log(`❗ status=${res.status} url=${BASE}${PATH}`);
  }
  // 응답이 큰 경우 서버 보호용 대기
  sleep(0.2);
}
