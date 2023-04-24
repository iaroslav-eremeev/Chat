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

        function setupEventSource() {
            evtSource = new EventSource('sse/chat-watch');
            evtSource.onmessage = function (e) {
                var msg = JSON.parse(e.data);
                $("#chat-messages").append("<p id='" + msg.user.userId + "'>" + " "
                    + "<span id='" + msg.user.name + "'>" + msg.user.name + "</span>" + ": " + msg.text + "</p>")
                    /*.scrollTop($('#chat-messages')[0].scrollHeight);*/
            };
            evtSource.onopen = function () {
                // Reset reconnect frequency upon successful connection
                reconnectFrequencySeconds = 1;
            };
            evtSource.onerror = function () {
                evtSource.close();
                reconnectFunc();
            };
        }
        setupEventSource();
    } else {
        alert("Your browser does not support EventSource!");
    }

    $('#send-message-button').click(function () {
        $.ajax({
            url: 'sse/chat-watch',
            method: "POST",
            data: {"userId": getCookie("userId"), "text": $('#message-input').val()},
            error: function (data) {
                alert('+');
            }
        })
        // Clear the input field
        $('#message-input').val("");
    });

    // Function to get a cookie by name
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(";").shift();
    }
});
