// A ser usado com o módulo nodeunit!
// É necessário corre o módulo init.js, de forma a ter os utilizadores usados nos testes
// Testa a Api localmente. Para testar no heroku tem de se remover o PORT!

'use strict'
let http = require("http");
let API_HOST="localhost";
let PORT="3010"
let token;

function pedido (options, cb, post_data) {
    let req = http.request(options, processResponse);
    req.on('error', (e) => {
        console.log(`problem with request: ${e.message}`);
    });
    if (post_data){req.write(post_data);}
    req.end();

    function processResponse(res) {
        let response = "";
        res.on('data', chunk => response += chunk);
        res.on('end', () => cb(null,JSON.parse(response)));
        res.on('error', (e) => cb(e));
        //console.log(response);
    }
}


exports.unauthenticatedTests= {
    /**
     * Testa pedido à Api sem passar o token
     */
    useApiWithoutToken:function(test) {
        
        pedido ({   host: API_HOST, 
                    method:"GET", 
                    port:PORT,
                    path: "/api/users"} , function (err,data){
                        test.equal(data.success, false, 'The response should be:{"success":false,"message":"No token provided."}' );
                        test.equal(data.message, "No token provided.", 'The response should be:{"success":false,"message":"No token provided."}');
                        test.done();
                    });
    },
    

    /**
     * Testa o Pedido de Autenticação com credenciais do Administrador
     */
    administratorAutentication: function(test) {
        var post_data = "email=admin&pass=123"; 

        var opt = { host: API_HOST, 
                                  path: "/api/authenticate",
                                  port:PORT,
                                  method:"POST",
                                  headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded',
                                    'Content-Length': Buffer.byteLength(post_data)
                                    }
                    } ;
        pedido (opt, function(err,data){
            test.equal(data.success, true, 'Verify if server is running and admin is on database!' );
            test.equal(data.message, "Jose Augusto");
            test.done();
        },post_data);
    
    }
    
} 

exports.authenticatedTests = {
    setUp: function (callback) {
        if (!token){
            var post_data = "email=admin&pass=123";  
            let opt = { host: API_HOST, path: "/api/authenticate",port:PORT,
                                    method:"POST",
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded',
                                        'Content-Length': Buffer.byteLength(post_data)
                                    }
                        };
            pedido (opt, function(err,data){token=data.token;}, post_data);
            setTimeout(function() {             // COMO ESPERAR O RESULTADO DE OUTRA FORMA...??
                callback();
            }, 2000);
        } else callback();
    },
    tearDown: function (callback) {
        //console.log("Usar se necessário...");
        callback();
    },
    
    getUsersTest : function(test) { 
        let opt =   { host: API_HOST, port:PORT,
                                method:"GET",     // não é necessário especificar...
                                headers: {'x-access-token': token},
                                path: "/api/users"} ;
        pedido (opt, function (err,data){
            test.ok(data.users, "Verify in DB if exists one user...");
                test.equal(data.users[0].email,"admin");
                test.done();
        });
        
    },

    getSessionTest: function(test) {   
        let opt = { host: API_HOST, port:PORT,
                                headers: {'x-access-token': token},
                                path: "/api/sessions"};
        pedido (opt,function(err,data){
            test.ok(data.sessions, "Response doesn't have session field");
                test.ok(data.sessions[0].name, "Verify in DB if exists at least one session..."); 
                test.done();
        });
                
    },

    getDiscontinuitiesTest: function(test) {   
        let opt = { host: API_HOST, port:PORT,
                                headers: {'x-access-token': token},
                                path: "/api/discontinuities"}
        pedido (opt, function(err,data){
            test.ok(data.discontinuities, "Response doesn't have discontinuities field...");
                test.ok(data.discontinuities[0].id, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].idUser, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].idSession, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].direction, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].dip, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].latitude, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].longitude, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].persistence, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].aperture, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].roughness, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].infilling, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].weathering, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].note, "Verify in DB if exists at least one discontinuity...");
                test.ok(data.discontinuities[0].datetime, "Verify in DB if exists at least one discontinuity...");
                test.done();
        });
    },

    getSummaryTest: function(test) { 
        let opt =  {host: API_HOST, 
                    port:PORT,
                    headers: {'x-access-token': token},
                    path: "/api/summary"
                    }
        pedido (opt,function(err, r){
            test.ok(r.summary, "Response doesn't have discontinuities field...");
                test.ok(r.summary[0].NumUsers, "Verify in DB if exists at least one discontinuity...");
                test.ok(r.summary[0].NumSessions, "Verify in DB if exists at least one discontinuity...");
                test.ok(r.summary[0].NumDiscontinuities, "Verify in DB if exists at least one discontinuity...");
                test.done();
        });
    },

    postAndDeleteUser: function(test) {
        let post_data = '{"username":"José António","email":"xpto@xpto.com", "password":"123"}';  
        let opt = { host: API_HOST, path: "/api/users",port:PORT,
                                method:"POST",
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Content-Length': Buffer.byteLength(post_data),
                                    'x-access-token': token
                                }
                    }
        pedido(opt, function(err,r){
            test.equal(r.message, "ok");  // if the user dont exists
           
            pedido (opt, function(err,data){
                test.equal(data.message, "User already exists!");  // if the user exists
                

                let opt2 = { host: API_HOST, path: "/api/deleteuser/xpto@xpto.com",port:PORT,
                                method:"DELETE",
                                headers: {
                                    'x-access-token': token
                                }
                    }
                pedido (opt2,function(err,d){
                    //console.log (d.message);
                    test.ok(d.message)
                    test.done();
                }, null)
            },post_data);
            
        },post_data);
    }

}