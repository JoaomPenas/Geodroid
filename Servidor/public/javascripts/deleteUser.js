function deleteUser(user) {   
    var xhttp = new XMLHttpRequest();
    
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {   
            $('#sucess').show();
            $('#insucess').hide();
            window.location.replace("/admin/deleteuser");
        }
        if (xhttp.readyState == 4 && xhttp.status == 404) {
            $('#insucess').show();
            $('#sucess').hide();
        }
    };

    xhttp.open("DELETE", "/admin/deleteuser/"+user, true);
    //xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(null);
    //alert ("xhttp.status:"+xhttp.status + "/ xhttp.responseText:"+ xhttp.responseText)

    document.getElementById('password').onkeypress=function(e){
        if(e.keyCode==13){
            document.getElementById('bttLogin').click();
        }
    }
}
