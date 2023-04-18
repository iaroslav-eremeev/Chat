$('#btn-go-login').click(function () {
        $(location).attr('href', "http://localhost:8080/QuizWeb/login.html");
    }
)


$('#btn-sign-up').click(function () {
        $.ajax({
            url: 'registration',
            method: "POST",
            data: {"login" : $('#login').val(),
                "name" : $('#name').val(),
                "password" : $('#password').val()},
            success: [function (data) {
                $('.popup-fade').fadeIn();
            }],
            error: [function (xhr, status, error) {
                alert(xhr.responseText);
            }]
        })
    }
)

$('#btn-ok').click(function () {
    $('.popup-fade').fadeOut();
    $(location).attr('href', "http://localhost:8080/QuizWeb/login.html");
})