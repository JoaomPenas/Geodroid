
function addUser() { 

    let user = $("#username").val();
    let password = $("#password").val();
    if (user ==="" || password===""){
        $("#wrongCred").show();
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
        xhttp.send("username=" + user + "&password=" + password);
    }
     
}
