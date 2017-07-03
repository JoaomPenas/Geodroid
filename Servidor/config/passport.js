
const passportStrategy = require('passport-local').Strategy

// expose this function to our app using module.exports
module.exports = function(passport, model) {

    // =========================================================================
    // passport session setup ==================================================
    // =========================================================================
    // required for persistent login sessions
    // passport needs ability to serialize and unserialize users out of session

    // used to serialize the user for the session
    passport.serializeUser(function(user, done) {
        done(null, user.email);
    });

    // used to deserialize the user
    passport.deserializeUser(function(id, done) {
        model.getUser(id, function(err, user){done(err, user);}	);
    });

    // =========================================================================
    // LOCAL SIGNUP ============================================================
    // =========================================================================
    // we are using named strategies since we have one for login and one for signup
    // by default, if there was no name, it would just be called 'local'
	// By default, LocalStrategy expects to find credentials in parameters named username and password. 
	// If your site prefers to name these fields differently, options are available to change the defaults.

    passport.use(new passportStrategy((usermail, password, cb) => {
        model.authenticate(
            usermail, 
            password,
            cb
        )
		}))
}
		