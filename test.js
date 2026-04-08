import http from "k6/http";

export default function () {
    http.get('http://localhost:8080/test', {
        headers: { 'X-User-Id': 'user-1' },
    });
}