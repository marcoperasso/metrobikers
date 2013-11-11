delimiter $$

CREATE TABLE `routepoints` (
  `routeid` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`routeid`,`time`),
  KEY `route` (`routeid`),
  CONSTRAINT `route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `latestupdate` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`,`userid`),
  KEY `user` (`userid`),
  CONSTRAINT `user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `userpositions` (
  `userid` int(11) NOT NULL,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `usersonroutes` (
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  PRIMARY KEY (`userid`,`routeid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `validationkeys` (
  `userid` int(11) NOT NULL,
  `validationkey` char(36) NOT NULL,
  `datecreated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_USER` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `trackings` (
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  `start` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`userid`,`routeid`,`start`,`end`),
  KEY `tracking_route` (`routeid`),
  CONSTRAINT `tracking_route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


