var stompClient = null;
var gameid;
var estado;
var ganador;
function sendRequest() {
    gameid=$("#gameid").val();
    console.log("/hangmangames/hiddenwords/"+gameid);
    $.get( "/hangmangames/hiddenwords/"+gameid, 
        function( data ) {      
            $("#palabra").html("<h1>"+data+"</h1>");
        }    
    ).fail(
        function(data){
            alert(data["responseText"]);
        }
            
    );
    getEstado().then(getGanador).then(actualizarEstado);
    disconnect();
    connect();

}

function connect() {
    var socket = new SockJS('/stompendpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        
        console.log('Connected: ' + frame);
        
        stompClient.subscribe('/topic/wupdate.'+gameid, function (word) {
            console.log(word);
            data=word.body;
            console.log(data);
            $("#palabra").html("<h1>"+data+"</h1>");
        });
        
        stompClient.subscribe('/topic/winner.'+gameid, function (winner) {
            console.log(winner);
            data=winner.body;
            console.log(data);
            ganador=data;
            estado="Terminado";
            getEstado().then(getGanador).then(actualizarEstado);
        });
        
    });
}
function actualizarEstado(){
    $("#status").html('<div>Estado: '+estado+'</div><div>Ganador: '+ganador+'.</div>');
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    //setConnected(false);
    console.log("Disconnected");
}

function ingresarCaracter(){
    console.log("char "+$("#caracter").val());
    putChar();
}

function ingresarPalabra(){
    console.log("try word "+$("#adivinanza").val());
    putWord();
}

function putChar(){
    return $.ajax({
        url: "/hangmangames/hiddenwords/"+gameid,
        type: 'PUT',
        data: JSON.stringify($("#caracter").val()),
        contentType: "application/json"
    });
}
function putWord(){
    return $.ajax({
        url: "/hangmangames/hiddenwords/"+gameid+"/"+$("#name").val(),
        type: 'PUT',
        data: JSON.stringify($("#adivinanza").val()),
        contentType: "application/json"
    });
}
function getEstado(){
    return $.get( "/hangmangames/hiddenwords/"+gameid+"/status", 
        function( data ) {      
            estado=data;
        }    
    )
}
function getGanador(){
    return $.get( "/hangmangames/hiddenwords/"+gameid+"/winner", 
        function( data ) {      
            ganador=data;
        }    
    )
}

$(document).ready(
        function () {
            connect();
            console.info('connecting to websockets');
        }
);
