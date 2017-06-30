
function addUser() { 
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    let user = $("#username").val();
    
    if (!re.test(user)){
        $("#wrongMail").show();
        setTimeout(function() { $("#wrongCred").hide();window.location.replace("/admin/user"); }, 1000);
        
        
    } else{
        var xhttp = new XMLHttpRequest();
    
        xhttp.onreadystatechange = function () {
                //alert ("xhttp.status:"+xhttp.status + "/ xhttp.responseText:"+ xhttp.responseText)
                //alert ("xhttp.status:"+xhttp.status)
                if (xhttp.readyState == 4 && xhttp.status == 201) {   
                    $('#sucess').show();
                    setTimeout(function() { $("#sucess").hide();window.location.replace("/admin/user"); }, 1000);
                }
                if (xhttp.readyState == 4 && xhttp.status == 500) {
                    $('#insucess').show();
                    setTimeout(function() { $("#insucess").hide();window.location.replace("/admin/user"); }, 1000);
                }
            };

        xhttp.open("POST", "/admin/adduser", true);
        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhttp.send("username=" + user);
    }
     
}
