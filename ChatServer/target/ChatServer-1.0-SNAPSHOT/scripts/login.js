$('#btn-go-to-sign-up').click(function () {
        $(location).attr('href', "http://localhost:8080/ChatServer/registration.html");
    }
)

$('#btn-login').click(function () {
        $.ajax({
            url: 'login',
            method: "POST",
            data: {"login": $('#login').val(), "password": $('#password').val()},
            success: [function (result) {
                // Make decrypt key a 1-digit number from mix of login and password char codes
                let decryptKey = ($('#login').val().charCodeAt(0) * $('#password').val().charCodeAt(0)) % 10 + 1;
                localStorage.setItem('decryptKey', decryptKey.toString());
                $(location).attr('href', "http://localhost:8080/ChatServer/index.html");
            }],
            error: [function (result) {
                alert("Wrong login or password!")
            }]
        })
    }
)