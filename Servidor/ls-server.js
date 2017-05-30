'use strict'
var express 		= require('express');
var app 			= express();
var passport 		= require('passport');
var expressHbs 		= require('express-handlebars');
var bodyParser 		= require('body-parser');
var path			= require('path');
var session      	= require('express-session');
var config          = require('./setup');           // get our config file

const usersDal      = require('./data-access/dal')(config.host, config.user, config.password, config.database);
const model			= require ('./model/model')(usersDal);

require('./config/passport')(passport, model);      // pass passport and model for configuration

app.use(bodyParser.text());
app.use(bodyParser.json());

app.use(bodyParser.urlencoded({
  extended: true
}));

app.set('superSecret', config.appsecret);           // secret variable

// required for passport
app.use(session({   secret: config.appsecret,       // session secret
                    resave:true,
                    saveUninitialized:true })); 
app.use(passport.initialize());
app.use(passport.session());                        // persistent login sessions

app.use(express.static(path.join(__dirname, 'public')));

app.engine('hbs', expressHbs({extname:'hbs', defaultLayout:'base', helpers: {
        foo: function (xpto) { return JSON.stringify(xpto); },
        bar: function () { return 'BAR'; }
    }}));
app.set('view engine', 'hbs');

const apiRouter 	= require ('./controllers/api-controler')(model,app);
const webAppRouter 	= require ('./controllers/webapp-controler')(app, passport,model);

app.use ('/', webAppRouter);
app.use ('/', apiRouter);

const PORT = process.env.PORT || 3010;

app.listen(PORT, function (err) {
    console.log('Application listening on port ' + PORT);
})
