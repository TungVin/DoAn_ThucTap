const registerButton = document.getElementById('register');
const loginButton = document.getElementById('login');
const container = document.getElementById('container');

registerButton.onclick = function() {
    container.className = 'active'; // Hiển thị trang đăng ký
}

loginButton.onclick = function() {
    container.className = 'close'; // Hiển thị trang đăng nhập
}