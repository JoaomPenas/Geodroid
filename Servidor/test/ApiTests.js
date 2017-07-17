// A ser usado com o módulo nodeunit!
// É necessário correr o módulo init.js, de forma a ter os utilizadores usados nos testes
// Testa a Api (a correr no Heroku)!

'use strict'
let http = require("https");
let API_HOST="sgeotest.herokuapp.com";
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
            let opt = { host: API_HOST, path: "/api/authenticate",
                                    method:"POST",
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded',
                                        'Content-Length': Buffer.byteLength(post_data)
                                    }
                        };
            pedido (opt, function(err,data){token=data.token;}, post_data);
            setTimeout(function() {       
                callback();
            }, 5000);
        } else callback();
    },

    tearDown: function (callback) {
        //console.log("Usar se necessário...");
        callback();
    },
    /**
     * Testa a obtenção do conjunto de utilizadores (campos email, data, pass e salt)
     */
    getUsersTest : function(test) { 
        let opt =   { host: API_HOST, 
                                method:"GET",     
                                headers: {'x-access-token': token},
                                path: "/api/users"} ;
        pedido (opt, function (err,data){
            test.ok(data.users, "Verify in DB if exists one user...");
            test.ok(data.users[0].email);
            test.ok(data.users[0].name);
            test.ok(data.users[0].pass);
            test.ok(data.users[0].salt);
            test.ok(data.users[1].email);
            test.ok(data.users[1].name);
            test.ok(data.users[1].pass);
            test.ok(data.users[1].salt);
            test.done();
        });
        
    },

    /**
     * Testa a obtenção do conjunto de sessões (e campo name)
     */
    getSessionTest: function(test) {   
        let opt = { host: API_HOST, 
                                headers: {'x-access-token': token},
                                path: "/api/sessions"};
        pedido (opt,function(err,data){
            test.ok(data.sessions, "Response doesn't have session field");
            test.ok(data.sessions[0].name, "Verify in DB if exists at least one session..."); 
            test.ok(data.sessions[1].name, "Verify in DB if exists at least two sessions..."); 
            test.done();
        });
                
    },

    /**
     * Testa a obtenção do conjunto de descontinuidades (e respetivos campos) 
     */
    getDiscontinuitiesTest: function(test) {   
        let opt = { host: API_HOST,
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

                test.ok(data.discontinuities[1].id, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].idUser, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].idSession, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].direction, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].dip, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].latitude, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].longitude, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].persistence, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].aperture, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].roughness, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].infilling, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].weathering, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].note, "Verify in DB if exists at least two discontinuities...");
                test.ok(data.discontinuities[1].datetime, "Verify in DB if exists at least two discontinuities...");
                test.done();
        });
    },

    /**
     * Testa o endpoint que dá o resumo da BD (numero de utilizadores, sessões e descontinuidades)
     */
    getSummaryTest: function(test) { 
        let opt =  {host: API_HOST, 
                    headers: {'x-access-token': token},
                    path: "/api/summary"
                    }
        pedido (opt,function(err, r){
            test.ok(r.summary);
            test.ok(r.summary[0].NumUsers);
            test.ok(r.summary[0].NumSessions);
            test.ok(r.summary[0].NumDiscontinuities);
            test.done();
        });
    },

    /**
     * Testa o endpoint que dá o resumo de um utilizador (número de descontinuidades e sessões)
     */
    getUserSummaryTest: function(test) { 
        let opt =  {host: API_HOST, 
                    headers: {'x-access-token': token},
                    path: "/api/usersummary/w@mail.com"
                    }
        pedido (opt,function(err, r){
            test.ok(r.summary);
            test.ok(r.summary[0].NumDiscontinuities);
            test.ok(r.summary[0].NumSessions);
                
            test.done();
        });
    },

    /**
     * Testa o endpoint que dá o  resumo de uma sessão (número de descontinuidades e utilizadores)
     */
    getSessionSummaryTest: function(test) { 
        let opt =  {host: API_HOST, 
                    headers: {'x-access-token': token},
                    path: "/api/sessionsummary/Arrabida"
                    };
        pedido (opt,function(err, r){
            test.ok(r.summary);
            test.ok(r.summary[0].NumDiscontinuities);
            test.ok(r.summary[0].NumUsers);
            
            test.done();
        });
    },

    /**
     * Testa a insersão de um utilizador e a respectiva remoção da BD.
     */
    postAndDeleteUser: function(test) {
        let post_data = '{"username":"José António","email":"xpto@xpto.com", "password":"123"}';  
        let opt = { host: API_HOST, path: "/api/users",
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
                test.equal(data.message, "User already exists!");  // if the user allready exists
                
                let opt2 = { host: API_HOST, path: "/api/deleteuser/xpto@xpto.com",
                                method:"DELETE",
                                headers: {
                                    'x-access-token': token
                                }
                    };
                pedido (opt2,function(err,d){
                    test.ok(d.message)
                    test.done();
                }, null)
            },post_data);
            
        },post_data);
    },

    /**
     * Testa a inserção de um conjunto de descontinuidades e a remoção da respetiva sessão.
     */
    postDiscontinuitiesAndDeleteSession: function(test) {
        let post_data = '{"discontinuities": [{ "id": 201, "idUser": "w@mail.com", "idSession": "Xpto", "direction": 59, "dip": 44, "latitude": 38.52, "longitude": -8.99, "persistence": 2, "aperture": 4, "roughness": 1, "infilling": 2, "weathering": 2, "note":"nota1","datetime":"2017-07-02 12:29:14" }, { "id": 202, "idUser": "x@mail.com", "idSession": "Xpto", "direction": 11, "dip": 111, "latitude": 1.1, "longitude": 1.2, "persistence": 2, "aperture": 2, "roughness": 2, "infilling": 2, "weathering": 2, "note":"nota2","datetime":"2017-07-02 12:29:15"}]}';  
        let opt = { host: API_HOST, 
                    path: "/api/discontinuities",
                    method:"POST",
                        headers: {
                            'Content-Type': 'application/json',
                            'Content-Length': Buffer.byteLength(post_data),
                            'x-access-token': token
                        }
                    };
        pedido(opt, function(err,r){
            test.equal(r.message, "ok"); 
            
            let opt2 = { host: API_HOST, path: "/api/deletesession/Xpto",
                            method:"DELETE",
                            headers: {
                                'x-access-token': token
                            }
                };
            pedido (opt2,function(err,d){
                test.equal(d.message,'session Xpto removida com sucesso!');
                test.done();
            }, null);
            
        },post_data);
    }
}