'use strict'
const config  	= require('./../setup');

const numPerPage=config.apiNumPerPage;

module.exports = function(model, app) {          
    const router 		= require('express').Router();
	var jwt             = require('jsonwebtoken');      // used to create, sign, and verify tokens

	/**
	 * Rota para proceder à autenticação para uso da Api. 
	 * Responde um JsonWebToken em caso de sucesso.
	 */
	router.post('/api/authenticate', function (req,rsp,next){ 
		console.log(req.body);
		model.authenticate (req.body.email, req.body.pass, function(err, user){	// check if password matches
			if (err) {
				rsp.json({ success: false, message: 'Authentication failed' });
			} 
			else 
				if (!user){
					rsp.json({ success: false, message: 'Authentication failed. Wrong credentials!' });
				} else 
				if (user) {
					// if user is found and password is right create a token
					var token = jwt.sign({usermail:user.email}, app.get('superSecret'),{expiresIn:'24h'});				
					// return the information including token as JSON
					rsp.json({
						success: true,
						message: user.name,
						token: token
					});
				}
		});
	});

	/**
	 * Devolve a informação dos utilizadores
	 */
	router.get('/api/users', VerifyToken, function (req,rsp,next){
		model.getAllUsers(null,function (err, res){
			if (err){
				rsp.status(500).json({ error: err });
			}
			else 	{
				rsp.json(res);
			}
		});	
	});

	/**
	 * Rota para inserir um novo user (necessário enviar um Json semelhante ao seguinte
	 * exemplo: {"username":"António Xpto","email":"xpto@xpto.com", "password":"123"}
	 * Função apenas disponível para o administrador (admin)
	 */
	router.post('/api/users', VerifyToken, IsAdmin, function (req,rsp,next){    
	   model.createUser(req.body.username, req.body.email, req.body.password, function (err, message){
		   if (!err){
			   	rsp.status(201);
				rsp.json({message:message})
		   }
		   else{
			   rsp.status(500);
			   rsp.json({message:err})
		   }
		});
   	});

   /**
	* Rota para remover da base de dados um utilizador existente
	* Função apenas disponível para o administrador (admin)
    */
   router.delete('/api/deleteuser/:idUser', VerifyToken, IsAdmin,  function (req,rsp,next){
	
		model.deleteUser(req.params.idUser, function (err, message){
			if (err){
				rsp.status(404);
				rsp.json({message:err})
			}
			else {
				rsp.status(200);
				rsp.json({message:message})
			}	
		});
   });

	/**
	 * Devolve informação de todas as sessões
	 */
	router.get('/api/sessions', VerifyToken, function (req,rsp,next){
		model.getAllSessions(null,function (err, res){
			if (!err){
				rsp.json(res);
			}
			else{
				rsp.status(500).json({ error: err });
			}
		});
    });


   /**
	* Rota para remover da base de dados uma sessão existente (e respectivas descontinuidades)
	* Função apenas disponível para o administrador (admin)
    */
   router.delete('/api/deletesession/:idSession', VerifyToken, IsAdmin,  function (req,rsp,next){
	
		model.deleteSession(req.params.idSession, function (err, message){
			if (err){
				rsp.status(404);
				rsp.json({message:err})
			}
			else {
				rsp.status(200);
				rsp.json({message:message})
			}	
		});
   });

	 /**
	  * Devolve informação sobre o numero de descontinuidades, utilizadores e sessões
	  */
	router.get('/api/summary', VerifyToken, function (req,rsp,next){
		model.getSummary(null,function (err,res){
			if (err){
				rsp.status(500);
				rsp.json({message:err})
			}
			else {
				rsp.status(200).json(res)
			}
		},false);
	});


	 /**
	  * Devolve informação sobre o numero de descontinuidades e sessões de determinado utilizador
	  */
	router.get('/api/usersummary/:idUser', VerifyToken, function (req,rsp,next){
		model.getSummaryByUser(null,req.params.idUser, function (err,res){
			if (err){
				rsp.status(500);
				rsp.json({message:err})
			}
			else {
				rsp.status(200).json(res)
			}
		},false);
	});

 	/**
	  * Devolve informação sobre o número de descontinuidades e utilizadores de determinada sessão
	  */
	router.get('/api/sessionsummary/:idSession', VerifyToken, function (req,rsp,next){
		model.getSummaryBySession(null,req.params.idSession, function (err,res){
			if (err){
				rsp.status(500);
				rsp.json({message:err})
			}
			else {
				rsp.status(200).json(res)
			}
		},false);
	});

	/**
	 * Devolve informação de todas as descontinuidades de forma paginada
	 * Caso não seja definida a paginação é retornada a primeira pagina (pagina 0)
	 * Exemplo de utilização: localhost:3010/api/discontinuities?page=1
	 * Se tiver a página retorna a pagina; se não retorna 403
	 */
	router.get('/api/discontinuities', VerifyToken, function (req,rsp,next){
		var userPage= req.query.page===undefined? 0: req.query.page;

		model.getNumberOfDiscontinuitiesPages (null, function (err,numPages){
			if (err){rsp.sendStatus(500);}
			else{
				if (userPage >=numPages) {
					rsp.sendStatus(403);}	// não exitem tantas paginas quanto o solicitado!
				else{
					model.getPagedDiscontinuities (null,userPage,numPerPage, function (err,res){
								if (!err){
									rsp.json(res);
								}
								else{
									rsp.sendStatus(500);
								}
					});
				}
			}
		}, numPerPage);
	});

	/**	
	 * Devolve informação das descontinuidades de uma sessão de forma paginada
	 */
 	router.get('/api/discontinuities/session/:idSession', VerifyToken, function (req,rsp,next){
		var userPage= req.query.page===undefined? 0: req.query.page
		model.getNumOfPages (null,"session", req.params.idSession,numPerPage, (err,numPages)=>{
			 	if (err){
					rsp.status(500).json({ error: err });
				}
				else {
					if (userPage >=numPages) {
						rsp.sendStatus(403);}	// não exitem tantas paginas quanto o solicitado!
					else{
						model.getPagedDiscontinuitiesOfOneSession(null, req.params.idSession, userPage, numPerPage,function (err,res){
							if (!err){
								console.log ("o resultado é: "+res);
								rsp.json(res);
							}
							else{
								rsp.sendStatus(500);
							}
						});
					}
				}
		 } );
	});

	 /**	
	 * Devolve informação das descontinuidades de um utilizador de forma paginada
	 */
 	router.get('/api/discontinuities/user/:idUser' , VerifyToken, function (req,rsp,next){
		var userPage= req.query.page===undefined? 0: req.query.page
		model.getNumOfPages (null,"user", req.params.idUser,numPerPage, (err,numPages)=>{
			 	if (err){
					rsp.status(500).json({ error: err });
				}
				else {
					if (userPage >=numPages) {
						rsp.sendStatus(403);}	// não exitem tantas paginas quanto o solicitado!
					else{
						model.getPagedDiscontinuitiesOfOneUser(null, req.params.idUser, userPage, numPerPage,function (err,res){
							if (!err){
								console.log ("o resultado é: "+res);
								rsp.json(res);
							}
							else{
								rsp.sendStatus(500);
							}
						});
					}
				}
		 } );
	});

	/**
	 * Rota para inserir na base de dados um conjunto de descontinuidades
	 * Espera-se um pedido com dados Json com estrutura semelhante à seguinte:
     * { "discontinuities": [{"aperture":5,"dip":66,"direction":50,"id":100,"infilling":5,"latitude":38,"longitude":9,"persistence":5,"roughness":5,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5,"note":"nota 1","datetime":"2010-10-10 10:10:10" }, 
	 * 						 {"aperture":2,"dip":66,"direction":50,"id":101,"infilling":4,"latitude":38,"longitude":9,"persistence":1,"roughness":3,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5,"note":"nota 2","datetime":"2011-11-11 11:11:11" } ]}
	 * 
	 */
	router.post('/api/discontinuities', VerifyToken, function (req,rsp,next){ 
		model.postDiscontinuities(req.body,function (err,result){
			if (err) {
				console.log(err);
				rsp.status(500).json({ error: err });
			}
			else{
				rsp.sendStatus(200);
			}	
		});
    });

    return router

		
	/**
	 * Função auxiliar tipo middleware para verificar o Token
	 */
	function VerifyToken(req, res, next) {

		// check header or url parameters or post parameters for token
		var token = req.body.token || req.query.token || req.headers['x-access-token'];

		// decode token
		if (token) {

			// verifies secret and checks exp
			jwt.verify(token, app.get('superSecret'), function(err, decoded) {      
					if (err) {
						return res.status(403).json({ success: false, message: 'Failed to authenticate token.' });    
					} else {
						// if everything is good, save to request for use later
						req.decoded = decoded;    
						next();
					}
				});
		} else {
			// if there is no token return an error
			return res.status(403).send({ 
				success: false, 
				message: 'No token provided.' 
			});
		}
	}

	/**
	 * Função auxiliar para verificar se o Token é do administrador.
	 * Deve ser usada depois da função VerifyToken
	 */
	function IsAdmin(req, res, next){
		console.log (req.decoded);
		if (req.decoded.usermail ==="admin") next();
		else return res.json({ success: false, message: 'Function available only for admin' });    
	}

}
