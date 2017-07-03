/* Este ficheiro destina-se a inserir o administrador da base de dados 
e deverá ser apenas para efeito de instalação da aplicação. Depois de 
inserido o administrador do sistema este ficheiro deverá ser eliminado
do sistema  */

'use strict'
var config          = require('./setup');
const usersDal      = require('./data-access/dal')(config.host, config.user, config.password, config.database);

usersDal.postUser({name:'José Augusto',email:'admin',password:'123'}, function (err){
    if (!err) {
        console.log ("ok!")
    }
    else {
        console.log ("Something wrong! Verify if the admin is allready created!")
    }
});

usersDal.postUser({name:'Pedro X',email:'x@mail.com',password:'123'}, function (err){
    if (!err) {
        console.log ("ok!")
    }
    else {
        console.log ("Something wrong! Verify if the x@mail.com is allready created!")
    }
});

usersDal.postUser({name:'Joana Y',email:'y@mail.com',password:'124'}, function (err){
    if (!err) {
        console.log ("ok!")
    }
    else {
        console.log ("Something wrong! Verify if the y@mail.com is allready created!")
    }
});

usersDal.postUser({name:'Afonso Z',email:'z@mail.com',password:'125'}, function (err){
    if (!err) {
        console.log ("ok!")
    }
    else {
        console.log ("Something wrong! Verify if the z@mail.com is allready created!")
    }
});

usersDal.postUser({name:'António W',email:'w@mail.com',password:'126'}, function (err){
    if (!err) {
        console.log ("ok!")
    }
    else {
        console.log ("Something wrong! Verify if the w@mail.com is allready created!")
    }
});
