'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
function getCurrentChatroomIdCookie(){
return document.cookie.split(";").map(e =>{return e.trim()}).map(e => {return e.split("=")}).at(0).at(1)
}
function getLoggedinUseridCookie() {
 return document.cookie.split(";").map(e =>{return e.trim()}).map(e => {return e.split("=")}).at(1).at(1)
}
function getLoggedinUsernameCookie() {
 return document.cookie.split(";").map(e =>{return e.trim()}).map(e => {return e.split("=")}).at(2).at(1)
}
window.onload = function(event) {
    username = getLoggedinUsernameCookie();
    console.log("logged in user: " + username);

    if(username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();


}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",{},JSON.stringify({sender: username, type: 'SYSTEM_INFO'})
    )

    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    console.log("The message content is: "+messageContent);
    if(messageContent && stompClient) {
        console.log("Constructing frontend Message");
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'TEXT'
        };
        stompClient.send("/topic/public", {}, JSON.stringify(chatMessage));
        console.log("Constructing backend Message");
        var chatMessageBackend = {
            senderId: getLoggedinUseridCookie(),
            content: messageInput.value,
            type: 'TEXT'
        }
        console.log("Message input: "+messageInput.value);
        var request = new XMLHttpRequest();
        request.open("POST","http://localhost:8090/postMessageToSpring", true);
        request.setRequestHeader('Content-Type', 'application/json');
        request.send(JSON.stringify(chatMessageBackend));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    console.log(payload);
    console.log(message.type);
    if(message.type === 'SYSTEM_INFO') {
        console.log("message of SYSTEM_INFO has been received")
        messageElement.classList.add('event-message');
        message.content = message.sender.username + ' joined!';
        console.log("Message sender: "+message.sender+"Message sender . username: ");
    } else if (message.type === 'MEDIA') {
        console.log("message of MEDIA has been received")
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        console.log("message of text type has been received");
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        console.log(avatarText);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}



function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

messageForm.addEventListener('submit', sendMessage, true)