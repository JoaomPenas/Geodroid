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
    * @param {string} email - O email do ustilizador  
    * @param {string} password - A password do ustilizador  
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
     * @param {string} email - O email do ustilizador 
     */
    function GetUser(email, cb){
        dal.getUser(email, cb);
    }

    /**
     * Função usada para remover um utilizador
     * @param {string} email - O email do ustilizador 
     */
    function DeleteUser(email, cb){
        dal.deleteUser(email, cb);
    }

    /**
     * Função usada para remover uma sessão
     * @param {string} session - A sessão
     */
    function DeleteSession(session, cb){
        dal.deleteSession(session, cb);
    }

    /**
     * O resultado é um objecto com o resumo de todos os utilizadores de acordo com o seguinte exemplo:
     *  {users:[{email:"w@mail.com",pass:"F1mZmpiuwK6b83ODjaS9r/1x7uWC+oSfrwd/eg4qxW0=",salt:114},
     *          {email:"z@mail.com",pass:"FG/+qep17U1wOphGUb8wnJ22I6ff4y7fE1lJx6KVm3E=",salt:113}]}
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
     * O resultado é um objecto com o resumo de todas as sessões de acordo com o seguinte exemplo:
     *  {sessions:[{session:"Arrabida",numUsers:2,numDiscont:50},
     *             {session:"FozCoa",numUsers:5,numDiscont:30}]}
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
     * (Função não encontra-se a ser utilizada)
     * var sess= {sessions: [{name:"Arrabida"}, {name:"FozCoa"}]}
     */
    function GetAllSessions(err, cb){
            dal.readAll("sessions",function(err,res){
                if(err) { cb("An error ocurred...please verifify your connection."); }
                else {cb(null, {sessions:res});}
            });
    }

    function GetDiscontinuitiesOfOneUser(user, cb){
        dal.getDiscontinuitiesOfOneUser (user, function (err, res){
             if(err) { cb("An error ocurred...please verifify your connection."); }
             else {cb(null, {discontinuities:res});}
        });
    }

    /**
     * O resultado é um objecto com as descontinuidades de uma sessão, de acordo com o seguinte exemplo:
     * var desc= {discontinuities: [{id:1, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2},
     * 					            {id:2, idUser: "w@mail.com", idSession: "Arrabida", direction: 59, dip: 44, latitude: 38.52, longitude: -8.99, persistence: 2, aperture: 4, roughness: 1, infilling: 2, weathering: 2}]} 
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
     */
    function GetAllDiscontinuities (err, cb){
        dal.readAll("discontinuities", function (err,res){
            if(err) { cb("An error ocurred...please verifify your connection."); }
            else {cb (null,{discontinuities:res});}
        });
    }

    /**
     * O resultado é um objecto semelhante ao seguinte:
     * { summary: [ RowDataPacket { NumUsers: 3, NumSessions: 4, NumDiscontinuities: 15 } ] }
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
     * Função para inserir um conjunto de descontinuidades (usada pelo controlador da Web Api)
     * @param {object} disc - objecto semelhante ao seguinte:
     * { "discontinuities": [{"aperture":5,"dip":66,"direction":50,"id":100,"infilling":5,"latitude":38,"longitude":9,"persistence":5,"roughness":5,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 },
     *                       {"aperture":2,"dip":66,"direction":50,"id":101,"2nfilling":4,"latitude":38,"longitude":9,"persistence":1,"roughness":3,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 } ]}
     */
    function PostDiscontinuities (disc, cb){
        dal.postDiscontinuities (disc, cb);
    }

    /**
     * 
     */
    function Authenticate (email, password, done) {

			// procura um user cujo email é o mesmo que o formulário
			// esta-se a verificar para ver se o user que está a tentar fazer
			// login já existe

			// finduser (email, function(err, user) {
	
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
				    
					console.log("pass+salt"+password+salt); 
					console.log ("Generated hash: "+hash);
					console.log ("uPass: "+user.password);

					if (hash !== user.password) {
							console.log ("invalid password!");
							return done(null, null);
					}
					console.log ("Correct Password !");
					return done(null, user);
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
        getDiscontinuitiesOfOneUser: GetDiscontinuitiesOfOneUser,
        getDiscontinuitiesFromOneSession:GetDiscontinuitiesFromOneSession,
        getAllDiscontinuities:GetAllDiscontinuities,
        postDiscontinuities:PostDiscontinuities,
        getSummary:GetSummary,
        getAllUsersResumedInformation:GetAllUsersResumedInformation,
        getAllSessionsResumedInformation:GetAllSessionsResumedInformation,
        authenticate:Authenticate
    }
}