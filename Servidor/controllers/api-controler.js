'use strict'

module.exports = function(model, app) {          
    const router 		= require('express').Router();
	var jwt             = require('jsonwebtoken');      // used to create, sign, and verify tokens

	/**
	 * Rota para proceder à autenticação para uso da Api. 
	 * Responde um JsonWebToken em caso de sucesso.
	 */
	router.post('/api/authenticate', function (req,rsp,next){ 
		
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
				var token = jwt.sign({username:user.username}, app.get('superSecret'),{expiresIn:'24h'});	// DUVIDA
				
				// return the information including token as JSON
				rsp.json({
					success: true,
					message: 'Enjoy your token!',
					token: token
				});
			}
		});

	});

	/**
	 * Devolve a informação dos utilizadores
	 */
	router.get('/api/users', VerifyToken, function (req,rsp,next){
	//router.get('/api/users', function (req,rsp,next){
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
	 * exemplo: {"username":"wer4", "password":"123"}). 
	 * Função apenas disponível para o administrador (admin)
	 */
	router.post('/api/users', VerifyToken, IsAdmin, function (req,rsp,next){   
	//router.post('/api/users', function (req,rsp,next){  
		console.log(req.body);
	   model.createUser(req.body.username, req.body.password, function (err, message){
		   if (!err){
				//console.log(message+"created! :)");
			   	rsp.status(201);
				rsp.json({message:message})
		   }
		   else{
			   rsp.status(500);
			   rsp.json({message:'erro'})
			   //rsp.render('error',{message:err})
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
	//router.get('/api/sessions',  function (req,rsp,next){
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
	 * Devolve informação de todas as descontinuidades
	 */
	router.get('/api/discontinuities', VerifyToken, function (req,rsp,next){
	//router.get('/api/discontinuities', function (req,rsp,next){
		model.getAllDiscontinuities (null, function (err,res){
			if (!err){
				rsp.json(res);
			}
			else{
				rsp.status(500).json({ error: err });
			}
		});
	});

	/**
	 * Devolve informação das descontinuidades de uma sessão
	 */
 	router.get('/api/discontinuities/:idSession', VerifyToken, function (req,rsp,next){
	//router.get('/api/discontinuities/:idSession', function (req,rsp,next){
		model.getDiscontinuitiesFromOneSession(req.params.idSession, function (err,res){
			if (!err){
				rsp.json(res);
			}
			else{
				rsp.status(500).json({ error: err });
			}
		});
	 });

	/**
	 * Rota para inserir na base de dados um conjunto de descontinuidades
	 * Espera-se um pedido com dados Json com estrutura semelhante à seguinte:
     * { "discontinuities": [{"aperture":5,"dip":66,"direction":50,"id":100,"infilling":5,"latitude":38,"longitude":9,"persistence":5,"roughness":5,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 },
     *                       {"aperture":2,"dip":66,"direction":50,"id":101,"2nfilling":4,"latitude":38,"longitude":9,"persistence":1,"roughness":3,"idSession":"Oeiras","idUser": "w@mail.com","weathering": 5 } ]}
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
						return res.json({ success: false, message: 'Failed to authenticate token.' });    
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
		//console.log (req.decoded);
		if (req.decoded.username ==="admin") next();
		else return res.json({ success: false, message: 'Function available only for admin' });    
	}

}
