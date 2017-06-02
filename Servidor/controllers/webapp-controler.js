'use strict'

module.exports = function(app,passport,model) {          
    const router = require('express').Router()
  
	/**
	 * Homepage
	 */
    router.get('/', function (req,rsp,next){
    	rsp.status(200);
		rsp.render('home'); 
    });

	/**
	 * Administration area
	 */
	router.get('/admin',  isAdminLoggedIn, function (req,rsp,next){
    		model.getSummary(null,function (err,res){
			if(!err){							
				rsp.status(200);
				rsp.render ('admin',res);
			} else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			} 
		})
    });
	
	/**
	 * Administration area / Manage users - add a new user or delete an existing user
	 */
    router.get('/admin/user',  isAdminLoggedIn, function (req,rsp,next){
    		rsp.status(200);
			rsp.render ('adminuser'); 
    });

	/**
	 * Administration area / Insert's a new user in database
	 */
   router.post('/admin/adduser',  isAdminLoggedIn, function (req,rsp,next){   
	   model.createUser(req.body.username, req.body.password, function (err, message){
		   if (!err){
				console.log(message+"created!");
			   	rsp.sendStatus(201);
		   }
		   else{
			   rsp.sendStatus(500);
		   }
		});
   });

    /**
	 * Administration area / show's information from all existing USERS in database (to delete one)
	 */
	router.get('/admin/deleteuser',  isAdminLoggedIn, function (req,rsp,next){
		model.getAllUsersResumedInformation(null,function(err,res){
			if(!err){
				rsp.status(200);
				rsp.render ('admindeleteuser',res);
			}else{
				rsp.status(404);
				rsp.render ('error',{message:err})
			}		
		}, false); 
	});
	
	 /**
	  * Administration area / route to delete a user
	  */
	 router.delete('/admin/deleteuser/:idUser', isAdminLoggedIn, function (req,rsp,next){
	 //router.post('/admin/delete/user/:idUser', isAdminLoggedIn, function (req,rsp,next){
		model.deleteUser(req.params.idUser, function (err, message){
			if (err){
				rsp.status(404);
				rsp.render('error',{message:err})
			}
			else {
				rsp.sendStatus(200);
			}	
		});
	 });
	 
	/**
	 * Administration area / show's information from all sessions in database (to delete one)
	 */
	router.get('/admin/deletesession',  isAdminLoggedIn, function (req,rsp,next){
		model.getAllSessionsResumedInformation(null, function (err,res){
			if (!err){
				rsp.status(200);
				rsp.render ('admindeletesession',res);
			}
			else{
				rsp.status(404);
				rsp.render ('error', {message:err})
			}
		});
	});


	 /**
	  * Administration area / route to delete a session
	  */
	 router.delete('/admin/deletesession/:idSession', isAdminLoggedIn, function (req,rsp,next){
		model.deleteSession(req.params.idSession, function (err, message){
			if (err){
				rsp.sendStatus(404);	// not found
			}
			else {
				rsp.sendStatus(200);
				};		
		});
	 });

	/**
	 * Information for Api use
	 */
	router.get('/api', function (req,rsp,next){	
		rsp.status(200);
    	rsp.render ('api');
	});

	/**
	 * Process the SIGNUP form (é usado por XtmlRequest)
	 */
    router.post('/login', passport.authenticate('local'), function (req, res, next){		
								res.sendStatus(200);
		}

		/* Para usar o login sem XtmlRequest substituir pelo código:
		passport.authenticate('local', { 			
        			successRedirect : '/dashboard', 
        			failureRedirect : '/',
				failureFlash: true }			
    	)
		*/
	);

	/**
	 * Lança no google maps as descontinuidades
	 */
	router.get('/gmaps', isLoggedIn, function (req,rsp,next){
		model.getAllDiscontinuities(null,function (err,res){
			if (!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;};
				res.numDiscont=res.discontinuities.length;
				rsp.status(200);
				rsp.render ('maps', res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			}
		});
	});

	/**
	 * Dashboard
	 */
	router.get('/dashboard', isLoggedIn, function (req,rsp,next){
		model.getSummary(null,function (err,res){
			if(!err){
				// console.log(res)								
				// { summary: [ RowDataPacket { NumUsers: 1, NumSessions: 3, NumDiscontinuities: 23 } ] }
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true; return false;}
				rsp.status(200);
				rsp.render ('dashboard',res);
			} else{
				rsp.status(500);
				rsp.render ('users',{message:err})
			} 
		});
	 });

	 /**
	  * Users information
	  */
	 router.get('/users', isLoggedIn, function (req,rsp,next){	
		model.getAllUsersResumedInformation(null,function(err,res){
			if(!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true; return false;}
				rsp.status(200);
				rsp.render ('users',res);
			}else{
				rsp.status(500);
				rsp.render ('error',{message,err})
			}
		},false);
	 });

	 /**
	  * One user information
	  */
	 router.get('/users/:idUser', isLoggedIn, function (req,rsp,next){
		 model.getDiscontinuitiesOfOneUser (req.params.idUser, function (err,res){	// TODO MODEL
			if (!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;}
				rsp.status(200);
				rsp.render ('userdiscontinuity',res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			}
		 })
	 });

	 /**
	  * Contributors information
	  */
	 router.get('/contributors', isLoggedIn, function (req,rsp,next){
		model.getAllUsersResumedInformation(null,function(err,res){
			if(!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;}
				rsp.status(200);
				rsp.render ('contributors',res);
			}else{
				rsp.status(500);
				rsp.render ('error',{message:err})
			}
		},true);
	 });

	 /**
	  * Sessions information
	  */
	 router.get('/sessions', isLoggedIn, function (req,rsp,next){	
    	model.getAllSessionsResumedInformation(null, function (err,res){
			if (!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;}
				rsp.status(200);
				rsp.render ('sessions',res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			}
		});
	  });

	 /**
	  * Discontinuities of one session
	  */
	 router.get('/sessions/:idSession', isLoggedIn, function (req,rsp,next){
		model.getDiscontinuitiesFromOneSession(req.params.idSession, function (err,res){
			if (!err){
				res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;}
				rsp.status(200);
				rsp.render ('sessiondiscontinuity',res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			}
		});
	 });

	 /**
	  * All discontinuities of the system
	  */
	router.get('/discontinuities', isLoggedIn, function (req,rsp,next){
		var userPage= req.query.page===undefined? 0: req.query.page;
		var numPerPage=5;
		model.getNumberOfApiPages (null, function (err,numPages){
				if (err){rsp.status(500).render(error, {message:"Server error..."});}
				else {
					if (userPage >=numPages) {
						console.log ("userPageGreather than available pages!");
						rsp.status(403).render('error', {message:"Page not available!"});
					}
					else{
						model.getPagedDiscontinuities(null,function (err,res){
							if (!err){
								res.userIsAdmin = function (){ if (req.user.username =="admin") return true;return false;}
								res.page=userPage;
								res.maxPages=numPages-1;
								rsp.status(200);
								rsp.render ('discontinuities', res);
							}
							else{
								rsp.status(500);
								rsp.render ('error',{message:err});
							}
						},userPage,numPerPage);
					}
				}
	 	},numPerPage);
	});
	

	/**
	 * Logout route
	 */
	router.get('/logout', function(req, res) {				
		// in http://stackoverflow.com/questions/13758207/why-is-passportjs-in-node-not-removing-session-on-logout
        req.session.destroy(function() {
   			 res.clearCookie('connect.sid');
   		 	res.redirect('/');
		});	
    });

    return router
}
        
/**
 * Middleware function to make sure a user is logged in
 */
function isLoggedIn(req, res, next) {
    // if user is authenticated in the session, carry on 
    if (req.isAuthenticated())
        return next();
    // if he are not redirect him to the home page
    res.status(401);
	res.redirect('/');
}

/**
 * Middleware function to make sure the administrator is logged in
 */
function isAdminLoggedIn(req, res, next) {
    // if user is authenticated in the session, carry on 
    if (req.isAuthenticated()&& req.user.username === 'admin')
        return next();
    // if he are not redirect him to the home page
    res.status(401);
	res.redirect('/');
}
