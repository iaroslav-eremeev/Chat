if (!!window.EventSource) {
    function isFunction(functionToCheck) {
        return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
    }
    function debounce(func, wait) {
        let timeout;
        let waitFunc;

        return function () {
            if (isFunction(wait)) {
                waitFunc = wait;
            } else {
                waitFunc = function () {
                    return wait
                };
            }
            let context = this, args = arguments;
            const later = function () {
                timeout = null;
                func.apply(context, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, waitFunc());
        };
    }
    // reconnectFrequencySeconds doubles every retry
    let reconnectFrequencySeconds = 2;
    let evtSource;

    let reconnectFunc = debounce(function () {
        setupEventSource();
        // Double every attempt to avoid overwhelming server
        reconnectFrequencySeconds *= 2;
        // Max out at ~1 minute as a compromise between user experience and server load
        if (reconnectFrequencySeconds >= 64) {
            reconnectFrequencySeconds = 64;
        }
    }, function () {
        return reconnectFrequencySeconds * 1000
    });

    function setupEventSource() {
        let evtSource = new EventSource('sse/chat-watch');
        evtSource.addEventListener('sse/chat-watch', function(event) {
            const msg = JSON.parse(event.data);
            const chatMessages = document.querySelector('#chat-messages');
            const messageDiv = document.createElement('div');
            messageDiv.innerText = `${msg.username}: ${msg.message}`;
            chatMessages.appendChild(messageDiv);
        });
        evtSource.onopen = function () {
            // Reset reconnect frequency upon successful connection
            reconnectFrequencySeconds = 2;
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
    const userId = getCookie("userId");
    const message = document.getElementById("message-input").value;
    $.ajax({
        url: 'sse/chat-watch',
        method: "POST",
        data: {"userId": userId, "text": message},
        error: function (xhr, status, error) {
            alert(xhr.responseText);
        }
    })
    // Clear the input field
    document.getElementById("message-input").value = "";
    });

// Function to get a cookie by name
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
}