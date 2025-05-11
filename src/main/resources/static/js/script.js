var log = document.getElementById("login")
var reg = document.getElementById("register")
var button = document.getElementById("btn")

function register() {
    log.style.left = "-400px";
    reg.style.left = "50px";
    button.style.left = "110px";
}

function login() {
    log.style.left = "50px"
    reg.style.left = "450px"
    button.style.left = "0"
}

function submitLogin(event) {
    try{
    console.log("submitLogin apelat");
    event.preventDefault();

    var formData = new FormData(document.getElementById("login"));
    var username = formData.get("usernameLogin");
    var password = formData.get("passwordLogin");

    fetch("/login", {
        method: "POST",
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
        .then(response => {
            console.log("ceva", response.status);
            if (response.ok) {
                window.location.href = "/home";
            } else {
                return response.text().then(text => {
                    alert("Login eșuat: " + text);
                });
            }
        })
        .then(data => {
            console.log(data);
            alert("Login reușit!");
        })
        .catch(error => {
            console.error('Error:', error);
        });

    } catch (error) {
        console.error("Error caught:", error);
        alert("A apărut o eroare!");
    }
}

window.login = login;
window.register = register;
window.submitLogin = submitLogin;
window.submitRegister = submitRegister;
