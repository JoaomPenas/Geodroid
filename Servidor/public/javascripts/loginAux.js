
function login() {
    let user = $("#username").val();
    let password = $("#password").val();
   
    var xhttp = new XMLHttpRequest();
    
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {   
            $('#sucess').show();
            setTimeout(function() { $("#insucess").hide();window.location.replace("/dashboard"); }, 100);
        }
        if (xhttp.readyState == 4 && xhttp.status == 401) {
            $('#insucess').show();
            setTimeout(function() { $("#insucess").hide();window.location.replace("/"); }, 1000);
        }
    };

    xhttp.open("POST", "/login", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("username=" + user + "&password=" + password);
    //alert ("xhttp.status:"+xhttp.status + "/ xhttp.responseText:"+ xhttp.responseText)

}

