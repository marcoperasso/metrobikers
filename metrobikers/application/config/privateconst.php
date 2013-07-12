<?php

/* Questo file contiene informazioni riservate, da non rilasciare su GIT se contiene dati di produzione
 * Qui dovrebbero esserci tutit i delta di configurazione fra ambiente di sviluppo e di produzione
*/

define('BASE_URL',                      'http://localhost:8888/ecommuters/');
define('BASE_URL_CONSIDERING_MOBILE',   'http://localhost:8888/ecommuters/');

//define('BASE_URL_CONSIDERING_MOBILE',   'http://10.0.2.2:8888/'); // l'emulatore android usa questo indirizzo per connettersi a localhost 
define('DATABASE_USER',         'webuser');
define('DATABASE_PASSWORD',     'webuser');
define('DATABASE_HOST',         'localhost');
define('DATABASE_NAME',         'metrobikers');


define('MAIL_HOST',             'ssl://smtp.gmail.com');
define('MAIL_USER',             'mtbgroupscout@gmail.com');
define('MAIL_PASSWORD',         'montoggio');
define('MAIL_PORT',             465);
?>
