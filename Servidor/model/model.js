'use strict';
const crypto 		= require('crypto');

function User(email, password){
    this.email=email;
    this.password=password;
}

module.exports=function(_dal){
    const dal=_dal;

    /**
    * Função usada na área de administração, para criação de um novo utilizador
    * (Usada pelo api-controler e web-controler)
    * @param {string} email - O email do ustilizador  
    * @param {string} password - A password do ustilizador  
    * @param {function} cb - função de callback
    */
    function CreateUser(email, password, cb){
        if (email=="") {cb("invalid email");}
        else{
            var user=new User(email, password);
            dal.postUser (user, cb);
        }
    }
    
    /**
     * Função usada para Login
     * (Usada apenas pelo webcontroler)
     * @param {string} email - designação do utilizador 
     * @param {function} cb - função de callback
     */
    function GetUser(email, cb){
        dal.getUser(email, cb);
    }

    /**
     * Função usada para remover um utilizador
     * (Usada pelo api-controler e web-controler)
     * @param {string} email - designação do utilizador 
     * @param {function} cb - função de callback
     */
    function DeleteUser(email, cb){
        dal.deleteUser(email, cb);
    }

    /**
     * ---------------------------usar também na web api!--------------------
     * Função usada para remover uma sessão
     * (usada apenas pelo webapp-controler)
     * @param {string} session - designação da sessão
     * @param {function} cb - função de callback
     */
    function DeleteSession(session, cb){
        dal.deleteSession(session, cb);
    }

    /**
     * O resultado é um objecto com o resumo de todos os utilizadores de acordo com o seguinte exemplo:
     * (Usado pelo apenas pelo api-controler)
     *  {users:[{email:"w@mail.com",pass:"F1mZmpiuwK6b83ODjaS9r/1x7uWC+oSfrwd/eg4qxW0=",salt:114},
     *          {email:"z@mail.com",pass:"FG/+qep17U1wOphGUb8wnJ22I6ff4y7fE1lJx6KVm3E=",salt:113}]}
     * @param {function} cb - função de callback
     */
    function GetAllUsers(err, cb){
            dal.readAll("users", function(err,res){
                if(err) { cb("An error ocurred...please verifify your connection."); }
                else { cb(null, {users:res});}
            });
    }

    /**
     * O resultado é um objecto com o resumo de todas as sessões de acordo com o seguinte exemplo:
     * {users:[{email:"w@mail.com",sessions:2,discont:50},
     *         {email:"z@mail.com",sessions:5,discont:30}]}
     * (Usado apenas pelo webapp-controler)
     * @param {function} cb - função de callback
     * @param {boolean} onlyContributers - flag para determinar se se pretende (ou não) apenas user's com descontinuidades
     */
    function GetAllUsersResumedInformation(err, cb, onlyContributers){ 
            dal.getUserSummary( function(err,res){ 
                
                if(err) { cb("An error ocurred...please verifify your connection."); }
                else {
                    cb(null, {users:res});
                }
            },onlyContributers);
    }

    /**
     * Retorna um objecto com o resumo de todas as sessões de acordo com o seguinte exemplo:
     *  {sessions:[{session:"Arrabida",numUsers:2,numDiscont:50},
     *             {session:"FozCoa",numUsers:5,numDiscont:30}]}
     * (usado apenas pelo webapp-controler)
     * @param {function} cb - função de callback
     */
    function GetAllSessionsResumedInformation(err, cb){ 
            dal.getSessionSummary( function(err,res){ 
                
                if(err) { cb("An error ocurred...please verifify your connection."); }
                else {
                    cb(null, {sessions:res});
                }
            });
    }

    /**
     * O resultado é um objecto com todas as descontinuidades, de acordo com o seguinte exemplo:
     * (Função usada apenas pelo api-controler)
     * var sess= {sessions: [{name:"Arrabida"}, {name:"FozCoa"}]}
     */
    function GetAllSessions(err, cb){
            dal.readAll("sessions",function(err,res){
                if(err) { cb("An error ocurred...please verifify your connection."); }
                else {cb(null, {sessions:res});}
            });
    }


    /**
     * * ---------------------A ser descontinuado...---------------------------
     * O resultado é um objecto com as descontinuidades de uma sessão, de acordo com o seguinte exemplo:
     * var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
     * 					            {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]} 
     * (usado apenas pelo api-controler)
     * @param {string} session - nome da sessão
     * @param {function} cb - função de callback
     */
    function GetDiscontinuitiesFromOneSession (session, cb){
        dal.readDiscontinuitiesFromOneSession (session, function (err, res){
             if(err) { cb("An error ocurred...please verifify your connection."); }
             else {cb(null, {discontinuities:res});}
        });
    }

    /**
     * O resultado é um objecto com todas as descontinuidades, de acordo com o seguinte exemplo:
     *     var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
	 *				  	                {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]}
     * (Função usada pela rota gmaps)
     * @param {function} cb - função de callback
     * @param {int} page (opcional) - numero da página pretendida
     * @param {int} numPerPage (opcional) - numero de elementos por página
     */
    function GetAllDiscontinuities (err, cb, page, numPerPage){
        var paged=false;
        dal.readAll("discontinuities", function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {cb (null,{discontinuities:res});}
        });
    }

    /**
     * O resultado é um objecto com todas as descontinuidades, de acordo com o seguinte exemplo:
     *     var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
	 *				  	                {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]}
     * (Usado pelo api-controler e webapp-controler)
     * @param {function} cb - função de callback
     * @param {int} page - numero da página pretendida
     * @param {int} numPerPage - numero de elementos por página (usado para limitar os resultados)
     */
    function GetPagedDiscontinuities (err, page, numPerPage, cb ){
        var paged=true;
        dal.readAll("discontinuities", function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {cb (null,{discontinuities:res});}
        }, paged, page, numPerPage);
    }


    /**
     * O resultado passado ao cb é um objecto com as descontinuidades da pagina, de acordo com o seguinte exemplo:
     *     var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
	 *				  	                {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]}
     * (Usado apenas pelo webapp-controler)
     * @param {string} user - designacao do utilizador
     * @param {int} page - numero da página pretendida
     * @param {int} numPerPage - numero de elementos por página (usado para limitar os resultados)
     * @param {function} cb - função de callback
     */
    function GetPagedDiscontinuitiesOfOneUser(err, user, page, numPerPage, cb ){
        dal.getPagedDiscontinuitiesOfOneUserOrSession(null, "user", user, page, numPerPage, function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {cb (null,{discontinuities:res});}
        });
    }

    /**
     * O resultado passado ao cb é um objecto com as descontinuidades da pagina, de acordo com o seguinte exemplo:
     *     var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
	 *				  	                {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]}
     * (Usado apenas pelo webapp-controler)
     * @param {string} session - designacao da sessão
     * @param {int} page - numero da página pretendida
     * @param {int} numPerPage - numero de elementos por página
     * @param {function} cb - função de callback
     */
    function GetPagedDiscontinuitiesOfOneSession(err, session, page, numPerPage, cb ){
        dal.getPagedDiscontinuitiesOfOneUserOrSession(null, "session", session, page, numPerPage, function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {cb (null,{discontinuities:res});}
        });
    }

    /**
     * O resultado é um objecto semelhante ao seguinte:
     * { summary: [ RowDataPacket { NumUsers: 3, NumSessions: 4, NumDiscontinuities: 15 } ] }
     * (Usado apenas pelo webapp-controler)
     * @param {function} cb - função de callback
     */
    function GetSummary (err, cb){
        dal.getSummaryCount(function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {
                console.log({summary:res})
                cb (null,{summary:res});
            }
        });
    }

    /**
     * Função para inserir um conjunto de descontinuidades 
     * (usada apenas pelo api-controler)
     * @param {object} disc - objecto semelhante ao seguinte:
     * { "discontinuities": [{"aperture":5,"dip":66,"direction":50,"id":100,"infilling":5,"latitude":38,"longitude":9,"persistence":5,"roughness":5,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 },
     *                       {"aperture":2,"dip":66,"direction":50,"id":101,"2nfilling":4,"latitude":38,"longitude":9,"persistence":1,"roughness":3,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 } ]}
     * @param {function} cb - função de callback
     */
    function PostDiscontinuities (disc, cb){
        dal.postDiscontinuities (disc, cb);
    }

    /**
     * Função de autenticação de um utilizador
     * (Usada pelo api-controler e passport)
     * @param {string} email - designação do utilizador
     * @param {string} password - password do utilizador
     * @param {function} done - função de callback
     */
    function Authenticate (email, password, done) {

			// procura um user cujo email é o mesmo que o formulário
			// esta-se a verificar para ver se o user que está a tentar fazer
			// login já existe
	
			GetUser(email, function(err, user) {
				// if there are any errors, return the error
				if (err)  return done(err);
				
				// check to see if theres already a user with that email
				if (!user) {
					return done(new Error ('Invalid user!'));
				} 
				else {
                    
					let salt = user.salt; 
					
					const hashC = crypto.createHash('sha256');
					hashC.update(password+salt);
					let hash = hashC.digest('base64');
				    
					//console.log("pass+salt"+password+salt); 
					//console.log ("Generated hash: "+hash);
					//console.log ("uPass: "+user.password);

					if (hash !== user.password) {
							console.log ("invalid password!");
							return done(null, null);
					}
					console.log ("Correct Password !");
					return done(null, user);
				}
			});   
    }

    /**
     * Obtém o número de páginas (para todas as descontinuidades):
     * (Usada pelo api-controler e webapp-controler)
     * @param {function} cb - função de callback
     * @param {int} numPerPage - número de descontinuidades por página
     */
    function GetNumberOfDiscontinuitiesPages (err, cb, numPerPage){
        dal.getSummaryCount(function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {
                var numDiscont = res[0].NumDiscontinuities;
                var numPages = Math.ceil(numDiscont/numPerPage);    
                cb (null,numPages);
            }
        });
    }

    /**
     * Obtém o número de páginas (para todas as descontinuidades de um determinado utilizador):
     * (Usada apenas pelo webapp-controler)
     * @param {string} type - can be only "user" or "session""
     * @param {string} name - the name/identification of the user or the session
     * @param {int} numPerPage - número de descontinuidades por página
     * @param {function} cb - função de callback
     */
    function GetNumOfPages (err, type, name, numPerPage, cb){
        dal.getNumOfDiscOfOneUserOrSession(null, type, name, numPerPage, function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {
                var numPages = Math.ceil(res/numPerPage);
                cb (null,numPages);
            }
        });
    }

    return {
		createUser:	CreateUser,
        getUser:   GetUser,
        deleteUser: DeleteUser,
        getAllUsers: GetAllUsers,
        getAllSessions:GetAllSessions,
        deleteSession: DeleteSession,
        getDiscontinuitiesFromOneSession:GetDiscontinuitiesFromOneSession,
        getAllDiscontinuities:GetAllDiscontinuities,
        getPagedDiscontinuities:GetPagedDiscontinuities,
        getPagedDiscontinuitiesOfOneUser:GetPagedDiscontinuitiesOfOneUser,
        getPagedDiscontinuitiesOfOneSession:GetPagedDiscontinuitiesOfOneSession,
        postDiscontinuities:PostDiscontinuities,
        getSummary:GetSummary,
        getAllUsersResumedInformation:GetAllUsersResumedInformation,
        getAllSessionsResumedInformation:GetAllSessionsResumedInformation,
        authenticate:Authenticate,
        getNumberOfDiscontinuitiesPages:GetNumberOfDiscontinuitiesPages,
        getNumOfPages:GetNumOfPages
    }
}