delimiter $$

CREATE TABLE `userpositions` (
  `userid` int(11) NOT NULL,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$

CREATE TABLE `usersonroutes` (
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  PRIMARY KEY (`userid`,`routeid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mail` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `password` char(32) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `gender` tinyint(4) DEFAULT '0',
  `activationdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mail_UNIQUE` (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `validationkeys` (
  `userid` int(11) NOT NULL,
  `validationkey` char(36) NOT NULL,
  `datecreated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `reason` smallint(6) DEFAULT '0',
  PRIMARY KEY (`userid`,`validationkey`),
  CONSTRAINT `FK_USER` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `latestupdate` datetime DEFAULT '0000-00-00 00:00:00',
  `before` tinyint(4) DEFAULT NULL,
  `after` tinyint(4) DEFAULT NULL,
  `days` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`,`userid`),
  KEY `user` (`userid`),
  CONSTRAINT `user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `routepoints` (
  `routeid` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`routeid`,`time`),
  KEY `route` (`routeid`),
  CONSTRAINT `route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `trackings` (
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  `start` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `distance` int(11) DEFAULT NULL,
  `points` int(11) DEFAULT NULL,
  `speedmax` float DEFAULT NULL,
  PRIMARY KEY (`userid`,`routeid`,`start`,`end`),
  KEY `tracking_route` (`routeid`),
  CONSTRAINT `tracking_route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


CREATE TABLE `linkedusers` (
  `userid1` int(11) NOT NULL,
  `userid2` int(11) NOT NULL,
  PRIMARY KEY (`userid1`,`userid2`),
  KEY `userlinks_user_1` (`userid1`),
  KEY `userlinks_user_2` (`userid2`),
  CONSTRAINT `userlinks_user_1` FOREIGN KEY (`userid1`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userlinks_user_2` FOREIGN KEY (`userid2`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `posts` (
  `userid` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `content` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`,`time`),
  KEY `post_user` (`userid`),
  CONSTRAINT `post_user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `mitu_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(20) NOT NULL DEFAULT '',
  `mail` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `password` char(60) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mail_UNIQUE` (`mail`),
  UNIQUE KEY `userid_UNIQUE` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `mitu_regids` (
  `userid` int(11) NOT NULL,
  `regid` varchar(250) NOT NULL,
  PRIMARY KEY (`regid`,`userid`),
  CONSTRAINT `regid_user` FOREIGN KEY (`userid`) REFERENCES `mitu_users` (`id`) ON DELETE NO CASCADE ON UPDATE NO CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `mitu_connections` (
  `idfrom` int(11) NOT NULL,
  `idto` int(11) NOT NULL,
  PRIMARY KEY (`idfrom`,`idto`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$;

CREATE TABLE `mitu_userpositions` (
  `userid` int(11) NOT NULL,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `gps` bit(1) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8$$;
