'use strict';

var mysql      	= require('mysql');
const crypto 	= require('crypto');

var getConnection = function(_hostP, _userP, _passwordP, _databaseP){
					return mysql.createConnection({
  						host     : _hostP,
  						user     : _userP,
  						password : _passwordP,
  						database : _databaseP,
  						multipleStatements: true
					});
			}

// função auxiliar - para debug (para apagar)
function showObject(obj) {
    for (var k in obj) {
        console.log("o[\'" + k          // k       -> contem a string da chave
                + "\']= " + obj[k])     // obj[k]   -> retorna o valor da propriedade
    }
}

module.exports = function(hostP, userP, passwordP, databaseP){
	
	/**
	 * Remove um utilizador da base de dados (incluido as respectivas descontinuidades)
	 * @param {string} user - designação do utilizador
	 */
	function DeleteUser(user, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		if (user!='admin'){
			var str='START TRANSACTION;'+
					'delete from Discontinuity where idUser="'+user+'";'+
					'delete from User where email="'+user+'";'+
					'COMMIT;'
			connection.query(str, function(err, rows, fields) {
					if (!err) {
							cb (null, "utilizador "+user+" removido com sucesso!" );
					}
					else {
						cb ( "erro...a remover o utilizador "+user);
					}
			});
		}
		else {cb ("Não é possivel remover o utilizador "+user+"!");}
		connection.end();
	}

	/**
	 * Remove uma sessão da base de dados (incluido as respectivas descontinuidades)
	 * @param {string} session - designação da sessao
	 */
	function DeleteSession(session, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		var str='START TRANSACTION;'+
				'delete from Discontinuity where idSession="'+session+'";'+
				'delete from Session where name="'+session+'";'+
				'COMMIT;'
		connection.query(str, function(err, rows, fields) {
				if (!err) {
						cb (null, "session "+session+" removida com sucesso!" );
				}
				else {
					cb ( "erro...a remover a sessão "+session+"!");
				}
			});
		connection.end();
	}

	/**
	 * Vai buscar à base de dados um utilizador a partir da sua designação
	 * Se existir passa à função de callback um utilizador semelhante ao seguinte exemplo:
	 * {  username: 'x@mail.com',	
	 *    password: 'F1mZmpiuwK6b83ODjaS9r/1x7uWC+oSfrwd/eg4qxW0=',
	 *    salt: 316795907876104200
	 * }
	 * @param {string} email - o email do utilizador
	 */
	function GetUser(email, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		connection.query('SELECT * from User where email="'+email+'"', function(err, rows, fields) {
			let user;
			if(err){ cb(err);}
			else{
				if (rows[0]){ // A primeira row porque como o email é primary key  apenas existe uma row
					 user = {username: rows[0].email, password: rows[0].pass,salt: rows[0].salt}
				}
				else {
					user = {username: null,	password: null, salt: null }
				}
				cb(null,user);	
			}
    	}); 
		connection.end();
	}


	function GetDiscontinuitiesOfOneUser (user, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		connection.query('SELECT * from Discontinuity where idUser="'+user+'"'+" order by idSession", function(err, rows, fields) {
  			if (!err){ cb(null,rows);}	
  			else { cb(err);	}
    	}); 
		connection.end();
	}

	/**
	 * Devolve uma linha da tabela Discontinuity para determinada sessão
	 * @param {string} session - nome da sessão
	 */
	function ReadDiscontinuitiesFromOneSession (session, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		connection.query('SELECT * from Discontinuity where idSession="'+session+'"', function(err, rows, fields) {
  			if (!err){ cb(null,rows);}	
  			else { cb(err);	}
    				
    	}); 
		connection.end();
	}

	/**
	 * Lê a informação de determinada tabela (User, Session ou Discontinuities) consoante str passada
	 * @param {string} str - valores  possiveis para str: 'users', 'sessions' or 'discontinuities'
	 */
	function ReadAll(str,cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		var table;
		
		if (str=="users") 			table = "User";
		if (str=="sessions") 		table = "Session";
		if (str=="discontinuities") table = "Discontinuity";
		connection.query('SELECT * from '+table, function(err, rows, fields) {
			if (!err){ cb(null,rows)}		
			else {cb(err);}	
		});
	
		connection.end();
	}

	/**
	 * Devolve as contagens dos users, sessões e descontinuidades
	 * É passado um array de semelhante:
	 * [RowDataPacket { NumUsers: 3, NumSessions: 4, NumDiscontinuities: 15 } ]
	 */
	function GetSummaryCount (cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		connection.query('select count(distinct idUser) as NumUsers, count(distinct idSession) as NumSessions , count(id) as NumDiscontinuities from Discontinuity', function(err, rows, fields) {
			if (!err){ cb(null,rows)}
		});
		connection.end();
	}

	/**
	 * Devolve a informação sumaria de cada utilizador.
	 * É passado um array semelhante ao exemplo:
	 * [ RowDataPacket { user: 'admin', sessoes: 0, discont: 0 }, RowDataPacket { user: 'humberto', sessoes: 0, discont: 0 }]
	 * @param {boolean} onlyContributers - para determinar se é para retornar os utilizadores que apenas tem descontinuidades ou todos
	 */
	function GetUserSummary(cb, onlyContributers){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		let typeJoin;
		if (!onlyContributers){
			typeJoin ="LEFT JOIN";
		}else { 
			 typeJoin="RIGHT JOIN";
		}
		connection.query('select user, count(distinct sessoes) as sessoes, count(discont) as discont from (select  User.email as user, Discontinuity.idSession as sessoes,  Discontinuity.id as discont from User '+typeJoin+' Discontinuity ON User.email=Discontinuity.idUser) as X group by user', function(err, rows, fields) {
			if (!err){ 
				cb(null,rows)}
			});
		connection.end();
	}

	/**
	 * Devolve a informação sumaria de cada sessão.
	 * É passado um array semelhante ao exemplo:
	 * [ RowDataPacket { sessao: 'Arrabida', numUsers: 1, numDiscont: 5 }, RowDataPacket { sessao: 'session1', numUsers: 2, numDiscont: 7 }]
	 */
	function GetSessionSummary(cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();
		connection.query('select sessao, count(distinct user) as numUsers, count(descont) as numDiscont from (select Session.name as sessao, Discontinuity.idUser as user, Discontinuity.id as descont from Session LEFT JOIN Discontinuity  on Discontinuity.idSession=Session.name) as X group by sessao', function(err, rows, fields) {
			if (!err){ 
				console.log(rows);
				cb(null,rows)}
		});
		connection.end();
	}

	/**
     * Função para inserir um conjunto de descontinuidades (usada pelo controlador da Web Api)
	 * O parametro recebido em parametro traz o objecto que vem no body do pedido post (COM HEADER key:Content-Type / value:application/json)
	 * Cria uma string de código MySql para inseri esses dados nas tabelas correspondentes da base de dados. Sao inseridas as descontinuidades na tabela Discontinuity  e session na tabela Session
	 * Exemplo da String construida:
	 *   insert into Discontinuity (id, idUser, idSession, direction, dip, latitude, longitude, persistence, aperture, roughness, infilling, weathering) 
	 *   values (100,"w@mail.com","Oeiras",50,66,38,9,5,5,5,5,5) 
	 *   on duplicate key update persistence=5, aperture=5, roughness=5, infilling=5, weathering=5; 
     * @param {object} disc - objecto equivalente ao seguinte:
     * { "discontinuities": [{aperture:5,dip:66,direction:50,id:100,infilling:5,latitude:38,longitude:9,persistence:5,roughness:5,idSession:"Oeiras",idUser: "w@mail.com",weathering: 5 },
     *                       {aperture:2,dip:66,direction:50,id:101,infilling:4,latitude:38,longitude:9,persistence:1,roughness:3,idSession:"Oeiras",idUser: "w@mail.com",weathering: 5 } ]}
     */
	function PostDiscontinuities(disc, cb){
		var connection = getConnection(hostP, userP, passwordP, databaseP);
    	connection.connect();

    	// Construção da string (comando MySql) "insert into Discontinuity..."
		let insertBaseStr = "insert into Discontinuity (id, idUser, idSession, direction, dip, latitude, longitude, persistence, aperture, roughness, infilling, weathering) values "
    	let tempStr="";

		disc.discontinuities.forEach(d=>{
			tempStr = `${tempStr} ${insertBaseStr}(${d.id}\,"${d.idUser}","${d.idSession}",${d.direction},${d.dip},${d.latitude},${d.longitude},${d.persistence},${d.aperture},${d.roughness},${d.infilling},${d.weathering}) on duplicate key update persistence=${d.persistence}, aperture=${d.aperture}, roughness=${d.roughness}, infilling=${d.infilling}, weathering=${d.weathering};`
		})
		//console.log(tempStr);
		
		/* Forma alternativa mais convencional para construir a string:
    	for (var d in disc.discontinuities){		// dá indices
    		tempStr=tempStr 
    			+ "(" +disc.discontinuities[d].id 
    			+" ," + "\""+disc.discontinuities[d].idUser + "\""
    			+" ," + "\""+disc.discontinuities[d].idSession+ "\""
    			+" ," + disc.discontinuities[d].direction
    			+" ," + disc.discontinuities[d].dip
    			+" ," + disc.discontinuities[d].latitude
    			+" ," + disc.discontinuities[d].longitude
    			+" ," + disc.discontinuities[d].persistence
    			+" ," + disc.discontinuities[d].aperture
    			+" ," + disc.discontinuities[d].roughness
    			+" ," + disc.discontinuities[d].infilling
    			+" ," + disc.discontinuities[d].weathering
    			+ ")"+ ","
    	}	
    	insertBaseStr= (insertBaseStr + tempStr);
		insertBaseStr=insertBaseStr.substring(0, insertBaseStr.length - 1);//concatenates the 2 string's and  removes last char from string
    	*/

    	// Construção da string (comando MySql) "insert into Session..."
    	var insertStrp="insert ignore into Session (name) values ( \""+disc.discontinuities[0].idSession+"\"); ";

		connection.query(insertStrp+tempStr, function(err, rows, fields) {
  			if (!err){ cb (null, "ok");	}
  			else { cb('Error...');}	
    	}); 

		connection.end();
	}

	/**
	 * Funçã para inserir um novo utilizador
	 * @param {Object} user - The user to post e.g. {email: 'admin', password: '567'}
	 */
	function PostUser(user, cb){
		GetUser(user.email, function(err, getteduser){  // e.g. getteduser { username: 'xxx', password: 'YMWLb+JeNk5XJSvNWx6ztzIPDWBfbTuv9gjmOJnVbiU=', salt: 5486517349989782000 }
			if (getteduser.username != null) {
				cb("User already exists!");
			}
			else{
				var connection = getConnection(hostP, userP, passwordP, databaseP);
    			connection.connect();
				let salt = Math.floor((Math.random() * 0x7FFFFFFFFFFFFFFF));
				const hashC = crypto.createHash('sha256');
				hashC.update(user.password+salt);
				let hash = hashC.digest('base64');

				// construct the "insert into User" table string
				let insertStr = "insert ignore into User (email, pass, salt) values (\""
									+user.email+"\",\""+hash+"\",\""+salt+"\")";
				
				connection.query(insertStr, function(err, rows, fields) {
					if (!err){ cb (null,"ok");}
					else 	 { cb('Error...');}	
				}); 
				connection.end();
			}
		});
	}

	return {
        deleteUser 							:DeleteUser,
		deleteSession 						:DeleteSession,
		getUser   							:GetUser,
        postDiscontinuities					:PostDiscontinuities,
		getDiscontinuitiesOfOneUser			:GetDiscontinuitiesOfOneUser,
		readDiscontinuitiesFromOneSession	:ReadDiscontinuitiesFromOneSession,	
		readAll								:ReadAll,						
		getSummaryCount						:GetSummaryCount,
		getUserSummary						:GetUserSummary,
		getSessionSummary					:GetSessionSummary,
		postUser							:PostUser
    };
}