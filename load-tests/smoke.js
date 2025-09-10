// load-tests/smoke.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = { vus: 1, duration: '10s' };

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const PATH = __ENV.PATH || '/actuator/health';
const TOKEN = __ENV.TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjQGMuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc1NzQ3NzMxNCwiZXhwIjoxNzU3NDc4MjE0fQ.bM4vNO6AoCQlZGPcCkFlsi_5K9KVnrc0L31UQ-v3uh4';

const headers = TOKEN
  ? { Authorization: TOKEN.startsWith('Bearer ') ? TOKEN : `Bearer ${TOKEN}` }
  : {};

export default function () {
  const url = `${BASE}${PATH}`;
  const res = http.get(url, { headers });

  // 실패 이유를 바로 보자
  if (res.status !== 200) {
    console.log(`❗ status=${res.status} url=${url}`);
    // console.log(res.body); // 응답 바디도 보고 싶으면 주석 해제
  }

  check(res, { '200 OK': r => r.status === 200 });
  sleep(1);
}
