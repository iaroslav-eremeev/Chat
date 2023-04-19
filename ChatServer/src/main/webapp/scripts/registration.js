$('#btn-go-login').click(function () {
        $(location).attr('href', "../login.html");
    }
)


$('#btn-sign-up').click(function () {
        $.ajax({
            url: 'registration',
            method: "POST",
            data: {"login" : $('#login').val(),
                "password" : $('#password').val(),
                "name" : $('#name').val(),},
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
    $(location).attr('href', "../login.html");
})