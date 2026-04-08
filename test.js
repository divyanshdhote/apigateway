import http from "k6/http";

export default function () {
    const userId = "user-" + Math.floor(Math.random() * 10);

    http.get('http://localhost:8080/test', {
        headers: { 'X-User-Id': userId },
    });
}