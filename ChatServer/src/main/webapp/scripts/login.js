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
                $(location).attr('href', "http://localhost:8080/ChatServer/index.html");
            }],
            error: [function (xhr, status, error) {
                alert(xhr.responseText);
            }]
        })
    }
)