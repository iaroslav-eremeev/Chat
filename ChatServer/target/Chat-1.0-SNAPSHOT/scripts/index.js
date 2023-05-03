$(document).ready(function() {
    if (!!window.EventSource) {
        function isFunction(functionToCheck) {
            return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
        }

        function debounce(func, wait) {
            var timeout;
            var waitFunc;

            return function () {
                if (isFunction(wait)) {
                    waitFunc = wait;
                } else {
                    waitFunc = function () {
                        return wait
                    };
                }
                var context = this, args = arguments;
                var later = function () {
                    timeout = null;
                    func.apply(context, args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, waitFunc());
            };
        }
        // reconnectFrequencySeconds doubles every retry
        var reconnectFrequencySeconds = 1;
        var evtSource;

        var reconnectFunc = debounce(function () {
            setupEventSource();
            // Double every attempt to avoid overwhelming server
            reconnectFrequencySeconds *= 2;
            // Max out at ~1 minute as a compromise between userId experience and server load
            if (reconnectFrequencySeconds >= 64) {
                reconnectFrequencySeconds = 64;
            }
        }, function () {
            return reconnectFrequencySeconds * 1000
        });

        checkOnlineUsers();

        function setupEventSource() {
            evtSource = new EventSource('sse/chat-watch');
            alert("Event source activated!");
            evtSource.onmessage = function (e) {
                alert("Event source message sent!");
                var msg = JSON.parse(e.data);
                $("#chat-messages").append("<p id='" + msg.user.userId + "'>" + " "
                    + "<span id='" + msg.user.name + "'>" + msg.user.name + "</span>" + ": " + msg.text + "</p>")
                .scrollTop($('#chat-messages')[0].scrollHeight);
            };
            evtSource.onopen = function () {
                // Reset reconnect frequency upon successful connection
                reconnectFrequencySeconds = 1;
            };
            evtSource.onerror = function () {
                evtSource.close();
                reconnectFunc();
            };

            // Check if the user is online
            setInterval(function () {
                checkOnlineUsers();
            }, 15000);
        }

        $('#send-message-button').click(function () {
            $.ajax({
                url: 'sse/chat-watch',
                method: "POST",
                data: {"userId": getCookie("userId"), "text": $('#message-input').val()},
                success: function() {
                    // Clear the input field
                    $('#message-input').val("");
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                }
            })
        });

        $('#message-input').keypress(function (e) {
            if (e.which === 13) {
                $('#send-message-button').click();
                return false;
            }
        });

        function checkOnlineUsers() {
            $('#online-users').html("<p><strong>Online:</strong></p>");
            $.ajax({
                url: 'sse/chat-watch',
                method: 'GET',
                data: { checkOnlineUsers: 1 },
                success: function (onlineUsers) {
                    var parsedOnlineUsers = JSON.parse(onlineUsers);
                    $.each(parsedOnlineUsers, function (key, value) {
                        $("#online-users").append("<p id='" + value.userId + "'>" + " " + "<span id='" + value.userId + "'>" + value.name + "</span>")
                            .scrollTop($('#chat-messages')[0].scrollHeight);
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                }
            });
        }

        // Setup Event Source in the very end, when everything is initialized
        setupEventSource();

    } else {
        alert("Your browser does not support EventSource!");
    }

    // Function to get a cookie by name
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(";").shift();
    }

    // Retrieve all messages from the database
    $.ajax({
        url: 'messages',
        method: "GET",
        data: {"userId": "0"},
        success: function(messages) {
            $.each(messages, function (key, value) {
                $("#chat-messages").append("<p id='" + value.user.userId + "'>" + " "
                    + "<span id='" + value.user.userId + "'>" + value.user.name + "</span>" + ": " + value.text + "</p>")
                    .scrollTop($('#chat-messages')[0].scrollHeight);
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });

});
