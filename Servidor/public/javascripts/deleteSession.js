function deleteSession(session) {   
    var xhttp = new XMLHttpRequest();
    
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {   
            $('#sucess').show();
            $('#insucess').hide();
            window.location.replace("/admin/deletesession");
        }
        if (xhttp.readyState == 4 && xhttp.status == 404) {
            $('#insucess').show();
            $('#sucess').hide();
        }
    };

    xhttp.open("DELETE", "/admin/deletesession/"+session, true);
    //xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(null);
    //alert ("xhttp.status:"+xhttp.status + "/ xhttp.responseText:"+ xhttp.responseText)
}
