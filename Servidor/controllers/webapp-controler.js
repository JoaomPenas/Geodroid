'use strict'
const fs 		= require('fs')
const config  	= require('./../setup');

const numPerPage=config.webAppNumPerPage;
function showObject(obj) {
    for (var k in obj) {
        console.log("o[\'" + k          // k       -> contem a string da chave
                + "\']= " + obj[k])     // obj[k]   -> retorna o valor da propriedade
    }
}
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
	 * Route to return  user discontinuities in csv formart
	 */
    router.get('/csvuserdiscontinuities/:idUser', isLoggedIn,function (req,rsp,next){
		model.getDiscontinuitiesFromOneSessionOrUserCsv ("user",req.params.idUser, (err, res)=>{
			if (!err){
				var filename=req.params.idUser+'_discontinuities.csv'
				rsp
				 .set('Content-Disposition','attachment;filename='+filename) 
				 .status(200)
				 .end (res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			} 
			
		});
    });

	/**
	 * Route to return  user session in csv formart
	 */
    router.get('/csvsessiondiscontinuities/:idSession', isLoggedIn, function (req,rsp,next){
		
		model.getDiscontinuitiesFromOneSessionOrUserCsv ("session",req.params.idSession, (err, res)=>{
			if (!err){
				var filename=req.params.idSession+'_discontinuities.csv'
				rsp
				.set('Content-Disposition','attachment;filename='+filename)
				 .status(200)
				 .end (res);
			}
			else{
				rsp.status(500);
				rsp.render ('error',{message:err});
			} 
			
		});
    	
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
	   model.createUserGeneratingPassword(req.body.username, req.body.usermail,  function (err, message){
		   if (!err){
			   	rsp.sendStatus(201);
		   }
		   else{
			   rsp.status(500);
			   rsp.send(err);
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
				res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;};
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
				res.userIsAdmin = function (){ if (req.user.email =="admin") return true; return false;}
				rsp.status(200);
				rsp.render ('dashboard',res);
			} else{
				rsp.status(500);
				rsp.render ('error',{message:err})
			} 
		});
	 });

	 /**
	  * Users information (for each user number of sessions and discontinuities)
	  */
	 router.get('/users', isLoggedIn, function (req,rsp,next){	
		model.getAllUsersResumedInformation(null,function(err,res){
			if(!err){
				res.userIsAdmin = function (){ if (req.user.email =="admin") return true; return false;}
				rsp.status(200);
				rsp.render ('users',res);
			}else{
				rsp.status(500);
				rsp.render ('error',{message,err})
			}
		},false);
	 });

	 /**
	  * Discontinuities of one user (PAGINATED)
	  */
	 router.get('/users/:idUser', isLoggedIn, function (req,rsp,next){
		 var userPage= req.query.page===undefined? 0: req.query.page;
		 model.getNumOfPages (null, "user", req.params.idUser, numPerPage, (err,numPages)=>{
			 	if (err){rsp.status(500).render(error, {message:"Server error..."});}
			 	if (userPage>=numPages){rsp.status(403).render('error', {message:"Page not available!"});}
				else{
					model.getPagedDiscontinuitiesOfOneUser(null, req.params.idUser, userPage, numPerPage, function (err,res){
						if (!err){
							res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;}
							res.page=userPage;
							res.maxPages=numPages-1;
							showObject(res);
							rsp.status(200);
							rsp.render ('userdiscontinuity',res);
						}
						else{
							rsp.status(500);
							rsp.render ('error',{message:err});
						}
					})
				}			
		 } );
	 });
	 
	 /**
	  * Discontinuities of one session (PAGINATED)
	  */
	 router.get('/sessions/:idSession', isLoggedIn, function (req,rsp,next){
		 var userPage= req.query.page===undefined? 0: req.query.page;
		 model.getNumOfPages (null,"session", req.params.idSession,numPerPage, (err,numPages)=>{
			 	if (err){rsp.status(500).render(error, {message:"Server error..."});}
			 	if (userPage>=numPages){rsp.status(403).render('error', {message:"Page not available!"});}
				else {
					model.getPagedDiscontinuitiesOfOneSession(null, req.params.idSession, userPage, numPerPage, function (err,res){
						if (!err){
							res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;}
							res.page=userPage;
							res.maxPages=numPages-1;
							showObject(res);
							rsp.status(200);
							rsp.render ('sessiondiscontinuity',res);
						}
						else{
							rsp.status(500);
							rsp.render ('error',{message:err});
						}
					})
				}
		 } );
			
	 });
	
	 /**
	  * Contributors information (DEPRECATED...)
	  */
	  /*
	 router.get('/contributors', isLoggedIn, function (req,rsp,next){
		model.getAllUsersResumedInformation(null,function(err,res){
			if(!err){
				res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;}
				rsp.status(200);
				rsp.render ('contributors',res);
			}else{
				rsp.status(500);
				rsp.render ('error',{message:err})
			}
		},true);
	 });
*/


	 /**
	  * Sessions information
	  */
	 router.get('/sessions', isLoggedIn, function (req,rsp,next){	
    	model.getAllSessionsResumedInformation(null, function (err,res){
			if (!err){
				res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;}
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
	  * All discontinuities of the system (PAGINATED)
	  */
	 router.get('/discontinuities', isLoggedIn, function (req,rsp,next){
		var userPage= req.query.page===undefined? 0: req.query.page;
		
		model.getNumberOfDiscontinuitiesPages (null, function (err,numPages){
				if (err){rsp.status(500).render(error, {message:"Server error..."});}
				else {
					if (userPage >=numPages) {
						rsp.status(403).render('error', {message:"Page not available!"});
					}
					else{
						model.getPagedDiscontinuities(null,userPage,numPerPage, function (err,res){
							if (!err){
								res.userIsAdmin = function (){ if (req.user.email =="admin") return true;return false;}
								res.page=userPage;
								res.maxPages=numPages-1;
								rsp.status(200);
								rsp.render ('discontinuities', res);
							}
							else{
								rsp.status(500);
								rsp.render ('error',{message:err});
							}
						});
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
    if (req.isAuthenticated()&& req.user.email === 'admin')
        return next();
    // if he are not redirect him to the home page
    res.status(401);
	res.redirect('/');
}
