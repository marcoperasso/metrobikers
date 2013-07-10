delimiter $$

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mail` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `password` char(32) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `activationdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mail_UNIQUE` (`mail`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `validationkeys` (
  `userid` int(11) NOT NULL,
  `validationkey` char(36) NOT NULL,
  `datecreated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_USER` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`,`userid`),
  KEY `user` (`userid`),
  CONSTRAINT `user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `routepoints` (
  `routeid` int(11) NOT NULL,
  `lat` mediumint(9) DEFAULT NULL,
  `lon` mediumint(9) DEFAULT NULL,
  `ele` float DEFAULT NULL,
  `time` timestamp NULL DEFAULT NULL,
  KEY `route` (`routeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

