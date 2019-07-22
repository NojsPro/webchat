// import Axios from 'https://cdn.bootcss.com/axios/0.19.0-beta.1/axios.min.js';
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/queueServer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/' + document.getElementById('user').value + '/message',
            function (greeting) {
                console.log(greeting.body);
                showGreeting(greeting.body);
                //showGreeting(JSON.parse(greeting.body).content);
                var response1 = document.getElementById('response');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.appendChild(document.createTextNode(response.body));
                response1.appendChild(p);
            });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    // stompClient.send("/app/test", {}, JSON.stringify({name:'hh',password:"hhhhh"}));
    stompClient.send("/app/online", {}, JSON.stringify({
        'userid': '2016127214'
    }));
    stompClient.send("/app/queue", {}, JSON.stringify({
        'username': $("#name").val(),
        'content': "hhhhh"
    }));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {

        connect();
        /*
    	$.ajax({
            url: "/link?name="+document.getElementById('user').value,
            method: 'GET',
            success: (response) => {
                console.log(response)
                connect();
            }
        });*/

    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
    $("#test").click(function () {
        totest();
    });
});

function totest() {
    console.log(Qs)
    axios.post('/user/login', Qs.stringify({
        name: 'luo',
        password: '13456'
    })).then(res=>{
        if(res.data.length == 1){
            console.log("登录成功",res)

        }
    })
    // let name = 'Y';
    // let email = 'email';
    // let password = '1';
    // let uid = '732522';
    // axios.get(`/user/register?name=${name}&email=${email}&password=${password}&uid=${uid}`).then(res=>{
    //     console.log(res)
    // })

    // let name = 'luo';
    // let avater = '13456a';
    // let uid = '456451a';
    // axios.get(`/line/register?name=${name}&avater=${avater}&uid=${uid}`).then(res=>{
    //     console.log(res)
    // })
    

}